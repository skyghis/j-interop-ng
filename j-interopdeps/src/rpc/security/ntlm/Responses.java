/**
 * Extracted from http://davenport.sourceforge.net/ntlm.html
 * Copyright © 2003, 2006 Eric Glass (eric.glass@gmail.com) 
 * 
 */
package rpc.security.ntlm;



import gnu.crypto.hash.MD4;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Calculates the various Type 3 responses.
 */
public class Responses {

    /**
     * Calculates the LM Response for the given challenge, using the specified
     * password.
     *
     * @param password The user's password.
     * @param challenge The Type 2 challenge from the server.
     *
     * @return The LM Response.
     */
    public static byte[] getLMResponse(String password, byte[] challenge)
            throws Exception {
        byte[] lmHash = lmHash(password);
        return lmResponse(lmHash, challenge);
    }

    /**
     * Calculates the NTLM Response for the given challenge, using the
     * specified password.
     *
     * @param password The user's password.
     * @param challenge The Type 2 challenge from the server.
     *
     * @return The NTLM Response.
     */
    public static byte[] getNTLMResponse(String password, byte[] challenge)
            throws Exception {
        byte[] ntlmHash = ntlmHash(password);
        return lmResponse(ntlmHash, challenge);
    }

    /**
     * Calculates the NTLMv2 Response for the given challenge, using the
     * specified authentication target, username, password, target information
     * block, and client nonce.
     *
     * @param target The authentication target (i.e., domain).
     * @param user The username. 
     * @param password The user's password.
     * @param targetInformation The target information block from the Type 2
     * message.
     * @param challenge The Type 2 challenge from the server.
     * @param clientNonce The random 8-byte client nonce. 
     *
     * @return The NTLMv2 Response.
     */
    public static byte[][] getNTLMv2Response(String target, String user,
            String password, byte[] targetInformation, byte[] challenge,
                    byte[] clientNonce) throws Exception {
    	byte[][] retval = new byte[2][];
        byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
        byte[] blob = createBlob(targetInformation, clientNonce);
        retval[1] = blob; 
        retval[0] = lmv2Response(ntlmv2Hash, blob, challenge);
        return retval;
    }

    /**
     * Calculates the LMv2 Response for the given challenge, using the
     * specified authentication target, username, password, and client
     * challenge.
     *
     * @param target The authentication target (i.e., domain).
     * @param user The username.
     * @param password The user's password.
     * @param challenge The Type 2 challenge from the server.
     * @param clientNonce The random 8-byte client nonce.
     *
     * @return The LMv2 Response. 
     */
    public static byte[] getLMv2Response(String target, String user,
            String password, byte[] challenge, byte[] clientNonce)
                    throws Exception {
        byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
        return lmv2Response(ntlmv2Hash, clientNonce, challenge);
    }

    /**
     * Calculates the NTLM2 Session Response for the given challenge, using the
     * specified password and client nonce.
     *
     * @param password The user's password.
     * @param challenge The Type 2 challenge from the server.
     * @param clientNonce The random 8-byte client nonce.
     *
     * @return The NTLM2 Session Response.  This is placed in the NTLM
     * response field of the Type 3 message; the LM response field contains
     * the client nonce, null-padded to 24 bytes.
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws IllegalStateException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     */
    public static byte[] getNTLM2SessionResponse(String password,
            byte[] challenge, byte[] clientNonce) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalStateException, IllegalBlockSizeException, BadPaddingException 
    {
        byte[] ntlmHash = ntlmHash(password);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(challenge);
        md5.update(clientNonce);
        byte[] sessionHash = new byte[8];
        System.arraycopy(md5.digest(), 0, sessionHash, 0, 8);
        return lmResponse(ntlmHash, sessionHash);
    }

    /**
     * Creates the LM Hash of the user's password.
     *
     * @param password The password.
     *
     * @return The LM Hash of the given password, used in the calculation
     * of the LM Response.
     */
    private static byte[] lmHash(String password) throws Exception {
        byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
        int length = Math.min(oemPassword.length, 14);
        byte[] keyBytes = new byte[14];
        System.arraycopy(oemPassword, 0, keyBytes, 0, length);
        Key lowKey = createDESKey(keyBytes, 0);
        Key highKey = createDESKey(keyBytes, 7);
        byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
        Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
        des.init(Cipher.ENCRYPT_MODE, lowKey);
        byte[] lowHash = des.doFinal(magicConstant);
        des.init(Cipher.ENCRYPT_MODE, highKey);
        byte[] highHash = des.doFinal(magicConstant);
        byte[] lmHash = new byte[16];
        System.arraycopy(lowHash, 0, lmHash, 0, 8);
        System.arraycopy(highHash, 0, lmHash, 8, 8);
        return lmHash;
    }

