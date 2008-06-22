package org.jinterop.dcom.test;

import java.net.UnknownHostException;
import java.util.Date;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallObject;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIUnsigned;
import org.jinterop.dcom.core.JIUnsignedInteger;
import org.jinterop.dcom.core.JIUnsignedShort;
import org.jinterop.dcom.impls.IJIDispatch;

/** Contributed Code sample. Works in conjunction with SampleTestServers.zip
 *
 *
 *
 */
public class SampleTestServer {

  private JIComServer comStub = null;
  private IJIComObject comObject = null;
  private IJIDispatch dispatch = null;
  private String address = null;
  private JISession session = null;

  public SampleTestServer(String address, String[] args) throws JIException, UnknownHostException {
    this.address = address;
    session = JISession.createSession(args[1], args[2], args[3]);
    comStub = new JIComServer(JIProgId.valueOf(session, "SampleTestServer.TestServer"), address, session);
    IJIComObject unknown = comStub.createInstance();
    comObject = (IJIComObject) unknown.queryInterface("1F438B1C-02BA-462E-A971-8E0640C141E5"); //ITestServer
  }

  public void performSquare(String[] args) throws JIException, InterruptedException, UnknownHostException {

    JICallObject callObject = new JICallObject(comObject.getIpid(), true);
    callObject.setOpnum(1); //obtained from the IDL or TypeLib. //    AskTestServerToSquare
    Object results[];
    short i = 3;
    callObject.addInParamAsShort(i, JIFlags.FLAG_NULL);
    callObject.addOutParamAsType(Short.class, JIFlags.FLAG_NULL); //Short
    results = comObject.call(callObject);
    System.out.println("ITestServer.AskTestServerToSquare succeeded, input=" + i + " output=" + results[0]);
  }

  public void performCallback(String[] args) throws JIException, InterruptedException, UnknownHostException {


  }

  public void getTCharArray()
      throws JIException, InterruptedException, UnknownHostException {
      System.gc();
      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(6);
      Object results[];

      callObject.addOutParamAsObject(new JIArray(Byte.class, new int[]{50},1,false), JIFlags.FLAG_NULL);
      results = comObject.call(callObject);

      JIArray arrayOfResults = (JIArray)results[0];
      Byte[] arrayOfBytes = (Byte[]) arrayOfResults.getArrayInstance();
      int length = 50;
      for (int i = 0; i < length; i++) {
        System.out.println(arrayOfBytes[i].byteValue());
      }
  }


  public void setTCharArray()
      throws JIException, InterruptedException, UnknownHostException {
      System.gc();
      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(7);
      Object results[];
      callObject.addInParamAsString("AHHHHHHH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);

      results = comObject.call(callObject);
  }

  public void setConformantIntArray()
      throws JIException, InterruptedException, UnknownHostException {
      System.gc();
      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(9);
      Object results[];
      int i = 4;
      Integer[] intAry = new Integer[i];
      for(int j = 0; j < i; j++) {
          intAry[j] = new Integer(j);
      }
      JIArray ary = new JIArray(intAry, true);
      callObject.addInParamAsInt(i, JIFlags.FLAG_NULL);
      callObject.addInParamAsArray(ary, JIFlags.FLAG_NULL);
      results = comObject.call(callObject);
  }

  public void getConformantIntArray()
      throws JIException, InterruptedException, UnknownHostException {

	  JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(8);
      Object results[];

      callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
      callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
      results = comObject.call(callObject);

      JIArray arrayOfResults = (JIArray)((JIPointer)results[1]).getReferent();
      Integer[] arrayOfIntegers = (Integer[]) arrayOfResults.getArrayInstance();
      int length = ((Integer)results[0]).intValue();
      for (int i = 0; i < length; i++) {
        System.out.println(arrayOfIntegers[i].intValue());
      }
    }

    public void GetStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(10); //obtained from the IDL or TypeLib. //
        Object results[];

        // change the struct to have the array as the last item
        JIStruct struct = new JIStruct();
        JIArray longArray = new JIArray(Integer.class, new int[]{50},1,false);
        struct.addMember(Integer.class);
        struct.addMember(Float.class);
        struct.addMember(longArray);
        callObject.addOutParamAsObject(new JIPointer(struct), JIFlags.FLAG_NULL);

        results = comObject.call(callObject);
        System.out.println(results[0]);
    }

