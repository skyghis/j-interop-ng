package org.jinterop.dcom.test;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIInterfacePointer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.win32.IJIDispatch;
import org.jinterop.dcom.win32.IJITypeInfo;
import org.jinterop.dcom.win32.JIComFactory;

public class MSExcel {

	private final int  xlWorksheet = -4167;
	private final int  xlXYScatterLinesNoMarkers = 75;
	private final int  xlColumns = 2;
	
	private JIComServer comServer = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null; 
	private IJIDispatch dispatchOfWorkSheet = null;
	private IJIDispatch dispatchOfWorkBook = null;
	private JISession session = null;
	public MSExcel(String address,String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
		comServer = new JIComServer(JIProgId.valueOf(session,"Excel.Application"),address,session);
	}
	
	public void startExcel() throws JIException
	{
		unknown = comServer.createInstance();
		dispatch = (IJIDispatch)JIComFactory.createCOMInstance(JIComFactory.IID_IDispatch,unknown);
		IJITypeInfo typeInfo = dispatch.getTypeInfo(0);
		typeInfo.getFuncDesc(0);
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
		Object[] out = new Object[]{JIVariant.class};
		JIVariant[] outVal2 = null;
		JIVariant outVal = dispatch.get(dispId);
		JIInterfacePointer ptr = (JIInterfacePointer)outVal.getObject();
		dispatchOfWorkBook =(IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		
		int[] dispIds = dispatchOfWorkBook.getIDsOfNames(new String[]{"Add","Template"});
		
		out = new Object[]{JIVariant.class};
		dispId = dispatchOfWorkBook.getIDsOfNames("Add");
		
		outVal2 = dispatchOfWorkBook.callMethodA(dispId,new Object[]{new Integer(xlWorksheet)});
		ptr = (JIInterfacePointer)outVal2[0].getObject();
		dispatchOfWorkBook =(IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		dispId = dispatchOfWorkBook.getIDsOfNames("Worksheets");
		JIVariant variant = new JIVariant((short)1);
		out = new Object[]{JIVariant.class};
		outVal2 = dispatchOfWorkBook.get(dispId,new Object[]{variant});
		
		ptr = (JIInterfacePointer)outVal2[0].getObject();
		dispatchOfWorkSheet =(IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
	}
	
	public void pasteStringToWorkSheet() throws JIException
	{
		int dispId = dispatchOfWorkSheet.getIDsOfNames("Range");

		JIVariant variant = new JIVariant(new JIString("A1"));
		Object[] out = new Object[]{JIVariant.class};
		JIVariant outVal,outVal2[] = null;
		outVal2 = dispatchOfWorkSheet.get(dispId, new Object[]{variant});
		
		JIInterfacePointer ptr = (JIInterfacePointer)outVal2[0].getObject();
		IJIDispatch dispRange = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		dispId = dispRange.getIDsOfNames("Select");
		out = new Object[]{JIVariant.class};
		outVal = dispRange.get(dispId)  ;
		
		dispId = dispatchOfWorkBook.getIDsOfNames("ActiveSheet");
		out = new Object[]{JIVariant.class};
		outVal = dispatchOfWorkBook.get(dispId);
	
		ptr = (JIInterfacePointer)outVal.getObject();
		IJIDispatch dispatchActiveSheet = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		dispId = dispatchActiveSheet.getIDsOfNames("Paste");
		out = new Object[]{JIVariant.class};
		try{
			outVal = dispatchActiveSheet.callMethodA(dispId);
		}catch(JIException e)
		{
			throw e;
		}
	}

	public void createXYChart() throws JIException
	{
		//column 2.
		int dispId = dispatchOfWorkSheet.getIDsOfNames("Columns");

		Double cols = new Double(2);
		Object[] out = new Object[]{JIVariant.class};
		JIVariant outVal ,outVal2[] = null;
		outVal2 = dispatchOfWorkSheet.get(dispId,new Object[]{cols});
		
		JIInterfacePointer ptr = (JIInterfacePointer)outVal2[0].getObject();
		IJIDispatch dispatchRange = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		dispId = dispatchOfWorkBook.getIDsOfNames("Charts");
		out = new Object[]{JIVariant.class};
		outVal = dispatchOfWorkBook.get(dispId);
		
		ptr = (JIInterfacePointer)outVal.getObject();
		IJIDispatch dispatchChart = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		
		
		dispId = dispatchChart.getIDsOfNames("Add");
		out = new Object[]{JIVariant.class};
		outVal = dispatchChart.callMethodA(dispId);
	
		ptr = (JIInterfacePointer)outVal.getObject();
		dispatchChart = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		dispId = dispatchOfWorkBook.getIDsOfNames("ActiveChart");
		out = new Object[]{JIVariant.class};
		
		outVal = dispatchOfWorkBook.get(dispId);
		
		ptr = (JIInterfacePointer)outVal.getObject();
		IJIDispatch dispatchActiveChart = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		
		dispId = dispatchActiveChart.getIDsOfNames("ChartType");
		out = new Object[]{JIVariant.class};

		dispatchActiveChart.put(dispId,new JIVariant(new Short((short)xlXYScatterLinesNoMarkers)));
		
		int[] dispIds = dispatchActiveChart.getIDsOfNames(new String[]{"SetSourceData","Source","PlotBy"});

		dispId = dispatchActiveChart.getIDsOfNames("SetSourceData");
		out = new Object[]{JIVariant.class};
		outVal2 = dispatchActiveChart.callMethodA(dispId,new Object[]{dispatchRange,new Short((short)xlColumns)},new int[]{dispIds[1],dispIds[2]});//invoke(dispIds[0],IJIDispatch.DISPATCH_METHOD,new Object[]{variant,new JIArray(new Integer[]{new Integer(dispIds[1]),new Integer(dispIds[2])},true),null,null,null},null);
		
		JISession.destroySession(session);
	}
	
	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				MSExcel test = new MSExcel(args[0],args);
				test.startExcel();
				test.showExcel();
				test.createWorkSheet();
				test.pasteStringToWorkSheet();
				test.createXYChart();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	
}
