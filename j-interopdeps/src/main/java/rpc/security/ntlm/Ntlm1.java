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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import jcifs.ntlmssp.NtlmFlags;
import jcifs.util.Hexdump;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.IntegrityException;
import rpc.Security;

public class Ntlm1 implements NtlmFlags, Security {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final byte[] CLIENT_SIGNING_MAGIC_CONSTANT = { // Session key to client-to-server signing key magic constant
        0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20,
        0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x2d, 0x74, 0x6f, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65,
        0x72, 0x20, 0x73, 0x69, 0x67, 0x6e, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x6d,
        0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00
    };
    private static final byte[] SERVER_SIGNING_MAGIC_CONSTANT = { // Session key to server-to-client signing key magic constant
        0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20,
        0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2d, 0x74, 0x6f, 0x2d, 0x63, 0x6c, 0x69, 0x65, 0x6e,
        0x74, 0x20, 0x73, 0x69, 0x67, 0x6e, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x6d,
        0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00
    };
    private static final byte[] CLIENT_SEALING_MAGIC_CONSTANT = { // Session key to client-to-server sealing key magic constant
        0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20,
        0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x2d, 0x74, 0x6f, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65,
        0x72, 0x20, 0x73, 0x65, 0x61, 0x6c, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x6d,
        0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00
    };
    private static final byte[] SERVER_SEALING_MAGIC_CONSTANT = { // Session key to server-to-client sealing key magic constant
        0x73, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x74, 0x6f, 0x20,
        0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x2d, 0x74, 0x6f, 0x2d, 0x63, 0x6c, 0x69, 0x65, 0x6e,
        0x74, 0x20, 0x73, 0x65, 0x61, 0x6c, 0x69, 0x6e, 0x67, 0x20, 0x6b, 0x65, 0x79, 0x20, 0x6d,
        0x61, 0x67, 0x69, 0x63, 0x20, 0x63, 0x6f, 0x6e, 0x73, 0x74, 0x61, 0x6e, 0x74, 0x00
    };
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static final int NTLM1_VERIFIER_LENGTH = 16;
    private final Cipher clientCipher;
    private final Cipher serverCipher;
    private final byte[] clientSigningKey;
    private final byte[] serverSigningKey;
    private final boolean isServer;
    private final int protectionLevel;
    private int requestCounter = 0;
    private int responseCounter = 0;

    public Ntlm1(int flags, byte[] sessionKey, boolean isServer) {
        this.protectionLevel = ((flags & NTLMSSP_NEGOTIATE_SEAL) != 0) ? PROTECTION_LEVEL_PRIVACY : PROTECTION_LEVEL_INTEGRITY;
        this.isServer = isServer;
        this.clientSigningKey = generateClientSigningKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        this.serverSigningKey = generateServerSigningKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        final byte[] clientSealingKey = generateClientSealingKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        final byte[] serverSealingKey = generateServerSealingKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        try {
            this.clientCipher = Cipher.getInstance("RC4");
            this.clientCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(clientSealingKey, "RC4"));
            this.serverCipher = Cipher.getInstance("RC4");
            this.serverCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(serverSealingKey, "RC4"));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to init decrypt RC4 cipher", ex);
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "Client Signing Key derieved from the session key: [{0}]", dumpString(clientSigningKey));
            LOGGER.log(Level.FINEST, "Client Sealing Key derieved from the session key: [{0}]", dumpString(clientSealingKey));
            LOGGER.log(Level.FINEST, "Server Signing Key derieved from the session key: [{0}]", dumpString(serverSigningKey));
            LOGGER.log(Level.FINEST, "Server Sealing Key derieved from the session key: [{0}]", dumpString(serverSealingKey));
        }
    }

    @Override
    public int getVerifierLength() {
        return NTLM1_VERIFIER_LENGTH;
    }

    @Override
    public int getAuthenticationService() {
        return NtlmAuthentication.AUTHENTICATION_SERVICE_NTLM;
    }

    @Override
    public int getProtectionLevel() {
        return protectionLevel;
    }

    @Override
    public void processIncoming(NetworkDataRepresentation ndr, int index, int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            final NdrBuffer buffer = ndr.getBuffer();
            final byte[] signingKey;
            final Cipher cipher;
            //reverse of what it is
            if (!isServer) {
                signingKey = serverSigningKey;
                cipher = serverCipher;
            } else {
                signingKey = clientSigningKey;
                cipher = clientCipher;
            }

            byte[] data = new byte[length];
            System.arraycopy(ndr.getBuffer().getBuffer(), index, data, 0, data.length);

            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
                data = cipher.update(data);
                System.arraycopy(data, 0, ndr.getBuffer().buf, index, data.length);
            }

            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info("AFTER Decryption");
                LOGGER.log(Level.FINEST, "\n{0}", Hexdump.toHexString(data));
                LOGGER.log(Level.FINEST, "\nLength is: {0}", data.length);
            }

            byte[] verifier = signingPt1(responseCounter, signingKey, buffer.getBuffer(), verifierIndex);
            signingPt2(verifier, cipher);

            buffer.setIndex(verifierIndex);
            //now read the next 16 bytes and pass compare them
            byte[] signing = new byte[16];
            ndr.readOctetArray(signing, 0, signing.length);

            //this should result in an access denied fault
            if (!Arrays.equals(verifier, signing)) {
                throw new IntegrityException("Message out of sequence. Perhaps the user being used to run this application is different from the one under which the COM server is running !.");
            }

            //only clients increment, servers just respond to the clients seq id.
