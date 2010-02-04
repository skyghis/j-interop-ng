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

package rpc;

import ndr.Format;
import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

public abstract class ConnectionOrientedPdu extends NdrObject implements ProtocolDataUnit {

    public static final int CONNECTION_ORIENTED_MAJOR_VERSION = 5;

    public static final int MUST_RECEIVE_FRAGMENT_SIZE = 7160;

    /**
     * Flag indicating the PDU is the first fragment.
     */
    public static final int PFC_FIRST_FRAG = 0x01;

    /**
     * Flag indicating the PDU is the last fragment.
     */
    public static final int PFC_LAST_FRAG = 0x02;

    /**
     * Flag indicating a cancel was pending at the sender.
     */
    public static final int PFC_PENDING_CANCEL = 0x04;

    /**
     * Flag indicating support for concurrent multiplexing of a
     * single connection.
     */
    public static final int PFC_CONC_MPX = 0x10;

    /**
     * Flag for fault PDUs; if set, indicates that the call definitely
     * did not execute.
     */
    public static final int PFC_DID_NOT_EXECUTE = 0x20;

    /**
     * Flag for request PDU indicating oneway call semantics; no response will
     * be provided by the server.
     */
    public static final int PFC_MAYBE = 0x40;

    /**
     * Flag indicating a valid object UUID was specified and is present
     * in the optional object field.  If not set, the object field is
     * omitted.
     */
    public static final int PFC_OBJECT_UUID = 0x80;

    public static final int MAJOR_VERSION_OFFSET = 0;

    public static final int MINOR_VERSION_OFFSET = 1;

    public static final int TYPE_OFFSET = 2;

    public static final int FLAGS_OFFSET = 3;

    public static final int DATA_REPRESENTATION_OFFSET = 4;

    public static final int FRAG_LENGTH_OFFSET = 8;

    public static final int AUTH_LENGTH_OFFSET = 10;

    public static final int CALL_ID_OFFSET = 12;

    public static final int HEADER_LENGTH = 16;

    private int minorVersion = 0;

    private int flags = PFC_FIRST_FRAG | PFC_LAST_FRAG;

    protected static int callIdCounter = 0;

    private int callId = callIdCounter;

    private boolean useCallIdCounter = true;

    private int fragLength = 0;

    private int authLength = 0;

    private Format format;

    public int getMajorVersion() {
        return CONNECTION_ORIENTED_MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Format getFormat() {
        return (format != null) ? format : (format = Format.DEFAULT_FORMAT);
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean getFlag(int flag) {
        return (getFlags() & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        setFlags(value ? (getFlags() | flag) :
                (getFlags() & ~flag));
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
    	useCallIdCounter = false;
        this.callId = callId;
    }

    public int getFragmentLength() {
        return fragLength;
    }

    protected void setFragmentLength(int fragLength) {
        this.fragLength = fragLength;
    }

    public int getAuthenticatorLength() {
        return authLength;
    }

    protected void setAuthenticatorLength(int authLength) {
        this.authLength = authLength;
    }

    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) {
        ndr.setBuffer(src);
        readPdu(ndr);
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) {
        ndr.setBuffer(dst);
        ndr.setFormat(getFormat());
        writePdu(ndr);
        NdrBuffer buffer = ndr.getBuffer();
        int length = buffer.getLength();
        setFragmentLength(length);
        // write the header lengths, now that we know them.
        buffer.setIndex(FRAG_LENGTH_OFFSET);
        ndr.writeUnsignedShort(length);
        ndr.writeUnsignedShort(getAuthenticatorLength());
        buffer.setIndex(length);
    }

    protected void readPdu(NetworkDataRepresentation ndr) {
        readHeader(ndr);
        readBody(ndr);
    }

    protected void writePdu(NetworkDataRepresentation ndr) {
        writeHeader(ndr);
        writeBody(ndr);
    }

    protected void readHeader(NetworkDataRepresentation ndr) {
        if (ndr.readUnsignedSmall() != CONNECTION_ORIENTED_MAJOR_VERSION) {
            throw new IllegalStateException("Version mismatch.");
        }
        // read minor version
        setMinorVersion(ndr.readUnsignedSmall());
        if (getType() != ndr.readUnsignedSmall()) {
            throw new IllegalArgumentException("Incorrect PDU type.");
        }
        setFlags(ndr.readUnsignedSmall());
        Format format = ndr.readFormat(false);
        setFormat(format);
        ndr.setFormat(format);
        setFragmentLength(ndr.readUnsignedShort());
        setAuthenticatorLength(ndr.readUnsignedShort());
        this.callId = ((int) ndr.readUnsignedLong());
    }

    protected void writeHeader(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedSmall((short) getMajorVersion());
        ndr.writeUnsignedSmall((short) getMinorVersion());
        ndr.writeUnsignedSmall((short) getType());
        ndr.writeUnsignedSmall((short) getFlags());
        ndr.writeFormat(false);
        // skip the fragment and auth lengths, since we don't have them yet.
        ndr.writeUnsignedShort(0);
        ndr.writeUnsignedShort(0);
        ndr.writeUnsignedLong(useCallIdCounter ? callIdCounter++ : callId);
    }

    protected void readBody(NetworkDataRepresentation ndr) { }

    protected void writeBody(NetworkDataRepresentation ndr) { }

    public abstract int getType();

}
