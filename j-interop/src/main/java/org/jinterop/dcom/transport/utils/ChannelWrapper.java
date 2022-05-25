/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jinterop.dcom.transport.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows non-blocking reads, but writes are blocking.
 */
public final class ChannelWrapper implements Closeable {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private final SelectorManager selectorManager;
    private final SocketChannel selectableChannel;

    public ChannelWrapper(SelectorManager selectorManager, SocketChannel selectableChannel, Runnable channelListener) throws IOException {
        this.selectorManager = selectorManager;
        this.selectableChannel = selectableChannel;
        this.selectorManager.registerChannel(selectableChannel, channelListener);
    }

    @Override
    public void close() throws IOException {
        selectableChannel.close();
    }

    @Override
    public String toString() {
        return "Channel to " + getRemoteSocketAddress();
    }

    public boolean isConnected() {
        return selectableChannel.isConnected();
    }

    public boolean isOpen() {
        return selectableChannel.isOpen();
    }

    public SocketAddress getRemoteSocketAddress() {
        return selectableChannel.socket().getRemoteSocketAddress();
    }

    public int read(final ByteBuffer buffer) throws IOException {
        return selectableChannel.read(buffer);
    }

    public void registerForRead() throws IOException {
        selectorManager.setReadInterest(selectableChannel);
    }

    public void unregisterForRead() throws IOException {
        selectorManager.removeReadInterest(selectableChannel);
    }

    private int write(final ByteBuffer buffer) throws IOException {
        return selectableChannel.write(buffer);
    }

    public void writeAll(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            final int bytesWritten = write(buffer);
            LOGGER.log(Level.FINE, "{0} bytes written {1}", new Object[]{this, bytesWritten});
        }
    }
}
