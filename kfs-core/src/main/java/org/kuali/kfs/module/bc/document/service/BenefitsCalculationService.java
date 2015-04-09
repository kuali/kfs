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
package org.kuali.kfs.module.bc.document.service;

/**
 * This class defines methods that a Benefits Calculation Service must provide The Benefits Calculation Service supports
 * functionality related to calculating benefits request amounts for a Budget Construction Document (account/sub-account). This
 * includes calculations for annual and monthly amounts. Monthly amounts are only calculated when associated monthly request amounts
 * exist.
 */
public interface BenefitsCalculationService {

    /**
     * This method returns the disabled setting of the System Parameter controlling Budget module Benefits Calculation. Disabling
     * Benefits Calculation will cause any UI controls related to the Benefits Calculation functionality to not be displayed.
     * Disabling will also cause associated business rules checks to behave differently or not be run.
     * 
     * @return
     */
    public boolean isBenefitsCalculationDisabled();
    
    /**
     * no rate category used in calc
     * calculates the annual benefits for the budget construction general ledger key passed in, and stores them in the database
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber the string containing the sub account number
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                       Integer fiscalYear,
                                                                       String chartOfAccounts,
                                                                       String accountNumber,
                                                                       String subAccountNumber);
    /**
     * no rate category used in calc
     * calculates the monthly budget benefits for the budget construction general ledger key passed in, and stores them in the database
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber the string containing the sub account number 
     * 
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                        Integer fiscalYear,
                                                                        String chartOfAccounts,
                                                                        String accountNumber,
                                                                        String subAccountNumber);


    /**
     * no rate category used in calc
     * calculates both the monthly budget and the annual budget budget construction general ledger benefits for the key passed in, and 
     * stores them in the database.
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber
     */
    public void calculateAllBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                    Integer fiscalYear,
                                                                    String chartOfAccounts,
                                                                    String accountNumber,
                                                                    String subAccountNumber);

   
    
    /**
     * rate category used in calc
     * calculates the annual benefits for the budget construction general ledger key passed in, and stores them in the database
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber the string containing the sub account number
     * @param laborBenefitRateCategoryCode the string containing the labor benefit rate category code
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, 
                                                                       Integer universityFiscalYear, 
                                                                       String chartOfAccountsCode, 
                                                                       String accountNumber, 
                                                                       String subAccountNumber, 
                                                                       String laborBenefitRateCategoryCode);
    
    /**
     * rate category used in calc
     * calculates the monthly budget benefits for the budget construction general ledger key passed in, and stores them in the database
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber the string containing the sub account number 
     * @param laborBenefitRateCategoryCode the string containing the labor benefit rate category code
     * 
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                        Integer fiscalYear,
                                                                        String chartOfAccounts,
                                                                        String accountNumber,
                                                                        String subAccountNumber, 
                                                                        String laborBenefitRateCategoryCode);

    /**
     * rate category used in calc 
     * calculates both the monthly budget and the annual budget budget construction general ledger benefits for the key passed in, and 
     * stores them in the database.
     * @param documentNumber  the string containing the document number
     * @param fiscalYear      the integer value of the fiscal year
     * @param chartOfAccounts the string containing the chart of accounts
     * @param accountNumber   the string containing the account number
     * @param subAccountNumber
     * @param laborBenefitRateCategoryCode the string containing the labor benefit rate category code
     */
    public void calculateAllBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                    Integer fiscalYear,
                                                                    String chartOfAccounts,
                                                                    String accountNumber,
                                                                    String subAccountNumber, 
                                                                    String laborBenefitRateCategoryCode);

   
    
}
