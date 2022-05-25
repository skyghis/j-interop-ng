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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ndr.NdrBuffer;
import org.jinterop.dcom.transport.utils.ChannelWrapper;
import org.jinterop.dcom.transport.utils.IoUtils;
import org.jinterop.dcom.transport.utils.SelectorManager;
import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

final class JIComTransport implements Transport {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final String PROTOCOL = "ncacn_ip_tcp";
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(PROTOCOL + ":(?<host>[^\\[]*)\\[(?<port>\\d+)\\]");
    private static final String LOCALHOST = getLocalhostName();
    private static final Object HANDOFF = new Object();
    private final SynchronousQueue<Object> readReadyHandoff = new SynchronousQueue<>();
    private final Properties properties;
    private final String host;
    private final int port;
    private final SelectorManager selectorManager;
    private ChannelWrapper wrappedChannel;
    private boolean attached = false;

    JIComTransport(String address, SelectorManager selectorManager, Properties properties) throws ProviderException {
        this.selectorManager = selectorManager;
        this.properties = properties;

        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith(PROTOCOL + ":")) {
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
            LOGGER.log(Level.FINEST, "Opening socket on {0}", this);
            final int timeout = getSocketTimeout();
            final SocketChannel channel = SocketChannel.open();

            final Socket socket = channel.socket();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(InetAddress.getByName(host), port), timeout);
            // Configure the channel to be non-blocking, we will handle simulating blocking mode using selectors.
            // Using a blocking connect above is fine as that does not cause the NIO code to generate temporary pipe on Linux/Unix.
            channel.configureBlocking(false);
            wrappedChannel = new ChannelWrapper(selectorManager, channel, () -> {
                try {
                    if (!readReadyHandoff.offer(HANDOFF, timeout, TimeUnit.MILLISECONDS)) {
                        // Maybe the reader thread has died between adding read interest and waiting for the handoff
                        LOGGER.log(Level.WARNING, "Timeout while awaiting read ready handoff to {0}", JIComTransport.this);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Re-set interrupt flag
                }
            });
            attached = true;
            return new JIComEndpoint(this, syntax);

        } catch (IOException ex) {
            close();
            throw ex;
        } catch (RuntimeException ex) {
            close();
            throw new IOException("Failed to attach COM Transport on " + this, ex);
        }
    }

    @Override
    public String toString() {
        return "Transport to " + host + ":" + port;
    }

    @Override
    public void close() {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "Close channelWrapper {0} host {1} , port {2}", new Object[]{wrappedChannel, host, port});
        }
        wrappedChannel = IoUtils.closeSilent(wrappedChannel);
        attached = false;
    }

    @Override
    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer.getBuffer(), 0, buffer.getLength());
        wrappedChannel.writeAll(byteBuffer);
    }

    @Override
    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) {
            throw new RpcException("Transport not attached.");
        }

        final int timeoutMillis = getSocketTimeout();
        // Register for read and wait for the read to occur
        wrappedChannel.registerForRead();
        try {
            final Object handoffResult;
            if (timeoutMillis == 0) {
                handoffResult = readReadyHandoff.take();
            } else {
                handoffResult = readReadyHandoff.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            }
            if (handoffResult == null) {
                throw new SocketTimeoutException();
            }
            buffer.length = wrappedChannel.read(ByteBuffer.wrap(buffer.getBuffer()));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Re-set interrupted flag
            throw new IOException("Interrupted while reading");
        }
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
