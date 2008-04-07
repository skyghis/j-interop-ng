package org.jinterop.dcom.test;

import java.net.UnknownHostException;

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

//StdCollection.VBCollection
public class MSEnumVariant {

	private JIComServer comServer = null;
	private JISession session = null;
	private IJIDispatch dispatch = null;
	
	public MSEnumVariant(String address,String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIProgId.valueOf(session,"StdCollection.VBCollection"),address,session);
		IJIComObject object = comServer.createInstance();
		dispatch = (IJIDispatch)JIComFactory.createCOMInstance(IJIDispatch.IID,object);
		
	}
	
	public void performOp() throws JIException
	{
		int i = 0;
		for (; i < 5; i++)
		{
			dispatch.callMethod("Add", new Object[]{new Integer(i),new JIString("Key-" + i)});
		}
		
		for (; i < 10; i++)
		{
			dispatch.callMethod("Add", new Object[]{new Integer(i),JIVariant.OPTIONAL_PARAM});
		}
		
		JIVariant variant = dispatch.get("_NewEnum");
		
		IJIComObject object2 = variant.getObjectAsComObject(dispatch);
		//IJIComObject enumObject = (IJIComObject)object2.queryInterface(IJIEnumVARIANT.IID);
		
		IJIEnumVARIANT enumVARIANT = (IJIEnumVARIANT)JIComFactory.createCOMInstance(IJIEnumVARIANT.IID,object2);
		
		for (i = 0; i < 10; i++)
		{
			Object[] values = enumVARIANT.next(1);
			JIArray array = (JIArray)values[0];
			Object[] arrayObj = (Object[])array.getArrayInstance();
			for (int j = 0; j < arrayObj.length; j++)
			{
				System.out.println(((JIVariant)arrayObj[j]).getObjectAsInt() + "," + ((Integer)values[1]).intValue());	
			}
			
			int j = 0;
		}
		
		enumVARIANT.reset();
		Object[] values = enumVARIANT.next(5);
		enumVARIANT.next(1);	
		enumVARIANT.skip(2);
		values = enumVARIANT.next(1);
		IJIEnumVARIANT newenum = enumVARIANT.Clone();
		newenum.reset();
		values = newenum.next(10);
		i = 0; 
	
		JISession.destroySession(session);
	}
	
	
	public static void main(String[] args) {
		
		try{
		    if (args.length < 4)
		    {
		    	System.out.println("Please provide address domain username password");
		    	return;
		    }
		    JISystem.setAutoRegisteration(true);
			MSEnumVariant enumVariant = new MSEnumVariant(args[0],args);
			enumVariant.performOp();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
