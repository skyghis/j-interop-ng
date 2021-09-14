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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.util.Hexdump;
import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import rpc.core.PresentationContext;
import rpc.core.PresentationResult;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.RequestCoPdu;
import rpc.pdu.ResponseCoPdu;
import rpc.pdu.ShutdownPdu;

public class ConnectionOrientedEndpoint implements Endpoint {

    private static final Logger logger = Logger.getLogger("org.jinterop");
    public static final String CONNECTION_CONTEXT = "rpc.connectionContext";
    private final Transport transport;
    private final PresentationSyntax syntax;
    private boolean bound;
    private int contextIdCounter = 0;
    private int contextIdToUse = contextIdCounter;
    //This is so as to reuse the contextids for already exported contexts.
    private final Map<String, Integer> uuidsVsContextIds = new HashMap<>();
    protected String currentIID = null;
    protected ConnectionContext context;

    public ConnectionOrientedEndpoint(Transport transport, PresentationSyntax syntax) {
        this.transport = transport;
        this.syntax = syntax;
    }

    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public PresentationSyntax getSyntax() {
        return syntax;
    }

    @Override
    public void call(int semantics, UUID object, int opnum, NdrObject ndrobj) throws IOException {
        bind();
        RequestCoPdu request = new RequestCoPdu();
        request.setContextId(contextIdToUse);

        byte[] b = new byte[1024];
        NdrBuffer buffer = new NdrBuffer(b, 0);
        NetworkDataRepresentation ndr = new NetworkDataRepresentation();
        ndrobj.encode(ndr, buffer);
        byte[] stub = new byte[buffer.getLength()];
        /* yuk */
        System.arraycopy(buffer.buf, 0, stub, 0, stub.length);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "{0}", Hexdump.toHexString(stub));
        }

        request.setStub(stub);
        request.setAllocationHint(buffer.getLength());
        request.setOpnum(opnum);
        request.setObject(object);
        if ((semantics & MAYBE) != 0) {
            request.setFlag(ConnectionOrientedPdu.PFC_MAYBE, true);
        }
        send(request);

        if (request.getFlag(ConnectionOrientedPdu.PFC_MAYBE)) {
            return;
        }
        ConnectionOrientedPdu reply = receive();
        if (reply instanceof ResponseCoPdu) {
            ndr.setFormat(reply.getFormat());

            buffer = new NdrBuffer(((ResponseCoPdu) reply).getStub(), 0);

            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "{0}", Hexdump.toHexString(buffer.buf));
            }

            ndrobj.decode(ndr, buffer);

        } else if (reply instanceof FaultCoPdu) {
            FaultCoPdu fault = (FaultCoPdu) reply;
            throw new FaultException("Received fault.", fault.getStatus(),
                    fault.getStub());
        } else if (reply instanceof ShutdownPdu) {
            throw new RpcException("Received shutdown request from server.");
        } else {
            throw new RpcException("Received unexpected PDU from server.");
        }
    }

    @Override
    public void detach() throws IOException {
        bound = false;
        context = null;
        getTransport().close();
    }

    protected void rebind() throws IOException {
        bound = false;
        bind();
    }

    protected void bind() throws IOException {
        if (bound) {
            return;
        }
        if (context != null) {
            bound = true;
            try {
                Integer cid = uuidsVsContextIds.get(getSyntax().toString().toUpperCase());
                ConnectionOrientedPdu pdu = context.alter(
                        new PresentationContext(cid == null ? ++contextIdCounter : cid, getSyntax()));
                boolean sendAlter = false;
                if (cid == null) {
                    uuidsVsContextIds.put(getSyntax().toString().toUpperCase(), contextIdCounter);
                    contextIdToUse = contextIdCounter;
                    sendAlter = true;
                } else {
                    contextIdToUse = cid;
                }

                if (sendAlter) {
                    if (pdu != null) {
                        send(pdu);
                    }
                    while (!context.isEstablished()) {
                        ConnectionOrientedPdu recieved = receive();
                        if ((pdu = context.accept(recieved)) != null) {
                            switch (pdu.getType()) {
                                case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                                    if (((BindAcknowledgePdu) pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION) {
                                        currentIID = ((BindPdu) recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                                    }
                                    break;
                                case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                                    //we need to record the iid now if this is successful and subsequent calls will now be for this iid.
                                    if (((AlterContextResponsePdu) pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION) {
                                        currentIID = ((AlterContextPdu) recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                                    }
                                    break;
                                default:
                                //nothing
                            }
                            send(pdu);
                        }
                    }
                }
            } catch (IOException | RuntimeException ex) {
                bound = false;
                throw ex;
            }
        } else {
            connect();
        }
    }

    protected void send(ConnectionOrientedPdu request) throws IOException {
        bind();
        context.getConnection().transmit(request, getTransport());
    }

    protected ConnectionOrientedPdu receive() throws IOException {
        return context.getConnection().receive(getTransport());
    }

    private void connect() throws IOException {
        bound = true;
        contextIdCounter = 0;
        currentIID = null;
        try {
            uuidsVsContextIds.put(getSyntax().toString().toUpperCase(), contextIdCounter);
            context = createContext();
            ConnectionOrientedPdu pdu = context.init(
                    new PresentationContext(contextIdCounter, getSyntax()),
                    getTransport().getProperties());
            contextIdToUse = contextIdCounter;
            if (pdu != null) {
                send(pdu);
            }
            while (!context.isEstablished()) {
                ConnectionOrientedPdu recieved = receive();
                if ((pdu = context.accept(recieved)) != null) {
                    switch (pdu.getType()) {
                        case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                            if (((BindAcknowledgePdu) pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION) {
                                currentIID = ((BindPdu) recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                            }
                            break;
                        case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                            //we need to record the iid now if this is successful and subsequent calls will now be for this iid.
                            if (((AlterContextResponsePdu) pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION) {
                                currentIID = ((AlterContextPdu) recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                            }
                            break;
                        default:
                        //nothing
                    }
                    send(pdu);
                }
            }
        } catch (IOException | RuntimeException ex) {
            try {
                detach();
            } catch (IOException ignore) {
            }
            throw ex;
        }
    }

    protected ConnectionContext createContext() throws ProviderException {
        Properties properties = getTransport().getProperties();
        if (properties == null) {
            return new BasicConnectionContext();
        }
        final String connectionContext = properties.getProperty(CONNECTION_CONTEXT);
        if (connectionContext == null) {
            return new BasicConnectionContext();
        }
        try {
            return (ConnectionContext) Class.forName(connectionContext).newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new ProviderException(ex.getMessage());
        }
    }
}
