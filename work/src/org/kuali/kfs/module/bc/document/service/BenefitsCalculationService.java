/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service;

/**
 * This class defines methods that a Benefits Calculation Service must provide
 * 
 * The Benefits Calculation Service supports functionality related to calculating benefits request amounts
 * for a Budget Construction Document (account/sub-account).  This includes calculations for annual and monthly amounts.
 * Monthly amounts are only calculated when associated monthly request amounts exist.
 */
public interface BenefitsCalculationService {

    /**
     * This method returns the disabled setting of the System Parameter controlling Budget module Benefits Calculation.
     * Disabling Benefits Calculation will cause any UI controls related to the Benefits Calculation functionality to not be displayed.
     * Disabling will also cause associated business rules checks to behave differently or not be run.
     * 
     * @return
     */
    public boolean getBenefitsCalculationDisabled(); 

}
