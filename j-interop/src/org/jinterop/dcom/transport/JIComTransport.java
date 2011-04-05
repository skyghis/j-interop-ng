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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import ndr.NdrBuffer;

import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.niosupport.ChannelListener;
import org.jinterop.dcom.transport.niosupport.ChannelWrapper;
import org.jinterop.dcom.transport.niosupport.ChannelWrapperFactory;
import org.jinterop.dcom.transport.niosupport.SelectorManager;

import rpc.Endpoint;
import rpc.ProviderException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationSyntax;

/**
 * Borrowed all from ncacn_ip_tcp.RpcTransport from jarapac.
 *
 * @exclude
 * @since 1.0
 */
final class JIComTransport implements Transport
{
    public static final String PROTOCOL = "ncacn_ip_tcp";

    private static final String LOCALHOST;

    private static final long DEFAULT_READ_READY_HANDOFF_TIMEOUT_SECS = 30;

    private static Object HANDOFF = new Object();

    private Properties properties;

    private String host;

    private int port;

    private boolean attached;

    private ChannelWrapper channelWrapper;

    private final SelectorManager selectorManager;

    // Use this as means of indicating to the reader thread that data is ready
    // to be read...
    // (alternatively could use a CyclicBarrier - but have to reset broken
    // barrier on a
    // timeout which causes spurious BrokenBarrierExceptions anyway (is this
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6253848 ?)).
    private final SynchronousQueue<Object> readReadyHandoff = new SynchronousQueue<Object>();

    private long readReadyHandoffTimeoutSecs = DEFAULT_READ_READY_HANDOFF_TIMEOUT_SECS;

    static
    {
        String localhost = null;
        try
        {
            localhost = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex)
        { /* ignored */
        }
        LOCALHOST = localhost;
    }

    public JIComTransport(String address, SelectorManager selectorManager,
            Properties properties) throws ProviderException
    {
        this.selectorManager = selectorManager;
        this.properties = properties;

        parse(address);
    }

    private void parse(String address) throws ProviderException
    {
        if (address == null)
        {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncacn_ip_tcp:"))
        {
            throw new ProviderException("Not an ncacn_ip_tcp address.");
        }
        address = address.substring(13);
        int index = address.indexOf('[');
        if (index == -1)
        {
            throw new ProviderException("No port specifier present.");
        }
        String server = address.substring(0, index);
        address = address.substring(index + 1);
        index = address.indexOf(']');
        if (index == -1)
        {
            throw new ProviderException("Port specifier not terminated.");
        }
        address = address.substring(0, index);
        if ("".equals(server))
        {
            server = LOCALHOST;
        }
        try
        {
            port = Integer.parseInt(address);
        }
        catch (Exception ex)
        {
            throw new ProviderException("Invalid port specifier.");
        }
        host = server;
    }

    /**
     * @see rpc.Transport#getProtocol()
     */
    public String getProtocol()
    {
        return PROTOCOL;
    }

    /**
     * @see rpc.Transport#getProperties()
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * @see rpc.Transport#attach(rpc.core.PresentationSyntax)
     */
    public Endpoint attach(PresentationSyntax syntax) throws IOException
    {
        if (attached)
        {
            throw new RpcException("Transport already attached.");
        }

        try
        {
            if (JISystem.getLogger().isLoggable(Level.FINEST))
            {
                JISystem.getLogger().finest(
                        "Opening socket on "
                                + new InetSocketAddress(InetAddress
                                        .getByName(host), port));
            }

            final SocketChannel channel = SocketChannel.open();

            // Connects without a timeout. If a timeout is needed then someone
            // should write a blockingConnect() method similar to the
            // blockingRead() method.
            channel.connect(new InetSocketAddress(InetAddress.getByName(host),
                    port));

            channelWrapper = ChannelWrapperFactory.createChannelWrapper(
                    selectorManager, channel, new ChannelListener()
                    {
                       
                        public void readReady()
                        {
                            try
                            {
                                if (!readReadyHandoff.offer(HANDOFF,
                                        readReadyHandoffTimeoutSecs,
                                        TimeUnit.SECONDS))
                                {
                                    // Maybe the reader thread has died between
                                    // adding read interest and waiting for the
                                    // handoff
                                    if (JISystem.getLogger().isLoggable(
                                            Level.FINE))
                                    {
                                        JISystem.getLogger().fine(
                                                "Timeout while awaiting read ready handoff to "
                                                        + JIComTransport.this);
                                    }
                                }
                            }
                            catch (InterruptedException e)
                            {
                                // Re-set interrupt flag
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

            // Configure the channel to be non-blocking, we will handle
            // simulating blocking mode using selectors. Using a blocking
            // connect above is fine as that does not cause the NIO code to
            // generate temporary pipe on Linux/Unix.
            channel.configureBlocking(false);

            attached = true;

            // backup for not providing a timeout...
            channel.socket().setKeepAlive(true);

            return new JIComEndpoint(this, syntax);
        }
        catch (IOException ex)
        {
            try
            {
                close();
            }
            catch (Exception ignore)
            { /* ignored */
            }
            throw ex;
        }
    }

    /**
     * @see rpc.Transport#close()
     */
    public void close() throws IOException
    {
        try
        {
            if (channelWrapper != null)
            {
                if (JISystem.getLogger().isLoggable(Level.FINEST))
                {
                    JISystem.getLogger().finest("Closing " + channelWrapper);
                }
                channelWrapper.close();
            }
        }
        finally
        {
            attached = false;
            channelWrapper = null;
        }
    }

    /**
     * @see rpc.Transport#send(ndr.NdrBuffer)
     */
    public void send(NdrBuffer buffer) throws IOException
    {
        if (!attached)
        {
            throw new RpcException("Transport not attached.");
        }

        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer.getBuffer(), 0,
                buffer.getLength());

        channelWrapper.writeAll(byteBuffer);
    }

    /**
     * @see rpc.Transport#receive(ndr.NdrBuffer)
     */
    public void receive(NdrBuffer buffer) throws IOException
    {
        if (!attached)
        {
            throw new RpcException("Transport not attached.");
        }

        final int timeoutMillis = getCurentTimeoutMillis();

        // Register for read and wait for the read to occur
        channelWrapper.registerForRead();

        try
        {
            Object handoffResult;
            if (timeoutMillis == 0)
            {
                handoffResult = readReadyHandoff.take();
            }
            else
            {
                handoffResult = readReadyHandoff.poll(timeoutMillis,
                        TimeUnit.MILLISECONDS);
            }

            if (handoffResult == null)
            {
                throw new SocketTimeoutException();
            }

            final ByteBuffer wrapped = ByteBuffer.wrap(buffer.getBuffer());

            buffer.length = channelWrapper.read(wrapped);
        }
        catch (InterruptedException e)
        {
            // Re-set interrupted flag
            Thread.currentThread().interrupt();

            throw new IOException("Interrupted while reading");
        }
    }

    /**
     * Returns the current socket timeout.
     */
    private int getCurentTimeoutMillis()
    {
        int timeout = 0;
        try
        {
            timeout = Integer.parseInt(this.properties.getProperty(
                    "rpc.socketTimeout", "0"));
        }
        catch (NumberFormatException e)
        { /* ignored */
        }

        return timeout;
    }

    /**
     * @see java.lang.Object#toString()
     */
   
    public String toString()
    {
        return "Transport to " + host + ":" + port;
    }
}
