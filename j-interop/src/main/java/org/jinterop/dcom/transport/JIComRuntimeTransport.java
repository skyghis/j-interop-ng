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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import ndr.NdrBuffer;
import org.jinterop.dcom.common.JISystem;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

public final class JIComRuntimeTransport implements Transport {

    public static final String PROTOCOL = "ncacn_ip_tcp";
    private static final Supplier<Socket> DEFAULT_SOCKET_FACTORY = () -> (Socket) JISystem.internal_getSocket();
    private final Supplier<Socket> socketFactory;
    private final BiFunction<Transport, PresentationSyntax, Endpoint> endpointFactory;
    private final Properties properties;
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private boolean attached;

    public JIComRuntimeTransport(String address, Properties properties) throws ProviderException {
        //address is ignored
        this(properties, DEFAULT_SOCKET_FACTORY, JIComRuntimeEndpoint::new);
    }

    public JIComRuntimeTransport(Properties properties) {
        this(properties, DEFAULT_SOCKET_FACTORY, JIComRuntimeEndpoint::new);
    }

    private JIComRuntimeTransport(Properties properties, Supplier<Socket> socketFactory, BiFunction<Transport, PresentationSyntax, Endpoint> endpointFactory) {
        this.socketFactory = socketFactory;
        this.endpointFactory = endpointFactory;
        this.properties = properties;
    }

    public JIComRuntimeTransport withSocketFactory(Supplier<Socket> socketFactory) {
        return new JIComRuntimeTransport(properties, socketFactory, endpointFactory);
    }

    public JIComRuntimeTransport withEndpointFactory(BiFunction<Transport, PresentationSyntax, Endpoint> endpointFactory) {
        return new JIComRuntimeTransport(properties, socketFactory, endpointFactory);
    }

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public Endpoint attach(PresentationSyntax syntax) throws IOException {
        if (attached) {
            throw new RpcException("Transport already attached.");
        }

        Endpoint endPoint = null;
        try {
            socket = socketFactory.get();
            output = null;
            input = null;
            attached = true;
            endPoint = endpointFactory.apply(this, syntax);
        } catch (RuntimeException ex) {
            close();
        }
        return endPoint;
    }

    @Override
    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger("org.jinterop").log(Level.WARNING, "Failed to close socket " + socket, ex);
        } finally {
            attached = false;
            socket = null;
            output = null;
            input = null;
        }
    }

    @Override
    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        if (output == null) {
            output = socket.getOutputStream();
        }
        output.write(buffer.getBuffer(), 0, buffer.getLength());
        output.flush();
    }

    @Override
    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        if (input == null) {
            input = socket.getInputStream();
        }
        buffer.length = (input.read(buffer.getBuffer(), 0, buffer.getCapacity()));
    }
}
