package org.jinterop.dcom.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Date;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JILocalInterfaceDefinition;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JILocalMethodDescriptor;
import org.jinterop.dcom.core.JILocalParamsDescriptor;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIUnsignedByte;
import org.jinterop.dcom.core.JIUnsignedInteger;
import org.jinterop.dcom.core.JIUnsignedShort;
import org.jinterop.dcom.impls.JIObjectFactory;

public class SampleTestServerCallback {

        private static void append(String fileName, String data)  {
          try {
            PrintWriter pWriter = new PrintWriter(new FileWriter(fileName, true));
            pWriter.print(data);
            pWriter.flush();
            pWriter.close();
          }
          catch (IOException e){ }
        }

        public void UpdateMe(JIUnsignedShort size, JIArray array)
        {
          append("C:\\Test\\callback_j.log", "SampleTestServerCallback::UpdateMe entered with array size=" + size + "\n");
          System.out.println("SampleTestServerCallback::UpdateMe entered with array size=" + size + "\n");
          JIStruct[] structArray = (JIStruct[]) array.getArrayInstance();
          for (int i = 0; i < size.getEncapsulatedUnsigned().intValue(); i++) {
            append("C:\\Test\\callback_j.log", "Member 0= " + structArray[i].getMember(0).toString() +"\n");
            System.out.println("Array elt=" + i+ ",Member 0= " + structArray[i].getMember(0).toString() + "\n");
          }
        }

        private static JILocalInterfaceDefinition registerInterface() throws JIException {
          //Now for the Java Implementation of SampleTestServer2 interface (from the type library or IDL)
           JILocalInterfaceDefinition interfaceDefinition = new JILocalInterfaceDefinition("D3F9CE10-686C-11d2-97BF-006008BD50B1", false);//IStatisUpdateMeSink

          JIStruct VarData = new JIStruct();// Will add in the struct later on
          VarData.addMember(JIUnsignedInteger.class);
          VarData.addMember(Float.class);
          VarData.addMember(Float.class);
          VarData.addMember(JIUnsignedShort.class);
          VarData.addMember(Float.class);
          VarData.addMember(Date.class);
          VarData.addMember(JIUnsignedShort.class);

          JIStruct NonVariableData = new JIStruct();// Will add in the struct later on
          NonVariableData.addMember(JIUnsignedInteger.class);
          NonVariableData.addMember(JIUnsignedInteger.class);
          NonVariableData.addMember(JIUnsignedByte.class);
          NonVariableData.addMember(new JIPointer(new JIArray(VarData, null, 1, true),true)); //since this is an embedded pointer
          JIArray NonVariableDataArray = new JIArray(NonVariableData, null, 1, true);

          JILocalParamsDescriptor updateParamObj = new JILocalParamsDescriptor();
          updateParamObj.addInParamAsType(JIUnsignedShort.class, JIFlags.FLAG_NULL);
          updateParamObj.addInParamAsObject(NonVariableDataArray, JIFlags.FLAG_NULL);
          JILocalMethodDescriptor methodDescriptor = new JILocalMethodDescriptor("UpdateMe", updateParamObj);
          interfaceDefinition.addMethodDescriptor(methodDescriptor);

          return interfaceDefinition;
        }

        public static void testStaticUpdateMeSink(String[] args) throws JIException, InterruptedException, UnknownHostException {

          JISession session = JISession.createSession(args[1], args[2], args[3]);
          JIComServer comStub = new JIComServer(JIProgId.valueOf("TstMarsh.Test"), args[0], session);
          IJIComObject unknown = comStub.createInstance();
          IJIComObject ITest = (IJIComObject) unknown.queryInterface("89D8C8BE-1E91-11D3-910F-00C04F9403C2"); //ITest

          //Create the Java Server class. This contains the instance to be called by the COM Server
          //
          JILocalInterfaceDefinition interfaceDefinition = registerInterface();
          if (StaticSinkJavaCoClass == null)
            StaticSinkJavaCoClass = new JILocalCoClass(interfaceDefinition, new SampleTestServerCallback());
          IJIComObject iStaticSink = JIObjectFactory.buildObject(session, StaticSinkJavaCoClass);

          Object[] results = new Object[1];
          // Create the session
          JICallBuilder javaCallback = new JICallBuilder(true);
          javaCallback.setOpnum(0);
          javaCallback.addInParamAsComObject(iStaticSink, JIFlags.FLAG_NULL);
          javaCallback.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL); //Long
          System.out.println("ITest.DoSomethingAndGetSomethingBack about to call this...");
          results = ITest.call(javaCallback);//<== same exception is thrown here as well
          System.out.println("ITest.DoSomethingAndGetSomethingBack succeeded, session out =" + results[0]);
          int staticSession = ((Integer)results[0]).intValue();

