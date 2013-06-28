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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import ndr.NdrBuffer;

import org.jinterop.dcom.common.JISystem;

import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;
/**
 * @exclude
 * @since 1.0
 *
 */
final class JIComRuntimeTransport implements Transport {


	public static final String PROTOCOL = "ncacn_ip_tcp";

    private Properties properties;


    private Socket socket;

    private OutputStream output;

    private InputStream input;

    private boolean attached;


    public JIComRuntimeTransport(String address, Properties properties)
            throws ProviderException {
        this.properties = properties;
        //address is ignored
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    public Properties getProperties() {
        return properties;
    }

    public Endpoint attach(PresentationSyntax syntax) throws IOException {
        if (attached) throw new RpcException("Transport already attached.");

        Endpoint endPoint = null;
        try {
            socket = (Socket)JISystem.internal_getSocket();
            output = null;
            input = null;
            attached = true;
            endPoint = new JIComRuntimeEndpoint(this, syntax);
        } catch (Exception ex) {
            try {
                close();
            } catch (Exception ignore) { }
        }
        return endPoint;
    }

    public void close() throws IOException {
        try {
            if (socket != null) socket.close();
        } finally {
            attached = false;
            socket = null;
            output = null;
            input = null;
        }
    }

    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        if (output == null) output = socket.getOutputStream();
        output.write(buffer.getBuffer(), 0, buffer.getLength());
        output.flush();
    }

    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        if (input == null) input = socket.getInputStream();
        buffer.length = (input.read(buffer.getBuffer(), 0,
                buffer.getCapacity()));
    }


}
