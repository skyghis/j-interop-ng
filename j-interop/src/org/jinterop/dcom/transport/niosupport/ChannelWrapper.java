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
