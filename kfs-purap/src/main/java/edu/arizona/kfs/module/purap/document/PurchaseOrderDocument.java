/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.arizona.kfs.module.purap.document;


import org.kuali.kfs.sys.businessobject.Building;

import edu.arizona.kfs.sys.businessobject.BuildingExtension;

/**
 * Purchase Order Document
 */
public class PurchaseOrderDocument extends org.kuali.kfs.module.purap.document.PurchaseOrderDocument {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocument.class);

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