    public void getSimpleStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(12); //obtained from the IDL or TypeLib. //
        Object results[];

        JIStruct struct = new JIStruct();
        struct.addMember(Integer.class);
        struct.addMember(Double.class);
        struct.addMember(Float.class);
        callObject.addOutParamAsObject(new JIPointer(struct), JIFlags.FLAG_NULL);

        results = comObject.call(callObject);
        System.out.println(results[0]);
    }

/*
     typedef struct stSimpleStruct
    {
       long     l;
       double   d;
       float    f;
    } SimpleStruct;

   [helpstring("13 method GetConformantStructArray")] HRESULT GetConformantStructArray(unsigned short* unDataSize,
                                                     [out, size_is(,*unDataSize)] SimpleStruct** ppSimpleStruct);


*/
    public void getSimpleStructArray(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(13); //obtained from the IDL or TypeLib. //
        Object results[];

        callObject.addOutParamAsType(JIUnsignedShort.class, JIFlags.FLAG_NULL);

        JIStruct struct = new JIStruct();
        struct.addMember(Integer.class);
        struct.addMember(Double.class);
        struct.addMember(Float.class);
        JIArray DataArray = new JIArray(struct, null, 1, true);
        callObject.addOutParamAsObject(new JIPointer(DataArray), JIFlags.FLAG_NULL);
        results = comObject.call(callObject);
        System.out.println(((JIUnsignedShort)results[0]).getEncapsulatedUnsigned());
    }

    public void getConformantStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(14); //obtained from the IDL or TypeLib. //
        Object results[];

        JIStruct struct = new JIStruct();
        struct.addMember(Integer.class);
        struct.addMember(Double.class);
        struct.addMember(JIUnsignedShort.class);
        JIArray longArray = new JIArray(Integer.class, null, 1, true);
        struct.addMember(new JIPointer(longArray));
        callObject.addOutParamAsObject(new JIPointer(struct), JIFlags.FLAG_NULL);

        results = comObject.call(callObject);
        System.out.println(results[0]);
    }


    public void GetStructStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(17); //obtained from the IDL or TypeLib. //
      Object results[];

      JIStruct struct = new JIStruct();
      struct.addMember(Integer.class);
      struct.addMember(Double.class);
      struct.addMember(JIUnsignedShort.class);
      JIArray longArray = new JIArray(Integer.class, null, 1, true);
      struct.addMember(new JIPointer(longArray));

      JIStruct StructStruct = new JIStruct();
      StructStruct.addMember(Integer.class);
      StructStruct.addMember(Double.class);
      StructStruct.addMember(struct);

     callObject.addOutParamAsObject(new JIPointer(StructStruct), JIFlags.FLAG_NULL);

      results = comObject.call(callObject);
      System.out.println(results[0]);

    }


    public void GetStructStructArray(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(18); //obtained from the IDL or TypeLib. //
      Object results[];

      JIStruct struct = new JIStruct();
      struct.addMember(Integer.class);
      struct.addMember(Double.class);
      struct.addMember(JIUnsignedShort.class);
      JIArray longArray = new JIArray(Integer.class, null, 1, true);
      struct.addMember(new JIPointer(longArray));

      JIStruct StructStruct = new JIStruct();
      StructStruct.addMember(Integer.class);
      StructStruct.addMember(Double.class);
      StructStruct.addMember(struct);

      JIArray DataArray = new JIArray(StructStruct, null, 1, true);
      callObject.addOutParamAsType(JIUnsignedShort.class, JIFlags.FLAG_NULL);
      callObject.addOutParamAsObject(new JIPointer(DataArray), JIFlags.FLAG_NULL);

      results = comObject.call(callObject);
      System.out.println(((JIUnsignedShort)results[0]).getEncapsulatedUnsigned());

    }

    public void GetSimpleArrayStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(19); //obtained from the IDL or TypeLib. //
      Object results[];

      JIStruct simpleStruct = new JIStruct();
      simpleStruct.addMember(Integer.class);
      simpleStruct.addMember(Double.class);
      simpleStruct.addMember(Float.class);

      JIStruct simpleArrayStruct = new JIStruct();
      simpleArrayStruct.addMember(Integer.class);
      simpleArrayStruct.addMember(Double.class);
      simpleArrayStruct.addMember(JIUnsignedShort.class);
      JIArray structArray = new JIArray(simpleStruct, null, 1, true);
      simpleArrayStruct.addMember(new JIPointer(structArray));

      callObject.addOutParamAsObject(new JIPointer(simpleArrayStruct), JIFlags.FLAG_NULL);

      results = comObject.call(callObject);
      System.out.println(results[0]);

    }

