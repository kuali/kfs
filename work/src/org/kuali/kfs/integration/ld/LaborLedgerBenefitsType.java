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
package org.kuali.kfs.integration.ld;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface LaborLedgerBenefitsType extends ExternalizableBusinessObject{

    /**
     * Gets the positionBenefitTypeCode attribute.
     * 
     * @return Returns the positionBenefitTypeCode
     */
    public abstract String getPositionBenefitTypeCode();

    /**
     * Sets the positionBenefitTypeCode attribute.
     * 
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     */
    public abstract void setPositionBenefitTypeCode(String positionBenefitTypeCode);

    /**
     * Gets the positionBenefitTypeDescription attribute.
     * 
     * @return Returns the positionBenefitTypeDescription
     */
    public abstract String getPositionBenefitTypeDescription();

    /**
     * Sets the positionBenefitTypeDescription attribute.
     * 
     * @param positionBenefitTypeDescription The positionBenefitTypeDescription to set.
     */
    public abstract void setPositionBenefitTypeDescription(String positionBenefitTypeDescription);

    /**
     * Gets the positionBenefitRetirementIndicator attribute.
     * 
     * @return Returns the positionBenefitRetirementIndicator
     */
    public abstract boolean isPositionBenefitRetirementIndicator();

    /**
     * Sets the positionBenefitRetirementIndicator attribute.
     * 
     * @param positionBenefitRetirementIndicator The positionBenefitRetirementIndicator to set.
     */
    public abstract void setPositionBenefitRetirementIndicator(boolean positionBenefitRetirementIndicator);

}
