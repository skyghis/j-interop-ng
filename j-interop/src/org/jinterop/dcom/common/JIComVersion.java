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

package org.jinterop.dcom.common;

import java.io.Serializable;

/**<p> Framework Internal.
 * This class represents the <code>COM</code> version of the currently 
 * supported COM protocol. Default version is 5.4.
 * </p>
 * @exclude
 * @since 1.0
 */
public final class JIComVersion implements Serializable {

	
	private static final long serialVersionUID = -1252228963385487909L;
	private int majorVersion = 5;
	private int minorVersion = 4;
	
	public JIComVersion() {}
	
	public JIComVersion(int majorVersion, int minorVersion)
	{
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}
	
}
