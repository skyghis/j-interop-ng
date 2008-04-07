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
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIInterfacePointer;
import org.jinterop.dcom.core.JIParameterObject;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;

/**
 * Frame container for Ole controls. 
 * 
 * @since 1.07
 *
 */
public final class JIOleFrame implements IJIOleInPlaceFrame {

	
	public static final String IID_CommandTarget = "b722bccb-4e68-101b-a2bc-00aa00404770";
	private final JIInterfacePointer interfacePointerOfSelf;
	private IJIComObject pActiveObject = null;
	private JIOleDocument oleDocument = null;
	private final IJIComObject inplaceObject;
	private int childFrameId = -1;
	private final int parentFrameId;
	
	/** Creates a frame object.
	 * 
	 * @param session
	 * @param frameIdentifier uniquely identifies the parent frame (Swing JFrame).
	 * @param inplaceObject object to be activated here.
	 */
	public JIOleFrame(JISession session, int frameIdentifier, IJIComObject inplaceObject) throws JIException
	{
		interfacePointerOfSelf = registerInterfacePointer(session);
		parentFrameId = frameIdentifier;
		this.inplaceObject = inplaceObject;
		oleDocument = new JIOleDocument(session,inplaceObject);
	}
	
	public void testShowWindow() throws JIException
	{
		oleDocument.testShowWindow();
	}
	
