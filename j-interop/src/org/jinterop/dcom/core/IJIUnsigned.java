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
