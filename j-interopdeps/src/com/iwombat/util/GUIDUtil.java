/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */


package com.iwombat.util;


/**
 * Generic utility for converting byte-arrays containing UUIDs to Microsoft
 * style GUID strings. This entails some rather unique little-to-big endian
 * conversions.
 *
 * <p>Due to how MS structures a UUID (GUID) in c and its decomposition into
 * a string, there are some rather unique little endian conversions that take
 * place. <br>
 * For example:<br>
 * Hex string:   <code>a1b1c1d1a2b2a3b3a4b4a5b5c5d5e5f5</code><br>
 * GUID string:  <code>{d1c1b1a1-b2a2-b3a3-b4a4-a5b5c5d5e5f5}</code><br>
 * <br>
 * <br>
 * Implied MS c-struct:
 * <pre>
 * struct {
 *   int lowTime;
 *   short midTime;
 *   short highTimeAndVersion;
 *   short sequenceAndVariant;
 *   uchar[] macaddr;
 * } GUID
 * </pre>
 *
 * Note: These conversions will leave the original version and variant in tact and
 * will not convert to the MS version and variant or vice-versa.
 *
 * @author  $Author: vikramrc $
 * @version $Revision: 1.2 $
 * 
 * <br>
 * <dl><dt><b>Date:</b><dd>$Date: 2007/12/25 10:18:57 $</dd></dl>
 */
public class GUIDUtil {
   private static final String LEFT_BRACE = "{"; 
   private static final String RIGHT_BRACE = "}"; 
   private static final String DELIMITER = "-";
   private static final int LEN = 32;
  /**
   * Convert hex string to GUID string
   *
   * @param hexString - an untokenized hex string
   * @return GUID string
   * @throws IllegalArgumentException
   */
   public static String guidStringFromHexString(String hexString) {
        // make sure the length is proper
        if (LEN != hexString.length()) {
           throw new IllegalArgumentException("Improper length UUID:" + hexString.length());
        }
        StringBuffer guidString=new StringBuffer("");

        guidString.append(hexString.substring(6,8));
        guidString.append(hexString.substring(4,6));
        guidString.append(hexString.substring(2,4));
        guidString.append(hexString.substring(0,2));
        guidString.append(DELIMITER);
        guidString.append(hexString.substring(10,12));
        guidString.append(hexString.substring(8,10));
        guidString.append(DELIMITER);
        guidString.append(hexString.substring(14,16));
        guidString.append(hexString.substring(12,14));
        guidString.append(DELIMITER);
        guidString.append(hexString.substring(16,18));
        guidString.append(hexString.substring(18,20));
        guidString.append(DELIMITER);
        guidString.append(hexString.substring(20));
       
        return guidString.toString();
    }

  /**
   * Convert GUID string to a HEX string
   *
   * @param guidString - tokenized MS-style GUID string
   * @return hexString - untokenized hexString
   * @throws IllegalArgumentException
   */
   public static String hexStringFromGUID(String guidString) {
        StringBuffer hexString = new StringBuffer();
        String workingString = guidString;
        workingString = StringUtil.replaceString(workingString, LEFT_BRACE, "");
        workingString = StringUtil.replaceString(workingString, RIGHT_BRACE, "");
        workingString = StringUtil.replaceString(workingString, DELIMITER, "");

        if (LEN != workingString.length()) {
           throw new IllegalArgumentException("Improper length GUID:" + workingString.length());
        }

        hexString.append(workingString.substring(6,8));
        hexString.append(workingString.substring(4,6));
        hexString.append(workingString.substring(2,4));
        hexString.append(workingString.substring(0,2));
        hexString.append(workingString.substring(10,12));
        hexString.append(workingString.substring(8,10));
        hexString.append(workingString.substring(14,16));
        hexString.append(workingString.substring(12,14));
        hexString.append(workingString.substring(16,18));
        hexString.append(workingString.substring(18,20));
        hexString.append(workingString.substring(20));

        return hexString.toString();
   }

}

