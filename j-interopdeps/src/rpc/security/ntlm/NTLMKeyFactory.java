package rpc.security.ntlm;


import gnu.crypto.hash.MD4;
import gnu.crypto.hash.MD5;
import gnu.crypto.prng.ARCFour;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


class NTLMKeyFactory {

	Random random = new Random();
	
	private static final byte[] clientSigningMagicConstant = new byte[]{0x73,0x65,0x73,0x73,0x69,0x6f,0x6e,0x20,0x6b,0x65,0x79,0x20,0x74,0x6f,0x20,0x63,0x6c,0x69,0x65,0x6e,0x74,0x2d,0x74,0x6f,0x2d,0x73,0x65,0x72,0x76,0x65,0x72,0x20,0x73
		,0x69,0x67,0x6e,0x69,0x6e,0x67,0x20,0x6b,0x65,0x79,0x20,0x6d,0x61,0x67,0x69,0x63,0x20,0x63,0x6f,0x6e,0x73,0x74,0x61,0x6e,0x74,0x00};//"session key to client-to-server signing key magic constant";
	private static final byte[] serverSigningMagicConstant = new byte[]{0x73,0x65,0x73,0x73,0x69,0x6f,0x6e,0x20,0x6b,0x65,0x79,0x20,0x74,0x6f,0x20,0x73,0x65,0x72,0x76,0x65,0x72,0x2d,0x74,0x6f,0x2d,0x63,0x6c,0x69,0x65,0x6e,0x74,0x20,0x73
		,0x69,0x67,0x6e,0x69,0x6e,0x67,0x20,0x6b,0x65,0x79,0x20,0x6d,0x61,0x67,0x69,0x63,0x20,0x63,0x6f,0x6e,0x73,0x74,0x61,0x6e,0x74,0x00};//"session key to server-to-client signing key magic constant";
	private static final byte[] clientSealingMagicConstant = new byte[]{0x73,0x65,0x73,0x73,0x69,0x6f,0x6e,0x20,0x6b,0x65,0x79,0x20,0x74,0x6f,0x20,0x63,0x6c,0x69,0x65,0x6e,0x74,0x2d,0x74,0x6f,0x2d,0x73,0x65,0x72,0x76,0x65,0x72,0x20,0x73
		,0x65,0x61,0x6c,0x69,0x6e,0x67,0x20,0x6b,0x65,0x79,0x20,0x6d,0x61,0x67,0x69,0x63,0x20,0x63,0x6f,0x6e,0x73,0x74,0x61,0x6e,0x74,0x00};//"session key to client-to-server sealing key magic constant";
	private static final byte[] serverSealingMagicConstant = new byte[]{0x73,0x65,0x73,0x73,0x69,0x6f,0x6e,0x20,0x6b,0x65,0x79,0x20,0x74,0x6f,0x20,0x73,0x65,0x72,0x76,0x65,0x72,0x2d,0x74,0x6f,0x2d,0x63,0x6c,0x69,0x65,0x6e,0x74,0x20,0x73
		,0x65,0x61,0x6c,0x69,0x6e,0x67,0x20,0x6b,0x65,0x79,0x20,0x6d,0x61,0x67,0x69,0x63,0x20,0x63,0x6f,0x6e,0x73,0x74,0x61,0x6e,0x74,0x00};//"session key to server-to-client sealing key magic constant";
	
	NTLMKeyFactory()
	{
		
	}
	
	
	/** NTLMv1 User Session Key. Cases where LMcompatibilitylevel is 0,1,2. For 3,4,5 the logic is different
	 * and based upon the reponses being sent back (either LMv2 or NTLMv2)
	 * 
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws DigestException 
	 */
	byte[] getNTLMUserSessionKey(String password) throws UnsupportedEncodingException, DigestException
	{
		//look at NTLMPasswordAuthentication in jcifs. It supports only the NTLMUserSessionKey and the LMv2UserSessionKey...we need more :(
		 byte key[] = new byte[16];
		 byte[] ntlmHash = Responses.ntlmHash(password);
		 MD4 md4 = new MD4();
	     md4.update(ntlmHash,0,ntlmHash.length); 
	     key = md4.digest(); 
	     return key;
	}

	/** Password of the user 
	 * 
	 * @param password
	 * @param servernonce challenge + nonce from NTLM2 Session Response
	 * @return
	 * @throws DigestException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	byte[] getNTLM2SessionResponseUserSessionKey(String password, byte[] servernonce) throws NoSuchAlgorithmException, UnsupportedEncodingException, DigestException
	{
		return Responses.hmacMD5(servernonce, getNTLMUserSessionKey(password));
	}
	
	/** Randomly generated 16 bytes
	 * 
	 * @return
	 */
	byte[] getSecondarySessionKey()
	{
		byte[] key = new byte[16];
		random.nextBytes(key);
		return key;
	}
	