	private JIInterfacePointer registerInterfacePointer(JISession session) throws JIException
	{
		
		JIInterfaceDefinition interfaceDefinition = new JIInterfaceDefinition(IJIOleInPlaceFrame.IID);
		//interfaceDefinition.
		//Add in the same order as in IDL
		
		JIStruct rect = new JIStruct();
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		rect.addMember(Integer.class);
		
		//GetWindow
		JIParameterObject getWindowParamObject = new JIParameterObject();
		getWindowParamObject.addInParamAsType(Integer.class, JIFlags.FLAG_NULL);
		JIMethodDescriptor methodDescriptor = new JIMethodDescriptor("GetWindow",getWindowParamObject); 
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
		
		JIStruct olemenugroupwidths = new JIStruct();
		olemenugroupwidths.addMember(new JIArray(Integer.class,new int[]{6},1,false));
		
		//InsertMenus
		JIParameterObject insertMenusParamObject = new JIParameterObject();
		insertMenusParamObject.addInParamAsObject(Integer.class, JIFlags.FLAG_NULL);
		insertMenusParamObject.addInParamAsObject(olemenugroupwidths, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("InsertMenus",insertMenusParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//SetMenu
		JIParameterObject setMenuParamObject = new JIParameterObject();
		setMenuParamObject.addInParamAsObject(Integer.class, JIFlags.FLAG_NULL);
		setMenuParamObject.addInParamAsObject(Integer.class, JIFlags.FLAG_NULL);
		setMenuParamObject.addInParamAsObject(Integer.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("SetMenu",setMenuParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//RemoveMenus
		JIParameterObject removeMenusParamObject = new JIParameterObject();
		setMenuParamObject.addInParamAsObject(Integer.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("RemoveMenus",removeMenusParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//SetStatusText
		JIParameterObject setStatusTextParamObject = new JIParameterObject();
		setStatusTextParamObject.addInParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("SetStatusText",setStatusTextParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		//EnableModeless
		JIParameterObject enableModelessParamObject = new JIParameterObject();
		enableModelessParamObject.addInParamAsObject(Boolean.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("EnableModeless",enableModelessParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);

		JIStruct msg = new JIStruct();
		msg.addMember(Integer.class);
		msg.addMember(Integer.class);
		msg.addMember(Integer.class);
		msg.addMember(Integer.class);
		msg.addMember(Integer.class);
		JIStruct point = new JIStruct();
		point.addMember(Integer.class);
		point.addMember(Integer.class);
		msg.addMember(point);
		//TranslateAccelerator
		JIParameterObject translateAcceleratorParamObject = new JIParameterObject();
		translateAcceleratorParamObject.addInParamAsObject(msg, JIFlags.FLAG_NULL);
		translateAcceleratorParamObject.addInParamAsObject(Short.class, JIFlags.FLAG_NULL);
		methodDescriptor = new JIMethodDescriptor("TranslateAccelerator",translateAcceleratorParamObject); 
		interfaceDefinition.addMethodDescriptor(methodDescriptor);
		
		JIJavaCoClass javaCoClass = new JIJavaCoClass(interfaceDefinition,this);
		List supportedInterfaces = new ArrayList();
		supportedInterfaces.add(IID_CommandTarget);
		supportedInterfaces.add(IJIOleInPlaceUIWindow.IID);
		supportedInterfaces.add(IJIOleWindow.IID);
		javaCoClass.setSupportedEventInterfaces(supportedInterfaces);	
		return JIInterfacePointer.getInterfacePointer(session,javaCoClass);
	}

	
	public void enableModeless(boolean fEnable) throws JIException {
//		TODO later on pass this to the parent frame
		System.out.println(fEnable);
		
	}

	public JIStruct insertMenus(int hmenuShared, JIStruct lpMenuWidths) throws JIException {
		//do nothing
		int i = 0;
		i++;
		return null;
	}

	public void removeMenus(int hmenuShared) throws JIException {
//		do nothing
		int i = 0;
		i++;
		
	}

	public void setMenu(int hmenuShared, int holemenu, int hwndActiveObject) throws JIException {
//		do nothing
		int i = 0;
		i++;
		
	}

	public void setStatusText(JIString pszStatusText) throws JIException {
		//TODO later on pass this to the parent frame
		System.out.println(pszStatusText);
		
	}

	public void translateAccelerator(JIStruct lpmsg, int wID) throws JIException {
//		do nothing
		int i = 0;
		i++;
		
	}

	//sizing up the space...
	public JIStruct getBorder() throws JIException {
		// For now hardcoding the size , will have to take it from the parent frame.
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
		return rect;
	}

	//called by the inner object to request for this much space
	public void requestBorderSpace(JIStruct pborderwidths) throws JIException {

		if (pborderwidths == null)
		{
			throw new JIException(JIErrorCodes.E_INVALIDARG);
		}
		
		/**
		 *  LONG left; 
		 	LONG top; 
		 	LONG right; 
		 	LONG bottom;
		 */
		
		int left = ((Integer)pborderwidths.getMember(0)).intValue();
		int top = ((Integer)pborderwidths.getMember(1)).intValue();
		int right = ((Integer)pborderwidths.getMember(2)).intValue();
		int bottom = ((Integer)pborderwidths.getMember(3)).intValue();
		
		  if ((left + right > 0x2F8/2) ||
			        (top + bottom > 0x1d6/2))
		  {
			  throw new JIException(JIErrorCodes.INPLACE_E_NOTOOLSPACE);
		  }
	}

	public void setActiveObject(JIInterfacePointer pActiveObject, JIString pszObjName) throws JIException {
		
		if (pActiveObject != null)
		{
			this.pActiveObject = JIComFactory.createCOMInstance(inplaceObject,pActiveObject);	
			
			JICallObject callObject = new JICallObject(this.pActiveObject.getIpid(),true);
			callObject.setOpnum(0);
			callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);
			childFrameId = ((Integer)(this.pActiveObject.call(callObject))[0]).intValue(); 
		}
		
	}

	public void setBorderSpace(JIStruct pborderwidths) throws JIException {
		// TODO right now doing nothing.
		int i = 0;
		i++;
	}

	public int getWindow() throws JIException {
		return parentFrameId;
	}

	public void contextSensitiveHelp(boolean fEnterMode) throws JIException {
		throw new JIException(JIErrorCodes.E_NOTIMPL);
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
	
	
	
	
	
	
	
	
	
	
	
	
}
