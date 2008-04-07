/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc.security.ntlm;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

public abstract class AuthenticationSource {

    private static final AuthenticationSource INSTANCE;

    static {
        String service = "META-INF/services/" +
                AuthenticationSource.class.getName();
        URL location = null;
        ClassLoader loader = AuthenticationSource.class.getClassLoader();
        if (loader != null) location = loader.getResource(service);
        if (location == null) location = ClassLoader.getSystemResource(service);
        AuthenticationSource instance = null;
        if (location != null) {
            try {
                Properties properties = new Properties();
                properties.load(location.openStream());
                Enumeration classNames = properties.propertyNames();
                if (classNames.hasMoreElements()) {
                    Class sourceClass = Class.forName((String)
                            classNames.nextElement());
                    instance = (AuthenticationSource) sourceClass.newInstance();
                }
            } catch (Exception ex) {
                System.err.println("WARNING: Unable to instantiate source.");
                ex.printStackTrace();
            }
        }
        INSTANCE = instance;
    }

    public static AuthenticationSource getDefaultInstance() {
        return INSTANCE;
    }

    public abstract byte[] createChallenge(Properties properties,
            Type1Message type1) throws IOException;

    public abstract byte[] authenticate(Properties properties,
            Type2Message type2, Type3Message type3) throws IOException;

}
