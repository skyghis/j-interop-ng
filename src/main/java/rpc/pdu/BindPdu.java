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

    public void resetCallIdCounter() {
        ConnectionOrientedPdu.callIdCounter = 0;
    }

    @Override
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

    @Override
    protected void readBody(NetworkDataRepresentation ndr) {
        setMaxTransmitFragment(ndr.readUnsignedShort());
        setMaxReceiveFragment(ndr.readUnsignedShort());
        setAssociationGroupId(ndr.readUnsignedLong());
        int count = ndr.readUnsignedSmall();
        PresentationContext[] contextList = new PresentationContext[count];
        for (int i = 0; i < count; i++) {
            contextList[i] = new PresentationContext();
            contextList[i].read(ndr);
        }
        setContextList(contextList);
    }

    @Override
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
