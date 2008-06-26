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



import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.jinterop.dcom.common.IJIAuthInfo;
import org.jinterop.dcom.common.IJIUnreferenced;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;

/**<p>Class representing the user's session. Each <code>IJIComObject</code> is associated with 1 session only.
 * All Interface references being given out by the library for a particular COM server are maintained
 * by the session. It is also responsible for the clean up once the system shuts down or objects loose there 
 * references.<br>
 * 
 * Please make sure that you call JISession.destroySession after you are done using the session. This will ensure that any
 * open sockets to COM server are closed.
 * </p>
 * 
 * @since 1.0
 */
public final class JISession {

	private static Random randomGen = new Random(Double.doubleToRawLongBits(Math.random()));
	private int sessionIdentifier = -1;
	private String username = null;
	private String password = null;
	private String domain = null;
	private String targetServer = null;
	private static Map mapOfObjects = new HashMap();
	private static Object mutex = new Object();
	private IJIAuthInfo authInfo = null;
	private JIComServer stub = null; 
	private static int oxidResolverPort = -1;
	private static byte[] localhost = new byte[]{127,0,0,1}; 
	private static String localhostStr = "127.0.0.1";
	private static String localhostStr2 = "LOCALHOST";
	private static Map mapOfSessionIdsVsSessions = new HashMap();
	private static List listOfSessions = new ArrayList();
	private List listOfDeferencedIpids = new ArrayList();
	private static Timer releaseRefsTimer = new Timer(true);
	private Map mapOfUnreferencedHandlers = new HashMap();
	private int timeout = 0;
	private boolean useSessionSecurity = false;
	private ArrayList links = new ArrayList();
	private static final Map mapOfOxidsVsJISessions = new HashMap();
	
	private static class IPID_SessionID_Holder
	{
		public final String IPID;
		public final Integer sessionID;
		public final boolean isOnlySessionIDPresent;
		public final byte[] oid;
		private IPID_SessionID_Holder(String IPID, int sessionID, boolean isOnlySessionId, byte[] oid)
		{
			this.IPID = IPID;
			this.isOnlySessionIDPresent = isOnlySessionId;
			this.sessionID = new Integer(sessionID);
			this.oid = oid;
		}
	}
	//static List listOfSessions = new ArrayList();
	//will be read by the system thread for cleanup and then passed 
	//to each session for clean up.
	static ReferenceQueue referenceQueueOfCOMObjects = new ReferenceQueue();
	static Thread cleanUpThread = new Thread(new Runnable() {
		public void run() {
			try {
		        while (true) {
		            Reference r = referenceQueueOfCOMObjects.remove();
		            if (r != null) {
		                // Object is no longer referenced.
		            	synchronized (mutex) {
			        		//get from hash map and call release ref on that object
			    			IPID_SessionID_Holder holder = (IPID_SessionID_Holder)mapOfObjects.remove(r);
			    			JISession session = null;
			    			if (holder == null)
			    			{
//			    				//it may be a session
//			    				session = (JISession)r.get();
			    				continue;
			    			}
			    			else
			    			{
			    				session = (JISession)mapOfSessionIdsVsSessions.get(holder.sessionID);
			    			}
			    			//this means that the session got lost...but this logic does not work, since 
			    			//session is strongly referenced from mapOfSessionIdsVsSessions and listOfSessions and even putting
			    			//WeakReference for JISession when adding it to the mapOfSessionIdsVsSessions/listOfSessions does not
			    			//make a difference as we always loose the session to GC before it come here.
			    			if (holder.isOnlySessionIDPresent)
			    			{
			    				try{
			    					destroySession(session);
			    				}catch(Exception e)
			    				{
			    					if (JISystem.getLogger().isLoggable(Level.FINEST))
			    					{
			    						JISystem.getLogger().finest("exception from destroy session in clean up thread: " + e.getMessage());
			    					}
			    				}
			    			}
			    			else
			    			{
			    				//session may have been "destroySession"...
			    				if (session == null)
			    				{
			    					continue;
			    				}
			    				
			    				try
			    				{
			    					String IPID = holder.IPID;
			    					//JIComOxidRuntime.delIPIDReference(IPID);
			    					//session.releaseRef(IPID); Not doing release anymore, this causes a lot of calls to 
			    					//go across, so will save these in this list and then the cleanup thread will deal with 
			    					//this every 3 minutes.
			    					session.addDereferencedIpids(IPID,holder.oid);
			    					holder = null;
			    					IJIUnreferenced unreferenced = (IJIUnreferenced)session.mapOfUnreferencedHandlers.get(IPID);
			    					if (unreferenced != null)
			    					{
			    						unreferenced.unReferenced();
			    					}
			    					session.unregisterUnreferencedHandler(IPID);
			    				}catch(Exception e)
			    				{
			    					if (JISystem.getLogger().isLoggable(Level.INFO))
			    					{
			    						JISystem.getLogger().info("exception from removing a IPID from session in clean up thread: " + e.getMessage());
			    					}
			    				}
			    			}
						}

		        // An IPID identifies a COM interface and according to definition of QI , it can be same across to different
		        //COM Clients, so it was anyways wrong to remove that IPID from all sessions.
				//				while(i < listOfSessions.size())
				//				{
				//					JISession session  = (JISession)listOfSessions.get(i);
				//					session.removeFromSession(r);
				//					i++;
				//				}
		            
		            }
		            
		            
		        }
		    } catch (Exception e) {
		    	JISystem.getLogger().throwing("JISession","CleanupThread:run()",e);  
		    }
		}
	},"jI_GarbageCollector");
	
