package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JILocalInterfaceDefinition;
import org.jinterop.dcom.core.JILocalMethodDescriptor;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class MSSysInfo {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    JISession session = null;
    IJIComObject sysInfoObject = null;
    IJIComObject sysInfoServer = null;
    IJIDispatch dispatch = null;
    String identifier = null;

    MSSysInfo(String[] args) throws JIException, UnknownHostException {
        session = JISession.createSession(args[1], args[2], args[3]);
        session.useSessionSecurity(true);
        JIComServer comServer = new JIComServer(JIProgId.valueOf("SYSINFO.SysInfo"), args[0], session);
        sysInfoServer = comServer.createInstance();
        sysInfoObject = sysInfoServer.queryInterface("6FBA474C-43AC-11CE-9A0E-00AA0062BB4C");
        dispatch = (IJIDispatch) JIObjectFactory.narrowObject(sysInfoObject.queryInterface(IJIDispatch.IID));

    }

    void displayValues() throws JIException {
        System.out.println("ACStatus: " + dispatch.get("ACStatus").getObjectAsShort());
        System.out.println("BatteryFullTime: " + dispatch.get("BatteryFullTime").getObjectAsInt());
        System.out.println("BatteryLifePercent: " + dispatch.get("BatteryLifePercent").getObjectAsShort());
        System.out.println("BatteryLifeTime: " + dispatch.get("BatteryLifeTime").getObjectAsInt());
        System.out.println("BatteryStatus: " + dispatch.get("BatteryStatus").getObjectAsShort());
        System.out.println("OSVersion: " + dispatch.get("OSVersion").getObjectAsFloat());
        //dispatch.callMethod("AboutBox");

    }

    void AttachEventListener() throws JIException {
        //6FBA474D-43AC-11CE-9A0E-00AA0062BB4C

        JILocalCoClass javaComponent = new JILocalCoClass(new JILocalInterfaceDefinition("6FBA474D-43AC-11CE-9A0E-00AA0062BB4C"), SysInfoEvents.class);
        javaComponent.getInterfaceDefinition().addMethodDescriptor(new JILocalMethodDescriptor("PowerStatusChanged", 8, null));
        javaComponent.getInterfaceDefinition().addMethodDescriptor(new JILocalMethodDescriptor("TimeChanged", 3, null));
        identifier = JIObjectFactory.attachEventHandler(sysInfoServer, "6FBA474D-43AC-11CE-9A0E-00AA0062BB4C", JIObjectFactory.buildObject(session, javaComponent));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //for call backs
    }

    void DetachEventListener() throws JIException {
        JIObjectFactory.detachEventHandler(sysInfoServer, identifier);
        JISession.destroySession(dispatch.getAssociatedSession());
    }

    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }
            LOGGER.setLevel(Level.OFF);
            JISystem.setAutoRegisteration(true);
            MSSysInfo sysInfo = new MSSysInfo(args);
            sysInfo.displayValues();
            sysInfo.AttachEventListener();
            Thread.sleep(20000);//now play around with power settings
            sysInfo.DetachEventListener();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
