/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
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
