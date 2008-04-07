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
