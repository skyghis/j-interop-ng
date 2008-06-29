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
