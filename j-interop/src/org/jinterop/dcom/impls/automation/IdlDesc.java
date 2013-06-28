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
