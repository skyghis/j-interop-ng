/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc.security.ntlm;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import jcifs.Config;
import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.smb.NtlmPasswordAuthentication;
import rpc.Security;

public class NtlmAuthentication {

    public static final int AUTHENTICATION_SERVICE_NTLM = 10;

   

    private static final boolean UNICODE_SUPPORTED =
            Config.getBoolean("jcifs.smb.client.useUnicode", true);

   
    private static final int BASIC_FLAGS = NtlmFlags.NTLMSSP_NEGOTIATE_NTLM |
            NtlmFlags.NTLMSSP_NEGOTIATE_OEM |
                    NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN |
                            (UNICODE_SUPPORTED ?
                                    NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE : 0);

    
    private Security security; 
    
    protected Properties properties;

    private NtlmPasswordAuthentication credentials;
    
    private AuthenticationSource authenticationSource;

    private boolean lanManagerKey;

    private boolean seal;

    private boolean sign;

    private boolean keyExchange;

    private int keyLength = 40;
    
    private boolean usentlmv2 = false;
    
    private static final Random RANDOM = new Random();

    public NtlmAuthentication(Properties properties) {
        this.properties = properties;
        String domain = null;
        String user = null;
        String password = null;
        if (properties != null) {
            lanManagerKey = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.lanManagerKey")).booleanValue();
            seal = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.seal")).booleanValue();
            sign = seal ? true : Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.sign")).booleanValue();
            keyExchange = Boolean.valueOf(properties.getProperty(
                    "rpc.ntlm.keyExchange")).booleanValue();
            String keyLength = properties.getProperty("rpc.ntlm.keyLength");
            if (keyLength != null) {
                try {
                    this.keyLength = Integer.parseInt(keyLength);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid key length: " +
                            keyLength);
                }
            }

            usentlmv2 = Boolean.valueOf(properties.getProperty(
            "rpc.ntlm.ntlm2")).booleanValue();	
            
            domain = properties.getProperty("rpc.ntlm.domain");
            user = properties.getProperty(Security.USERNAME);
            password = properties.getProperty(Security.PASSWORD);
        }
        credentials = new NtlmPasswordAuthentication(domain, user, password);
        
        
    }

    public Security getSecurity() throws IOException 
    {
       return security;
    }

    protected AuthenticationSource getAuthenticationSource() {
        if (authenticationSource != null) return authenticationSource;
        String sourceClass = (properties != null) ?
                properties.getProperty("rpc.ntlm.authenticationSource") : null;
        if (sourceClass == null) {
            return (authenticationSource =
                    AuthenticationSource.getDefaultInstance());
        }
        try {
            return (authenticationSource = (AuthenticationSource)
                    Class.forName(sourceClass).newInstance());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Invalid authentication source: " + ex);
        }
    }

    private int getDefaultFlags() {
        int flags = BASIC_FLAGS;
        if (lanManagerKey) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        if (sign) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        if (seal) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        if (keyExchange) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        if (keyLength >= 56) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_56;
        if (keyLength >= 128) flags |= NtlmFlags.NTLMSSP_NEGOTIATE_128;
        if (usentlmv2)
        {
        	flags |= NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2;
        }
        
        return flags;
    }

    private int adjustFlags(int flags) {
        if (UNICODE_SUPPORTED &&
                ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE) != 0)) {
            flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
        } else {
            flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;
            flags |= NtlmFlags.NTLMSSP_NEGOTIATE_OEM;
        }
        if (!lanManagerKey) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_LM_KEY;
        if (!(sign || seal)) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SIGN;
        if (!seal) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_SEAL;
        if (!keyExchange) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH;
        if (keyLength < 128) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_128;
        if (keyLength < 56) flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_56;
        if (!usentlmv2)
        {
        	flags &= ~NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2;
        }
        return flags;
    }

    public Type1Message createType1() throws IOException {
        int flags = getDefaultFlags();
        return new Type1Message(flags, credentials.getDomain(),Type1Message.getDefaultWorkstation());
    }

    public Type2Message createType2(Type1Message type1) throws IOException {
        int flags;
        if (type1 == null) {
            flags = getDefaultFlags();
        } else {
            flags = adjustFlags(type1.getFlags());
        }
        flags |= 0x00020000; //challenge accept response flag
        
        Type2Message type2Message = new Type2Message(flags,
                    new byte[]{1,2,3,4,5,6,7,8}, //generate our own, since SMB will throw exception here
                    credentials.getDomain());
        
//        String domainName = InetAddress.getByName(credentials.getDomain()).getHostName();
//        byte[] domain = new byte[0];
//        if (domainName != null) {
//            try {
//                domain = domainName.getBytes("UnicodeLittleUnmarked");
//            } catch (IOException ex) { }
//        }
//        int domainLength = domain.length;
//        byte[] server = new byte[0];
////        try {
//        	//TODO VIKRAM just temporary
//            String host = domainName;
//            if (host != null) {
//                try {
//                    server = host.getBytes("UnicodeLittleUnmarked");
//                } catch (IOException ex) { }
//            }
////        } catch (UnknownHostException ex) { }
//        int serverLength = server.length;
//        byte[] targetInfo = new byte[(domainLength > 0 ? domainLength + 4 : 0) +
//                (serverLength > 0 ? serverLength + 4 : 0) + 4];
//        int offset = 0;
//        if (domainLength > 0) {
//            writeUShort(targetInfo, offset, 2);
//            offset += 2;
//            writeUShort(targetInfo, offset, domainLength);
//            offset += 2;
//            System.arraycopy(domain, 0, targetInfo, offset, domainLength);
//            offset += domainLength;
//        }
//        if (serverLength > 0) {
//            writeUShort(targetInfo, offset, 1);
//            offset += 2;
//            writeUShort(targetInfo, offset, serverLength);
//            offset += 2;
//            System.arraycopy(server, 0, targetInfo, offset, serverLength);
//        }
//        
//        type2Message.setTargetInformation(targetInfo);
        
        return type2Message;
    }

