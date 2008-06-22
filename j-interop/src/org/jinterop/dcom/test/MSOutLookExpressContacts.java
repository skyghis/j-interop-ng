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
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.IJIDispatch;
import org.jinterop.dcom.impls.JIComFactory;

public class MSOutLookExpressContacts {

	JISession session  = null;
	JIComServer comServer = null;

	MSOutLookExpressContacts(String args[]) throws UnknownHostException, JIException
	{
		session  = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIProgId.valueOf(session,"Outlook.Application"),args[0],session);
	}

	void doStuff() throws JIException
	{
		IJIComObject unknown = (IJIComObject)comServer.createInstance();
		IJIComObject application = (IJIComObject)unknown.queryInterface("00063001-0000-0000-C000-000000000046");

		JICallObject callObject = new JICallObject(application.getIpid(),!application.isDispatchSupported());
		callObject.setOpnum(12);
		callObject.addInParamAsString("MAPI", JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		Object[] res = application.call(callObject);

		IJIComObject namespace = JIComFactory.instantiateComObject(application,(JIInterfacePointer)res[0]);
		callObject = new JICallObject(namespace.getIpid());
		callObject.setOpnum(16);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		res = namespace.call(callObject);

		if (res[0] == null)
		{
			System.out.println("user cancelled request");
			return;
		}

		IJIComObject folder = JIComFactory.instantiateComObject(application,(JIInterfacePointer)res[0]);
		callObject = new JICallObject(folder.getIpid());
		callObject.setOpnum(4);
		callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
		res = folder.call(callObject);

		if (((Integer)res[0]).intValue() != 2)
		{
			System.out.println("Invalid folder selected, this is not a \"contact\" folder , please reselect..");
			return;
		}

		callObject.reInit();
		callObject.setOpnum(10);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		res = folder.call(callObject);
		if (res[0] == null)
		{
			System.out.println("Unable to get Contact Items.");
			return;
		}

		IJIComObject items = JIComFactory.instantiateComObject(application,(JIInterfacePointer)res[0]);
		callObject = new JICallObject(items.getIpid());
		callObject.setOpnum(12);
		callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		res = items.call(callObject);

		while(true)
		{
			if (res[0] == null)
			{
				break;
			}

			String details = null;
			IJIDispatch contactItem = (IJIDispatch)JIComFactory.instantiateComObject(application,(JIInterfacePointer)res[0]);
			JIVariant res2 = contactItem.get("FullName");
//			callObject = new JICallObject(contactItem.getIpid());
//			callObject.setOpnum(124);
//			callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
//			res = contactItem.call(callObject);
			details = res2.getObjectAsString().getString();

//			callObject.reInit();
//			callObject.setOpnum(100);
//			callObject.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
//			res = contactItem.call(callObject);
			res2 = contactItem.get("Email1Address");
			details = details + "<" + res2.getObjectAsString().getString() + ">";

			System.out.println(details);

			callObject = new JICallObject(items.getIpid());
			callObject.setOpnum(14);
			callObject.addOutParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
			res = items.call(callObject);
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
			MSOutLookExpressContacts outlookMessages = new MSOutLookExpressContacts(args);
			outlookMessages.doStuff();
			JISession.destroySession(outlookMessages.session);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
