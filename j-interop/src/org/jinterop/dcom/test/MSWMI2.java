package org.jinterop.dcom.test;



import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.win32.IJIDispatch;
import org.jinterop.dcom.win32.IJIEnumVARIANT;
import org.jinterop.dcom.win32.JIComFactory;

/** WMI example showing how to use a new logger implementation.
 * 
 * @since 1.23
 *
 */
public class MSWMI2 {

	private JIComServer comStub = null;
	private IJIComObject comObject = null; 
	private IJIDispatch dispatch = null;
	private String address = null;
	private JISession session = null;
	public MSWMI2(String address, String[] args) throws JIException, UnknownHostException
	{
		this.address = address;
		session = JISession.createSession(args[1],args[2],args[3]);
		session.useSessionSecurity(true);
		session.setGlobalSocketTimeout(5000);
		comStub = new JIComServer(JIProgId.valueOf(session,"WbemScripting.SWbemLocator"),address,session);
		IJIComObject unknown = comStub.createInstance();
		comObject = (IJIComObject)unknown.queryInterface("76A6415B-CB41-11d1-8B02-00600806D9B6");//ISWbemLocator
		//This will obtain the dispatch interface
		dispatch = (IJIDispatch)JIComFactory.createCOMInstance(JIComFactory.IID_IDispatch,comObject);
	}
	
	
	public void performOp() throws JIException, InterruptedException
	{
		JIVariant results[] = dispatch.callMethodA("ConnectServer",new Object[]{new JIString(address),new JIString("ROOT\\CIMV2"),JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM
				,JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,new Integer(0),JIVariant.OPTIONAL_PARAM});
		
		IJIDispatch wbemServices_dispatch = (IJIDispatch)(results[0]).getObjectAsComObject(comObject);
		results = wbemServices_dispatch.callMethodA("ExecQuery", new Object[]{new JIString("select * from Win32_OperatingSystem where Primary=True"), JIVariant.OPTIONAL_PARAM, JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM});
		IJIDispatch wbemObjectSet_dispatch = (IJIDispatch)(results[0]).getObjectAsComObject(comObject);
		JIVariant variant = wbemObjectSet_dispatch.get("_NewEnum");
		IJIComObject object2 = variant.getObjectAsComObject(wbemObjectSet_dispatch);
		
		IJIEnumVARIANT enumVARIANT = (IJIEnumVARIANT)JIComFactory.createCOMInstance(IJIEnumVARIANT.IID,object2);
		
		JIVariant Count = wbemObjectSet_dispatch.get("Count");
		int count = Count.getObjectAsInt();
		for (int i = 0; i < count; i++)
		{
			Object[] values = enumVARIANT.next(1);
			JIArray array = (JIArray)values[0];
			Object[] arrayObj = (Object[])array.getArrayInstance();
			for (int j = 0; j < arrayObj.length; j++)
			{
				IJIDispatch wbemObject_dispatch = (IJIDispatch)JIComFactory.createCOMInstance(wbemObjectSet_dispatch,((JIVariant)arrayObj[j]).getObjectAsInterfacePointer());
				JIVariant variant2 = (JIVariant)(wbemObject_dispatch.callMethodA("GetObjectText_",new Object[]{new Integer(1)}))[0];
				System.out.println(variant2.getObjectAsString().getString());
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}
		}
		
		
	}
	
	private void killme() throws JIException
	{
		JISession.destroySession(session);
	}
	
	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				
//				JISystem.setInBuiltLogHandler(false);
				JISystem.getLogger().setLevel(Level.OFF);
				JISystem.setAutoRegisteration(true);
				MSWMI2 test = new MSWMI2(args[0],args);
				for (int i = 0 ; i < 2; i++)
				{
					System.out.println("Vikram i: " + i);
					test.performOp();
				}
				test.killme();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	
}
