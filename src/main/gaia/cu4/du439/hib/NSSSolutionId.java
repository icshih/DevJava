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
		object.setSolutionId(nsssolution.getSolutionId());
		object.setSourceId(nsssolution.getSourceId());
		object.setModels(nsssolution.getModels());
		object.setFirstComp(nsssolution.getFirstComp());
		object.setSecondComp(nsssolution.getSecondComp());
		object.setNParam(nsssolution.getNParam());
		object.setCorrVec(nsssolution.getCorrVec());
		object.setObjFunc(nsssolution.getObjFunc());
		object.setF2(nsssolution.getF2());
		object.setEfficiency(nsssolution.getEfficiency());
		object.setFlags(nsssolution.getFlags());
		
		return object;
	}
	
	static public NSSSolutionImpl toNSSSolutionImpl(NSSSolutionId nsssolutionId) {
		if( nsssolutionId == null )
			return null;

		NSSSolutionImpl object = new NSSSolutionImpl();
		object.setSolutionId(nsssolutionId.getSolutionId());
		object.setSourceId(nsssolutionId.getSourceId());
		object.setModels(nsssolutionId.getModels());
		object.setFirstComp(nsssolutionId.getFirstComp());
		object.setSecondComp(nsssolutionId.getSecondComp());
		object.setNParam(nsssolutionId.getNParam());
		object.setCorrVec(nsssolutionId.getCorrVec());
		object.setObjFunc(nsssolutionId.getObjFunc());
		object.setF2(nsssolutionId.getF2());
		object.setEfficiency(nsssolutionId.getEfficiency());
		object.setFlags(nsssolutionId.getFlags());
		
		return object;
	}
}
