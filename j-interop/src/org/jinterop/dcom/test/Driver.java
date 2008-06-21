package org.jinterop.dcom.test;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.rmi.server.UID;

import jcifs.util.Encdec;

import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.win32.IJIDispatch;

import rpc.core.UUID;

//import com.iwombat.foundation.ObjectId;

class Driver implements iota {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  try {
			  Object o = Array.newInstance(IJIDispatch.class,10);
			  
			  iota[] s = new Driver[100];
			  System.out.println(s.getClass().getComponentType());
			  JISystem.getErrorMessages();
			  Byte[][] bgh = new Byte[10][0];
			  long jj = 2147483670L;
			  short b = (short)40000;
			  byte b1123 = (byte)b;
			  String msd = null + "";
			  UID uid = new UID();
			  System.out.println(uid);
//			  System.out.println(new ObjectId().toHexString());
//			  String str = new ObjectId().toHexString();
//			  System.out.println(str);
//			  str = str.substring(0,8) + "-" + str.substring(8,12) + "-" + str.substring(12,16) + "-" + str.substring(16,20) + "-" + str.substring(20)   ;
//			  System.out.println(str);
			  Object[][] obj = new Object[6][2];
			  System.out.println(obj.length);
			  int lowbyte = -12345678;
			  //lowbyte = (int)(lowbyte - lowbyte%10000);
			  double lowbyte1 = lowbyte%10000.0;
			  UUID uuid = new UUID();
			  System.out.println(uuid.toString());
			  int toSend = 0xFFFFFFFF;
			  System.out.println(Integer.toHexString(toSend));
			  double g = 100020%10000;
			  String toSend2 =(Integer.toHexString(toSend));
			  byte[] hibuffer = new byte[]{0,0,0,0,0,0,0,0};
			  byte[] lowbuffer = new byte[]{0,0,0,0,0,0,0,0};
			  String lo = "";
			  if (toSend2.length() > 8)
			  {
				  System.arraycopy(toSend2.substring(8).getBytes(),0,lowbuffer,0,8);
				  System.arraycopy(toSend2.substring(0,8).getBytes(),0,hibuffer,0,8);
			  }
			  else
			  {
				  System.arraycopy(toSend2.getBytes(),0,lowbuffer,0,8);
			  }

//			  double d = -125;
//			  Double.
//			  d = 0xf;
//			  long va = d & 0xF;