	IRandom getARCFOUR(byte[] key)
	{
		HashMap attrib = new HashMap();
		IRandom keystream = new ARCFour();
		attrib.put(ARCFour.ARCFOUR_KEY_MATERIAL, key);
		keystream.init(attrib);
		return keystream;
	}
	
	byte[] applyARCFOUR(IRandom keystream, byte[] data) throws IllegalStateException, LimitReachedException
	{
		byte[] retData = new byte[data.length]; 
		
		
		for (int i = 0; i < data.length; i++) {
		   retData[i] = (byte) (data[i] ^ keystream.nextByte());
		}
		
		return retData;
	}
	
	byte[] decryptSecondarySessionKey(byte[] encryptedData, byte[] key) throws IllegalStateException, LimitReachedException
	{
		return applyARCFOUR(getARCFOUR(key),encryptedData);
	}
	
	byte[] encryptSecondarySessionKey(byte[] plainData, byte[] key) throws IllegalStateException, LimitReachedException
	{
		return applyARCFOUR(getARCFOUR(key),plainData);
	}
	
	byte[] generateClientSigningKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey)
	{
		//TODO this can be moved out of here...
		byte[] dataforhash = new byte[secondarySessionKey.length + clientSigningMagicConstant.length];
		System.arraycopy(secondarySessionKey, 0, dataforhash , 0, secondarySessionKey.length);
		System.arraycopy(clientSigningMagicConstant, 0, dataforhash , secondarySessionKey.length, clientSigningMagicConstant.length);
		MD5 md5 = new MD5();
		md5.update(dataforhash, 0, dataforhash.length);
		return md5.digest();
	}
	
	byte[] generateClientSealingKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey)
	{
		//TODO this can be moved out of here...
		byte[] dataforhash = new byte[secondarySessionKey.length + clientSealingMagicConstant.length];
		System.arraycopy(secondarySessionKey, 0, dataforhash , 0, secondarySessionKey.length);
		System.arraycopy(clientSealingMagicConstant, 0, dataforhash , secondarySessionKey.length, clientSealingMagicConstant.length);
		MD5 md5 = new MD5();
		md5.update(dataforhash, 0, dataforhash.length);
		return md5.digest();
	}
	
	byte[] generateServerSigningKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey)
	{
		//TODO this can be moved out of here...
		byte[] dataforhash = new byte[secondarySessionKey.length + serverSigningMagicConstant.length];
		System.arraycopy(secondarySessionKey, 0, dataforhash , 0, secondarySessionKey.length);
		System.arraycopy(serverSigningMagicConstant, 0, dataforhash , secondarySessionKey.length, serverSigningMagicConstant.length);
		MD5 md5 = new MD5();
		md5.update(dataforhash, 0, dataforhash.length);
		return md5.digest();
	}
	
	byte[] generateServerSealingKeyUsingNegotiatedSecondarySessionKey(byte[] secondarySessionKey)
	{
		//TODO this can be moved out of here...
		byte[] dataforhash = new byte[secondarySessionKey.length + serverSealingMagicConstant.length];
		System.arraycopy(secondarySessionKey, 0, dataforhash , 0, secondarySessionKey.length);
		System.arraycopy(serverSealingMagicConstant, 0, dataforhash , secondarySessionKey.length, serverSealingMagicConstant.length);
		MD5 md5 = new MD5();
		md5.update(dataforhash, 0, dataforhash.length);
		return md5.digest();
	}
	
	//TODO merge the signing routine for both client and server all that they differ by are keys...as expected
	byte[] signingPt1(int sequenceNumber, byte[] signingKey, byte[] data, int lengthOfBuffer) throws NoSuchAlgorithmException, IllegalStateException, LimitReachedException
	{
		byte[] seqNumPlusData = new byte[4 + lengthOfBuffer];
		
		seqNumPlusData[0] = (byte)(sequenceNumber & 0xFF);
		seqNumPlusData[1] = (byte)((sequenceNumber >> 8) & 0xFF);
		seqNumPlusData[2] = (byte)((sequenceNumber >> 16) & 0xFF);
		seqNumPlusData[3] = (byte)((sequenceNumber >> 24) & 0xFF);
		
		System.arraycopy(data, 0, seqNumPlusData, 4, lengthOfBuffer);
		
		byte[] retval = new byte[16];
		retval[0] = 0x01; //Version number LE 1.
		
		byte[] sign = Responses.hmacMD5(seqNumPlusData, signingKey);
		
		for (int i = 0; i < 8; i++) {
			retval[i+4] = sign[i];
		}
		
		retval[12] = (byte)(sequenceNumber & 0xFF);
		retval[13] = (byte)((sequenceNumber >> 8) & 0xFF);
		retval[14] = (byte)((sequenceNumber >> 16) & 0xFF);
		retval[15] = (byte)((sequenceNumber >> 24) & 0xFF);
		
		return retval;
	}
	
	void signingPt2(byte[] verifier, IRandom rc4) throws IllegalStateException, LimitReachedException
	{
		for (int i = 0; i < 8; i++) {
			verifier[i+4] = (byte) (verifier[i+4] ^ rc4.nextByte());
		}
	}
	
	boolean compareSignature(byte[] src, byte[] target)
	{
		return Arrays.equals(src, target);
	}
	
	//TODO merge the signing routine for both client and server all that they differ by are keys...as expected
