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
