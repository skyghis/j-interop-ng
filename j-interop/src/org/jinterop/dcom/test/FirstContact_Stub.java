package org.jinterop.dcom.test;

import java.util.Date;
import java.util.ResourceBundle;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JICurrency;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIInterfaceDefinition;
import org.jinterop.dcom.core.JIJavaCoClass;
import org.jinterop.dcom.core.JIMethodDescriptor;
import org.jinterop.dcom.core.JIParameterObject;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIProgId;
import org.jinterop.dcom.core.JISession;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIUnsigned;
import org.jinterop.dcom.core.JIUnsignedInteger;
import org.jinterop.dcom.core.JIUnsignedShort;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.FuncDesc;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.jinterop.dcom.impls.automation.IJITypeInfo;
import org.jinterop.dcom.impls.automation.IJITypeLib;


public class FirstContact_Stub implements FirstContact {

	private JIComServer stub = null;

	JISession session = null;
	public FirstContact_Stub(String address) throws Exception
	{
		JIArray arry123 = new JIArray(new Byte[10][0],true);
		JIStruct struct123 = new JIStruct();
		struct123.addMember(arry123);
		struct123.addMember(0,new Integer(1));
		struct123.addMember(0,new JIPointer(arry123));

		//JIArray array = new JIArray(new short[]{0});
		JISystem.setAutoRegisteration(true);
		JISystem.setInBuiltLogHandler(false);
		//Config.setProperty("jcifs.smb.client.domain","ITLINFOSYS");
		 session = JISession.createSession("FDGNT","roopchand","QweQwe007");
		 //session = JISession.createSession("10.74.85.56","itl-hw-38602a\\Vikram","Infosys@123");
		//session = JISession.createSession("federation","administrator","enterprise");
		//stub = new JIComServer(JIClsid.valueOf("8B21775E-717D-11CE-AB5B-D41203C10000"),address,session);
		//stub = new JIComServer(JIProgId.valueOf(session,"TestCOM123.TestServer2"),address,session);
		//stub = new JIComServer(JIProgId.valueOf(session,"VirtualServer.Application"),address,session);

		// stub = new JIComServer(JIProgId.valueOf(session,"ArrayTry.myarray"),address,session);
		stub = new JIComServer(JIProgId.valueOf("ATLDemo.TestSafeArray"),address,session);
		//stub = new JIComServer(JIProgId.valueOf(session,"SafeArrayDemo.SafeArrayTest"),address,session);
		 //stub = new JIComServer(JIProgId.valueOf(session,"Project1.Class1"),address,session);
		 //stub = new JIComServer(JIProgId.valueOf(session,"TLI.TLIApplication"),address,session);

		 //stub = new JIComServer(JIProgId.valueOf(session,"TestSinglePtr.TestSinglePtr2"),address,session);
	}
//	protected String getSyntax() {
//		// TODO Auto-generated method stub
//		//return "e1af8308-5d1f-11c9-91a4-08002b14a0fa:3.0";
//		return "4d9f4ab8-7d1c-11cf-861e-0020af6e7c57:0.0";
//	}

