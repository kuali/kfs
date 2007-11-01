/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Instances of this class represent the various statuses an Award can be in.
 */
public class AwardStatus extends PersistableBusinessObjectBase {

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
     */
    public String getAwardStatusCode() {
        return awardStatusCode;
    }


    /**
     * Sets the awardStatusCode attribute.
     * 
     * @param awardStatusCode The awardStatusCode to set.
     */
    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }


    /**
     * This method is a dummy getter that simply returns the same value as getAwardStatusCode(). This method was needed so we could
     * add an attribute reference to the DD with a different name than awardStatusCode. This allowed us to override the input
     * behavior and define two different methods of input based on what type of page we're displaying.
     * 
     * @return Returns the awardStatusCode
     */
    public String getAwardStatusCodeDropDown() {
        return awardStatusCode;
    }

    /**
     * @param awardStatusCode
     * @deprecated Do not use this method, it is only here for DD mapping purposes and has no defined functionality. See KULCG-281
     *             for further details.
     */
    public void setAwardStatusCodeDropDown(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    /**
     * Gets the awardStatusDescription attribute.
     * 
     * @return Returns the awardStatusDescription
     */
    public String getAwardStatusDescription() {
        return awardStatusDescription;
    }

    /**
     * Sets the awardStatusDescription attribute.
     * 
     * @param awardStatusDescription The awardStatusDescription to set.
     */
    public void setAwardStatusDescription(String awardStatusDescription) {
        this.awardStatusDescription = awardStatusDescription;
    }

    /**
     * Gets the rowActiveIndicator attribute.
     * 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * 
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("awardStatusCode", this.awardStatusCode);
        return m;
    }

}
