/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package rpc;

import java.io.IOException;
import java.util.Properties;
import rpc.core.PresentationContext;

public interface ConnectionContext {

    public static final String MAX_TRANSMIT_FRAGMENT = "rpc.connectionContext.maxTransmitFragment";
    public static final String MAX_RECEIVE_FRAGMENT = "rpc.connectionContext.maxReceiveFragment";
    public static final int DEFAULT_MAX_TRANSMIT_FRAGMENT = 4280;
    public static final int DEFAULT_MAX_RECEIVE_FRAGMENT = 4280;

    public ConnectionOrientedPdu init(PresentationContext context, Properties properties) throws IOException;

    public ConnectionOrientedPdu alter(PresentationContext context) throws IOException;

    public ConnectionOrientedPdu accept(ConnectionOrientedPdu pdu) throws IOException;

    public Connection getConnection();

    public boolean isEstablished();

}
