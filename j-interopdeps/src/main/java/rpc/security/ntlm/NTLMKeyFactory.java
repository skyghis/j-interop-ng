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
package rpc.security.ntlm;

import gnu.crypto.prng.ARCFour;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

final class NTLMKeyFactory {

    private static final Random RANDOM = new Random();


    private NTLMKeyFactory() {
    }

    /**
     * NTLMv1 User Session Key. Cases where LMcompatibilitylevel is 0,1,2. For
     * 3,4,5 the logic is different and based upon the reponses being sent back
     * (either LMv2 or NTLMv2)
     *
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     * @throws DigestException
     */
    static byte[] getNTLMUserSessionKey(String password) throws UnsupportedEncodingException, DigestException {
        //look at NTLMPasswordAuthentication in jcifs. It supports only the NTLMUserSessionKey and the LMv2UserSessionKey...we need more :(
        byte[] ntlmHash = Responses.ntlmHash(password);
        return DigestHelper.md4(ntlmHash);
    }

    static byte[] getNTLMv2UserSessionKey(String target, String user, String password, byte[] challenge, byte[] blob) throws Exception {
        byte key[];
        byte[] ntlm2Hash = Responses.ntlmv2Hash(target, user, password);
        byte[] data = new byte[challenge.length + blob.length];
        System.arraycopy(challenge, 0, data, 0, challenge.length);
        System.arraycopy(blob, 0, data, challenge.length,
                blob.length);
        byte[] mac = Responses.hmacMD5(data, ntlm2Hash);
        key = Responses.hmacMD5(mac, ntlm2Hash);
        return key;
    }

    /**
     * Password of the user
     *
     * @param password
     * @param servernonce challenge + nonce from NTLM2 Session Response
     * @return
     * @throws DigestException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    static byte[] getNTLM2SessionResponseUserSessionKey(String password, byte[] servernonce) throws NoSuchAlgorithmException, UnsupportedEncodingException, DigestException {
        return Responses.hmacMD5(servernonce, getNTLMUserSessionKey(password));
    }

    /**
     * Randomly generated 16 bytes
     *
     * @return
     */
    static byte[] getSecondarySessionKey() {
        byte[] key = new byte[16];
        RANDOM.nextBytes(key);
        return key;
    }

    static IRandom getARCFOUR(byte[] key) {
        Map<String, byte[]> attrib = new HashMap<>();
        IRandom keystream = new ARCFour();
        attrib.put(ARCFour.ARCFOUR_KEY_MATERIAL, key);
        keystream.init(attrib);
        return keystream;
    }

    static byte[] applyARCFOUR(IRandom keystream, byte[] data) throws IllegalStateException, LimitReachedException {
        byte[] retData = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            retData[i] = (byte) (data[i] ^ keystream.nextByte());
        }

        return retData;
    }

    static byte[] decryptSecondarySessionKey(byte[] encryptedData, byte[] key) throws IllegalStateException, LimitReachedException {
        return applyARCFOUR(getARCFOUR(key), encryptedData);
    }

    static byte[] encryptSecondarySessionKey(byte[] plainData, byte[] key) throws IllegalStateException, LimitReachedException {
        return applyARCFOUR(getARCFOUR(key), plainData);
    }

    //TODO merge the signing routine for both client and server all that they differ by are keys...as expected
    static byte[] signingPt1(int sequenceNumber, byte[] signingKey, byte[] data, int lengthOfBuffer) throws NoSuchAlgorithmException {
        byte[] seqNumPlusData = new byte[4 + lengthOfBuffer];

        seqNumPlusData[0] = (byte) (sequenceNumber & 0xFF);
        seqNumPlusData[1] = (byte) ((sequenceNumber >> 8) & 0xFF);
        seqNumPlusData[2] = (byte) ((sequenceNumber >> 16) & 0xFF);
        seqNumPlusData[3] = (byte) ((sequenceNumber >> 24) & 0xFF);

        System.arraycopy(data, 0, seqNumPlusData, 4, lengthOfBuffer);

        byte[] retval = new byte[16];
        retval[0] = 0x01; //Version number LE 1.

        byte[] sign = Responses.hmacMD5(seqNumPlusData, signingKey);

        System.arraycopy(sign, 0, retval, 4, 8);

        retval[12] = (byte) (sequenceNumber & 0xFF);
        retval[13] = (byte) ((sequenceNumber >> 8) & 0xFF);
        retval[14] = (byte) ((sequenceNumber >> 16) & 0xFF);
        retval[15] = (byte) ((sequenceNumber >> 24) & 0xFF);

        return retval;
    }

    static void signingPt2(byte[] verifier, IRandom rc4) throws IllegalStateException, LimitReachedException {
        for (int i = 0; i < 8; i++) {
            verifier[i + 4] = (byte) (verifier[i + 4] ^ rc4.nextByte());
        }
    }

    static boolean compareSignature(byte[] src, byte[] target) {
        return Arrays.equals(src, target);
    }
}
