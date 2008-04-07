package ndr;

public abstract class NdrObject {

	static final int opnum = -1;

    public Object value;

	public int getOpnum() {
		return opnum;
	}

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) throws NdrException {
		ndr.buf = dst;
		write(ndr); /* just for compatibility with jarapac < 0.2 */
    }
    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) throws NdrException {
		ndr.buf = src;
		read(ndr);
    }
	public void write(NetworkDataRepresentation ndr) { }
	public void read(NetworkDataRepresentation ndr) { }
}

