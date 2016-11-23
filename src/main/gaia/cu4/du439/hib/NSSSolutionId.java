package main.gaia.cu4.du439.hib;

import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.mdb.cu4.du439.dmimpl.NSSSolutionImpl;

public class NSSSolutionId extends NSSSolutionImpl {
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
	
	static public NSSSolutionId fromNSSSolutionImpl(NSSSolution nsssolution) {
		if( nsssolution == null )
			return null;

		NSSSolutionId object = new NSSSolutionId();
		object.setModels(nsssolution.getModels());

		return object;
	}
	
	static public NSSSolutionImpl toNSSSolutionImpl(NSSSolutionId nsssolution) {
		if( nsssolution == null )
			return null;

		NSSSolutionImpl object = new NSSSolutionImpl();

		return object;
	}
	
}
