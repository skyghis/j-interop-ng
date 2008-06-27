package org.jinterop.dcom.test;



import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;

public class MetrikonOPC {

	private JIComServer comStub = null;
	private IJIComObject unknown = null;
	private IJIComObject opcServer = null;

	public MetrikonOPC(String address, String[] args) throws JIException, UnknownHostException
	{
		JISession session = JISession.createSession(args[1],args[2],args[3]);
		comStub = new JIComServer(JIProgId.valueOf("Matrikon.OPC.Simulation"),address,session);
	}

	public void getOPC() throws JIException
	{
		unknown = comStub.createInstance();
		opcServer = (IJIComObject)unknown.queryInterface("39C13A4D-011E-11D0-9675-0020AFD8ADB3");
	}



	public void performOp() throws JIException, InterruptedException
	{

		JICallBuilder callObject = new JICallBuilder (true);
        callObject.setOpnum ( 0 );

        callObject.addInParamAsString("",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsInt(0xFFFFFFFF, JIFlags.FLAG_NULL );
        callObject.addInParamAsInt ( 1000,JIFlags.FLAG_NULL );
        callObject.addInParamAsInt ( 1234,JIFlags.FLAG_NULL );
        callObject.addInParamAsPointer ( new JIPointer(new Integer(0)), JIFlags.FLAG_NULL );
        callObject.addInParamAsPointer ( new JIPointer(new Float(0.0)),JIFlags.FLAG_NULL );
        callObject.addInParamAsInt ( 0, JIFlags.FLAG_NULL );
        callObject.addOutParamAsType ( Integer.class,JIFlags.FLAG_NULL );
        callObject.addOutParamAsType ( Integer.class,JIFlags.FLAG_NULL );
        callObject.addInParamAsUUID( "39C13A50-011E-11D0-9675-0020AFD8ADB3", JIFlags.FLAG_NULL );
        callObject.addOutParamAsType ( IJIComObject.class, JIFlags.FLAG_NULL );

        Object[] result = opcServer.call ( callObject );



        JISession.destroySession(unknown.getAssociatedSession());
	}

	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				MetrikonOPC test = new MetrikonOPC(args[0],args);
				test.getOPC();
				test.performOp();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}





}
