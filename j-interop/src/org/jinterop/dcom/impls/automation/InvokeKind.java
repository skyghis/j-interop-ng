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
