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
