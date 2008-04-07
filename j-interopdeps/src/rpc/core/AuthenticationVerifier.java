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