//	byte[] serverSigning(int sequenceNumber, byte[] serverSigningKey, byte[] data, IRandom rc4) throws NoSuchAlgorithmException, IllegalStateException, LimitReachedException
//	{
//		byte[] seqNumPlusData = new byte[4 + data.length];
//		
//		seqNumPlusData[0] = (byte)(sequenceNumber & 0xFF);
//		seqNumPlusData[1] = (byte)((sequenceNumber >> 8) & 0xFF);
//		seqNumPlusData[2] = (byte)((sequenceNumber >> 16) & 0xFF);
//		seqNumPlusData[3] = (byte)((sequenceNumber >> 24) & 0xFF);
//		
//		System.arraycopy(data, 0, seqNumPlusData, 4, data.length);
//		
//		byte[] retval = new byte[16];
//		retval[0] = 0x01; //Version number LE 1.
//		
//		byte[] sign = Responses.hmacMD5(seqNumPlusData, serverSigningKey);
//		
//		for (int i = 0; i < 8; i++) {
//			retval[i+4] = (byte) (sign[i] ^ rc4.nextByte());
//		}
//		
//		retval[12] = (byte)(sequenceNumber & 0xFF);
//		retval[13] = (byte)((sequenceNumber >> 8) & 0xFF);
//		retval[14] = (byte)((sequenceNumber >> 16) & 0xFF);
//		retval[15] = (byte)((sequenceNumber >> 24) & 0xFF);
//		
//		return retval;
//	}
	
//	byte[] clientSealing(int sequenceNumber, byte[] clientSealingKey, byte[] clientSigningKey, byte[] data,IRandom rc4) throws IllegalStateException, LimitReachedException, NoSuchAlgorithmException
//	{
//		//TODO..Imp... this implementation is not correct and should work for sequence 0, for the rest of the 
//		// sequences the arcfour state has to be maintained and not a new one used everytime...
//		byte[] cipheredData = applyARCFOUR(rc4, data);
//		byte[] signature = clientSigning(sequenceNumber, clientSigningKey, data, rc4);
//		byte[] retval = new byte[cipheredData.length + signature.length];
//		System.arraycopy(cipheredData, 0, retval, 0, cipheredData.length);
//		System.arraycopy(signature, 0, retval, cipheredData.length,signature.length);
//		return retval;
//	}
//	
//	byte[] serverSealing(int sequenceNumber, byte[] serverSealingKey, byte[] serverSigningKey, byte[] data, IRandom rc4) throws IllegalStateException, LimitReachedException, NoSuchAlgorithmException
//	{
//		//TODO..Imp... this implementation is not correct and should work for sequence 0, for the rest of the 
//		// sequences the arcfour state has to be maintained and not a new one used everytime...
//		byte[] cipheredData = applyARCFOUR(rc4, data);
//		byte[] signature = clientSigning(sequenceNumber, serverSigningKey, data, rc4);
//		byte[] retval = new byte[cipheredData.length + signature.length];
//		System.arraycopy(cipheredData, 0, retval, 0, cipheredData.length);
//		System.arraycopy(signature, 0, retval, cipheredData.length,signature.length);
//		return retval;
//	}
	
