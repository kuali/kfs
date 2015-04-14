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
package org.kuali.kfs.module.bc.document.dataaccess;


public interface BenefitsCalculationDao {

    /**
     * calculate the annual benefits (no rate category code) for the budget construction general ledger key passed in, and stores them in the database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param finObjTypeExpenditureexpCd
     * @param expenditureINList
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String expenditureINList);

    /**
     * calculate the monthly budget benefits (no rate category code) for the budget construction general ledger key passed in, and stores them in the
     * database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param finObjTypeExpenditureexpCd
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd);


    /**
     * calculate the annual benefits (with rate category code) for the budget construction general ledger key passed in, and stores them in the database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param finObjTypeExpenditureexpCd
     * @param expenditureINList
     * @param laborBenefitRateCategoryCode
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String expenditureINList, String laborBenefitRateCategoryCode);

    /**
     * calculate the monthly budget benefits (with rate category code) for the budget construction general ledger key passed in, and stores them in the
     * database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param finObjTypeExpenditureexpCd
     * @param laborBenefitRateCategoryCode
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd, String laborBenefitRateCategoryCode);


}
