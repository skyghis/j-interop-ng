package rpc.security.ntlm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD4 {

    private MD4() {
    }

    public static byte[] digest(byte[] input) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD4");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
        md.update(input);
        return md.digest();
    }
}
