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
