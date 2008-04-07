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

package rpc.pdu;

import ndr.NetworkDataRepresentation;
import rpc.ConnectionOrientedPdu;
import rpc.core.ProtocolVersion;

public class BindNoAcknowledgePdu extends ConnectionOrientedPdu {

    public static final int BIND_NO_ACKNOWLEDGE_TYPE = 0x0d;

    public static final int REASON_NOT_SPECIFIED = 0;

    public static final int TEMPORARY_CONGESTION = 1;

    public static final int LOCAL_LIMIT_EXCEEDED = 2;

    public static final int CALLED_PADDR_UNKNOWN = 3; // not used

    public static final int PROTOCOL_VERSION_NOT_SUPPORTED = 4;

    public static final int DEFAULT_CONTEXT_NOT_SUPPORTED = 5; // not used

    public static final int USER_DATA_NOT_READABLE = 6; // not used

    public static final int NO_PSAP_AVAILABLE = 7; // not used

    private ProtocolVersion[] versionList;

    private int rejectReason = REASON_NOT_SPECIFIED;

    public int getType() {
        return BIND_NO_ACKNOWLEDGE_TYPE;
    }

    public int getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(int rejectReason) {
        this.rejectReason = rejectReason;
    }

    public ProtocolVersion[] getVersionList() {
        return versionList;
    }

    public void setVersionList(ProtocolVersion[] versionList) {
        this.versionList = versionList;
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        int reason = ndr.readUnsignedSmall();
        setRejectReason(reason);
        ProtocolVersion[] versionList = null;
        if (reason == PROTOCOL_VERSION_NOT_SUPPORTED) {
            int count = ndr.readUnsignedSmall();
            versionList = new ProtocolVersion[count];
            for (int i = 0; i < count; i++) {
                versionList[i] = new ProtocolVersion();
                versionList[i].read(ndr);
            }
        }
        setVersionList(versionList);
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        int reason = getRejectReason();
        ndr.writeUnsignedSmall((short) reason);
        if (reason != PROTOCOL_VERSION_NOT_SUPPORTED) return;
        ProtocolVersion[] versionList = getVersionList();
        int count = (versionList != null) ? versionList.length : 0;
        for (int i = 0; i < count; i++) {
            versionList[i].write(ndr);
        }
    }

}
