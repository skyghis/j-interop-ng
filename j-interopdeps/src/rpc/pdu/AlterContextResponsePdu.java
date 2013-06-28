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
import rpc.core.Port;
import rpc.core.PresentationResult;

public class AlterContextResponsePdu extends ConnectionOrientedPdu {

    public static final int ALTER_CONTEXT_RESPONSE_TYPE = 0x0f;

    private PresentationResult[] resultList;

    private int maxTransmitFragment = -1;

    private int maxReceiveFragment = -1;

    private int associationGroupId = 0;

    private Port secondaryAddress;

    public int getType() {
        return ALTER_CONTEXT_RESPONSE_TYPE;
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

    public Port getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setSecondaryAddress(Port secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public PresentationResult[] getResultList() {
        return resultList;
    }

    public void setResultList(PresentationResult[] resultList) {
        this.resultList = resultList;
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        setMaxTransmitFragment(ndr.readUnsignedShort());
        setMaxReceiveFragment(ndr.readUnsignedShort());
        setAssociationGroupId((int) ndr.readUnsignedLong());
        Port secondaryAddress = new Port();
        secondaryAddress.read(ndr);
        setSecondaryAddress(secondaryAddress);
        ndr.getBuffer().align(4);
        int count = ndr.readUnsignedSmall();
        PresentationResult[] resultList = new PresentationResult[count];
        for (int i = 0; i < count; i++) {
            resultList[i] = new PresentationResult();
            resultList[i].read(ndr);
        }
        setResultList(resultList);
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        int maxTransmitFragment = getMaxTransmitFragment();
        int maxReceiveFragment = getMaxReceiveFragment();
        ndr.writeUnsignedShort((maxTransmitFragment == -1) ?
                ndr.getBuffer().getCapacity() : maxTransmitFragment);
        ndr.writeUnsignedShort((maxReceiveFragment == -1) ?
                ndr.getBuffer().getCapacity() : maxReceiveFragment);
        ndr.writeUnsignedLong(getAssociationGroupId());
        Port secondaryAddress = getSecondaryAddress();
        if (secondaryAddress == null) secondaryAddress = new Port();
        secondaryAddress.write(ndr);
        ndr.getBuffer().align(4);
        PresentationResult[] resultList = getResultList();
        int count = resultList.length;
        ndr.writeUnsignedSmall((short) count);
        for (int i = 0; i < count; i++) {
            resultList[i].write(ndr);
        }
    }

}
