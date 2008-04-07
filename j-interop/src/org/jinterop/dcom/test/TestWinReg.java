package org.jinterop.dcom.test;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.IJIAuthInfo;
import org.jinterop.dcom.common.JIDefaultAuthInfoImpl;
import org.jinterop.dcom.common.JIException;
import org.jinterop.winreg.IJIWinReg;
import org.jinterop.winreg.JIPolicyHandle;
import org.jinterop.winreg.JIWinRegFactory;


/**
 * 
 * Make sure you have Administrator level access on the target machine and if your password/username has special
 * characters , please use the URLEncoder before passing them to WinReg example. 
 *
 */
public class TestWinReg {

	public static void main(String[] args) {
	
		if (args.length < 5)
	    {
	    	System.out.println("Please provide address domain username password keyname");
	    	return;
	    }
		IJIAuthInfo authInfo = new JIDefaultAuthInfoImpl(args[1],args[2],args[3]);
		
	
		
		try {
			IJIWinReg registry = JIWinRegFactory.getSingleTon().getWinreg(authInfo,args[0],true);
			//Open HKLM
			JIPolicyHandle policyHandle = registry.winreg_OpenHKLM();
			//Open a key here
			JIPolicyHandle policyHandle2 = registry.winreg_OpenKey(policyHandle,"Software\\Classes",IJIWinReg.KEY_ALL_ACCESS);
			
			System.out.println("Printing first 1000 entries under \"Software\\Classes\"...");
			for (int i = 0;i < 1000;i++)
			{
				String[] values = registry.winreg_EnumKey(policyHandle2,i);
				System.out.println(values[0] + " , " + values[1]);
			}
			
			System.out.println("****************************************************");
			System.out.println("\nCreating Key " + args[4] + " under \"Software\\Classes\"...");
			String key = args[4].trim();
			JIPolicyHandle policyHandle3 = registry.winreg_CreateKey(policyHandle2,key,IJIWinReg.REG_OPTION_NON_VOLATILE,IJIWinReg.KEY_ALL_ACCESS);
			
			System.out.println("Setting values to " + key);
			registry.winreg_SetValue(policyHandle3,"j-Interop_None");
			registry.winreg_SetValue(policyHandle3,"j-Interop_String",".".getBytes(),false,false);
			Object[] values1 = registry.winreg_QueryValue(policyHandle3,"j-Interop_String",1024);
			registry.winreg_SetValue(policyHandle3,"j-Interop_String_Ex","%PATH%\\Test12345".getBytes(),false,true);
			registry.winreg_SetValue(policyHandle3,"j-Interop_Bin","123456789".getBytes(),true,false);
			registry.winreg_SetValue(policyHandle3,"j-Interop_Dword",100);
			
			String[] strings = {"123", "456", "6789", "10","11"};
			byte[][] data = new byte[strings.length][];
			for (int i = 0; i < strings.length;i++)
			{
				data[i] = strings[i].getBytes();
			}
			
			registry.winreg_SetValue(policyHandle3,"j-Interop_Multi_sz",data);
			
			for (int i = 0; i < 6;i++)
			{
				Object[] values = registry.winreg_EnumValue(policyHandle3,i);
				System.out.println(values[0] + " , " + values[1]);
			}
			
			System.out.println("Retrieving j-Interop_String_Ex value " + key);
			Object[] values = registry.winreg_QueryValue(policyHandle3,"j-Interop_String_Ex",1024);
			System.out.println(new String((byte[])values[1]));
			
			System.out.println("Deleting j-Interop_Bin value");
			registry.winreg_DeleteKeyOrValue(policyHandle3,"j-Interop_Bin",false);
			
			System.out.println("Saving the " + key + " in a file to local server location as c:\\temp\\j-Interop");
			registry.winreg_SaveFile(policyHandle3,"c:\\temp\\j-Interop");
			
			registry.winreg_CloseKey(policyHandle3);
			registry.winreg_CloseKey(policyHandle2);
			registry.winreg_CloseKey(policyHandle);
			registry.closeConnection();
			
//			
//			//Open HKCR
//			policyHandle = registry.winreg_OpenHKCR();
//			
//			policyHandle2 = registry.winreg_OpenKey(policyHandle,"ClSID",IJIWinReg.KEY_ALL_ACCESS);
//			policyHandle3 = registry.winreg_CreateKey(policyHandle2,"j-Interop007",IJIWinReg.REG_OPTION_NON_VOLATILE,IJIWinReg.KEY_ALL_ACCESS);
//			registry.winreg_CloseKey(policyHandle3);
//			registry.winreg_CloseKey(policyHandle2);
//			registry.winreg_CloseKey(policyHandle);
//			
//			//Open HKCU
//			policyHandle = registry.winreg_OpenHKCU();
//			
//			policyHandle2 = registry.winreg_OpenKey(policyHandle,"Software\\Classes",IJIWinReg.KEY_ALL_ACCESS);
//			registry.winreg_CloseKey(policyHandle2);
//			registry.winreg_CloseKey(policyHandle);
//			
//			//Open HKU
//			policyHandle = registry.winreg_OpenHKU();
//			
//			policyHandle2 = registry.winreg_OpenKey(policyHandle,".DEFAULT",IJIWinReg.KEY_ALL_ACCESS);
//			registry.winreg_CloseKey(policyHandle2);
//			registry.winreg_CloseKey(policyHandle);
		
		
		} catch (JIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
