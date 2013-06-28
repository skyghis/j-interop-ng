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
