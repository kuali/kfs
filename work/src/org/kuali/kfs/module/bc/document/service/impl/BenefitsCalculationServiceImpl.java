/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    protected ConfigurationService kualiConfigurationService;
    protected BenefitsCalculationDao benefitsCalculationDao;
    protected OptionsService optionsService;

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
