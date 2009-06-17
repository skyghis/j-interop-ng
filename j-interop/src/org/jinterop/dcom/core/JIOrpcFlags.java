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

package org.jinterop.dcom.core;


final class JIOrpcFlags  {

	private JIOrpcFlags() {}

	static final long ORPCF_NULL      = 0;  // no additional info in packet
	static final long ORPCF_LOCAL     = 1;  // call is local to this machine
	static final long ORPCF_RESERVED1 = 2;  // reserved for local use
	static final long ORPCF_RESERVED2 = 4;  // reserved for local use
	static final long ORPCF_RESERVED3 = 8;  // reserved for local use
	static final long ORPCF_RESERVED4 = 16; // reserved for local use



}
