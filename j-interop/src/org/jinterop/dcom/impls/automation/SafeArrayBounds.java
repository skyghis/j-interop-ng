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

package org.jinterop.dcom.impls.automation;

import java.io.Serializable;

import org.jinterop.dcom.core.JIStruct;

/** Implements the <i>SAFEARRAYBOUNDS</i> structure of COM Automation.
 *
 * @since 1.0
 *
 */
public final class SafeArrayBounds implements Serializable{

	private static final long serialVersionUID = -3110688445129575984L;
	public final int cElements;
	public final int lLbound;

	SafeArrayBounds(JIStruct values)
	{
		if (values == null)
		{
			cElements = -1;
			lLbound = -1;
			return;
		}
		cElements = ((Integer)values.getMember(0)).intValue();
		lLbound = ((Integer)values.getMember(0)).intValue();
	}
}
