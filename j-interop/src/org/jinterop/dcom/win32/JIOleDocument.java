/**j-Interop (Pure Java implementation of DCOM protocol)  
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jinterop.dcom.win32;

import java.util.ArrayList;
import java.util.List;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIInterfaceDefinition;
import org.jinterop.dcom.common.JIJavaCoClass;
import org.jinterop.dcom.common.JIMethodDescriptor;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.IJIUnknown;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIInterfacePointer;
import org.jinterop.dcom.core.JIParameterObject;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;

/**
 * Document encapsulating the activated object.
 * @since 1.07
 *
 */
final class JIOleDocument implements IJIOleDocumentSite, IJIOleClientSite,
		IJIOleInPlaceUIWindow, IJIOleInPlaceSite {
	
	private final IJIComObject oleObject;
	private IJIComObject inplaceObject = null;
	private IJIComObject runnableObject = null;
	private final JIInterfacePointer interfacePointerOfSelf; 
	
	JIOleDocument(JISession session,IJIComObject inplaceObject) throws JIException
	{
		this.inplaceObject = inplaceObject;
		//IOleObject IID.
		oleObject = (IJIComObject)inplaceObject.queryInterface("00000112-0000-0000-C000-000000000046");
		
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
		
		//run the object, object must support IRunnableObject (IID below)
//		runnableObject = (IJIComObject)oleObject.queryInterface("00000126-0000-0000-C000-000000000046");
//		JICallObject callObject = new JICallObject(runnableObject.getIpid(),true);
//		callObject.setOpnum(1);
//		callObject.addInParamAsPointer(new JIPointer(null),JIFlags.FLAG_NULL);
//		runnableObject.call(callObject);
//		
		interfacePointerOfSelf = registerInterfacePointer(session);
		
		JICallObject callObject = new JICallObject(oleObject.getIpid(),true);
		callObject.setOpnum(0);
		callObject.addInParamAsInterfacePointer(interfacePointerOfSelf,JIFlags.FLAG_NULL);
		oleObject.call(callObject);
		
//		callObject = new JICallObject(runnableObject.getIpid(),true);
//		callObject.setOpnum(3);
//		callObject.addInParamAsBoolean(true,JIFlags.FLAG_NULL);
//		callObject.addInParamAsBoolean(false,JIFlags.FLAG_NULL);
//		runnableObject.call(callObject);
		
//		callObject = new JICallObject(oleObject.getIpid(),true);
//		callObject.setOpnum(2);
//		callObject.addInParamAsString("TestVikramApp",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
//		callObject.addInParamAsPointer(new JIPointer(new JIString("TestContainer",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)),JIFlags.FLAG_NULL);
//		oleObject.call(callObject);
		
		
		
	}
	
	void testShowWindow() throws JIException
	{
		/**hr = _poleobj->DoVerb(OLEIVERB_INPLACEACTIVATE, NULL, static_cast<IOleClientSite*>(this), (UINT)-1, _hwndDoc, &rcView);
        if (hr == OLEOBJ_E_INVALIDVERB)
            hr = _poleobj->DoVerb(OLEIVERB_SHOW, NULL, static_cast<IOleClientSite*>(this), (UINT)-1, _hwndDoc, &rcView);
        */
		
		JICallObject callObject = new JICallObject(oleObject.getIpid(),true);
		callObject.setOpnum(8);
		callObject.addInParamAsInt(-5,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(0,JIFlags.FLAG_NULL);
		callObject.addInParamAsInterfacePointer(interfacePointerOfSelf,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(-1,JIFlags.FLAG_NULL);
		callObject.addInParamAsInt(1234,JIFlags.FLAG_NULL);
		JIStruct rect = new JIStruct();
		/**
		 *  LONG left; 
		 	LONG top; 
		 	LONG right; 
		 	LONG bottom;
		 */
		rect.addMember(new Integer(0x02a));
		rect.addMember(new Integer(0x003));
		rect.addMember(new Integer(0x2f8));
		rect.addMember(new Integer(0x1d6));
		callObject.addInParamAsStruct(rect,JIFlags.FLAG_NULL);
		oleObject.call(callObject);
		
		
		
	}

	public void activateMe(JIInterfacePointer pViewToActivate)
			throws JIException {
		// TODO Auto-generated method stub

	}

	public IJIUnknown queryInterface(String iid) throws JIException {
		//call won't come here, and should not be invoked directly
		throw new JIException(JIErrorCodes.E_NOTIMPL);
	}

	public void addRef() throws JIException {
		//call won't come here, and should not be invoked directly
		throw new JIException(JIErrorCodes.E_NOTIMPL);
	}

	public void release() throws JIException {
		//call won't come here, and should not be invoked directly
		throw new JIException(JIErrorCodes.E_NOTIMPL);
	}
	

	public void saveObject() throws JIException {
		//TODO for now ..do nothing ..
		int i = 0; 
		i++;
	}

	public JIInterfacePointer getContainer() throws JIException {
		// TODO Auto-generated method stub
		return null;
	}

	public void showObject() throws JIException {
		// TODO Auto-generated method stub

	}

	public void onShowWindow(boolean fShow) throws JIException {
		// TODO Auto-generated method stub

	}

	public void requestNewObjectLayout() throws JIException {
		// TODO Auto-generated method stub

	}

	public JIStruct getBorder() throws JIException {
		// TODO Auto-generated method stub
		return null;
	}

	public void requestBorderSpace(JIStruct pborderwidths) throws JIException {
		// TODO Auto-generated method stub

	}

	public void setActiveObject(JIInterfacePointer pActiveObject,
			JIString pszObjName) throws JIException {
		// TODO Auto-generated method stub

	}

	public void setBorderSpace(JIStruct pborderwidths) throws JIException {
		// TODO Auto-generated method stub

	}

	public int getWindow() throws JIException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void contextSensitiveHelp(boolean fEnterMode) throws JIException {
		// TODO Auto-generated method stub

	}

	public void canInPlaceActivate() throws JIException {
		// TODO Auto-generated method stub

	}

	public void deactivateAndUndo() throws JIException {
		// TODO Auto-generated method stub

	}

	public void discardUndoState() throws JIException {
		// TODO Auto-generated method stub

	}

	public JIStruct getWindowContext(JIStruct lpFrameInfo) throws JIException {
		// TODO Auto-generated method stub
		return null;
	}

	public void onInPlaceActivate() throws JIException {
		// TODO Auto-generated method stub

	}

	public void onInPlaceDeactivate() throws JIException {
		// TODO Auto-generated method stub

	}

	public void onPosRectChange(JIRect lprcPosRect) throws JIException {
		// TODO Auto-generated method stub

	}

	public void onUIActivate() throws JIException {
		// TODO Auto-generated method stub

	}

	public void onUIDeactivate(boolean fUndoable) throws JIException {
		// TODO Auto-generated method stub

	}

	public void scroll(int scrollX, int scrollY) throws JIException {
		// TODO Auto-generated method stub

	}

	protected void finalize()
	{
		JICallObject callObject = new JICallObject(runnableObject.getIpid(),true);
		callObject.setOpnum(3);
		callObject.addInParamAsBoolean(false,JIFlags.FLAG_NULL);
		callObject.addInParamAsBoolean(false,JIFlags.FLAG_NULL);
		try {
			runnableObject.call(callObject);
		} catch (JIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JIInterfacePointer registerInterfacePointer(JISession session) throws JIException
	{
		JIInterfaceDefinition interfaceDefinition = new JIInterfaceDefinition(IJIOleClientSite.IID);
		
		//Add in the same order as in IDL
		
		//SaveObject
		JIParameterObject SaveObjectParamObject = new JIParameterObject();
		JIMethodDescriptor methodDescriptor = new JIMethodDescriptor("SaveObject",SaveObjectParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//GetMoniker
		JIParameterObject GetMonikerParamObject = new JIParameterObject();
		GetMonikerParamObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		GetMonikerParamObject.addInParamAsType(Integer.class,JIFlags.FLAG_NULL);
		GetMonikerParamObject.addInParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("GetMoniker",GetMonikerParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//GetContainer
		JIParameterObject GetContainerParamObject = new JIParameterObject();
		GetContainerParamObject.addInParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("GetContainer",GetContainerParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//ShowObject
		JIParameterObject ShowObjectParamObject = new JIParameterObject();
		methodDescriptor = new JIMethodDescriptor("ShowObject",ShowObjectParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//OnShowWindow
		JIParameterObject OnShowWindowParamObject = new JIParameterObject();
		OnShowWindowParamObject.addInParamAsType(Boolean.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("OnShowWindow",OnShowWindowParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		
		//RequestNewObjectLayout
		JIParameterObject RequestNewObjectLayoutParamObject = new JIParameterObject();
		methodDescriptor = new JIMethodDescriptor("RequestNewObjectLayout",RequestNewObjectLayoutParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//ActivateMe
		JIParameterObject ActivateMeParamObject = new JIParameterObject();
		GetContainerParamObject.addInParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("ActivateMe",ActivateMeParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		JIStruct rect = new JIStruct();
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		
		//GetWindow
		JIParameterObject getWindowParamObject = new JIParameterObject();
		getWindowParamObject.addInParamAsType(Integer.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("GetWindow",getWindowParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//ContextSensitiveHelp
		JIParameterObject contextSensitiveHelpParamObject = new JIParameterObject();
		contextSensitiveHelpParamObject.addInParamAsType(Boolean.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("ContextSensitiveHelp",contextSensitiveHelpParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//GetBorder
		JIParameterObject getBorderParamObject = new JIParameterObject();
		getBorderParamObject.addInParamAsObject(rect, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("GetBorder",getBorderParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//RequestBorderSpace
		JIParameterObject requestBorderSpaceParamObject = new JIParameterObject();
		requestBorderSpaceParamObject.addInParamAsObject(rect, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("RequestBorderSpace",requestBorderSpaceParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//SetBorderSpace
		JIParameterObject setBorderSpaceParamObject = new JIParameterObject();
		setBorderSpaceParamObject.addInParamAsObject(rect, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("SetBorderSpace",setBorderSpaceParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//SetActiveObject
		JIParameterObject setActiveObjectParamObject = new JIParameterObject();
		setActiveObjectParamObject.addInParamAsObject(JIInterfacePointer.class, JIFlags.FLAG_NULL);
		setActiveObjectParamObject.addInParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("SetActiveObject",setActiveObjectParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//CanInPlaceActivate
		JIParameterObject CanInPlaceActivateParamObject = new JIParameterObject();
		methodDescriptor = new JIMethodDescriptor("CanInPlaceActivate",CanInPlaceActivateParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//OnInPlaceActivate
		JIParameterObject OnInPlaceActivateParamObject = new JIParameterObject();
		methodDescriptor = new JIMethodDescriptor("OnInPlaceActivate",OnInPlaceActivateParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//OnUIActivate
		JIParameterObject OnUIActivateParamObject = new JIParameterObject();
		methodDescriptor = new JIMethodDescriptor("OnUIActivate",OnInPlaceActivateParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//GetWindowContext
//		JIParameterObject GetWindowContextParamObject = new JIParameterObject();
//		getWindowParamObject.addInParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
//		getWindowParamObject.addInParamAsType(JIInterfacePointer.class,JIFlags.FLAG_NULL);
//		getWindowParamObject.addInParamAsObject(rect,JIFlags.FLAG_NULL);
//		getWindowParamObject.addInParamAsObject(rect,JIFlags.FLAG_NULL);
//		JIStruct inplaceFrameInfo = new JIStruct();
//		inplaceFrameInfo.a
//		getWindowParamObject.addInParamAsObject(inplaceFrameInfo,JIFlags.FLAG_NULL);
//		methodDescriptor = new JIMethodDescriptor("GetWindowContext",GetWindowContextParamObject); 
//		interfaceDefinition.addMethodDescriptor(methodDescriptor);
//		
		
		JIJavaCoClass javaCoClass = new JIJavaCoClass(interfaceDefinition,this);
		List supportedInterfaces = new ArrayList();
		supportedInterfaces.add(IJIOleDocumentSite.IID);
		supportedInterfaces.add(IJIOleInPlaceUIWindow.IID);
		supportedInterfaces.add(IJIOleWindow.IID);
		supportedInterfaces.add(IJIOleInPlaceSite.IID);
		
		javaCoClass.setSupportedEventInterfaces(supportedInterfaces);	
		return JIInterfacePointer.getInterfacePointer(session,javaCoClass);
	}

}