    /**
     * Creates the NTLM Hash of the user's password.
     *
     * @param password The password.
     *
     * @return The NTLM Hash of the given password, used in the calculation
     * of the NTLM Response and the NTLMv2 and LMv2 Hashes.
     */
    static byte[] ntlmHash(String password) throws UnsupportedEncodingException {
        byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
        MD4 md4 = new MD4();
        md4.update(unicodePassword,0,unicodePassword.length);
        return md4.digest();
    }

    /**
     * Creates the NTLMv2 Hash of the user's password.
     *
     * @param target The authentication target (i.e., domain).
     * @param user The username.
     * @param password The password.
     *
     * @return The NTLMv2 Hash, used in the calculation of the NTLMv2
     * and LMv2 Responses. 
     */
     static byte[] ntlmv2Hash(String target, String user,
            String password) throws Exception {
        byte[] ntlmHash = ntlmHash(password);
        String identity = user.toUpperCase() + target;
        return hmacMD5(identity.getBytes("UnicodeLittleUnmarked"), ntlmHash);
    }

   
     
    /**
     * Creates the LM Response from the given hash and Type 2 challenge.
     *
     * @param hash The LM or NTLM Hash.
     * @param challenge The server challenge from the Type 2 message.
     *
     * @return The response (either LM or NTLM, depending on the provided
     * hash).
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws IllegalStateException 
     */
    private static byte[] lmResponse(byte[] hash, byte[] challenge) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalStateException, IllegalBlockSizeException, BadPaddingException
             {
        byte[] keyBytes = new byte[21];
        System.arraycopy(hash, 0, keyBytes, 0, 16);
        Key lowKey = createDESKey(keyBytes, 0);
        Key middleKey = createDESKey(keyBytes, 7);
        Key highKey = createDESKey(keyBytes, 14);
        Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
        des.init(Cipher.ENCRYPT_MODE, lowKey);
        byte[] lowResponse = des.doFinal(challenge);
        des.init(Cipher.ENCRYPT_MODE, middleKey);
        byte[] middleResponse = des.doFinal(challenge);
        des.init(Cipher.ENCRYPT_MODE, highKey);
        byte[] highResponse = des.doFinal(challenge);
        byte[] lmResponse = new byte[24];
        System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
        System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
        System.arraycopy(highResponse, 0, lmResponse, 16, 8);
        return lmResponse;
    }

    /**
     * Creates the LMv2 Response from the given hash, client data, and
     * Type 2 challenge.
     *
     * @param hash The NTLMv2 Hash.
     * @param clientData The client data (blob or client nonce).
     * @param challenge The server challenge from the Type 2 message.
     *
     * @return The response (either NTLMv2 or LMv2, depending on the
     * client data).
     */
    private static byte[] lmv2Response(byte[] hash, byte[] clientData,
            byte[] challenge) throws Exception {
        byte[] data = new byte[challenge.length + clientData.length];
        System.arraycopy(challenge, 0, data, 0, challenge.length);
        System.arraycopy(clientData, 0, data, challenge.length,
                         clientData.length);
        byte[] mac = hmacMD5(data, hash);
        byte[] lmv2Response = new byte[mac.length + clientData.length];
        System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
        System.arraycopy(clientData, 0, lmv2Response, mac.length,
                         clientData.length);
        return lmv2Response;
    }

