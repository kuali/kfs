/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * To provide its clients with access to the benefit calculation.
 */
@Transactional
public class LaborBenefitsCalculationServiceImpl implements LaborBenefitsCalculationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBenefitsCalculationServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private LaborPositionObjectBenefitService laborPositionObjectBenefitService;
    private AccountService accountService;
    private SubAccountService subAccountService;
    private String costSharingSourceAccountNumber;
    private String costSharingSourceSubAccountNumber;
    private String costSharingSourceAccountChartOfAccountsCode;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#getBenefitsCalculation(java.lang.Integer,
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
     * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#getBenefitsCalculation(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public BenefitsCalculation getBenefitsCalculation(Integer universityFiscalYear, String chartOfAccountsCode, String benefitTypeCode, String laborBenefitRateCategoryCode) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        fieldValues.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, benefitTypeCode);
        fieldValues.put(LaborPropertyConstants.LABOR_BENEFIT_RATE_CATEGORY_CODE, laborBenefitRateCategoryCode);

        return (BenefitsCalculation) businessObjectService.findByPrimaryKey(BenefitsCalculation.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#calculateFringeBenefit(java.lang.Integer,
     *      java.lang.String, java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        LaborObject laborObject = new LaborObject();
        
        laborObject.setUniversityFiscalYear(fiscalYear);
        laborObject.setChartOfAccountsCode(chartCode);
        laborObject.setFinancialObjectCode(objectCode);

        laborObject = (LaborObject) businessObjectService.retrieve(laborObject);

        return calculateFringeBenefit(laborObject, salaryAmount, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#calculateFringeBenefit(org.kuali.kfs.module.ld.businessobject.LaborObject,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        KualiDecimal fringeBenefit = KualiDecimal.ZERO;

        if (salaryAmount == null || salaryAmount.isZero() || ObjectUtils.isNull(laborLedgerObject)) {
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
            KualiDecimal benefitAmount = this.calculateFringeBenefit(positionObjectBenefit, salaryAmount, accountNumber, subAccountNumber);
            fringeBenefit = fringeBenefit.add(benefitAmount);
        }

        return fringeBenefit;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#calculateFringeBenefit(org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(PositionObjectBenefit positionObjectBenefit, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber) {
        if (salaryAmount == null || salaryAmount.isZero() || ObjectUtils.isNull(positionObjectBenefit)) {
            return KualiDecimal.ZERO;
        }
        
        KualiDecimal fringeBenefitAmount = new KualiDecimal(0);
        
        //create a  map for the search criteria to lookup the fringe benefit percentage 
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, positionObjectBenefit.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, positionObjectBenefit.getChartOfAccountsCode());
        fieldValues.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, positionObjectBenefit.getFinancialObjectBenefitsTypeCode());

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        Boolean enableFringeBenefitCalculationByBenefitRate = parameterService.getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, LaborConstants.BenefitCalculation.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_PARAMETER);
        
        //If system parameter is evaluated to use calculation by benefit rate category
        if (enableFringeBenefitCalculationByBenefitRate) {
            
            //get the benefit rate based off of the university fiscal year, chart of account code, labor benefit type code and labor benefit rate category code 
            String laborBenefitRateCategoryCode = getBenefitRateCategoryCode(positionObjectBenefit.getChartOfAccountsCode(), accountNumber, subAccountNumber);
            
            //add the Labor Benefit Rate Category Code to the search criteria
            fieldValues.put("laborBenefitRateCategoryCode",laborBenefitRateCategoryCode);
            
            String search = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR) + "," + fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) + "," + fieldValues.get(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE) + "," + fieldValues.get("laborBenefitRateCategoryCode");
            LOG.info("Searching for Benefits Calculation {" + search + "}");
            //perform the lookup based off the map 
            BenefitsCalculation bc = (BenefitsCalculation) businessObjectService.findByPrimaryKey(BenefitsCalculation.class, fieldValues);
            
            //make sure the benefits calculation isn't null
            if (bc != null) {
                LOG.info("Found a Benefit Calculation for {" + search + "}");
                //lookup from the db the fringe benefit percentage from the list that is return.  ***Should only return one value from the database.
                KualiDecimal fringeBenefitPercent = bc.getPositionFringeBenefitPercent();
                
                LOG.debug("fringeBenefitPercent: " + fringeBenefitPercent);
                
                // calculate the benefit amount (ledger amt * (benfit pct/100) )
                fringeBenefitAmount = fringeBenefitPercent.multiply(salaryAmount).divide(KFSConstants.ONE_HUNDRED.kualiDecimalValue());
            }else{
                LOG.info("Did not locate a Benefits Calculation for {" + search + "}.");
                //set the benefit amount to 0
                fringeBenefitAmount = new KualiDecimal(0);
            }
        }else{
            // calculate the benefit amount (ledger amt * (benfit pct/100) )
            KualiDecimal fringeBenefitPercent = positionObjectBenefit.getBenefitsCalculation().getPositionFringeBenefitPercent();
            
            fringeBenefitAmount = fringeBenefitPercent.multiply(salaryAmount).divide(KFSConstants.ONE_HUNDRED.kualiDecimalValue());
        }
        
        LOG.debug("fringBenefitAmount: " + fringeBenefitAmount);
        return fringeBenefitAmount;
    }
    
	/**
	 * @see org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService#getBenefitRateCategoryCode(java.lang.String,
	 *      java.lang.String)
	 */
	public String getBenefitRateCategoryCode(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
	    this.setCostSharingSourceAccountNumber(null);
	    this.setCostSharingSourceAccountChartOfAccountsCode(null);
	    this.setCostSharingSourceSubAccountNumber(null);
		//make sure the sub accout number is filled in
		if (subAccountNumber != null) {
			LOG.info("Sub Account Number was filled in. Checking to see if it is a Cost Sharing Sub Account.");

			//make sure the system parameter exists
			if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "USE_COST_SHARE_SOURCE_ACCOUNT_BENEFIT_RATE_IND")) {
				//parameter exists, determine the value of the parameter
				String sysParam2 = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "USE_COST_SHARE_SOURCE_ACCOUNT_BENEFIT_RATE_IND");
				LOG.debug("sysParam2: " + sysParam2);

				//if sysParam2 == Y then check to see if it's a cost sharing sub account
				if (sysParam2.equalsIgnoreCase("Y")) {
					//lookup the A21SubAccount to get the cost sharing source account
					Map<String, Object> subFieldValues = new HashMap<String, Object>();
					subFieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
					subFieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
					subFieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_TYPE_CODE, "CS");
					LOG.info("Looking for a cost sharing sub account for sub account number " + subAccountNumber);
					
					if(ObjectUtils.isNull(businessObjectService)){
					    this.businessObjectService = SpringContext.getBean(BusinessObjectService.class);
					}
					
					//perform the lookup
					List<A21SubAccount> subAccountList = (List<A21SubAccount>) businessObjectService.findMatching(A21SubAccount.class, subFieldValues);
					//check to see if the lookup returns an empty list
					if (subAccountList.size() > 0) {
						LOG.info("Found A21 Sub Account. Retrieving source account number for cost sharing.");
						accountNumber = subAccountList.get(0).getCostShareSourceAccountNumber();
						LOG.debug("Cost Sharing Source Account Number : " + accountNumber);
						this.setCostSharingSourceAccountNumber(accountNumber);
						this.setCostSharingSourceAccountChartOfAccountsCode(subAccountList.get(0).getCostShareChartOfAccountCode());
						this.setCostSharingSourceSubAccountNumber(subAccountList.get(0).getCostShareSourceSubAccountNumber());
					}
					else {
						LOG.info(subAccountNumber + " is not a cost sharing account.  Using the Labor Benefit Rate Category from the account number.");
					}
				}
				else {
					LOG.info("Using the Grant Account to determine the labor benefit rate category code.");

				}
			}
		}

		LOG.info("Looking up Account {" + chartOfAccountsCode + "," + accountNumber + "}");
		//lookup the account from the db based off the account code and the account number
		Account account = this.getAccountService().getByPrimaryId(chartOfAccountsCode, accountNumber);
		String laborBenefitRateCategoryCode = account.getLaborBenefitRateCategoryCode();
		
		//make sure the laborBenefitRateCategoryCode is not null or blank
		if(StringUtils.isBlank(laborBenefitRateCategoryCode)){
		    LOG.info("The Account did not have a Labor Benefit Rate Category Code. Will use the system parameter default.");
		    //The system parameter does not exist. Using a blank Labor Benefit Rate Category Code
		    laborBenefitRateCategoryCode = StringUtils.defaultString(SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, LaborConstants.BenefitCalculation.DEFAULT_BENEFIT_RATE_CATEGORY_CODE_PARAMETER));
		}else{
		    LOG.debug("Labor Benefit Rate Category Code for Account " + accountNumber + " is " + laborBenefitRateCategoryCode);
		}
		
		return laborBenefitRateCategoryCode;
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

    /**
     * Gets the accountService attribute. 
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
        if(this.accountService == null){
            this.setAccountService(SpringContext.getBean(AccountService.class));
        }
        
        return accountService;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the subAccountService attribute. 
     * @return Returns the subAccountService.
     */
    public SubAccountService getSubAccountService() {
        if(this.subAccountService == null){
            this.setSubAccountService(SpringContext.getBean(SubAccountService.class));
        }
        
        return subAccountService;
    }

    /**
     * Sets the subAccountService attribute value.
     * @param subAccountService The subAccountService to set.
     */
    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    /**
     * Gets the costSharingSourceAccountNumber attribute. 
     * @return Returns the costSharingSourceAccountNumber.
     */
    public String getCostSharingSourceAccountNumber() {
        return costSharingSourceAccountNumber;
    }

    /**
     * Sets the costSharingSourceAccountNumber attribute value.
     * @param costSharingSourceAccountNumber The costSharingSourceAccountNumber to set.
     */
    public void setCostSharingSourceAccountNumber(String costSharingSourceAccountNumber) {
        this.costSharingSourceAccountNumber = costSharingSourceAccountNumber;
    }

    /**
     * Gets the costSharingSourceSubAccountNumber attribute. 
     * @return Returns the costSharingSourceSubAccountNumber.
     */
    public String getCostSharingSourceSubAccountNumber() {
        return costSharingSourceSubAccountNumber;
    }

  
 
    /**
     * Sets the costSharingSourceSubAccountNumber attribute value.
     * @param costSharingSourceSubAccountNumber The costSharingSourceSubAccountNumber to set.
     */
    public void setCostSharingSourceSubAccountNumber(String costSharingSourceSubAccountNumber) {
        this.costSharingSourceSubAccountNumber = costSharingSourceSubAccountNumber;
    }

    /**
     * Gets the costSharingSourceAccountChartOfAccountsCode attribute. 
     * @return Returns the costSharingSourceAccountChartOfAccountsCode.
     */
    public String getCostSharingSourceAccountChartOfAccountsCode() {
        return costSharingSourceAccountChartOfAccountsCode;
    }

    /**
     * Sets the costSharingSourceAccountChartOfAccountsCode attribute value.
     * @param costSharingSourceAccountChartOfAccountsCode The costSharingSourceAccountChartOfAccountsCode to set.
     */
    public void setCostSharingSourceAccountChartOfAccountsCode(String costSharingSourceAccountChartOfAccountsCode) {
        this.costSharingSourceAccountChartOfAccountsCode = costSharingSourceAccountChartOfAccountsCode;
    }
}
