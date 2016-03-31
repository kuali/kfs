package edu.arizona.kfs.module.purap.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.krad.util.ObjectUtils;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;

public class PurchaseOrderAmendmentDocument extends org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument {
	
    protected String routeCode;
    protected Building buildingObj;

    /**
     * Gets the routeCodeObj attribute. 
     * @return Returns the routeCodeObj.
     */
    public Building getBuildingObj() {
    	if(ObjectUtils.isNull(buildingObj)) {
    		this.refreshReferenceObject("buildingObj");
    	}
    	
        if (buildingObj == null || !StringUtils.equals(buildingObj.getBuildingCode(), deliveryBuildingCode)) {
        	buildingObj = getBusinessObjectService().findBySinglePrimaryKey(Building.class, deliveryBuildingCode);
        }
        return buildingObj;
    }

    /**
     * Sets the routeCodeObj attribute value.
     * @param routeCodeObj The routeCodeObj to set.
     */
    public void setBuildingObj(Building buildingObj) {
    	if(ObjectUtils.isNull(buildingObj)) {
    		this.refreshReferenceObject("buildingObj");
    	}

        this.buildingObj = buildingObj;
        setDeliveryBuildingCode(buildingObj.getBuildingCode());
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#getRouteCode()
     */
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

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#setRouteCode(java.lang.String)
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
    
}