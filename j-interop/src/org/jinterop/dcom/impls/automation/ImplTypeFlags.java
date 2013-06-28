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

/** Implements the <i>IMPLTYPEFLAGS</i> structure of COM Automation.
 *
 * @since 2.0 (formerly IMPLTYPEFLAGS)
 */

public interface ImplTypeFlags {

	/**
	 * The interface or dispinterface represents the default for the source or sink.
	 */
	public static final int IMPLTYPEFLAG_FDEFAULT = 0x1;
	/**
	 * This member of a coclass is called rather than implemented.
	 */
	public static final int IMPLTYPEFLAG_FSOURCE =  0x2;
	/**
	 * The member should not be displayed or programmable by users.
	 */
	public static final int IMPLTYPEFLAG_FRESTRICTED = 0x4;
	/**
	 * Sinks receive events through the VTBL.
	 */
	public static final int IMPLTYPEFLAG_FDEFAULTVTABLE = 0x800;

}
