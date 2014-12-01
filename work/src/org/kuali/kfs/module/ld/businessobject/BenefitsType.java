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
