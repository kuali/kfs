/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.businessobject.RequestBenefits;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Implements custom search for showing single request line benefits impact
 */
public class RequestBenefitsLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private LaborModuleService laborModuleService;

    /**
     * Creates <code>RequestBenefits</code> objects based on a BC expenditure line chart, object code and request amount.
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        List<RequestBenefits> requestBenefits = new ArrayList<RequestBenefits>();

        Integer fiscalYear = Integer.valueOf(fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
        String chartOfAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String objectCode = fieldValues.get(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME);
        List<LaborLedgerPositionObjectBenefit> positionObjectBenefits = laborModuleService.retrieveLaborPositionObjectBenefits(fiscalYear, chartOfAccountsCode, objectCode);
        for (Iterator<LaborLedgerPositionObjectBenefit> iterator = positionObjectBenefits.iterator(); iterator.hasNext();) {
            LaborLedgerPositionObjectBenefit positionObjectBenefit = (LaborLedgerPositionObjectBenefit) iterator.next();

            KualiPercent fringePct = positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitPercent();
            RequestBenefits requestBenefit = new RequestBenefits();
            requestBenefit.setFinancialObjectBenefitsTypeCode(positionObjectBenefit.getFinancialObjectBenefitsTypeCode());
            requestBenefit.setFinancialObjectBenefitsTypeDescription(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getLaborLedgerBenefitsType().getPositionBenefitTypeDescription());
            requestBenefit.setPositionFringeBenefitObjectCode(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitObjectCode());
            requestBenefit.setPositionFringeBenefitObjectCodeName(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitObject().getFinancialObjectCodeName());
            requestBenefit.setPositionFringeBenefitPercent(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitPercent());
            
            BigDecimal requestAmount = new BigDecimal(Integer.valueOf(fieldValues.get(KFSPropertyConstants.ACCOUNT_LINE_ANNUAL_BALANCE_AMOUNT)));
            BigDecimal fringePctDecimal = fringePct.bigDecimalValue().divide(new BigDecimal(100));
            BigDecimal result = requestAmount.multiply(fringePctDecimal).setScale(0, RoundingMode.HALF_UP);
            KualiInteger detAmount = new KualiInteger(result.toBigInteger());
            requestBenefit.setFringeDetailAmount(detAmount);
            requestBenefits.add(requestBenefit);
        }

        return requestBenefits;
    }

    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }

}
