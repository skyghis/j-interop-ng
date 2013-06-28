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

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JISystem;

/**
 * Class representing the unsigned c++ integer.
 *
 * @since 1.15(b)
 *
 */
public final class JIUnsignedInteger implements IJIUnsigned {

	private final Long intValue;

	JIUnsignedInteger(Long intValue)
	{
		if (intValue == null || intValue.longValue() < 0)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_UNSIGNED_NEGATIVE));
		}
		this.intValue = intValue;
	}

	public int getType() {
		return JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT;
	}

	public Number getValue() {
		return intValue;
	}

}
