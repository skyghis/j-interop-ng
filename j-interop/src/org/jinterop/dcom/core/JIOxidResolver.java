/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2007  Vikram Roopchand
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
