package edu.arizona.kfs.module.purap.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.krad.util.ObjectUtils;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;

public class BulkReceivingDocument extends org.kuali.kfs.module.purap.document.BulkReceivingDocument {

    protected String routeCode;
    protected Building buildingObj;

    public Building getBuildingObj() {
    	if(ObjectUtils.isNull(buildingObj)) {
    		this.refreshReferenceObject("buildingObj");
    	}
    	
        return buildingObj;
    }

    public void setBuildingObj(Building buildingObj) {
        this.buildingObj = buildingObj;
    }
    
    public String getRouteCode() {
    	if (StringUtils.isBlank(routeCode)) {
	    	try{
	    		BuildingExtension be = (BuildingExtension)(buildingObj.getExtension());
	    		routeCode = be.getRouteCode();
	    	}catch(Exception e){
	    		LOG.debug("Routing Code was not Retrieved");
	    	}
    	}
    	
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
}