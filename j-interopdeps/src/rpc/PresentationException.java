/**
* Donated by Jarapac (http://jarapac.sourceforge.net/) and released under EPL.
* 
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
* Vikram Roopchand  - Moving to EPL from LGPL v1.
*  
*/



package rpc;

import rpc.core.PresentationResult;

public class PresentationException extends BindException {

    /**
	 *
	 */
	private static final long serialVersionUID = 5421952951585948361L;

	public PresentationException() {
        super();
    }

    public PresentationException(String message) {
        super(message);
    }

    public PresentationException(String message, PresentationResult result) {
        super(message(message, result));
    }

    private static String message(String message, PresentationResult result) {
        if (result == null) return message;
        return (message != null) ? message + " (" + message(result) + ")" :
                message(result);
    }

    private static String message(PresentationResult result) {
        StringBuffer message = new StringBuffer();
        switch (result.result) {
        case PresentationResult.ACCEPTANCE:
            message.append("ACCEPTANCE");
            break;
        case PresentationResult.USER_REJECTION:
            message.append("USER_REJECTION");
            break;
        case PresentationResult.PROVIDER_REJECTION:
            message.append("PROVIDER_REJECTION");
            break;
        default:
            message.append("unknown");
        }
        message.append("; ");
        switch (result.reason) {
        case PresentationResult.REASON_NOT_SPECIFIED:
            message.append("REASON_NOT_SPECIFIED");
            break;
        case PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED:
            message.append("ABSTRACT_SYNTAX_NOT_SUPPORTED");
            break;
        case PresentationResult.PROPOSED_TRANSFER_SYNTAXES_NOT_SUPPORTED:
            message.append("PROPOSED_TRANSFER_SYNTAXES_NOT_SUPPORTED");
            break;
        case PresentationResult.LOCAL_LIMIT_EXCEEDED:
            message.append("LOCAL_LIMIT_EXCEEDED");
            break;
        default:
            message.append("unknown");
        }
        return message.toString();
    }

}
