/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.integration.bo;


public interface LaborLedgerBenefitsType {

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