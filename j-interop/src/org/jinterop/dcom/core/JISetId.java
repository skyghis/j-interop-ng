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
