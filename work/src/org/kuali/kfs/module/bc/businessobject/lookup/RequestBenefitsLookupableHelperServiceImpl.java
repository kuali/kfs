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
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.businessobject.RequestBenefits;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

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

      // account number is used in the lookup of the labor benefit rate category code below
      String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
      
      // find out if we are running in rate category mode
      Boolean categoryRateCalcMode = false;
      Boolean categoryRateCalcParmExists = SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND);
      String sysParam = " ";
      if (categoryRateCalcParmExists) {
          sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND);
          if (sysParam.equalsIgnoreCase("Y")){
              categoryRateCalcMode = true;
          }
      }
        
      Integer fiscalYear = Integer.valueOf(fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
      String chartOfAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
      String objectCode = fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
      List<LaborLedgerPositionObjectBenefit> positionObjectBenefits = laborModuleService.retrieveActiveLaborPositionObjectBenefits(fiscalYear, chartOfAccountsCode, objectCode);
      for (Iterator<LaborLedgerPositionObjectBenefit> iterator = positionObjectBenefits.iterator(); iterator.hasNext();) {
          LaborLedgerPositionObjectBenefit positionObjectBenefit = (LaborLedgerPositionObjectBenefit) iterator.next();

          KualiPercent fringePct = new KualiPercent(0);
          Boolean beneCalcExistsAndActive = true;
          LaborLedgerBenefitsCalculation benefitsCalculation = null;
          String laborBenefitRateCategoryCode = "";
          if (categoryRateCalcMode){

              // get rate category code from account
              Map<String, Object> accountLookupFields = new HashMap<String, Object>();
              accountLookupFields.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
              accountLookupFields.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
              Account account = (Account) businessObjectService.findByPrimaryKey(Account.class, accountLookupFields);
              laborBenefitRateCategoryCode = account.getLaborBenefitRateCategoryCode();

              benefitsCalculation = positionObjectBenefit.getLaborLedgerBenefitsCalculation(laborBenefitRateCategoryCode);
          } else {
              benefitsCalculation = positionObjectBenefit.getLaborLedgerBenefitsCalculation();
          }

          if (ObjectUtils.isNull(benefitsCalculation) || !benefitsCalculation.isActive()){
              beneCalcExistsAndActive = false;
              if (ObjectUtils.isNull(benefitsCalculation)){
                  LOG.warn("Could not locate a benefits calculation for {" + fiscalYear + "," + chartOfAccountsCode + "," + positionObjectBenefit.getFinancialObjectBenefitsTypeCode() + "," + positionObjectBenefit.getFinancialObjectCode() + "," + laborBenefitRateCategoryCode + "}");
              }
          } else {
              fringePct = benefitsCalculation.getPositionFringeBenefitPercent();
          }

          if (beneCalcExistsAndActive){
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
