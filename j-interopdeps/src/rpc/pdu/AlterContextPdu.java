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
import rpc.core.PresentationContext;

public class AlterContextPdu extends ConnectionOrientedPdu {

    public static final int ALTER_CONTEXT_TYPE = 0x0e;

    private PresentationContext[] contextList;

    private int maxTransmitFragment = -1;

    private int maxReceiveFragment = -1;

    private int associationGroupId = 0;

    public int getType() {
        return ALTER_CONTEXT_TYPE;
    }

    public int getMaxTransmitFragment() {
        return maxTransmitFragment;
    }

    public void setMaxTransmitFragment(int maxTransmitFragment) {
        this.maxTransmitFragment = maxTransmitFragment;
    }

    public int getMaxReceiveFragment() {
        return maxReceiveFragment;
    }

    public void setMaxReceiveFragment(int maxReceiveFragment) {
        this.maxReceiveFragment = maxReceiveFragment;
    }

    public int getAssociationGroupId() {
        return associationGroupId;
    }

    public void setAssociationGroupId(int associationGroupId) {
        this.associationGroupId = associationGroupId;
    }

    public PresentationContext[] getContextList() {
        return contextList;
    }

    public void setContextList(PresentationContext[] contextList) {
        this.contextList = contextList;
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        setMaxTransmitFragment(ndr.readUnsignedShort());
        setMaxReceiveFragment(ndr.readUnsignedShort());
        setAssociationGroupId((int) ndr.readUnsignedLong());
        int count = ndr.readUnsignedSmall();
        PresentationContext[] contextList = new PresentationContext[count];
        for (int i = 0; i < count; i++) {
            contextList[i] = new PresentationContext();
            contextList[i].read(ndr);
        }
        setContextList(contextList);
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        int maxTransmitFragment = getMaxTransmitFragment();
        int maxReceiveFragment = getMaxReceiveFragment();
        ndr.writeUnsignedShort((maxTransmitFragment == -1) ?
                ndr.getBuffer().getCapacity() : maxTransmitFragment);
        ndr.writeUnsignedShort((maxReceiveFragment == -1) ?
                ndr.getBuffer().getCapacity() : maxReceiveFragment);
        ndr.writeUnsignedLong(getAssociationGroupId());
        PresentationContext[] contextList = getContextList();
        int count = contextList.length;
        ndr.writeUnsignedSmall((short) count);
        for (int i = 0; i < count; i++) {
            contextList[i].write(ndr);
        }
    }

}
