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

/** Default implementation of <code>IJIAuthInfo</code>.
 * 
 * @since 1.0
 */
public final class JIDefaultAuthInfoImpl implements IJIAuthInfo {

	private String username = null;
	private String password = null;
	private String domain = null;
	
	/**Creates the AuthInfo Object.
	 * 
	 * @param domain
	 * @param username
	 * @param password
	 */
	public JIDefaultAuthInfoImpl(String domain,String username,String password)
	{
		this.username = username;
		this.password = password;
		this.domain = domain;
	}
	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDomain() {
		return domain;
	}

}