	public void obtainReference() {
		try {
//			System.setOut(new PrintStream(new FileOutputStream("c:/temp/vikram.txt")));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// TODO Auto-generated method stub
		//try {
			//call(Endpoint.IDEMPOTENT,new JIRemActivation("10000002-0000-0000-0000-000000000001"));
			//IIDSum 10000001-0000-0000-0000-000000000001,ICreate_MyCar 5DD52389-B1A4-4fe7-B131-0F8EF73DD175, IParseDisplayName {0000011A-0000-0000-C000-000000000046}
			//ITestServer 35AF6037-294F-48B2-9B7E-AA8D4885E084
			//IID_ITestServer2 620012E2-69E3-4DC0-B553-AE252524D2F6
		//media player2 20D4F5E0-5475-11D2-9774-0000F80855E6
		//5E456FAC-D883-416A-B965-25C140C08AEF ITestObject (TestAnotherCOM)
		//0BBE2D86-D665-4DCC-B9DC-C24F631BDD0E , ITestCOMT4
			//init();
//



			ResourceBundle bundle = JISystem.getErrorMessages();
			IJIComObject unknown = stub.createInstance();
			IJIDispatch dispatch = (IJIDispatch)JIObjectFactory.narrowObject(unknown.queryInterface(IJIDispatch.IID));
			JIVariant variants = dispatch.callMethodA("GetDispatch");
			
//			dispatch.callMethodA("TestVariant1", new Object[]{variants[1]} );
			
//			JIStruct struct = new JIStruct();
//			struct.addMember(Character.class);
//			struct.addMember(Double.class);
//			struct.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR));
//			
//			Object[] t1 = dispatch.callMethodA("CreateArray", new Object[]{new JIVariant(10), new JIVariant(new JIArray(struct,null,1,true),true)} );
//			Object[] t1 = dispatch.callMethodA("GetFlavorsWithPrices", new Object[]{JIVariant.EMPTY_BYREF()} );
//			t1 = dispatch.callMethodA("GetFlavors", new Object[]{JIVariant.EMPTY_BYREF()} );

//			String sXmlEncode = "";
//            for (int i=0; i<10000;i++)
//                    sXmlEncode = sXmlEncode + "P";
//
//            JIVariant psXml = new JIVariant(new JIString(sXmlEncode));
//            JIVariant psError = new JIVariant(new JIString(""), true);
//            Object params[] = new Object[] {psXml, psError};

//            int id = dispatch.getIDsOfNames("testHresult2");
//            JIVariant[] rt = dispatch.callMethodA("testSafeArrayOfVariants", new Object[]{JIVariant.EMPTY()_BYREF});




			//JIVariant t1234 = dispatch.callMethodA("GetStooges");

		   // dispatch.callMethod("testArrayOfVariants",new Object[]{new JIArray(new JIVariant[]{new JIVariant(new JIArray(new JIString[]{new JIString("ab"),new JIString("cd")}))},true)});
			IJIComObject handle2 = (IJIComObject)unknown.queryInterface("620012E2-69E3-4DC0-B553-AE252524D2F6");
			//IJIComObject handle3 = (IJIComObject)unknown.queryInterface(IJITypeLib.IID);
			//JIArray arry34 = new JIArray(new JIVariant[]{new JIVariant(new JIString("40807810804000300798")),new JIVariant(new JIString("1"))},true);
			//JIVariant[] c2 = dispatch.callMethodA("Request", new Object[]{new JIString("rtrtr"),new JIVariant(new JIVariant(arry34)),JIVariant.EMPTY()_BYREF,JIVariant.EMPTY()_BYREF} );
			//Object[] t1 = dispatch.callMethodA("GetFlavorsWithPrices", new Object[]{JIVariant.EMPTY()_BYREF} );

			JICallBuilder callObject = new JICallBuilder(handle2.getIpid());
			callObject.setOpnum(156);
			callObject.addInParamAsPointer(new JIPointer(new JIArray(new JIVariant[]{}, true)), JIFlags.FLAG_NULL);
			//callObject.addInParamAsArray(new JIArray(new JIVariant[]{new JIVariant(new JIArray(new JIString[]{new JIString("ab"),new JIString("cd")}))},true), JIFlags.FLAG_NULL);
			Object[] r = handle2.call(callObject);


			Object[] t123 = dispatch.callMethodA("GetFlavorsWithPrices", new Object[]{JIVariant.EMPTY_BYREF()} );
			Object[] t12 = dispatch.callMethodA("GetFlavors", new Object[]{JIVariant.EMPTY_BYREF()} );
			//dispatch.callMethodA("testSAFEARRAY01", new Object[]{new JIVariant(new JIArray(new Integer[]{new Integer(1),new Integer(2),new Integer(4)},true), true)});
			//JIArray arry34 = new JIArray(new JIVariant[]{new JIVariant(new JIString("40807810804000300798")),new JIVariant(new JIString("1"))},true);
			//dispatch.callMethodA("Request", new Object[]{new Integer(8194),arry34,JIVariant.EMPTY()_BYREF,JIVariant.EMPTY()_BYREF} );
			Object[] ret0 = dispatch.callMethodA("LongArray", new Object[]{new JIVariant(new JIArray(new Integer[]{new Integer(1),new Integer(2),new Integer(4)},true), true)});
			JIArray ret01 = ((JIVariant)ret0[1]).getObjectAsArray();
			ret0 = dispatch.callMethodA("ReadAsicRegisterBlock", new Object[]{new JIString("Chonap"),new JIString("Cho"),new JIVariant(new JIArray(new JIUnsignedShort[]{
					(JIUnsignedShort)JIUnsigned.getUnsigned(new Integer(4000), JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT),(JIUnsignedShort)JIUnsigned.getUnsigned(new Integer(4001), JIFlags.FLAG_REPRESENTATION_UNSIGNED_SHORT)},true),true),new JIVariant(new JIArray(new JIUnsignedInteger[]{
							(JIUnsignedInteger)JIUnsigned.getUnsigned(new Long(9999), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT),(JIUnsignedInteger)JIUnsigned.getUnsigned(new Long(9999), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT)},true),true), Boolean.FALSE,Boolean.TRUE});
			ret01 = ((JIVariant)ret0[1]).getObjectAsArray();

			ret0 = dispatch.callMethodA("testSA1", new Object[]{new JIVariant(new JIArray(new Boolean[]{Boolean.FALSE,Boolean.TRUE},true),true),new JIVariant(new JIArray(new Float[]{new Float(123.4),new Float(123.4)},true),true),new JIVariant(new JIArray(new Double[]{new Double(123.4),new Double(123.4)},true),true)});
			ret01 = ((JIVariant)ret0[1]).getObjectAsArray();
			ret01 = ((JIVariant)ret0[2]).getObjectAsArray();
			ret01 = ((JIVariant)ret0[3]).getObjectAsArray();
			ret0 = dispatch.callMethodA("testSA3", new Object[]{new JIVariant(new JIArray(new JIVariant[]{new JIVariant(new JIVariant(dispatch,true)),new JIVariant(dispatch,true),new JIVariant(new JIString("Hello")),new JIVariant(123,true)},true),true)});
			ret01 = ((JIVariant)ret0[1]).getObjectAsArray();
			ret0 = dispatch.callMethodA("testSA2", new Object[]{new JIVariant(new JIArray(new Byte[]{new Byte((byte)1),new Byte((byte)1)},true),true),
					new JIVariant(new JIArray(new JIVariant[]{new JIVariant(dispatch,true)},true),true),new JIVariant(new JIArray(new JIVariant[]{new JIVariant(unknown,true)},true),true)});
			ret01 = ((JIVariant)ret0[1]).getObjectAsArray();
			ret01 = ((JIVariant)ret0[2]).getObjectAsArray();
			ret01 = ((JIVariant)ret0[3]).getObjectAsArray();

			JIVariant tr = dispatch.callMethodA("testHresult2");

			
			//IJIComObject handle2 = (IJIComObject)unknown.queryInterface("FA11DECE-7660-11D2-9C43-006008AD8BC06");

			//IJIComObject handle2 = (IJIComObject)unknown.queryInterface("A12E7F85-B011-4AB3-A924-215F67A725D5");

			dispatch.callMethod("testUnsignedInt", new Object[]{JIUnsigned.getUnsigned(new Short((short)-200), JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE)});

			JIStruct filetime = new JIStruct();
			filetime.addMember(Integer.class);
			filetime.addMember(Integer.class);

			JIStruct ONEVENTSTRUCT = new JIStruct();
			ONEVENTSTRUCT.addMember(Short.class);
			ONEVENTSTRUCT.addMember(Short.class);
			ONEVENTSTRUCT.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
			ONEVENTSTRUCT.addMember(filetime);
			ONEVENTSTRUCT.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
			ONEVENTSTRUCT.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
			ONEVENTSTRUCT.addMember(Short.class);
			ONEVENTSTRUCT.addMember(Short.class);
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(filetime);
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(Integer.class);
			ONEVENTSTRUCT.addMember(new JIPointer(JIVariant.class));
			ONEVENTSTRUCT.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));