//            if (!isServer || isFragmented)
//            {
//            	responseCounter++;
//            }
            responseCounter++;

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "", ex);
            throw ex;
        } catch (ShortBufferException | RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "", ex);
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

    @Override
    public void processOutgoing(NetworkDataRepresentation ndr, int index, int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();

            final byte[] signingKey;
            final Cipher cipher;
            if (isServer) {
                signingKey = serverSigningKey;
                cipher = serverCipher;
            } else {
                signingKey = clientSigningKey;
                cipher = clientCipher;
            }

            byte[] verifier = signingPt1(requestCounter, signingKey, buffer.getBuffer(), verifierIndex);
            byte[] data = new byte[length];
            System.arraycopy(ndr.getBuffer().getBuffer(), index, data, 0, data.length);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info("BEFORE Encryption");
                LOGGER.log(Level.FINEST, "\n{0}", Hexdump.toHexString(data));
                LOGGER.log(Level.INFO, "Length is: {0}", data.length);
            }

            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
                byte[] data2 = cipher.update(data);
                System.arraycopy(data2, 0, ndr.getBuffer().buf, index, data2.length);
            }
            signingPt2(verifier, cipher);

            buffer.setIndex(verifierIndex);
            buffer.writeOctetArray(verifier, 0, verifier.length);
            requestCounter++;

        } catch (ShortBufferException | RuntimeException ex) {
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

    private static String dumpString(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static byte[] generateClientSigningKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey) {
        byte[] dataforhash = new byte[secondarySessionKey.length + CLIENT_SIGNING_MAGIC_CONSTANT.length];
        System.arraycopy(secondarySessionKey, 0, dataforhash, 0, secondarySessionKey.length);
        System.arraycopy(CLIENT_SIGNING_MAGIC_CONSTANT, 0, dataforhash, secondarySessionKey.length, CLIENT_SIGNING_MAGIC_CONSTANT.length);
        return DigestHelper.md5(dataforhash);
    }

    private static byte[] generateClientSealingKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey) {
        byte[] dataforhash = new byte[secondarySessionKey.length + CLIENT_SEALING_MAGIC_CONSTANT.length];
        System.arraycopy(secondarySessionKey, 0, dataforhash, 0, secondarySessionKey.length);
        System.arraycopy(CLIENT_SEALING_MAGIC_CONSTANT, 0, dataforhash, secondarySessionKey.length, CLIENT_SEALING_MAGIC_CONSTANT.length);
        return DigestHelper.md5(dataforhash);
    }

    private static byte[] generateServerSigningKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey) {
        byte[] dataforhash = new byte[secondarySessionKey.length + SERVER_SIGNING_MAGIC_CONSTANT.length];
        System.arraycopy(secondarySessionKey, 0, dataforhash, 0, secondarySessionKey.length);
        System.arraycopy(SERVER_SIGNING_MAGIC_CONSTANT, 0, dataforhash, secondarySessionKey.length, SERVER_SIGNING_MAGIC_CONSTANT.length);
        return DigestHelper.md5(dataforhash);
    }

    private static byte[] generateServerSealingKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey) {
        byte[] dataforhash = new byte[secondarySessionKey.length + SERVER_SEALING_MAGIC_CONSTANT.length];
        System.arraycopy(secondarySessionKey, 0, dataforhash, 0, secondarySessionKey.length);
        System.arraycopy(SERVER_SEALING_MAGIC_CONSTANT, 0, dataforhash, secondarySessionKey.length, SERVER_SEALING_MAGIC_CONSTANT.length);
        return DigestHelper.md5(dataforhash);
    }

    private static byte[] signingPt1(int sequenceNumber, byte[] signingKey, byte[] data, int lengthOfBuffer) {
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

    private static void signingPt2(byte[] verifier, Cipher rc4) throws ShortBufferException {
        rc4.update(verifier, 4, 8, verifier, 4);
    }
}
