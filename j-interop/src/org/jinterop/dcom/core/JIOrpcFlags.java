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
