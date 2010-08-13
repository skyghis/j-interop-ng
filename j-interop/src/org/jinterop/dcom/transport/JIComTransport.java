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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.logging.Level;

import ndr.NdrBuffer;

import org.jinterop.dcom.common.JISystem;

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
final class JIComTransport implements Transport {

	public static final String PROTOCOL = "ncacn_ip_tcp";

    private static final String LOCALHOST;

    private Properties properties;

    private String host;

    private int port;

    private Socket socket;

    private boolean attached;

    private SocketChannel channel = null;
    
    static {
        String localhost = null;
        try {
            localhost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) { /*ignored*/ }
        LOCALHOST = localhost;
    }

	public JIComTransport(String address, Properties properties)
    throws ProviderException {
        this.properties = properties;
        parse(address);
     }

    public String getProtocol() {
        return PROTOCOL;
    }

    public Properties getProperties() {
        return properties;
    }

    public Endpoint attach(PresentationSyntax syntax) throws IOException {
        if (attached) throw new RpcException("Transport already attached.");
        try {
        	if (JISystem.getLogger().isLoggable(Level.FINEST))
        	{
        		JISystem.getLogger().finest("Opening socket on " + new InetSocketAddress(InetAddress.getByName(host),port));
        	}

        	channel = SocketChannel.open();
        	
        	// Connects without a timeout. If a timeout is needed then someone should
        	// write a blockingConnect() method similar to the blockingRead() method.
        	channel.connect(new InetSocketAddress(InetAddress.getByName(host), port));
        	
        	// Configure the channel to be non-blocking, we will handle simulating
            // blocking mode using selectors. Using a blocking connect above is fine
        	// as that does not cause the NIO code to generate temporary pipe on Linux/Unix.
            channel.configureBlocking(false);
        	
        	socket = channel.socket();        	
            attached = true;
            socket.setKeepAlive(true); //backup for not providing a timeout.
            
            return new JIComEndpoint(this,syntax);
        } catch (IOException ex) {
            try {
                close();
            } catch (Exception ignore) { /*ignored*/ }
            throw ex;
        }
    }
    
    public void close() throws IOException {
        
        try {
            if (socket != null)
        	{
            	socket.shutdownInput();
            	socket.shutdownOutput();
            	socket.close();            	
            	if (JISystem.getLogger().isLoggable(Level.FINEST))
            	{
            		JISystem.getLogger().finest("Socket closed... " + socket + " host " + host + " , port " + port);
            	}
        	}
            if ( channel != null ) {
                // Even if the socket is null, we still need to close the channel as well.
                channel.close();
            }
        } finally {
            attached = false;
            socket = null;
            channel = null;
        }
    }

    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        
        // This will not cause the file descriptor leak
        channel.configureBlocking(true);
        
        // This is cached by the socket so we don't need to cache it ourselves.
        final OutputStream output = socket.getOutputStream();
         
        output.write(buffer.getBuffer(), 0, buffer.getLength());
        output.flush();
    }

    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        
        final int timeout = getCurentTimeout();
        // We have to handle the read+timeout ourselves
        final ByteBuffer wrapped = ByteBuffer.wrap(buffer.getBuffer());
        buffer.length = blockingRead(wrapped, timeout);
    }

    protected void parse(String address) throws ProviderException {
        if (address == null) {
            throw new ProviderException("Null address.");
        }
        if (!address.startsWith("ncacn_ip_tcp:")) {
            throw new ProviderException("Not an ncacn_ip_tcp address.");
        }
        address = address.substring(13);
        int index = address.indexOf('[');
        if (index == -1) {
            throw new ProviderException("No port specifier present.");
        }
        String server = address.substring(0, index);
        address = address.substring(index + 1);
        index = address.indexOf(']');
        if (index == -1) {
            throw new ProviderException("Port specifier not terminated.");
        }
        address = address.substring(0, index);
        if ("".equals(server)) server = LOCALHOST;
        try {
            port = Integer.parseInt(address);
        } catch (Exception ex) {
            throw new ProviderException("Invalid port specifier.");
        }
        host = server;
    }
    
    /**
     * Returns the current socket timeout.
     */
    private int getCurentTimeout() {
        int timeout = 0;
        try
        {
            timeout = Integer.parseInt(this.properties.getProperty("rpc.socketTimeout", "0"));
        }
        catch ( Exception e ) { /*ignored*/ }
        
        return timeout;
    }
    
    /**
     * Reads from the socket into the provided ByteBuffer. If a timeout has been specified
     * a SocketTimeoutException will be generated if the read times out.
     * 
     * @return The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream.
     */
    private int blockingRead(ByteBuffer bb, int timeout)  throws IOException {
        synchronized (channel.blockingLock()) {
            if ( ! channel.isBlocking() ) {
                throw new IllegalBlockingModeException();
            }
            
            if ( timeout == 0 ) {
                // that was easy.
                return channel.read(bb);
            }
            
            // We have to implement a timeout with a selector.
            
            SelectionKey sk = null;
            Selector sel = null;
            channel.configureBlocking(false);
            
            try {
                int n;
                if ( (n = channel.read(bb)) != 0 ) {
                    // we got something right away, no need to wait
                    return n;
                }
                
                sel = Selector.open();
                sk = channel.register(sel, SelectionKey.OP_READ);
                
                long to = timeout;
                final long start = System.currentTimeMillis();
                
                for (;;) {
                    if ( ! channel.isOpen() ) {
                        // got closed while we were waiting
                        throw new ClosedChannelException();
                    }
                    
                    int numSelected = sel.select(to);
                    
                    if ( numSelected > 0 && sk.isReadable() ) {
                        // there is something there to be read
                        if ( (n = channel.read(bb)) != 0 ) {
                            return n;
                        }
                    }
                    
                    // We timed out. Remove the key, reduce the remaining time
                    // and try again. We might still have some time left before
                    // the timeout expires.
                    sel.selectedKeys().remove(sk);
                    to -= System.currentTimeMillis() - start;
                    if ( to <= 0 ) {
                        // we ran out of time. A "timeout" if you will ...
                        throw new SocketTimeoutException();
                    }
                }
                
            }
            finally {
                // Selector cleanup
                if ( sk != null ) {
                    sk.cancel();
                }
                if ( sel != null ) {
                    sel.close();
                }
            }
        }
    }

}
