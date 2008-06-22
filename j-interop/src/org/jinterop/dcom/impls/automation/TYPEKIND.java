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
package org.jinterop.dcom.impls.automation;

/**
 * 
 * @since 1.0
 *
 */
public interface TYPEKIND {

	public static final Integer TKIND_ENUM = new Integer(0);
	public static final Integer TKIND_RECORD = new Integer(1);
	public static final Integer TKIND_MODULE = new Integer(2);
	public static final Integer TKIND_INTERFACE = new Integer(3);
	public static final Integer TKIND_DISPATCH = new Integer(4);
	public static final Integer TKIND_COCLASS = new Integer(5);
	public static final Integer TKIND_ALIAS = new Integer(6);
	public static final Integer TKIND_UNION = new Integer(7);
	public static final Integer TKIND_MAX = new Integer(8);

}