          // set the refresh rate
          int rate = 4000;
          javaCallback.reInit();
          javaCallback.setOpnum(4);
          javaCallback.addInParamAsInt(staticSession, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          javaCallback.addInParamAsInt(rate, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          System.out.println("ITest.SetSomethingInSomethingsRate about to be called");
          results = ITest.call(javaCallback);
          System.out.println("ITest.SetSomethingInSomethingsRate succeeded");

          //start the session
          javaCallback.reInit();
          javaCallback.setOpnum(6);
          javaCallback.addInParamAsInt(staticSession, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          System.out.println("ITest.StartSomething about to be called");
          results = ITest.call(javaCallback);
          System.out.println("ITest.StartSomething succeeded");

          //stop the session
          Thread.sleep(10000);
          javaCallback.reInit();
          javaCallback.setOpnum(7);
          javaCallback.addInParamAsInt(staticSession, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          System.out.println("ITest.StopSomething about to be called");
          results = ITest.call(javaCallback);
          System.out.println("ITest.StopSomething succeeded");

          //destroy the session
          Thread.sleep(1000);
          javaCallback.reInit();
          javaCallback.setOpnum(1);
          javaCallback.addInParamAsInt(staticSession, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          System.out.println("ITest.DestroySomething about to be called");
          results = ITest.call(javaCallback);
          System.out.println("ITest.DestroySomething succeeded");

          JISession.destroySession(session);
        }

        public static void testSinkDebug(String[] args) throws JIException, InterruptedException, UnknownHostException {

          JISession session = JISession.createSession(args[1], args[2], args[3]);
          JIComServer comStub = new JIComServer(JIProgId.valueOf("TstMarsh.Test"), args[0], session);
          IJIComObject unknown = comStub.createInstance();
          IJIComObject ITest = (IJIComObject) unknown.queryInterface("89D8C8BE-1E91-11D3-910F-00C04F9403C2"); //ITest

          //Create the Java Server class. This contains the instance to be called by the COM Server
          //
          JILocalInterfaceDefinition interfaceDefinition = registerInterface();
          if (StaticSinkJavaCoClass != null)
            StaticSinkJavaCoClass = new JILocalCoClass(interfaceDefinition, new SampleTestServerCallback());

          IJIComObject iStaticSink = JIObjectFactory.buildObject(session, StaticSinkJavaCoClass);

          Object[] results = new Object[1];
          // Create the session
          JICallBuilder javaCallback = new JICallBuilder(true);
          javaCallback.setOpnum(8);
          javaCallback.addInParamAsComObject(iStaticSink, JIFlags.FLAG_NULL);
          javaCallback.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL); //Long
          results = ITest.call(javaCallback);//<== same exception is thrown here as well
          System.out.println("ITest.DoSomethingAndGetSomethingBack succeeded, session out =" + results[0]);
          int staticSession = ((Integer)results[0]).intValue();

          Thread.sleep(30000);

          javaCallback.reInit();
          javaCallback.setOpnum(1);
          javaCallback.addInParamAsInt(staticSession, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
          System.out.println("ITest.UnDoSomething about to be called");
          results = ITest.call(javaCallback);
          System.out.println("ITest.UnDoSomething succeeded");

          JISession.destroySession(session);
        }

        public static void main(String[] args) {
          try {
            if (args.length < 4) {
              System.out.println("Please provide address domain username password");
              return;
            }
            JISystem.setInBuiltLogHandler(false);
            JISystem.setAutoRegisteration(true);
            for (int i=0; i<100; i++){
              System.out.println("**********************Invoking callback sequence....\n");
              testStaticUpdateMeSink(args);
              Thread.sleep(12000);
            }
//            testSinkDebug(args);
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }

        static JILocalCoClass StaticSinkJavaCoClass;

      }
