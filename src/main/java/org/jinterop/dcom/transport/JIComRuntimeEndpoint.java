/** j-Interop (Pure Java implementation of DCOM protocol)
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
package org.jinterop.dcom.transport;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.util.Hexdump;
import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import org.jinterop.dcom.common.IJICOMRuntimeWorker;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIRuntimeException;
import rpc.ConnectionOrientedEndpoint;
import rpc.ConnectionOrientedPdu;
import rpc.FaultException;
import rpc.RpcException;
import rpc.Transport;
import rpc.core.PresentationContext;
import rpc.core.PresentationResult;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.Auth3Pdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.pdu.FaultCoPdu;
import rpc.pdu.RequestCoPdu;
import rpc.pdu.ResponseCoPdu;
import rpc.pdu.ShutdownPdu;

/**
 * @exclude @since 1.0
 */
public final class JIComRuntimeEndpoint extends ConnectionOrientedEndpoint {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");

    JIComRuntimeEndpoint(Transport transport, PresentationSyntax syntax) {
        super(transport, syntax);
    }

    @Override
    public void call(int semantics, UUID object, int opnum, NdrObject ndrobj) throws IOException {
        throw new JIRuntimeException(JIErrorCodes.JI_ILLEGAL_CALL);
    }

    //use this oxidObject, it is actually OxidResolverImpl extends NdrObject.
    public void processRequests(IJICOMRuntimeWorker workerObject, String baseIID, List<String> listOfSupportedInterfaces) throws IOException {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "processRequests: [JIComRuntimeEndPoint] started new thread {0}", Thread.currentThread().getName());
        }
        //this iid is the component IID just in case.
        if (baseIID != null) {
            getTransport().getProperties().setProperty("IID2", baseIID);
        }

        getTransport().getProperties().put("LISTOFSUPPORTEDINTERFACES", listOfSupportedInterfaces);
        bind();// will bind to the server and perform the initial bind\bind ack.

        while (true) {
            // first recieve and then answer
            ConnectionOrientedPdu response = null;
            ConnectionOrientedPdu request = receive();

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "processRequests: [JIComRuntimeEndPoint] request : {0} , {1} workerObject is resolver: {2}", new Object[]{Thread.currentThread().getName(), request, workerObject.isResolver()});
            }
            NetworkDataRepresentation ndr = new NetworkDataRepresentation();
            workerObject.setCurrentIID(currentIID);
            if (request instanceof RequestCoPdu) {
                NdrBuffer buffer = new NdrBuffer(((RequestCoPdu) request).getStub(), 0);
                if (buffer.buf != null) {
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.log(Level.FINEST, Hexdump.toHexString(buffer.buf));
                    }
                    // System.err.println("Vikram: " + Long.toString(Thread.currentThread().getId()));
                    // jcifs.util.Hexdump.hexdump(System.err, buffer.buf, 0, buffer.buf.length);
                }
                ndr.setFormat(request.getFormat());
                workerObject.setOpnum(request.getOpnum());
                //sets the current object, this is used to identify the JILocalCoClass to work on.
                //for most cases this will be null , till there is an actual COM interface request.
                workerObject.setCurrentObjectID(((RequestCoPdu) request).getObject());

                try {

                    ((NdrObject) workerObject).decode(ndr, buffer);
                    ResponseCoPdu responseCoPdu = new ResponseCoPdu();
                    responseCoPdu.setContextId(((RequestCoPdu) request).getContextId());
                    responseCoPdu.setFormat(request.getFormat());
                    responseCoPdu.setCallId(request.getCallId());
                    ((NdrObject) workerObject).encode(ndr, null);
                    int length = ndr.getBuffer().length > ndr.getBuffer().index ? ndr.getBuffer().length : ndr.getBuffer().index;
                    // length = length + 4;
                    responseCoPdu.setAllocationHint(length + 4);
                    byte[] responsebytes = new byte[length + 4];
                    System.arraycopy(ndr.getBuffer().getBuffer(), 0, responsebytes, 0, responsebytes.length - 4);
                    responseCoPdu.setStub(responsebytes);
                    // responseCoPdu.setStub(ndr.getBuffer().getBuffer());
                    response = responseCoPdu;

                } catch (JIRuntimeException e) {
                    LOGGER.throwing("JIComRuntimeEndpoint", "processRequests", e);
                    //create a fault PDU
                    response = new FaultCoPdu();
                    response.setCallId(request.getCallId());
                    ((FaultCoPdu) response).setStatus(e.getHResult());
                }
            } else if (request instanceof BindPdu || request instanceof AlterContextPdu) {

                if (!workerObject.isResolver()) {
                    //this list will be clear after this call.
                    /* Basically the cycle expected is like this...first a bind call comes, then a RemQI, that populates the
                     * list internally (Remunknownobject), then an alter context comes for the QIed interface, this clears the set
                     * object (if any) , then a normal request comes through.
                     *
                     */
                    //this call is only valid when the workerObject is RemUnknownObject.
                    //so the context us NTLMConnectionContext
                    if (context instanceof JIComRuntimeNTLMConnectionContext) {
                        ((JIComRuntimeNTLMConnectionContext) context).updateListOfInterfacesSupported(workerObject.getQIedIIDs());
                    }

                    switch (request.getType()) {
                        case BindPdu.BIND_TYPE:
                            currentIID = ((BindPdu) request).getContextList()[0].abstractSyntax.getUuid().toString();
                            break;
                        case AlterContextPdu.ALTER_CONTEXT_TYPE:
                            //we need to record the iid now if this is successful and subsequent calls will now be for this iid.
                            currentIID = ((AlterContextPdu) request).getContextList()[0].abstractSyntax.getUuid().toString();
                            break;
                        default:
                        //nothing
                    }
                }

                response = context.accept(request);

                if (!workerObject.isResolver()) {
                    PresentationResult[] result = null;
                    PresentationContext context = null;
                    boolean successful = false;
                    if (response instanceof BindAcknowledgePdu) {
                        result = ((BindAcknowledgePdu) response).getResultList();
                        successful = result[0].result == PresentationResult.ACCEPTANCE;
                        context = ((BindPdu) request).getContextList()[0]; //am expecting only one
                    } else {
                        result = ((AlterContextResponsePdu) response).getResultList();
                        successful = result[0].result == PresentationResult.ACCEPTANCE;
                        context = ((AlterContextPdu) request).getContextList()[0]; //am expecting only one
                    }
                    //  if (successful)
                    //  {
                    //    //now select the Interface from the request and set that as the object expected to come.
                    //    workerObject.setCurrentJavaInstanceFromIID(context.abstractSyntax.toString().toUpperCase());
                    //    //set the component null;
                    //  }
                }
            } else if (request instanceof FaultCoPdu) {
                // TODO to throw or not to throw ...that is the question :)...i think it should be logged , but not thrown
                // otherwise this thread will be terminated and further access will be blocked for the com server.
                // TODO write logging code here and comment this code.
                FaultCoPdu fault = (FaultCoPdu) request;
                throw new FaultException("Received fault.", fault.getStatus(),
                        fault.getStub());
            } else if (request instanceof ShutdownPdu) {
                throw new RpcException("Received shutdown request from server.");
            } else if (request instanceof Auth3Pdu) {
                //try {
                //  Thread.sleep(1000);
                //} catch (InterruptedException e) {
                //  // TODO Auto-generated catch block
                //  e.printStackTrace();
                //}
                continue; //don't do anything here, the server will send another request
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "processRequests: [JIComRuntimeEndPoint] response : {0} , {1}", new Object[]{Thread.currentThread().getName(), response});
            }
            //now send the response.
            send(response);

            if (workerObject.workerOver()) {
                LOGGER.log(Level.INFO, "processRequests: [JIComRuntimeEndPoint] Worker is over, all IPID references have been released. Thread {0} will now exit.", Thread.currentThread().getName());
                break;
            }
        }

    }
}