//	static void testFromDavenportPaper()
//	{
//		try
//		{
//			
//			NTLMKeyFactory keyFactory = new NTLMKeyFactory();
//			byte[] challengePlusclientNonce = Util.toBytesFromString("677f1c557a5ee96c404d1b6f69152580");
//			byte [] ntlm2UserSessionReponseKey = keyFactory.getNTLM2SessionResponseUserSessionKey("test1234", challengePlusclientNonce);
//			
//			System.out.println(Util.dumpString(ntlm2UserSessionReponseKey));
//			
//			byte[] secondaryEncryptedKey = Util.toBytesFromString("727a5240822ec7af4e9100c43e6fee7f");
//
//			byte[] decryptedSecondaryKey = keyFactory.decryptSecondarySessionKey(secondaryEncryptedKey, ntlm2UserSessionReponseKey);
//			System.out.println(Util.dumpString(decryptedSecondaryKey));
//			
//			//now lets try signature from server
//			byte[] data = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
//			byte[] serverSigningKey = keyFactory.generateServerSigningKeyUsingNegotiatedSecondarySessionKey(decryptedSecondaryKey);
//			System.out.println(Util.dumpString(serverSigningKey));
//			byte[] serverSealingKey = keyFactory.generateServerSealingKeyUsingNegotiatedSecondarySessionKey(decryptedSecondaryKey);
//			System.out.println(Util.dumpString(serverSealingKey));
//			IRandom rc4 = keyFactory.getARCFOUR(serverSealingKey);
//			System.out.println(Util.dumpString(keyFactory.serverSigning(0, serverSigningKey, data, rc4)));
//			byte[] cipheredPack = keyFactory.serverSealing(1, serverSealingKey, serverSigningKey,data, rc4);
//			System.out.println(Util.dumpString(cipheredPack));
//			
//			IRandom rc4fordecipher = keyFactory.getARCFOUR(serverSealingKey);
//			keyFactory.serverSigning(0, serverSigningKey, data, rc4fordecipher);//just like that for increasing rc4fordecipher state...will not be like this
//			//in the actual implementation...
//			byte[] cipheredData = new byte[8];
//			System.arraycopy(cipheredPack, 0, cipheredData, 0, 8);
//			
//			System.out.println(Util.dumpString(keyFactory.applyARCFOUR(rc4fordecipher, cipheredData)));
//			int i = 0;
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//	}
//	
//	/**
//	 * @param args
//	 */
//	static void main(String[] args) {
//		
//		try
//		{
//			
//			NTLMKeyFactory keyFactory = new NTLMKeyFactory();
//			byte[] challengePlusclientNonce = Util.toBytesFromString("38c2c82866a284b6a2d45d0f58feb085");
//			byte [] ntlm2UserSessionReponseKey = keyFactory.getNTLM2SessionResponseUserSessionKey("enterprise", challengePlusclientNonce);
//			
//			System.out.println(Util.dumpString(ntlm2UserSessionReponseKey));
//			
//			byte[] secondaryEncryptedKey = Util.toBytesFromString("fa650f59feb62161fc08defeb9e5f5d2");
//
//			byte[] decryptedSecondaryKey = keyFactory.decryptSecondarySessionKey(secondaryEncryptedKey, ntlm2UserSessionReponseKey);
//			System.out.println(Util.dumpString(decryptedSecondaryKey));
//			
//			//now lets try signature from server
//			byte[] data = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
//			byte[] clientSigningKey = keyFactory.generateClientSigningKeyUsingNegotiatedSecondarySessionKey(decryptedSecondaryKey);
//			System.out.println(Util.dumpString(clientSigningKey));
//			byte[] clientSealingKey = keyFactory.generateClientSealingKeyUsingNegotiatedSecondarySessionKey(decryptedSecondaryKey);
//			System.out.println(Util.dumpString(clientSealingKey));
////			IRandom rc4 = keyFactory.getARCFOUR(serverSealingKey);
//
////			byte[] cipheredPack = keyFactory.serverSealing(0, serverSealingKey, serverSigningKey,data, rc4);
////			System.out.println(Util.dumpString(cipheredPack));
//			
//			IRandom rc4fordecipher = keyFactory.getARCFOUR(clientSealingKey);
////			keyFactory.serverSigning(0, serverSigningKey, data, rc4fordecipher);//just like that for increasing rc4fordecipher state...will not be like this
//			//in the actual implementation...
//			byte[] cipheredData = new byte[496];
//			FileInputStream stream = new FileInputStream("c:/temp/encrypted");
//			stream.read(cipheredData, 0, 496);
////			System.arraycopy(cipheredPack, 0, cipheredData, 0, 8);
//			cipheredData = keyFactory.applyARCFOUR(rc4fordecipher, cipheredData);
//        	Hexdump.hexdump(System.out, cipheredData, 0, cipheredData.length);
//			int i = 0;
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//	}

}
