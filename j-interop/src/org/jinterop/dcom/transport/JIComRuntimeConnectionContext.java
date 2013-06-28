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

package org.jinterop.dcom.transport;

import java.io.IOException;
import java.util.Properties;

import rpc.BasicConnectionContext;
import rpc.ConnectionOrientedPdu;
import rpc.core.PresentationContext;
import rpc.core.PresentationResult;
import rpc.core.PresentationSyntax;
import rpc.core.UUID;
import rpc.pdu.AlterContextPdu;
import rpc.pdu.AlterContextResponsePdu;
import rpc.pdu.BindAcknowledgePdu;
import rpc.pdu.BindPdu;

/**
 * @exclude
 * @since 1.0
 *
 */
public final class JIComRuntimeConnectionContext extends BasicConnectionContext {

	  private static final String IID = "IID";


	  private boolean established = false;
	  private Properties properties = null;
	  // this returns null, so that a recieve is performed first.
	  public ConnectionOrientedPdu init(PresentationContext context,
	            Properties properties) throws IOException {
	      super.init(context,properties);
	      this.properties = properties;
	      return null;
	   }

	  public ConnectionOrientedPdu accept(ConnectionOrientedPdu pdu)
      throws IOException {
		  ConnectionOrientedPdu reply = null;
		  switch (pdu.getType()) {
		  	case BindPdu.BIND_TYPE:
		  		established = true;
		  		PresentationContext[] presentationContexts = ((BindPdu)pdu).getContextList();
		  		reply = new BindAcknowledgePdu();
  				PresentationResult[] result = new PresentationResult[1];
		  		for (int i = 0; i < presentationContexts.length;i++)
		  		{
		  			PresentationContext presentationContext = presentationContexts[i];
		  			if (!presentationContext.abstractSyntax.toString().toUpperCase().equalsIgnoreCase(properties.getProperty(IID)))
		  			{
		  				//create a fault PDU stating the syntax is not supported.
		  				result[0] = new PresentationResult(PresentationResult.PROVIDER_REJECTION,PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED,new PresentationSyntax(UUID.NIL_UUID + ":0.0"));
		  				((BindAcknowledgePdu)reply).setResultList(result);
		  				break;
		  			}
		  		}

		  		//all okay
		  		if (((BindAcknowledgePdu)reply).getResultList() == null)
		  		{
		  			result[0] = new PresentationResult();//this will be acceptance.
		  			((BindAcknowledgePdu)reply).setAssociationGroupId(new Object().hashCode()); //TODO should I save this ?
		  			((BindAcknowledgePdu)reply).setResultList(result);
		  		}
		  		((BindAcknowledgePdu)reply).setCallId(pdu.getCallId());
		  		break;
		  	case AlterContextPdu.ALTER_CONTEXT_TYPE:
		  		established = true;

		  		presentationContexts = ((AlterContextPdu)pdu).getContextList();
		  		reply = new AlterContextResponsePdu();
  				result = new PresentationResult[1];
		  		for (int i = 0; i < presentationContexts.length;i++)
		  		{
		  			PresentationContext presentationContext = presentationContexts[i];
		  			if (!presentationContext.abstractSyntax.toString().toUpperCase().equalsIgnoreCase(properties.getProperty(IID)))
		  			{
		  				//create a fault PDU stating the syntax is not supported.
		  				result[0] = new PresentationResult(PresentationResult.PROVIDER_REJECTION,PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED,new PresentationSyntax(UUID.NIL_UUID + ":0.0"));
		  				((AlterContextResponsePdu)reply).setResultList(result);
		  				break;
		  			}
		  		}

		  		//all okay
		  		if (((AlterContextResponsePdu)reply).getResultList() == null)
		  		{
		  			result[0] = new PresentationResult();//this will be acceptance.
		  			((AlterContextResponsePdu)reply).setAssociationGroupId(new Object().hashCode()); //TODO should I save this ?
		  			((AlterContextResponsePdu)reply).setResultList(result);
		  		}

		  		((AlterContextResponsePdu)reply).setCallId(pdu.getCallId());

		  	break;
		  	default:
		  		reply = super.accept(reply);
		  }

		  return reply;
	  }

	  public boolean isEstablished() {
	        return super.isEstablished() | established;
	  }



}
