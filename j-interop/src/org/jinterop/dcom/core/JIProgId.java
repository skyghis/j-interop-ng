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


/**<p>Wrapper class used to define user friendly <code>ProgID</code>. 
 * <p> Definition from MSDN: <i>
 *  A ProgID, or programmatic identifier, is a registry entry that can be associated 
 *  with a CLSID. The format of a ProgID is <Vendor>.<Component>.<Version>, separated 
 *  by periods and with no spaces, as in Word.Document.6. Like the CLSID, the ProgID 
 *  identifies a class, but with less precision. 
 *  </i>
 * <p>
 * This class uses the <code>WINREG</code> service to get the mapping between the <code>ProgId</code>
 * and the <code>CLSID</code>. 
 * <p>
 * The <code>WINREG</code> package of j-Interop is capable of querying the Windows registry in a 
 * platform independent way using SMB. The internal database is looked up first before 
 * making calls to <code>WINREG</code> service.
 * </p>
 * @since 1.0
 */
public class JIProgId  {

	private String progId = null;
	private JIClsid clsid = null;
	private JISession session = null;
	private String server = null; 
	private boolean autoRegister = false;
	
	/** Indicates to the framework, if Windows Registry settings for DLL\OCX
	 * component identified by this object should be modified to add a <code>Surrogate</code> 
	 * automatically. A <code>Surrogate</code> is a process which provides resources
	 * such as memory and cpu for a DLL\OCX to execute.
	 * 
	 * @param autoRegister <code>true</code> if auto registration should be done by the framework.
	 */
	public void setAutoRegistration(boolean autoRegister)
	{
		this.autoRegister = autoRegister; 
	}

	/**Returns the status of the auto registration flag for the component identified by this object.
	 * 
	 * @return <code>true</code> if the auto registration flag is set.
	 */
	public boolean isAutoRegistrationSet()
	{
		return autoRegister;
	}
	
	private JIProgId(String progId)
	{
		this.progId = progId;
		clsid = JIClsid.valueOf(JISystem.getClsidFromProgId(progId));
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
		JISystem.internal_setClsidtoProgId(progId,clsid.getCLSID());
		
	}
	
	/** Factory method returning an instance of this class.
	 * 
	 * @param progId user-friendly string representation such as "Excel.Application"
	 * @return
	 */
	public static JIProgId valueOf(String progId)
	{
		return new JIProgId(progId);
	}
	
	/** Returns the <code>CLSID</code> for this <code>ProgId</code>. 
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
	
	void setSession(JISession session)
	{
		this.session = session;
	}
}
