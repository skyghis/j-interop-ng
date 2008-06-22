package org.jinterop.dcom.test;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIComFactory;

public class DWebBrowserEvents2 {


	public DWebBrowserEvents2()
	{
		
	}
//	[id(0x00000070), helpstring("Fired when the PutProperty method has been called.")]
//	 void PropertyChange([in] BSTR szProperty);
	public void PropertyChange(JIString szProperty)
	{
		System.out.println("PropertyChange -> " + szProperty.getString());
	}
	
	
//	[id(0x000000fa), helpstring("Fired before navigate occurs in the given WebBrowser (window or frameset element). The processing of this navigation may be modified.")]
//	 void BeforeNavigate2(
//	                 [in] IDispatch* pDisp, 
//	                 [in] VARIANT* URL, 
//	                 [in] VARIANT* Flags, 
//	                 [in] VARIANT* TargetFrameName, 
//	                 [in] VARIANT* PostData, 
//	                 [in] VARIANT* Headers, 
//	                 [in, out] VARIANT_BOOL* Cancel);

	public JIVariant BeforeNavigate2(IJIComObject dispatch,JIVariant URL,JIVariant Flags,JIVariant TargetFrameName,
										JIVariant PostData, JIVariant Headers, JIVariant Cancel) throws JIException 
	{
		dispatch = JIComFactory.narrowObject(dispatch);
		JIVariant realURL = URL;
		while (realURL.isByRefFlagSet())
		{
			realURL = realURL.getObjectAsVariant();
		}
		
		System.out.println("BeforeNavigate2  -> " + realURL.getObjectAsString().getString());
		
		//uncomment and return this to stop loading the page
		//JIVariant variant = new JIVariant(true,true);
		
		return Cancel;
	}
	
   //[id(0x00000066), helpstring("Statusbar text changed.")]
    public void StatusTextChange(JIString text)
    {
    	System.out.println("StatusTextChange -> " + text.getString());
    }
    
    //[id(0x0000006c), helpstring("Fired when download progress is updated.")]
    public void ProgressChange(
                     int Progress, 
                     int ProgressMax)
    {
    	System.out.println("ProgressChange -> " + Progress + " , " + ProgressMax);
    }
    
    //[id(0x00000069), helpstring("The enabled state of a command changed.")]
    public void CommandStateChange(
                    int Command, 
                    boolean Enable)
    {
    	System.out.println("CommandStateChange -> " + Command + " , " + Enable);
    }
    
    //    [id(0x0000006a), helpstring("Download of a page started.")]
    public void DownloadBegin()
    {
    	System.out.println("DownloadBegin");
    }
    
    //    [id(0x00000068), helpstring("Download of page complete.")]
    public void DownloadComplete()
    {
    	System.out.println("DownloadComplete");
    }

    //[id(0x00000071), helpstring("Document title changed.")]
    public void TitleChange(JIString Text)
    {
    	System.out.println("TitleChange -> " + Text.getString());
    }
    
   //[id(0x000000fb), helpstring("A new, hidden, non-navigated WebBrowser window is needed.")]
    public JIVariant NewWindow2(JIVariant ppDisp, 
                            JIVariant Cancel) throws JIException
    {
    	System.out.println("NewWindow2 -> "  + Cancel.getObjectAsBoolean());
    	return Cancel;
    }
    
    //[id(0x000000fc), helpstring("Fired when the document being navigated to becomes visible and enters the navigation stack.")]
    public void NavigateComplete2(
                         IJIComObject pDisp, 
                        JIVariant URL) throws JIException
    {
    	pDisp = JIComFactory.narrowObject(pDisp);
		JIVariant realURL = URL;
		while (realURL.isByRefFlagSet())
		{
			realURL = realURL.getObjectAsVariant();
		}

    	System.out.println("NavigateComplete2 -> " + pDisp.getInterfaceIdentifier() + " , "  + realURL.getObjectAsString().getString());
    }
    
    //[id(0x00000103), helpstring("Fired when the document being navigated to reaches ReadyState_Complete.")]
    public void DocumentComplete(
                        IJIComObject pDisp, 
                        JIVariant URL) throws JIException
    {
    	System.out.println("DocumentComplete -> " + pDisp.getInterfaceIdentifier() + " , "  + URL);
    }
    
    //[id(0x000000fd), helpstring("Fired when application is quiting.")]
    public void OnQuit()
    {
    	System.out.println("OnQuit -> ");
    }
   
    //[id(0x000000fe), helpstring("Fired when the window should be shown/hidden")]
    public void OnVisible(boolean Visible)
    {
    	System.out.println("OnVisible -> " + Visible);
    }
    
    //[id(0x000000ff), helpstring("Fired when the toolbar  should be shown/hidden")]
    public void OnToolBar(boolean ToolBar)
    {
    	System.out.println("OnToolBar -> " + ToolBar);
    }

    //[id(0x00000100), helpstring("Fired when the menubar should be shown/hidden")]
    public void OnMenuBar(boolean MenuBar)
    {
    	System.out.println("OnMenuBar -> " + MenuBar);
    }
    
    //[id(0x00000101), helpstring("Fired when the statusbar should be shown/hidden")]
    public void OnStatusBar(boolean StatusBar)
    {
    	System.out.println("OnStatusBar -> " + StatusBar);
    }
    
