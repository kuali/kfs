/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/AwardStatus.java,v $
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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class AwardStatus extends BusinessObjectBase {

    private String awardStatusCode;
    private String awardStatusDescription;
    private boolean rowActiveIndicator;
   
    /**
     * Default constructor.
     */
    public AwardStatus() {

    }

    /**
     * Gets the awardStatusCode attribute.
     * 
     * @return Returns the awardStatusCode
     * 
     */
    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    /**
     * Sets the awardStatusCode attribute.
     * 
     * @param awardStatusCode The awardStatusCode to set.
     * 
     */
    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }


    /**
     * Gets the awardStatusDescription attribute.
     * 
     * @return Returns the awardStatusDescription
     * 
     */
    public String getAwardStatusDescription() {
        return awardStatusDescription;
    }

    /**
     * Sets the awardStatusDescription attribute.
     * 
     * @param awardStatusDescription The awardStatusDescription to set.
     * 
     */
    public void setAwardStatusDescription(String awardStatusDescription) {
        this.awardStatusDescription = awardStatusDescription;
    }

    /**
     * Gets the rowActiveIndicator attribute. 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("awardStatusCode", this.awardStatusCode);
        return m;
    }

}
