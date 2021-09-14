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
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIRuntimeException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.transport.JIComRuntimeEndpoint;
import org.jinterop.dcom.transport.JIComRuntimeTransportFactory;
import rpc.Stub;
import rpc.core.UUID;

/**
 * Used to manipulate Oxid details. one instance is created per binding call to
 * the oxid resolver.
 *
 * @since 1.0
 *
 */
final class JIComOxidRuntimeHelper extends Stub {

    JIComOxidRuntimeHelper(Properties properties) {
        super.setTransportFactory(JIComRuntimeTransportFactory.getSingleTon());
        super.setProperties(properties);
        super.setAddress("127.0.0.1[135]");//this is never consulted so , putting localhost here.
    }

    @Override
    protected String getSyntax() {
        //return "99fcfec4-5260-101b-bbcb-00aa0021347a:0.0";//IOxidResolver IID
        return UUID.NIL_UUID + ":0.0"; //returning nothing
    }

    void startOxid(int portNumLocal, int portNumRemote) throws IOException {
        Thread oxidResolverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (JISystem.getLogger().isLoggable(Level.INFO)) {
                        JISystem.getLogger().log(Level.INFO, "started startOxid thread: {0}", Thread.currentThread().getName());
                    }
                    attach();
                    ((JIComRuntimeEndpoint) getEndpoint()).processRequests(new OxidResolverImpl(getProperties()), null, new ArrayList<>());
                } catch (IOException e) {
                    if (JISystem.getLogger().isLoggable(Level.WARNING)) {
                        JISystem.getLogger().throwing("Oxid Resolver Thread", "run", e);
                        JISystem.getLogger().log(Level.WARNING, "Oxid Resolver Thread: {0} , on thread Id: {1}", new Object[]{e.getMessage(), Thread.currentThread().getName()});
                    }
                } finally {
                    try {
                        getEndpoint().detach();
                    } catch (IOException e) {
                    }
                }
                if (JISystem.getLogger().isLoggable(Level.INFO)) {
                    JISystem.getLogger().log(Level.INFO, "terminating startOxid thread: {0}", Thread.currentThread().getName());
                }
            }
        }, "jI_OxidResolver_Client[" + portNumLocal + " , " + portNumRemote + "]");
        oxidResolverThread.setDaemon(true);
        oxidResolverThread.start();
    }

    //returns the port to which the server is listening.
    Object[] startRemUnknown(final String baseIID, final String ipidOfRemUnknown, final String ipidOfComponent, final List<String> listOfSupportedInterfaces) throws IOException {
        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        final ServerSocket serverSocket = serverSocketChannel.socket();//new ServerSocket(0);
        //serverSocket.setSoTimeout(120*1000); //2 min timeout.
        serverSocket.bind(null);
        int remUnknownPort = serverSocket.getLocalPort();
        //have to pick up a random name so adding the ipid of remunknown this is a uuid so the string is quite random.
        final ThreadGroup remUnknownForThisListener = new ThreadGroup("ThreadGroup - " + baseIID + "[" + ipidOfRemUnknown + "]");
        remUnknownForThisListener.setDaemon(true);
        Thread remUnknownThread = new Thread(remUnknownForThisListener, new Runnable() {
            @Override
            public void run() {
                if (JISystem.getLogger().isLoggable(Level.INFO)) {
                    JISystem.getLogger().log(Level.INFO, "started RemUnknown listener thread for : {0}", Thread.currentThread().getName());
                }
                try {

                    while (true) {
                        final Socket socket = serverSocket.accept();
                        if (JISystem.getLogger().isLoggable(Level.INFO)) {
                            JISystem.getLogger().log(Level.INFO, "RemUnknown listener: Got Connection from {0}", socket.getPort());
                        }

                        //now create the JIComOxidRuntimeHelper Object and start it. We need a new one since the old one is already attached to the listener.
                        final JIComOxidRuntimeHelper remUnknownHelper = new JIComOxidRuntimeHelper(getProperties());
                        synchronized (JIComOxidRuntime.mutex) {
                            JISystem.internal_setSocket(socket);
                            remUnknownHelper.attach();
                        }

                        //now start a new thread with this socket
                        Thread remUnknown = new Thread(remUnknownForThisListener, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((JIComRuntimeEndpoint) remUnknownHelper.getEndpoint()).processRequests(new RemUnknownObject(ipidOfRemUnknown, ipidOfComponent), baseIID, listOfSupportedInterfaces);
                                } catch (SmbAuthException e) {
                                    JISystem.getLogger().log(Level.WARNING, "JIComOxidRuntimeHelper RemUnknownThread (not listener)", e);
                                    throw new JIRuntimeException(JIErrorCodes.JI_CALLBACK_AUTH_FAILURE);
                                } catch (SmbException e) {
                                    //System.out.println(e.getMessage());
                                    JISystem.getLogger().log(Level.WARNING, "JIComOxidRuntimeHelper RemUnknownThread (not listener)", e);
                                    throw new JIRuntimeException(JIErrorCodes.JI_CALLBACK_SMB_FAILURE);
                                } catch (ClosedByInterruptException e) {
                                    JISystem.getLogger().log(Level.INFO, "JIComOxidRuntimeHelper RemUnknownThread (not listener){0} is purposefully closed by interruption.", Thread.currentThread().getName());
                                } catch (IOException e) {
                                    JISystem.getLogger().log(Level.WARNING, "JIComOxidRuntimeHelper RemUnknownThread (not listener)", e);
                                } finally {
                                    try {
                                        remUnknownHelper.detach();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        }, "jI_RemUnknown[" + baseIID + " , L(" + socket.getLocalPort() + "):R(" + socket.getPort() + ")]");
                        remUnknown.setDaemon(true);
                        remUnknown.start();
                    }
                } catch (ClosedByInterruptException e) {
                    JISystem.getLogger().log(Level.INFO, "JIComOxidRuntimeHelper RemUnknownListener{0} is purposefully closed by interruption.", Thread.currentThread().getName());
                } catch (IOException e) {
                    if (JISystem.getLogger().isLoggable(Level.WARNING)) {
                        JISystem.getLogger().log(Level.WARNING, "JIComOxidRuntimeHelper RemUnknownListener", e);
                        JISystem.getLogger().log(Level.WARNING, "RemUnknownListener Thread: {0} , on thread Id: {1}", new Object[]{e.getMessage(), Thread.currentThread().getName()});
                    }
                    //e.printStackTrace();
                } catch (Throwable e) {
                    if (JISystem.getLogger().isLoggable(Level.WARNING)) {
                        JISystem.getLogger().log(Level.WARNING, "JIComOxidRuntimeHelper RemUnknownListener", e);
                    }
                }

                if (JISystem.getLogger().isLoggable(Level.INFO)) {
                    JISystem.getLogger().log(Level.INFO, "terminating RemUnknownListener thread: {0}", Thread.currentThread().getName());
                }
            }
        }, "jI_RemUnknownListener[" + baseIID + " , " + remUnknownPort + "]");

        remUnknownThread.setDaemon(true);
        remUnknownThread.start();
        return new Object[]{remUnknownPort, remUnknownForThisListener};
    }
}