			callObject = new JICallBuilder(handle2.getIpid());
//			callObject.setOpnum(3);
//			callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
//			callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
//			callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
//			callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
//			callObject.addOutParamAsObject(new JIArray(ONEVENTSTRUCT,null,1,true), JIFlags.FLAG_NULL);
//			handle2.call(callObject);



			//Long
			//Short
		//Integer
			callObject.reInit();
			callObject.setOpnum(147);
			callObject.addInParamAsUnsigned(JIUnsigned.getUnsigned(new Short((short)200), JIFlags.FLAG_REPRESENTATION_UNSIGNED_BYTE),JIFlags.FLAG_NULL);
			handle2.call(callObject);

			JIArray	 aIn	= new JIArray(new JIVariant[] {new JIVariant(new JIString("40807810804000300798")),new JIVariant(new JIString("1"))},true);
			   JIVariant varArray	= new JIVariant(aIn);

			callObject.setOpnum(3);

		    JIVariant vOpt = new JIVariant(8194);

//		    callObject.addInParamAsInt(8194,JIFlags.FLAG_NULL);
		    callObject.addInParamAsShort((short)8194,JIFlags.FLAG_NULL);
//		    callObject.addInParamAsVariant(vOpt,JIFlags.FLAG_NULL);
		    callObject.addInParamAsVariant(varArray,JIFlags.FLAG_NULL);
		    callObject.addInParamAsVariant(JIVariant.EMPTY_BYREF(),JIFlags.FLAG_NULL);
		    callObject.addInParamAsVariant(JIVariant.EMPTY_BYREF(),JIFlags.FLAG_NULL);
//		 callObject.addInParamAsVariant(vOut,JIFlags.FLAG_NULL);
//		 callObject.addInParamAsVariant(vExc,JIFlags.FLAG_NULL);

		    callObject.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		    callObject.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
		    callObject.addOutParamAsType(Integer.class,JIFlags.FLAG_NULL);

		    Object[] t = handle2.call(callObject);

			//since this is a byRef (check using the isByReflagSet())
			JIArray arrt = ((JIVariant)t[0]).getObjectAsVariant().getObjectAsArray();
			System.out.println(arrt);

			callObject.setOpnum(16);

