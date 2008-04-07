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

import java.io.Serializable;
import java.util.ArrayList;

import ndr.NetworkDataRepresentation;

/** Represents array of network address and security bindings.
 * 
 * @exclude
 * @since 1.0
 */
final class JIDualStringArray implements Serializable {


	private static final long serialVersionUID = -3351948896808028565L;
	
	private JIDualStringArray(){}
	
	 //static boolean test = false;
	//Will get called from Oxid Resolver
	JIDualStringArray(int port)
	{
		//create bindings here.
		stringBinding = new JIStringBinding[2]; //only 1
		stringBinding[0] = new JIStringBinding(port,false);
		
		length = stringBinding[0].getLength();
	
		stringBinding[1] = new JIStringBinding(port,true);
		
		length = length + stringBinding[1].getLength() + 2; //null termination
		
		secOffset = length;
		
		securityBinding = new JISecurityBinding[1]; //support only winnt NTLM
		securityBinding[0] = new JISecurityBinding(0x0a,0xffff,"");
		length = length + securityBinding[0].getLength();
		
		length = length + 2 + 2 + 2; //null termination, 2 bytes for num entries and 2 bytes for sec offset.
	}
	
	private JIStringBinding[] stringBinding = null;
	private JISecurityBinding[] securityBinding = null;
	private int length = 0;
	private int secOffset = 0;

	static JIDualStringArray decode(NetworkDataRepresentation ndr)
	{
		JIDualStringArray dualStringArray = new JIDualStringArray();
		
		//first extract number of entries
		int numEntries = ndr.readUnsignedShort();
		
		//return empty
		if (numEntries == 0)
			return dualStringArray;
		
		//extract security offset
		int securityOffset = ndr.readUnsignedShort();
		
		ArrayList listOfStringBindings = new ArrayList();
		ArrayList listOfSecurityBindings = new ArrayList();
		
		boolean stringbinding = true;
		while (true)
		{
			if (stringbinding)
			{
				JIStringBinding s = JIStringBinding.decode(ndr);
				if (s == null)
				{
					stringbinding = false;
					//null termination
					dualStringArray.length = dualStringArray.length + 2;
					dualStringArray.secOffset = dualStringArray.length;  
					continue;
				}
				
				listOfStringBindings.add(s);
				dualStringArray.length = dualStringArray.length + s.getLength();
			}
			else
			{
				JISecurityBinding s = JISecurityBinding.decode(ndr);
				if (s == null)
				{
					//null termination
					dualStringArray.length = dualStringArray.length + 2;
					break;
				}
				
				listOfSecurityBindings.add(s);
				dualStringArray.length = dualStringArray.length + s.getLength();
			}
			
		}
		
		// 2 bytes for num entries and 2 bytes for sec offset.
		dualStringArray.length = dualStringArray.length + 2 + 2;
		
		dualStringArray.stringBinding = (JIStringBinding[])listOfStringBindings.toArray(new JIStringBinding[listOfStringBindings.size()]);
		dualStringArray.securityBinding = (JISecurityBinding[])listOfSecurityBindings.toArray(new JISecurityBinding[listOfSecurityBindings.size()]);
		return dualStringArray;
	}
	
	public JIStringBinding[] getStringBindings()
	{
		return stringBinding;
	}
	
	public JISecurityBinding[] getSecurityBindings()
	{
		return securityBinding;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public void encode(NetworkDataRepresentation ndr)
	{
		//fill num entries
		//this is total length/2. since they are all shorts
		ndr.writeUnsignedShort((length - 4)/2);
		ndr.writeUnsignedShort((secOffset)/2);
		
		int i = 0;
		if(stringBinding != null)
		{
			while (i < stringBinding.length)
			{
				stringBinding[i].encode(ndr);
				i++;
			}
			ndr.writeUnsignedShort(0);
		}
		
		
		
		
		i = 0;
		
		if(securityBinding != null)
		{
			while (i < securityBinding.length)
			{
				securityBinding[i].encode(ndr);
				i++;
			}
			ndr.writeUnsignedShort(0);
		}
		
	}
	
}

