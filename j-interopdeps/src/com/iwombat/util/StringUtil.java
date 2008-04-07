/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */

package com.iwombat.util;



import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Class StringUtil
 * Description:  General utilities for string manipulation
 *
 * @author  $Author: vikramrc $
 * @version $Revision: 1.2 $
 * 
 * <br>
 * <dl><dt><b>Date:</b><dd>$Date: 2007/12/25 10:18:57 $</dd></dl>
 */

public final class StringUtil extends Object {

    /** supress creation of instances of StringUtil */

    private StringUtil() {}

    /**
     * Method replaceString
     * Description: replace all occurances of substring with another
     *
     * @param target - the target string
     * @param match - the substring to be replaced
     * @param replace - the replacement for match
     * @return String
     */

    public static String replaceString(String target, String match, String replace) {

        if (null == target) {
            return null;
        }

        if ((null == match) || (match.equals(""))) {
            return target;
        }

        String       temp      = new String(target);
        StringBuffer newString = new StringBuffer();
        int          loc;

        while ((loc = temp.indexOf(match)) != -1) {
            newString.append(temp.substring(0, loc));
            newString.append(replace);

            temp = temp.substring(loc + match.length());
        }

        newString.append(temp);

        return newString.toString();
    }

    /**
     * Method splitString
     * Description: Split a delimited string into an ArrayList
     *   Much like the perl split function
     *
     * @param valueList
     * @param delimiter
     * @return $returnType
     */

    public static ArrayList splitString(String valueList, String delimiter) {

        ArrayList result = new ArrayList();

        if ((valueList != null) && (delimiter != null)) {
            StringTokenizer splitter = new StringTokenizer(valueList, delimiter);

            while (splitter.hasMoreTokens()) {
                result.add(splitter.nextToken());
            }
        }

        return result;
    }
}
