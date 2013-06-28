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