			callObject.addInParamAsPointer(new JIPointer(new JIString("123",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)),JIFlags.FLAG_NULL);
	        callObject.addOutParamAsObject( new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),JIFlags.FLAG_NULL );
	        Object[] t2= handle2.call(callObject);

	        callObject.reInit();
			callObject.setOpnum(143);

			callObject.addInParamAsString("123",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
			callObject.addInParamAsBoolean(true ,JIFlags.FLAG_NULL );
	        callObject.addInParamAsInt ( 10,JIFlags.FLAG_NULL );
	        callObject.addInParamAsInt ( 20,JIFlags.FLAG_NULL );
	        callObject.addInParamAsInt(10,JIFlags.FLAG_NULL );
	        callObject.addInParamAsFloat(20,JIFlags.FLAG_NULL );

//	        callObject.addInParamAsPointer ( new JIPointer(new Integer(10)),JIFlags.FLAG_NULL );
//	        callObject.addInParamAsPointer ( new JIPointer(new Float(20.2)),JIFlags.FLAG_NULL );
	        callObject.addInParamAsInt ( 0x800,JIFlags.FLAG_NULL );
	        callObject.addOutParamAsObject ( new JIPointer(Integer.class,false),JIFlags.FLAG_NULL );
	        callObject.addOutParamAsObject ( new JIPointer(Integer.class,false),JIFlags.FLAG_NULL );
	        callObject.addInParamAsUUID ("620012E2-69E3-4DC0-B553-AE252524D2F6", JIFlags.FLAG_NULL );
	        callObject.addOutParamAsType (IJIComObject.class, JIFlags.FLAG_NULL );

	        t2= handle2.call(callObject);


//			JIVariant variantDate = new JIVariant(new Date(),true);
//			callObject.addInParamAsVariant(variantDate,JIFlags.FLAG_NULL);
//			callObject.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			callObject.setOpnum(2);
//			Object[] t = handle2.call(callObject);
//			Date date = ((JIVariant)t[0]).getObjectAsDate();
//
//			callObject = new JICallBuilder(handle2.getIpid());
//			callObject.addInParamAsVariant(JIVariant.EMPTY()_BYREF,JIFlags.FLAG_NULL);
//			callObject.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			callObject.setOpnum(1);
//			 t = handle2.call(callObject);
//			JIVariant ref = (JIVariant)t[0];
//			JIArray tr = ref.getObjectAsArray();


		//	Object[] t1 = dispatch.callMethodA("GetFlavorsWithPrices", new Object[]{JIVariant.EMPTY()_BYREF} );


			//JIArray arry34 = new JIArray(new JIString[]{new JIString("40807810804000300798"),new JIString("1")},true);
			//JIVariant[] c = dispatch.callMethodA("Request", new Object[]{new Integer(8194),new JIVariant(new JIVariant(arry34)),JIVariant.EMPTY()_BYREF,JIVariant.EMPTY()_BYREF} );
			//JIVariant[] c = dispatch.callMethodA("Request", new Object[]{new Integer(8194),arry34,JIVariant.EMPTY()_BYREF,JIVariant.EMPTY()_BYREF} );
			//JIArray arrtt = (c[2]).getObjectAsVariant().getObjectAsArray();
			//System.out.println(arrtt);

			IJIComObject handle = (IJIComObject)unknown.queryInterface("620012E2-69E3-4DC0-B553-AE252524D2F6");
			JICallBuilder callObject2 = new JICallBuilder(handle.getIpid());



			//JIVariant variantwe = new JIVariant();
			//JIVariant[] rett = dispatch.callMethodA("GetFlavors", new Object[]{JIVariant.EMPTY()});




			//handle.addRef();
			//handle.release();



//			dispatch.put("TestProperty1", new Object[]{new Short((short)1), new JIString("Hello")});
	//		dispatch.put("TestProperty2", new Object[]{new Short((short)1), new Short((short)2), new Integer(3)});

			JIVariant[] bhalue = dispatch.get("TestProperty1", new Object[]{new Short((short)1)});
			bhalue = dispatch.get("TestProperty2", new Object[]{new Short((short)1), new Short((short)2)});

			IJITypeInfo typeInfo = dispatch.getTypeInfo(0);
			FuncDesc funcDesc = typeInfo.getFuncDesc(0);
			Object[] re = typeInfo.getNames(funcDesc.memberId,100);
			Object[] arry = typeInfo.getDocumentation(funcDesc.memberId);
			JIString mops = typeInfo.getMops(funcDesc.memberId);
			//int[] ids = typeInfo.getIdOfNames(new String[]{"QueryInterface"});
			//IJIUnknown unknown2 = typeInfo.createInstance(JIObjectFactory.IID_IDispatch);
			int hrefType = typeInfo.getRefTypeOfImplType(0);
			IJITypeInfo info = typeInfo.getRefTypeInfo(hrefType);
			//int implTypeFlags = typeInfo.getImplTypeFlags(1);
			//VarDesc varDesc = typeInfo.getVarDesc(0);
			IJITypeLib typeLib = (IJITypeLib)((Object[])typeInfo.getContainingTypeLib())[0];
			int type = typeLib.getTypeInfoCount();
			typeLib.getLibAttr();
			typeLib.findName(new JIString("QueryInterface",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),0,(short)1);
			//Object[] ry = typeLib.getDocumentation(funcDesc.memberId);
			type = typeLib.getTypeInfoType(0);
			//IJITypeInfo type2 = typeLib.getTypeInfo(type);
			//typeInfo.getDllEntry(funcDesc.memberId, INVOKEKIND.INVOKE_FUNC.intValue());
			//typeInfo.getTypeAttr();


//			IJMeowWrapper handle = queryInterface("0BBE2D86-D665-4DCC-B9DC-C24F631BDD0E",false);
			//if (handle.isIDispatchSupported())
			{
//				//System.out.println(handle.getDispatch().GetTypeInfoCount());
//				dispatch = (IJIDispatch)JIObjectFactory.createCOMInstance(JIObjectFactory.IID_IDispatch,(IJIComObject)unknown.queryInterface(IJIDispatch.IID,false));
//				int i = dispatch.GetIDsOfNames("testSA");
//				//int i = dispatch.GetIDsOfNames("testAllVARIANTS");
//				//IJITypeInfo type = dispatch.GetTypeInfo(0);
//				//flags are going to be defined in IJIDispatch
//				JIVariant variant = new JIVariant(new Object[]{new JIString("Hi")});
//				dispatch.invoke(i,1,new Object[]{variant,null,null,null,null},new Object[]{JIVariant.class});
//				//JIVariant params = new JIVariant(new Object[]{new Integer(10),new JIString("123456")});
//				//dispatch.invoke(i,1,new Object[]{params,null,new Integer(2),new Integer(0)},new Object[]{JIVariant.class});
			}

			JICallBuilder obj = new JICallBuilder(handle.getIpid());
			Object[] in = null;
			Object[] out = null;
//			obj.setOpnum(13);//31);//30);//29);//32);
			Object[] result = null;
//
//
//			obj.reInit();
//			obj.setOpnum(2);
//			obj.addInParamAsVariant(new JIVariant(JIVariant.SCODE,0x80020004), JIFlags.FLAG_NULL);
//			//obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);
////
//			obj.reInit();
//			obj.setOpnum(2);
//			obj.addInParamAsVariant(new JIVariant(JIVariant.NULL), JIFlags.FLAG_NULL);
//			//obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(93);
//			obj.addInParamAsString("VikramShilpa",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR);
//			obj.addInParamAsString("ShilpaAkshat",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
//
//			//obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);
//
			//JIVariant variant = new JIVariant(new JIString("4567"));
//			JIVariant variant = new JIVariant(JIVariant.NULL);
//			JIVariant variant2 = new JIVariant(new Integer(10));
//			dispatch.callMethod("test3variants",new Object[]{variant,variant,variant,variant2});

//			obj.reInit();
//			obj.setOpnum(4);
//			JIVariant variant3 = new JIVariant(new Object[]{new JIVariant(new JIVariant(123.234)),Integer.valueOf(100)});
//			obj.addInParamAsVariant(variant3,JIFlags.FLAG_NULL);
//			//obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(9);
//			JIVariant variant =  new JIVariant(new JIString("4567"));
//			JIVariant variant2 =  new JIVariant(handle);
//			JIArray array = new JIArray(new JIVariant[]{variant,variant2});
//			obj.addInParamAsArray(array,JIFlags.FLAG_NULL);
//			//obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(4); //4
//			JIVariant variant =  new JIVariant(dispatch,true);
//			JIVariant variant2 =  new JIVariant(variant);
//			obj.addInParamAsVariant(variant,JIFlags.FLAG_NULL);
//			obj.addInParamAsInt(10,JIFlags.FLAG_NULL);
//			obj.addInParamAsVariant(variant2,JIFlags.FLAG_NULL);
//			//obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			result = handle.call(obj);
//
//			obj.reInit();
//			obj.setOpnum(98); //4
//			variant =  new JIVariant(dispatch,true);
//			variant2 =  new JIVariant(true);
//			obj.addInParamAsVariant(variant,JIFlags.FLAG_NULL);
//			obj.addInParamAsInt(10,JIFlags.FLAG_NULL);
//			obj.addInParamAsVariant(variant2,JIFlags.FLAG_NULL);
//			//obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(46); //4
//			JIVariant variant = new JIVariant(new JIVariant(handle,true));
//			JIVariant variant2 = new JIVariant(new JIVariant(new JIVariant(dispatch)));
//
//	//		variant = new JIVariant(JIVariant.EMPTY());
//	//		variant2 =  new JIVariant(JIVariant.EMPTY());
//
//			obj.addInParamAsVariant(variant,JIFlags.FLAG_NULL);
//			obj.addInParamAsShort((short)10,JIFlags.FLAG_NULL);
//			//obj.addInParamAsPointer(new JIPointer(Short.valueOf((short)10)),JIFlags.FLAG_NULL);
//			obj.addInParamAsVariant(variant2,JIFlags.FLAG_NULL);
//			obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			obj.addOutParamAsObject(new JIPointer(Short.class,true),JIFlags.FLAG_NULL);
//			obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(49);
			obj.addInParamAsPointer(new JIPointer(new JIPointer(new Integer(100))),JIFlags.FLAG_NULL);
			handle.call(obj);

			obj.reInit();
			obj.setOpnum(53);
			obj.addInParamAsPointer(new JIPointer((new Integer(100))),JIFlags.FLAG_NULL);
			handle.call(obj);


			obj.reInit();
			obj.setOpnum(134);
			obj.addInParamAsComObject(dispatch,JIFlags.FLAG_NULL);
			handle.call(obj);

			obj.reInit();
			obj.setOpnum(135);
			obj.addInParamAsComObject(dispatch,JIFlags.FLAG_NULL);
			obj.addOutParamAsType(IJIComObject.class,JIFlags.FLAG_NULL);
			handle.call(obj);

			obj.reInit();
			obj.setOpnum(136);
			obj.addInParamAsComObject(dispatch,JIFlags.FLAG_NULL);
			handle.call(obj);

			obj.reInit();
			obj.setOpnum(137);
			obj.addInParamAsString("Hello", JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
			obj.addOutParamAsObject(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR),JIFlags.FLAG_NULL);
			handle.call(obj);

			Object[] ret = dispatch.callMethodA("testBSTR01",new Object[]{new JIVariant(new JIString("Hello"),true)});

			obj.reInit();
			obj.setOpnum(138);
			obj.addInParamAsString("Hello", JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
			handle.call(obj);

//			 ret = dispatch.callMethodA("testBSTR02",new Object[]{new JIVariant(new JIString("Hello"),true)});

			obj.reInit();
			obj.setOpnum(139);
			obj.addInParamAsPointer(new JIPointer(new JIString("Hello")), JIFlags.FLAG_NULL);
			handle.call(obj);

	//		ret = dispatch.callMethodA("testBSTR03",new Object[]{new JIVariant(new JIString("Hello"),true)});

			//dispatch.callMethod("testIntPtr3D1",new Object[]{new JIVariant(100,true)});
			JIArray array = new JIArray(new Object[][]
			                             			 {{new Date()},{handle},{handle},{handle}}
			                             			);

			JIVariant[] variant3 = dispatch.callMethodA("testSA",new Object[]{new JIVariant(array,true)});
			JIArray array2 = variant3[1].getObjectAsArray();

			 Object[][] newValue = {
				        { new JIString("defe"), new Boolean(false), new Double(98765.0/12345.0)}
				        ,
				        { new Date(),        new Integer(5454),  new Float(22.0/7.0)        },
				        { new Boolean(true), new JIString("dffe"),             new Date()                 }
				      };
			//JIVariant variant2[] = dispatch.callMethodA(0x82,new Object[]{new JIVariant(new JIArray(new Integer[]{Integer.valueOf(100),Integer.valueOf(100),Integer.valueOf(200)}),true)});
			//variant2[1].getObjectAsArray();
			 JIVariant[] variant2 = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIArray(newValue),true)});
			 variant2 = dispatch.callMethodA("testSAFEARRAY01",new Object[]{new JIVariant(new JIArray(new Integer[]{new Integer(100),new Integer(100),new Integer(200)},true),true)});
			 variant2[1].getObjectAsArray();

			 dispatch.callMethod(3,new Object[]{new JIVariant(new JICurrency(-1,0),true)});
			 JIVariant[] variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIVariant(dispatch,true))});
			variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(JIVariant.SCODE,0,true)});
			variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(JIVariant.NULL())});
			variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(dispatch,true)});
			variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(true,true)});
			//JIVariant[] variant = dispatch.callMethodA("test3variants",new Object[]{new JIVariant(100,true),new JIVariant(400,true),new JIVariant(300,true),new Integer(200)});
			//JIVariant[] variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("Qweqrt2e"),true)});
			JIVariant[] variant11  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("Qwertweer"),true)});
			JIVariant[] variant111  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("2qe4twreggwfgwdfgwdgfssdgwegwertgwertwweQA"),true)});
			JIVariant[] variant222  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("Q4624twegewgA"),true)});
			JIVariant[] variant333  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("ABdfgfdgdgdgfdgfC"),true)});
			JIVariant[] variant444  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("ABdfggdgdgfdgfgfdfgdfgdgfdgfC"),true)});
			JIVariant[] variant555  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new JIString("ABCDEFGH"),true)});
			JIVariant[] variant4 = dispatch.callMethodA("testVariants678",new Object[]{new JIVariant(100),new JIVariant(true),new JIVariant(100,true)});
			variant  = dispatch.callMethodA("testSA",new Object[]{new JIVariant(100,true)});

			//this is failing as well...variant within a variant

			//....

			JIArray array3 = variant4[1].getObjectAsArray();

			variant = dispatch.callMethodA("testSA",new Object[]{new JIVariant(new Date(System.currentTimeMillis()),true)});
			variant2 = dispatch.callMethodA("testSAFEARRAY01",new Object[]{new JIVariant(new JIArray(new Integer[]{new Integer(100),new Integer(100),new Integer(200)}))});
			//variant2[1].getObjectAsArray();
			//dispatch.callMethod(0x82,new Object[]{new JIVariant(new Boolean[]{Boolean.TRUE})});
			//dispatch.callMethod(3,new Object[]{new JIVariant(new JIVariant(new JIVariant(handle)))});
			//dispatch.callMethod(3,new Object[]{new JIVariant(new Date(System.currentTimeMillis()))});
		//	dispatch.callMethod(3,new Object[]{new JIVariant(new JICurrency(10,0))});
			dispatch.callMethod(3,new Object[]{new JIVariant(new JICurrency(-1,0),true)});

			//dispatch.callMethod(3,new Object[]{new JIVariant(true,true)});

			//dispatch.callMethod(0x64,new Object[]{new JIVariant(dispatch),new JIVariant(true,true)});

			int jj = 0;
			//Just testing
			obj.reInit();
			obj.setOpnum(4);

			JIInterfaceDefinition interfaceDefinition = new JIInterfaceDefinition("620012E2-69E3-4DC0-B553-AE252524D2F6");
			JIJavaCoClass component = new JIJavaCoClass(interfaceDefinition,Test.class);
			JIParameterObject runtimeObject = new JIParameterObject();
			JIMethodDescriptor methodDescriptor = new JIMethodDescriptor("test",1,runtimeObject);
			interfaceDefinition.addMethodDescriptor(methodDescriptor);

			IJIComObject objMyCOM = JIObjectFactory.buildObject(session,component);
			obj.addInParamAsVariant(new JIVariant(objMyCOM),JIFlags.FLAG_NULL);
			obj.addOutParamAsType(JIVariant.class,JIFlags.FLAG_NULL);
			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(4);
