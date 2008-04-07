/**j-Interop (Pure Java implementation of DCOM protocol) 
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional, 
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY; 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jinterop.winreg;



import jcifs.util.Encdec;
import ndr.NdrObject;
import ndr.NetworkDataRepresentation;

import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JIRuntimeException;

/** Interface for performing C-R-U-D on the Windows registry in a platform independent way without using JNI. 
 * This interface used Windows Remote Registry service and should be running on target workstation. The SERVER service 
 * should also be running. <br>
 * 
 * @since 1.0
 *
 */
public interface IJIWinReg {

	public static final int KEY_ALL_ACCESS = 0x000f003f;
	public static final int KEY_CREATE_LINK = 0x00000020;
	public static final int KEY_CREATE_SUB_KEY = 0x00000004;
	public static final int KEY_ENUMERATE_SUB_KEYS = 0x00000008;
	public static final int KEY_EXECUTE = 0x00020019;
	public static final int KEY_NOTIFY = 0x00000010;
	public static final int KEY_QUERY_VALUE = 0x00000001;
	public static final int KEY_READ = 0x00020019;
	public static final int KEY_SET_VALUE = 0x00000002;
	public static final int KEY_WRITE = 0x00020006;
	
	/**
	 * Type specifying String
	 */
	public static final int REG_SZ = 1;
	/**
	 * Type specifying Binary
	 */
	public static final int REG_BINARY = 3;
	/**
	 * Type specifying DWORD
	 */
	public static final int REG_DWORD = 4;
	/**
	 * Type specifying environment string
	 */
	public static final int REG_EXPAND_SZ = 2;
	/**
	 * Type specifying mutliple strings (array)
	 */
	public static final int REG_MULTI_SZ = 7;
	/**
	 * Type specifying empty type
	 */
	public static final int REG_NONE = 0;
	
	public static final int REG_OPTION_NON_VOLATILE = 0;
	public static final int REG_OPTION_VOLATILE = 1;
	
