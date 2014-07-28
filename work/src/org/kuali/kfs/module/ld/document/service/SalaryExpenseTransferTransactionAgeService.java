/*
 * Copyright 2014 The Kuali Foundation.
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