//[helpstring("20 method GetSimpleArrayStructArray")] HRESULT GetSimpleArrayStructArray([out] unsigned short* unDataSize,
//    [out, size_is(,*unDataSize)] SimpleArrayStruct** pp);
    public void GetSimpleArrayStructArray(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

      JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(20); //obtained from the IDL or TypeLib. //
      Object results[];

      JIStruct simpleStruct = new JIStruct();
      simpleStruct.addMember(Integer.class);
      simpleStruct.addMember(Double.class);
      simpleStruct.addMember(Float.class);

      JIStruct simpleArrayStruct = new JIStruct();
      simpleArrayStruct.addMember(Integer.class);
      simpleArrayStruct.addMember(Double.class);
      simpleArrayStruct.addMember(JIUnsignedShort.class);
      JIArray structArray = new JIArray(simpleStruct, null, 1, true);
      simpleArrayStruct.addMember(new JIPointer(structArray));//try no pointer next

      JIArray DataArray = new JIArray(simpleArrayStruct, null, 1, true);
      callObject.addOutParamAsType(JIUnsignedShort.class, JIFlags.FLAG_NULL);
      callObject.addOutParamAsObject(new JIPointer(DataArray), JIFlags.FLAG_NULL);

      results = comObject.call(callObject);
      System.out.println(((JIUnsignedShort)results[0]).getEncapsulatedUnsigned());

    }


    public void SetSimpleArrayStructArray(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(21); //obtained from the IDL or TypeLib. ModifyStaticData
        Object results[];

        JIStruct simpleStruct = new JIStruct();
        simpleStruct.addMember(new Integer(5));
        simpleStruct.addMember(new Double(25));
        simpleStruct.addMember(new Float(2.5));

        Integer shortValue = new Integer(1);
        JIStruct simpleArrayStruct = new JIStruct();
        simpleArrayStruct.addMember(new Integer(54));
        simpleArrayStruct.addMember(new Double(5));
        simpleArrayStruct.addMember(JIUnsigned.getUnsigned(shortValue, JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT));
        JIStruct[] structArray = new JIStruct[1];
        structArray[0] = simpleStruct;
        simpleArrayStruct.addMember(new JIPointer(new JIArray(structArray, true)));
        JIStruct[] DataArray = new JIStruct[1];
        DataArray[0] = simpleArrayStruct;
        short size = 1;
        callObject.addInParamAsShort(size, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(DataArray, true), JIFlags.FLAG_NULL);

        results = comObject.call(callObject);
        System.out.println("SetSimpleArrayStructArray worked!");
    }



  // Index out of bound exception
  public void GetStaticStruct(String[] args)
      throws JIException, InterruptedException, UnknownHostException {

       JICallObject callObject = new JICallObject(comObject.getIpid(), true);
      callObject.setOpnum(15); //obtained from the IDL or TypeLib. //
      Object results[];

      JIStruct varStruct = new JIStruct();
      varStruct.addMember(JIUnsignedInteger.class);
      varStruct.addMember(Float.class);
      varStruct.addMember(Float.class);
      varStruct.addMember(JIUnsignedShort.class);
      varStruct.addMember(Float.class);
      varStruct.addMember(Date.class);
      varStruct.addMember(JIUnsignedInteger.class);

      JIStruct pointStruct = new JIStruct();
      pointStruct.addMember(JIUnsignedInteger.class);
      pointStruct.addMember(JIUnsignedInteger.class);
      pointStruct.addMember(Byte.class);
      JIArray structArray = new JIArray(varStruct, null, 1, true);
      pointStruct.addMember(new JIPointer(structArray));


      JIArray DataArray = new JIArray(pointStruct, null, 1, true);
      callObject.addOutParamAsType(JIUnsignedShort.class, JIFlags.FLAG_NULL);
      callObject.addOutParamAsObject(new JIPointer(DataArray, false), JIFlags.FLAG_NULL);


      results = comObject.call(callObject);
      System.out.println(((JIUnsignedShort)results[0]).getEncapsulatedUnsigned());

    }

    public void SetStaticStruct(String[] args)
        throws JIException, InterruptedException, UnknownHostException {

        JICallObject callObject = new JICallObject(comObject.getIpid(), true);
        callObject.setOpnum(16); //obtained from the IDL or TypeLib.
        Object results[];

        JIUnsignedShort j;
        Long value = new Long(10);
        Integer shortValue = new Integer(5);
        JIStruct varStruct = new JIStruct();
        varStruct.addMember(JIUnsigned.getUnsigned(value, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT));
        varStruct.addMember(new Float(1.1));
        varStruct.addMember(new Float(1.2));
        varStruct.addMember(JIUnsigned.getUnsigned(shortValue, JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT));
        varStruct.addMember(new Float(1.0));
        varStruct.addMember(new Date());
        varStruct.addMember(JIUnsigned.getUnsigned(value, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT));

        JIStruct pointStruct = new JIStruct();
        pointStruct.addMember(JIUnsigned.getUnsigned(new Long(15), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT));
        pointStruct.addMember(JIUnsigned.getUnsigned(new Long(10), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT));
        pointStruct.addMember(new Byte((byte)1));
        JIStruct[] varStructArray = new JIStruct[1];
        varStructArray[0] = varStruct;
        pointStruct.addMember(new JIPointer(new JIArray(varStructArray, true))); //since this is an embedded pointer

        JIStruct[] pointAry = new JIStruct[1];
        pointAry[0] = pointStruct;

        JIArray ary = new JIArray(pointAry,true);
        callObject.addInParamAsShort((short)1, JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT);
        callObject.addInParamAsArray(ary, JIFlags.FLAG_NULL);

        results = comObject.call(callObject);
        System.out.println("SetStaticStruct worked!");
    }


  public static void main(String[] args) {

    try {
      if (args.length < 4) {
        System.out.println("Please provide address domain username password");
        return;
      }
      JISystem.setInBuiltLogHandler(false);
      JISystem.setAutoRegisteration(true);
      SampleTestServer test = new SampleTestServer(args[0], args);

      test.performCallback(args);
      test.performSquare(args);
      test.setTCharArray();
      test.getTCharArray();
      test.setConformantIntArray();
      test.getConformantIntArray();
      test.GetStruct(args);
      test.getSimpleStruct(args);
      test.getConformantStruct(args);
      test.getSimpleStructArray(args);
      test.GetStructStruct(args);
      test.GetStructStructArray(args);
      test.GetSimpleArrayStruct(args);
//
      test.GetSimpleArrayStructArray(args);
      test.SetSimpleArrayStructArray(args);
      test.GetStaticStruct(args);
      test.SetStaticStruct(args);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


}
