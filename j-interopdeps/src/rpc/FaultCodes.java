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

public interface FaultCodes {

    /**
     * Fault status indicating the server does not support the RPC protocol
     * version specified in the request.
     */
    public static final int RPC_VERSION_MISMATCH = 0x1c000008;

    /**
     * Fault status indicating the server is rejecting the request for
     * unspecified reasons.
     */
    public static final int UNSPECIFIED_REJECTION = 0x1c000009;

    /**
     * Connectionless fault status indicating the server has no state
     * corresponding to the specified activity identifier.
     */
    public static final int BAD_ACTIVITY_ID = 0x1c00000a;

    /**
     * Connectionless fault status indicating the conversation manager
     * callback failed.
     */
    public static final int WHO_ARE_YOU_FAILED = 0x1c00000b;

    /**
     * Fault status indicating the server manager routine has not been
     * entered and executed.
     */
    public static final int MANAGER_NOT_ENTERED = 0x1c00000c;

    /**
     * Fault status indicating the requested operation number is out of
     * range.
     */
    public static final int OPERATION_RANGE_ERROR = 0x1c010002;

    /**
     * Fault status indicating the server does not export the interface
     * requested by the client.
     */
    public static final int UNKNOWN_INTERFACE = 0x1c010003;

    /**
     * Connectionless fault status indicating the specified boot time does
     * not match the actual server boot time.
     */
    public static final int WRONG_BOOT_TIME = 0x1c010006;

    /**
     * Connectionless fault status indicating a restarted server called
     * back a client.
     */
    public static final int YOU_CRASHED = 0x1c010009;

    /**
     * Fault status indicating a protocol violation.
     */
    public static final int PROTOCOL_ERROR = 0x1c01000b;

    /**
     * Fault status indicating the operation's output parameters are larger
     * than their declared maximum size.
     */
    public static final int OUTPUT_ARGUMENTS_TOO_BIG = 0x1c010013;

    /**
     * Fault status indicating the server is currently too busy to service
     * the request.
     */
    public static final int SERVER_TOO_BUSY = 0x1c010014;

    /**
     * Fault status indicating the server does not implement the requested
     * operation for the requested object's type.
     */
    public static final int UNSUPPORTED_TYPE = 0x1c010017;

    /**
     * Connection-oriented fault status indicating the requested presentation
     * context ID is invalid.
     */
    public static final int INVALID_PRESENTATION_CONTEXT_ID = 0x1c00001c;

    /**
     * Fault status indicating the server does not support the authentication
     * level requested.
     */
    public static final int UNSUPPORTED_AUTHENTICATION_LEVEL = 0x1c00001d;

    /**
     * Fault status indicating an invalid checksum.
     */
    public static final int INVALID_CHECKSUM = 0x1c00001f;

    /**
     * Fault status indicating an invalid CRC.
     */
    public static final int INVALID_CRC = 0x1c000020;

}
