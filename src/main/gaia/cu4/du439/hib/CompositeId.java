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
		object.setSolutionId(composite.getSolutionId());
		object.setSourceId(composite.getSourceId());
		object.setParams(composite.getParams());
		object.setModelId(composite.getModelId());
		object.setObjFunc(composite.getObjFunc());
		object.setCovVec(composite.getCovVec());
		object.setEfficiency(composite.getEfficiency());
		object.setDoF(composite.getDoF());
		object.setDimObs(composite.getDimObs());
		object.setNTrans(composite.getNTrans());
		object.setResiduals(composite.getResiduals());
		object.setDiscarded(composite.getDiscarded());
		object.setResidualKind(composite.getResidualKind());
		object.setF2(composite.getF2());
		object.setAdditiveNoise(composite.getAdditiveNoise());
		object.setFlags(composite.getFlags());

		return object;
	}
	
	static public CompositeImpl toCompositeImpl(CompositeId compositeId) {
		if( compositeId == null )
			return null;

		CompositeImpl object = new CompositeImpl();
		object.setSolutionId(compositeId.getSolutionId());
		object.setSourceId(compositeId.getSourceId());
		object.setParams(compositeId.getParams());
		object.setModelId(compositeId.getModelId());
		object.setObjFunc(compositeId.getObjFunc());
		object.setCovVec(compositeId.getCovVec());
		object.setEfficiency(compositeId.getEfficiency());
		object.setDoF(compositeId.getDoF());
		object.setDimObs(compositeId.getDimObs());
		object.setNTrans(compositeId.getNTrans());
		object.setResiduals(compositeId.getResiduals());
		object.setDiscarded(compositeId.getDiscarded());
		object.setResidualKind(compositeId.getResidualKind());
		object.setF2(compositeId.getF2());
		object.setAdditiveNoise(compositeId.getAdditiveNoise());
		object.setFlags(compositeId.getFlags());
		
		return object;
	}	

}
