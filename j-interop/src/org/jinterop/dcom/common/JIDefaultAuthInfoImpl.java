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
