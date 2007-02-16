/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.labor.bo;

import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.AMOUNT;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.PropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.PropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE;
import static org.kuali.PropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR;
import static org.kuali.PropertyConstants.PAYROLL_TOTAL_HOURS;
import static org.kuali.PropertyConstants.POSITION_NUMBER;
import static org.kuali.PropertyConstants.PROJECT_CODE;
import static org.kuali.PropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParserBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;

/**
 * Extended class for parsing serialized <code>AccountingLine</code>s for <code>TransactionalDocument</code>s
 * 
 * 
 */
public class LaborLedgerAccountingLineParser extends AccountingLineParserBase {

    protected static final String[] LABOR_LEDGER_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, POSITION_NUMBER,
        PAYROLL_END_DATE_FISCAL_YEAR, PAYROLL_END_DATE_FISCAL_PERIOD_CODE, PAYROLL_TOTAL_HOURS, AMOUNT };
    
    public LaborLedgerAccountingLineParser() {
    }
    
    /**
     * @see org.kuali.core.bo.AccountingLineParser#getSourceAccountingLineFormat()
     */
    public String[] getSourceAccountingLineFormat() {
        return LABOR_LEDGER_FORMAT;
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    public String[] getTargetAccountingLineFormat() {
        return LABOR_LEDGER_FORMAT;
    }
    
    
    private String[] chooseFormat(Class<? extends AccountingLine> accountingLineClass) {
        String[] format = null;
        if (SourceAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getSourceAccountingLineFormat();
        }
        else if (TargetAccountingLine.class.isAssignableFrom(accountingLineClass)) {
            format = getTargetAccountingLineFormat();
        }
        else {
            throw new IllegalStateException("unknow accounting line class: " + accountingLineClass);
        }
        return format;
    }

}