//			obj.addInParamAsVariant(new JIVariant(handle),JIFlags.FLAG_NULL);
//			//obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(95);
//			obj.addInParamAsPointer(new JIPointer(new JIString("VikramShilpa",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL);
//			obj.addInParamAsPointer(new JIPointer(new JIString("AkshatShilpa",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)), JIFlags.FLAG_NULL);
//			//obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(3);
//			obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,dispatch,new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);
//
//			obj.reInit();
//			obj.setOpnum(3);
//			obj.addInParamAsVariant(new JIVariant(new JIString("123456789qwertyuiop")),JIFlags.FLAG_NULL);
//
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(94);
//			out = new Object[]{new Pointer(new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)),new Pointer(new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR))};
//			obj.setUpParams(new Object[]{new Pointer(null),new Pointer(null)}, out,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);



			//obj.setUpParams(new Object[]{new Float[]{new Float(50),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60),new Float(60)}}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			Float[] array = new Float[10];
//			array[0] = new Float(10.00);
//			array[1] = new Float(20.00);
//			Double[] array = new Double[10];
//			array[0] = new Double(10.3030303);
//			array[1] = new Double(20.3030303);
//			array[2] = new Double(30.3030303);
//			Short[] array = new Short[10];
//			array[0] = new Short((short)10);
//			array[1] = new Short((short)20);
//			array[2] = new Short((short)30);
//			Boolean[] array = new Boolean[10];
//			array[0] = Boolean.TRUE;
//			array[9] = Boolean.TRUE;
//			Integer[][] array = new Integer[2][2];
//			obj.setOpnum(4);
//			array[0][0] = new Integer(10);
//			array[0][1] = new Integer(20);

