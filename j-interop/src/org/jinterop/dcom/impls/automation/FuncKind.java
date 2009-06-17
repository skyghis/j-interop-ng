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
