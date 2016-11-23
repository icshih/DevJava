package main.gaia.cu4.du439.hib;

import gaia.cu1.mdb.cu4.du439.dm.NSSParam;
import gaia.cu1.mdb.cu4.du439.dmimpl.NSSParamImpl;

public class NSSParamId extends NSSParamImpl {

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
	
	static public NSSParamId fromNSSParamImpl(NSSParam nssParam) {
		if( nssParam == null )
			return null;

		NSSParamId object = new NSSParamId();
		object.setName(nssParam.getName());
		object.setIsUsed(nssParam.getIsUsed());
		object.setIsConstrained(nssParam.getIsConstrained());
		object.setIsDerived(nssParam.getIsDerived());
		object.setValue(nssParam.getValue());
		object.setError(nssParam.getError());
		object.setIndexCorrMat(nssParam.getIndexCorrMat());

		return object;
	}
	
	static public NSSParamImpl toNSSParamImpl(NSSParamId nssParamId) {
		if( nssParamId == null )
			return null;

		NSSParamImpl object = new NSSParamImpl();
		object.setName(nssParamId.getName());
		object.setIsUsed(nssParamId.getIsUsed());
		object.setIsConstrained(nssParamId.getIsConstrained());
		object.setIsDerived(nssParamId.getIsDerived());
		object.setValue(nssParamId.getValue());
		object.setError(nssParamId.getError());
		object.setIndexCorrMat(nssParamId.getIndexCorrMat());
		
		return object;
	}
}
