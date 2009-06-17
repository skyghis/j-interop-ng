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

}
