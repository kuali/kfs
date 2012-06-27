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
package org.kuali.kfs.module.bc.document.service.impl;

import org.kuali.kfs.module.bc.document.dataaccess.BenefitsCalculationDao;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.util.BudgetConstructionUtils;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the BenefitsCalculationService interface
 */
@Transactional
public class BenefitsCalculationServiceImpl implements BenefitsCalculationService {

    private ConfigurationService kualiConfigurationService;
    private BenefitsCalculationDao benefitsCalculationDao;
    private OptionsService optionsService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#getBenefitsCalculationDisabled()
     */
    public boolean isBenefitsCalculationDisabled() {
        // Note: for now just return false, implement application parameter
        // if decision is made to implement this functionality as an enhancement
        return false;

        // return kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM,
        // BCConstants.DISABLE_BENEFITS_CALCULATION_FLAG);
    }
    
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateAnnualBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String laborBenefitRateCategoryCode) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense and expenditure types list
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        /**
         * calculate annual benefits
         */
        benefitsCalculationDao.calculateAnnualBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, expenditureINList, laborBenefitRateCategoryCode);
    }
    

    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateAnnualBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */

    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense and expenditure types list
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();

        /**
         * calculate annual benefits
         */
        benefitsCalculationDao.calculateAnnualBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, expenditureINList);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        /**
         * calculate monthly benefits (assumes annual benefits have already been calculated
         */
        benefitsCalculationDao.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String laborBenefitRateCategoryCode) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        /**
         * calculate monthly benefits (assumes annual benefits have already been calculated
         */
        benefitsCalculationDao.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, laborBenefitRateCategoryCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateAllBudgetConstructionGeneralLedgerBenefits(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateAllBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense and expenditure types list
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();

        /**
         * call both annual and monthly calculations (order is important)
         */
        benefitsCalculationDao.calculateAnnualBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, expenditureINList);
        benefitsCalculationDao.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BenefitsCalculationService#calculateAllBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateAllBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String laborBenefitRateCategoryCode) {
        /**
         * do nothing if benefits calculation is disabled
         */
        if (isBenefitsCalculationDisabled())
            return;
        /**
         * get the financial object type expenditure/expense and expenditure types list
         */
        String finObjTypeExpenditureexpCd = optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd();
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();

        /**
         * call both annual and monthly calculations (order is important)
         */
        benefitsCalculationDao.calculateAnnualBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, expenditureINList, laborBenefitRateCategoryCode);
        benefitsCalculationDao.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber, finObjTypeExpenditureexpCd, laborBenefitRateCategoryCode);
        
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * This method allows spring to initialize the Dao, so we don't have to look up the bean on each call from the application
     * 
     * @param benefitsCalculationDao - the Dao for benefits calculation
     */
    public void setBenefitsCalculationDao(BenefitsCalculationDao benefitsCalculationDao) {
        this.benefitsCalculationDao = benefitsCalculationDao;
    }

    /**
     * use this to return the "Expenditures/Expense" financial object type code from the options table this must be done by fiscal
     * year, so unfortunately we have to make one call to OJB whenever one of the methods that needs this constant is called.
     * 
     * @param optionsService
     */

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

}