//			Double[][] array = new Double[3][5];
//			array[0][0] = new Double(10.3030303);
//			array[1][0] = new Double(20.3030303);
//			array[2][0] = new Double(30.3030303);
//			Float[][][] array = new Float[10][3][7];
//			array[0][0][6] = new Float(10);
//			array[1][0][6] = new Float(20);
//			array[2][0][4] = new Float(30);

	/*		Boolean array[][][][] = new Boolean[1][2][1][2];
			obj.setOpnum(7);
			array[0][1][0][0] = Boolean.TRUE;
			array[0][1][0][1] = Boolean.TRUE;
			array[0][0][0][1] = Boolean.TRUE;
			obj.setUpParams(new Object[]{new JIArray(array)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
////
//
			obj.reInit();
			obj.setOpnum(11);
			in = new Object[]{new JIArray(new Integer[10]),new JIArray(new Float[3]),new JIArray(new Double[5]),new JIArray(new Short[10][5])};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(9);
			in = new Object[]{new Integer(10),new JIVariant(new Short((short)10))};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{new JIString(null)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
//
			obj.reInit();
			obj.setOpnum(19);
			 in = new Object[]{new JIVariant(new Integer(5)),new JIVariant(new JIString("wfwre")),new JIVariant(new JIString("wfwre")), new Integer(10)};//new JIVariant(new JIString("Mangoes"))};
			 out = null;//new Object[]{new JIString(null)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(0)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(1)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(2)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(3)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(4)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(5)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(6)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(7)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(8)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(9)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(19);
			in = new Object[]{new JIVariant(null),new JIVariant(null),new JIVariant(null), new Integer(10)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(18);
			in = new Object[]{new JIVariant(null),new JIVariant(null)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
			int k = 0;

			obj.reInit();
			obj.setOpnum(23);
			obj.setUpParams(new Object[]{ Boolean.TRUE,new Integer(10)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(21);
			obj.setUpParams(new Object[]{Boolean.TRUE,new JIVariant(new JIString("12"))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(28);
			obj.setUpParams(new Object[]{new Integer("10"),new Double("10"),new JIVariant(null)}, new Object[]{JIVariant.class},JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(24);
			obj.setUpParams(new Object[]{new Integer(10),new JIVariant(new Double(10)),new JIVariant(new Integer("123"))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(22);
			obj.setUpParams(new Object[]{new Short((short)123),new JIVariant(new Integer("12"))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(20);
			obj.setUpParams(new Object[]{new JIVariant(new JIString("12")),new Integer(10)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(27);
			obj.setUpParams(new Object[]{new Integer(10),Boolean.TRUE,new JIVariant(new JIString("12")),new Integer(1000),Boolean.FALSE,new JIVariant(new Integer("12")),new JIVariant(new Double("12")),new JIVariant(Boolean.TRUE),new JIVariant(Boolean.FALSE)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(26);
			obj.setUpParams(new Object[]{new Integer(10),Boolean.TRUE,new JIVariant(null),new JIVariant(new Double("12")),new JIVariant(Boolean.TRUE)},new Object[]{JIVariant.class,JIVariant.class,JIVariant.class},JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);  //TODO screwed
			obj.setUpParams(new Object[]{new JIVariant(new JIString("12"))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(12);
			 in = new Object[]{handle,handle.getDispatch()}; //TODO screwed
			 out = new Object[]{MInterfacePointer.class,MInterfacePointer.class};
			obj.setUpParams(in, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(13);
			 in = new Object[]{new Integer(0),new Float(0),new Double(0),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),new Short((short)0)};
			 out = new Object[]{Integer.class,Float.class,Double.class,new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),Short.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
			int j = 0;

			obj.reInit();
			obj.setOpnum(14);
			 in = new Object[]{new JIString("qwe",JIFlags.FLAG_REPRESENTATION_STRING_BSTR)};
			 out = new Object[]{new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
////
//
			obj.reInit();
			obj.setOpnum(15);
			 in = new Object[]{new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)};
			 out = new Object[]{new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(16);
			 in = new Object[]{new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)};
			 out = new Object[]{new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//


			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Object[]{new Character('S'),new Integer(12),handle,handle.getDispatch(),new Double(12.23),new Float(101),new Float(101),new Double(12.23)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


//
//			//GenericObject obj = new GenericObject(handle);
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(handle)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(handle.getDispatch())}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new JIString("123456789qwertyuiop"))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
////
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Integer(100))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Float(100.07))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Double(100))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(Boolean.TRUE)}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Character('S'))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Short((short)100))}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Short[]{new Short((short)100),new Short((short)100)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Character[]{new Character('s'),new Character('s'),new Character('s'),new Character('s'),new Character('s')})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Boolean[]{Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//////
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Float[]{new Float(101),new Float(102),new Float(103),new Float(10),new Float(10),new Float(10),new Float(10),new Float(10),new Float(10),new Float(1032)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Double[]{new Double(10),new Double(10),new Double(10),new Double(10),new Double(10),new Double(10),new Double(103232),new Double(101123434)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new Integer[]{new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10),new Integer(10)})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
////
			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new JIString[]{new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF"),new JIString("ABCDEF")})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new JMeowWrapperImpl[]{(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle,(JMeowWrapperImpl)handle})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(3);
			obj.setUpParams(new Object[]{new JIVariant(new JIDispatchImpl[]{(JIDispatchImpl)handle.getDispatch(),(JIDispatchImpl)handle.getDispatch(),(JIDispatchImpl)handle.getDispatch(),(JIDispatchImpl)handle.getDispatch(),(JIDispatchImpl)handle.getDispatch(),(JIDispatchImpl)handle.getDispatch()})}, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//

			obj.reInit();
			obj.setOpnum(29);
			in = new Object[]{Boolean.TRUE,new Double(0),new JIVariant(new JIString("He"))};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(28);
			in = new Object[]{new Integer(0),new Double(0),new JIVariant(new JIString("He"))};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(28);
			in = new Object[]{new Integer(0),new Double(0),new JIVariant(null)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


			obj.reInit();
			obj.setOpnum(18);
			in = new Object[]{new JIVariant(null),new JIVariant(null)};//new JIVariant(new JIString("Mangoes"))};
			out = new Object[]{JIVariant.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(17);
			in = new Object[]{new JIArray(new Integer[][]{{new Integer(1),new Integer(2)},{new Integer(3),new Integer(4)}})};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(44);
			in = new Object[]{new JIArray(new Short[3][3])};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(43);
			in = new Object[]{new JIArray(new Integer[2][2][3])};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


			obj.reInit();
			obj.setOpnum(40);
			in = new Object[]{new JIArray(new Integer[]{new Integer(30),new Integer(40),new Integer(50)})};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(41);
			in = new Object[]{new JIArray(new Integer[][]{{new Integer(1),new Integer(1),new Integer(1)},{new Integer(1),new Integer(70),new Integer(1)},{new Integer(90),new Integer(100),new Integer(110)}})};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(46);
			in = new Object[]{new JIArray(new Integer[][]{{new Integer(1),new Integer(1),new Integer(1)},{new Integer(50),new Integer(1),new Integer(60)}})};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(47);
			in = new Object[]{new JIArray(new Integer[][]{{new Integer(1),new Integer(1),new Integer(1)},{new Integer(1),new Integer(1),new Integer(50)},{new Integer(1),new Integer(60),new Integer(1)},{new Integer(70),new Integer(1),new Integer(80)}})};
			out = null;//new Object[]{new JIArray(Integer.class,new int[]{2},1)};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(45);
			in = new Object[]{new JIVariant(null),new Short((short)10),new JIVariant(null)};
			out = new Object[]{JIVariant.class,Short.class,JIVariant.class};
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

	/*
			obj.reInit();
			obj.setOpnum(48);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(10)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(49);
			in = new Object[]{new JIArray(new Integer[4])};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(50);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(6),new Integer(1),new Integer(1),new Integer(9),new Integer(1),new Integer(1),new Integer(12)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(51);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(2),new Integer(3),new Integer(4),new Integer(5),new Integer(6),new Integer(7),new Integer(8),new Integer(9),new Integer(10),new Integer(11),new Integer(12),new Integer(13),new Integer(14),new Integer(15),new Integer(16)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(52);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(2)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(53);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(0),new Integer(10000),new Integer(10000)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(54);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(0),new Integer(0)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(55);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(56);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(57);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(58);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1),new Integer(1)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


			obj.reInit();
			obj.setOpnum(59);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1),new Integer(1)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(65);
			in = new Object[]{new JIArray(new Integer[]{new Integer(0),new Integer(0),new Integer(0),new Integer(0)})};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(66);
			in = new Object[]{new JIArray(new Double[]{new Double(10), new Double(20)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);


			obj.reInit();
			obj.setOpnum(67);
			in = new Object[]{new JIArray(new Double[]{new Double(10), new Double(20), new Double(20)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(65);
			in = new Object[]{new JIArray(new Double[]{new Double(1000)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(66);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(67);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123),new Double(1235765)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(68);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123),new Double(1235765),new Double(1235765)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(69);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123),new Double(1235765),new Double(1235765),new Double(1235765)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(70);
			in = new Object[]{new JIArray(new Double[]{new Double(1000)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(71);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(72);
			in = new Object[]{new JIArray(new Double[]{new Double(1000),new Double(123),new Double(123)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(73);
			in = new Object[]{new JIArray(new Integer[]{new Integer(1000)}, true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);
//
			obj.reInit();
			obj.setOpnum(74);
			in = new Object[]{new JIArray(new Integer[]{new Integer(10),new Integer(12)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(75);
			in = new Object[]{new JIArray(new Integer[]{new Integer(10),new Integer(12),new Integer(13)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(76);
			in = new Object[]{new JIArray(new Integer[]{new Integer(10),new Integer(12),new Integer(12),new Integer(12)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(77);
			in = new Object[]{new JIArray(new Short[]{new Short((short)10)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(78);
			in = new Object[]{new JIArray(new Short[]{new Short((short)101),new Short((short)10)}, true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(79);
			in = new Object[]{new JIArray(new Short[]{new Short((short)110),new Short((short)10),new Short((short)10)}, true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(80);
			in = new Object[]{new JIArray(new Short[]{new Short((short)110),new Short((short)10),new Short((short)120),new Short((short)10)},true)};
			out = null;
			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
			result = handle.call(obj);



//			obj.reInit();
//			obj.setOpnum(11);
//			in = new Object[]{new JIArray(new Integer[10]),new JIArray(new Float[3]),new JIArray(new Double[5]),new JIArray(new Short[10][5])};
//			//out = new Object[]{new JIArray(Integer.class,new int[]{2},1)};
//			obj.setUpParams(in, null,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

			*/
