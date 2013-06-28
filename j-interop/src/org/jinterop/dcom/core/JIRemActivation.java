/**
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
*/

package org.jinterop.dcom.core;



import java.util.ArrayList;
import java.util.HashMap;

import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;

final class JIRemActivation extends NdrObject implements JIIServerActivation {

	private int impersonationLevel = RPC_C_IMP_LEVEL_IMPERSONATE;
	private int mode = 0;
	private String monikerName = null;
	private UUID clsid = null;
	private boolean activationSuccessful = false;
	private JIOrpcThat orpcthat = null;
	private byte[] oxid = null;
	private JIDualStringArray dualStringArrayForOxid = null;
	private String ipid = null;
	private int authenticationHint = -1;
	private JIComVersion comVersion = null;
	private int hresult = -1;
	private JIInterfacePointer mInterfacePointer = null;
	boolean isDual = false;
	String dispIpid = null;
	int dispRefs = 5;
	byte[] dispOid = null;

	public JIRemActivation(String clsid)
	{
		//10000002-0000-0000-0000-000000000001 Inside DCOM
		this.clsid = new UUID(clsid);
	}

	public void setMode (int mode)
	{
		this.mode = mode;
	}
	public void setClientImpersonationLevel(int implLevel)
	{
		impersonationLevel = implLevel;
	}

	public void setfileMonikerAtServer(String name)
	{
		if (name != null  && !name.equalsIgnoreCase(""))
		{
			monikerName = name;
		}
	}

	public int getOpnum() {
		return 0;
	}
	public void write(NetworkDataRepresentation ndr) {


		JIOrpcThis orpcThis = new JIOrpcThis();
		orpcThis.encode(ndr);

		//JIClsid of the component being activated.
		UUID uuid = new UUID();
		uuid.parse(clsid.toString());
		try {
			uuid.encode(ndr,ndr.buf);
		} catch (NdrException e) {

			JISystem.getLogger().throwing("JIRemActivation","write",e);
		}
		if (monikerName == null)
		{
			ndr.writeUnsignedLong(0);
		}
		else
		{
			ndr.writeCharacterArray(monikerName.toCharArray(),0,monikerName.length()); // Object Name
		}


		ndr.writeUnsignedLong(0); // Minterface pointer
		ndr.writeUnsignedLong(impersonationLevel); // impersonation level
		ndr.writeUnsignedLong(mode); //mode, when object name , interface pointer are not null , this is passed directly to IPersistFile:Load

		ndr.writeUnsignedLong(2); //No. of IIDs requested.

		ndr.writeUnsignedLong(new Object().hashCode());

		ndr.writeUnsignedLong(2); //Array length

		//IID of IUnknown , this is hard coded here, standard way of COM is to first get a handle to the IUnknown
		uuid.parse("00000000-0000-0000-c000-000000000046");
		try {
			uuid.encode(ndr,ndr.buf);
		} catch (NdrException e) {

			JISystem.getLogger().throwing("JIRemActivation","write",e);
		}

		//checking for IDispatch support
		uuid.parse("00020400-0000-0000-c000-000000000046");
		try {
			uuid.encode(ndr,ndr.buf);
		} catch (NdrException e) {

			JISystem.getLogger().throwing("JIRemActivation","write",e);
		}

		ndr.writeUnsignedLong(1); //Protocol Sequences available
		ndr.writeUnsignedLong(1); //Array length
		ndr.writeUnsignedShort(7); //TCP

		byte[] address = JISession.getLocalhostAddressAsIPbytes();

		ndr.writeUnsignedShort(address[0]);
		ndr.writeUnsignedShort(address[1]);
		ndr.writeUnsignedShort(address[2]);
		ndr.writeUnsignedShort(address[3]);
		ndr.writeUnsignedShort(0);
	}


