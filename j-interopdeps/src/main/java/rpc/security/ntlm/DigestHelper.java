/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rpc.security.ntlm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class DigestHelper {

    // Load additional providers from BouncyCastle
    static {
        if (!Boolean.getBoolean("ignore_bouncycastleprovider") && Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private DigestHelper() {
    }

    public static byte[] md4(byte[] input) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD4");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        md.update(input);
        return md.digest();
    }

    public static byte[] md5(byte[] input) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        md.update(input);
        return md.digest();
    }

    public static byte[] md5(byte[] input1, byte[] input2) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        md.update(input1);
        md.update(input2);
        return md.digest();
    }
}
