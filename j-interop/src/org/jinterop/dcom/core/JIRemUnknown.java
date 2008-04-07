/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jinterop.dcom.core;

import ndr.NdrException;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;

import rpc.core.UUID;


final class JIRemUnknown extends NdrObject {

	public static final String IID = "00000143-0000-0000-c000-000000000046"; 
	private String ipidOfIUnknown = null;
	private String requestedIID = null;
	private int requestedPublicRefs = 5;
	
	private JIOrpcThat orpcthat = null;
	private int hresult = -1;
	private JIStdObjRef stdObjRef = null;
	private boolean isSuccessful = false;
	
	JIRemUnknown(String ipidOfIUnknown,String requestedIID, int requestedPublicRefs)
	{
		this.ipidOfIUnknown = ipidOfIUnknown;
		this.requestedIID = requestedIID;
		this.requestedPublicRefs = requestedPublicRefs;
	}
	
	public int getOpnum() {
		//opnum is 3 as this is a COM interface and 0,1,2 are occupied by IUnknown
		//TODO remember this for extending com components also.
		return 3;
	}
	
	public void write(NetworkDataRepresentation ndr) 
	{
		
		
		JIOrpcThis orpcthis = new JIOrpcThis();
		orpcthis.encode(ndr);
		
		//now write the IPID
		
		
		UUID uuid = new UUID(ipidOfIUnknown);
		try {
			uuid.encode(ndr,ndr.buf);
		} catch (NdrException e) {
			
			JISystem.getLogger().throwing("JIRemUnknown","write",e);  
		}
		
		ndr.writeUnsignedLong(requestedPublicRefs); //crefs 5
		ndr.writeUnsignedShort(1);//1 interface.
		ndr.writeUnsignedShort(0);//byte alignment
		ndr.writeUnsignedLong(1);//length of the array
		uuid = new UUID(requestedIID);
		try {
			uuid.encode(ndr,ndr.buf);
		} catch (NdrException e) {
			
			JISystem.getLogger().throwing("JIRemUnknown","write",e);  
		}
		ndr.writeUnsignedLong(0); //TODO Index Matching , there seems to be a bug in 
									// the jarapac system, it only reads upto (length - 6) bytes and one has to have another
									// call after that or incomplete request will go. in case no param is present just put an unsigned long = 0.
	}
	
	public void read(NetworkDataRepresentation ndr)
	{
		orpcthat = JIOrpcThat.decode(ndr);
		int ptr = ndr.readUnsignedLong(); 
		if (ptr == 0)
		{
			//something happened.
			hresult = ndr.readUnsignedLong();//read hresult
			isSuccessful = false;
			throw new JIRuntimeException(hresult);
		}
		int length = ndr.readUnsignedLong(); //will be 1 only 
		hresult = ndr.readUnsignedLong();//read hresult , only 1 will be present.
		if (hresult != 0)
		{
			isSuccessful = false;
			throw new JIRuntimeException(hresult);
		}
		ndr.readUnsignedLong(); 
		stdObjRef = JIStdObjRef.decode(ndr);
		isSuccessful = true;
	}
	
	public JIOrpcThat getORPCThat()
	{
		return orpcthat;
	}
	
	public int getHresult()
	{
		return hresult;
	}
	
	public JIStdObjRef getObjectReference()
    {
    	return stdObjRef;
    }
	
	public boolean isSuccessful()
	{
		return isSuccessful;
	}
}
