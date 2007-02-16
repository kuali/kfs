/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class State extends PersistableBusinessObjectBase {

    private String postalStateCode;
    private String postalStateName;

    /**
     * Default no-arg constructor.
     */
    public State() {

    }

    /**
     * Gets the postalStateCode attribute.
     * 
     * @return Returns the postalStateCode
     * 
     */
    public String getPostalStateCode() {
        return postalStateCode;
    }

    /**
     * Sets the postalStateCode attribute.
     * 
     * @param postalStateCode The postalStateCode to set.
     * 
     */
    public void setPostalStateCode(String postalStateCode) {
        this.postalStateCode = postalStateCode;
    }

    /**
     * Gets the postalStateName attribute.
     * 
     * @return Returns the postalStateName
     * 
     */
    public String getPostalStateName() {
        return postalStateName;
    }

    /**
     * Sets the postalStateName attribute.
     * 
     * @param postalStateName The postalStateName to set.
     * 
     */
    public void setPostalStateName(String postalStateName) {
        this.postalStateName = postalStateName;
    }


    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getPostalStateCode() + " - " + getPostalStateName();
        return theString;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("postalStateCode", this.postalStateCode);
        return m;
    }
}
