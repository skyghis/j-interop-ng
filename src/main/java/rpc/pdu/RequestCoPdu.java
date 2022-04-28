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
import java.util.logging.Level;
import java.util.logging.Logger;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.ConnectionOrientedPdu;
import rpc.Fragmentable;
import rpc.core.UUID;

public class RequestCoPdu extends ConnectionOrientedPdu implements Fragmentable {

    private static final Logger logger = Logger.getLogger("org.jinterop");
    public static final int REQUEST_TYPE = 0x00;
    private byte[] stub;
    private int allocationHint = 0;
    private int contextId = 0;
    private int opnum = 0;
    private UUID object;

    @Override
    public int getType() {
        return REQUEST_TYPE;
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

    @Override
    public int getOpnum() {
        return opnum;
    }

    public void setOpnum(int opnum) {
        this.opnum = opnum;
    }

    public UUID getObject() {
        return object;
    }

    public void setObject(UUID object) {
        this.object = object;
        setFlag(PFC_OBJECT_UUID, object != null);
    }

    @Override
    protected void readPdu(NetworkDataRepresentation ndr) {
        readHeader(ndr);
        readBody(ndr);
        readStub(ndr);
    }

    @Override
    protected void writePdu(NetworkDataRepresentation ndr) {
        writeHeader(ndr);
        writeBody(ndr);
        writeStub(ndr);
    }

    @Override
    protected void readBody(NetworkDataRepresentation ndr) {
        UUID uuid = null;
        NdrBuffer src = ndr.getBuffer();
        setAllocationHint(src.dec_ndr_long());
        setContextId(src.dec_ndr_short());
        setOpnum(src.dec_ndr_short());
        if (getFlag(PFC_OBJECT_UUID)) {
            uuid = new UUID(src);
        }
        setObject(uuid);
    }

    @Override
    protected void writeBody(NetworkDataRepresentation ndr) {
        NdrBuffer dst = ndr.getBuffer();
        dst.enc_ndr_long(getAllocationHint());
        dst.enc_ndr_short(getContextId());
        dst.enc_ndr_short(getOpnum());
        if (getFlag(PFC_OBJECT_UUID)) {
            getObject().encode(ndr.getBuffer());
        }
    }

    protected void readStub(NetworkDataRepresentation ndr) {
        NdrBuffer src = ndr.getBuffer();
        src.align(8);
        byte[] fragmentStub = null;
        int length = getFragmentLength() - src.getIndex();
        if (length > 0) {
            fragmentStub = new byte[length];
            ndr.readOctetArray(fragmentStub, 0, length);
        }
        setStub(fragmentStub);
    }

    protected void writeStub(NetworkDataRepresentation ndr) {
        NdrBuffer dst = ndr.getBuffer();
        dst.align(8, (byte) 0);
        if (stub != null) {
            ndr.writeOctetArray(stub, 0, stub.length);
        }
    }

    @Override
    public Iterator fragment(int size) {
        if (stub == null) {
            return Arrays.asList(new RequestCoPdu[]{this}).iterator();
        }

        //subtracting 8 bytes for authentication header and 16 for the authentication verifier size, someone forgot the
        //poor guys..
        int stubSize = size - (getFlag(PFC_OBJECT_UUID) ? 40 : 24) - 8 - 16;
        if (stub.length <= stubSize) {
            return Arrays.asList(new RequestCoPdu[]{this}).iterator();
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("In fragment of RequestCoPdu, this packet will be fragmented while sending...\n");
        }
        return new FragmentIterator(stubSize);
    }

    @Override
    public Fragmentable assemble(Iterator fragments) throws IOException {
        if (!fragments.hasNext()) {
            throw new IOException("No fragments available.");
        }
        try {
            RequestCoPdu pdu = (RequestCoPdu) fragments.next();
            byte[] pduStub = pdu.getStub();
            if (pduStub == null) {
                pduStub = new byte[0];
            }
            while (fragments.hasNext()) {
                RequestCoPdu fragment = (RequestCoPdu) fragments.next();
                byte[] fragmentStub = fragment.getStub();
                if (fragmentStub != null && fragmentStub.length > 0) {
                    byte[] tmp = new byte[pduStub.length + fragmentStub.length];
                    System.arraycopy(pduStub, 0, tmp, 0, pduStub.length);
                    System.arraycopy(fragmentStub, 0, tmp, pduStub.length,
                            fragmentStub.length);
                    pduStub = tmp;
                }
            }
            int length = pduStub.length;
            if (length > 0) {
                pdu.setStub(pduStub);
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

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception ex) {
            throw new IllegalStateException();
        }
    }

    private class FragmentIterator implements Iterator<RequestCoPdu> {

        private final int stubSize;
        private int index = 0;
        private final int callId = callIdCounter++;

        FragmentIterator(int stubSize) {
            this.stubSize = stubSize;
        }

        @Override
        public boolean hasNext() {
            return index < stub.length;
        }

        @Override
        public RequestCoPdu next() {
            if (index >= stub.length) {
                throw new NoSuchElementException();
            }
            RequestCoPdu fragment = (RequestCoPdu) RequestCoPdu.this.clone();
            int allocation = stub.length - index;
            fragment.setAllocationHint(allocation);
            if (stubSize < allocation) {
                allocation = stubSize;
            }
            byte[] fragmentStub = new byte[allocation];
            System.arraycopy(stub, index, fragmentStub, 0, allocation);
            fragment.setStub(fragmentStub);
            int flags = getFlags() & ~(PFC_FIRST_FRAG | PFC_LAST_FRAG);
            if (index == 0) {
                flags |= PFC_FIRST_FRAG;
            }
            index += allocation;
            if (index >= stub.length) {
                flags |= PFC_LAST_FRAG;
            }
            fragment.setFlags(flags);

            //always use the same callId now
            fragment.setCallId(callId);

//            if (firstfragsent)
//            {
//            	//this is so that all fragments have the same callid.
//            	fragment.setCallId(callId);
//            }
//            else
//            {
//            	firstfragsent = true;
//            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "In FragementIterator:next(): callIdCounter is {0} ,  for thread: {1}", new Object[]{callId, Thread.currentThread()});
            }
            return fragment;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
