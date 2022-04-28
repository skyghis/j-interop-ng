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
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.NtlmUtil;
import jcifs.util.Encdec;
import net.sourceforge.jtds.util.SSPIJNIClient;
import rpc.CifsContextSingleton;
import rpc.Security;

public class NtlmAuthentication {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final String DEFAULT_WORKSTATION = CifsContextSingleton.instance().getNameServiceClient().getLocalHost().getHostName();
    public static final int AUTHENTICATION_SERVICE_NTLM = 10;
    private static final boolean UNICODE_SUPPORTED = CifsContextSingleton.instance().getConfig().isUseUnicode();
    private static final int BASIC_FLAGS
            = NtlmFlags.NTLMSSP_REQUEST_TARGET
            | NtlmFlags.NTLMSSP_NEGOTIATE_NTLM
            | NtlmFlags.NTLMSSP_NEGOTIATE_OEM
            | NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN
            | (UNICODE_SUPPORTED ? NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE : 0);
    private Security security;
    protected Properties properties;
    private NtlmPasswordAuthenticator credentials;
    private AuthenticationSource authenticationSource;
    private boolean lanManagerKey;
    private boolean seal;
    private boolean sign;
    private boolean keyExchange;
    //we always go for 128
    private int keyLength = 128;
    private boolean useNtlm2sessionsecurity = false;
    private boolean useNtlmV2 = false;
    private boolean useSSO = false;
    private static final Random RANDOM = new Random();
    private final SSPIJNIClient jniClient;

    public NtlmAuthentication(Properties properties) {
        this.properties = properties;
        String domain = null;
        String user = null;
        String password = null;
        if (properties != null) {
            lanManagerKey = Boolean.valueOf(properties.getProperty("rpc.ntlm.lanManagerKey"));
            seal = Boolean.valueOf(properties.getProperty("rpc.ntlm.seal"));
            sign = seal ? true : Boolean.valueOf(properties.getProperty("rpc.ntlm.sign"));
            keyExchange = Boolean.valueOf(properties.getProperty("rpc.ntlm.keyExchange"));
            String keyLength = properties.getProperty("rpc.ntlm.keyLength");
            if (keyLength != null) {
                try {
                    this.keyLength = Integer.parseInt(keyLength);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid key length: " + keyLength);
                }
            }

            useNtlm2sessionsecurity = Boolean.valueOf(properties.getProperty("rpc.ntlm.ntlm2"));
            useNtlmV2 = Boolean.valueOf(properties.getProperty("rpc.ntlm.ntlmv2"));
            useSSO = Boolean.valueOf(properties.getProperty("rpc.ntlm.sso"));
            domain = properties.getProperty("rpc.ntlm.domain");
            user = properties.getProperty(Security.USERNAME);
            password = properties.getProperty(Security.PASSWORD);
        }

        if (useSSO) {
            LOGGER.warning("Using deprecated (Windows only) SSO. Use password authentication instead.");
            jniClient = SSPIJNIClient.getInstance();
        } else {
            jniClient = null;
            credentials = new NtlmPasswordAuthenticator(domain, user, password);
        }
    }

    public Security getSecurity() throws IOException {
        return security;
    }

    protected AuthenticationSource getAuthenticationSource() {
        if (authenticationSource == null) {
            String sourceClass = (properties != null) ? properties.getProperty("rpc.ntlm.authenticationSource") : null;
            if (sourceClass == null) {
                authenticationSource = AuthenticationSource.getDefaultInstance();
            }
            if (authenticationSource == null) {
                try {

                    authenticationSource = (AuthenticationSource) Class.forName(sourceClass).newInstance();
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Invalid authentication source: " + ex);
                }
            }
        }
        return authenticationSource;
    }

    private int getDefaultFlags() {
        int flags = BASIC_FLAGS;
        if (lanManagerKey) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        }
        if (sign) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        }
        if (seal) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        }
        if (keyExchange) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        }
        if (keyLength >= 56) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_56;
        }
        if (keyLength >= 128) {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_128;
        }
        //We always negotiate for NTLM2 session security
//        if (useNtlm2sessionsecurity)
        {
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY;
        }

        return flags;
    }

    private int adjustFlags(int flags) {
        int adjustedFlags = flags;
        if (UNICODE_SUPPORTED && ((adjustedFlags & NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE) != 0)) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
            adjustedFlags |= NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
        } else {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
            adjustedFlags |= NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
        }
        if (!lanManagerKey) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        }
        if (!(sign || seal)) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        }
        if (!seal) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        }
        if (!keyExchange) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        }
        if (keyLength < 128) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_128;
        }
        if (keyLength < 56) {
            adjustedFlags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_56;
        }
