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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.ConnectionOrientedPdu;
import rpc.FaultCodes;
import rpc.Fragmentable;

public class FaultCoPdu extends ConnectionOrientedPdu implements FaultCodes,
        Fragmentable {

    public static final int FAULT_TYPE = 0x03;

    private byte[] stub;

    private int allocationHint = 0;

    private int contextId = 0;

    private int cancelCount = 0;

    private int status = UNSPECIFIED_REJECTION;

    public int getType() {
        return FAULT_TYPE;
    }

    public byte[] getStub() {
        return stub;
    }

    public void setStub(byte[] stub) {
        this.stub = stub;
    }

    public int getAllocationHint() {
        return allocationHint;
    }

    public void setAllocationHint(int allocationHint) {
        this.allocationHint = allocationHint;
    }

    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    protected void readPdu(NetworkDataRepresentation ndr) {
        readHeader(ndr);
        readBody(ndr);
        readStub(ndr);
    }

    protected void writePdu(NetworkDataRepresentation ndr) {
        writeHeader(ndr);
        writeBody(ndr);
        writeStub(ndr);
    }

    protected void readBody(NetworkDataRepresentation ndr) {
        setAllocationHint(ndr.readUnsignedLong());
        setContextId(ndr.readUnsignedShort());
        setCancelCount(ndr.readUnsignedSmall());
        setStatus((int) ndr.readUnsignedLong());
    }

    protected void writeBody(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedLong(getAllocationHint());
        ndr.writeUnsignedShort(getContextId());
        ndr.writeUnsignedSmall((short) getCancelCount());
        ndr.writeUnsignedLong(getStatus());
    }

    protected void readStub(NetworkDataRepresentation ndr) {
		NdrBuffer buf = ndr.getBuffer();
		buf.align(8);
        byte[] stub = null;
        int length = getFragmentLength() - buf.getIndex();
        if (length > 0) {
            stub = new byte[length];
			ndr.readOctetArray(stub, 0, length);
        }
        setStub(stub);
    }

    protected void writeStub(NetworkDataRepresentation ndr) {
		NdrBuffer buf = ndr.getBuffer();
		buf.align(8, (byte) 0);
        byte[] stub = getStub();
		if (stub != null) ndr.writeOctetArray(stub, 0, stub.length);
    }

    public Iterator fragment(int size) {
        byte[] stub = getStub();
        if (stub == null) {
            return Arrays.asList(new FaultCoPdu[] { this }).iterator();
        }
        int stubSize = size - 24;
        if (stub.length <= stubSize) {
            return Arrays.asList(new FaultCoPdu[] { this }).iterator();
        }
        return new FragmentIterator(stubSize);
    }

    public Fragmentable assemble(Iterator fragments) throws IOException {
        if (!fragments.hasNext()) {
            throw new IOException("No fragments available.");
        }
        try {
            FaultCoPdu pdu = (FaultCoPdu) fragments.next();
            byte[] stub = pdu.getStub();
            if (stub == null) stub = new byte[0];
            while (fragments.hasNext()) {
                FaultCoPdu fragment = (FaultCoPdu) fragments.next();
                byte[] fragmentStub = fragment.getStub();
                if (fragmentStub != null && fragmentStub.length > 0) {
                    byte[] tmp = new byte[stub.length + fragmentStub.length];
                    System.arraycopy(stub, 0, tmp, 0, stub.length);
                    System.arraycopy(fragmentStub, 0, tmp, stub.length,
                            fragmentStub.length);
                    stub = tmp;
                }
            }
            int length = stub.length;
            if (length > 0) {
                pdu.setStub(stub);
                pdu.setAllocationHint(length);
            } else {
                pdu.setStub(null);
                pdu.setAllocationHint(0);
            }
            pdu.setFlag(PFC_FIRST_FRAG, true);
            pdu.setFlag(PFC_LAST_FRAG, true);
            return pdu;
        } catch (Exception ex) {
            throw new IOException("Unable to assemble PDU fragments.");
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception ex) {
            throw new IllegalStateException();
        }
    }

    private class FragmentIterator implements Iterator {

        private int stubSize;

        private int index = 0;

        public FragmentIterator(int stubSize) {
            this.stubSize = stubSize;
        }

        public boolean hasNext() {
            return index < stub.length;
        }

        public Object next() {
            if (index >= stub.length) throw new NoSuchElementException();
            FaultCoPdu fragment = (FaultCoPdu) FaultCoPdu.this.clone();
            int allocation = stub.length - index;
            fragment.setAllocationHint(allocation);
            if (stubSize < allocation) allocation = stubSize;
            byte[] fragmentStub = new byte[allocation];
            System.arraycopy(stub, index, fragmentStub, 0, allocation);
            fragment.setStub(fragmentStub);
            int flags = getFlags() & ~(PFC_FIRST_FRAG | PFC_LAST_FRAG);
            if (index == 0) flags |= PFC_FIRST_FRAG;
            index += allocation;
            if (index >= stub.length) flags |= PFC_LAST_FRAG;
            fragment.setFlags(flags);
            return fragment;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