	public void read(NetworkDataRepresentation ndr) {

		//first take out JIOrpcThat
		orpcthat = JIOrpcThat.decode(ndr);

		//now fill the oxid
		oxid = JIMarshalUnMarshalHelper.readOctetArrayLE(ndr,8);

		int skipdual = ndr.readUnsignedLong();

		if (skipdual != 0)
		{
			ndr.readUnsignedLong();
			//now fill the dual string array for oxid bindings, the call to IRemUnknown will be
			//directed to this address and the port in that address.
			dualStringArrayForOxid = JIDualStringArray.decode(ndr);
		}
		//get the IPID which will be the "Object" in the call to IRemUknown. This is the IPID of the
		//component which has been specified as the JIClsid. This may differ in multiple invokations of
		//of remote activation as everytime a new object may be created at the server per call. This is all
		//server implementation dependent.
		try {
			UUID ipid2 = new UUID();
			ipid2.decode(ndr,ndr.getBuffer());
			ipid = (ipid2.toString());
		} catch (NdrException e) {

			JISystem.getLogger().throwing("JIRemActivation","read",e);
		}

		//read the auth hint
		authenticationHint = ndr.readUnsignedLong();

		comVersion = new JIComVersion();
		comVersion.setMajorVersion(ndr.readUnsignedShort());
		comVersion.setMinorVersion(ndr.readUnsignedShort());

		hresult = ndr.readUnsignedLong();

		if (hresult != 0)
		{
			//System.out.println("EXCEPTION FROM SERVER ! --> " + "0x" + Long.toHexString(hresult).substring(8));
			throw new JIRuntimeException(hresult);
		}


		//int numRet = ndr.readUnsignedLong();//Number of interface pointers returned. Currently only 2.

		JIArray array = new JIArray(JIInterfacePointer.class,null,1,true);
		ArrayList listOfDefferedPointers = new ArrayList();
		array = (JIArray)JIMarshalUnMarshalHelper.deSerialize(ndr,array,listOfDefferedPointers ,JIFlags.FLAG_NULL,new HashMap());
		int x = 0;

		while (x < listOfDefferedPointers.size())
		{

			ArrayList newList = new ArrayList();
			JIPointer replacement = (JIPointer)JIMarshalUnMarshalHelper.deSerialize(ndr,(JIPointer)listOfDefferedPointers.get(x),newList,JIFlags.FLAG_NULL,null);
			((JIPointer)listOfDefferedPointers.get(x)).replaceSelfWithNewPointer(replacement); //this should replace the value in the original place.
			x++;
			listOfDefferedPointers.addAll(x,newList);
		}
		JIInterfacePointer[] arrayObjs = (JIInterfacePointer[])array.getArrayInstance();
		mInterfacePointer = arrayObjs[0];

		if (arrayObjs[1] != null)
		{
			//dual is supported since the IDispatch was obtained
			isDual = true;
			//eat this keeping only the IPID for cleanup , let the user perform another queryInterface for this.
			JIInterfacePointer ptr = arrayObjs[1];
			dispIpid = ptr.getIPID();
			dispOid = ptr.getOID();
			dispRefs = ((JIStdObjRef)ptr.getObjectReference(JIInterfacePointer.OBJREF_STANDARD)).getPublicRefs();
		}

		array = new JIArray(Integer.class,null,1,true);
		//ignore the retvals
		JIMarshalUnMarshalHelper.deSerialize(ndr,array,null,JIFlags.FLAG_NULL,null);

		activationSuccessful = true;

	}

	/* (non-Javadoc)
	 * @see org.jinterop.dcom.core.JIIServerActivation#isActivationSuccessful()
	 */
	public boolean isActivationSuccessful()
	{
		return activationSuccessful;
	}

	public JIOrpcThat getORPCThat()
	{
		return orpcthat;
	}

	public byte[] getOxid()
	{
		return oxid;
	}

	/* (non-Javadoc)
	 * @see org.jinterop.dcom.core.JIIServerActivation#getDualStringArrayForOxid()
	 */
	public JIDualStringArray getDualStringArrayForOxid()
	{
		return dualStringArrayForOxid;
	}

	public int getAuthenticationHint()
	{
		return authenticationHint;
	}

	public JIComVersion getComVersion()
	{
		return comVersion;
	}

	public int getHresult()
	{
		return hresult;
	}

	/* (non-Javadoc)
	 * @see org.jinterop.dcom.core.JIIServerActivation#getMInterfacePointer()
	 */
	public JIInterfacePointer getMInterfacePointer()
	{
		return mInterfacePointer;
	}

	/* (non-Javadoc)
	 * @see org.jinterop.dcom.core.JIIServerActivation#getIPID()
	 */
	public String getIPID()
	{
		return ipid;
	}

	public boolean isDual() {
		return isDual;
	}

	public String getDispIpid() {
		return dispIpid;
	}

	public int getDispRefs() {
		return dispRefs;
	}

	public void setDispIpid(String dispIpid) {
		this.dispIpid = dispIpid;
	}
}
