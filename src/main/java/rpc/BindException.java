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

import rpc.pdu.BindNoAcknowledgePdu;

public class BindException extends RpcException {

    public BindException() {
        super();
    }

    public BindException(String message) {
        super(message);
    }

    public BindException(String message, int rejectReason) {
        super(message(message, rejectReason));
    }

    private static String message(String message, int reason) {
        return (message != null) ? message + " (" + message(reason) + ")"
                : message(reason);
    }

    private static String message(int reason) {
        switch (reason) {
            case BindNoAcknowledgePdu.REASON_NOT_SPECIFIED:
                return "REASON_NOT_SPECIFIED";
            case BindNoAcknowledgePdu.TEMPORARY_CONGESTION:
                return "TEMPORARY_CONGESTION";
            case BindNoAcknowledgePdu.LOCAL_LIMIT_EXCEEDED:
                return "LOCAL_LIMIT_EXCEEDED";
            case BindNoAcknowledgePdu.CALLED_PADDR_UNKNOWN:
                return "CALLED_PADDR_UNKNOWN";
            case BindNoAcknowledgePdu.PROTOCOL_VERSION_NOT_SUPPORTED:
                return "PROTOCOL_VERSION_NOT_SUPPORTED";
            case BindNoAcknowledgePdu.DEFAULT_CONTEXT_NOT_SUPPORTED:
                return "DEFAULT_CONTEXT_NOT_SUPPORTED";
            case BindNoAcknowledgePdu.USER_DATA_NOT_READABLE:
                return "USER_DATA_NOT_READABLE";
            case BindNoAcknowledgePdu.NO_PSAP_AVAILABLE:
                return "NO_PSAP_AVAILABLE";
            default:
                return "unknown";
        }
    }

}