			  byte[] buffer = new byte[1148];//1144
				FileInputStream inputStream;
				try {
					inputStream = new FileInputStream("c:/temp/webbrowserevent3"); //change the 32nd member to 106 byte value , in inspect and change
					inputStream.read(buffer,0,1148);//1144

//					FileOutputStream outputStream = new FileOutputStream("c:/temp/webbrowserevent3");
//					outputStream.write(buffer,0,544);
//					outputStream.write(buffer,548,1148 - 548);
//					outputStream.flush();
//					outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



//			  	Integer iarray[][] = new Integer[5][6];
//			  	System.out.println(((Object[])iarray).length);
//			  	Object r = Array.get(iarray,0);
//			  	Short s = new Short((short)Float.NaN);
//			  	Float[] flt = new Float[]{new Float(10)};
//			  	Class c = flt.getClass();
//			  	if (c.equals(Float[].class))
//			  		System.out.println(true);
//
//
//			  	Float f[][][] = new Float[][][]{new Float[][]{{new Float(1),new Float(2)},{new Float(3),new Float(4)},{new Float(13),new Float(14)},{new Float(113),new Float(114)}},new Float[][]{{new Float(1),new Float(2)},{new Float(1234),new Float(123)},{new Float(999),new Float(555)},{new Float(345),new Float(123)}}};
//				computeLengthArray(f);
			  	Integer yx[] = new Integer[]{new Integer(19),new Integer(20),new Integer(22),new Integer(23)};
				computeLengthArray(yx);
				Double yx2[][] = new Double[][]{new Double[]{}, new Double[]{new Double(123.3),new Double(123.4),new Double(123.5)}, new Double[]{}};
				computeLengthArray(yx2);
//				serializeArray(yx);
//
			  	Object array = deSerializeArray(Float.class,new int[]{10,20},2);
//			  	//Object o3 = Array.get(f,0);
//
//			  	//Object[] o = f[0];
//			  	//System.out.println(o[0]);
//			  	c = new Float[10][10].getClass();
//			  	System.out.println(c.getCanonicalName());
//			  	System.out.println(c);
//			  	System.out.println("Starting...");
//			  	byte b1[] = new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)-1,(byte)128};
//			  	double val = Encdec.dec_doublele(b1, 0);
//			    byte[] b = new byte[100];
//			    int i = 0;
//			    while (i < 100)b[i++] = -1;
//			  	Encdec.enc_doublele(Float.NaN,b,0);
			    //Encdec.enc_uint32le(268435456,b,0);
//			  InetAddress address = InetAddress.getLocalHost();
//			  byte[] array = address.getAddress();
			  	byte b1[] = new byte[10];//{(byte)0x9c,(byte)0x3f,(byte)0x16,(byte)0};
			  	int val = Encdec.dec_uint32le(b1,0);
			  	Encdec.enc_doublele(10.0,b1,0);
//				FirstContact_Stub test = (FirstContact_Stub)
//				  StubFactory.newInstance().createStub(
//						  FirstContact.class);
//				test.setAddress("ncacn_ip_tcp:10.74.2.90[135]");
			  	String strKey = Integer.toString(12345678);
			  	char buffer1[] = {'0','x','0','0','0','0','0','0','0','0'};
			  	System.arraycopy(strKey.toCharArray(),0,buffer1,buffer1.length - strKey.length(),strKey.length());
			  	strKey = String.valueOf(buffer1);
			  	//FirstContact_Stub test = new FirstContact_Stub("ncacn_ip_tcp:10.74.85.56[135]");
			  	//FirstContact_Stub test = new FirstContact_Stub("ncacn_ip_tcp:10.24.10.14[135]");
			  	//FirstContact_Stub test = new FirstContact_Stub("ncacn_ip_tcp:127.0.0.1[135]");
			  	//FirstContact_Stub test = new FirstContact_Stub("itl-hw-38602a");
			  	//FirstContact_Stub test = new FirstContact_Stub("20.0.0.1");
			  	JISystem.setInBuiltLogHandler(false);
			  	FirstContact_Stub test = new FirstContact_Stub("estroopchandxp");
			  	//FirstContact_Stub test = new FirstContact_Stub("ncacn_ip_tcp:10.74.2.87[135]");
				//test.setAddress("ncacn_ip_tcp:127.0.0.1[135]");
				//test.setObject("4d9f4ab8-7d1c-11cf-861e-0020af6e7c57");
				//test.setObject(UUID.NIL_UUID);
				test.obtainReference();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}



	static void serializeArray(Object array)
	{
		String name = array.getClass().getName();
		Object o[] = (Object[])array;
		for (int i = 0;i < o.length; i++)
		{
			if (name.charAt(1) != '[')
			{
				Object o1[] = (Object[])array;
				for (int j = 0;j < o1.length; j++)
				{
					System.out.println(o1[j]);
				}
				return;
			}
			serializeArray(Array.get(array,i));
		}

	}

	static int computeLengthArray(Object array)
	{
		int length = 0;
		String name = array.getClass().getName();
		Object o[] = (Object[])array;
		for (int i = 0;i < o.length; i++)
		{
			if (name.charAt(1) != '[')
			{
				Object o1[] = (Object[])array;
				System.out.println(o1.getClass().getComponentType());
				return length;
			}
			length = length + computeLengthArray(Array.get(array,i));
		}

		return length;
	}

	static Object deSerializeArray(Class arrayType,int[] upperBounds,int dimension)
	{
		Object array = null;
		Class c = arrayType;
		for (int j = 0; j < dimension; j++ )
		{
			array = Array.newInstance(c, upperBounds[upperBounds.length - j - 1]);
			c = array.getClass();
		}

		for (int i = 0; i < upperBounds[upperBounds.length - dimension] ; i++)
		{
			if(dimension == 1)
			{
				//fill value here
				Array.set(array,i,new Float(i));
			}
			else
			{
				Array.set(array,i,deSerializeArray(arrayType,upperBounds,dimension - 1));
			}

		}

		return array;
	}


public void v(){}



}

interface iota
{
	public void v();
}
