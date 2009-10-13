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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Common Budget Construction rule utilities
 */
public class BudgetConstructionRuleUtil {

    /**
     * Checks if newLine already exists in existingLines using the comparable fields as the uniqueness test
     * 
     * @param existingLines
     * @param newLine
     * @param comparableFields
     * @return
     */
    public static boolean hasExistingPBGLLine(List<PendingBudgetConstructionGeneralLedger> existingLines, PendingBudgetConstructionGeneralLedger newLine) {

        boolean isFound = false;

        // fields used to check for unique line below
        List<String> comparableFields = new ArrayList<String>();
        comparableFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        comparableFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        if (newLine.getFinancialSubObjectCode() == null) {
            newLine.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        for (PendingBudgetConstructionGeneralLedger line : existingLines) {
            if (ObjectUtil.equals(line, newLine, comparableFields)) {
                isFound = true;
                break;
            }
        }
        if (newLine.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode())) {
            newLine.setFinancialSubObjectCode(null);
        }

        return isFound;
    }
    public static Calendar getNoBudgetAllowedExpireDate(Integer activeBCFiscalYear){
        
        AccountingPeriod accountingPeriod = (AccountingPeriod) SpringContext.getBean(AccountingPeriodService.class).getByPeriod(BCConstants.NO_BUDGET_ALLOWED_EXPIRE_ACCOUNTING_PERIOD, activeBCFiscalYear-BCConstants.NO_BUDGET_ALLOWED_FY_OFFSET);
        
        Date date = accountingPeriod.getUniversityFiscalPeriodEndDate();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(Calendar.DAY_OF_MONTH, 1);

        return gcal;
        
    }
    public static Calendar getAccountExpiredWarningExpireDate(Integer activeBCFiscalYear){
        
        AccountingPeriod accountingPeriod = (AccountingPeriod) SpringContext.getBean(AccountingPeriodService.class).getByPeriod(BCConstants.ACCOUNT_EXPIRE_WARNING_ACCOUNTING_PERIOD, activeBCFiscalYear-BCConstants.ACCOUNT_EXPIRE_WARNING_FY_OFFSET);
        
        Date date = accountingPeriod.getUniversityFiscalPeriodEndDate();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(date);
        gcal.add(Calendar.DAY_OF_MONTH, 1);

        return gcal;
        
    }
}
