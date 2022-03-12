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

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.ntlmssp.NtlmFlags;
import jcifs.util.Hexdump;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.IntegrityException;
import rpc.Security;

public class Ntlm1 implements NtlmFlags, Security {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final int NTLM1_VERIFIER_LENGTH = 16;
    private IRandom clientCipher = null;
    private IRandom serverCipher = null;
    private byte[] clientSigningKey = null;
    private byte[] serverSigningKey = null;
    private NTLMKeyFactory keyFactory = null;
    private boolean isServer = false;
    private final int protectionLevel;
    private int requestCounter = 0;
    private int responseCounter = 0;

    public Ntlm1(int flags, byte[] sessionKey, boolean isServer) {

        protectionLevel = ((flags & NTLMSSP_NEGOTIATE_SEAL) != 0)
                ? PROTECTION_LEVEL_PRIVACY : PROTECTION_LEVEL_INTEGRITY;

        this.isServer = isServer;
        keyFactory = new NTLMKeyFactory();
        clientSigningKey = keyFactory.generateClientSigningKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        byte[] clientSealingKey = keyFactory.generateClientSealingKeyUsingNegotiatedSecondarySessionKey(sessionKey);

        serverSigningKey = keyFactory.generateServerSigningKeyUsingNegotiatedSecondarySessionKey(sessionKey);
        byte[] serverSealingKey = keyFactory.generateServerSealingKeyUsingNegotiatedSecondarySessionKey(sessionKey);

        //Used by the server to decrypt client messages
        clientCipher = keyFactory.getARCFOUR(clientSealingKey);

        //Used by the client to decrypt server messages
        serverCipher = keyFactory.getARCFOUR(serverSealingKey);

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
    public void processIncoming(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();

            byte[] signingKey;
            IRandom cipher;

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
                data = keyFactory.applyARCFOUR(cipher, data);
                System.arraycopy(data, 0, ndr.getBuffer().buf, index, data.length);
            }

            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info("AFTER Decryption");
                LOGGER.log(Level.FINEST, "\n{0}", Hexdump.toHexString(data));
                LOGGER.log(Level.FINEST, "\nLength is: {0}", data.length);
            }

            byte[] verifier = keyFactory.signingPt1(responseCounter, signingKey, buffer.getBuffer(), verifierIndex);
            keyFactory.signingPt2(verifier, cipher);

            buffer.setIndex(verifierIndex);
            //now read the next 16 bytes and pass compare them
            byte[] signing = new byte[16];
            ndr.readOctetArray(signing, 0, signing.length);

            //this should result in an access denied fault
            if (!keyFactory.compareSignature(verifier, signing)) {
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
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "", ex);
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

    @Override
    public void processOutgoing(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();

            byte[] signingKey;
            IRandom cipher;
            if (isServer) {
                signingKey = serverSigningKey;
                cipher = serverCipher;
            } else {
                signingKey = clientSigningKey;
                cipher = clientCipher;
            }

            byte[] verifier = keyFactory.signingPt1(requestCounter, signingKey, buffer.getBuffer(), verifierIndex);
            byte[] data = new byte[length];
            System.arraycopy(ndr.getBuffer().getBuffer(), index, data, 0, data.length);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info("BEFORE Encryption");
                LOGGER.log(Level.FINEST, "\n{0}", Hexdump.toHexString(data));
                LOGGER.log(Level.INFO, "Length is: {0}", data.length);
            }

            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
                byte[] data2 = keyFactory.applyARCFOUR(cipher, data);
                System.arraycopy(data2, 0, ndr.getBuffer().buf, index, data2.length);
            }
            keyFactory.signingPt2(verifier, cipher);
            buffer.setIndex(verifierIndex);
            buffer.writeOctetArray(verifier, 0, verifier.length);
            requestCounter++;

        } catch (LimitReachedException | NoSuchAlgorithmException | RuntimeException ex) {
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String dumpString(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
