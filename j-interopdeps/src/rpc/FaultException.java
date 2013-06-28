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

public class FaultException extends RpcException implements FaultCodes {

    private final byte[] stub;
    public int status = -1;
    public FaultException() {
        super();
        stub = null;
    }

    public FaultException(String message) {
        super(message);
        stub = null;
    }

    public FaultException(String message, int status) {
    	super(message(message, status));
    	this.status = status;
        stub = null;
    }

    public FaultException(String message, int status, byte[] stub) {
        super(message(message, status));
        this.status = status;
        this.stub = stub;
    }

    public byte[] getStub() {
        return stub;
    }

    private static String message(String message, int status) {
        return (message != null) ? message + " (" + message(status) + ")" :
                message(status);
    }

    private static String message(int status) {
        switch (status) {
        case RPC_VERSION_MISMATCH:
            return "RPC_VERSION_MISMATCH";
        case UNSPECIFIED_REJECTION:
            return "UNSPECIFIED_REJECTION";
        case BAD_ACTIVITY_ID:
            return "BAD_ACTIVITY_ID";
        case WHO_ARE_YOU_FAILED:
            return "WHO_ARE_YOU_FAILED";
        case MANAGER_NOT_ENTERED:
            return "MANAGER_NOT_ENTERED";
        case OPERATION_RANGE_ERROR:
            return "OPERATION_RANGE_ERROR";
        case UNKNOWN_INTERFACE:
            return "UNKNOWN_INTERFACE";
        case WRONG_BOOT_TIME:
            return "WRONG_BOOT_TIME";
        case YOU_CRASHED:
            return "YOU_CRASHED";
        case PROTOCOL_ERROR:
            return "PROTOCOL_ERROR";
        case OUTPUT_ARGUMENTS_TOO_BIG:
            return "OUTPUT_ARGUMENTS_TOO_BIG";
        case SERVER_TOO_BUSY:
            return "SERVER_TOO_BUSY";
        case UNSUPPORTED_TYPE:
            return "UNSUPPORTED_TYPE";
        case INVALID_PRESENTATION_CONTEXT_ID:
            return "INVALID_PRESENTATION_CONTEXT_ID";
        case UNSUPPORTED_AUTHENTICATION_LEVEL:
            return "UNSUPPORTED_AUTHENTICATION_LEVEL";
        case INVALID_CHECKSUM:
            return "INVALID_CHECKSUM";
        case INVALID_CRC:
            return "INVALID_CRC";
        default:
            return "unknown";
        }
    }

}