    /**
     * Creates the NTLMv2 blob from the given target information block and
     * client nonce.
     *
     * @param targetInformation The target information block from the Type 2
     * message.
     * @param clientNonce The random 8-byte client nonce.
     *
     * @return The blob, used in the calculation of the NTLMv2 Response.
     */
    static byte[] createBlob(byte[] targetInformation,
            byte[] clientNonce) {
        byte[] blobSignature = new byte[] {
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00
        };
        byte[] reserved = new byte[] {
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        byte[] unknown1 = new byte[] {
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        byte[] unknown2 = new byte[] {
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        long time = System.currentTimeMillis();
        time += 11644473600000l; // milliseconds from January 1, 1601 -> epoch.
        time *= 10000; // tenths of a microsecond.
        // convert to little-endian byte array.
        byte[] timestamp = new byte[8];
        for (int i = 0; i < 8; i++) {
            timestamp[i] = (byte) time;
            time >>>= 8;
        }
        byte[] blob = new byte[blobSignature.length + reserved.length +
                               timestamp.length + clientNonce.length +
                               unknown1.length + targetInformation.length +
                               unknown2.length];
        int offset = 0;
        System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
        offset += blobSignature.length;
        System.arraycopy(reserved, 0, blob, offset, reserved.length);
        offset += reserved.length;
        System.arraycopy(timestamp, 0, blob, offset, timestamp.length);
        offset += timestamp.length;
        System.arraycopy(clientNonce, 0, blob, offset,
                         clientNonce.length);
        offset += clientNonce.length;
        System.arraycopy(unknown1, 0, blob, offset, unknown1.length);
        offset += unknown1.length;
        System.arraycopy(targetInformation, 0, blob, offset,
                         targetInformation.length);
        offset += targetInformation.length;
        System.arraycopy(unknown2, 0, blob, offset, unknown2.length);
        return blob;
    }

    /**
     * Calculates the HMAC-MD5 hash of the given data using the specified
     * hashing key.
     *
     * @param data The data for which the hash will be calculated. 
     * @param key The hashing key.
     *
     * @return The HMAC-MD5 hash of the given data.
     * @throws NoSuchAlgorithmException 
     */
    static byte[] hmacMD5(byte[] data, byte[] key) throws NoSuchAlgorithmException  {
        byte[] ipad = new byte[64];
        byte[] opad = new byte[64];
        for (int i = 0; i < 64; i++) {
            ipad[i] = (byte) 0x36;
            opad[i] = (byte) 0x5c;
        }
        for (int i = key.length - 1; i >= 0; i--) {
            ipad[i] ^= key[i];
            opad[i] ^= key[i];
        }
        byte[] content = new byte[data.length + 64];
        System.arraycopy(ipad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        data = md5.digest(content);
        content = new byte[data.length + 64];
        System.arraycopy(opad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        return md5.digest(content);
    }

    /**
     * Creates a DES encryption key from the given key material.
     *
     * @param bytes A byte array containing the DES key material.
     * @param offset The offset in the given byte array at which
     * the 7-byte key material starts.
     *
     * @return A DES encryption key created from the key material
     * starting at the specified offset in the given byte array.
     */
    private static Key createDESKey(byte[] bytes, int offset) {
        byte[] keyBytes = new byte[7];
        System.arraycopy(bytes, offset, keyBytes, 0, 7);
        byte[] material = new byte[8];
        material[0] = keyBytes[0];
        material[1] = (byte) (keyBytes[0] << 7 | (keyBytes[1] & 0xff) >>> 1);
        material[2] = (byte) (keyBytes[1] << 6 | (keyBytes[2] & 0xff) >>> 2);
        material[3] = (byte) (keyBytes[2] << 5 | (keyBytes[3] & 0xff) >>> 3);
        material[4] = (byte) (keyBytes[3] << 4 | (keyBytes[4] & 0xff) >>> 4);
        material[5] = (byte) (keyBytes[4] << 3 | (keyBytes[5] & 0xff) >>> 5);
        material[6] = (byte) (keyBytes[5] << 2 | (keyBytes[6] & 0xff) >>> 6);
        material[7] = (byte) (keyBytes[6] << 1);
        oddParity(material);
        return new SecretKeySpec(material, "DES");
    }

    /**
     * Applies odd parity to the given byte array.
     *
     * @param bytes The data whose parity bits are to be adjusted for
     * odd parity.
     */
    private static void oddParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            boolean needsParity = (((b >>> 7) ^ (b >>> 6) ^ (b >>> 5) ^
                                    (b >>> 4) ^ (b >>> 3) ^ (b >>> 2) ^
                                    (b >>> 1)) & 0x01) == 0;
            if (needsParity) {
                bytes[i] |= (byte) 0x01;
            } else {
                bytes[i] &= (byte) 0xfe;
            }
        }
    }

}