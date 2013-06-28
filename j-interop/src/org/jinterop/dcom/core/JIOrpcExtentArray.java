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


/**
 *
 * @since 1.0
 *
 */
final class JIOrpcExtentArray implements Serializable {

	private static final long serialVersionUID = -3594184670915738836L;
	private String uuid = null;
	private int size = -1;
	private Byte[] data = null;

	JIOrpcExtentArray(String guid,int size,Byte[] data)
	{
		uuid = guid;
		this.size = size;
		this.data = data;
	}

	public String getGUID()
	{
		return uuid;
	}

	public int getSizeOfData()
	{
		return size;
	}

	public byte[] getData()
	{
		byte[] newData = new byte[data.length];
		for (int i = 0;i < data.length;i++)
		{
			newData[i] = data[i].byteValue();
		}
		return newData;
	}
}
