/**
 * iwombat donated the pieces of code required by the library for UUID generation, Many Thanks to Bob Combs and www.iwombat.com for this. 
 */

package com.iwombat.foundation;

import com.iwombat.foundation.uuid.UUIDFactory;

/**
 * Class responsible for creating unique identifiers.
 * @author jeremyp
 */
public final class IdentifierFactory {
    
    /**
     * Private constructor to ensure utility-class usage.
     */
    private IdentifierFactory() { } 

    /**
     * @return a new unique identifer.
     */
    public static Identifier createUniqueIdentifier() {
        return UUIDFactory.createUUID();
    }

    /**
     * Creates a new identifier from an existing (persisted) byte-array representation
     * @param byteArray persisted byte-array representation of an Identifier.
     * @return new Identifier from the existing byte-array representation
     */
    public static Identifier createUniqueIdentifier(byte[] byteArray) {
        return  UUIDFactory.createUUID(byteArray);
    }

    /**
     * Creates Identifier from hex String representation of an existing Identifier
     * @param hexString The String version of an existing Identifier.
     * @return New Identifier from the existing hex String representation
     */
    public static Identifier createUniqueIdentifier(String hexString) {
        return  UUIDFactory.createUUID(hexString);
    }
}
