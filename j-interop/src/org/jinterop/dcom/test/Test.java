package org.jinterop.dcom.test;

import java.io.IOException;
import java.net.UnknownHostException;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

import org.jinterop.dcom.core.JIComServer;

public class Test {

 public static void main(String[] args) {
	 try {

		 boolean b = null instanceof JIComServer ? false : true;
		// Socket socket = new Socket("10.24.10.65",139);
		 //socket.close();
//	 UniAddress mydomaincontoller = UniAddress.getByName( "192.168.170.6" );
//	 NtlmPasswordAuthentication mycreds = new NtlmPasswordAuthentication( "itlinfosys", "vikram_roopchand", "Dilbert007" );
//
//	 SmbSession.logon( mydomaincontoller, mycreds );
	//	 Config.setProperty("jcifs.smb.client.laddr","10.24.10.65");
	//	 Config.setProperty("jcifs.smb.client.domain","itl-hw-lt15522.ad.infosys.com");
//		 System.setProperty("jcifs.smb.client.laddr","10.24.10.65");
//		 System.setProperty("jcifs.smb.client.domain","itl-hw-lt15522.ad.infosys.com");
//		 System.setProperty("jcifs.netbios.hostname","itl-hw-lt15522.ad.infosys.com");

		 //NtlmChallenge challenge = SmbSession.getChallengeForDomain();

		 UniAddress mydomaincontoller = UniAddress.getByName("itl-hw-lt15522");
		 NtlmPasswordAuthentication mycreds = new NtlmPasswordAuthentication( "itl-hw-lt15522", "TestUser", "Enabler2000" );
		 SmbSession.logon(mydomaincontoller,mycreds);
		 //PLEASE NOTE THAT THE WINDOWS "SERVER" SERVICE SOULD BE RUNNING !!! OTHERWISE THE
		 //GETCHALLENGE WILL FAIL.
//		 UniAddress mydomaincontoller = UniAddress.getByName("itl-hw-lt15522.ad.infosys.com");
//		 byte[] b =  SmbSession.getChallenge(mydomaincontoller,139);
		 int i = 0;
		// NtlmChallenge challenge = SmbSession.getChallengeForDomain();

	     // SUCCESS

	 } catch( SmbAuthException sae ) {
	     // AUTHENTICATION FAILURE
	     sae.printStackTrace();
	 } catch( SmbException se ) {
	     // NETWORK PROBLEMS?
	     se.printStackTrace();
	 } catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

 public void test()
 	{
 		System.out.println("Called back !!!");
 	}

}
