/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/SubFundGroupType.java,v $
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

package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class SubFundGroupType extends BusinessObjectBase {

    private String subFundGroupTypeCode;
    private String subFundGroupTypeDescription;
    private boolean subFundGroupTypeActiveIndicator;

    /**
     * Default constructor.
     */
    public SubFundGroupType() {

    }

    /**
     * Gets the subFundGroupTypeCode attribute.
     * 
     * @return Returns the subFundGroupTypeCode
     * 
     */
    public String getSubFundGroupTypeCode() {
        return subFundGroupTypeCode;
    }

    /**
     * Sets the subFundGroupTypeCode attribute.
     * 
     * @param subFundGroupTypeCode The subFundGroupTypeCode to set.
     * 
     */
    public void setSubFundGroupTypeCode(String subFundGroupTypeCode) {
        this.subFundGroupTypeCode = subFundGroupTypeCode;
    }


    /**
     * Gets the subFundGroupTypeDescription attribute.
     * 
     * @return Returns the subFundGroupTypeDescription
     * 
     */
    public String getSubFundGroupTypeDescription() {
        return subFundGroupTypeDescription;
    }

    /**
     * Sets the subFundGroupTypeDescription attribute.
     * 
     * @param subFundGroupTypeDescription The subFundGroupTypeDescription to set.
     * 
     */
    public void setSubFundGroupTypeDescription(String subFundGroupTypeDescription) {
        this.subFundGroupTypeDescription = subFundGroupTypeDescription;
    }


    /**
     * Gets the subFundGroupTypeActiveIndicator attribute.
     * 
     * @return Returns the subFundGroupTypeActiveIndicator
     * 
     */
    public boolean getSubFundGroupTypeActiveIndicator() {
        return subFundGroupTypeActiveIndicator;
    }

    /**
     * Sets the subFundGroupTypeActiveIndicator attribute.
     * 
     * @param subFundGroupTypeActiveIndicator The subFundGroupTypeActiveIndicator to set.
     * 
     */
    public void setSubFundGroupTypeActiveIndicator(boolean subFundGroupTypeActiveIndicator) {
        this.subFundGroupTypeActiveIndicator = subFundGroupTypeActiveIndicator;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("subFundGroupTypeCode", this.subFundGroupTypeCode);
        return m;
    }
}
