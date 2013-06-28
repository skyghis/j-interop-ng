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

/** Interface for setting user credentials.
 * 
 * @since 1.0
 */
public interface IJIAuthInfo {

	/** Returns username.
 	 * 
	 * @return
	 */
	public String getUserName() ;
	
	/** Returns password.
	 * 
	 * @return
	 */
	public String getPassword();
	
	/** Returns user's domain.
	 * 
	 * @return
	 */
	public String getDomain() ;
	
}
