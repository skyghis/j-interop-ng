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

package org.jinterop.dcom.impls;

import java.io.Serializable;

import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/**
 * @exclude
 * @since 1.0
 *
 */
public final class IdlDesc implements Serializable{

	private static final long serialVersionUID = 7130410752801712935L;
	public static final short IDLFLAG_NONE    = ParamDesc.PARAMFLAG_NONE;
	public static final short IDLFLAG_FIN     = ParamDesc.PARAMFLAG_FIN;
	public static final short IDLFLAG_FOUT    = ParamDesc.PARAMFLAG_FOUT;
	public static final short IDLFLAG_FLCID   = ParamDesc.PARAMFLAG_FLCID;
	public static final short IDLFLAG_FRETVAL = ParamDesc.PARAMFLAG_FRETVAL;

	
	public final JIPointer dwReserved;
	public final short wIDLFlags;
	
	IdlDesc(JIStruct values)
	{
		if (values == null)
		{
			dwReserved = null;
			wIDLFlags = -1;
			return;
		}
		dwReserved = (JIPointer)values.getMember(0);
		wIDLFlags = ((Short)values.getMember(1)).shortValue();
	}

}
