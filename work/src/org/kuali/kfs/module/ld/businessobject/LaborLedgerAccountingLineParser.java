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
