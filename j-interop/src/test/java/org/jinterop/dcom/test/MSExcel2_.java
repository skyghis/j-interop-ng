package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

public class MSExcel2_ {

    private static final Logger LOGGER = Logger.getLogger("org.jinterop");
    private JIComServer comServer = null;
    private IJIDispatch dispatch = null;
    private IJIComObject unknown = null;
    private IJIDispatch dispatchOfWorkSheets = null;
    private IJIDispatch dispatchOfWorkBook = null;
    private IJIDispatch dispatchOfWorkSheet = null;
    private JISession session = null;

    public MSExcel2_(String address, String[] args) throws JIException, UnknownHostException {
        session = JISession.createSession(args[1], args[2], args[3]);
        session.useSessionSecurity(true);
        comServer = new JIComServer(JIProgId.valueOf("Excel.Application"), address, session);
    }

    public void startExcel() throws JIException {
        unknown = comServer.createInstance();
        dispatch = (IJIDispatch) JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
    }

    public void showExcel() throws JIException {
        int dispId = dispatch.getIDsOfNames("Visible");
        JIVariant variant = new JIVariant(true);
        dispatch.put(dispId, variant);
    }

    public void createWorkSheet() throws JIException {
        int dispId = dispatch.getIDsOfNames("Workbooks");

        JIVariant outVal = dispatch.get(dispId);

        IJIDispatch dispatchOfWorkBooks = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());

        JIVariant[] outVal2 = dispatchOfWorkBooks.callMethodA("Add", new Object[]{JIVariant.OPTIONAL_PARAM()});
        dispatchOfWorkBook = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());

        outVal = dispatchOfWorkBook.get("Worksheets");

        dispatchOfWorkSheets = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());

        outVal2 = dispatchOfWorkSheets.callMethodA("Add", new Object[]{JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        dispatchOfWorkSheet = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());
    }

    public void pasteArrayToWorkSheet(int nRow) throws JIException {
        int dispId = dispatchOfWorkSheet.getIDsOfNames("Range");
        JIVariant variant = new JIVariant(new JIString("A1:C" + nRow));
        Object[] out = new Object[]{JIVariant.class};
        JIVariant[] outVal2 = dispatchOfWorkSheet.get(dispId, new Object[]{variant});
        IJIDispatch dispRange = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());

        JIVariant[][] newValue = new JIVariant[nRow][3];

        for (JIVariant[] newValue1 : newValue) {
            for (int j = 0; j < newValue1.length; j++) {
                newValue1[j] = new JIVariant((10.0 * Math.random()));
            }
        }

        dispRange.put("Value2", new JIVariant(new JIArray(newValue)));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JIVariant variant2 = dispRange.get("Value2");
        JIArray newValue2 = variant2.getObjectAsArray();
        newValue = (JIVariant[][]) newValue2.getArrayInstance();
        for (JIVariant[] newValue1 : newValue) {
            for (JIVariant item : newValue1) {
                System.out.print(item + "\t");
            }
            System.out.println();
        }

        //Now write the value down
        dispRange.put("Value2", new JIVariant(newValue2));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dispatchOfWorkBook.callMethod("close", new Object[]{Boolean.FALSE, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        dispatch.callMethod("Quit");
        JISession.destroySession(session);

    }

    public static void main(String[] args) {

        try {

            LOGGER.setLevel(Level.FINEST);

            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }
            int nRow = 600;

            if (args.length > 4) {
                try {
                    nRow = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {

                }
            }

            MSExcel2_ test = new MSExcel2_(args[0], args);

            test.startExcel();
            test.showExcel();
            test.createWorkSheet();

            test.pasteArrayToWorkSheet(nRow);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