	//from JDK bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037
	private static String getLocalHost(String destination)
	{
        DatagramSocket sock;
        InetAddress intendedDestination;
		try {
			sock = new DatagramSocket();
			intendedDestination = InetAddress.getByName(destination);
		} catch (Exception e) {
			return "127.0.0.1";
		}
        sock.connect(intendedDestination,sock.getLocalPort());
        return sock.getLocalAddress().getHostAddress();
	}

	static
	{
		JISystem.internal_initLogger();
		try {
			InetAddress localhostAddr = InetAddress.getLocalHost(); 
			localhost = localhostAddr.getAddress();
			localhostStr = localhostAddr.getHostAddress();
			localhostStr2 = localhostAddr.getCanonicalHostName();
		} catch (UnknownHostException e) {}
		
		System.setProperty("jcifs.smb.client.domain","JIDomain");//is being put in for completing type2 message
		//somehow windows is not taking empty domain name.
		
		//start the cleanup thread.
		// and create a shutdown hook also.
		cleanUpThread.setDaemon(true);
		//cleanUpThread.setPriority(Thread.MIN_PRIORITY);
		cleanUpThread.start();
		
		JIComOxidRuntime.startResolver();
		JIComOxidRuntime.startResolverTimer();
		oxidResolverPort = JIComOxidRuntime.getOxidResolverPort();
		releaseRefsTimer.scheduleAtFixedRate(new Release_References_TimerTask(),0,3*60*1000);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
					int i = 0;
					while(i < listOfSessions.size())
					{
						JISession session  = (JISession)(listOfSessions.get(i));
						try {
							JISession.destroySession(session);
						} catch (JIException e) {
							JISystem.getLogger().throwing("JISession","addShutDownHook Thread:run()",e);  
						}
					}
					JISystem.writeProgIdsToFile();
					JIComOxidRuntime.stopResolver();
					releaseRefsTimer.cancel();
					mapOfSessionIdsVsSessions.clear();
					mapOfObjects.clear();
					listOfSessions.clear();
				}
		},"jI_ShutdownHook"));
		
		
	}
	
	private static class Release_References_TimerTask extends TimerTask
	{
		public void run() {
			synchronized (mutex) {

				int i = 0;
				
				while(i < listOfSessions.size())
				{
					JISession session = (JISession)listOfSessions.get(i);

					//now iterate over each sessions listOfDereferencedIpids and send a call to release for the entire lot.
					ArrayList listToKill = new ArrayList();
					for (int j = 0;j < session.listOfDeferencedIpids.size();j++)
					{
						try {
							listToKill.add(session.prepareForReleaseRef((String)session.listOfDeferencedIpids.get(j)));
						} catch (JIException e) {
						//eaten, will never get thrown from the try block.
						}
					}
					
					if (JISystem.getLogger().isLoggable(Level.INFO))
		            {
						JISystem.getLogger().info("Release_References_TimerTask:[RUN] Session:  " + session.getSessionIdentifier() + " , listOfDeferencedIpids: " + session.listOfDeferencedIpids);
		            }

					session.listOfDeferencedIpids.clear();
					if (listToKill.size() > 0)
					{
						JIArray array = new JIArray(listToKill.toArray(new JIStruct[listToKill.size()]),true);
						try{
							session.releaseRefs(array);
						}catch(JIException e)
						{
							//This release cycle has to go on.
							JISystem.getLogger().throwing("JISession","Release_References_TimerTask:run()",e);
						}	
					}
					
					i++;
				}
				
				
			}
		}
	}
	
	
	
	void setTargetServer(String targetServer)
	{
		if (targetServer.equalsIgnoreCase("127.0.0.1"))
		{
			//Replace with it's actual bindings, otherwise does not work for JCIFS authentication
			this.targetServer = getLocalhostAddressAsIPString();
		}
		else
		{
			this.targetServer = targetServer;
			
			//will change the localhost to the actual address as well
			if (localhostStr.equalsIgnoreCase("127.0.0.1"))
			{ //Bug in JDK , time to find alternate logic. 
				localhostStr = getLocalHost(targetServer);
			}
			
		}
		
		
	}
	
	static byte[] getLocalhostAddressAsIPbytes()
	{
		return localhost;
	}
	
	static String getLocalhostAddressAsIPString()
	{
		return localhostStr;
	}
	
	static String getLocalhostCanonicalAddressAsString()
	{
		return localhostStr2;
	}
	
	
	String getTargetServer()
	{
		return targetServer;
	}
	
	private JISession() {};
	
	static int getOxidResolverPort()
	{
		return oxidResolverPort;
	}
	
	/**Returns the authInfo (if any) associated with this session.
	 * 
	 * @return
	 */
	public IJIAuthInfo getAuthInfo()
	{
		return authInfo;
	}
	
	/** Creates a session with the authInfo of the user. authInfo cannot be null.
	 * 
	 * @param authInfo
	 * @return
	 */
	public static JISession createSession(IJIAuthInfo authInfo)
	{
		if (authInfo == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_AUTH_NOT_SUPPLIED));
		}
		
		JISession session = new JISession();

		session.authInfo = authInfo;
		
		session.sessionIdentifier = authInfo.getUserName().hashCode() ^ authInfo.getPassword().hashCode() ^ authInfo.getDomain().hashCode() ^ new Object().hashCode() ^ (int)Runtime.getRuntime().freeMemory() ^ randomGen.nextInt();
		
		//WeakReference r = new WeakReference(session,referenceQueueOfCOMObjects);
		mapOfSessionIdsVsSessions.put(new Integer(session.sessionIdentifier),session);
		//IPID_SessionID_Holder holder = new IPID_SessionID_Holder(null,session.sessionIdentifier,true);
		//mapOfObjects.put(new WeakReference(session,referenceQueueOfCOMObjects),holder);
		listOfSessions.add(session);
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Created Session: " + session.sessionIdentifier);
		}
		return session;
	}
	
	
	
	
	/** Creates a session.
	 * 
	 * @param domain Domain of the user.
	 * @param username Username
	 * @param password password of the user.
	 * @return
	 */
	public static JISession createSession(String domain,String username, String password)
	{
		if (username == null || password == null || domain == null)
		{
			throw new IllegalArgumentException(JISystem.getLocalizedMessage(JIErrorCodes.JI_AUTH_NOT_SUPPLIED));
		}
		
		JISession session = new JISession();
		session.username = username;
		session.password = password;
		session.domain = domain;
		session.sessionIdentifier = username.hashCode() ^ password.hashCode() ^ domain.hashCode() ^ new Object().hashCode() ^ (int)Runtime.getRuntime().freeMemory() ^ randomGen.nextInt();
		
		//WeakReference r = new WeakReference(session,referenceQueueOfCOMObjects);
		mapOfSessionIdsVsSessions.put(new Integer(session.sessionIdentifier),session);
		//IPID_SessionID_Holder holder = new IPID_SessionID_Holder(null,session.sessionIdentifier,true);
		//mapOfObjects.put(new WeakReference(session,referenceQueueOfCOMObjects),holder);
		listOfSessions.add(session);
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("Created Session: " + session.sessionIdentifier);
		}
		//System.out.println("Created Session: " + session.sessionIdentifier);
		return session;
	}


	/** Creates a new Session , using credentials of the session sent as parameter.
	 * 
	 * @param session
	 * @return
	 */
	public static JISession createSession(JISession session)
	{
		JISession newSession = createSession(session.getDomain(),session.getUserName(),session.getPassword());
		newSession.authInfo = session.authInfo;
		return newSession;

	}
	
	/**<p>Used to destroy the session, this release all references of the <code>COM</code> server and it's interfaces. 
	 * It should be called once the developer is done with the usage of a <code>COM</code> object, it is required so as to close all open sockets
	 * with the COM server. The references to COM interfaces are discarded from time to time when a random GC is done or when the system shuts down.<br>
	 * Please note that all interface pointers belonging to sessions linked to this session (refer the JIObjectFactory.createCOMInstance API) will also be 
	 * destroyed.
	 * </p>
	 * @param session
	 * @throws JIException
	 */
	public static void destroySession(JISession session) throws JIException
	{
		synchronized (mutex) {
			try{
				//session may have been destroyed and this call is from finalize.
				if (session == null || session.stub == null)
				{
					return;
				}
				ArrayList list = new ArrayList();
				//take all the objects registered with this session and call release on them.
				Iterator iterator = mapOfObjects.keySet().iterator();
				while(iterator.hasNext())
				{
					//String ipid = (String)session.mapOfObjects.get(iterator.next());
					IPID_SessionID_Holder holder = (IPID_SessionID_Holder)mapOfObjects.get(iterator.next());
					if (session.getSessionIdentifier() != holder.sessionID.intValue())
					{
						continue;
					}
	    			String ipid = holder.IPID;
					if (ipid == null)
					{
						continue;
					}
					list.add(session.prepareForReleaseRef(ipid));
					//iterator.remove();
				}
				
				//now for the list of dereferenced IPIDs
				for (int j = 0;j < session.listOfDeferencedIpids.size();j++)
				{
					list.add(session.prepareForReleaseRef((String)session.listOfDeferencedIpids.get(j)));
				}
				
				session.listOfDeferencedIpids.clear();

				
				//now to kill the stub itself
				if(session.stub != null && session.stub.getServerInterfacePointer() != null)
				{
					//session.releaseRef(session.stub.getActivated().getMInterfacePointer().getIPID());
					list.add(session.prepareForReleaseRef(session.stub.getServerInterfacePointer().getIPID()));
					// and remove its entry from the map
					mapOfOxidsVsJISessions.remove(new JIOxid(session.stub.getServerInterfacePointer().getOXID()));
				}
				
				//release is performed if only something is in the session.
				if (list.size() > 0)
				{
					JIArray array = new JIArray(list.toArray(new JIStruct[list.size()]),true);
					try{
						session.releaseRefs(array);
					}catch(JIException e)
					{
						//This release cycle has to go on.
						JISystem.getLogger().throwing("JISession","destroySession",e);
					}
				}
				
				JIComOxidRuntime.clearIPIDsforSession(session);
				if (JISystem.getLogger().isLoggable(Level.INFO))
				{
					JISystem.getLogger().info("Destroyed Session: " + session.sessionIdentifier);
				}
			}finally
			{
				if (session.stub != null)
				{
					session.stub.closeStub();
					session.stub = null;
				}
				mapOfSessionIdsVsSessions.remove(new Integer(session.getSessionIdentifier()));
				listOfSessions.remove(session);
			}
			
			//now destroy all linked sessions
			if (JISystem.getLogger().isLoggable(Level.INFO))
			{
				JISystem.getLogger().info("About to destroy links for Session: " + session.getSessionIdentifier() + " , size of which is " + session.links.size());
			}
			
			for (int i = 0; i < session.links.size();i++)
			{
				JISession.destroySession((JISession)session.links.get(i));
			}
			session.links.clear();
		}
		
		//finally any oids exported by this session.
		JIComOxidRuntime.destroySessionOIDs(session.getSessionIdentifier());
		
	}
	
	//each session is associated with 1 and only 1 stub.
	void setStub(JIComServer stub)
	{
		this.stub = stub;
		synchronized (mutex) {
			mapOfOxidsVsJISessions.put(new JIOxid(stub.getServerInterfacePointer().getOXID()),this);
		}
	}
	
	JIComServer getStub()
	{
		return this.stub;
	}
	
	/**
	 * @exclude
	 * @param IPID
	 */
	void addToSession(IJIComObject comObject, byte[] oid)
	{
		IPID_SessionID_Holder holder = new IPID_SessionID_Holder(comObject.getIpid(),getSessionIdentifier(),false,oid);
		//mapOfObjects.put(new WeakReference(comObject,referenceQueueOfCOMObjects),holder);
		synchronized (mutex) 
		{
			mapOfObjects.put(new WeakReference(comObject,referenceQueueOfCOMObjects),holder);
		}
		addToSession(comObject.getIpid(),oid);
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info(" for IID: " + comObject.getInterfaceIdentifier());
		}
	}
	
	/**
	 * @exclude
	 * @param IPID
	 */
	private void addToSession(String IPID,byte[] oid)
	{
		//Weak reference of the object
		//mapOfObjects.put(new WeakReference(IPID,referenceQueueOfCOMObjects),IPID);
		//it does not matter if we create a new OID here, the OxidCOMRunttime API uses the OID in the MAP , and not this one.
		JIObjectId joid = new JIObjectId(oid);
		JIComOxidRuntime.addUpdateOXIDs(this,IPID,joid);
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("[addToSession] Adding IPID: " + IPID + " to session: " + getSessionIdentifier());
		}
	}
	
	
	//this gets called from the cleanupthread and no place else and it calls the releaseRef of session which 
	//internally calls the add_releaseRef of the JIComServer, that method is synched at the instance level.
	//I was worried about a deadlock with destroySession , since that also ultimately calls the add_releaseRef, but 
	//this will not happen since under a simultaneous destroy and removefromsession call , the "mutex" object will get synch.
	//If suppose a comServer.getInterface(...) is being done (which also calls releaseRef), then that is synched at instance level
	//and so is add_releaseRef (on the same instance), so deadlock won't happen there. If a simulataneous remove and getInterface call comes 
	//then getInterface(which internally calls releaseRef) will go through, since releaseRef is not synched but the api it calls i.e. add_releaseRef is synched with the same lock
	//as getInterface. The remove will have to wait till that call gets over.
	void releaseRef(String IPID) throws JIException
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("releaseRef:Reclaiming from Session: " + getSessionIdentifier() + " , the IPID: " + IPID);
		}
		JICallBuilder obj = new JICallBuilder(IPID,true);
		obj.setOpnum(2);//release
		//length
		obj.addInParamAsShort((short)1,JIFlags.FLAG_NULL);
		//ipid to addfref on
		JIArray array = new JIArray(new rpc.core.UUID[]{new rpc.core.UUID(IPID)},true);
		obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
		//TODO requesting 5 for now, will later build caching mechnaism to exhaust 5 refs first before asking for more
		// same with release.
		obj.addInParamAsInt(5,JIFlags.FLAG_NULL);
		obj.addInParamAsInt(0,JIFlags.FLAG_NULL);//private refs = 0
		stub.addRef_ReleaseRef(obj);
	}

	
	private void addDereferencedIpids(String IPID, byte[] oid)
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("addDereferencedIpids for session : " + getSessionIdentifier() + " , IPID is: " + IPID);
		}
		listOfDeferencedIpids.add(IPID);	
		
		//Will call the JIComOxidRuntime, and that is synched on mutex3, but that will not cause a deadlock, since
		//it or rather any method of JIComOxidRuntime does not call back into JISession.
		JIComOxidRuntime.delIPIDReference(IPID,new JIObjectId(oid),this);
	}
	
	private void releaseRefs(JIArray arrayOfStructs) throws JIException
	{
		if (JISystem.getLogger().isLoggable(Level.INFO))
		{
			JISystem.getLogger().info("In releaseRefs for session : " + getSessionIdentifier() + " , array length is: " + (short)(((Object[])arrayOfStructs.getArrayInstance()).length));
		}
		JICallBuilder obj = new JICallBuilder(null,true);
		obj.setOpnum(2);//release
		//length
		obj.addInParamAsShort((short)(((Object[])arrayOfStructs.getArrayInstance()).length),JIFlags.FLAG_NULL);
		obj.addInParamAsArray(arrayOfStructs,JIFlags.FLAG_NULL);
		stub.addRef_ReleaseRef(obj);
		
		//ignore the results
	}
	
	private JIStruct prepareForReleaseRef(String IPID) throws JIException
	{
		JIStruct remInterface = new JIStruct();
		remInterface.addMember(new rpc.core.UUID(IPID));
		remInterface.addMember(new Integer(5));
		remInterface.addMember(new Integer(0));//private refs = 0
		return remInterface;
	}

	/**Gets the username associated with this session.
	 * 
	 * @return
	 */
	public String getUserName()
	{
		return authInfo == null ? username : authInfo.getUserName();
	}
	
	String getPassword()
	{
		return authInfo == null ? password : authInfo.getPassword();
	}
	
	/**Gets the domain of the user associated with this session.
	 * 
	 * @return
	 */
	public String getDomain()
	{
		return authInfo == null ? domain : authInfo.getDomain();
	}
	
	
	
	/**Returns a unique identifier for this session.
	 * 
	 * @return
	 */
	public int getSessionIdentifier()
	{
		return sessionIdentifier;
	}
	
	/**
	 * @exclude
	 */
	public boolean equals(Object obj) {
		
		if (obj == null || !(obj instanceof JISession))
		{
			return false;
		}
		
		JISession temp  = (JISession)obj;
		return temp.sessionIdentifier == sessionIdentifier ;
	}
	
	/**
	 * @exclude
	 */
	public int hashCode()
	{
		return sessionIdentifier;
	}
	
	protected void finalize()
	{
		try {
			destroySession(this);
		} catch (JIException e) {
			if (JISystem.getLogger().isLoggable(Level.FINEST))
			{
				JISystem.getLogger().finest("Exception in finalize when destroying session " + e.getMessage());
			}
		}
	}
	
	IJIUnreferenced getUnreferencedHandler(String ipid) {
		synchronized (mutex) {
			return (IJIUnreferenced)mapOfUnreferencedHandlers.get(ipid);
		}
	}

	void registerUnreferencedHandler(String ipid, IJIUnreferenced unreferenced) {
		synchronized (mutex) {
			mapOfUnreferencedHandlers.put(ipid, unreferenced);
		}
	}

	void unregisterUnreferencedHandler(String ipid) {
		synchronized (mutex) {
			mapOfUnreferencedHandlers.remove(ipid);
		}
	}
	
	/**<p> Sets the timeout for all sockets opened to (not fro) the COM server for this session. Default value is 0 (no timeout).
	 * The class level and the API level settings in case of IJIComObject override this timeout. </p>  
	 * 
	 * @param timeout in millisecs
	 */
	public void setGlobalSocketTimeout(int timeout)
	{
		this.timeout = timeout;
	}
	
	/** Returns the global timeout applied to all sockets opened from this session to COM Server. 
	 * 
	 * @return
	 */
	public int getGlobalSocketTimeout()
	{
		return this.timeout;
	}
	
	/**<p> Sets the use of NTLM2 Session Security. j-Interop will use NTLM Packet Level Privacy and Sign\Seal all packets. Once the JIComServer is
	 * bound to this session (using any of the {@link JIComServer#JIComServer(...)} ctors) the use of session security cannot be enabled or disabled.
	 * 
	 * Please note that session security can come at any available level of authentication (LM\NTLM\LMv2\NTLMv2). The library currently
	 * only supports sign and seal at NTLMv1 level. Whether to use NTLM1 or not is dictated by this field in the registry. 
	 * 
	 * <code>
	 * HKLM\System\CurrentControlSet\Control\Lsa\LmCompatibilityLevel 
	 * </code>
	 * 
	 * This article on MSDN talks more about it http://support.microsoft.com/default.aspx?scid=KB;en-us;239869 
	 * </p>
	 * @param enable true to enable, false to disable. 
	 */
	public void useSessionSecurity(boolean enable)
	{
		useSessionSecurity = enable;
//		if (enable)
//		{
//			useNTLMv2 = enable;
//		}
	}
	
	/**<p> Flag indicating whether session security is enabled. </p>
	 * 
	 * @return true for enabled.
	 */
	public boolean isSessionSecurityEnabled()
	{
		return useSessionSecurity;
	}
	
	
	/**<p> Links the src with target. These two sessions can now be destroyed in a cascade effect.
	 * </p>
	 * 
	 * @param session
	 */
	static void linkTwoSessions(JISession src, JISession target)
	{
		if (src.equals(target))
			return;
		
		synchronized (mutex) {
			if (!src.links.contains(target))
			{
				src.links.add(target);
			}
		}
	}
	
	/** Removes session from src sessions list.
	 * 
	 */
	static void unLinkSession(JISession src, JISession tobeunlinked)
	{
		if (src.equals(tobeunlinked))
			return;
		
		synchronized (mutex) {
			src.links.remove(tobeunlinked);
		}	
	}
	
	
	
	/** Based on the oxid returns the JISession (and thus the COM Server) associated with it. This is required, since there are
	 * cases where a different JISession may be passed in JIObjectFactory for an JIInterfacePointer which does not belong to this JISession.
	 * Under those scenarios, the COM factory will create a new instance of a JISession and associate that Interface pointer with the session.
	 * But that is not the right approach as a COM Server for that interface and thus a session might already exist and these have to be tied together.
	 * 
	 * @exclude
	 */
	static JISession resolveSessionForOxid(JIOxid oxid)
	{
		synchronized (mutex) {
			return (JISession)mapOfOxidsVsJISessions.get(oxid);	
		}
	}
	
	
   
	
}
