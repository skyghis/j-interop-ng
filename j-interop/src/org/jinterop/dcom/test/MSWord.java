package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class MSWord {

	private JIComServer comStub = null;

	private IJIDispatch dispatch = null;

	private IJIComObject unknown = null;

	public MSWord(String address, String[] args) throws JIException, UnknownHostException {
		JISession session = JISession.createSession(args[1], args[2], args[3]);
		session.useSessionSecurity(true);
		comStub = new JIComServer(JIProgId.valueOf("Word.Application"), address, session);
	}

	public void startWord() throws JIException {
		unknown = comStub.createInstance();
		dispatch = (IJIDispatch) JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
	}

	public void showWord() throws JIException {
		int dispId = dispatch.getIDsOfNames("Visible");
		JIVariant variant = new JIVariant(Boolean.TRUE);
		dispatch.put(dispId, variant);
	}

	public void performOp() throws JIException, InterruptedException {

		/* JISystem config *
		 *
		 */
		JISystem.setJavaCoClassAutoCollection(true);



		System.out.println(((JIVariant) dispatch.get("Version")).getObjectAsString().getString());
		System.out.println(((JIVariant) dispatch.get("Path")).getObjectAsString().getString());
		JIVariant variant = dispatch.get("Documents");

		System.out.println("Open document...");
		IJIDispatch documents = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());
		JIString filePath = new JIString("c:\\temp\\test.doc");
		JIVariant variant2[] = documents.callMethodA("open", new Object[] { filePath, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() ,JIVariant.OPTIONAL_PARAM() , JIVariant.OPTIONAL_PARAM() });

		System.out.println("doc opened");
		//10
		sleep(10);

		System.out.println("Get content...");
		IJIDispatch document = (IJIDispatch) JIObjectFactory.narrowObject(variant2[0].getObjectAsComObject());
		variant = document.get("Content");
		IJIDispatch range = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());

		//10
		sleep(10);
		System.out.println("Running find...");
		variant = range.get("Find");
		IJIDispatch find = (IJIDispatch) JIObjectFactory.narrowObject(variant.getObjectAsComObject());

		//2
		sleep(5);

		System.out.println("Running execute...");
		JIString findString = new JIString("ow");
		JIString replaceString = new JIString("igh");
		find.callMethodA("Execute", new Object[] { findString.VariantByRef, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
				replaceString.VariantByRef, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
				JIVariant.OPTIONAL_PARAM() });

		//1
		sleep(2);

		System.out.println("Closing document...");
		document.callMethod("Close");

	}

	private void sleep(int minutes) throws InterruptedException {
		System.out.println("Sleeping "+minutes+" minute(s)...");
		Thread.sleep( (int)(minutes * 60 * 1000) );
	}

	/**
	 * @throws JIException
	 */
	private void quitAndDestroy() throws JIException {
		System.out.println("Quit...");
		dispatch.callMethod("Quit", new Object[] { new JIVariant(-1, true), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM() });
		JISession.destroySession(dispatch.getAssociatedSession());
	}

	public static void main(String[] args) {

		try {
			if (args.length < 4) {
				System.out.println("Please provide address domain username password");
				return;
			}

			JISystem.getLogger().setLevel(Level.INFO);
			JISystem.setInBuiltLogHandler(false);
			MSWord test = new MSWord(args[0], args);
			test.startWord();
			test.showWord();

//			for (int i = 0; i < 10; i++) {
				test.performOp();
//			}

			test.quitAndDestroy();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
