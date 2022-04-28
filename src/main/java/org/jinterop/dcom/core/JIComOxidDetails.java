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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores the oxid details in memory.
 *
 * @since 1.0
 */
final class JIComOxidDetails {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private final JILocalCoClass referent;
    private final String ipid;
    private final JIOxid oxid;
    private final JIObjectId oid;
    private final String iid;
    private final JIComOxidRuntimeHelper comRuntimeHelper;
    private int portForRemUnknown = -1;
    private int protectionLevel = 2;
    private String remUnknownIpid = null;
    private ThreadGroup remUnknownThread = null;

    JIComOxidDetails(JILocalCoClass javaInstance, JIOxid oxid, JIObjectId oid, String iid, String ipid, JIInterfacePointer ptr, JIComOxidRuntimeHelper helper, int protectionLevel) {
        this.referent = javaInstance;
        this.ipid = ipid;
        this.oxid = oxid;
        this.oid = oid;
        this.iid = iid;
        this.protectionLevel = protectionLevel;
        this.comRuntimeHelper = helper;
    }

    void setPortForRemUnknown(int port) {
        portForRemUnknown = port;
    }

    int getPortForRemUnknown() {
        return portForRemUnknown;
    }

    String getIID() {
        return iid;
    }

    String getIpid() {
        return ipid;
    }

    String getRemUnknownIpid() {
        return remUnknownIpid;
    }

    void setRemUnknownIpid(String ipid) {
        this.remUnknownIpid = ipid;
    }

    JIObjectId getOid() {
        return oid;
    }

    JIOxid getOxid() {
        return oxid;
    }

    JILocalCoClass getReferent() {
        return referent;
    }

    JIComOxidRuntimeHelper getCOMRuntimeHelper() {
        return comRuntimeHelper;
    }

    int getProtectionLevel() {
        return protectionLevel;
    }

    void setRemUnknownThreadGroup(ThreadGroup remUnknown) {
        this.remUnknownThread = remUnknown;
    }

    void interruptRemUnknownThreadGroup() {
        if (remUnknownThread != null) {
            try {
                remUnknownThread.interrupt();
                //remUnknownThread.destroy();
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "JIComOxidDetails interruptRemUnknownThreadGroup {0}", e.toString());
            }
            remUnknownThread = null;
        }
    }
}
