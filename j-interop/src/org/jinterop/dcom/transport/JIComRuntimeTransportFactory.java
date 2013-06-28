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

package org.jinterop.dcom.transport;

import java.util.Properties;

import rpc.ProviderException;
import rpc.Transport;

/**
 * @exclude
 * @since 1.0
 *
 */
public final class JIComRuntimeTransportFactory extends rpc.TransportFactory {

	private static JIComRuntimeTransportFactory factory = null;
	private JIComRuntimeTransportFactory() {}

	public Transport createTransport(String address, Properties properties)
    	throws ProviderException {
			return new JIComRuntimeTransport(address, properties);
	}

	public static JIComRuntimeTransportFactory getSingleTon()
	{
		if (factory == null)
		{
			synchronized (JIComTransportFactory.class) {
				if (factory == null)
				{
					factory = new JIComRuntimeTransportFactory();
				}
			}
		}

		return factory;
	}
}
