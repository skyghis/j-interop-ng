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


import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIDefaultAuthInfoImpl;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.winreg.IJIWinReg;
import org.jinterop.winreg.JIPolicyHandle;
import org.jinterop.winreg.JIWinRegFactory;

//this class will contain all logic of looking up the 
//JISystem for progId to clsid mapping and 
//if not found query the windows registry service.
// this will be called from JIComServer, incase it's progid ctor has been called.

/**<p>Wrapper class used to define user friendly <code>ProgId</code>. This class uses the WINREG service to get the
 * mapping between the ProgId and the Clsid. The Winreg package of j-Interop is capable of querying the Windows
 * registry in a platform independent way using SMB. The internal database is looked up first before making calls
 * to WINREG service.
 * </p>
 * @since 1.0
 */
public class JIProgId  {

	private String progId = null;
	private JIClsid clsid = null;
	private JISession session = null;
	private String server = null; 
	private boolean autoRegister = false;
	
	/** Pass true if, this is an OCX\DLL component and you want the library to do auto registration.
	 * 
	 * @param autoRegister
	 */
	public void setAutoRegistration(boolean autoRegister)
	{
		this.autoRegister = autoRegister; 
	}

	/**Returns true is auto registration is enabled.
	 * 
	 * @return
	 */
	public boolean isAutoRegistrationSet()
	{
		return autoRegister;
	}
	
	private JIProgId(JISession session,String progId)
	{
		this.progId = progId;
		clsid = JIClsid.valueOf(JISystem.getClsidFromProgId(progId));
		this.session = session;
	}
	
	void setServer(String server)
	{
		this.server = server;
	}
	
	private void getIdFromWinReg() throws JIException
	{
		IJIWinReg winreg;
		//winreg = JIWinRegFactory.getSingleTon().getWinreg(new JIDefaultAuthInfoImpl(session.getDomain(),session.getUserName(),session.getPassword()),server,true);
		//System.out.println("Encoding the password...");
		
//		try {
//			winreg = JIWinRegFactory.getSingleTon().getWinreg(new JIDefaultAuthInfoImpl(session.getDomain(),session.getUserName(),URLEncoder.encode(session.getPassword(),"UTF-8")),server,true);
//		} catch (UnsupportedEncodingException e) {
//			try {
//				winreg = JIWinRegFactory.getSingleTon().getWinreg(new JIDefaultAuthInfoImpl(session.getDomain(),session.getUserName(),URLEncoder.encode(session.getPassword(),System.getProperty("file.encoding"))),server,true);
//			} catch (UnsupportedEncodingException e1) {
//				throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION2);
//			}catch (UnknownHostException e2)
//			{
//				throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION3);
//			}
//		} catch (UnknownHostException e)
//		{
//			throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION3);
//		}
		
		if (server == null)
		{
			server = session.getTargetServer();
		}
		
		try {
			winreg = JIWinRegFactory.getSingleTon().getWinreg(new JIDefaultAuthInfoImpl(session.getDomain(),session.getUserName(),session.getPassword()),server,true);
		} catch (UnknownHostException e)
		{
			throw new JIException(JIErrorCodes.JI_WINREG_EXCEPTION3);
		}
		JIPolicyHandle handle = winreg.winreg_OpenHKLM();
		JIPolicyHandle handle2 = winreg.winreg_OpenKey(handle,"SOFTWARE\\Classes\\" + progId + "\\CLSID",IJIWinReg.KEY_READ);
		String key = new String(winreg.winreg_QueryValue(handle2,255));
		winreg.winreg_CloseKey(handle2);
		winreg.winreg_CloseKey(handle);
		winreg.closeConnection();
		//seperate the {}
		clsid = JIClsid.valueOf(key.substring(key.indexOf("{") + 1,key.indexOf("}")));
		clsid.setAutoRegistration(autoRegister);
		JISystem.setClsidtoProgId(progId,clsid.getCLSID());
		
	}
	
	/** Creates a JIProgId.
	 * 
	 * @param session
	 * @param progId
	 * @return
	 */
	public static JIProgId valueOf(JISession session,String progId)
	{
		return new JIProgId(session,progId);
	}
	
	/** Gets the clsid for this ProgId. 
	 * 
	 * @return
	 * @throws JIException
	 */
	public JIClsid getCorrespondingCLSID() throws JIException
	{
		if (clsid == null)
		{
			getIdFromWinReg();
		}
		return clsid;
	}
	
}