//			obj.reInit();
//			obj.setOpnum(91);
//			obj.setUpParams(new Object[]{new JIString("Hello",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),Boolean.TRUE,new JIString("Hi",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR), new Short((short)100),new JIString("HPri",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(91);
//			obj.setUpParams(new Object[]{new JIString("Hello121",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),Boolean.TRUE,new JIString("Hi1",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR), new Short((short)100),new JIString("HPri1",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR)}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);


//			obj.reInit();
//			obj.setOpnum(92);
//			obj.setUpParams(new Object[]{new JIString("Hello",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),Boolean.TRUE,new JIString("QWERTY",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), new Short((short)100),new JIString("123WE",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);


//			obj.reInit();
//			obj.setOpnum(92);
//			obj.setUpParams(new Object[]{new JIString("Hello121",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),Boolean.TRUE,new JIString("QWERTY1",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), new Short((short)100),new JIString("123WE1",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);



//			obj.reInit();
//			obj.setOpnum(13);
//			in = new Object[]{new Integer(0),new Float(0),new Double(0),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR),new Short((short)0)};
//			out = new Object[]{Integer.class,Float.class,Double.class,new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR | JIFlags.FLAG_REPRESENTATION_POINTER),new JIString(null,JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR | JIFlags.FLAG_REPRESENTATION_POINTER),Short.class};
//			obj.setUpParams(in, out,JIFlags.FLAG_NULL,JIFlags.FLAG_NULL);
//			result = handle.call(obj);
//			int j = 0;

