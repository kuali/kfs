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

package org.kuali.kfs.module.ld.businessobject;

import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.POSITION_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;

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
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(LABOR_LEDGER_FORMAT);
    }

    /**
     * Gets the LABOR_LEDGER_FORMAT the TargetAccountingLineFormat.
     * 
     * @return Returns the LABOR_LEDGER_FORMAT.
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    @Override
    public String[] getTargetAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(LABOR_LEDGER_FORMAT);
    }

}
