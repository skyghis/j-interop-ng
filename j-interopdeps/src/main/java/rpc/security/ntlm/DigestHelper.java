/**
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
