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

package org.jinterop.dcom.impls.automation;

import java.io.Serializable;

import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/** Implements the <i>PARAMDESC</i> structure of COM Automation. Contains
 * information needed for transferring a structure element, parameter,
 * or function return value between processes.
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

	/**
	 * IN, OUT, etc
	 */
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
