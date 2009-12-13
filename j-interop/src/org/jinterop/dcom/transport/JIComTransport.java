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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
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

/**Borrowed all from ncacn_ip_tcp.RpcTransport from jarapac, modified attach api to include SocketChannel.
 *
 * @exclude
 * @since 1.0
 *
 */
final class JIComTransport implements Transport {

	public static final String PROTOCOL = "ncacn_ip_tcp";

    private static final String LOCALHOST;

    private Properties properties;

    private String host;

    private int port;

    private Socket socket;

    private OutputStream output;

    private InputStream input;

    private boolean attached;

    private boolean timeoutModifiedfrom0 = false;
    
    private SocketChannel channel = null;

    static {
        String localhost = null;
        try {
            localhost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) { }
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

        	channel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(host),port));
        	socket = channel.socket();//new Socket(host, port);
            output = null;
            input = null;
            attached = true;
            socket.setKeepAlive(true);//backup for not providing a timeout.
            return new JIComEndpoint(this,syntax);
        } catch (IOException ex) {
            try {
                close();
            } catch (Exception ignore) { }
            throw ex;
        }
    }

    public void close() throws IOException {
        try {
            if (socket != null)
        	{
//            	input.close();
//            	output.close();
            	socket.shutdownInput();
            	socket.shutdownOutput();
            	socket.close();
            	channel.close();
            	if (JISystem.getLogger().isLoggable(Level.FINEST))
            	{
            		JISystem.getLogger().finest("Socket closed... " + socket + " host " + host + " , port " + port);
            	}
        	}
        } finally {
            attached = false;
            socket = null;
            output = null;
            input = null;
            channel = null;
        }
    }

    public void send(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        if (output == null) output = socket.getOutputStream();
        channel.configureBlocking(true);
        output.write(buffer.getBuffer(), 0, buffer.getLength());
        output.flush();
    }

    public void receive(NdrBuffer buffer) throws IOException {
        if (!attached) throw new RpcException("Transport not attached.");
        applySocketTimeout();
        if (input == null) input = socket.getInputStream();
        buffer.length = (input.read(buffer.getBuffer(), 0,
                buffer.getCapacity()));
    }

    private void applySocketTimeout ()
    {
	    int timeout = 0;
	    try
	    {
	    	timeout = Integer.parseInt(this.properties.getProperty("rpc.socketTimeout", "0"));
	    	if (timeout != 0)
	    	{
	    		socket.setSoTimeout(timeout);
	    		timeoutModifiedfrom0 = true;
	    	}
	    	else
	    	{
	    		if (timeoutModifiedfrom0)
	    		{
	    			socket.setSoTimeout(timeout);
	    			timeoutModifiedfrom0 = false;
	    		}
	    	}
	    }
	    catch ( Exception e )
	    {
	    }
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

}
