/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc;

import java.io.IOException;
import java.util.Properties;

import ndr.NdrBuffer;
import rpc.core.PresentationSyntax;

public interface Transport {

    public String getProtocol();

    public Properties getProperties();

    public Endpoint attach(PresentationSyntax syntax) throws IOException;

    public void send(NdrBuffer buffer) throws IOException;

    public void receive(NdrBuffer buffer) throws IOException;

    public void close() throws IOException;

}
