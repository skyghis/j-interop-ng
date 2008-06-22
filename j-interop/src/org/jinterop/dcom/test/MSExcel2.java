package org.jinterop.dcom.test;



import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.IJIDispatch;
import org.jinterop.dcom.impls.JIComFactory;

public class MSExcel2 {

	private JIComServer comServer = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null;
	private IJIDispatch dispatchOfWorkSheets = null;
	private IJIDispatch dispatchOfWorkBook = null;
	private IJIDispatch dispatchOfWorkSheet = null;
	private JISession session = null;
	public MSExcel2(String address, String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
		session.useSessionSecurity(true);
		comServer = new JIComServer(JIProgId.valueOf(session,"Excel.Application"),address,session);
	}

	public void startExcel() throws JIException
	{
		unknown = comServer.createInstance();
		dispatch = (IJIDispatch)JIComFactory.narrowObject(unknown.queryInterface(JIComFactory.IID_IDispatch));
	}

	public void showExcel() throws JIException
	{
		int dispId = dispatch.getIDsOfNames("Visible");
		JIVariant variant = new JIVariant(Boolean.TRUE);
		dispatch.put(dispId,variant);
	}

	public void createWorkSheet() throws JIException
	{
		int dispId = dispatch.getIDsOfNames("Workbooks");

		JIVariant outVal = dispatch.get(dispId);

		IJIDispatch dispatchOfWorkBooks =(IJIDispatch)JIComFactory.narrowObject(outVal.getObjectAsComObject());


		JIVariant[] outVal2 = dispatchOfWorkBooks.callMethodA("Add",new Object[]{JIVariant.OPTIONAL_PARAM()});
		dispatchOfWorkBook =(IJIDispatch)JIComFactory.narrowObject(outVal2[0].getObjectAsComObject());

		outVal = dispatchOfWorkBook.get("Worksheets");

		dispatchOfWorkSheets = (IJIDispatch)JIComFactory.narrowObject(outVal.getObjectAsComObject());

		outVal2 = dispatchOfWorkSheets.callMethodA("Add",new Object[]{JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM()});
		dispatchOfWorkSheet =(IJIDispatch)JIComFactory.narrowObject(outVal2[0].getObjectAsComObject());
	}

	public void pasteArrayToWorkSheet() throws JIException
	{
		int dispId = dispatchOfWorkSheet.getIDsOfNames("Range");
		JIVariant variant = new JIVariant(new JIString("A1:C3"));
		Object[] out = new Object[]{JIVariant.class};
		JIVariant[] outVal2 = dispatchOfWorkSheet.get(dispId, new Object[]{variant});
		IJIDispatch dispRange = (IJIDispatch)JIComFactory.narrowObject(outVal2[0].getObjectAsComObject());


	      JIVariant[][] newValue = {
	    	        { new JIVariant(new JIString("defe")), new JIVariant(new Boolean(false)), new JIVariant(new Double(98765.0 / 12345.0))},
	    	        { new JIVariant(new Date()), new JIVariant(new Integer(5454)),new JIVariant(new Float(22.0 / 7.0) )       },
	    	        { new JIVariant(new Boolean(true)), new JIVariant(new JIString("dffe")),new JIVariant(new Date())}
	    	      };

	     // implement safe array XxX dimension

		dispRange.put("Value2", new JIVariant(new JIArray(newValue)));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JIVariant variant2 = dispRange.get("Value2");
		JIArray newValue2 = variant2.getObjectAsArray();
		newValue = (JIVariant[][])newValue2.getArrayInstance();
		for(int i = 0; i < newValue.length; i++){
	        for(int j = 0; j < newValue[i].length; j++){
	          System.out.print(newValue[i][j] + "\t");
	        }
	        System.out.println();
		}

		dispatchOfWorkBook.callMethod("close",new Object[]{Boolean.FALSE,JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM()});
		dispatch.callMethod("Quit");
		JISession.destroySession(session);
	}


	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				JISystem.setInBuiltLogHandler(false);
				Logger l = Logger.getLogger("org.jinterop");
				l.setLevel(Level.FINEST);
				MSExcel2 test = new MSExcel2(args[0],args);
				test.startExcel();
				test.showExcel();
				test.createWorkSheet();
				test.pasteArrayToWorkSheet();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}





}
