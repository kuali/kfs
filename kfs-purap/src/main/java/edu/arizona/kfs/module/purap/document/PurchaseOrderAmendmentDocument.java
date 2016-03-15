package edu.arizona.kfs.module.purap.document;

import org.kuali.kfs.sys.businessobject.Building;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;

public class PurchaseOrderAmendmentDocument extends org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument {
	
    protected String routeCode;
    protected Building buildingObj;


    /**
     * Gets the routeCodeObj attribute. 
     * @return Returns the routeCodeObj.
     */
    public Building getBuildingObj() {
        return buildingObj;
    }

    /**
     * Sets the routeCodeObj attribute value.
     * @param routeCodeObj The routeCodeObj to set.
     */
    public void setBuildingObj(Building buildingObj) {
        this.buildingObj = buildingObj;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#getRouteCode()
     */
    public String getRouteCode() {
    	
    	try{
    		BuildingExtension be = (BuildingExtension)(buildingObj.getExtension());
    		routeCode = be.getRouteCode();
    	}catch(Exception e){
    		LOG.debug("Routing Code was not Retrieved");
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