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
