/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */


package com.iwombat.foundation.uuid;

import java.io.Serializable;

import com.iwombat.foundation.Identifier;
import com.iwombat.util.HexStringUtil;

/**
 *
 * Universal object identifier. <br>
 * 16-byte Object identifier for use by all persistant objects based on DCE standard <br>
 * <ul>
 * <li>byte 00-03: low time comonent
 * <li>byte 04-05: mid time component
 * <li>byte 06-07: high time component and version
 * <li>byte 08: sequence number high byte and variant
 * <li>byte 09: sequence number low byte
 * <li>byte 10-15: MAC address - passed in from System property
 * </ul>
 * <br>
 * 
 * Implementation notes: The DCE spec calls for MacAddress as part of the UUID algorythm. Since java supplies no
 * mechanism for this ObjectId looks for a MACADDR system property consisting of a hex string of six bytes ( a 12
 * character hex string - no delimiters and no 0x token ). Failing to find said system property ObjectId generates a
 * random MACADDR and uses the last octet of the ip address as the last octet in the MACADDR to guarantee unequeness on
 * the same subnet. (If localhost is NOT 127.0.0.1)
 * 
 * @author bobc
 *
 */
public class UUID implements Identifier, Serializable {

    private static final long serialVersionUID = 1;
    
    private byte[] value = new byte[16];
    
    protected UUID(byte[] value) {
        this.setValue(value);
    }

    private static final int UUID_LEN = 16;
    
    protected UUID(String hexString) {       
        if ((2 * UUID_LEN) != hexString.length()) {
            throw new IllegalArgumentException("Invalid UUID Length of :" + hexString.length() + "  String:" + hexString);
        }
        
        byte[] idBytes = HexStringUtil.bytesFromHexString(hexString);
        this.setValue(idBytes);
    }
    
    /**
     * @see com.raf.foundation.Identifier#getValue()
     */
    public byte[] getValue() {
        return value;
    }
    
    /**
     * @param newValue
     */
    protected void setValue(byte[] newValue) {
        if (UUID_LEN != newValue.length) {
            throw new IllegalArgumentException("Invalid UUID Length of :" + newValue.length);
        }
 
        this.value = newValue;
    }

    /**
     * @see com.raf.foundation.Identifier#toHexString()
     */
    public String toHexString() {
        return HexStringUtil.stringFromBytes(getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return toHexString();
    }
    
    /**
     * Compares for equality.
     * 
     * @return true if o is an ObjectId, and it's value equal.
     * @param obj The object to which this id should be compared.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof UUID)) {
            return false;
        }

        byte[] otherBinaryValue = ((UUID) obj).getValue();
        byte[] myBinaryValue = this.getValue();

        for (int counter = 0; counter < UUID_LEN; counter++) {
            if (otherBinaryValue[counter] != myBinaryValue[counter]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Supports the java obect hashCode - lamely.
     * 
     * @return hashcode.
     */
    public int hashCode() {
        return this.toHexString().hashCode();
    }

    
}
