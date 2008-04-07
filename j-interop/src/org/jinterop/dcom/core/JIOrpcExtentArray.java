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
