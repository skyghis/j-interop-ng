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



package rpc.core;

import java.util.Arrays;

import ndr.NdrBuffer;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;
import rpc.Security;

public class AuthenticationVerifier extends NdrObject {

    public int authenticationService;

    public int protectionLevel;

    public int contextId;

    public byte[] body;

    public AuthenticationVerifier() {
        this(Security.AUTHENTICATION_SERVICE_NONE,
                Security.PROTECTION_LEVEL_NONE, 0, null);
    }

    public AuthenticationVerifier(int authenticatorLength) {
        this(Security.AUTHENTICATION_SERVICE_NONE,
                Security.PROTECTION_LEVEL_NONE, 0, authenticatorLength);
    }

    public AuthenticationVerifier(int authenticationService,
            int protectionLevel, int contextId, int authenticatorLength) {
        this(authenticationService, protectionLevel, contextId,
                new byte[authenticatorLength]);
    }

    public AuthenticationVerifier(int authenticationService,
            int protectionLevel, int contextId, byte[] body) {
        this.authenticationService = authenticationService;
        this.protectionLevel = protectionLevel;
        this.contextId = contextId;
        this.body = body;
    }

    public void decode(NetworkDataRepresentation ndr, NdrBuffer src) {
        src.align(4);
        authenticationService = src.dec_ndr_small();
        protectionLevel = src.dec_ndr_small();
        src.dec_ndr_small(); // padding count
        contextId = src.dec_ndr_long();
        System.arraycopy(src.getBuffer(), src.getIndex(), body, 0, body.length);
		src.index += body.length;
    }

    public void encode(NetworkDataRepresentation ndr, NdrBuffer dst) {
        int padding = dst.align(4, (byte) 0);
        dst.enc_ndr_small(authenticationService);
        dst.enc_ndr_small(protectionLevel);
        dst.enc_ndr_small(padding);
        dst.enc_ndr_small(0);  //Reserved
        dst.enc_ndr_long(contextId);
        System.arraycopy(body, 0, dst.getBuffer(), dst.getIndex(), body.length);
		//dst.index += body.length;
        dst.advance(body.length);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AuthenticationVerifier)) return false;
        AuthenticationVerifier other = (AuthenticationVerifier) obj;
        return (authenticationService == other.authenticationService &&
                protectionLevel == other.protectionLevel &&
                        contextId == other.contextId &&
                                Arrays.equals(body, other.body));
    }

    public int hashCode() {
        return authenticationService ^ protectionLevel ^ contextId;
    }

}
