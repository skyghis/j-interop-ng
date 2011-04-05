/*
 * Copyright 2004 WIT-Software, Lda. 
 * - web: http://www.wit-software.com 
 * - email: info@wit-software.com
 *
 * All rights reserved. Relased under terms of the 
 * Creative Commons' Attribution-NonCommercial-ShareAlike license.
 */
package org.jinterop.dcom.transport.niosupport;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;

import org.jinterop.dcom.common.JISystem;

/**
 * Wrapper for a {@link SelectableChannel} so that it can straightforwardly be
 * used with a {@link SelectorManager}.
 * <p>
 * Allows non-blocking reads, but writes are blocking.
 */
public final class ChannelWrapperImpl implements ChannelWrapper
{
    private final SelectorManager selectorManager;

    private final SelectableChannel selectableChannel;

    private final ChannelListener channelListener;

    /**
     * Constructor for ChannelWrapperImpl.
     * 
     * @param selectorManager
     * @param selectableChannel
     * @param channelListener
     * @throws IOException
     */
    ChannelWrapperImpl(final SelectorManager selectorManager,
            final SelectableChannel selectableChannel,
            final ChannelListener channelListener) throws IOException
    {
        this.selectorManager = selectorManager;
        this.selectableChannel = selectableChannel;
        this.channelListener = channelListener;

        selectorManager.registerChannel(selectableChannel, channelListener);
    }

    private ChannelListener getChannelListener()
    {
        return channelListener;
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#isConnected()
     */
   
    public boolean isConnected()
    {
        return ((SocketChannel) selectableChannel).isConnected();
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#isOpen()
     */
   
    public boolean isOpen()
    {
        return selectableChannel.isOpen();
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#getRemoteSocketAddress()
     */
   
    public SocketAddress getRemoteSocketAddress()
    {
        return ((SocketChannel) selectableChannel).socket()
                .getRemoteSocketAddress();
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#read(java.nio.ByteBuffer)
     */
   
    public int read(final ByteBuffer buffer) throws IOException
    {
        return ((ReadableByteChannel) selectableChannel).read(buffer);
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#registerForRead()
     */
   
    public void registerForRead() throws IOException
    {
        selectorManager.setReadInterest(selectableChannel);
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#unregisterForRead()
     */
   
    public void unregisterForRead() throws IOException
    {
        selectorManager.removeReadInterest(selectableChannel);
    }

    private int write(final ByteBuffer buffer) throws IOException
    {
        return ((WritableByteChannel) selectableChannel).write(buffer);
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#writeAll(java.nio.ByteBuffer)
     */
   
    public void writeAll(ByteBuffer buffer) throws IOException
    {
        while (buffer.hasRemaining())
        {
            final int bytesWritten = write(buffer);

            if (JISystem.getLogger().isLoggable(Level.FINE))
            {
                JISystem.getLogger().fine(
                        this + " bytes written " + bytesWritten);
            }
        }
    }

    /**
     * @see org.jinterop.dcom.transport.niosupport.ChannelWrapper#close()
     */
   
    public void close() throws IOException
    {
        selectableChannel.close();
    }

    /**
     * @see java.lang.Object#toString()
     */
   
    public String toString()
    {
        return "Channel to " + getRemoteSocketAddress();
    }
}
