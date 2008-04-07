package org.jinterop.dcom.test;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.win32.JIOleFrame;

public class TestFrame {

	private JIComServer comServer = null;
	private JISession session = null;
	private IJIComObject ieObject = null;
	private IJIComObject ieObject2= null;
	private String identifier = null;
	public TestFrame(String address, String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIClsid.valueOf("00020820-0000-0000-C000-000000000046"),address,session);
		ieObject = comServer.createInstance();
		//ieObject2 = (IJIComObject)ieObject.queryInterface(""); 
		
	}
	
	void tryme() throws JIException
	{
		JIOleFrame frame = new JIOleFrame(session, 1,ieObject);
		frame.testShowWindow();
		//frame.
	}
	
	public static void main(String[] args) {
		try{
			if (args.length < 4)
		    {
		    	System.out.println("Please provide address domain username password");
		    	return;
		    }
			JISystem.setAutoRegisteration(true);
			TestFrame frame = new TestFrame(args[0],args);
			frame.tryme();
			Thread.sleep(5000);
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}


}
