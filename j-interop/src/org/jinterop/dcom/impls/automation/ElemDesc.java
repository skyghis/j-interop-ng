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

/**Implements the <i>ELEMDESC</i> structure of COM Automation.
 * <p>
 * Definition from MSDN: <i> Includes the type description and process-transfer
 * information for a variable a function, or a function parameter.
 * </i>
 *
 *
 * @since 1.0
 *
 */
public final class ElemDesc implements Serializable{

	private static final long serialVersionUID = 3022259075461969376L;
	/**
	 * Type of the element.
	 */
	public final TypeDesc typeDesc;
	/**
	 * Information about the parameter.
	 */
	public final ParamDesc paramDesc;

	public ElemDesc(JIStruct values)
	{
		if (values == null)
		{
			typeDesc = null;
			paramDesc = null;
			return;
		}
		typeDesc = new TypeDesc((JIStruct)values.getMember(0));
		paramDesc = new ParamDesc((JIStruct)values.getMember(1));
	}

	ElemDesc(JIPointer ptrValues)
	{
		this(ptrValues.isNull() ? null : (JIStruct)ptrValues.getReferent());
	}

}
