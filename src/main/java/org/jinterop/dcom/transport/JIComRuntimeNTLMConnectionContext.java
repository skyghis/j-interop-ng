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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import rpc.ConnectionOrientedPdu;
import rpc.core.PresentationContext;
import rpc.core.PresentationResult;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindPdu;
import rpc.security.ntlm.NtlmConnectionContext;

/**
 * @exclude @since 1.0
 */
public final class JIComRuntimeNTLMConnectionContext extends NtlmConnectionContext {

    private static final String IID = "IID";
    private static final String IID2 = "IID2";

    private boolean established = false;
    private final List listOfInterfacesSupported = Collections.synchronizedList(new ArrayList());

    // this returns null, so that a recieve is performed first.
    @Override
    public ConnectionOrientedPdu init(PresentationContext context,
            Properties properties) throws IOException {
        super.init2(context, properties);
        listOfInterfacesSupported.add(properties.getProperty(IID).toUpperCase());
        listOfInterfacesSupported.add(properties.getProperty(IID2).toUpperCase() + ":0.0");
        updateListOfInterfacesSupported2((List) properties.get("LISTOFSUPPORTEDINTERFACES"));
        return null;
    }

    @Override
    public ConnectionOrientedPdu accept(ConnectionOrientedPdu pdu)
            throws IOException {
        ConnectionOrientedPdu reply = null;
        switch (pdu.getType()) {
            case BindPdu.BIND_TYPE:
                established = true;
                PresentationContext[] presentationContexts = ((BindPdu) pdu).getContextList();
                reply = new BindAcknowledgePdu();
                PresentationResult[] result = new PresentationResult[1];
            for (PresentationContext presentationContext : presentationContexts) {
                boolean contains = false;
                synchronized (listOfInterfacesSupported) {
                    contains = listOfInterfacesSupported.contains(presentationContext.abstractSyntax.toString().toUpperCase());
                }
                if (!contains) {
                    //create a fault PDU stating the syntax is not supported.
                    result[0] = new PresentationResult(PresentationResult.PROVIDER_REJECTION, PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED, new PresentationSyntax(UUID.NIL_UUID + ":0.0"));
                    ((BindAcknowledgePdu) reply).setResultList(result);
                    break;
                }
            }

                //all okay
                if (((BindAcknowledgePdu) reply).getResultList() == null) {
                    result[0] = new PresentationResult();//this will be acceptance.
                    ((BindAcknowledgePdu) reply).setAssociationGroupId(new Object().hashCode()); //TODO should I save this ?
                    ((BindAcknowledgePdu) reply).setResultList(result);
                }
                reply.setCallId(pdu.getCallId());

                //issue a challenge against the request info
                break;

            case AlterContextPdu.ALTER_CONTEXT_TYPE:
                established = true;

                presentationContexts = ((AlterContextPdu) pdu).getContextList();
                reply = new AlterContextResponsePdu();
                result = new PresentationResult[1];
            for (PresentationContext presentationContext : presentationContexts) {
                boolean contains = false;
                synchronized (listOfInterfacesSupported) {
                    contains = listOfInterfacesSupported.contains(presentationContext.abstractSyntax.toString().toUpperCase());
                }
                if (!contains) {
                    //create a fault PDU stating the syntax is not supported.
                    result[0] = new PresentationResult(PresentationResult.PROVIDER_REJECTION, PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED, new PresentationSyntax(UUID.NIL_UUID + ":0.0"));
                    ((AlterContextResponsePdu) reply).setResultList(result);
                    break;
                }
            }

                //all okay
                if (((AlterContextResponsePdu) reply).getResultList() == null) {
                    result[0] = new PresentationResult();//this will be acceptance.
                    ((AlterContextResponsePdu) reply).setAssociationGroupId(new Object().hashCode()); //TODO should I save this ?
                    ((AlterContextResponsePdu) reply).setResultList(result);
                }

                reply.setCallId(pdu.getCallId());

                //issue a challenge against the request info
                break;

            default:
                reply = super.accept(reply);
        }

        return reply;
    }

    @Override
    public boolean isEstablished() {
        return super.isEstablished() | established;
    }

    void updateListOfInterfacesSupported(List newList) {
        synchronized (listOfInterfacesSupported) {
            listOfInterfacesSupported.addAll(newList);
        }
    }

    void updateListOfInterfacesSupported2(List newList) {
        for (int i = 0; i < newList.size(); i++) {
            listOfInterfacesSupported.add(newList.get(i) + ":0.0");
        }
    }
}
