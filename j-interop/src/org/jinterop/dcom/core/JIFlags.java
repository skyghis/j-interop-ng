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

package org.jinterop.dcom.core;

/** Class representing various FLAGS used in the system.
 * 
 * @since 1.0
 */
public final class JIFlags {
	private JIFlags(){}
	
	/**
	 * FLAG representing nothing. Use this when you don't know what flag to send.
	 */
	public static final int FLAG_NULL = 0;
	
	//bstr string
	/**
	 * FLAG representing a <code>BSTR</code> String . <br>
	 * MSDN defines it as <i>A length-prefixed string used by Automation data manipulation functions.</i>
	 */
	public static final int FLAG_REPRESENTATION_STRING_BSTR = 1;
	
	/**
	 * FLAG representing a normal String. 
	 */
	public static final int FLAG_REPRESENTATION_STRING_LPCTSTR = 2;
	
	/**
	 * FLAG representing a Wide Char (16 bit characters)
	 */
	public static final int FLAG_REPRESENTATION_STRING_LPWSTR = 4;
	
	/**
	 * @exclude
	 */
	//flag representing an array
	public static final int FLAG_REPRESENTATION_ARRAY = 8;
	
	/**
	 * @exclude
	 */
	//flag representing that this is a pointer
	public static final int FLAG_REPRESENTATION_POINTER = 16; 
	
	/**
	 * @exclude
	 */
	//flag representing that this is a reference
	public static final int FLAG_REPRESENTATION_REFERENCE = 32; 

	/**
	 * @exclude
	 */
	//flag representing that this is a IDispatch invoke call
	public static final int FLAG_REPRESENTATION_IDISPATCH_INVOKE = 64; 
	
	/**
	 * @exclude
	 */
	//flag representing that this is a IDispatch invoke call
	public static final int FLAG_REPRESENTATION_NESTED_POINTER = 128;
	
	/**
	 * Flag representing unsigned byte.
	 */
	public static final int FLAG_REPRESENTATION_UNSIGNED_BYTE = 256;
	
	/**
	 * Flag representing unsigned short.
	 */
	public static final int FLAG_REPRESENTATION_UNSIGNED_SHORT = 512;
	
	/**
	 * Flag representing unsigned integer.
	 */
	public static final int FLAG_REPRESENTATION_UNSIGNED_INT = 1024;
	
	/**
	 * Flag representing integer of the type VT_INT.
	 */
	public static final int FLAG_REPRESENTATION_VT_INT = 2048;
	
	/**
	 * Flag representing (unsigned) integer of the type VT_UINT.
	 */
	public static final int FLAG_REPRESENTATION_VT_UINT = 4096;
	
	/**
	 * Flag representing a boolean , VARIANT_BOOL, the boolean is 2 bytes for a variant and 1 byte for normal calls.
	 * Use this when setting array of booleans within Variants.
	 */
	public static final int FLAG_REPRESENTATION_VARIANT_BOOL = 8192;

	/**
	 * Represents an internal flag, which will disallow direct Strings from being marshalled or unmarshalled. Come via JIString only.
	 */
	static final int FLAG_REPRESENTATION_VALID_STRING = 16384; 
	
	/**
	 * Used from within JIInterfacePointer to use decode2 API.
	 */
	static final int FLAG_REPRESENTATION_INTERFACEPTR_DECODE2 = 32768;
}
