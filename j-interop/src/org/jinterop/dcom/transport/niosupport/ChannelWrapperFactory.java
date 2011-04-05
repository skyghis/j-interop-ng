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
import java.nio.channels.SelectableChannel;

/**
 * Factory for ChannelWrappers
 */
public final class ChannelWrapperFactory
{
    private ChannelWrapperFactory()
    {
        // Nothing to do
    }

    /**
     * Static method to create a Channel Wrapper.
     *
     * @param selectorManager
     * @param selectableChannel
     * @param channelListener
     * @return the new read/write channel wrapper
     * @throws IOException
     */
    public static ChannelWrapper createChannelWrapper(
            final SelectorManager selectorManager,
            final SelectableChannel selectableChannel,
            final ChannelListener channelListener) throws IOException
    {
        return new ChannelWrapperImpl(selectorManager, selectableChannel,
                channelListener);
    }
}
