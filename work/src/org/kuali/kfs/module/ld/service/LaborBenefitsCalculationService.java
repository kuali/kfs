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
package org.kuali.module.labor.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.LaborLedgerObject;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.LaborObject;

/**
 * The interface provides its clients with access to the benefit calculation.
 * 
 * @see org.kuali.module.labor.bo.BenefitsCalculation
 */
public interface LaborBenefitsCalculationService {

    /**
     * get the benefit calculation object based on the given information
     * 
     * @param universityFiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param benefitTypeCode the given benefit type code
     * @return the benefit calculation object matching the given information
     */
    public BenefitsCalculation getBenefitsCalculation(Integer universityFiscalYear, String chartOfAccountsCode, String benefitTypeCode);

    /**
     * calculate the fringe benefit amount for the given labor object and salary amount
     * 
     * @param laborLedgerObject the given labor object
     * @param salaryAmount the given salary amount
     * @return the fringe benefit amount for the given labor object and salary amount
     */
    public KualiDecimal calculateFringeBenefit(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount);
}
