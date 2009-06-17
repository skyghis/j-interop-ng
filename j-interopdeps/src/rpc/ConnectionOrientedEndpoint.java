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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public static final String CONNECTION_CONTEXT = "rpc.connectionContext";

    protected ConnectionContext context;

    private Transport transport;

    private PresentationSyntax syntax;

    private boolean bound;

    private int callId;

    private int contextIdCounter = 0;

    private int contextIdToUse = contextIdCounter;

    private static final Logger logger = Logger.getLogger("org.jinterop");

    //This is so as to reuse the contextids for already exported contexts.
    private Map uuidsVsContextIds = new HashMap();

    public ConnectionOrientedEndpoint(Transport transport,
            PresentationSyntax syntax) {
        this.transport = transport;
        this.syntax = syntax;
    }

    public Transport getTransport() {
        return transport;
    }

    public PresentationSyntax getSyntax() {
        return syntax;
    }

    public void call(int semantics, UUID object, int opnum, NdrObject ndrobj) throws IOException {
        bind();
        RequestCoPdu request = new RequestCoPdu();
        request.setContextId(contextIdToUse);

        byte[] b = new byte[1024];
        NdrBuffer buffer = new NdrBuffer(b, 0);
        NetworkDataRepresentation ndr = new NetworkDataRepresentation();
        ndrobj.encode(ndr, buffer);
		byte[] stub = new byte[buffer.getLength()]; /* yuk */
		System.arraycopy(buffer.buf, 0, stub, 0, stub.length);

		if (logger.isLoggable(Level.FINEST))
		{
			//jcifs.util.Hexdump.hexdump(System.err, stub, 0, stub.length);
		   	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		   	jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), stub, 0, stub.length);
		   	logger.finest("\n" + byteArrayOutputStream.toString());
		}



        request.setStub(stub);
        request.setAllocationHint(buffer.getLength());
        request.setOpnum(opnum);
        request.setObject(object);
        if ((semantics & MAYBE) != 0) {
            request.setFlag(ConnectionOrientedPdu.PFC_MAYBE, true);
        }
        send(request);

//        if (semantics == 100)
//        try{
//        	Thread.sleep(100);
//        }catch(Exception e)
//        {
//
//        }

        if (request.getFlag(ConnectionOrientedPdu.PFC_MAYBE)) return;
        ConnectionOrientedPdu reply = receive();
        if (reply instanceof ResponseCoPdu) {
            ndr.setFormat(reply.getFormat());

            buffer = new NdrBuffer(((ResponseCoPdu) reply).getStub(), 0);

            if (logger.isLoggable(Level.FINEST))
    		{
            	//jcifs.util.Hexdump.hexdump(System.err, buffer.buf, 0, buffer.buf.length);
    		   	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    		   	jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), buffer.buf, 0, buffer.buf.length);
    		   	logger.finest("\n" + byteArrayOutputStream.toString());
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

    protected void rebind() throws IOException {
        bound = false;
        bind();
    }


    protected void bind() throws IOException {
        if (bound) return;
        if (context != null) {
            bound = true;
            try {
            	Integer cid = (Integer)uuidsVsContextIds.get(getSyntax().toString().toUpperCase());
            	ConnectionOrientedPdu pdu = context.alter(
                        new PresentationContext(cid == null ? ++contextIdCounter : cid.intValue(), getSyntax()));
            	boolean sendAlter = false;
                if (cid == null)
                {
                	uuidsVsContextIds.put(getSyntax().toString().toUpperCase(), new Integer(contextIdCounter));
                	contextIdToUse = contextIdCounter;
                	sendAlter = true;
                }
                else
                {
                	contextIdToUse = cid.intValue();
                }

                if (sendAlter)
                {
                	if (pdu != null) send(pdu);
	                while (!context.isEstablished()) {
	                	ConnectionOrientedPdu recieved = receive();
	                    if ((pdu = context.accept(recieved)) != null)
	                    {
	                    	switch(pdu.getType())
	                    	{
	                    		case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
	                    			if (((BindAcknowledgePdu)pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION)
	                    			{
	                    				currentIID = ((BindPdu)recieved).getContextList()[0].abstractSyntax.getUuid().toString();
	                    			}
	                    			break;
	                    		case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
	                    			//we need to record the iid now if this is successful and subsequent calls will now be for this iid.
	                    			if (((AlterContextResponsePdu)pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION)
	                    			{
	                    				currentIID = ((AlterContextPdu)recieved).getContextList()[0].abstractSyntax.getUuid().toString();
	                    			}
	                    			break;
	                    		default:
	                    			//nothing
	                    	}
	                    	send(pdu);
	                    }
	                }
                }
            } catch (IOException ex) {
                bound = false;
                throw ex;
            } catch (RuntimeException ex) {
                bound = false;
                throw ex;
            } catch (Exception ex) {
                bound = false;
                throw new IOException(ex.getMessage());
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

    public void detach() throws IOException {
        bound = false;
        context = null;
        getTransport().close();
    }

    protected String currentIID = null;

    private void connect() throws IOException {
        bound = true;
        contextIdCounter = 0;
        currentIID = null;
        try {
        	uuidsVsContextIds.put(getSyntax().toString().toUpperCase(), new Integer(contextIdCounter));
            context = createContext();
            ConnectionOrientedPdu pdu = context.init(
                    new PresentationContext(contextIdCounter, getSyntax()),
                            getTransport().getProperties());
            contextIdToUse = contextIdCounter;
            if (pdu != null) send(pdu);
            while (!context.isEstablished()) {
            	ConnectionOrientedPdu recieved = receive();
                if ((pdu = context.accept(recieved)) != null)
                {
                	switch(pdu.getType())
                	{
                		case BindAcknowledgePdu.BIND_ACKNOWLEDGE_TYPE:
                			if (((BindAcknowledgePdu)pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION)
                			{
                				currentIID = ((BindPdu)recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                			}
                			break;
                		case AlterContextResponsePdu.ALTER_CONTEXT_RESPONSE_TYPE:
                			//we need to record the iid now if this is successful and subsequent calls will now be for this iid.
                			if (((AlterContextResponsePdu)pdu).getResultList()[0].reason != PresentationResult.PROVIDER_REJECTION)
                			{
                				currentIID = ((AlterContextPdu)recieved).getContextList()[0].abstractSyntax.getUuid().toString();
                			}
                			break;
                		default:
                			//nothing
                	}
                	send(pdu);
                }
            }
        } catch (IOException ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw ex;
        } catch (RuntimeException ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw ex;
        } catch (Exception ex) {
            try {
                detach();
            } catch (IOException ignore) { }
            throw new IOException(ex.getMessage());
        }
    }

    protected ConnectionContext createContext() throws ProviderException {
        Properties properties = getTransport().getProperties();
        if (properties == null) return new BasicConnectionContext();
        String context = properties.getProperty(CONNECTION_CONTEXT);
        if (context == null) return new BasicConnectionContext();
        try {
            return (ConnectionContext) Class.forName(context).newInstance();
        } catch (Exception ex) {
            throw new ProviderException(ex.getMessage());
        }
    }

}
