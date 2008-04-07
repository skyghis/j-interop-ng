package org.jinterop.dcom.win32;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIUnknown;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
/**
 * This interface provides functionality of an embedded object. 
 * @since 1.0
 *
 */
public interface IJIOleObject extends IJIUnknown {

	 public static final String IID = "00000112-0000-0000-C000-000000000046";
	 
	 //int advise(IAdviseSink pIAdviseSink) 
     
	 public void close(int dwSaveOption) throws JIException; 
	            
	 public void doVerb(int iVerb, JIStruct lpmsg, IJIOleClientSite pActiveSite, int lindex, int hwndParent, JIRect lprcPosRect) throws JIException; 
	            
	 //IEnumStatData enumAdvise() 
	            
	 //IEnumOleVerb enumVerbs() throws JIException;
	            
	 public IJIOleClientSite getClientSite() throws JIException;
	            
	 //public IJIDataObject getClipboardData(int dwReserved) throws JIException;
	            
	 public JIStruct getExtent(int dwDrawAspect) throws JIException; 
	            
	 public int getMiscStatus(int dwAspect) throws JIException;
	            
	 public IJIMoniker getMoniker(int dwAssign, int dwWhichMoniker) throws JIException; 
	            
	 public JIString getUserClassID() throws JIException;
	            
	 public JIString getUserType(int dwFormOfType) throws JIException; 
	            
	 //public void initFromData(IJIDataObject pDataObject, boolean fCreation, int dwReserved) throws JIException; 
	            
	 public void isUpToDate() throws JIException;
	            
	 public void setClientSite(IJIOleClientSite ppClientSite) throws JIException; 
	            
	 public void setColorScheme(JIStruct pLogpal) throws JIException; 
	            
	 public void setExtent(int dwDrawAspect, JIStruct pSIZE) throws JIException;
	            
	 public void setHostNames(JIString szContainerApp, JIString szContainerObj) throws JIException; 
	            
	 public void setMoniker(int dwWhichMoniker, IJIMoniker pmIMoniker) throws JIException; 
	            
	 public void unadvise(int dwConnection) throws JIException; 
	            
	 public void update() throws JIException; 
	 

}
