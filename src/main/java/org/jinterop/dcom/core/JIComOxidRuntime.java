/** j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */
package org.jinterop.dcom.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import rpc.Security;
import rpc.core.UUID;

/**
 * Thread for Oxid Resolver. Creates and accepts socket connections for
 * resolving oxids. Gets started once for each instance of the library.
 *
 * Please note that the <b>"Server" <b> Service should be running on the machine
 * where the
 * <br> COM server is running.
 *
 * @since 1.0
 *
 */
final class JIComOxidRuntime {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private static final Properties defaults = new Properties();
    private static final Properties defaults2 = new Properties();
    private static boolean stopSystem = false;
    private static boolean resolverStarted = false;
    private static int oxidResolverPort = -1;
    private static final Map<String, JIComOxidDetails> mapOfIPIDVsComponent = new HashMap<>(); //java client , com server
    private static final Map<JILocalCoClass, JIComOxidDetails> mapOfJavaVsOxidDetails = new HashMap<>(); //java client , com server
    private static final Map<JIOxid, JIComOxidDetails> mapOfOxidVsOxidDetails = new HashMap<>();//java client , com server
    private static final Map<JIObjectId, JILocalCoClass> mapOfOIDVsComponents = new HashMap<>(); //java client , com server
    //list of all exported oids per session, all these oids have to be removed.
    private static final Map<Integer, List<JIObjectId>> mapOfSessionIdsVsOIDs = new HashMap<>(); //java server , com client
    private static final Map<JISetId, List<JIObjectId>> mapOfSetIdVsListOfOIDs = new HashMap<>(); //com client , java server
    private static final Map<JISession, PingSetHolder> mapOfSessionVsPingSetHolder = new HashMap<>(); //com client , java server
    private static final Map<String, JIComOxidStub> mapOfAddressVsStub = new HashMap<>(); //java client , com server, so that we don't have to keep doing bind everytime.
    private static final List<JILocalCoClass> listOfExportedJavaComponents = new ArrayList<>();
    private static final Object mutex2 = new Object();//for access to the maps
    private static final Object mutex3 = new Object(); //for access to the AddressVsSession,Stub Map
    private static final Random randomGen = new Random(Double.doubleToRawLongBits(Math.random()));
    private static final Timer pingTimer_2minutes = new Timer(true);
    private static final Timer pingTimer_8minutes = new Timer(true);
    private static final Object mutex4 = new Object(); //for access to the mapOfAddressVsStub
    private static ServerSocket serverSocket = null;
    static final Object mutex = new Object(); //for access to the sockets

    private JIComOxidRuntime() {
    }

    //one per session.
    private static class PingSetHolder {

        byte[] setId = null;
        String username = null;
        String password = null;
        String domain = null;
        boolean modified = false;
        boolean closed = false;
        int seqNum = 1;
        //JISession session  = null;
        Map<JIObjectId, JIObjectId> currentSetOIDs = new HashMap<>();//list of JIObjectId, this list is iterated and if the IPID ref count is 0 ,
        //it is added as a delete in set and a complex ping is sent.
        Map<JIObjectId, JIObjectId> pingedOnce = new HashMap<>();

        @Override
        public String toString() {
            return "SetID[" + Arrays.toString(setId) + "] , currentSetOIDs[" + currentSetOIDs + "]";
        }
    }

    //this task just checks for expired OIDs in the mapOfOIDVsComponents, each OID carries with itself, lastPingedTime,
    //if that (currenttime - thattime) is < ping interval...all is okay, otherwise , all it's details are erased, thus
    //removing any reference of the given java server from j-Interop library, after which if no one outside has references, this
    //object can be GCed.
    private static class ServerPingTimerTask extends TimerTask {

