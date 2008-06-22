package org.jinterop.dcom.test;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;

public class MSShell {

	JISession session  = null;
	JIComServer comServer = null;

	MSShell(String args[]) throws UnknownHostException, JIException
	{
		session  = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIProgId.valueOf(session,"Shell.Application"),args[0],session);
	}


	void doStuff() throws JIException
	{
		//this will return a reference to the IUnknown of the Shell coclass.
		IJIComObject comUnknown = (IJIComObject)comServer.createInstance();

		//now we query for the IShellDispatch interface
		IJIComObject shellDispatch = (IJIComObject)comUnknown.queryInterface("D8F015C0-C278-11CE-A49E-444553540000");

		JICallObject callObject = new JICallObject(shellDispatch.getIpid());
//		callObject.setOpnum(5);
//		callObject.addInParamAsVariant(new JIVariant(new JIString("c:")),JIFlags.FLAG_NULL);
//		Object result[] = shellDispatch.call(callObject);

//		callObject.reInit();
//		callObject.setOpnum(7);
//		result = shellDispatch.call(callObject);

		callObject.reInit();
		callObject.setOpnum(2);
		callObject.addInParamAsVariant(new JIVariant(2),JIFlags.FLAG_NULL);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		Object[] result = shellDispatch.call(callObject);
		JIInterfacePointer folderPtr = (JIInterfacePointer)result[0];
		IJIComObject folder = JIObjectFactory.instantiateComObject(shellDispatch,folderPtr);

		callObject = new JICallObject(folder.getIpid());
		callObject.setOpnum(0);
		callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
		result = folder.call(callObject);
		System.out.println("Current Folder: " + result[0]);

		callObject.reInit();
		callObject.setOpnum(1);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		result = folder.call(callObject);
		IJIComObject test = JIObjectFactory.instantiateComObject(shellDispatch,(JIInterfacePointer)result[0]);

//		Not implemented by shell
//		callObject.reInit();
//		callObject.setOpnum(2);
//		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
//		result = folder.call(callObject);
//		test = JIObjectFactory.createCOMInstance(shellDispatch,(JIInterfacePointer)result[0]);

		callObject.reInit();
		callObject.setOpnum(3);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		result = folder.call(callObject);
		test = JIObjectFactory.instantiateComObject(shellDispatch,(JIInterfacePointer)result[0]);

		callObject.reInit();
		callObject.setOpnum(4);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		result = folder.call(callObject);
		IJIComObject folderItems = JIObjectFactory.instantiateComObject(shellDispatch,(JIInterfacePointer)result[0]);

		callObject = new JICallObject(folderItems.getIpid());
		callObject.setOpnum(0);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		result = folderItems.call(callObject);

		int count = ((Integer)result[0]).intValue();

		for (int i = 0;i<count;i++)
		{
			callObject.reInit();
			callObject.setOpnum(3);
			callObject.addInParamAsVariant(new JIVariant(i),JIFlags.FLAG_NULL);
			callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
			result = folderItems.call(callObject);
			IJIComObject folderItem = JIObjectFactory.instantiateComObject(shellDispatch,(JIInterfacePointer)result[0]);


			JICallObject callObject2 = new JICallObject(folderItem.getIpid());
			callObject2.setOpnum(2);
			callObject2.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
			result = folderItem.call(callObject2);
			System.out.println("Name of Object: " + result[0]);

			callObject2.reInit();
			callObject2.setOpnum(4);
			callObject2.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
			result = folderItem.call(callObject2);
			System.out.println("Path of the Object: " + result[0]);


			callObject2.reInit();
			callObject2 = new JICallObject(folderItem.getIpid());
			callObject2.setOpnum(9);
			//VARIANT_BOOL is Boolean
			callObject2.addOutParamAsType(Boolean.class,JIFlags.FLAG_NULL);
			result = folderItem.call(callObject2);

			boolean isFileSystemObject = ((Boolean)result[0]).booleanValue();

			if (isFileSystemObject)
			{
				System.out.print(" and is part of file system\n");
			}
			else
			{
				System.out.print(" and is not part of file system\n");
			}

			callObject2.reInit();
			callObject2 = new JICallObject(folderItem.getIpid());
			callObject2.setOpnum(13);
			callObject2.addOutParamAsObject((Integer.class),JIFlags.FLAG_NULL);
			result = folderItem.call(callObject2);
			System.out.print(" and size(in bytes) is: " + ((Integer)result[0]).intValue() + "\n");

		}

	}



	public static void main(String[] args) {

		if (args.length < 4)
	    {
	    	System.out.println("Please provide address domain username password");
	    	return;
	    }
		JISystem.setAutoRegisteration(true);
		try {
			MSShell shell = new MSShell(args);
			shell.doStuff();
			JISession.destroySession(shell.session);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
