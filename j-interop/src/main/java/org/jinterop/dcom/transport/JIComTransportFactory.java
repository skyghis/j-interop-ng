/** j-Interop (Pure Java implementation of DCOM protocol)
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

import java.io.IOException;
import java.util.Properties;
import org.jinterop.dcom.transport.utils.SelectorManager;
import rpc.ProviderException;
import rpc.Transport;
import rpc.TransportFactory;

/**
 * Factory for {@link JIComTransport}
 *
 * @since 1.0
 */
public final class JIComTransportFactory implements TransportFactory {

    private static JIComTransportFactory instance = null;
    private final SelectorManager selectorManager;

    private JIComTransportFactory() {
        try {
            selectorManager = new SelectorManager();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Transport createTransport(String address, Properties properties) throws ProviderException {
        return new JIComTransport(address, selectorManager, properties);
    }

    public static JIComTransportFactory getSingleton() {
        synchronized (JIComTransportFactory.class) {
            if (instance == null) {
                instance = new JIComTransportFactory();
            }
            return instance;
        }
    }

    @Deprecated // Use getSingleton instead
    public static JIComTransportFactory getSingleTon() {
        return getSingleton();
    }
}
