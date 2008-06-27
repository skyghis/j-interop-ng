package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JILocalInterfaceDefinition;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JILocalMethodDescriptor;
import org.jinterop.dcom.core.JILocalParamsDescriptor;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class MSInternetExplorer {

	private JIComServer comServer = null;
	private JISession session = null;
	private IJIComObject ieObject = null;
	private IJIDispatch ieObjectDispatch = null;
	private String identifier = null;
	public MSInternetExplorer(String address, String[] args) throws JIException, UnknownHostException
	{
		session = JISession.createSession(args[1],args[2],args[3]);
//		session.useSessionSecurity(true);
		comServer = new JIComServer(JIProgId.valueOf("InternetExplorer.Application"),address,session);
		ieObject = comServer.createInstance();
		IJIComObject ieObjectWebBrowser2 = (IJIComObject)ieObject.queryInterface("D30C1661-CDAF-11D0-8A3E-00C04FC9E26E");
		ieObjectDispatch = (IJIDispatch)JIObjectFactory.narrowObject((IJIComObject)ieObject.queryInterface(IJIDispatch.IID));

	}

	private void setVisible() throws JIException
	{

		int dispId = ieObjectDispatch.getIDsOfNames("Visible");
		ieObjectDispatch.put(dispId,new JIVariant(Boolean.TRUE));
		ieObjectDispatch.put("AddressBar",new JIVariant(Boolean.TRUE));
		ieObjectDispatch.put("MenuBar",new JIVariant(Boolean.TRUE));
		ieObjectDispatch.put("ToolBar",new JIVariant(Boolean.TRUE));

	}

	private void navigateToUrl(String url) throws JIException
	{
		//ieObjectDispatch.put("Top",new JIVariant(new Integer(600)));
		//ieObjectDispatch.put("Left",new JIVariant(new Integer(700)));
		ieObjectDispatch.callMethod("Navigate2",new Object[]{new JIString(url),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM(),JIVariant.OPTIONAL_PARAM()});
	}

	private void attachCallBack() throws JIException
	{

	/**
	 * The JIJavaCOClass is a representation for a Java server class. It's there so that when we get to the next version of the library, I am able to support full bi-directional access. Currently, you can implement any IDL of an existing COM server using the JIJavaCOClass and
	 * pass it's interface pointer instead of the original COM server and it will work fine. Similar mechanism is exploited for call backs.In our case I had to implement DWebBrowserEvents interface.
	 *
	 * IJavaCoClass javaComponent = new JILocalCoClass(new JILocalInterfaceDefinition("45B5FC0C-FAC2-42bd-923E-2B221A89E092"),DWebBrowserEvents2.class);
	 *
	 * This definition create a Java component with an IID of 45B5FC0C-FAC2-42bd-923E-2B221A89E092...I just made this one up for uniquely classifying this class...you can equate this to a lib identifier of COM IDL. This is required if there are multilple interfaces being implemented in the same Java Class.
	 * If you have only one...you can put it's IID here. I just did not do it for showing the user a possiblity.
	 *
	 * The JIJavaCOClass has the option of instantiating the DWebBrowserEvents.class or it could use another ctor to pass an already instantiated object. In latter scenario, the object would be used as target for the events instead of instantiating a new one from DWebBrowserEvents.class.
	 * Now that we have a Java server, we need to define the methods\events it will handle.
	 *
	 * This is done using the Method descriptors which are themselves described using the Parameter Objects.
	 *
	 * JILocalParamsDescriptor propertyChangeObject = new JILocalParamsDescriptor();
	 *
	 * This creates a Parameter Object, capable of defining a IN or OUT type for a Method.
	 *
	 * like:-
	 * propertyChangeObject.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
	 *
	 * JILocalMethodDescriptor methodDescriptor = new JILocalMethodDescriptor("PropertyChange",0x70,propertyChangeObject);
	 * javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);
	 *
	 * This declares a method descriptor. The first parameter in the ctor is the API name of the api to implement, the second one is it's OP number.
	 * This one can be obtained from the IDL\TypeLib. And the third param is the parameterObject describing the input\output types of this method.
	 * If you do not want to use this ctor, there is another, which sequentially increments the method numbers starting from 1.
	 * The calls below add a new interface IID to this Java server. It simply means that the server supports this interface definition.
	 *
	 * ArrayList list = new ArrayList();
	 * list.add("34A715A0-6587-11D0-924A-0020AFC7AC4D");
	 * javaComponent.setSupportedEventInterfaces(list);
	 *
	 * This will be the list of all COM interfaces which this Java class supports or implements.
	 *
	 * The next call attaches the event handler (our JILocalCoClass) to the actual COM server for recieving events for the interface identified by the IID.
	 * There can be many such calls on the same COM server for different IIDs.
	 * identifier = JIObjectFactory.attachEventHandler(ieObject,"34A715A0-6587-11D0-924A-0020AFC7AC4D",JIInterfacePointer.getInterfacePointer(session,javaComponent));
	 *
	 * Now whether you use IJIDispatch or not, events will work regardless of that. The COM object you have to use in the attachEventHandler is the COM Object on
	 * which you did the queryinterface for the IJIDispatch.
	 *
	 **/
		JILocalCoClass javaComponent = new JILocalCoClass(new JILocalInterfaceDefinition("34A715A0-6587-11D0-924A-0020AFC7AC4D"),DWebBrowserEvents2.class);

		JILocalParamsDescriptor propertyChangeObject = new JILocalParamsDescriptor();
		propertyChangeObject.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
		JILocalMethodDescriptor methodDescriptor = new JILocalMethodDescriptor("PropertyChange",0x70,propertyChangeObject);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);


		JILocalParamsDescriptor navigateObject = new JILocalParamsDescriptor();
		navigateObject.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		navigateObject.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("BeforeNavigate2",0xFA,navigateObject);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor StatusTextChange = new JILocalParamsDescriptor();
		StatusTextChange.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("StatusTextChange",0x66,StatusTextChange);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor ProgressChange = new JILocalParamsDescriptor();
		ProgressChange.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		ProgressChange.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("ProgressChange",0x6c,ProgressChange);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor CommandStateChange = new JILocalParamsDescriptor();
		CommandStateChange.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		CommandStateChange.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("CommandStateChange",0x69,CommandStateChange);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor DownloadBegin = new JILocalParamsDescriptor();
		methodDescriptor = new JILocalMethodDescriptor("DownloadBegin",0x6a,DownloadBegin);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor DownloadComplete = new JILocalParamsDescriptor();
		methodDescriptor = new JILocalMethodDescriptor("DownloadComplete",0x68,DownloadComplete);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor TitleChange = new JILocalParamsDescriptor();
		TitleChange.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("TitleChange",0x71,TitleChange);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor NewWindow2 = new JILocalParamsDescriptor();
		NewWindow2.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NewWindow2.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("NewWindow2",0xfb,NewWindow2);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor NavigateComplete2 = new JILocalParamsDescriptor();
		NavigateComplete2.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		NavigateComplete2.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("NavigateComplete2",0xfc,NavigateComplete2);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor DocumentComplete = new JILocalParamsDescriptor();
		DocumentComplete.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		DocumentComplete.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("DocumentComplete",0x103,DocumentComplete);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnQuit = new JILocalParamsDescriptor();
		methodDescriptor = new JILocalMethodDescriptor("OnQuit",0xfd,OnQuit);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnVisible = new JILocalParamsDescriptor();
		OnVisible.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnVisible",0xfe,OnVisible);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnToolBar = new JILocalParamsDescriptor();
		OnToolBar.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnToolBar",0xff,OnToolBar);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnMenuBar = new JILocalParamsDescriptor();
		OnMenuBar.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnMenuBar",0x100,OnMenuBar);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnStatusBar = new JILocalParamsDescriptor();
		OnStatusBar.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnStatusBar",0x101,OnStatusBar);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnFullScreen = new JILocalParamsDescriptor();
		OnFullScreen.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnFullScreen",0x102,OnFullScreen);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor OnTheaterMode = new JILocalParamsDescriptor();
		OnTheaterMode.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("OnTheaterMode",0x104,OnTheaterMode);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowSetResizable = new JILocalParamsDescriptor();
		WindowSetResizable.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowSetResizable",0x106,WindowSetResizable);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowSetLeft = new JILocalParamsDescriptor();
		WindowSetLeft.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowSetLeft",0x108,WindowSetLeft);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowSetTop = new JILocalParamsDescriptor();
		WindowSetTop.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowSetTop",0x109,WindowSetTop);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowSetWidth = new JILocalParamsDescriptor();
		WindowSetWidth.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowSetWidth",0x10a,WindowSetWidth);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowSetHeight = new JILocalParamsDescriptor();
		WindowSetHeight.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowSetHeight",0x10b,WindowSetHeight);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowClosing = new JILocalParamsDescriptor();
		WindowClosing.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		WindowClosing.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowClosing",0x107,WindowClosing);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor ClientToHostWindow = new JILocalParamsDescriptor();
		ClientToHostWindow.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		ClientToHostWindow.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("ClientToHostWindow",0x10c,ClientToHostWindow);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor SetSecureLockIcon = new JILocalParamsDescriptor();
		SetSecureLockIcon.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("SetSecureLockIcon",0x10d,SetSecureLockIcon);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor FileDownload = new JILocalParamsDescriptor();
		FileDownload.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		FileDownload.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("FileDownload",0x10e,FileDownload);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor NavigateError = new JILocalParamsDescriptor();
		NavigateError.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		NavigateError.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NavigateError.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NavigateError.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NavigateError.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("NavigateError",0x10f,NavigateError);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor NewWindow3 = new JILocalParamsDescriptor();
		NewWindow3.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NewWindow3.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		NewWindow3.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		NewWindow3.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
		NewWindow3.addInParamAsType(JIString.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("NewWindow3",0x111,NewWindow3);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor PrintTemplateInstantiation = new JILocalParamsDescriptor();
		PrintTemplateInstantiation.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("PrintTemplateInstantiation",0xe1,PrintTemplateInstantiation);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor PrintTemplateTeardown = new JILocalParamsDescriptor();
		PrintTemplateTeardown.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("PrintTemplateTeardown",0xe2,PrintTemplateTeardown);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor SetPhishingFilterStatus = new JILocalParamsDescriptor();
		SetPhishingFilterStatus.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("SetPhishingFilterStatus",0x11A,SetPhishingFilterStatus );
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor WindowStateChanged = new JILocalParamsDescriptor();
		WindowStateChanged.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		WindowStateChanged.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("WindowStateChanged",0x11B,WindowStateChanged);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);


		JILocalParamsDescriptor UpdatePageStatus = new JILocalParamsDescriptor();
		UpdatePageStatus.addInParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
		UpdatePageStatus.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		UpdatePageStatus.addInParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("UpdatePageStatus",0xe3,UpdatePageStatus);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);

		JILocalParamsDescriptor PrivacyImpactedStateChange = new JILocalParamsDescriptor();
		PrivacyImpactedStateChange.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JILocalMethodDescriptor("PrivacyImpactedStateChange",0x110,PrivacyImpactedStateChange);
		javaComponent.getInterfaceDefinition().addMethodDescriptor(methodDescriptor);


		ArrayList list = new ArrayList();
		list.add("34A715A0-6587-11D0-924A-0020AFC7AC4D");
		javaComponent.setSupportedEventInterfaces(list);



		identifier = JIObjectFactory.attachEventHandler(ieObject,"34A715A0-6587-11D0-924A-0020AFC7AC4D",JIObjectFactory.buildObject(session,javaComponent));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //for call backs
	}

	private void detachCallBack() throws JIException
	{
		JIObjectFactory.detachEventHandler(ieObject,identifier);
		JISession.destroySession(ieObjectDispatch.getAssociatedSession());
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
				l.setLevel(Level.INFO);
				MSInternetExplorer internetExplorer = new MSInternetExplorer(args[0],args);
				internetExplorer.setVisible();
				internetExplorer.attachCallBack();
				internetExplorer.navigateToUrl("http://j-interop.sourceforge.net");
				Thread.sleep(30000); //for call backs
				internetExplorer.detachCallBack();

		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}