	class closeKey extends NdrObject
	{
		public JIPolicyHandle key = null; 
		public int getOpnum() {
			return 5;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			ndr.writeOctetArray(key.handle,0,20);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class openHKLM extends NdrObject
	{
		public int getOpnum() {
			return 2;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//it's a pointer
			
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			
			//system name
			ndr.writeUnsignedShort(40736);
			
			//length
			ndr.writeUnsignedShort(1);
			
			ndr.writeUnsignedLong(0x2000000);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class openHKCU extends NdrObject
	{
		public int getOpnum() {
			return 1;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//it's a pointer
			
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			
			//system name
			ndr.writeUnsignedShort(49736);
			
			//length
			ndr.writeUnsignedShort(1);
			
			ndr.writeUnsignedLong(0x2000000);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class openHKU extends NdrObject
	{
		public int getOpnum() {
			return 4;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//it's a pointer
			
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			
			//system name
			ndr.writeUnsignedShort(49736);
			
			//length
			ndr.writeUnsignedShort(1);
			
			ndr.writeUnsignedLong(0x2000000);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class openHKCR extends NdrObject
	{
		public int getOpnum() {
			return 0;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//it's a pointer
			
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			
			//system name
			ndr.writeUnsignedShort(49736);
			
			//length
			ndr.writeUnsignedShort(1);
			
			ndr.writeUnsignedLong(0x2000000);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	
	class deleteValueOrKey extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String valueName = null;
		public boolean isKey = false;
		public int getOpnum() {
			if (isKey)
			{
				return 7;
			}
			else
			{
				return 8;
			}
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((valueName.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((valueName.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(valueName.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(valueName.length() + 1);
			
			int i = 0;
			while (i < valueName.length())
			{
				ndr.writeUnsignedShort(valueName.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		
	}
	
	class saveFile extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String fileName = null;
		public int getOpnum() {
			return 20;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((fileName.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((fileName.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(fileName.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(fileName.length() + 1);
			
			int i = 0;
			while (i < fileName.length())
			{
				ndr.writeUnsignedShort(fileName.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			//now align for int
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			long k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			ndr.writeUnsignedLong(0);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		
	}
	
	class createKey extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String key = null;
		public int accessMask = -1;
		public int options = -1;
		public int actiontaken = -1;
		public int getOpnum() {
			return 6;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(key.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(key.length() + 1);
			
			int i = 0;
			while (i < key.length())
			{
				ndr.writeUnsignedShort(key.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			
			//now align for int
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			long k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			//write the class
			String clazz = "REG_SZ";
			//clazz len , since it is uint16
			ndr.writeUnsignedShort((clazz.length() + 1) * 2);
			//clazz size, since it is uint16
			ndr.writeUnsignedShort((clazz.length() + 1) * 2);
			
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(clazz.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(clazz.length() + 1);
			
			i = 0;
			while (i < clazz.length())
			{
				ndr.writeUnsignedShort(clazz.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			
			//now align for int
			index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			k = 0;
			k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			//options
			ndr.writeUnsignedLong(options);
			
			ndr.writeUnsignedLong(accessMask);
			
			//ptr to sec desc , null
			ndr.writeUnsignedLong(0);
			//pointer to action taken
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			//pointer to action taken
			ndr.readUnsignedLong();
			actiontaken = ndr.readUnsignedLong();
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class setValue extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String valueName = null; 
		public int clazzType = -1;
		public int lengthInBytes = -1;
		public byte[] data = null; //should be in the right encoding for Strings.
		public byte[][] data2 = null; //reg_
		public int dword;
		public int getOpnum() {
			return 22;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((valueName.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((valueName.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(valueName.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(valueName.length() + 1);
			
			int i = 0;
			while (i < valueName.length())
			{
				ndr.writeUnsignedShort(valueName.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			
			//now align for int
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			long k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			//write the type.
			ndr.writeUnsignedLong(clazzType);
			
			i = 0;
			if (lengthInBytes != 0)
			{
				switch(clazzType)
				{
					case REG_EXPAND_SZ: //for environment variable strings
					case REG_SZ: //for strings, strings are null terminated, length in bytes will NOT include the null termination
						//character
						//writing the max count
						ndr.writeUnsignedLong((lengthInBytes + 1) * 2);		
						
						while (i < data.length)
						{
							ndr.writeUnsignedShort(data[i]);
							i++;
						}
						
						//null termination
						ndr.writeUnsignedShort(0);
						
						//now align for int
						index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
						k = 0;
						k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
						ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
						
						ndr.writeUnsignedLong((lengthInBytes + 1) * 2);	
						
					break;
					case REG_DWORD:
						ndr.writeUnsignedLong(lengthInBytes);
						ndr.writeUnsignedLong(dword);
						ndr.writeUnsignedLong(lengthInBytes);
					break;	
					case REG_NONE:
						data = new byte[0];
						lengthInBytes = 0;
					case REG_BINARY:
						ndr.writeUnsignedLong(lengthInBytes);
						ndr.writeOctetArray(data,0,lengthInBytes);
						index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
						k = 0;
						k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
						ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
						ndr.writeUnsignedLong(lengthInBytes);
					break;
					case REG_MULTI_SZ: //for strings, strings are null terminated, length in bytes will NOT include the null termination
						//character
						//writing the max count , this will be computed before hand
						ndr.writeUnsignedLong(lengthInBytes);		
						
						for (i = 0; i < data2.length;i++)
						{
							for (int j = 0; j < data2[i].length;j++)
							{
								ndr.writeUnsignedShort(data2[i][j]);
							}
							//null termination for each string
							ndr.writeUnsignedShort(0);
						}
						//null termination for the multi sz.
						ndr.writeUnsignedShort(0);
						
						//now align for int
						index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
						k = 0;
						k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
						ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
						
						ndr.writeUnsignedLong(lengthInBytes);
						
					break;
						
					
					default:
						throw new JIRuntimeException(JIErrorCodes.JI_WINREG_EXCEPTION4);
				}
			}
			else
			{
				//for data
				ndr.writeUnsignedLong(0);
				//for length
				ndr.writeUnsignedLong(0);
			}
			
			
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		
	}
	
	class enumKey extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public int index = -1;
		public String[] retval = new String[2];
		public int getOpnum() {
			return 9;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			ndr.writeUnsignedLong(index);
			
			//buffer len , since it is uint16
			ndr.writeUnsignedShort(0);
			//buffer size, since it is uint16
			ndr.writeUnsignedShort(2048);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(1024);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(0);
			
			//pointer
			ndr.writeUnsignedLong(new Object().hashCode());
			//buffer len , since it is uint16
			ndr.writeUnsignedShort(0);
			//buffer size, since it is uint16
			ndr.writeUnsignedShort(2048);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(1024);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(0);
			
			//pointer for time
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
			ndr.writeUnsignedLong(0);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			//buffer len , since it is uint16
			ndr.readUnsignedShort();
			//buffer size, since it is uint16
			ndr.readUnsignedShort();
			
			//it's a pointer
			//referent
			ndr.readUnsignedLong();
			//max count
			ndr.readUnsignedLong();
			//offset
			ndr.readUnsignedLong();
			
			int actuallength = ndr.readUnsignedLong();//actuallength
			byte[] bytes = new byte[0];
			if (actuallength != 0)
			{
				bytes = new byte[actuallength - 1];
			}
			int i = 0;
			//last 2 bytes , null termination will be eaten outside the loop
			while (i < actuallength - 1)
			{
				int retVal = ndr.readUnsignedShort();
				bytes[i] = (byte)retVal;
				i++;
			}
			if (actuallength != 0)
			{
				ndr.readUnsignedShort();
			}
			
			retval[0] = new String(bytes);
			
			long l = (l=Math.round(ndr.getBuffer().getIndex()%4.0)) == 0 ? 0 : 4 - l ;
			ndr.readOctetArray(new byte[(int)l],0,(int)l);		
			
//			it's a pointer
			//referent
			ndr.readUnsignedLong();
			
//			buffer len , since it is uint16
			ndr.readUnsignedShort();
			//buffer size, since it is uint16
			ndr.readUnsignedShort();
			
			//it's a pointer
			//referent
			ndr.readUnsignedLong();
			//max count
			ndr.readUnsignedLong();
			//offset
			ndr.readUnsignedLong();
			
			actuallength = ndr.readUnsignedLong();//actuallength
			bytes = new byte[0];
			if (actuallength != 0)
			{
				bytes = new byte[actuallength - 1];
			}
			i = 0;
			//last 2 bytes , null termination will be eaten outside the loop
			while (i < actuallength - 1)
			{
				int retVal = ndr.readUnsignedShort();
				bytes[i] = (byte)retVal;
				i++;
			}
			if (actuallength != 0)
			{
				ndr.readUnsignedShort();
			}
			
			retval[1] = new String(bytes);
			
			l = 0;
			l = (l=Math.round(ndr.getBuffer().getIndex()%4.0)) == 0 ? 0 : 4 - l ;
			ndr.readOctetArray(new byte[(int)l],0,(int)l);		
			//now to read the time
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		
	}
	
	class enumValue extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public int index = -1;
		public Object[] retval = new Object[2];
		public int getOpnum() {
			return 10;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			ndr.writeUnsignedLong(index);
			
			//buffer len , since it is uint16
			ndr.writeUnsignedShort(0);
			//buffer size, since it is uint16
			ndr.writeUnsignedShort(2048);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(1024);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(0);
			
			//pointer
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
			
			ndr.writeUnsignedLong(0);
			
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
			
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
			
			
			
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			//buffer len , since it is uint16
			ndr.readUnsignedShort();
			//buffer size, since it is uint16
			ndr.readUnsignedShort();
			
			//it's a pointer
			//referent
			ndr.readUnsignedLong();
			//max count
			ndr.readUnsignedLong();
			//offset
			ndr.readUnsignedLong();
			
			int actuallength = ndr.readUnsignedLong();//actuallength
			byte[] bytes = new byte[0];
			if (actuallength != 0)
			{
				bytes = new byte[actuallength - 1];
			}
			int i = 0;
			//last 2 bytes , null termination will be eaten outside the loop
			while (i < actuallength - 1)
			{
				int retVal = ndr.readUnsignedShort();
				bytes[i] = (byte)retVal;
				i++;
			}
			if (actuallength != 0)
			{
				ndr.readUnsignedShort();
			}
			
			retval[0] = new String(bytes);
			
			long l = (l=Math.round(ndr.getBuffer().getIndex()%4.0)) == 0 ? 0 : 4 - l ;
			ndr.readOctetArray(new byte[(int)l],0,(int)l);		
			
//			it's a pointer
			//referent
			ndr.readUnsignedLong();
			
			int type = ndr.readUnsignedLong();
			retval[1] = new Integer(type);
			
			ndr.readUnsignedLong();
			
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		
	}
	
	class openKey extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String key = null;
		public int accessMask = KEY_READ;
		
		public int getOpnum() {
			return 15;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(key.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(key.length() + 1);
			
			int i = 0;
			while (i < key.length())
			{
				ndr.writeUnsignedShort(key.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			
			//now align for int
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			long k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			//reserved
			ndr.writeUnsignedLong(0);
			
			ndr.writeUnsignedLong(accessMask);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			ndr.readOctetArray(policyhandle,0,20);
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	class queryValue extends NdrObject
	{
		public JIPolicyHandle parentKey = null;
		public String key = "";
		public int bufferLength = -1;
		public int type = -1;
		public byte[] buffer = null;
		public byte[][] buffer2 = new byte[2048][];
		public int getOpnum() {
			return 17;
		}
		
		public void write(NetworkDataRepresentation ndr) 
		{ 
			
			//write parent handle
			ndr.writeOctetArray(parentKey.handle,0,20);
			
			//key len , since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			//key size, since it is uint16
			ndr.writeUnsignedShort((key.length() + 1) * 2);
			
			//it's a pointer
			//referent
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(key.length() + 1);
			//offset
			ndr.writeUnsignedLong(0);
			//actual count
			ndr.writeUnsignedLong(key.length() + 1);
			
			int i = 0;
			while (i < key.length())
			{
				ndr.writeUnsignedShort(key.charAt(i));
				i++;
			}
			
			//null termination
			ndr.writeUnsignedShort(0);
			
			//now align for int
			double index = new Integer(ndr.getBuffer().getIndex()).doubleValue();
			long k = (k = Math.round(index%4.0)) == 0 ? 0 : 4 - k ;
			ndr.writeOctetArray(new byte[(int)k],0,(int)k);		
			
			//pointer to type
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
			
			//pointer to data
			ndr.writeUnsignedLong(new Object().hashCode());
			//max count
			ndr.writeUnsignedLong(bufferLength);
			ndr.writeUnsignedLong(0);//offset
			ndr.writeUnsignedLong(0);//actual
			
			//pointer to size
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(bufferLength);
			
			//pointer to length
			ndr.writeUnsignedLong(new Object().hashCode());
			ndr.writeUnsignedLong(0);
		}
		
		public void read(NetworkDataRepresentation ndr) 
		{ 
			int i = 0;
			//pointer
			ndr.readUnsignedLong();
			type = ndr.readUnsignedLong();//type
			byte[] retval = new byte[bufferLength];
			//StringBuffer buffer = new StringBuffer();
			//pointer to data
			ndr.readUnsignedLong();
			int maxcount = ndr.readUnsignedLong(); //maxcount
			int offset = ndr.readUnsignedLong();//offset
			switch(type)
			{
				case REG_EXPAND_SZ: //for environment variable strings
				case REG_SZ:
					
					int actuallength = (int)Math.round(new Integer(ndr.readUnsignedLong()).doubleValue()/2.0);//actuallength
					
					//last 2 bytes , null termination will be eaten outside the loop
					while (i < actuallength - 1)
					{
						int retVal = ndr.readUnsignedShort();
						//even though this is a unicode string , but will not have anything else
						//other than ascii charset, which is supported by all encodings.
						//buffer.append(new String(new byte[]{(byte)retVal}));
						retval[i] = (byte)retVal;
						i++;
					}
					if (actuallength != 0)
					{
						ndr.readUnsignedShort();
					}
					
				break;
				case REG_DWORD:
					i = ndr.readUnsignedLong();
					int value = ndr.readUnsignedLong();
					Encdec.enc_uint32le(value, retval, 0);
				break;	
				case REG_NONE:
				case REG_BINARY:
					i = ndr.readUnsignedLong();
					ndr.readOctetArray(retval,0,i);
				break;
				case REG_MULTI_SZ:

					actuallength = (int)Math.round(new Integer(ndr.readUnsignedLong()).doubleValue()/2.0);//actuallength
					int kk = 0,ll = 0;
					i = 0;
					//last 2 bytes , null termination will be eaten outside the loop
					while (i < actuallength - 1)
					{
						int retVal = ndr.readUnsignedShort();
						if (retVal == 0)
						{
							//reached end of one string
							buffer2[kk] = new byte[ll];
							System.arraycopy(retval,0,buffer2[kk],0,ll);
							kk++;
							ll = -1; //it will become 0 next
							retval = new byte[bufferLength];
						}
						else
						{
							retval[ll] = (byte)retVal;
						}
						i++;
						ll++;
					}
					if (actuallength != 0)
					{
						ndr.readUnsignedShort();
					}
					
					break;
				default:
					throw new JIRuntimeException(JIErrorCodes.JI_WINREG_EXCEPTION4);
				
				
			}
			
			long l = (l=Math.round(ndr.getBuffer().getIndex()%4.0)) == 0 ? 0 : 4 - l ;
			ndr.readOctetArray(new byte[(int)l],0,(int)l);		
			
			//pointer to size
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			
			//pointer to length
			ndr.readUnsignedLong();
			ndr.readUnsignedLong();
			
			int hresult = ndr.readUnsignedLong(); 
			if (hresult != 0)
			{
				throw new JIRuntimeException(hresult);
			}
			
			if (type != REG_MULTI_SZ)
			{
				this.buffer = new byte[i];
				System.arraycopy(retval,0,this.buffer,0,i);
			}
			else
			{
				//we have the data already in buffer2.
			}
			//key = buffer.toString();
		}
		
		public byte[] policyhandle = new byte[20];
	}
	
	/** Opens the HKEY_CLASSES_ROOT key
	 * 
	 * @return handle representing the opened key
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_OpenHKCR() throws JIException;
	
	/** Opens the HKEY_CURRENT_USER key
	 * 
	 * @return handle representing the opened key
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_OpenHKCU() throws JIException;
	
	/** Opens the HKEY_USERS key
	 * 
	 * @return handle representing the opened key
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_OpenHKU() throws JIException;
	
	/** Opens the HKEY_LOCAL_MACHINE key
	 * 
	 * @return handle representing the opened key
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_OpenHKLM() throws JIException;
	
	/** Opens the subkey of key specified by handle. 
	 * 
	 * @param handle
	 * @param key
	 * @param accessMask type of access required.
	 * @return
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_OpenKey(JIPolicyHandle handle,String key,int accessMask) throws JIException;
	

	/** Closes the key.
	 * 
	 * @param handle
	 * @throws JIException
	 */
	public void winreg_CloseKey(JIPolicyHandle handle) throws JIException;
	
	/** Query the key for it's name. Please put buffer size more than the estimated expected value. In this case
	 * 1024 would do. 
	 * 
	 * @param handle
	 * @param bufferSize
	 * @return
	 * @throws JIException
	 */
	public byte[] winreg_QueryValue(JIPolicyHandle handle,int bufferSize) throws JIException;
	
	/** Query the key-value for it's value.Please put buffer size more than the estimated expected value. 
	 * 
	 * @param handle
	 * @param bufferSize
	 * @param valueName
	 * @return first param contains the class type as an Integer, second param contains the value as a 1 dimensional byte array,if any. In case of REG_MULTI_SZ
	 * you will get a 2 dimensional byte array as the second param.
	 * @throws JIException
	 */
	public Object[] winreg_QueryValue(JIPolicyHandle handle,String valueName,int bufferSize) throws JIException;
	
	/**Creates a new key by name subKey under the handle. If REG_OPTION_NON_VOLATILE option is used then the key is preserved
	 * in the registry when the machine shutsdown, otherwise it is stored only in memory.
	 * 
	 * @param handle
	 * @param subKey
	 * @param options
	 * @param accessMask
	 * @return
	 * @throws JIException
	 */
	public JIPolicyHandle winreg_CreateKey(JIPolicyHandle handle, String subKey,int options,int accessMask) throws JIException;
	
	/** Sets name-value for a REG_MULTI_SZ type. data is a 2 dimensional array, each primary dimension representing
	 * one string. Please make sure that the encoding is correct while doing String.getBytes(...). 
	 * 
	 * @param handle
	 * @param valueName
	 * @param data
	 * @throws JIException
	 */
	public void winreg_SetValue(JIPolicyHandle handle,String valueName,byte[][] data) throws JIException;
	
	/**Sets an empty name-value for a REG_NONE type.
	 * 
	 * @param handle
	 * @param valueName
	 * @throws JIException
	 */
	public void winreg_SetValue(JIPolicyHandle handle,String valueName) throws JIException;
	
	/** Sets name-value for a REG_SZ\REG_EXPAND_SZ\REG_BINARY type. The data will be considered as String if the binary flag is not set to true. 
	 * In case of non binary data, please make sure that the encoding is correct while doing String.getBytes(...). Set expand_sz to true if the String
	 * contains environment variables. When both binary and expand_sz are set , binary will take precedence. 
	 * 
	 * @param handle
	 * @param valueName
	 * @param data
	 * @param binary
	 * @param expand_sz
	 * @throws JIException
	 */
	public void winreg_SetValue(JIPolicyHandle handle,String valueName, byte[] data, boolean binary,boolean expand_sz) throws JIException;
	
	/**Sets name-value for a REG_DWORD type.
	 * 
	 * @param handle
	 * @param valueName
	 * @param data
	 * @throws JIException
	 */
	public void winreg_SetValue(JIPolicyHandle handle,String valueName, int data) throws JIException;
	
	/**Deletes a key or value specified by valueName.
	 * 
	 * @param handle
	 * @param valueName
	 * @param isKey
	 * @throws JIException
	 */
	public void winreg_DeleteKeyOrValue(JIPolicyHandle handle,String valueName, boolean isKey) throws JIException;
	
	/** Saves registry entries from handle location to local fileName. This path is local to the target machine.
	 * 
	 * @param handle
	 * @param fileName
	 * @throws JIException
	 */
	public void winreg_SaveFile(JIPolicyHandle handle,String fileName) throws JIException;
	
	/** Returns name and class (in that order) for the key identified by index under parent handle.
	 * 
	 * @param handle
	 * @param index
	 * @return
	 * @throws JIException
	 */
	public String[] winreg_EnumKey(JIPolicyHandle handle,int index) throws JIException;

	/**Returns name and type (in that order) for the value identified by index under parent handle.
	 * 
	 * @param handle
	 * @param index
	 * @return First is a String (valueName) and second param is an Integer (type)
	 * @throws JIException
	 */
	public Object[] winreg_EnumValue(JIPolicyHandle handle,int index) throws JIException;
	
	/**
	 * Closes this connection, but a word of caution, it does not close any OPEN Key. Just releases the NP resources it is holding.
	 * 
	 * @throws JIException
	 */
	public void closeConnection() throws JIException;
}
