/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ld.LaborLedgerBenefitsType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Labor business object for Benefits Type
 */
public class BenefitsType extends PersistableBusinessObjectBase implements LaborLedgerBenefitsType, MutableInactivatable {

    private String positionBenefitTypeCode;
    private String positionBenefitTypeDescription;
    private boolean positionBenefitRetirementIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public BenefitsType() {

    }

    /**
     * Gets the positionBenefitTypeCode attribute.
     * 
     * @return Returns the positionBenefitTypeCode
     */
    public String getPositionBenefitTypeCode() {
        return positionBenefitTypeCode;
    }

    /**
     * Sets the positionBenefitTypeCode attribute.
     * 
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     */
    public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {
        this.positionBenefitTypeCode = positionBenefitTypeCode;
    }

    /**
     * Gets the positionBenefitTypeDescription attribute.
     * 
     * @return Returns the positionBenefitTypeDescription
     */
    public String getPositionBenefitTypeDescription() {
        return positionBenefitTypeDescription;
    }

    /**
     * Sets the positionBenefitTypeDescription attribute.
     * 
     * @param positionBenefitTypeDescription The positionBenefitTypeDescription to set.
     */
    public void setPositionBenefitTypeDescription(String positionBenefitTypeDescription) {
        this.positionBenefitTypeDescription = positionBenefitTypeDescription;
    }

    /**
     * Gets the positionBenefitRetirementIndicator attribute.
     * 
     * @return Returns the positionBenefitRetirementIndicator
     */
    public boolean isPositionBenefitRetirementIndicator() {
        return positionBenefitRetirementIndicator;
    }

    /**
     * Sets the positionBenefitRetirementIndicator attribute.
     * 
     * @param positionBenefitRetirementIndicator The positionBenefitRetirementIndicator to set.
     */
    public void setPositionBenefitRetirementIndicator(boolean positionBenefitRetirementIndicator) {
        this.positionBenefitRetirementIndicator = positionBenefitRetirementIndicator;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("positionBenefitTypeCode", this.positionBenefitTypeCode);

        return m;
    }
}
