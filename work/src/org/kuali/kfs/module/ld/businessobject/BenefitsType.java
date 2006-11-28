/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/ld/businessobject/BenefitsType.java,v $
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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class BenefitsType extends BusinessObjectBase {

    private String positionBenefitTypeCode;
    private String positionBenefitTypeDescription;
    private boolean positionBenefitRetirementIndicator;

    /**
     * Default constructor.
     */
    public BenefitsType() {

    }

    /**
     * Gets the positionBenefitTypeCode attribute.
     * 
     * @return Returns the positionBenefitTypeCode
     * 
     */
    public String getPositionBenefitTypeCode() {
        return positionBenefitTypeCode;
    }

    /**
     * Sets the positionBenefitTypeCode attribute.
     * 
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     * 
     */
    public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {
        this.positionBenefitTypeCode = positionBenefitTypeCode;
    }


    /**
     * Gets the positionBenefitTypeDescription attribute.
     * 
     * @return Returns the positionBenefitTypeDescription
     * 
     */
    public String getPositionBenefitTypeDescription() {
        return positionBenefitTypeDescription;
    }

    /**
     * Sets the positionBenefitTypeDescription attribute.
     * 
     * @param positionBenefitTypeDescription The positionBenefitTypeDescription to set.
     * 
     */
    public void setPositionBenefitTypeDescription(String positionBenefitTypeDescription) {
        this.positionBenefitTypeDescription = positionBenefitTypeDescription;
    }


    /**
     * Gets the positionBenefitRetirementIndicator attribute.
     * 
     * @return Returns the positionBenefitRetirementIndicator
     * 
     */
    public boolean isPositionBenefitRetirementIndicator() {
        return positionBenefitRetirementIndicator;
    }


    /**
     * Sets the positionBenefitRetirementIndicator attribute.
     * 
     * @param positionBenefitRetirementIndicator The positionBenefitRetirementIndicator to set.
     * 
     */
    public void setPositionBenefitRetirementIndicator(boolean positionBenefitRetirementIndicator) {
        this.positionBenefitRetirementIndicator = positionBenefitRetirementIndicator;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("positionBenefitTypeCode", this.positionBenefitTypeCode);
        return m;
    }
}
