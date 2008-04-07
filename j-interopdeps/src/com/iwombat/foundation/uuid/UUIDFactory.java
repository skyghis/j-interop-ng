/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */


package com.iwombat.foundation.uuid;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.iwombat.util.HexStringUtil;

/**
 * Class responsible for creating unique identifiers specific 
 * to the DCE UUID implementation.
 * 
 * This factory contains all the algorythms necessary to generate
 * a UUID and place the value into the UUID value object.
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
 */
public final class UUIDFactory {

    private UUIDFactory() {
        
    }
    private static final long serialVersionUID = 1;
    
    // length of the ObjectId byte array
    private static final short OID_LENGTH = 16;

    // length to shift by one byte
    private static final short BYTE_SHIFT = 8;

    // byte and nibble masks
    private static final short LOW_BYTE_MASK = (short) ((short) (1 << BYTE_SHIFT) - 1);

    // private static final short HIGH_NIBBLE_MASK = (short) 0xf0;

    private static final short LOW_NIBBLE_MASK = (short) 0x0f;

    // maximum sequence number (four bytes)
    // leave room on high byte to stuff a version number
    private static final int MAX_SEQUENCE_NUMBER = 0x4000;

    // static sequence number
    private static int sequenceNumber = 0;

    // static MAC address
    private static byte[] macAddr;

    private static final short MACADDR_LENGTH = 6;

    // random number generator
    private static Random rand = new Random(System.currentTimeMillis());

    // last time encountered
    private static BigInteger lastTimestamp = BigInteger.ZERO;

    // Time masks and offsets
//    private static final long LOW_TIME_MASK = 0x00000000ffffffffL;

//    private static final long MID_TIME_MASK = 0x0000ffff00000000L;

//    private static final long HIGH_TIME_MASK = 0x0fff000000000000L;

//    private static final short MID_TIME_OFFSET = BYTE_SHIFT * 4;

//    private static final short HIGH_TIME_OFFSET = BYTE_SHIFT * 6;

    // Version and variant
    private static final byte VERSION = (byte) 0x10;

    private static final byte VARIANT = (byte) 0x80;

    // generate the number of 100 nanosecond intervals between 10/15/1582
    // and 1/1/1970 - the beginning of the Java clock
//  generate the number of 100 nanosecond intervals between 10/15/1582
    // and 1/1/1970 - the beginning of the Java clock
    private static final long MILLIS_NANOS_MULT = 1000;
    private static final long NANOSECONDS_PER_DAY = 86400 * 10000000;
    private static final long DAYS_SINCE_EPOCH = ((365 * (1970 - 1583)) // days per year
    + (24 * 3) // leap days per century
    + (5) // leap days 1563-1600 inclusive
    + (17) // leap days 20th century
    + (17 + 30 + 31) // remaining days in 1582
    - (12) // calendar reset in 9/1752
    );
    private static final BigInteger TIME_OFFSET = BigInteger.valueOf(DAYS_SINCE_EPOCH).
			multiply(BigInteger.valueOf(NANOSECONDS_PER_DAY));  

    // set the value of the MAC address at class initialization time
    static {
        // get the machine's ip address once per JVM invocation of the class
        String addrString = System.getProperty("MACADDR");
        try {
            macAddr = HexStringUtil.bytesFromHexString(addrString);
        } catch (RuntimeException e) {
            macAddr = getDummyMACADDR();
        }

        // make sure we got a valid MACAddr length
        // we may just want to just die here instead
        if (macAddr.length < MACADDR_LENGTH) {
            System.err.println("StartupError: MACADDR not defined properly in System.properties");
            macAddr = getDummyMACADDR();
        }

        // seed the sequence number with a random
        sequenceNumber = rand.nextInt();
        sequenceNumber %= MAX_SEQUENCE_NUMBER;
    }

    /**
     * increment the sequence number -- synchronized for your protection
     */
    private static int incrementSequence() {
        ++sequenceNumber;
        sequenceNumber %= MAX_SEQUENCE_NUMBER;
        return sequenceNumber;
    }

