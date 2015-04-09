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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * The interface provides its clients with access to the benefit calculation.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.BenefitsCalculation
 */
public interface LaborBenefitsCalculationService {

    /**
     * get the benefit calculation object based on the given information
     * 
     * @param universityFiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param benefitTypeCode the given benefit type code
     * @return the benefit calculation object matching the given information
     */
    public BenefitsCalculation getBenefitsCalculation(Integer universityFiscalYear, String chartOfAccountsCode, String benefitTypeCode);

    /**
     * get the benefit calculation object based on the given information
     * 
     * @param universityFiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param benefitTypeCode the given benefit type code
     * @param laborBenefitRateCategorycode
     * @return the benefit calculation object matching the given information
     */
    public BenefitsCalculation getBenefitsCalculation(Integer universityFiscalYear, String chartOfAccountsCode, String benefitTypeCode, String laborBenefitRateCategoryCode);
    /**
     * calculate the fringe benefit amount for the given labor object and salary amount
     * 
     * @param laborLedgerObject the given labor object
     * @param salaryAmount the given salary amount
     * @param accountNumber
     * @param subAccountNumber
     * @return the fringe benefit amount for the given labor object and salary amount
     */
    public KualiDecimal calculateFringeBenefit(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber);
    
    /**
     * calculate the fringe benefit amount for the given object code and salary amount
     * 
     * @param fiscalYear the year for object code record
     * @param chartCode the chart for object code record
     * @param objectCode the object code
     * @param salaryAmount amount to calculate benefits for
     * @param accountNumber
     * @param subAccountNumber
     * @return the fringe benefit amount 
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber);
    
    /**
     * calculate the fringe benefit amount from the given position object benefit and salary amount
     * 
     * @param positionObjectBenefit the given position object benefit
     * @param salaryAmount the given salary amount
     * @param accountNumber
     * @param subAccountNumber
     * @return the fringe benefit amount for the given position object benefit and salary amount
     */
    public KualiDecimal calculateFringeBenefit(PositionObjectBenefit positionObjectBenefit, KualiDecimal salaryAmount, String accountNumber, String subAccountNumber);
    
    /**
     * retrieves the benefit rate category code for the given account number. If the the account number has cost share sub accounts
     * the cost share account number is used for determining the category code
     * 
     * @param chartOfAccountsCode - the chart of account code for the account
     * @param accountNumber - the account number to use for the category code lookup
     * @param subAccountNumber - the sub account number to use for category code lookup (for use with cost share accounts)
     * @return the associated benefit rate category code
     */
    public String getBenefitRateCategoryCode(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    public String getCostSharingSourceAccountNumber();

    public String getCostSharingSourceSubAccountNumber();

    public String getCostSharingSourceAccountChartOfAccountsCode();
}
