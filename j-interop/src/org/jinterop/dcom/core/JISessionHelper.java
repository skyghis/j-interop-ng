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

/** Helper class
 * 
 * @exclude
 *
 */
public final class JISessionHelper {

	/** 
	 * @exclude
	 * @param src
	 * @param target
	 */
	public static void link2Sessions(JISession src, JISession target)
	{
		if (src == null || target == null)
		{
			throw new NullPointerException();
		}
		
		JISession.linkTwoSessions(src, target);
	}
	
	/** 
	 * @exclude
	 * @param src
	 * @param target
	 */
	public static void unLinkSession(JISession src, JISession unlinkedSession)
	{
		if (src == null || unlinkedSession == null)
		{
			throw new NullPointerException();
		}
		
		JISession.unLinkSession(src, unlinkedSession);
	}
	
	/** 
	 * @exclude
	 * @param src
	 * @param target
	 */
	public static JISession resolveSessionForOXID(JIInterfacePointer interfacePointer)
	{
		return JISession.resolveSessionForOxid(new JIOxid(interfacePointer.getOXID()));
	}
	
	/** 
	 * @exclude
	 * @param src
	 * @param target
	 */
	public static JIInterfacePointer getInterfacePointerOfStub(JISession session)
	{
		return session.getStub().getServerInterfacePointer();
	}
}
