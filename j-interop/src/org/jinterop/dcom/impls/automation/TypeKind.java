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

/** Implements the <i>TYPEKIND</i> structure of COM Automation
 *
 * @since 2.0 (formerly TYPEKIND)
 *
 */
public interface TypeKind {

	/**
	 * A set of enumerators.
	 */
	public static final Integer TKIND_ENUM = new Integer(0);
	/**
	 * A structure with no methods.
	 */
	public static final Integer TKIND_RECORD = new Integer(1);
	/**
	 * A module that can only have static functions and data (for example, a DLL).
	 */
	public static final Integer TKIND_MODULE = new Integer(2);
	/**
	 * A type that has virtual and pure functions.
	 */
	public static final Integer TKIND_INTERFACE = new Integer(3);
	/**
	 * A set of methods and properties that are accessible through IDispatch::Invoke.
	 * By default, dual interfaces return TKIND_DISPATCH.
	 */
	public static final Integer TKIND_DISPATCH = new Integer(4);
	/**
	 * A set of implemented component object interfaces.
	 */
	public static final Integer TKIND_COCLASS = new Integer(5);
	/**
	 * A type that is an alias for another type.
	 */
	public static final Integer TKIND_ALIAS = new Integer(6);
	/**
	 * A union, all of whose members have an offset of zero.
	 */
	public static final Integer TKIND_UNION = new Integer(7);
	/**
	 * End of ENUM marker.
	 */
	public static final Integer TKIND_MAX = new Integer(8);

}
