package org.jinterop.dcom.test;

import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class TenKServer {

    private final JIComServer comStub = null;
    private final IJIDispatch dispatch = null;
    private final IJIComObject unknown = null;

    public static void main(String[] args) {

        try {

            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }
            JISystem.setAutoRegisteration(true);
            for (int i = 0; i < 10000; ++i) {

                JISession session = JISession.createSession(args[1], args[2], args[3]);
                JIComServer comServer = new JIComServer(JIProgId.valueOf("MSMQ.MSMQQueueInfo"), args[0], session);
                IJIComObject unknown = comServer.createInstance();
                IJIDispatch dispatch = (IJIDispatch) JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
                //JISession.destroySession(session);
                Thread.sleep(150);
                if (i % 100 == 0) {
                    System.out.println(String.valueOf(i));
                }
                System.gc();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
