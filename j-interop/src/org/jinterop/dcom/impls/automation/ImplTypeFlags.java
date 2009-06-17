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
