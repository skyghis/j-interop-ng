package org.jinterop.dcom.test;



import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class MSWord2 {

	private JIComServer comStub = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null;

	public MSWord2(String address, String[] args) throws JIException, UnknownHostException
	{
		JISession session = JISession.createSession(args[1],args[2],args[3]);
		comStub = new JIComServer(JIProgId.valueOf(session,"Word.Application"),address,session);
	}

	public void startWord() throws JIException
	{
		unknown = comStub.createInstance();
		dispatch = (IJIDispatch)JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
	}

	public void showWord() throws JIException
	{
		int dispId = dispatch.getIDsOfNames("Visible");
		JIVariant variant = new JIVariant(Boolean.TRUE);
		dispatch.put(dispId,variant);
	}

	public void performOp() throws JIException, InterruptedException
	{
		System.out.println(((JIVariant)dispatch.get("Version")).getObjectAsString().getString());
		System.out.println(((JIVariant)dispatch.get("Path")).getObjectAsString().getString());
		JIVariant variant = dispatch.get("Documents");
		//JIInterfacePointer ptr = variant.getObjectAsInterfacePointer();
		//IJIDispatch documents = (IJIDispatch)JIObjectFactory.createCOMInstance(unknown,ptr);
		IJIDispatch documents = (IJIDispatch)JIObjectFactory.narrowObject(variant.getObjectAsComObject());
		JIString filePath = new JIString("c:/temp/test.doc");
		JIVariant variant2[] = documents.callMethodA("open",new Object[]{filePath.VariantByRef,JIVariant.OPTIONAL_PARAM()
				,JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM()});
		//IJIDispatch document = (IJIDispatch)JIObjectFactory.createCOMInstance(unknown,variant2[0].getObjectAsInterfacePointer());
		IJIDispatch document = (IJIDispatch)JIObjectFactory.narrowObject(variant2[0].getObjectAsComObject());
		variant = document.get("Content");
		//IJIDispatch range = (IJIDispatch)JIObjectFactory.createCOMInstance(unknown,variant.getObjectAsInterfacePointer());
		IJIDispatch range = (IJIDispatch)JIObjectFactory.narrowObject(variant.getObjectAsComObject());

		variant = range.get("Find");
		IJIDispatch find = (IJIDispatch)JIObjectFactory.narrowObject(variant.getObjectAsComObject());

		Thread.sleep(2000);
		JIString findString = new JIString("ow");
		JIString replaceString = new JIString("igh");
		find.callMethodA("Execute",new Object[]{findString.VariantByRef,JIVariant.OPTIONAL_PARAM()
				,JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),replaceString.VariantByRef,JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM()});
		Thread.sleep(5000);

		dispatch.callMethod("Quit", new Object[]{new JIVariant(-1,true),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM()});
		JISession.destroySession(dispatch.getAssociatedSession());
	}

	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				MSWord2 test = new MSWord2(args[0],args);
				test.startWord();
				test.showWord();
				test.performOp();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}





}
