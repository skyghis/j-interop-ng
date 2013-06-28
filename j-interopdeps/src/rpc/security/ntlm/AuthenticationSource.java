/**
* Donated by Jarapac (http://jarapac.sourceforge.net/) and released under EPL.
* 
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v1.
*  
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
