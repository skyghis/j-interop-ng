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

package org.jinterop.dcom.impls;

import java.io.Serializable;

import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/** From MSDN:- <br>
 * <i>Describes a function.</i> <br> <br>
 * 
 * @since 1.0
 *
 */
public final class FuncDesc implements Serializable{

  private static final long serialVersionUID = -1361861233072624432L;
  public static final int FUNCFLAG_FRESTRICTED = 0x1;
  public static final int FUNCFLAG_FSOURCE = 0x2;
  public static final int   FUNCFLAG_FBINDABLE = 0x4;
  public static final int FUNCFLAG_FREQUESTEDIT = 0x8;
  public static final int FUNCFLAG_FDISPLAYBIND = 0x10;
  public static final int FUNCFLAG_FDEFAULTBIND = 0x20;
  public static final int FUNCFLAG_FHIDDEN = 0x40;
  public static final int FUNCFLAG_FUSESGETLASTERROR = 0x80;
  public static final int FUNCFLAG_FDEFAULTCOLLELEM = 0x100;
  public static final int FUNCFLAG_FUIDEFAULT = 0x200;
  public static final int FUNCFLAG_FNONBROWSABLE = 0x400;
  public static final int FUNCFLAG_FREPLACEABLE = 0x800;
  public static final int FUNCFLAG_FIMMEDIATEBIND = 0x1000;

	
//    MEMBERID memid;                        // Function member ID.
///* [size_is] */ SCODE __RPC_FAR *lprgscode;
///* [size_is] */ ELEMDESC __RPC_FAR *lprgelemdescParam;    
//	FUNCKIND funckind;           // Specifies whether the function is virtual, static, or dispatch-only.
//    INVOKEKIND invkind;        // Invocation kind. Indicates if this is a property function, and if so, what kind.
//    CALLCONV callconv;        // Specifies the function's calling 
//                            // convention.
//    short cParams;            // Count of total number of parameters.
//    short cParamsOpt;        // Count of optional parameters (detailed 
//                            // description follows).
//    short oVft;                // For FUNC_VIRTUAL, specifies the offset in the VTBL.
//    short cScodes;    // Count of permitted return values. 
//    ELEMDESC elemdescFunc;    // Contains the return type of the function.
//    WORD wFuncFlags;     // Definition of flags follows.

	private JIStruct values = null;
	public final  int memberId;
	public final JIPointer lprgscode;
	public final JIPointer lprgelemdescParam;
	public final int funcKind;
	public final int invokeKind;
	public final int callConv;
	public final short cParams;
	public final short cParamsOpt;
	public final short oVft;
	public final short cScodes;
	public final ElemDesc elemdescFunc;
	public final short wFuncFlags;
	
	
	FuncDesc(JIPointer values)
	{
		this(values.isNull() ? null : (JIStruct)values.getReferent());
	}
	FuncDesc(JIStruct filledStruct) 
	{
		if (filledStruct == null)
		{
			values = null;
			memberId = -1;
			lprgscode = null;
			lprgelemdescParam = null;
			funcKind = -1;
			invokeKind = -1;
			callConv = -1;
			cParams = -1;
			cParamsOpt = -1;
			oVft = -1;
			cScodes = -1;
			elemdescFunc = null;
			wFuncFlags = -1;
			return;
		}
		values = filledStruct;
		memberId = ((Integer)values.getMember(0)).intValue();
		lprgscode = (JIPointer)values.getMember(1);
		JIPointer ptr = (JIPointer)values.getMember(2);
		JIArray arrayOfElemDesc = null;
		if (!ptr.isNull())
		{
			JIArray arry = (JIArray)ptr.getReferent();
			Object[] obj = (Object[])arry.getArrayInstance();
//			ElemDesc[] arry2 = new ElemDesc[obj.length];
//			for (int i = 0; i < obj.length; i++)
//			{
//				arry2[i] = new ElemDesc((JIStruct)obj[i]);
//			}
			
//			arrayOfElemDesc = new JIArray(arry2);
			arrayOfElemDesc = new JIArray(obj);
		}
		
		lprgelemdescParam =  new JIPointer(arrayOfElemDesc);
		funcKind = ((Integer)values.getMember(3)).intValue();
		invokeKind = ((Integer)values.getMember(4)).intValue();
		callConv = ((Integer)values.getMember(5)).intValue();
		cParams = ((Short)values.getMember(6)).shortValue();
		cParamsOpt = ((Short)values.getMember(7)).shortValue();
		oVft = ((Short)values.getMember(8)).shortValue();
		cScodes = ((Short)values.getMember(9)).shortValue();
		elemdescFunc = new ElemDesc(((JIStruct)values.getMember(10)));
		wFuncFlags = ((Short)values.getMember(11)).shortValue();
	}
	
}
