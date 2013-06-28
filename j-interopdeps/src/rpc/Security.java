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

package rpc;

import java.io.IOException;

import ndr.NetworkDataRepresentation;

public interface Security {

    public static final String USERNAME = "rpc.security.username";

    public static final String PASSWORD = "rpc.security.password";

    public static final int AUTHENTICATION_SERVICE_NONE = 0;

    public static final int PROTECTION_LEVEL_NONE = 1;

    public static final int PROTECTION_LEVEL_CONNECT = 2;

    public static final int PROTECTION_LEVEL_CALL = 3;

    public static final int PROTECTION_LEVEL_PACKET = 4;

    public static final int PROTECTION_LEVEL_INTEGRITY = 5;

    public static final int PROTECTION_LEVEL_PRIVACY = 6;

    public int getVerifierLength();

    public int getAuthenticationService();

    public int getProtectionLevel();

    public void processIncoming(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException;

    public void processOutgoing(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException;

}