    private static void setTimeStamp(BigInteger time) {
        lastTimestamp = time;
    }

    /**
     * get a new time if not incremented since the last OID was generated we 
     * negate the result to flag rolling of the sequence number
     */
    private static final int GREATER_THAN = 1;
    private static BigInteger getTime() {
        BigInteger time = TIME_OFFSET.add(BigInteger.valueOf(MILLIS_NANOS_MULT * System.currentTimeMillis()));
        
        if (GREATER_THAN == time.compareTo(lastTimestamp)) {
            incrementSequence();
        } else {
            setTimeStamp(time);
        }

        return time;
    }

    /**
     * convienience method puts a long into the id buffer little endian style - decending
     * 
     * @param arrayPosition
     *            beginning position
     * @param numBytes
     */
    private static void stuffOidWithLong(byte[] tempOid, long l, int arrayPosition, int numBytes) {
        int index = (arrayPosition + numBytes) - 1;
        do {
            tempOid[index] = (byte) (l & LOW_BYTE_MASK);
            l >>>= BYTE_SHIFT;
        } while (index-- > arrayPosition);
    }

    private static byte[] getDummyMACADDR() {
        byte[] dummy = new byte[6];
        rand.nextBytes(dummy);

        // set the broadcast bit so we know its a dummy
        dummy[0] |= (byte) 0x80;

        // put the last octet of the ip address in index 5 for some
        // guarantee of uniqueness on the same subnet
        // unless the localhost is set to 127.0.0.1
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            byte[] address = localhost.getAddress();
            if ((byte) 0x7F != address[0]) {
                dummy[dummy.length - 1] = address[address.length - 1];
            }
        } catch (UnknownHostException e) {
        }
        return dummy;
    }

    private static void stuffOidWithBytes(byte[] oid, int oidIdx, byte[] value) {
        int idx = 0;
        while (idx < value.length) {
            oid[oidIdx++] = value[idx++];
        }
    }

    private static final int TIME_ARRAY_LEN = 8;
    /**
     * @param array time bytes
     * @return time as bytes
     */
    private static byte[] createTimeArray(byte[] array) {
        byte[] timeArray = new byte[TIME_ARRAY_LEN];
        int idx = 0;
        while (idx < array.length) {
            timeArray[timeArray.length - idx - 1] = array[array.length - idx - 1];
            idx++;
        }
        return timeArray;
    }
    
    /**
     * Generate an oid from scratch.
     */
    private static synchronized byte[] createOid() {
        long naughtyBits = 0;
        byte[] localOid = new byte[OID_LENGTH];       
        
        // get since epoch and convert it to a byte array
        byte[] timeArray = createTimeArray(getTime().toByteArray());

        // set the time bytes in the oid
        stuffOidWithBytes(localOid, 0, timeArray);

        naughtyBits = sequenceNumber;
        stuffOidWithLong(localOid, naughtyBits, 8, 2);

        // the machine's macaddr - do it by hand
        stuffOidWithBytes(localOid, 10, macAddr);

        // put the version into position 6
        localOid[6] &= LOW_NIBBLE_MASK;
        localOid[6] |= VERSION;

        // put the variant into position 8
        localOid[8] &= LOW_NIBBLE_MASK;
        localOid[8] |= VARIANT;

        return localOid;
    }

    /**
     * @return a new unique identifer.
     */
    public static UUID createUUID() {
        return new UUID(createOid());
    }

    /**
     * Creates a new identifier from an existing (persisted) byte-array representation
     * @param byteArray persisted byte-array representation of an Identifier.
     * @return new Identifier from the existing byte-array representation
     */
    public static UUID createUUID(byte[] byteArray) {
        return  new UUID(byteArray);
    }

    /**
     * Creates Identifier from hex String representation of an existing Identifier
     * @param hexString The String version of an existing Identifier.
     * @return New Identifier from the existing hex String representation
     */
    public static UUID createUUID(String hexString) {
        return  (new UUID(hexString));
    }
}
    
