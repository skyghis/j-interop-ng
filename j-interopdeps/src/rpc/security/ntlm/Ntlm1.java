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
import gnu.crypto.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import jcifs.ntlmssp.NtlmFlags;
import ndr.NdrBuffer;
import ndr.NetworkDataRepresentation;
import rpc.IntegrityException;
import rpc.Security;

public class Ntlm1 implements NtlmFlags, Security {

    private static final int NTLM1_VERIFIER_LENGTH = 16;

    private IRandom clientCipher = null;
    private IRandom serverCipher = null;
    private byte[] clientSigningKey = null;
    private byte[] serverSigningKey = null;
    private NTLMKeyFactory keyFactory = null;
    private boolean isServer = false;
    private int protectionLevel;

    private int requestCounter = 0;
    private int responseCounter = 0;

    private static final Logger logger = Logger.getLogger("org.jinterop");

    public Ntlm1(int flags, byte[] sessionKey, boolean isServer)  {

        protectionLevel = ((flags & NTLMSSP_NEGOTIATE_SEAL) != 0) ?
                PROTECTION_LEVEL_PRIVACY : PROTECTION_LEVEL_INTEGRITY;

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

		 if (logger.isLoggable(Level.FINEST))
 	    {
			 logger.finest("Client Signing Key derieved from the session key: [" + Util.dumpString(clientSigningKey) + "]");
			 logger.finest("Client Sealing Key derieved from the session key: [" + Util.dumpString(clientSealingKey) + "]");
			 logger.finest("Server Signing Key derieved from the session key: [" + Util.dumpString(serverSigningKey) + "]");
			 logger.finest("Server Sealing Key derieved from the session key: [" + Util.dumpString(serverSealingKey) + "]");
 	    }
    }

    public int getVerifierLength() {
        return NTLM1_VERIFIER_LENGTH;
    }

    public int getAuthenticationService() {
        return NtlmAuthentication.AUTHENTICATION_SERVICE_NTLM;
    }

    public int getProtectionLevel() {
        return protectionLevel;
    }

    public void processIncoming(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();

            byte[] signingKey = null;
            IRandom cipher = null;

            //reverse of what it is
            if (!isServer)
            {
            	signingKey = serverSigningKey;
            	cipher = serverCipher;
            }
            else
            {
            	signingKey = clientSigningKey;
            	cipher = clientCipher;
            }

            byte[] data = new byte[length];
            System.arraycopy(ndr.getBuffer().getBuffer(),index,data, 0, data.length);

            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
            	data = keyFactory.applyARCFOUR(cipher, data);
            	System.arraycopy(data, 0, ndr.getBuffer().buf, index, data.length);
            }


            if (logger.isLoggable(Level.FINEST))
    	    {
				logger.finest("\n AFTER Decryption");
    	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	        jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), data, 0, data.length);
    	        logger.finest("\n" + byteArrayOutputStream.toString());
    	        logger.finest("\nLength is: " + data.length);
    	    }



            byte[] verifier = keyFactory.signingPt1(responseCounter, signingKey, buffer.getBuffer(),verifierIndex);
            keyFactory.signingPt2(verifier, cipher);

            buffer.setIndex(verifierIndex);
            //now read the next 16 bytes and pass compare them
            byte[] signing = new byte[16];
            ndr.readOctetArray(signing, 0, signing.length);

            //this should result in an access denied fault
            if (!keyFactory.compareSignature(verifier, signing))
            {
            	throw new IntegrityException("Message out of sequence. Perhaps the user being used to run this application is different from the one under which the COM server is running !.");
            }

            //only clients increment, servers just respond to the clients seq id.
//            if (!isServer || isFragmented)
//            {
//            	responseCounter++;
//            }

            responseCounter++;


        } catch (IOException ex) {
        	logger.log(Level.SEVERE, "", ex);
            throw ex;
        } catch (Exception ex) {
        	logger.log(Level.SEVERE, "", ex);
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

    public void processOutgoing(NetworkDataRepresentation ndr, int index,
            int length, int verifierIndex, boolean isFragmented) throws IOException {
        try {
            NdrBuffer buffer = ndr.getBuffer();

            byte[] signingKey = null;
            IRandom cipher = null;
            if (isServer)
            {
            	signingKey = serverSigningKey;
            	cipher = serverCipher;
            }
            else
            {
            	signingKey = clientSigningKey;
            	cipher = clientCipher;
            }

            byte[] verifier = keyFactory.signingPt1(requestCounter, signingKey, buffer.getBuffer(),verifierIndex);
            byte[] data = new byte[length];
            System.arraycopy(ndr.getBuffer().getBuffer(),index,data, 0, data.length);
            if (logger.isLoggable(Level.FINEST))
    	    {
				logger.finest("\n BEFORE Encryption");
			    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	        jcifs.util.Hexdump.hexdump(new PrintStream(byteArrayOutputStream), data, 0, data.length);
    	        logger.finest("\n" + byteArrayOutputStream.toString());
    	        logger.finest("\n Length is: " + data.length);
    	    }


            if (getProtectionLevel() == PROTECTION_LEVEL_PRIVACY) {
            	byte[] data2 = keyFactory.applyARCFOUR(cipher, data);
            	System.arraycopy(data2, 0, ndr.getBuffer().buf, index, data2.length);
            }
            keyFactory.signingPt2(verifier, cipher);
            buffer.setIndex(verifierIndex);
            buffer.writeOctetArray(verifier, 0, verifier.length);


//            if (isServer && !isFragmented)
//            {
//            	responseCounter++;
//            }

            requestCounter++;


        } catch (Exception ex) {
            throw new IntegrityException("General error: " + ex.getMessage());
        }
    }

}
