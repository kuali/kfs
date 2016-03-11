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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;



/**
 * @author Kevin McOmber
 * 
 */
public class RouteCode extends PersistableBusinessObjectBase implements MutableInactivatable {
    
    private String routeCode;           // Route Code 
    private String routeCodeDesc;       // Route Code Description
    private boolean active;             // Route Code Active Indicator    

    

    

    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("RouteCode", this.routeCode);
        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method gets routeCode
     * @return String
     */
    public String getRouteCode() {
        return routeCode;
    }

    /**
     * This method sets routeCode
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    /**
     * This method gets routeCodeDesc
     * @return String
     */
    public String getRouteCodeDesc() {
        return routeCodeDesc;
    }

    /**
     * This method sets routeCodeDesc
     */
    public void setRouteCodeDesc(String routeCodeDesc) {
        this.routeCodeDesc = routeCodeDesc;
    }

}