//        if (!useNtlm2sessionsecurity)
//        {
//        	flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY;
//        }
        return adjustedFlags;
    }

    public Type1Message createType1() throws IOException {
        if (useSSO) {
            byte[] ntlmMessage = jniClient.invokePrepareSSORequest();
            Type1Message type1Message = new Type1Message(ntlmMessage);
            type1Message.setFlags(getDefaultFlags());
            return type1Message;
        } else {
            int flags = getDefaultFlags();
            return new Type1Message(CifsContextSingleton.instance(), flags, credentials.getUserDomain(), DEFAULT_WORKSTATION);
        }
    }

    public Type2Message createType2(Type1Message type1) throws IOException {
        int flags;
        if (type1 == null) {
            flags = getDefaultFlags();
        } else {
            flags = adjustFlags(type1.getFlags());
        }
        flags |= 0x00020000; //challenge accept response flag

        Type2Message type2Message = new Type2Message(CifsContextSingleton.instance(), flags,
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, //generate our own, since SMB will throw exception here
                credentials.getUserDomain());
        return type2Message;
    }

    public Type3Message createType3(Type2Message type2) throws IOException {
        if (useSSO) {
            byte[] ntlmMessage = type2.toByteArray();
            byte[] ret = jniClient.invokePrepareSSOSubmit(ntlmMessage);
            Type3Message message = new Type3Message(ret);
            int flags = type2.getFlags();
            if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE) != 0) {
                flags = adjustFlags(flags);
                flags &= ~0x00020000;
            }
            message.setFlags(flags);
            return message;
        } else {
            int flags = type2.getFlags();
            if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE) != 0) {
                flags = adjustFlags(flags);
                flags &= ~0x00020000;
            }

            Type3Message type3 = null;

            byte[] clientNonce = new byte[8];
            byte[] blob = null;

            String target = null;//getTargetFromTargetInformation(type2.getTargetInformation());

            if (target == null) {
                target = credentials.getUserDomain().toUpperCase();
                if (target.isEmpty()) {
                    target = getTargetFromTargetInformation(type2.getTargetInformation());
                }
            }

            if (useNtlmV2) {
                RANDOM.nextBytes(clientNonce);
                try {
                    byte[] lmv2Response = Responses.getLMv2Response(target, credentials.getUsername(), credentials.getPassword(), type2.getChallenge(), clientNonce);
                    byte[][] retval = Responses.getNTLMv2Response(target, credentials.getUsername(), credentials.getPassword(), type2.getTargetInformation(), type2.getChallenge(), clientNonce);
                    byte[] ntlmv2Response = retval[0];
                    blob = retval[1];
                    type3 = new Type3Message(flags, lmv2Response, ntlmv2Response, target, credentials.getUsername(), DEFAULT_WORKSTATION);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occured while forming NTLMv2 Type3Response", e);
                }

            } else if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY) != 0) //NTLM2 Session security response
            {
                flags = adjustFlags(flags);
                flags &= ~0x00020000;
                //flags =  0xe2888235;
                byte[] challenge = type2.getChallenge();
                //LMReponse is 24 bytes. 8 byte random client nonce and the rest is null padded.
                byte[] lmResponse = new byte[24];

                RANDOM.nextBytes(clientNonce);
                System.arraycopy(clientNonce, 0, lmResponse, 0, clientNonce.length);
                byte[] ntResponse;
                try {
                    ntResponse = Responses.getNTLM2SessionResponse(credentials.getPassword(), challenge, clientNonce);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occured while forming Session Security Type3Response", e);
                }
                type3 = new Type3Message(flags, lmResponse, ntResponse, target, credentials.getUsername(), DEFAULT_WORKSTATION);
            } else //Plain NTLMv1 response
            {
                final byte[] challenge = type2.getChallenge();
                final byte[] lmResponse;
                final byte[] ntResponse;
                try {
                    lmResponse = NtlmUtil.getPreNTLMResponse(CifsContextSingleton.instance(), credentials.getPassword(), challenge);
                    ntResponse = NtlmUtil.getNTLMResponse(credentials.getPassword(), challenge);
                } catch (GeneralSecurityException ex) {
                    throw new IOException(ex);
                }
                type3 = new Type3Message(flags, lmResponse, ntResponse,
                        target, credentials.getUsername(),
                        null);
                if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH) != 0) {
                    throw new RuntimeException("Key Exchange not supported by Library !");
                }
            }
            //we have to now form lmv2 and ntlmv2 response with regards to the session security
            //the type3message also has to be altered
            if (useNtlm2sessionsecurity && (flags & NtlmFlags.NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY) != 0) {
                byte[] userSessionKey;
                if (useNtlmV2) {
                    try {
                        userSessionKey = NTLMKeyFactory.getNTLMv2UserSessionKey(target, credentials.getUsername(), credentials.getPassword(), type2.getChallenge(), blob);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception occured while forming NTLMv2 with NTLM2 Session Security for Type3Response", e);
                    }
                } else {
                    //now create the key for the session
                    //this key will be used to RC4 a 16 byte random key and set to the type3 message
                    byte[] servernonce = new byte[16];
                    System.arraycopy(type2.getChallenge(), 0, servernonce, 0, type2.getChallenge().length);
                    System.arraycopy(clientNonce, 0, servernonce, 8, clientNonce.length);
                    try {
                        userSessionKey = NTLMKeyFactory.getNTLM2SessionResponseUserSessionKey(credentials.getPassword(), servernonce);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception occured while forming Session Security for Type3Response", e);
                    }

                }

                try {
                    //now RC4 encrypt a random 16 byte key
                    byte[] secondayMasterKey = NTLMKeyFactory.getSecondarySessionKey();
                    type3.setEncryptedSessionKey(encryptSecondarySessionKey(secondayMasterKey, userSessionKey));
                    security = new Ntlm1(flags, secondayMasterKey, false);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occured while forming Session Security for Type3Response", e);
                }
            }

            return type3;
        }
    }

    private String getTargetFromTargetInformation(byte[] targetInformation) {
        String target = null;

        int i = 0;
        while (i < targetInformation.length) {
            switch (Encdec.dec_uint16le(targetInformation, i)) {
                case 1: //Server name
                    i++;
                    i++; //advance two bytes
                    int length = Encdec.dec_uint16le(targetInformation, i);
                    i++;
                    i++;//advance two bytes
                    byte[] domainb = new byte[length];
                    System.arraycopy(targetInformation, i, domainb, 0, length);
                    target = new String(domainb, StandardCharsets.UTF_16LE);
                    i = targetInformation.length;
                    break;
                default: //skip bytes
                    i++;
                    i++; //advance two bytes
                    length = Encdec.dec_uint16le(targetInformation, i);
                    i++;
                    i++;//advance two bytes
                    i += length;
            }
        }

        return target;
    }

    void createSecurityWhenServer(NtlmMessage type3) {
        Type3Message type3Message = (Type3Message) type3;
        //two things here...check for anonymous , in that case the user response key is new byte[16].
        //in case anonymous has not been sent then create the key using credentials.
        int flags = type3Message.getFlags();
        byte[] secondayMasterKey;
        byte[] sessionResponseUserSessionKey = null;
        if (type3Message.getFlag(0x00000800))//anonymous flag
        {
            //if it is anonymous the user session key is new byte[16];
            sessionResponseUserSessionKey = new byte[16];
        } else if (useNtlmV2) {
            //TODO this needs to be checked here since the key logic will be totally different
            //and we have to get the key out of Type3 message response (blob of the NTLMv2 response.)
            int h = 0;
        } else {
            //now create the key for the session
            //this key will be used to RC4 a 16 byte random key and set to the type3 message
            byte[] servernonce = new byte[16];
            byte[] challenge = new byte[]{1, 2, 3, 4, 5, 6, 7, 8}; //challenge is fixed
            System.arraycopy(challenge, 0, servernonce, 0, challenge.length);
            System.arraycopy(type3Message.getLMResponse(), 0, servernonce, 8, 8);//first 8 bytes only , the rest are all 0x00 and not required.
            try {
                sessionResponseUserSessionKey = NTLMKeyFactory.getNTLM2SessionResponseUserSessionKey(credentials.getPassword(), servernonce);
            } catch (Exception e) {
                throw new RuntimeException("Exception occured while forming Session Security from Type3 AUTH", e);
            }
        }

        try {
            //now RC4 decrypt the session key
            secondayMasterKey = decryptSecondarySessionKey(type3Message.getEncryptedSessionKey(), sessionResponseUserSessionKey);
            security = new Ntlm1(flags, secondayMasterKey, true);
        } catch (Exception e) {
            throw new RuntimeException("Exception occured while forming Session Security Type3Response", e);
        }
    }

    private static byte[] decryptSecondarySessionKey(byte[] encryptedData, byte[] key) {
        return NTLMKeyFactory.applyARCFOUR(NTLMKeyFactory.getARCFOUR(key), encryptedData);
    }

    private static byte[] encryptSecondarySessionKey(byte[] plainData, byte[] key) {
        return NTLMKeyFactory.applyARCFOUR(NTLMKeyFactory.getARCFOUR(key), plainData);
    }
}
