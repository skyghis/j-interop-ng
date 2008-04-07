/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */


package com.iwombat.util;





/**
 * Class HexStringUtil
 * Description: Utility class for byte-array/string conversions
 *
 * @author  $Author: vikramrc $
 * @version $Revision: 1.2 $
 * 
 * <br>
 * <dl><dt><b>Date:</b><dd>$Date: 2007/12/25 10:18:57 $</dd></dl>
 */

public final class HexStringUtil {

    private static final short BYTE_SHIFT       = 8;
    private static final short LOW_BYTE_MASK    = (short) ((short) (1 << BYTE_SHIFT) - 1);
    private static final short HIGH_NIBBLE_MASK = (short) 0xf0;
    private static final int   RADIX            = 16;
    private static final String HEX_TOKEN       = "0x";


    private HexStringUtil() {}

    /**
     * Method bytesFromHexString
     * Description: create a byte-array from a hexidecimal string
     *   accepts tokenized (0x leading) and non-tokenized hex strings
     * @param hexString
     * @return byte[]
     * @throws IllegalArgumentException
     */

    public static byte[] bytesFromHexString(String hexString) {

        if (hexString.startsWith(HEX_TOKEN)) {
            hexString = hexString.substring(HEX_TOKEN.length());
        }

        int length = (int) hexString.length() / 2;

        if (0 == length) {
            throw new IllegalArgumentException("zero length string");
        }

        if ((hexString.length() % 2) != 0) {
            throw new IllegalArgumentException("odd length string");
        }

        byte tempArray[] = new byte[length];

        for (int i = 0; i < length; i++) {

            // fail if we can't convert into bytes
            try {
                String temp     = hexString.substring((i * 2), (i * 2) + 2);
                char   digs[]   = temp.toCharArray();
                int    theValue = Character.digit(digs[0], RADIX) * RADIX;

                theValue     += Character.digit(digs[1], RADIX);
                tempArray[i] = (byte) theValue;
            } catch (Exception e) {
                throw new IllegalArgumentException("improperly formed hex string");
            }
        }

        return tempArray;
    }

    /**
     * Method stringFromBytes
     * Description: create a hexidecimal string from a byte-array
     *
     * @param byteArray
     * @return String
     */

    public static String stringFromBytes(byte[] byteArray) {

        StringBuffer str = new StringBuffer("");

        for (int i = 0; i < byteArray.length; i++) {
            if ((int) (byteArray[i] & HIGH_NIBBLE_MASK) > 0) {
                str.append(Integer.toHexString((int) byteArray[i] & LOW_BYTE_MASK));
            } else {
                str.append("0"
                           + Integer.toHexString((int) byteArray[i]
                                                 & LOW_BYTE_MASK));
            }
        }

        return str.toString();
    }

   /**
     * Method stringFromBytesWithToken
     * Description: create a hexidecimal string from a byte-array
     *  preappend with 0x token
     * @param byteArray
     * @return String
     */
    public static String stringFromBytesWithToken(byte[] byteArray) {
        return new String(HEX_TOKEN + stringFromBytes(byteArray));
    }

}
