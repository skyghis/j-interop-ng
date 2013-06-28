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

import java.io.Serializable;

import ndr.NetworkDataRepresentation;

final class JIStringBinding implements Serializable {


	private static final long serialVersionUID = -5797400235890434880L;

	private JIStringBinding(){}

	private int towerId = -1;

	//IP or resolved name follwed by port in []
	private String networkAddress = null;

	private int length = -1;

	public int getLength()
	{
		return length;
	}

	//private static boolean test = false;
	JIStringBinding(int port,boolean hostname)
	{
		String hostaddress = null;
		if (!hostname)
		{
			//single binding with our IP address
			hostaddress = JISession.getLocalhostAddressAsIPString();
		}
		else
		{
			hostaddress = JISession.getLocalhostCanonicalAddressAsString();
		}

		if (port == -1)
		{
			networkAddress = hostaddress ;
		}
		else
		{
			networkAddress = hostaddress + "[" + Integer.toString(port) + "]";
		}

		length = 2 + networkAddress.length() * 2 + 2;
		towerId = 0x7; //TCP_IP
	}

	JIStringBinding(int port)
	{
		this(port,false);
	}

	static JIStringBinding decode(NetworkDataRepresentation ndr)
	{
		JIStringBinding stringBinding = new JIStringBinding();

		stringBinding.towerId = ndr.readUnsignedShort();

		//hit the end , security bindings start.
		if (stringBinding.towerId == 0)
		{
			return null;
		}

		//now to read the String till a null termination character.
		// a '0' will be represented as 30
		int retVal = -1;
		StringBuffer buffer = new StringBuffer();
		while ((retVal = ndr.readUnsignedShort()) != 0)
		{
			//even though this is a unicode string , but will not have anything else
			//other than ascii charset, which is supported by all encodings.
			buffer.append(new String(new byte[]{(byte)retVal}));
		}

		stringBinding.networkAddress = buffer.toString();

		// 2 bytes for tower id, each character is 2 bytes (short) and last 2 bytes for null termination
		stringBinding.length = 2 + stringBinding.networkAddress.length() * 2 + 2;



		return stringBinding;
	}

	public int getTowerId()
	{
		return towerId;
	}

	public String getNetworkAddress()
	{
		return networkAddress;
	}

	public void encode(NetworkDataRepresentation ndr)
	{
		ndr.writeUnsignedShort(towerId);

		//now to write the network address.
		int i = 0;
		while (i < networkAddress.length())
		{
			ndr.writeUnsignedShort(networkAddress.charAt(i));
			i++;
		}

//		//TODO testing only.
//		if (networkAddress.length()%2 != 0)
//		{
//			ndr.writeUnsignedShort(0);
//		}
		ndr.writeUnsignedShort(0); //null termination

	}

}
