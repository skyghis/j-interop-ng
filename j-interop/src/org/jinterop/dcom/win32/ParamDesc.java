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

package org.jinterop.dcom.win32;

import java.io.Serializable;

import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/**Contains information needed for transferring a structure element, parameter, or function return value between processes
 * 
 * @since 1.0
 *
 */
public final class ParamDesc implements Serializable {

	private static final long serialVersionUID = 7181403713923608809L;
	public static final short PARAMFLAG_NONE         = 0x00;
	public static final short PARAMFLAG_FIN          = 0x01;
	public static final short PARAMFLAG_FOUT         = 0x02;
	public static final short PARAMFLAG_FLCID        = 0x04;
	public static final short PARAMFLAG_FRETVAL      = 0x08;
	public static final short PARAMFLAG_FOPT         = 0x10;
	public static final short PARAMFLAG_FHASDEFAULT  = 0x20;
	public static final short PARAMFLAG_FHASCUSTDATA = 0x40;

	
	public final JIPointer lpVarValue;
	//public final JIVariant lpVarValue;
	public final short wPARAMFlags;
	
	ParamDesc(JIStruct values)
	{
		if (values == null)
		{
			lpVarValue = null;
			wPARAMFlags = -1;
			return;
		}
		
		lpVarValue = (JIPointer)values.getMember(0);
		//lpVarValue = (JIVariant)values.getMember(0);
		wPARAMFlags = ((Short)values.getMember(1)).shortValue();
	}
	
}
