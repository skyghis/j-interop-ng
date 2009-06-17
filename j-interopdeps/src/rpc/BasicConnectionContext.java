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
import rpc.core.PresentationResult;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindNoAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.ShutdownPdu;

public class BasicConnectionContext implements ConnectionContext {

    private int maxTransmitFragment = DEFAULT_MAX_TRANSMIT_FRAGMENT;

    private int maxReceiveFragment = DEFAULT_MAX_RECEIVE_FRAGMENT;

    private Connection connection;

    private boolean established;

    private int transmitLength;

    private int receiveLength;

    public ConnectionOrientedPdu init(PresentationContext context,
            Properties properties) throws IOException {
        established = false;
        connection = new DefaultConnection();
        if (properties != null) {
            String maxTransmit = properties.getProperty(MAX_TRANSMIT_FRAGMENT);
            if (maxTransmit != null) {
                maxTransmitFragment = Integer.parseInt(maxTransmit);
            }
            String maxReceive = properties.getProperty(MAX_RECEIVE_FRAGMENT);
            if (maxReceive != null) {
                maxReceiveFragment = Integer.parseInt(maxReceive);
            }
        }
        BindPdu pdu = new BindPdu();
        pdu.setContextList(new PresentationContext[] { context });
        pdu.setMaxTransmitFragment(maxTransmitFragment);
        pdu.setMaxReceiveFragment(maxReceiveFragment);
        return pdu;
    }

    public ConnectionOrientedPdu alter(PresentationContext context)
            throws IOException {
        established = false;
        AlterContextPdu pdu = new AlterContextPdu();
        pdu.setContextList(new PresentationContext[] { context });
        return pdu;
    }

    public ConnectionOrientedPdu accept(ConnectionOrientedPdu pdu)
            throws IOException {
        PresentationResult[] results = null;
        switch (pdu.getType()) {
        case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
            BindAcknowledgePdu bindAck = (BindAcknowledgePdu) pdu;
            results = bindAck.getResultList();
            if (results == null) {
                throw new BindException("No presentation context results.");
            }
            for (int i = results.length - 1; i >= 0; i--) {
                if (results[i].result != PresentationResult.ACCEPTANCE) {
                    throw new PresentationException("Context rejected.",
                            results[i]);
                }
            }
            transmitLength = bindAck.getMaxReceiveFragment();
            receiveLength = bindAck.getMaxTransmitFragment();
            established = true;
            connection = new DefaultConnection(transmitLength, receiveLength);
            return null;
        case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
            AlterContextResponsePdu alterContextResponse =
                    (AlterContextResponsePdu) pdu;
            results = alterContextResponse.getResultList();
            if (results == null) {
                throw new BindException("No presentation context results.");
            }
            for (int i = results.length - 1; i >= 0; i--) {
                if (results[i].result != PresentationResult.ACCEPTANCE) {
                    throw new PresentationException("Context rejected.",
                            results[i]);
                }
            }
            established = true;
            return null;
        case BindNoAcknowledgePdu.BIND_NO_ACKNOWLEDGE_TYPE:
            throw new BindException("Unable to bind.",
                    ((BindNoAcknowledgePdu) pdu).getRejectReason());
        case FaultCoPdu.FAULT_TYPE:
            throw new FaultException("Fault occurred.",
                    ((FaultCoPdu) pdu).getStatus());
        case ShutdownPdu.SHUTDOWN_TYPE:
            throw new RpcException("Server shutdown connection.");
        case BindPdu.BIND_TYPE:
        case AlterContextPdu.ALTER_CONTEXT_TYPE:
            throw new RpcException("Server-side currently unsupported.");
        default:
            throw new RpcException("Unknown/unacceptable PDU type.");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isEstablished() {
        return established;
    }

}
