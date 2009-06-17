/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package org.jinterop.dcom.core;

import java.io.Serializable;
import java.util.Arrays;

final class JIOxid implements Serializable{

	private static final long serialVersionUID = 3456725801334190150L;
	byte[] oxid = null;

	JIOxid(byte[] oxid)
	{
		this.oxid = oxid;
	}

	byte[] getOXID()
	{
		return oxid;
	}

	public int hashCode()
	{
		int result = 1;
        //from SUN
        for (int i = 0;i< oxid.length;i++)
        {
            result = 31 * result + oxid[i];
        }
        return result;
		//return Arrays.hashCode(oxid);
	}

	 public boolean equals(Object obj)
	 {
		 if (!(obj instanceof JIOxid)) {
			return false;
		 }

		 return Arrays.equals(oxid,((JIOxid)obj).getOXID());
	 }

}
