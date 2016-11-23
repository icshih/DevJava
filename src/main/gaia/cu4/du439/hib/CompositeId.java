package main.gaia.cu4.du439.hib;

import gaia.cu1.mdb.cu4.du439.dm.Composite;
import gaia.cu1.mdb.cu4.du439.dmimpl.CompositeImpl;

public class CompositeId extends CompositeImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	
    public long getId() {
	    return this.id;
	}

	public void setId(long id) {
	    this.id = id;
	}
	
	static public CompositeId fromCompositeImpl(Composite composite) {
		if( composite == null )
			return null;

		CompositeId object = new CompositeId();
		object.setParams(composite.getParams());

		return object;
	}
	
	static public CompositeImpl toNSSParamImpl(CompositeId compositeId) {
		if( compositeId == null )
			return null;

		CompositeImpl object = new CompositeImpl();
		object.setParams(compositeId.getParams());

		return object;
	}	

}
