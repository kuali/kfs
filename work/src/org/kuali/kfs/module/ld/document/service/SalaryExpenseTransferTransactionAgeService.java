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
package org.kuali.kfs.module.ld.document.service;

import java.util.List;

import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;

/**
 * Defines methods that need to be implemented by SalaryExpenseTransferTransactionAgeServiceImpl.
 */
public interface SalaryExpenseTransferTransactionAgeService {

    /**
     * Determines if the age of any of the transactions is older than the value in a parameter.
     *
     * @param accountingLines
     * @param periodsFromParameter
     * @return true if all of the transaction dates are younger by fiscal periods than specified in the appropriate parameter; false
     *         otherwise
     */
    public boolean defaultNumberOfFiscalPeriodsCheck(List<LaborLedgerExpenseTransferAccountingLine> accountingLines, Integer periodsFromParameter);

    /**
     * Checks the sub fund associated with the account in a target accounting line. If sub fund is in
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter, use different # of fiscal periods.
     *
     * @param periodsFromParameter initial periods from a parameter
     * @param currentTargetLine
     * @return the periodsFromParameter, which may have value in ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND
     */
    public Integer checkCurrentSubFund(Integer periodsFromParameter, ExpenseTransferTargetAccountingLine currentTargetLine);
}