//    private void writeUShort(byte[] dest, int offset, int ushort) {
//        dest[offset] = (byte) (ushort & 0xff);
//        dest[offset + 1] = (byte) (ushort >> 8 & 0xff);
//    }
    
    public Type3Message createType3(Type2Message type2) throws IOException {
        int flags = type2.getFlags();
        if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_DATAGRAM_STYLE) != 0) 
        {
            flags = adjustFlags(flags);
            flags &= ~0x00020000;
        }
        
        Type3Message type3 = null;
       
        //we have to now form lmv2 and ntlmv2 response with regards to the session security
        //the type3message also has to be altered
        if (usentlmv2)
        {
         	flags = adjustFlags(flags);
            flags &= ~0x00020000;
        	//flags =  0xe2888235;
        	byte[] challenge = type2.getChallenge();
        	//LMReponse is 24 bytes. 8 byte random client nonce and the rest is null padded.
            byte[] lmResponse = new byte[24];
            byte[] clientNonce = new byte[8];
            RANDOM.nextBytes(clientNonce);
            System.arraycopy(clientNonce, 0, lmResponse, 0, clientNonce.length);
            byte[] ntResponse;
			try {
				ntResponse = Responses.getNTLM2SessionResponse(credentials.getPassword(), challenge, clientNonce);
			} catch (Exception e)
			{
				throw new RuntimeException("Exception occured while forming Session Security Type3Response",e);
			}
            
            type3 = new Type3Message(flags, lmResponse, ntResponse,
                   credentials.getDomain(), credentials.getUsername(),
                    Type3Message.getDefaultWorkstation());
            NTLMKeyFactory ntlmKeyFactory = new NTLMKeyFactory();
            //now create the key for the session 
            //this key will be used to RC4 a 16 byte random key and set to the type3 message 
            byte[] servernonce = new byte[16];
            System.arraycopy(challenge, 0, servernonce, 0, challenge.length);
            System.arraycopy(clientNonce, 0, servernonce, 8, clientNonce.length);
            byte[] sessionResponseUserSessionKey;
            try {
				sessionResponseUserSessionKey = ntlmKeyFactory.getNTLM2SessionResponseUserSessionKey(credentials.getPassword(), servernonce);
				//now RC4 encrypt a random 16 byte key
				byte[] secondayMasterKey = ntlmKeyFactory.getSecondarySessionKey();
				type3.setSessionKey(ntlmKeyFactory.encryptSecondarySessionKey(secondayMasterKey, sessionResponseUserSessionKey));
				security = (Security) new Ntlm1(flags, secondayMasterKey,false);
			} catch (Exception e)
			{
				throw new RuntimeException("Exception occured while forming Session Security for Type3Response",e);
			}
            
        }
        else
        {
        
        	byte[] challenge = type2.getChallenge();
            byte[] lmResponse = NtlmPasswordAuthentication.getPreNTLMResponse(
                    credentials.getPassword(), challenge);
            byte[] ntResponse = NtlmPasswordAuthentication.getNTLMResponse(
                    credentials.getPassword(), challenge);
            type3 = new Type3Message(flags, lmResponse, ntResponse,
                    credentials.getDomain(), credentials.getUsername(),
                            Type3Message.getDefaultWorkstation());
            if ((flags & NtlmFlags.NTLMSSP_NEGOTIATE_KEY_EXCH) != 0) {
            	throw new RuntimeException("Key Exchange not supported by Library !");
            }
        }
        
        
        
        return type3;
    }

    void createSecurityWhenServer(NtlmMessage type3)
    {
    	Type3Message type3Message = (Type3Message)type3;
    	//two things here...check for anonymous , in that case the user response key is new byte[16].
    	//in case anonymous has not been sent then create the key using credentials.
    	int flags = type3Message.getFlags();
    	NTLMKeyFactory ntlmKeyFactory = new NTLMKeyFactory();
    	byte[] secondayMasterKey;
    	byte[] sessionResponseUserSessionKey;
    	if (type3Message.getFlag(0x00000800))//anonymous flag
    	{
    		//if it is anonymous the user session key is new byte[16];
    		sessionResponseUserSessionKey = new byte[16];
    	}
    	else
    	{
    		 //now create the key for the session 
            //this key will be used to RC4 a 16 byte random key and set to the type3 message 
            byte[] servernonce = new byte[16];
            byte[] challenge = new byte[]{1,2,3,4,5,6,7,8}; //challenge is fixed
            System.arraycopy(challenge, 0, servernonce, 0, challenge.length);
            System.arraycopy(type3Message.getLMResponse(), 0, servernonce, 8, 8);//first 8 bytes only , the rest are all 0x00 and not required.
            try {
				sessionResponseUserSessionKey = ntlmKeyFactory.getNTLM2SessionResponseUserSessionKey(credentials.getPassword(), servernonce);
			} catch (Exception e) {
				throw new RuntimeException("Exception occured while forming Session Security from Type3 AUTH",e);
			}
    	}
    	
    	try {
			//now RC4 decrypt the session key
    		secondayMasterKey = ntlmKeyFactory.decryptSecondarySessionKey(type3Message.getSessionKey(), sessionResponseUserSessionKey);
			security = (Security) new Ntlm1(flags, secondayMasterKey,true);
		} catch (Exception e)
		{
			throw new RuntimeException("Exception occured while forming Session Security Type3Response",e);
		}
    }

}
