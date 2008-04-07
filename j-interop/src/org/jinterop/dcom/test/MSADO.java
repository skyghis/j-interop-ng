package org.jinterop.dcom.test;



import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.win32.IJIDispatch;
import org.jinterop.dcom.win32.IJITypeInfo;
import org.jinterop.dcom.win32.JIComFactory;

public class MSADO {

	private JIComServer comServer = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null; 
	private JISession session = null; 
	public MSADO(String address, String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIProgId.valueOf(session,"ADODB.Connection"),address,session);
	}
	
	
	public void performOp() throws JIException, InterruptedException
	{
		unknown = comServer.createInstance();
		dispatch = (IJIDispatch)JIComFactory.createCOMInstance(JIComFactory.IID_IDispatch,unknown);
		IJITypeInfo typeInfo = dispatch.getTypeInfo(0);
		typeInfo.getFuncDesc(0);
		
		dispatch.callMethod("Open",new Object[]{new JIString("driver=Microsoft Access Driver (*.mdb);dbq=C:\\temp\\products.mdb"),JIVariant.OPTIONAL_PARAM,JIVariant.OPTIONAL_PARAM,new Integer(-1)});
		
		JIVariant variant[] = dispatch.callMethodA("Execute",new Object[]{new JIString("SELECT * FROM Products"),new Integer(-1)});
		if (variant[0].isNull())
		{
			System.out.println("Recordset is empty.");
		}
		else
		{
			IJIDispatch resultSet = (IJIDispatch)variant[0].getObjectAsComObject(dispatch);
			//variant = resultSet.get("EOF");
			while(!resultSet.get("EOF").getObjectAsBoolean())
			{
				JIVariant variant2 = resultSet.get("Fields");
				IJIDispatch fields = (IJIDispatch)variant2.getObjectAsComObject(dispatch);
				int count = fields.get("Count").getObjectAsInt();
				for (int i = 0;i < count;i++)
				{
					variant = fields.get("Item",new Object[]{new Integer(i)});
					IJIDispatch field = (IJIDispatch)variant[0].getObjectAsComObject(dispatch);
					variant2 = field.get("Value");
					Object val = null;
					if (variant2.getType() == JIVariant.VT_BSTR)
					{
						val = variant2.getObjectAsString().getString();
					}
					if (variant2.getType() == JIVariant.VT_I4)
					{
						val = new Integer(variant2.getObjectAsInt());
					}
					System.out.println(field.get("Name").getObjectAsString().getString() + " = " + val + "[" + variant2.getType() + "]");	
				}
				resultSet.callMethod("MoveNext");
			}
			
			
		}
		
		JISession.destroySession(session);
	}
	
	public static void main(String[] args) {

		try {
			    if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
			    JISystem.setAutoRegisteration(true);
				MSADO test = new MSADO(args[0],args);
				test.performOp();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	
}
