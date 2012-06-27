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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.businessobject.RequestBenefits;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.LaborBenefitRateCategory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Implements custom search for showing single request line benefits impact
 */
public class RequestBenefitsLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequestBenefitsLookupableHelperServiceImpl.class);
    private LaborModuleService laborModuleService;
    private BusinessObjectService businessObjectService;

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

            // create a new KualiPercent with a value of 0
            KualiPercent fringePct = new KualiPercent(0);
            RequestBenefits requestBenefit = new RequestBenefits();
            requestBenefit.setFinancialObjectBenefitsTypeCode(positionObjectBenefit.getFinancialObjectBenefitsTypeCode());
            requestBenefit.setFinancialObjectBenefitsTypeDescription(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getLaborLedgerBenefitsType().getPositionBenefitTypeDescription());
            requestBenefit.setPositionFringeBenefitObjectCode(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitObjectCode());
            requestBenefit.setPositionFringeBenefitObjectCodeName(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitObject().getFinancialObjectCodeName());


            // make sure the system parameter exists
            Boolean categoryRateCalcParmExists = SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
            String sysParam = " ";
            if (categoryRateCalcParmExists){
                sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
            }
            // if calc using rate category exists and is not "N" do new calc by category process
            if (categoryRateCalcParmExists && !sysParam.equalsIgnoreCase("N")) {
                String laborBenefitRateCategoryCode = "";
                // check the system param to see if the labor benefit rate category should be filled in
                sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
                LOG.debug("sysParam: " + sysParam);
                // if sysParam == Y then Labor Benefit Rate Category Code must be filled in
                if (sysParam.equalsIgnoreCase("Y")) {
                    // get the account number from the map to use in the lookup of the labor benefit rate category code
                    String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);

                    // create a map to use in the lookup of the account
                    Map<String, Object> accountLookupFields = new HashMap<String, Object>();
                    accountLookupFields.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
                    accountLookupFields.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

                    LOG.info("Looking up account to get labor benefit rate category code");

                    // lookup the account based off the map
                    Account account = (Account) businessObjectService.findByPrimaryKey(Account.class, accountLookupFields);

                    // create string to hold the labor benefit rate category code
                    laborBenefitRateCategoryCode = account.getLaborBenefitRateCategoryCode();

                }
                else {
                    if (SpringContext.getBean(ParameterService.class).parameterExists(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE")) {
                        laborBenefitRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE");
                    }
                    else {
                        laborBenefitRateCategoryCode = "";
                    }
                }


                // make sure the labor benefit rate category code is filled in
                if (laborBenefitRateCategoryCode != null) {
                    // create a map to be used to look up the benefit calculation
                    Map<String, Object> benefitMap = new HashMap<String, Object>();
                    benefitMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
                    benefitMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
                    benefitMap.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, positionObjectBenefit.getFinancialObjectBenefitsTypeCode());
                    // benefitMap.put("positionFringeBenefitObjectCode", positionObjectBenefit.getFinancialObjectCode()); Removed
                    // because it is not necessary
                    benefitMap.put(LaborPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE, laborBenefitRateCategoryCode);

                    LOG.debug("Looking up benefits calcuation {" + fiscalYear + "," + chartOfAccountsCode + "," + positionObjectBenefit.getFinancialObjectBenefitsTypeCode() + "," + positionObjectBenefit.getFinancialObjectCode() + "," + laborBenefitRateCategoryCode + "}");
                    // use the map to lookup a benefit calculation
                    List<BenefitsCalculation> bcArrayList = (List<BenefitsCalculation>) businessObjectService.findMatching(BenefitsCalculation.class, benefitMap);

                    // make sure a benefits calculation was found
                    if (bcArrayList.size() > 0) {
                        LOG.info("Found benefits calculation for {" + fiscalYear + "," + chartOfAccountsCode + "," + positionObjectBenefit.getFinancialObjectBenefitsTypeCode() + "," + positionObjectBenefit.getFinancialObjectCode() + "," + laborBenefitRateCategoryCode + "}");
                        fringePct = (bcArrayList.get(0).getPositionFringeBenefitPercent());

                        // create a map to use for the lookup of the labor benefit rate category
                        Map<String, Object> lbrcMap = new HashMap<String, Object>();
                        lbrcMap.put(LaborPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE, laborBenefitRateCategoryCode);

                        // lookup the labor benefit rate category to get the description
                        LaborBenefitRateCategory lbrc = (LaborBenefitRateCategory) businessObjectService.findByPrimaryKey(LaborBenefitRateCategory.class, lbrcMap);

                        // set the category code and description from the labor benefit rate category object to display in the
                        // results
                        requestBenefit.setLaborBenefitRateCategoryCode(lbrc.getLaborBenefitRateCategoryCode());
                        requestBenefit.setLaborBenefitRateCategoryCodeDesc(lbrc.getCodeDesc());

                        // set the benefit percent to be display in the results
                        requestBenefit.setPositionFringeBenefitPercent(fringePct);

                    }
                    else {
                        LOG.info("Could not locate a benfits calculation for {" + fiscalYear + "," + chartOfAccountsCode + "," + positionObjectBenefit.getFinancialObjectBenefitsTypeCode() + "," + positionObjectBenefit.getFinancialObjectCode() + "," + laborBenefitRateCategoryCode + "}");
                    }
                }
            }
            else {
                // no rate calc parm or it is set to "N" - do original calc benefit without category code
                requestBenefit.setLaborBenefitRateCategoryCode(" ");
                requestBenefit.setLaborBenefitRateCategoryCodeDesc("Category Calc Mode Off");

                requestBenefit.setPositionFringeBenefitPercent(positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitPercent());
                fringePct = positionObjectBenefit.getLaborLedgerBenefitsCalculation().getPositionFringeBenefitPercent();
            }


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

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