//			obj.reInit();
//			obj.setOpnum(93);
//			obj.setUpParams(new Object[]{new JIString("Hello121",JIFlags.FLAG_REPRESENTATION_STRING_LPCTSTR),new JIString("1",JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
//			result = handle.call(obj);



			/*

			IJMeowWrapper handle = queryInterface("5DD52389-B1A4-4fe7-B131-0F8EF73DD175",false);

			GenericObject obj = new GenericObject();
			obj.setOpnum(0);
			//obj.setUpParams(new Object[]{new Integer(35),new Integer(35)}, new Object[]{Integer.class});
			obj.setUpParams(new Object[]{new JIString("testname)")}, null,JIFlags.FLAG_REPRESENTATION_STRING_BSTR,JIFlags.FLAG_NULL);
			Object[] result = handle.call(obj);

//			obj.reInit();
//			obj.setOpnum(1);
//			obj.setUpParams(new Object[]{new Integer(90)},null);
//			handle.call(obj);


			//IStats of CAR : FE78387F-D150-4089-832C-BBF02402C872
			handle = queryInterface("FE78387F-D150-4089-832C-BBF02402C872",false);
			obj.reInit();

			obj.setOpnum(1);
			obj.setUpParams(null,new Object[]{JIString.class},JIFlags.FLAG_NULL,JIFlags.FLAG_REPRESENTATION_STRING_BSTR);
			handle.call(obj);

		/*	obj.reInit();
			obj.setOpnum(1);
			obj.setUpParams(null,new Object[]{JIString.class});
			result = handle.call(obj);

			//IEngine E27972D8-717F-4516-A82D-B688DC70170C
			handle = queryInterface("E27972D8-717F-4516-A82D-B688DC70170C",false);
			obj.reInit();

			obj.setOpnum(0); //speedup
			handle.call(obj);

			obj.setOpnum(0); //speedup
			handle.call(obj);

			obj.setOpnum(0); //speedup
			handle.call(obj);


			obj.reInit();
			obj.setOpnum(1);
			obj.setUpParams(null,new Object[]{Integer.class});
			result = handle.call(obj);

			obj.reInit();
			obj.setOpnum(2);
			obj.setUpParams(null,new Object[]{Integer.class});
			result = handle.call(obj);

			//System.out.println(result[0]);
//			handle.call(obj);
//			handle.call(obj);
//			handle.call(obj);
//
 			*/
			int i = 0;
			i++;


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				JISession.destroySession(session);
			} catch (JIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}


