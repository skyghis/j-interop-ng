package org.jinterop.dcom.test;



import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.IJIDispatch;
import org.jinterop.dcom.impls.JIComFactory;

public class MSPowerPoint {

	private JIComServer comStub = null;
	private IJIDispatch dispatch = null;
	private IJIComObject unknown = null;

	public MSPowerPoint(String address, String[] args) throws JIException, UnknownHostException
	{
		JISession session = JISession.createSession(args[1],args[2],args[3]);
		comStub = new JIComServer(JIProgId.valueOf(session,"PowerPoint.Application"),address,session);
	}

	public void startPowerPoint() throws JIException
	{
		unknown = comStub.createInstance();
		dispatch = (IJIDispatch)JIComFactory.instantiateComObject(JIComFactory.IID_IDispatch,unknown);
	}

	public void showPowerPoint() throws JIException
	{
		int dispId = dispatch.getIDsOfNames("Visible");
		JIVariant variant = new JIVariant(-1);
		dispatch.put(dispId,variant);
	}

	public void performOp() throws JIException, InterruptedException
	{
		//JIVariant variant = dispatch.get("Presentations");
		//JIInterfacePointer ptr = variant.getObjectAsInterfacePointer();
		//IJIDispatch presentations = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
		IJIDispatch presentations = (IJIDispatch)dispatch.get("Presentations").getObjectAsComObject(unknown);

		for (int i = 0; i < 2; i++)
		{
			JIVariant results[] = presentations.callMethodA("Add",new Object[]{JIVariant.OPTIONAL_PARAM()});
			//variant = results[0];
			//ptr = variant.getObjectAsInterfacePointer();
			//IJIDispatch presentation = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
			IJIDispatch presentation = (IJIDispatch)results[0].getObjectAsComObject(unknown);
			//variant = presentation.get("Slides");
			//ptr = variant.getObjectAsInterfacePointer();
			//IJIDispatch slides = (IJIDispatch)JIComFactory.createCOMInstance(unknown,ptr);
			IJIDispatch slides = (IJIDispatch)presentation.get("Slides").getObjectAsComObject(unknown);

			results = slides.callMethodA("Add", new Object[]{new Integer(1),new Integer(1)});
			//variant = results[0];
			//ptr = variant.getObjectAsInterfacePointer();
			IJIDispatch slide = (IJIDispatch)results[0].getObjectAsComObject(unknown);

			//variant = slide.get("Shapes");
			//ptr = variant.getObjectAsInterfacePointer();
			IJIDispatch shapes = (IJIDispatch)slide.get("Shapes").getObjectAsComObject(unknown);

			//variant = shapes.get("Title");
			//ptr = variant.getObjectAsInterfacePointer();
			IJIDispatch shape = (IJIDispatch)shapes.get("Title").getObjectAsComObject(unknown);

			//variant = shape.get("TextFrame");
			//ptr = variant.getObjectAsInterfacePointer();
			IJIDispatch textframe = (IJIDispatch)shape.get("TextFrame").getObjectAsComObject(unknown);

			//variant = textframe.get("TextRange");
			//ptr = variant.getObjectAsInterfacePointer();
			IJIDispatch textrange = (IJIDispatch)textframe.get("TextRange").getObjectAsComObject(unknown);

			if (i == 0)
			{
				textrange.put("Text",new JIString("Presentation1").Variant);
				presentation.callMethod("SaveAs", new Object[]{new JIString("C:\\temp\\presentation1.ppt").Variant,JIVariant.OPTIONAL_PARAM(),new Integer(-1)});
				Thread.sleep(3000);
				presentation.callMethod("Close");
			}
			else
			{
				textrange.put("Text",new JIString("Presentation2").Variant);
				slides.callMethod("InsertFromFile", new Object[]{new JIString("C:\\temp\\presentation1.ppt"),new Integer(1), new Integer(1), new Integer(1)});
				presentation.callMethod("SaveAs", new Object[]{new JIString("C:\\temp\\presentation2.ppt"),JIVariant.OPTIONAL_PARAM(),new Integer(-1)});
				Thread.sleep(3000);
				presentation.callMethod("Close");

				dispatch.callMethod("Quit");
			}


		}

		JISession.destroySession(dispatch.getAssociatedSession());
	}

	public static void main(String[] args) {

		try {
				if (args.length < 4)
			    {
			    	System.out.println("Please provide address domain username password");
			    	return;
			    }
				MSPowerPoint test = new MSPowerPoint(args[0],args);
				test.startPowerPoint();
				test.showPowerPoint();
				test.performOp();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}





}
