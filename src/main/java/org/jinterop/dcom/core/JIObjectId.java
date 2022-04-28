/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package org.jinterop.dcom.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.util.Hexdump;

final class JIObjectId implements Serializable {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final long serialVersionUID = -4335536047242439700L;
    private final byte[] oid;
    private int refcountofIPID = 0;
    private long lastPingTime = System.currentTimeMillis();
    final boolean dontping;

    int getIPIDRefCount() {
        return refcountofIPID;
    }

    boolean hasExpired() {
        //8 minutes interval...giving COM Client some grace period.
        return (System.currentTimeMillis() - lastPingTime) > 8 * 60 * 1000;// lastPingTime = System.currentTimeMillis();
    }

    void updateLastPingTime() {
        lastPingTime = System.currentTimeMillis();
    }

    void setIPIDRefCountTo0() {
        refcountofIPID = 0;
    }

    void decrementIPIDRefCountBy1() {
        refcountofIPID--;
    }

    void incrementIPIDRefCountBy1() {
        refcountofIPID++;
    }

    JIObjectId(byte[] oid, boolean dontping) {
        this.oid = oid;
        this.dontping = dontping;
        if (dontping) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "DONT PING is true for OID: {0}", toString());
            }
        }
    }

    byte[] getOID() {
        return oid;
    }

    @Override
    public int hashCode() {
        int result = 1;
        //from SUN
        for (int i = 0; i < oid.length; i++) {
            result = 31 * result + oid[i];
        }
        return result;

        //return Arrays.hashCode(oid);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JIObjectId)) {
            return false;
        }

        return Arrays.equals(oid, ((JIObjectId) obj).getOID());
    }

    @Override
    public String toString() {
        return "{ IPID ref count is " + refcountofIPID + " } and OID in bytes[] " + Hexdump.toHexString(oid) + " , hasExpired " + hasExpired() + " } ";
    }
}
