/*
 * Main.java
 *
 * Created on 20 ������ 2007 �., 14:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jinterop.dcom.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class Main {
    
    public void Execute(JIString str) {
        System.out.println(str.getString());
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        if (args.length < 4) {
            System.out.println("Please provide address domain username password");
            return;
        }
        
        
        
        try {
            
            String domain       = args[1];
            String username     = args[2];
            String password     = args[3];
                    
            JISystem.getLogger().setLevel(Level.FINEST);
            JISystem.setInBuiltLogHandler(false);
            JISystem.setAutoRegisteration(true);
            JISession session3 = JISession.createSession(domain,username,password);
            session3.useSessionSecurity(true);
            JIComServer virtualServer = new JIComServer(JIProgId.valueOf(session3, "VirtualServer.Application"),args[0],session3);
            IJIComObject unkVirtualServer = virtualServer.createInstance();    
            IJIDispatch dispatchVirtualServer = (IJIDispatch)JIObjectFactory.narrowObject(unkVirtualServer.queryInterface(IJIDispatch.IID));                        
                        
 
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (JIException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
    
}