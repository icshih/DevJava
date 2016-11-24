package main.gaia.cu4.du439.hib;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gaia.cu1.mdb.cu4.du439.dm.Composite;
import gaia.cu1.mdb.cu4.du439.dm.NSSParam;
import gaia.cu1.mdb.cu4.du439.dmimpl.CompositeImpl;

public class CompositeId extends CompositeImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;

	private main.gaia.cu4.du439.hib.NSSParamId[] paramsId;
	
    public long getId() {
	    return this.id;
	}

	public void setId(long id) {
	    this.id = id;
	}
	
    public main.gaia.cu4.du439.hib.NSSParamId[] getParamsId() {
        return this.paramsId;
    }


    public void setParamsId(main.gaia.cu4.du439.hib.NSSParamId[] paramsId) {
        this.paramsId = paramsId;
    }
	
	static public CompositeId fromCompositeImpl(Composite composite) {
		if( composite == null )
			return null;

		CompositeId object = new CompositeId();
		object.setSolutionId(composite.getSolutionId());
		object.setSourceId(composite.getSourceId());
		List<NSSParamId> list = Stream.of(composite.getParams())
			.map(p -> NSSParamId.fromNSSParamImpl(p)).collect(Collectors.toList());
		object.setParamsId(list.toArray(new NSSParamId[0]));
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
		List<NSSParam> list = Stream.of(compositeId.getParamsId())
				.map(p -> NSSParamId.toNSSParamImpl(p)).collect(Collectors.toList());
		object.setParams(list.toArray(new NSSParam[0]));
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
