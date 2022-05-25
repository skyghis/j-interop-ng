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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ndr.NdrBuffer;
import org.jinterop.IoUtils;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

final class JIComTransport implements Transport {

    public static final String PROTOCOL = "ncacn_ip_tcp";
    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("ncacn_ip_tcp:(?<host>[^\\[]*)\\[(?<port>\\d+)\\]");
    private static final String LOCALHOST = getLocalhostName();
    private final Properties properties;
    private final String host;
    private final int port;
    private SocketChannel channel = null;
    private Socket socket = null;
    private boolean attached = false;

    JIComTransport(String address, Properties properties) throws ProviderException {
        this.properties = properties;

        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncacn_ip_tcp:")) {
            throw new ProviderException("Not an ncacn_ip_tcp address.");
        }
        final Matcher addressMatcher = ADDRESS_PATTERN.matcher(address);
        if (!addressMatcher.matches()) {
            throw new ProviderException("Invalid address format, expecting 'ncacn_ip_tcp:host[port]' got '" + address + "'");
        }
        this.host = addressMatcher.group("host").isEmpty() ? LOCALHOST : addressMatcher.group("host");
        this.port = Integer.parseInt(addressMatcher.group("port"));
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
        try {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "Opening socket on {0}", new InetSocketAddress(InetAddress.getByName(host), port));
            }
            channel = SocketChannel.open();
            socket = channel.socket();
            int timeout = getSocketTimeout();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(InetAddress.getByName(host), port), timeout);

            attached = true;

            return new JIComEndpoint(this, syntax);
        } catch (IOException ex) {
            close();
            throw ex;
        } catch (RuntimeException ex) {
            close();
            throw new IOException("Failed to attach COM Transport on " + this);
        }
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public void close() {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "Close socket {0} host {1} , port {2}", new Object[]{socket, host, port});
        }
        socket = IoUtils.closeSilent(socket, "JIComTransport socket");
        channel = IoUtils.closeSilent(channel, "JIComTransport channel");
        attached = false;
    }

    @Override
    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        final OutputStream output = socket.getOutputStream();
        output.write(buffer.getBuffer(), 0, buffer.getLength());
        output.flush();
    }

    @Override
    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        buffer.length = channel.read(ByteBuffer.wrap(buffer.getBuffer()));
    }

    private int getSocketTimeout() {
        try {
            return Integer.parseInt(this.properties.getProperty("rpc.socketTimeout", "0"));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, ex, () -> "Invalid timeout value " + this.properties.getProperty("rpc.socketTimeout"));
            return 0;
        }
    }

    private static String getLocalhostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
}