    //[id(0x00000102), helpstring("Fired when fullscreen mode should be on/off")]
    public void OnFullScreen(boolean FullScreen)
    {
    	System.out.println("OnFullScreen -> " + FullScreen);
    }
    
    //[id(0x00000104), helpstring("Fired when theater mode should be on/off")]
    public void OnTheaterMode(boolean TheaterMode)
    {
    	System.out.println("OnTheaterMode -> " + TheaterMode);
    }
    
    //[id(0x00000106), helpstring("Fired when the host window should allow/disallow resizing")]
    public void WindowSetResizable(boolean Resizable)
    {
    	System.out.println("OnResizable -> " + Resizable);
    }
    
    //[id(0x00000108), helpstring("Fired when the host window should change its Left coordinate")]
    public void WindowSetLeft(int Left)
    {
    	System.out.println("WindowSetLeft - > " + Left);
    }
    
    //[id(0x00000109), helpstring("Fired when the host window should change its Top coordinate")]
    public void WindowSetTop(int Top)
    {
    	System.out.println("WindowSetTop - > " + Top);
    }
    
    //[id(0x0000010a), helpstring("Fired when the host window should change its width")]
    public void WindowSetWidth(int Width)
    {
    	System.out.println("WindowSetWidth - > " + Width);	
    }
    
    //[id(0x0000010b), helpstring("Fired when the host window should change its height")]
    public void WindowSetHeight(int Height)
    {
    	System.out.println("WindowSetHeight - > " + Height);
    }
    
    //[id(0x00000107), helpstring("Fired when the WebBrowser is about to be closed by script")]
    public JIVariant WindowClosing(
    		boolean IsChildWindow, 
                        JIVariant Cancel) throws JIException
    {
    	System.out.println("WindowClosing -> " + IsChildWindow + " , " +  Cancel.getObjectAsBoolean());
    	return Cancel;
    }
    
    //[id(0x0000010c), helpstring("Fired to request client sizes be converted to host window sizes")]
    public Integer[] ClientToHostWindow(
                        int CX, 
                        int CY)
    {
    	System.out.println("ClientToHostWindow - > " + CX + " , " + CY);
    	return new Integer[] {new Integer(CX),new Integer(CY)};
    }
    
    //    [id(0x0000010d), helpstring("Fired to indicate the security level of the current web page contents")]
    public void SetSecureLockIcon(int SecureLockIcon)
    {
    	System.out.println("SetSecureLockIcon - > " + SecureLockIcon);
    }
    
    //[id(0x0000010e), helpstring("Fired to indicate the File Download dialog is opening")]
    public JIVariant FileDownload(boolean noIdeaWhat,JIVariant Cancel) throws JIException
    {
    	System.out.println("FileDownload - > " + Cancel);
    	return Cancel;
    }
   
    //[id(0x0000010f), helpstring("Fired when a binding error occurs (window or frameset element).")]
    public JIVariant  NavigateError(
                        IJIComObject pDisp, 
                        JIVariant URL, 
                        JIVariant Frame, 
                        JIVariant StatusCode, 
                        JIVariant Cancel) throws JIException
    {
    	System.out.println("NavigateError - > " + URL.getObjectAsString());
    	return Cancel;
    }
    
   //[id(0x000000e1), helpstring("Fired when a print template is instantiated.")]
   public void PrintTemplateInstantiation(IJIComObject pDisp)
   {
	   System.out.println("PrintTemplateInstantiation - > " + pDisp.getInterfaceIdentifier());
   }
   
   //[id(0x000000e2), helpstring("Fired when a print template destroyed.")]
   public void PrintTemplateTeardown(IJIComObject pDisp)
   {
	   System.out.println("PrintTemplateTeardown - > " + pDisp.getInterfaceIdentifier());
   }
   
   //[id(0x000000e3), helpstring("Fired when a page is spooled. When it is fired can be changed by a custom template.")]
   public void UpdatePageStatus(
                        IJIComObject pDisp, 
                        JIVariant nPage, 
                        JIVariant fDone)
   {
	   System.out.println("UpdatePageStatus - > " + pDisp.getInterfaceIdentifier());
   }

   //[id(0x00000110), helpstring("Fired when the global privacy impacted state changes")]
   public void PrivacyImpactedStateChange(boolean bImpacted)
   {
	   System.out.println("PrivacyImpactedStateChange - > " + bImpacted);
   }
   
   //[id(0x00000111), helpstring("A new, hidden, non-navigated WebBrowser window is needed.")]
   public JIVariant NewWindow3(
                        JIVariant ppDisp, 
                        JIVariant Cancel, 
                        int dwFlags, 
                        JIString bstrUrlContext, 
                        JIString bstrUrl) throws JIException
   {
	   System.out.println("NewWindow3 - > " + ppDisp + " , " + Cancel.getObjectAsBoolean() + " , " + bstrUrl.getString());
	   return Cancel;
   }

   public void SetPhishingFilterStatus(int PhishingFilterStatus) throws JIException
   {
	   System.out.println("SetPhishingFilterStatus - > " + PhishingFilterStatus);
	   //return Cancel;
   }

   public void WindowStateChanged(
           int dwWindowStateFlags, 
           int dwValidFlagsMask)
   {
	   System.out.println("WindowStateChanged - > " + dwWindowStateFlags + " , " + dwValidFlagsMask);
   }


	
}
