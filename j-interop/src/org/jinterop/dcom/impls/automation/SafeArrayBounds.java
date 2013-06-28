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
