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
package org.kuali.module.labor.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.LaborLedgerObject;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.service.LaborBenefitsCalculationService;
import org.kuali.module.labor.service.LaborPositionObjectBenefitService;
import org.springframework.transaction.annotation.Transactional;

/**
 * To provide its clients with access to the benefit calculation.
 */
@Transactional
public class LaborBenefitsCalculationServiceImpl implements LaborBenefitsCalculationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBenefitsCalculationServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private LaborPositionObjectBenefitService laborPositionObjectBenefitService;

    /**
     * @see org.kuali.module.labor.service.LaborBenefitsCalculationService#getBenefitsCalculation(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public BenefitsCalculation getBenefitsCalculation(Integer universityFiscalYear, String chartOfAccountsCode, String benefitTypeCode) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        fieldValues.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, benefitTypeCode);

        return (BenefitsCalculation) businessObjectService.findByPrimaryKey(BenefitsCalculation.class, fieldValues);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBenefitsCalculationService#calculateFringeBenefit(java.lang.Integer,
     *      java.lang.String, java.lang.String, org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        LaborObject laborObject = new LaborObject();
        
        laborObject.setUniversityFiscalYear(fiscalYear);
        laborObject.setChartOfAccountsCode(chartCode);
        laborObject.setFinancialObjectCode(objectCode);

        laborObject = (LaborObject) businessObjectService.retrieve(laborObject);

        return calculateFringeBenefit(laborObject, salaryAmount);
    }

    /**
     * @see org.kuali.module.labor.service.LaborBenefitsCalculationService#calculateFringeBenefit(org.kuali.module.labor.bo.LaborObject,
     *      org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        KualiDecimal fringeBenefit = KualiDecimal.ZERO;

        if (salaryAmount.isZero() || ObjectUtils.isNull(laborLedgerObject)) {
            return fringeBenefit;
        }

        String FringeOrSalaryCode = laborLedgerObject.getFinancialObjectFringeOrSalaryCode();
        if (!LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(FringeOrSalaryCode)) {
            return fringeBenefit;
        }

        Integer fiscalYear = laborLedgerObject.getUniversityFiscalYear();
        String chartOfAccountsCode = laborLedgerObject.getChartOfAccountsCode();
        String objectCode = laborLedgerObject.getFinancialObjectCode();

        Collection<PositionObjectBenefit> positionObjectBenefits = laborPositionObjectBenefitService.getPositionObjectBenefits(fiscalYear, chartOfAccountsCode, objectCode);
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            KualiDecimal benefitAmount = this.calculateFringeBenefit(positionObjectBenefit, salaryAmount);
            fringeBenefit = fringeBenefit.add(benefitAmount);
        }

        return fringeBenefit;
    }

    /**
     * @see org.kuali.module.labor.service.LaborBenefitsCalculationService#calculateFringeBenefit(org.kuali.module.labor.bo.PositionObjectBenefit,
     *      org.kuali.core.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(PositionObjectBenefit positionObjectBenefit, KualiDecimal salaryAmount) {
        if (salaryAmount.isZero() || ObjectUtils.isNull(positionObjectBenefit)) {
            return KualiDecimal.ZERO;
        }

        // calculate the benefit amount (ledger amt * (benfit pct/100) )
        KualiDecimal fringeBenefitPercent = positionObjectBenefit.getBenefitsCalculation().getPositionFringeBenefitPercent();
        return fringeBenefitPercent.multiply(salaryAmount).divide(new KualiDecimal(100));
    }

    /**
     * Sets the laborPositionObjectBenefitService attribute value.
     * 
     * @param laborPositionObjectBenefitService The laborPositionObjectBenefitService to set.
     */
    public void setLaborPositionObjectBenefitService(LaborPositionObjectBenefitService laborPositionObjectBenefitService) {
        this.laborPositionObjectBenefitService = laborPositionObjectBenefitService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
