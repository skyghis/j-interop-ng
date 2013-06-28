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

/**Implements the <i>CALLCONV</i> data type of COM Automation.
 * <p>
 * Definition from MSDN: <i> Identifies the calling convention used by a member function. </i>
 *
 *
 *@since 2.0 (formerly CALLCONV)
 */
public interface CallConv {

	public static final Integer CC_FASTCALL   = new Integer(0);
	/**
	 * Indicates that the Cdecl calling convention is used for a method.
	 */
	public static final Integer CC_CDECL      = new Integer(1);
	/**
	 * Indicates that the Mscpascal calling convention is used for a method.
	 */
	public static final Integer CC_MSCPASCAL  = new Integer(CC_CDECL.intValue() + (1));
	/**
	 * Indicates that the Pascal calling convention is used for a method.
	 */
	public static final Integer CC_PASCAL     = CC_MSCPASCAL;
	/**
	 * Indicates that the Macpascal calling convention is used for a method.
	 */
	public static final Integer CC_MACPASCAL  = new Integer(CC_PASCAL.intValue() + 1);
	/**
	 * Indicates that the Stdcall calling convention is used for a method.
	 */
	public static final Integer CC_STDCALL    = new Integer(CC_MACPASCAL.intValue() + 1);
	public static final Integer CC_FPFASTCALL = new Integer(CC_STDCALL.intValue() + 1);
	/**
	 * Indicates that the Syscall calling convention is used for a method.
	 */
	public static final Integer CC_SYSCALL    = new Integer(CC_FPFASTCALL.intValue() + 1);
	/**
	 * Indicates that the Mpwcdecl calling convention is used for a method.
	 */
	public static final Integer CC_MPWCDECL   = new Integer(CC_SYSCALL.intValue() + 1);
	/**
	 * Indicates that the Mpwpascal calling convention is used for a method.
	 */
	public static final Integer CC_MPWPASCAL  = new Integer(CC_MPWCDECL.intValue() + 1);
	/**
	 *Indicates the end of the CALLCONV enumeration.
	 */
	public static final Integer CC_MAX    	  = new Integer(CC_MPWPASCAL.intValue() + 1);

}
