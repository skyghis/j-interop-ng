package org.jinterop.dcom.test;

import java.net.UnknownHostException;
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
import org.jinterop.dcom.impls.automation.JIExcepInfo;

public class MSExcel3 {

    private JIComServer comServer = null;
    private IJIDispatch dispatch = null;
    private IJIComObject unknown = null;
    private IJIDispatch dispatchOfWorkSheets = null;
    private IJIDispatch dispatchOfWorkBook = null;

    public MSExcel3(String address, String args[]) throws JIException, UnknownHostException {
        JISession session = JISession.createSession(args[1], args[2], args[3]);
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

        dispatch.put("DisplayAlerts", new JIVariant(true));
    }

    public void createWorkSheet() throws JIException {
        int dispId = dispatch.getIDsOfNames("Workbooks");

        JIVariant outVal = dispatch.get(dispId);
        IJIDispatch dispatchOfWorkBooks = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());

        JIVariant[] outVal2 = dispatchOfWorkBooks.callMethodA("Open", new Object[]{
            new JIString("C:\\temp\\chart.xls"), Boolean.TRUE, Boolean.TRUE, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM()});
        dispatchOfWorkBook = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());

        outVal = dispatchOfWorkBook.get("Worksheets");
        dispatchOfWorkSheets = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());

        outVal2 = dispatchOfWorkSheets.get("Item", new Object[]{new JIVariant(1)});
        IJIDispatch sheet = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());
        outVal2 = sheet.get("Range", new Object[]{new JIString("A1:B19"), JIVariant.OPTIONAL_PARAM()});
        IJIDispatch range = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());

        Integer[][] newValue = {
            {121, 117},
            {111, 156},
            {132, 138},
            {116, 119},
            {148, 126},
            {163, 143},
            {174, 135},
            {136, 142},
            {142, 163},
            {121, 117},
            {111, 156},
            {132, 138},
            {116, 119},
            {148, 126},
            {163, 143},
            {174, 135},
            {136, 142},
            {142, 163},
            {121, 117}
        };

        range.put("Value", new JIVariant(new JIArray(newValue)));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < 60; j++) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer temp1 = newValue[0][0];
            Integer temp2 = newValue[0][1];
            int i = 0;
            for (i = 1; i < newValue.length; i++) {
                System.arraycopy(newValue[i], 0, newValue[i - 1], 0, newValue[i - 1].length);
            }

            newValue[i - 1][0] = temp1;
            newValue[i - 1][1] = temp2;
            // For Excel XP, use: range.setValue2(newValue);
            range.put("Value", new JIVariant(new JIArray(newValue)));
        }

        outVal2 = sheet.get("ChartObjects", new Object[]{JIVariant.OPTIONAL_PARAM()});
        IJIDispatch chartObjects = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());
        outVal2 = chartObjects.callMethodA("Add", new Object[]{new Double(100), new Double(30), new Double(400), new Double(250)});
        //outVal2 = chartObjects.get("Item", new Object[]{new Integer(1)});
        IJIDispatch chartObject = (IJIDispatch) JIObjectFactory.narrowObject(outVal2[0].getObjectAsComObject());
        outVal = chartObject.get("Chart");
        IJIDispatch chart = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());
        chart.callMethod("SetSourceData", new Object[]{range, JIVariant.OPTIONAL_PARAM()});
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        outVal = sheet.get("PageSetup");
        IJIDispatch pageSetup = (IJIDispatch) JIObjectFactory.narrowObject(outVal.getObjectAsComObject());
        pageSetup.put("Orientation", new JIVariant(2));
        pageSetup.put("Zoom", new JIVariant(100));
        try {
            sheet.callMethod("PrintOut", new Object[]{JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),
                JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        } catch (JIException e) {
            e.printStackTrace();
            JIExcepInfo excepInfo = sheet.getLastExcepInfo();
            System.out.println("Error Code in EXCEPINFO: " + excepInfo.getErrorCode());
        }
        dispatchOfWorkBook.callMethod("close", new Object[]{Boolean.FALSE, JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()});
        dispatch.callMethod("Quit");
        JISession.destroySession(dispatch.getAssociatedSession());
    }

    public static void main(String[] args) {

        try {
            if (args.length < 4) {
                System.out.println("Please provide address domain username password");
                return;
            }
            MSExcel3 test = new MSExcel3(args[0], args);
            test.startExcel();
            test.showExcel();
            test.createWorkSheet();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
