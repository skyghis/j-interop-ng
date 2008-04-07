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

package rpc.security.ntlm;

import java.io.IOException;
import java.util.Properties;

import rpc.BindException;
import rpc.Connection;
import rpc.ConnectionContext;
import rpc.ConnectionOrientedPdu;
import rpc.FaultException;
import rpc.PresentationException;
import rpc.RpcException;
import rpc.core.PresentationContext;
import rpc.core.PresentationResult;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.Auth3Pdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindNoAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.ShutdownPdu;

public class NtlmConnectionContext implements ConnectionContext {

    private int maxTransmitFragment = DEFAULT_MAX_TRANSMIT_FRAGMENT;

    private int maxReceiveFragment = DEFAULT_MAX_RECEIVE_FRAGMENT;

    private NtlmConnection connection;

    private boolean established;

    private int transmitLength;

    private int receiveLength;
    
    private int assocGroupId = 0;

    public ConnectionOrientedPdu init2(PresentationContext context,
            Properties properties) throws IOException {
        established = false;
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
        connection = new NtlmConnection(properties);
        assocGroupId = 0;
        return pdu;
    }
    
    public ConnectionOrientedPdu init(PresentationContext context,
            Properties properties) throws IOException {
        
    	BindPdu pdu = (BindPdu)init2(context, properties);
        pdu.resetCallIdCounter();
        return pdu;
    }

    public ConnectionOrientedPdu alter(PresentationContext context)
            throws IOException {
        established = false;
        AlterContextPdu pdu = new AlterContextPdu();
        pdu.setContextList(new PresentationContext[] { context });
        pdu.setAssociationGroupId(assocGroupId);
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
            connection.setTransmitLength(transmitLength);
            connection.setReceiveLength(receiveLength);
            assocGroupId = bindAck.getAssociationGroupId();
            return new Auth3Pdu();
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
            //return new Auth3Pdu();
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
            established = false;
            //CHECK PRESENTATION CONTEXT
            //CHALLENGE
            throw new RuntimeException();
        case AlterContextPdu.ALTER_CONTEXT_TYPE:
            established = false;
            //CHECK PRESENTATION CONTEXT
            //CHALLENGE
            throw new RuntimeException();
        case Auth3Pdu.AUTH3_TYPE:
            //AUTHENTICATE
            //TWEAK CONNECTION
            established = true;
            return null;
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
