/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.sys.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;


/**
 * This class represents the RouteCode BusinessObject.
 * @author $Author$
 * @version $Revision$
 */

public class BuildingExtension extends PersistableBusinessObjectExtensionBase {
    private String routeCode;       //Route Code Extended Attribute
    private RouteCode routeCodeObj; // Route Code Object
    private String campusCode;
    private String buildingCode;
    
    /**
     * Gets the campusCode attribute. 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the buildingCode attribute. 
     * @return Returns the buildingCode.
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute value.
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    /**
     * Returns the RouteCode
     * @return RouteCode
     */
    public RouteCode getRouteCodeObj() {
        return routeCodeObj;
    }

    /**
     * Setter for  RouteCode
     * @param routeCodeObj
     */
    public void setRouteCodeObj(RouteCode routeCodeObj) {
        this.routeCodeObj = routeCodeObj;
    }

    /**
     * Getter for  Route Code
     * @return routeCode
     */
    public String getRouteCode() {
        return routeCode;
    }

    /**
     * Setter for  Route Code
     * @param routeCode
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

}

