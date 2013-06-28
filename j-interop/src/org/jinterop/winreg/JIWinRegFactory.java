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

package org.jinterop.winreg;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.IJIAuthInfo;
import org.jinterop.winreg.smb.JIWinRegStub;



/** Factory to get an implementation of <code>IJIWinReg</code>.
 * <p>This interface uses "Windows Remote Registry" and "Server" services and these must be running on target workstation.
 *
 * @since 1.0
 *
 */
//based upon the transport intended to be used this
//factory provides either the smb impl of ijiwinreg or the tcp/ip one.
public class JIWinRegFactory {

	private JIWinRegFactory() {};

	private static JIWinRegFactory factory = null;

	/** Instantiates the Factory.
	 *
	 * @return
	 */
	public static JIWinRegFactory getSingleTon()
	{
		if (factory == null)
		{
			synchronized (JIWinRegFactory.class) {
				if (factory == null)
				{
					factory = new JIWinRegFactory();
				}
			}
		}

		return factory;
	}

	/** Gets an Implementation of WinReg interface, currently only SMB transport is supported.
	 *
	 * @param authInfo credentials for access to Windows Remote Registry service
	 * @param serverName target server
	 * @param smbTransport true if SMB transport is required , false will return null.
	 * @return
	 * @throws UnknownHostException
	 */
	public IJIWinReg getWinreg(IJIAuthInfo authInfo,String serverName, boolean smbTransport) throws UnknownHostException
	{
		if (smbTransport)
		{
			return new JIWinRegStub(authInfo,serverName);
		}
		else
		{
			return null;
		}
	}

	/** Gets an Implementation of WinReg interface, currently only SMB transport is supported.
	 *
	 * @param smbTransport true if SMB transport is required , false will return null.
	 * @return
	 * @throws UnknownHostException
	 */
	public IJIWinReg getWinreg(String serverName, boolean smbTransport) throws UnknownHostException
	{
		if (smbTransport)
		{
			return new JIWinRegStub(serverName);
		}
		else
		{
			return null;
		}
	}
}
