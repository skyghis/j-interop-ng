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

/** Java representation of a C++ <i>unsigned</i> number. An unsigned number can
 * be obtained by using {@link JIUnsignedFactory#getUnsigned(Number, int)}.
 * 
 * @since 1.15(b)
 */
public interface IJIUnsigned {

	/** Returns the unsigned type (<code>byte</code>,<code>short</code>,<code>int</code>).
	 * 
	 * @return {@link JIFlags#FLAG_REPRESENTATION_UNSIGNED_BYTE} or 
	 * {@link JIFlags#FLAG_REPRESENTATION_UNSIGNED_SHORT} or 
	 * {@link JIFlags#FLAG_REPRESENTATION_UNSIGNED_INT}
	 */
	public int getType();
	
	/** Returns the number represented by this object.
	 * 
	 * @return value 
	 */
	public Number getValue();
}
