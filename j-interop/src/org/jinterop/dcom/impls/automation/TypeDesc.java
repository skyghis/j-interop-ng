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

/**Implements the <i>TYPEDESC</i> structure of COM Automation and
 * describes the type of a variable, the return type of a function,
 * or the type of a function parameter.
 *
 *@since 1.0
 */
public final class TypeDesc implements Serializable{

	private static final long serialVersionUID = 6276233095707726579L;
	public static final Short VT_PTR = new Short((short)0x1a);
	public static final Short VT_SAFEARRAY = new Short((short)0x1b);
	public static final Short VT_CARRAY = new Short((short)0x1c);
	public static final Short VT_USERDEFINED = new Short((short)0x1d);

	public final JIPointer typeDesc;
	public final JIPointer arrayDesc;
	public final int hrefType;
	public final short vt;

	TypeDesc(JIStruct values)
	{
		if (values == null)
		{
			typeDesc = null;
			arrayDesc = null;
			hrefType = -1;
			vt = -1;
			return;
		}

		vt = ((Short)values.getMember(1)).shortValue();
		JIUnion union = (JIUnion)values.getMember(0);

		if (new Short(vt).equals(VT_PTR) || new Short(vt).equals(VT_SAFEARRAY))
		{
			JIPointer pointer = (pointer = (JIPointer)union.getMembers().get(VT_PTR)) == null ? (JIPointer)union.getMembers().get(VT_SAFEARRAY) : pointer ;
			typeDesc = new JIPointer(new TypeDesc(pointer),false);
			arrayDesc = null;
			hrefType = -1;
		}
		else if (new Short(vt).equals(VT_CARRAY))
		{
			hrefType = -1;
			typeDesc = null;
			arrayDesc = new JIPointer(new ArrayDesc((JIPointer)union.getMembers().get(VT_CARRAY)));
		}
		else if (new Short(vt).equals(VT_USERDEFINED))
		{
			typeDesc = null;
			arrayDesc = null;
			hrefType = ((Integer)union.getMembers().get(VT_USERDEFINED)).intValue();
		}
		else
		{
			typeDesc = null;
			arrayDesc = null;
			hrefType = -1;
		}

	}

	TypeDesc(JIPointer values)
	{
		this(values.isNull() ? null : (JIStruct)values.getReferent());
	}

}
