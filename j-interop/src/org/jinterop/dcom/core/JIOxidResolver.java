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

import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIComVersion;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;

/** Partially implements IOxidResolver interface, used only for ResolveOxid calls.
 *
 *
 * @since 1.23
 *
 */
final class JIOxidResolver extends NdrObject
{
	private final byte[] oxid;

	private JIDualStringArray oxidBindings = null;
	private String ipid = null;

	JIOxidResolver(final byte[] oxid)
	{
		this.oxid = oxid;
	}

	public int getOpnum() {
		return 4;
	}

	public void write(NetworkDataRepresentation ndr)
	{
		JIMarshalUnMarshalHelper.writeOctetArrayLE(ndr,oxid);
		JIMarshalUnMarshalHelper.serialize(ndr, Short.class, new Short((short)1), new ArrayList(), JIFlags.FLAG_NULL);
		JIMarshalUnMarshalHelper.serialize(ndr, JIArray.class, new JIArray(new Short[]{new Short((short)7)},true), new ArrayList(), JIFlags.FLAG_REPRESENTATION_ARRAY);
	}

	public void read(NetworkDataRepresentation ndr)
	{
		ndr.readUnsignedLong(); //pointer
		ndr.readUnsignedLong(); //some length component, irrelevant for us right now
		oxidBindings = JIDualStringArray.decode(ndr);
		try {
			UUID ipid2 = new UUID();
			ipid2.decode(ndr,ndr.getBuffer());
			ipid = (ipid2.toString());
		} catch (NdrException e) {

			JISystem.getLogger().throwing("JIRemActivation","read",e);
		}

		//read the auth hint
		int authenticationHint = ndr.readUnsignedLong();

		JIComVersion comVersion = new JIComVersion();
		comVersion.setMajorVersion(ndr.readUnsignedShort());
		comVersion.setMinorVersion(ndr.readUnsignedShort());

		int hresult = ndr.readUnsignedLong();

		if (hresult != 0)
		{
			//System.out.println("EXCEPTION FROM SERVER ! --> " + "0x" + Long.toHexString(hresult).substring(8));
			throw new JIRuntimeException(hresult);
		}

	}

	JIDualStringArray getOxidBindings()
	{
		return oxidBindings;
	}

	String getIPID()
	{
		return ipid;
	}

}
