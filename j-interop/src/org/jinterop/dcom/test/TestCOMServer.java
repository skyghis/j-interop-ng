package org.jinterop.dcom.test;



import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class TestCOMServer {

	private JIComServer comStub = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null;

	public TestCOMServer(String address, String[] args) throws JIException, UnknownHostException
	{
		JISession session = JISession.createSession(args[1],args[2],args[3]);


		//instead of this the ProgID "TestCOMServer.ITestCOMServer"	can be used as well.
		//comStub = new JIComServer(JIProgId.valueOf(session,"TestCOMServer.ITestCOMServer"),address,session);
		//CLSID of ITestCOMServer
		comStub = new JIComServer(JIClsid.valueOf("44A9CD09-0D9B-4FD2-9B8A-0151F2E0CAD1"),address,session);
	}

	public void execute() throws JIException
	{
		unknown = comStub.createInstance();
		//CLSID of IITestCOMServer
		IJIComObject comObject = (IJIComObject)unknown.queryInterface("4AE62432-FD04-4BF9-B8AC-56AA12A47FF9");
		dispatch = (IJIDispatch)JIObjectFactory.narrowObject(comObject.queryInterface(IJIDispatch.IID));

		//Now call via automation
		Object results[] = dispatch.callMethodA("Add",new Object[]{new Integer(1), new Integer(2), new JIVariant(0,true)});
		System.out.println(results[1]);

		//now without automation
		JICallObject callObject = new JICallObject(comObject.getIpid());
		callObject.setOpnum(1);//obtained from the IDL or TypeLib.
		callObject.addInParamAsInt(1,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(2,JIFlags.FLAG_NULL);
		callObject.addInParamAsPointer(new JIPointer(new Integer(0)),JIFlags.FLAG_NULL);
		//Since the retval is a top level pointer , it will get replaced with it's base type.
		callObject.addOutParamAsObject(Integer.class,JIFlags.FLAG_NULL);
		results = comObject.call(callObject);
		System.out.println(results[0]);
		JISession.destroySession(dispatch.getAssociatedSession());
	}



	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				TestCOMServer test = new TestCOMServer(args[0],args);
				test.execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}





}
