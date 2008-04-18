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
package org.kuali.module.labor.bo;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.integration.bo.LaborFringeBenefitInformation;

/**
* A simple wrapper for fringe benefits information
 */
public class FringeBenefitInformation implements LaborFringeBenefitInformation {
    private BenefitsCalculation benefitsCalculation;
    
    /**
     * Constructs a FringeBenefitInformation to wrap the given PositionObjectBenefit
     * @param positionObjectBenefit a positionObjectBenefit record for this to wrap
     */
    public FringeBenefitInformation(PositionObjectBenefit positionObjectBenefit) {
        this.benefitsCalculation = positionObjectBenefit.getBenefitsCalculation();
    }

    /**
     * @see org.kuali.module.integration.bo.LaborFringeBenefitInformation#getPositionFringeBenefitObjectCode()
     */
    public String getPositionFringeBenefitObjectCode() {
        return benefitsCalculation.getPositionFringeBenefitObjectCode();
    }

    /**
     * @see org.kuali.module.integration.bo.LaborFringeBenefitInformation#getPositionFringeBenefitPercent()
     */
    public KualiDecimal getPositionFringeBenefitPercent() {
        return benefitsCalculation.getPositionFringeBenefitPercent().toKualiDecimal();
    }

}
