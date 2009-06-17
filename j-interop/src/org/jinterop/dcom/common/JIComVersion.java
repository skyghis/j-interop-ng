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

package org.jinterop.dcom.common;

import java.io.Serializable;

/**<p> Framework Internal.
 * This class represents the <code>COM</code> version of the currently 
 * supported COM protocol. Default version is 5.2.
 * </p>
 * @exclude
 * @since 1.0
 */
public final class JIComVersion implements Serializable {

	
	private static final long serialVersionUID = -1252228963385487909L;
	private int majorVersion = 5;
	private int minorVersion = 2;
	
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
