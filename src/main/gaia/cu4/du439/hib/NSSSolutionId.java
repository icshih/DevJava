package main.gaia.cu4.du439.hib;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import gaia.cu1.mdb.cu4.du439.dm.Composite;
import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.mdb.cu4.du439.dm.TwoBodySolution;
import gaia.cu1.mdb.cu4.du439.dmimpl.NSSSolutionImpl;

public class NSSSolutionId extends NSSSolutionImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	protected main.gaia.cu4.du439.hib.CompositeId[] modelsId;
	
    public long getId() {
	    return this.id;
	}

	public void setId(long id) {
	    this.id = id;
	}
	
    public main.gaia.cu4.du439.hib.CompositeId[] getModelsId() {
        return this.modelsId;
    }

    public void setModelsId(main.gaia.cu4.du439.hib.CompositeId[] modelsId) {
        this.modelsId = modelsId;
    }
    
	static public NSSSolutionId fromNSSSolutionImpl(NSSSolution nsssolution) {
		if( nsssolution == null )
			return null;

		NSSSolutionId object = new NSSSolutionId();
		object.setSolutionId(nsssolution.getSolutionId());
		object.setSourceId(nsssolution.getSourceId());
		object.setModelsId(Stream.of(nsssolution.getModels())
				.map(t -> CompositeId.fromCompositeImpl(Composite.class.cast(t)))
				.collect(Collectors.toList())
				.toArray(new CompositeId[0]));
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
		object.setModels(Stream.of(nsssolutionId.getModelsId())
				.map(c -> TwoBodySolution.class.cast(CompositeId.toCompositeImpl(c)))
				.collect(Collectors.toList())
				.toArray(new TwoBodySolution[0]));
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
