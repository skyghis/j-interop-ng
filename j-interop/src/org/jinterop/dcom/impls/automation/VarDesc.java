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
import org.jinterop.dcom.core.JIUnion;

/** Implements the <i>VARDESC</i> structure of COM Automation
 *
 * @since 1.0
 *
 */
public final class VarDesc implements Serializable{

	private static final long serialVersionUID = -3874889610447398180L;
	public static final int VAR_PERINSTANCE = 0;
	public static final int VAR_STATIC = 1;
	public static final int VAR_CONST = 2;
	public static final int VAR_DISPATCH = 3;

	public final int memberId;
	public final JIPointer lpstrSchema;
	public final JIUnion u;
	/**
	 *  Contains the variable type.
	 */
	public final ElemDesc elemdescVar;
	/**
	 * Definition of flags follows
	 */
	public final short wVarFlags;
	public final int varkind;

	VarDesc(JIPointer values)
	{
		this(values.isNull() ? null : (JIStruct)values.getReferent());
	}

	VarDesc(JIStruct filledStruct)
	{
		if (filledStruct == null)
		{
			memberId = -1;
			lpstrSchema = null;
			u = null;
			elemdescVar = null ;
			wVarFlags = -1;
			varkind = -1;
			return;
		}

		memberId = ((Integer)filledStruct.getMember(0)).intValue();
		lpstrSchema = (JIPointer)filledStruct.getMember(1);
		u = (JIUnion)filledStruct.getMember(2);
		elemdescVar = new ElemDesc((JIStruct)filledStruct.getMember(3));
		wVarFlags = ((Short)filledStruct.getMember(4)).shortValue();
		varkind = ((Integer)filledStruct.getMember(5)).intValue();
	}


}
