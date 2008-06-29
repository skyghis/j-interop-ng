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

package org.jinterop.dcom.core;


/** Describe a method of the COM <code>IDL</code> to be used in Callback implementations.
 * Framework uses java reflection to invoke methods requested by COM clients so it is 
 * absolutely essential that java methods in the implementation class conform exactly to 
 * what is described in this object. 
 * <p>
 * <i>Please refer to MSInternetExplorer, Test_ITestServer2_Impl, SampleTestServer 
 * and MSShell examples for more details on how to use this class.</i><br>
 * 
 * @since 1.0
 */
public final class JILocalMethodDescriptor 
{
	private String methodName = null;
	private int methodNum = -1;
	private int dispId = -1;
	private Class[] inparametersAsClass = new Class[0];
	private JILocalParamsDescriptor parameters = null;

	/**Creates the method descriptor. The method number is set by the order in which this instance is 
	 * added to the <code>JILocalInterfaceDefinition</code>. This number is incremented by 1 for each subsequent 
	 * and new addition into interface definition.
	 * 
	 * @param methodName name of the method.
	 * @param parameters pass <code>null</code> if the method has no parameters. 
	 */
	public JILocalMethodDescriptor(String methodName,JILocalParamsDescriptor parameters)
	{
		this.methodName = methodName;
		setParameterObject(parameters);
	}

	/** Creates the method descriptor.
	 * 
	 * @param methodName name of the method.
	 * @param dispId <code>DISPID</code> of this method as in the <code>IDL</code> or the TypeLibrary.
	 * @param parameters pass <code>null</code> if the method has no parameters. 
	 */
	public JILocalMethodDescriptor(String methodName,int dispId, JILocalParamsDescriptor parameters)
	{
		this.methodName = methodName;
		this.dispId = dispId;
		setParameterObject(parameters);
	}

	void setMethodNum(int methodNum)
	{
		this.methodNum = methodNum;	
	}
	
	private void setParameterObject(JILocalParamsDescriptor parameters) {
		
		if (parameters == null)
		{
			return;
		}
		
		this.parameters = parameters;
		Object[] params = parameters.getInParams();
		inparametersAsClass = new Class[params.length];
		
		for(int i = 0; i < params.length; i++)
		{
			Object obj = params[i];
			if (obj instanceof Class)
			{
				Class c = (Class)obj;
				
				{
					//get the primitive members here
					if (c.equals(Boolean.class))
					{
						c = boolean.class;
					}else if (c.equals(Character.class))
					{
						c = char.class;
					}else if (c.equals(Byte.class))
					{
						c = byte.class;
					}else if (c.equals(Short.class))
					{
						c = short.class;
					}else if (c.equals(Integer.class))
					{
						c = int.class;
					}else if (c.equals(Long.class))
					{
						c = long.class;
					}else if (c.equals(Float.class))
					{
						c = float.class;
					}else if (c.equals(Double.class))
					{
						c = double.class;
					}else if (c.equals(Void.class))
					{
						c = void.class;
					}
				}
				inparametersAsClass[i] = c;
			}
			else
			{
				inparametersAsClass[i] = obj.getClass();
			}
		}
		
	}

	/**Returns the method name.
	 * 
	 * @return
	 */
	public String getMethodName() {
		return methodName;
	}

	/**Gets the opnum of this method in the order as defined in the IDL.
	 * 
	 * @return
	 */
	public int getMethodNum() {
		return methodNum;
	}
	
	/**Gets the <code>DISPID</code> of this method.
	 * 
	 * @return
	 */
	public int getMethodDispID() {
		return dispId;
	}
	
	/**Returns the parameter object.
	 * 
	 * @return <code>null</code> if the method has no parameters. 
	 */
	public JILocalParamsDescriptor getParameterObject() {
		return parameters;
	}

	/**
	 * @exclude
	 * @return
	 */
	Class[] getInparametersAsClass() {
		return inparametersAsClass;
	}
}