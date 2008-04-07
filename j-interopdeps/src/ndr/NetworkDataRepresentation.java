package ndr;

import jcifs.util.Hexdump;

public class NetworkDataRepresentation {

    public static final String NDR_UUID =
            "8a885d04-1ceb-11c9-9fe8-08002b104860";

    public static final int NDR_MAJOR_VERSION = 2;

    public static final int NDR_MINOR_VERSION = 0;

    public static final String NDR_SYNTAX = NDR_UUID + ":" + NDR_MAJOR_VERSION +
            "." + NDR_MINOR_VERSION;

    public int ptr;
    public NdrBuffer buf;
    public Format format;

	public NetworkDataRepresentation() {
	}

    public void setBuffer(NdrBuffer buf) {
        this.buf = buf;
    }
    public NdrBuffer getBuffer() {
        return buf;
    }

	public void hexdump(int count) {
		Hexdump.hexdump( System.err, buf.buf, buf.index, count );
	}
    public boolean readBoolean() {
        return buf.dec_ndr_small() == 0 ? false : true;
    }
    public void writeBoolean(boolean value) {
        buf.enc_ndr_small(value ? 1 : 0);
    }
    public int readUnsignedSmall() {
        return buf.dec_ndr_small();
    }
    public int readUnsignedShort() {
        return buf.dec_ndr_short();
    }
    public int readUnsignedLong() {
        return buf.dec_ndr_long();
    }
    public void writeUnsignedSmall(int value) {
        buf.enc_ndr_small(value);
    }
    public void writeUnsignedShort(int value) {
        buf.enc_ndr_short(value);
    }
    public void writeUnsignedLong(int value) {
        buf.enc_ndr_long(value);
    }

    public void setFormat(Format format) {
        this.format = format;
    }
    public Format getFormat() {
        return format;
    }
    public Format readFormat(boolean connectionless) {
        Format format = Format.readFormat(buf.buf, buf.index, connectionless);
        buf.index += 4;
        return format;
    }
    public void writeFormat(Format format) {
        format.writeFormat(buf.buf, buf.index, false);
        buf.index += 4;
    }
    public void writeFormat(boolean connectionless) {
        int index = buf.getIndex();
		buf.index += connectionless ? 3 : 4;
        format.writeFormat(buf.buf, index, connectionless);
    }

    public void readCharacterArray(char[] array, int offset, int length) {
        if (array == null || length == 0) return;
        length += offset;
        // won't work for EBCDIC
        for (int i = offset; i < length; i++) array[i] = (char) buf.buf[buf.index++];
    }
    public void writeCharacterArray(char[] array, int offset, int length) {
        if (array == null || length == 0) return;
        length += offset;
        // won't work for EBCDIC
        for (int i = offset; i < length; i++) buf.buf[buf.index++] = (byte) array[i];
    }
	public void writeOctetArray(byte[] b, int i, int l) {
		buf.writeOctetArray(b, i, l);
	}
	public void readOctetArray(byte[] b, int i, int l) {
		buf.readOctetArray(b, i, l);
	}
}
