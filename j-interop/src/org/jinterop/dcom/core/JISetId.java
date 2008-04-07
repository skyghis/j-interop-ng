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
import java.util.Arrays;

final class JISetId implements Serializable{
	

	private static final long serialVersionUID = -3819165506317998524L;
	byte[] setid = null;
	
	JISetId(byte[] setid)
	{
		this.setid = setid;
	}
	
	byte[] getSetID()
	{
		return setid;
	}
	
	public int hashCode()
	{
		int result = 1;
        //from SUN
        for (int i = 0;i< setid.length;i++)
        {
            result = 31 * result + setid[i];
        }
        return result;
		//return Arrays.hashCode(setid);
	}
	
	 public boolean equals(Object obj)
	 {
		 if (!(obj instanceof JISetId)) {
			return false;
		 }
		 
		 return Arrays.equals(setid,((JISetId)obj).getSetID());
	 }

}
