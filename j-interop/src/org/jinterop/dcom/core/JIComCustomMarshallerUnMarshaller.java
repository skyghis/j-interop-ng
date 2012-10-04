package org.jinterop.dcom.core;

import java.util.List;
import java.util.Map;

import ndr.NetworkDataRepresentation;

/** Must be implemented by Classes providing marshall, unmarshall support
 * for OBJREF_CUSTOM.
 * 
 * @author vikram
 *
 */
public abstract class JIComCustomMarshallerUnMarshaller {

	public final String CLSID;
	private final IJIComObject me;
	public JIComCustomMarshallerUnMarshaller(String CLSID, IJIComObject comObject)
	{
		this(CLSID, comObject, false);
	}
	
	public JIComCustomMarshallerUnMarshaller(String CLSID, IJIComObject comObject, boolean isTemplate)
	{
		this.CLSID = CLSID;
		if (isTemplate)
		{
			me = new JIComObjectImpl(comObject.getAssociatedSession(), comObject.internal_getInterfacePointer());
			((JIComObjectImpl)me).setCustomObject(this);
		}
		else
		{
			me = comObject;
		}
	}
	
	
	public IJIComObject getComObject()
	{
		return me;
	}
	
	/** Implement for custom encoding. Called by the framework.
	 * 
	 * @param ndr 
	 * @param defferedPointers
	 * @param FLAG
	 */
	public abstract void encode(NetworkDataRepresentation ndr,List defferedPointers, int FLAG);
	
	/** Implement for custom decoding. Called by the framework. 
	 * 
	 * @param ndr
	 * @param defferedPointers
	 * @param FLAG
	 * @param additionalData
	 * @return
	 */
	public abstract JIComCustomMarshallerUnMarshaller decode(IJIComObject newMe, NetworkDataRepresentation ndr,List defferedPointers,int FLAG, Map additionalData);
	
	protected void serialize(NetworkDataRepresentation ndr,Class c, Object value,List defferedPointers,int FLAG)
	{
		JIMarshalUnMarshalHelper.serialize(ndr, c, value, defferedPointers, FLAG);
	}
	
	protected Object deSerialize(NetworkDataRepresentation ndr,Object obj,List defferedPointers,int FLAG, Map additionalData)
	{
		return JIMarshalUnMarshalHelper.deSerialize(ndr, obj, defferedPointers, FLAG, additionalData);
	}
	
	protected static int getLengthInBytes(Class c,Object obj, int FLAG)
	{
		return JIMarshalUnMarshalHelper.getLengthInBytes(c, obj, FLAG);
	}
	
}
