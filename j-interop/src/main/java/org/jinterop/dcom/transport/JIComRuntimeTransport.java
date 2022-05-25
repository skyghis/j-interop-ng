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
import ndr.NdrBuffer;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.utils.IoUtils;
import rpc.Endpoint;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

/**
 * @since 1.0
 */
public class JIComRuntimeTransport implements Transport {

    private static final String PROTOCOL = "ncacn_ip_tcp";
    private final Properties properties;
    protected Socket socket;
    private OutputStream output;
    private InputStream input;
    private boolean attached;

    protected JIComRuntimeTransport(String address, Properties properties) {
        this.properties = properties;
        //address is ignored
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
    public Endpoint attach(PresentationSyntax syntax) throws RpcException {
        if (attached) {
            throw new RpcException("Transport already attached.");
        }
        try {
            socket = this.getSocket();
            output = null;
            input = null;
            attached = true;
            return getEndpoint(syntax);
        } catch (RuntimeException ex) {
            close();
            return null;
        }
    }

    protected Endpoint getEndpoint(PresentationSyntax syntax) {
        return new JIComRuntimeEndpoint(this, syntax);
    }

    protected Socket getSocket() {
        return JISystem.internal_getSocket();
    }

    @Override
    public void close() {
        socket = IoUtils.closeSilent(socket);
        output = null;
        input = null;
        attached = false;
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
