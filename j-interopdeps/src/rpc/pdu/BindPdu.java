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

public class BindPdu extends ConnectionOrientedPdu {

    public static final int BIND_TYPE = 0x0b;

    private PresentationContext[] contextList;

    private int maxTransmitFragment = MUST_RECEIVE_FRAGMENT_SIZE;

    private int maxReceiveFragment = MUST_RECEIVE_FRAGMENT_SIZE;

    private int associationGroupId = 0;

    public void resetCallIdCounter()
    {
    	super.callIdCounter = 0;
    }

    public int getType() {
        return BIND_TYPE;
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
        ndr.writeUnsignedShort(getMaxTransmitFragment());
        ndr.writeUnsignedShort(getMaxReceiveFragment());
        ndr.writeUnsignedLong(getAssociationGroupId());
        PresentationContext[] contextList = getContextList();
        int count = contextList.length;
        ndr.writeUnsignedSmall((short) count);
        for (int i = 0; i < count; i++) {
            contextList[i].write(ndr);
        }
    }

}
