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

import java.io.IOException;
import java.util.Properties;

import org.jinterop.dcom.transport.niosupport.SelectorManager;

import rpc.ProviderException;
import rpc.Transport;

/**
 * Factory for {@link JIComTransport}
 */
public final class JIComTransportFactory extends rpc.TransportFactory
{
    private static JIComTransportFactory instance;

    private final SelectorManager selectorManager;

    /**
     * Constructor for JIComTransportFactory.
     */
    private JIComTransportFactory() throws IOException
    {
        selectorManager = new SelectorManager();
    }

    /**
     * @see rpc.TransportFactory#createTransport(java.lang.String,
     *      java.util.Properties)
     */
    public Transport createTransport(String address, Properties properties)
            throws ProviderException
    {
        return new JIComTransport(address, selectorManager, properties);
    }

    /**
     * @return the singleton instance
     */
    public static JIComTransportFactory getSingleton()
    {
        synchronized (JIComTransportFactory.class)
        {
            if (instance == null)
            {
                try
                {
                    instance = new JIComTransportFactory();
                }
                catch (IOException e)
                {
                    throw new ExceptionInInitializerError(e);
                }
            }
            return instance;
        }
    }

    public static JIComTransportFactory getSingleTon()
    {
        return getSingleton();
    }
}
