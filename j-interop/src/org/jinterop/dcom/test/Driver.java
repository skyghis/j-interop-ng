package org.jinterop.dcom.test;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.rmi.server.UID;
import java.util.ArrayList;

import jcifs.util.Encdec;

import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.IJIUnsigned;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIUnsignedFactory;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import rpc.core.UUID;

//import com.iwombat.foundation.ObjectId;

class Driver implements iota {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  try {
			 
			  String n = "variant[index]sss".replaceFirst("index", Integer.toString(100));
			  Short xxxs = new Short((short)1);
			  Class ccccccc = JIVariant[][].class;
			  System.out.println(ccccccc.getName());
			  ccccccc = Integer[].class;
			  System.out.println(ccccccc.getName());
			  ccccccc = Short[][].class;
			  System.out.println(ccccccc.getName());
				 if (ccccccc.isArray())
				 {
					 String name2 = ccccccc.getName();
					 int i = name2.lastIndexOf("L");
					 System.out.println(name2.substring(i+1,name2.length() - 1));
					// System.out.println(ccccccc.getSimpleName());
				 }
				 
			  Object oi = new float[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new int[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new double[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new char[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new boolean[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new byte[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new short[10][10];
			  System.out.println(oi.getClass().getComponentType().getName());
			  oi = new long[][]{{1,2},{3,4,5,6,7},{8,9,10}};
			  System.out.println(oi.getClass().getName() + " , " + oi.getClass().getComponentType().getName());
			  
			  boolean isPrimitive = false;
			  Class d = oi.getClass().getComponentType();
			  while (d != null)
			  {
				  Class dd = d.getComponentType();
				  if (dd == null)
				  {
					  isPrimitive = d.isPrimitive();
				  }
				  d = dd;
			  }
			  //extract the class name
			  String clazzName = null;
			  if (isPrimitive)
			  {
				  clazzName = oi.getClass().getName();
				  if (clazzName.endsWith("F"))
				  {
					  clazzName = Float.class.getName();
				  }
				  else
				  if (clazzName.endsWith("I"))
				  {
					  clazzName = Integer.class.getName();
				  }	  
				  else
				  if (clazzName.endsWith("D"))
				  {
					  clazzName = Double.class.getName();
				  }
				  else
				  if (clazzName.endsWith("C"))
				  {
					  clazzName = Character.class.getName();
				  }
				  else
				  if (clazzName.endsWith("Z"))
				  {
					  clazzName = Boolean.class.getName();
				  }
				  else
				  if (clazzName.endsWith("B"))
				  {
					  clazzName = Byte.class.getName();
				  }
				  else
				  if (clazzName.endsWith("S"))
				  {
					  clazzName = Short.class.getName();
				  }
				  else
				  if (clazzName.endsWith("J"))
				  {
					  clazzName = Long.class.getName();
				  }
			  }
			  else
			  {
				  
			  }
			  
			  JIArray arrrry = new JIArray(new Long[][]{{new Long(1),new Long(2)},{new Long(3), new Long(4), new Long(5)},{new Long(3), new Long(4), new Long(5)}});
			  ArrayList upperBounds2 = new ArrayList();
				String name = oi.getClass().getName();
				Object subArray = oi;
				int dimension = 0;
				while (name.startsWith("["))
				{
					name = name.substring(1);
					int x = Array.getLength(subArray);
					upperBounds2.add(new Integer(x));
					if (x == 0) //In which ever index the length is 0 , the array stops there, example Byte[0],Byte[0][10],Byte[10][0]
					{
						break;
					}
					subArray = Array.get(subArray,0);
					dimension++;
				}
				
				int[] upperBounds = new int[upperBounds2.size()];
				for (int i = 0;i < upperBounds2.size(); i++)
				{
					upperBounds[i] = ((Integer)upperBounds2.get(i)).intValue(); 
				}
				
//			 Object newArray = createArray(oi, Class.forName(clazzName), dimension);
				Object newArray = createArray(new Long[][]{{new Long(1),new Long(2)},{new Long(3), new Long(4), new Long(5)},{new Long(3), new Long(4), new Long(5)}}, long.class, dimension);
				
			  int[][] g1 = new int[10][10];
			  Integer[][] g = new Integer[10][10];
			  
			 Array.set(g, 0, Array.get(g1, 0)); 
			 
			 
			 Class cx = ccccccc.getComponentType();
			  Object rrr = new Integer(0);
			 // System.out.println(rrr.getClass().getSimpleName());
			  System.out.println(rrr.getClass());
			  //Object hhhhh = Integer.class.cast(rrr);
			  IJIUnsigned un = JIUnsignedFactory.getUnsigned(new Long(100), JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
			  System.out.println(un.getClass());
			  Class xx = void.class;
			  Integer y = new Integer(123);
			  Class xxxx = y.getClass();
			  System.out.println(xxxx.isPrimitive());
			  Constructor cc = JIVariant.class.getConstructor(new Class[]{int.class, boolean.class});
			  JIVariant vv = (JIVariant)cc.newInstance(new Object[]{y, Boolean.valueOf(true)});
			  Object o007 = new Integer(1); 
			  Driver.cc(o007);
			  if (int.class == Integer.class)
			  {
				  System.out.println("same");
			  }
			  Object o123 = new int[][]{{1,2},{2,3}};
			  Object o1234 = o123;
			  Class c = o1234.getClass();
			  Object o234 = Array.get(o123,0);
			   c = o234.getClass();
			  System.out.println(o123.getClass());
			  
			  String[] str = "123.9".split("\\.");
			  int a = ~(Integer.MIN_VALUE);
			  System.out.println(a);
			  a = a + 1;
			  System.out.println(a);
			  if (a == Integer.MAX_VALUE)
			  {
				  
			  }
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
			  double gss = 100020%10000;
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

	static Object createArray(Object srcArray,Class targetArrayType,int dimension)
	{
		Object array = null;
		Class c = targetArrayType;
		int len = Array.getLength(srcArray);
		for (int j = 0; j < dimension; j++ )
		{
			array = Array.newInstance(c, len);
			c = array.getClass();
		}

		for (int i = 0; i < len ; i++)
		{
			if(dimension == 1)
			{
				//fill value here
				if (i == Array.getLength(srcArray))
				{
					//this means this array has less data than its upper bounds which is the max value.
					//resize it.
					Object array2 = Array.newInstance(targetArrayType, i);
					System.arraycopy(array, 0, array2, 0, i);
					array = array2;
					break;
				}
				Array.set(array,i,Array.get(srcArray, i));
			}
			else
			{
				Array.set(array,i,createArray(Array.get(srcArray, i),targetArrayType,dimension - 1));
			}

		}

		return array;
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

	public static void cc(Integer i)
	{
		System.out.println(i + " , " + i.getClass());
	}

	public static void cc(Object i)
	{
		System.out.println(i + " () , " + i.getClass());
	}

public void v(){}



}

interface iota
{
	public void v();
}
