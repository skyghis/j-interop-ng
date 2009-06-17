/** j-Interop (Pure Java implementation of DCOM protocol)
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

/** Implements the <i>INVOKEKIND</i> structure of COM Automation.
 *
 * @since 2.0 (formerly INVOKEKIND)
 */
public interface InvokeKind {

	/**
	 *  The member is called using a normal function invocation syntax
	 */
	public static final Integer INVOKE_FUNC = new Integer(1);

	/**
	 * The function is invoked using a normal property-access syntax.
	 */
	public static final Integer INVOKE_PROPERTYGET = new Integer(2);

	/**
	 * The function is invoked using a property value assignment syntax.
	 * Syntactically, a typical programming language might represent
	 * changing a property in the same way as assignment.
	 * For example:object.property : = value.
	 */
	public static final Integer INVOKE_PROPERTYPUT = new Integer(4);

	/**
	 *  The function is invoked using a property reference assignment syntax.
	 */
	public static final Integer INVOKE_PROPERTYPUTREF = new Integer(8);
}
