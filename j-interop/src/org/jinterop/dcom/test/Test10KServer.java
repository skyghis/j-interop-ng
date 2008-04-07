package org.jinterop.dcom.test;



import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.win32.IJIDispatch;
import org.jinterop.dcom.win32.JIComFactory;

public class Test10KServer {

	private JIComServer comStub = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null; 
	
	
	public static void main(String[] args) {

		try {
			
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				JISystem.setInBuiltLogHandler(false);
				JISystem.setAutoRegisteration(true);
				for (int i=0;i<10000;++i) 
				{ 
					 
					JISession session = JISession.createSession(args[1],args[2],args[3]); 
					JIComServer comServer = new JIComServer(JIProgId.valueOf(session,"MSMQ.MSMQQueueInfo"),args[0],session); 
					IJIComObject unknown = comServer.createInstance(); 
					IJIDispatch dispatch = (IJIDispatch)JIComFactory.createCOMInstance(JIComFactory.IID_IDispatch,unknown); 
					//JISession.destroySession(session);
					Thread.sleep(150);
					if(i%100 == 0)
					{
						System.out.println(new String().valueOf(i));
					}
					System.gc();
				} 

		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	
}
