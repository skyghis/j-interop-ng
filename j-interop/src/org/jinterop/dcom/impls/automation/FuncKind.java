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

/** Implements the <i>FUNCKIND</i> structure of COM Automation.
 *
 * @since 2.0 (formerly FUNCKIND)
 *
 */
public interface FuncKind {

	/**
	 * The function is accessed the same as PUREVIRTUAL, except the function has an implementation.
	 */
	public static final Integer FUNC_VIRTUAL = new Integer(0);
	/**
	 * The function is accessed through the virtual function table (VTBL), and takes an implicit <i>this</i> pointer.
	 */
	public static final Integer FUNC_PUREVIRTUAL = new Integer(1);
	/**
	 * The function is accessed by static address and takes an implicit <i>this</i> pointer.
	 */
	public static final Integer FUNC_NONVIRTUAL = new Integer(2);
	/**
	 * The function is accessed by static address and does not take an implicit <i>this</i> pointer.
	 */
	public static final Integer FUNC_STATIC = new Integer(3);
	/**
	 * The function can be accessed only through IDispatch.
	 */
	public static final Integer FUNC_DISPATCH = new Integer(4);

}
