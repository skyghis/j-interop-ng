/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */


package com.iwombat.foundation;

import java.io.Serializable;

/**
 * Generic Identifier interface.
 * @author jeremyp
 */
public interface Identifier extends Serializable {
    /**
     * @return the byte-array value for this identifier
     */
    byte[] getValue();

    /**
     * @return a hex-string representation of the byte value 
     */
    String toHexString();

    /** 
     * @return human-readable String representation
     */
    String toString();

    /**
     * @param obj The object to which this id should be compared
     * @return true if this Identifier is equal to another, false otherwise
     */
    boolean equals(Object obj);

    /**
     * @return a valid hash-code for this id
     */
    int hashCode();
}
