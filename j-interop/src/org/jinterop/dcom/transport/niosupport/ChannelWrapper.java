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

package org.jinterop.dcom.transport.niosupport;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * A wrapper for a selectable channel and a selection manager
 */
public interface ChannelWrapper
{
    /**
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * Follows the contract of SocketChannel.read() for non-blocking operations.
     *
     * @param buffer
     * @return bytes read
     * @throws IOException
     */
    int read(ByteBuffer buffer) throws IOException;

    /**
     * This method may result in a read attempt from the socket.
     *
     * @throws IOException
     */
    void registerForRead() throws IOException;

    /**
     * @throws IOException
     */
    void unregisterForRead() throws IOException;

    /**
     * Writes the remaining contents of the buffer. May block.
     *
     * @param buffer
     * @throws IOException
     */
    void writeAll(ByteBuffer buffer) throws IOException;

    /**
     * @return whether the underlying channel is connected.
     */
    boolean isConnected();

    /**
     * @return whether the underlying channel is open.
     */
    boolean isOpen();

    /**
     * Gets the remote socket address
     *
     * @return the remote socket address
     * @see java.net.Socket#getRemoteSocketAddress()
     */
    SocketAddress getRemoteSocketAddress();
}
