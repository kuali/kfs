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

import static org.kuali.kfs.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.KFSPropertyConstants.POSITION_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParserBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.module.labor.LaborPropertyConstants;

/**
 * Labor Extended class for parsing serialized <code>AccountingLine</code>s for <code>TransactionalDocument</code>s
 */
public class LaborLedgerAccountingLineParser extends AccountingLineParserBase {

    protected static final String[] LABOR_LEDGER_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, POSITION_NUMBER, LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, LaborPropertyConstants.PAYROLL_TOTAL_HOURS, KFSPropertyConstants.AMOUNT };

    /**
     * Constructs a LaborLedgerAccountingLineParser.java.
     */
    public LaborLedgerAccountingLineParser() {

    }

    /**
     * Gets the LABOR_LEDGER_FORMAT the SourceAccountingLineFormat.
     * 
     * @return Returns the LABOR_LEDGER_FORMAT.
     * @see org.kuali.core.bo.AccountingLineParser#getSourceAccountingLineFormat()
     */
    public String[] getSourceAccountingLineFormat() {
        return LABOR_LEDGER_FORMAT;
    }

    /**
     * Gets the LABOR_LEDGER_FORMAT the TargetAccountingLineFormat.
     * 
     * @return Returns the LABOR_LEDGER_FORMAT.
     * @see org.kuali.core.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    public String[] getTargetAccountingLineFormat() {
        return LABOR_LEDGER_FORMAT;
    }

    /**
     * Will return the format determing if the line is Source or Target.
     * 
     * @param accountingLineClass
     * @return Returns The format.
     */
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
