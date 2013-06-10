/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_DESCRIPTION;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;


/**
 * This class represents a <code>BudgetAdjustmentDocument</code> accounting line parser
 */
public class BudgetAdjustmentAccountingLineParser extends AccountingLineParserBase {
    protected static final String[] AD_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, FINANCIAL_DOCUMENT_LINE_DESCRIPTION, CURRENT_BUDGET_ADJUSTMENT_AMOUNT, BASE_BUDGET_ADJUSTMENT_AMOUNT, FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT };

    /**
     * Constructs a AdvanceDepositAccountingLineParser.java.
     */
    public BudgetAdjustmentAccountingLineParser() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(AD_FORMAT);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    @Override
    public String[] getTargetAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(AD_FORMAT);
    }
}