        @Override
        public void run() {
            synchronized (mutex2) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Running ServerPingTimerTask !");
                }
                Iterator<JIObjectId> itr = mapOfOIDVsComponents.keySet().iterator();
                while (itr.hasNext()) {
                    JIObjectId oid = itr.next();
                    if (oid.hasExpired()) {
                        //remove all
                        JILocalCoClass component = mapOfOIDVsComponents.get(oid);
                        //this means the local system still has references and we cannot delete this object
                        //since the user may reuse it.
                        if (component.isAssociatedReferenceAlive()) {
                            continue;
                        }
                        JIComOxidDetails details = mapOfJavaVsOxidDetails.get(component);
                        mapOfOxidVsOxidDetails.remove(details.getOxid());
                        mapOfIPIDVsComponent.remove(details.getIpid());
                        mapOfJavaVsOxidDetails.remove(component);
                        listOfExportedJavaComponents.remove(component);
                        itr.remove();

                        //the thread associated with this will also stop.
                        details.interruptRemUnknownThreadGroup();
                    }
                }
            }
        }
    }

    static void destroySessionOIDs(int sessionId) {
        synchronized (mutex2) {

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "destroySessionOIDs for session: {0}", sessionId);
            }
            final List<JIObjectId> oids = mapOfSessionIdsVsOIDs.remove(sessionId);
            if (oids == null || oids.isEmpty()) {
                return;
            }
            for (int i = 0; i < oids.size(); i++) {
                JIObjectId oid = oids.get(i);
                //remove all
                JILocalCoClass component = mapOfOIDVsComponents.remove(oid);
                JIComOxidDetails details = mapOfJavaVsOxidDetails.get(component);
                if (details != null) {
                    mapOfOxidVsOxidDetails.remove(details.getOxid());
                    mapOfIPIDVsComponent.remove(details.getIpid());
                }
                mapOfJavaVsOxidDetails.remove(component);
                listOfExportedJavaComponents.remove(component);
                //the thread associated with this will also stop.
                if (details != null) {
                    details.interruptRemUnknownThreadGroup();
                }
            }
            oids.clear();
        }
    }

    private static class ClientPingTimerTask extends TimerTask {

        @Override
        public void run() {
            Iterator<Map.Entry<JISession, PingSetHolder>> itr;
            synchronized (mutex3) {
                itr = new HashMap<>(mapOfSessionVsPingSetHolder).entrySet().iterator();
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Running ClientPingTimerTask !");
            }
            //iterate over the map and get the corresponding stubs and use there sessions to
            //stub is created here and used per address

            //if set id is null send a complex ping to get back the set id for all the OIDs in the
            //PingSetHolder
            while (itr.hasNext()) {
                Map.Entry<JISession, PingSetHolder> entry = itr.next();
                PingSetHolder holder = entry.getValue();
                String address = entry.getKey().getTargetServer();
                //will get it from the cache, since it is getting called after every 4 minutes
                //what if this stub has timed out, I guess I will have to ask the developers to increase the timeout for now.
                JIComOxidStub stub;
                synchronized (mutex4) {
                    stub = mapOfAddressVsStub.get(address);
                    if (stub == null) {
                        stub = new JIComOxidStub(address, holder.domain, holder.username, holder.password);
                        mapOfAddressVsStub.put(address, stub);
                    }
                }

                final List<JIObjectId> listOfAddedOIDs = new ArrayList<>();
                final List<JIObjectId> listOfRemovedOIDs = new ArrayList<>();
                //form a list if OID is 0 ref
                synchronized (mutex3) {
                    for (Iterator<JIObjectId> itr2 = holder.currentSetOIDs.keySet().iterator(); itr2.hasNext();) {
                        JIObjectId oid = itr2.next();
                        if (oid.getIPIDRefCount() == 0) {
                            if (!oid.dontping) {
                                listOfRemovedOIDs.add(oid);
                                holder.pingedOnce.remove(oid);
                                holder.modified = true;
                            }
                            itr2.remove();
                        } else {
                            if (!oid.dontping && !holder.pingedOnce.containsKey(oid)) {
                                listOfAddedOIDs.add(oid);
                                holder.pingedOnce.put(oid, oid);
                                holder.modified = true;
                            }
                        }
                    }
                }
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Within ClientPingTimerTask: holder.currentSetOIDs, current size of which is {0}", holder.currentSetOIDs.size());
                }

                //this is the first time this is going and objects with no references will not be added to ping set.
                if (holder.setId == null) {
                    listOfRemovedOIDs.clear();
                }

                boolean isSimplePing = false;

                //No additions and no deletions
                if (holder.setId != null && !holder.modified) {
                    //send simple set ping
                    isSimplePing = true;
                }

                //seqNum will be 0 for simple ping, but incremented for complex pings. seqNum is per setId. first one will be 0 and increments
                //there on...
                holder.setId = stub.call(isSimplePing, holder.setId, listOfAddedOIDs, listOfRemovedOIDs, isSimplePing ? 0 : holder.seqNum++);

                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.INFO, "Within ClientPingTimerTask: holder.seqNum {0}", holder.seqNum);
                }

                holder.modified = false;
                //stub.close(); commenting this since we are caching the stub.
                if (holder.closed) {
                    //this means that this set is empty and there is no need for it. The set has emptied  itself and
                    //will get removed from COM servers side as well.
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Within ClientPingTimerTask: Holder {0} is empty, will remove this from mapOfSessionVsPingSetHolder", holder);
                    }
                    itr.remove();
                    synchronized (mutex3) {
                        mapOfSessionVsPingSetHolder.remove(entry.getKey());
                    }
                }
            }
        }
    }

    static {
        defaults2.put("rpc.ntlm.lanManagerKey", "false");
        defaults2.put("rpc.ntlm.sign", "false");
        defaults2.put("rpc.ntlm.seal", "false");
        defaults2.put("rpc.ntlm.keyExchange", "false");
        defaults2.put("rpc.connectionContext", "org.jinterop.dcom.transport.JIComRuntimeNTLMConnectionContext");
        defaults.put("rpc.connectionContext", "org.jinterop.dcom.transport.JIComRuntimeConnectionContext");
    }

    //ip address
    static void addUpdateOXIDs(JISession session, String IPID, JIObjectId oid) {
        synchronized (mutex3) {
            //make sure this is the IP address
            PingSetHolder holder = mapOfSessionVsPingSetHolder.get(session);
            if (holder == null) {
                //new
                holder = new PingSetHolder();
                holder.username = session.getUserName();
                holder.password = session.getPassword();
                holder.domain = session.getDomain();
                holder.currentSetOIDs.put(oid, oid);
                holder.modified = true;
                holder.seqNum = 0;
                mapOfSessionVsPingSetHolder.put(session, holder);
            } else //found , means it is another call for a new IPID
            {
                JIObjectId oid2 = holder.currentSetOIDs.get(oid);
                if (oid2 != null) {
                    //have to update this oid, since the one from parameters is a "new" one.
                    oid = oid2;
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "addUpdateOXIDs: Adding OID to holder {0}, current size of currentSetOIDs is {1}", new Object[]{holder, holder.currentSetOIDs.size()});
                    }
                    holder.currentSetOIDs.put(oid, oid);
                    holder.modified = true;
                }
            }

            oid.incrementIPIDRefCountBy1();
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "addUpdateOXIDs: finally this oid is {0}", oid);
            }
        }

    }

    static void delIPIDReference(String IPID, JIObjectId oid, JISession session) {
        synchronized (mutex3) {
            PingSetHolder holder = mapOfSessionVsPingSetHolder.get(session);
            //this will be non-null, since we are trying to remove an IPID reference so the PingSet for its OID should exist
            if (holder != null) {
                JIObjectId oid2 = holder.currentSetOIDs.get(oid);
                if (oid2 != null) {
                    //temp gets replaced by the real one.
                    oid = oid2;
                } else {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "In delIPIDReference: Could not find Original OID for this temp OID for session: {0} , temp oid is {1} , and IPID is {2}", new Object[]{session.getSessionIdentifier(), oid, IPID});
                    }
                    return;
                }

                //this is the same OID as in the PingSetHolder.
                oid.decrementIPIDRefCountBy1();
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "delIPIDReference: Decrementing reference count for IPID {0} on OID {1}", new Object[]{IPID, oid});
                }

                //should we retain this now ??? , we need not send a ping for this as well. It is being retained for the last ping only.
                if (oid.getIPIDRefCount() <= 0) {
                    holder.currentSetOIDs.remove(oid);
                    //everything is gone, remove the session
                    if (holder.currentSetOIDs.isEmpty()) {
                        holder.closed = true;
                        mapOfSessionVsPingSetHolder.remove(session);
                    }
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "delIPIDReference: sessionid {0}Ref count is <= 0, for OID {1}, holder status: {2}", new Object[]{session.getSessionIdentifier(), oid, holder.closed});
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "In delIPIDReference: Could not find PingSetHolder for this session: {0} , temp oid is {1} , and IPID is {2}", new Object[]{session.getSessionIdentifier(), oid, IPID});
                }
            }
        }
    }

    static void clearIPIDsforSession(JISession session) {
        synchronized (mutex3) {
            //make sure this is the IP address
            PingSetHolder holder = mapOfSessionVsPingSetHolder.get(session);
            if (holder != null) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "clearIPIDsforSession: holder.currentSetOIDs''s size is {0}", holder.currentSetOIDs.size());
                }

                //No need to do this we are clearing the map anyways.
                //  for (Iterator itr2 = holder.currentSetOIDs.keySet().iterator();itr2.hasNext();)
                //  {
                //    JIObjectId oid = (JIObjectId)itr2.next();
                //    oid.setIPIDRefCountTo0();
                //  }
                holder.modified = true;
                holder.currentSetOIDs.clear(); //being done since this session is being destroyed and the corresponding COM server
                //need not be retained by us.
                holder.closed = true;

                //Should be not remove this entry ??? I think it is being retained only for the pings ... we should let this go.
                mapOfSessionVsPingSetHolder.remove(session);
            }
        }

        //remove the socket for this session associated with ping timer
        synchronized (mutex4) {
            JIComOxidStub stub = mapOfAddressVsStub.remove(session.getTargetServer());
            if (stub != null) {
                stub.close();
            }
        }

    }

    static synchronized void startResolverTimer() {
        //schedule only 1 timer task , the task to ping the OIDs obtained.
        pingTimer_2minutes.scheduleAtFixedRate(new ClientPingTimerTask(), 0, (4 * 60 * 1000));
        if (JISystem.isJavaCoClassAutoCollectionSet()) {
            pingTimer_8minutes.scheduleAtFixedRate(new ServerPingTimerTask(), 0, 8 * 60 * 1000);
        }
    }

    //only one thread , that is the main is expected to enter this one.
    static synchronized void startResolver() {
        if (resolverStarted) {
            return;
        }

        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try (final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
                    serverSocket = serverSocketChannel.socket();//new ServerSocket(0); //bind on any free port
                    serverSocket.bind(null);
                    oxidResolverPort = serverSocket.getLocalPort();
                    // server infinite loop
                    while (!stopSystem) {
                        Socket socket = serverSocket.accept();
                        synchronized (mutex) {
                            JISystem.internal_setSocket(socket);
                            //now create the JIComOxidRuntimeHelper Object and start it.
                            Properties properties = new Properties(defaults);
                            properties.put("IID", "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0".toUpperCase()); //IOxidResolver
                            JIComOxidRuntimeHelper oxidResolver = new JIComOxidRuntimeHelper(properties);
                            oxidResolver.startOxid(socket.getLocalPort(), socket.getPort());
                        }
                    }
                } catch (IOException e) {
                }
            }
        };

        Thread thread2 = new Thread(thread, "jI_OxidResolver");
        thread2.setDaemon(true);
        thread2.start();
        resolverStarted = true;
    }

    static int getOxidResolverPort() {
        return oxidResolverPort;
    }

    //Will be called from shutDownHook thread.
    static synchronized void stopResolver() {
        stopSystem = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
        }

        pingTimer_2minutes.cancel();
        pingTimer_8minutes.cancel();

        for (JIComOxidStub s : mapOfAddressVsStub.values()) {
            s.close();
        }
        mapOfAddressVsStub.clear(); //will clean up all the others as well
    }

    /**
     * Returns the MIP for the Java Instance, this will also have the
     * OXID,OID,IPID for the same.
     *
     * @param javaInstance
     * @return
     */
    static JIInterfacePointer getInterfacePointer(JISession session, JILocalCoClass component) throws JIException {
        JIInterfacePointer ptr = null;

        synchronized (mutex2) {
            if (component.isAlreadyExported()) {
                throw new JIException(JIErrorCodes.JI_JAVACOCLASS_ALREADY_EXPORTED);
            }

            component.setSession(session);
            //JIComOxidDetails details =   (JIComOxidDetails)mapOfJavaVsOxidDetails.get(component);
            //if (details != null)
            //{
            //  return details.getInterfacePtr();
            //}

            //as the ID could be repeated, this is the ipid of the interface being requested.
            String ipid = UUID.createHexString();
            String iid = component.isCoClassUnderRealIID() ? component.getCoClassIID() : IJIComObject.IID;//has to be IUnknown's IID.
            byte[] bytes = new byte[8];
            randomGen.nextBytes(bytes);
            JIOxid oxid = new JIOxid(bytes);
            byte[] bytes2 = new byte[8];
            randomGen.nextBytes(bytes2);

            JIObjectId oid = new JIObjectId(bytes2, false);

            component.setObjectId(oid.getOID());

            //JIComOxidDetails details = new JIComOxidDetails();
            JIStdObjRef objref = new JIStdObjRef(ipid, oxid, oid);
            ptr = new JIInterfacePointer(iid, oxidResolverPort, objref);

            Properties properties = new Properties(defaults2);
            properties.put("IID", "00000131-0000-0000-C000-000000000046:0.0".toUpperCase()); //IRemUnknown

            properties.put("rpc.ntlm.domain", session.getTargetServer());

            int protecttionLevel = 2;

            if (session.isSessionSecurityEnabled()) {
                protecttionLevel = 6;
                properties.setProperty("rpc.ntlm.seal", "true");
                properties.setProperty("rpc.ntlm.sign", "true");
                properties.setProperty("rpc.ntlm.keyExchange", "true");
                properties.setProperty("rpc.ntlm.keyLength", "128");
                properties.setProperty("rpc.ntlm.ntlm2", "true");
                properties.setProperty(Security.USERNAME, session.getUserName());
                properties.setProperty(Security.PASSWORD, session.getPassword());
                properties.setProperty("rpc.ntlm.ntlm2", "true");
            }

            if (session.isNTLMv2Enabled()) {
                properties.setProperty("rpc.ntlm.ntlmv2", "true");
            }

            JIComOxidRuntimeHelper remUnknown = new JIComOxidRuntimeHelper(properties);

            //now create a new JIComOxidDetails
            //this carries a reference to the javaInstance , incase we do not get pings from the client
            //at the right times, the cleaup thread will remove this entry and it's OXID as well from both the maps.
            JIComOxidDetails details = new JIComOxidDetails(component, oxid, oid, iid, ipid, ptr, remUnknown, protecttionLevel);

            mapOfJavaVsOxidDetails.put(component, details);

            mapOfOxidVsOxidDetails.put(oxid, details);

            mapOfOIDVsComponents.put(oid, component);

            listOfExportedJavaComponents.add(component);

            mapOfIPIDVsComponent.put(ipid, details); //this is the ipid of the component.

            List<JIObjectId> oids = mapOfSessionIdsVsOIDs.get(session.getSessionIdentifier());
            if (oids == null) {
                oids = new ArrayList<>();
                mapOfSessionIdsVsOIDs.put(session.getSessionIdentifier(), oids);
            }
            oids.add(oid);

            component.setAssociatedInterfacePointer(ptr);
        }
        return ptr;
    }

    //will get called from OxidResolverImpl only
    static JIComOxidDetails getOxidDetails(JIOxid oxid) {
        synchronized (mutex2) {
            return mapOfOxidVsOxidDetails.get(oxid);
        }
    }

    //Will get called from RemQueryInterface of IRemUnknown, when it gets the IPID
    //it will identify the correct component to act on.
    //on this component the IID (provided again by the client) will do a exportInstance, with a
    //randomly generated IPID and this IPID will be returned to the client.
    //The oid be the one present in details object.
    //Now , when the alter context call will come with the new IID (which was just QIed), the
    //state of RemUnknownObject will get set for the correct component using getJavaComponentForIID.
    //The next call of requestcopdu will contain the request along with the field object having the IPID of the
    //instance to call on. Pass this to the components (identified previously) invoke API., along with the rest of params
    //How will the request get decoded with out IDL info ??? Hard code for now for toString ??
    static JIComOxidDetails getComponentFromIPID(String ipid) {
        synchronized (mutex2) {
            return mapOfIPIDVsComponent.get(ipid);
        }
    }

    static void addUpdateSets(JISetId setId, List<JIObjectId> objectIdsAdded, List<JIObjectId> objectIdsDel) {
        synchronized (mutex2) {
            List<JIObjectId> listOfOIDs = mapOfSetIdVsListOfOIDs.get(setId);
            if (listOfOIDs == null) {
                listOfOIDs = new ArrayList<>();
                //first time
                listOfOIDs.addAll(objectIdsAdded);
                mapOfSetIdVsListOfOIDs.put(setId, listOfOIDs);
                //del list would be empty I presume
            } else {
                for (int i = 0; i < listOfOIDs.size(); i++) {
                    JIObjectId oid = listOfOIDs.get(i);
                    if (!objectIdsDel.contains(oid)) {
                        oid.updateLastPingTime();
                    }
                }
                listOfOIDs.addAll(objectIdsAdded);
            }
        }
    }

    static JILocalCoClass getJavaComponentFromIPID(String ipid) {
        JILocalCoClass component = null;
        synchronized (mutex2) {
            for (int i = 0; i < listOfExportedJavaComponents.size(); i++) {
                component = listOfExportedJavaComponents.get(i);
                //this will be unique, no two components will ever have same IPID for an IID.They will have different IPIDs for same IIDs.
                if (component.getIIDFromIpid(ipid) != null) {
                    break;
                }
                component = null;
            }
        }

        return component;
    }

}
