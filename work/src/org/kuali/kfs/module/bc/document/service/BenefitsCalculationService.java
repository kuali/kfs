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
