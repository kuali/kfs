/*
 * Copyright 2006 The Kuali Foundation.
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

package org.kuali.module.financial.bo;

import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.PropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.PropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.PropertyConstants.OVERRIDE_CODE;
import static org.kuali.PropertyConstants.PROJECT_CODE;
import static org.kuali.PropertyConstants.SUB_ACCOUNT_NUMBER;

import org.kuali.core.bo.AccountingLineParserBase;

/**
 * <code>BudgetAdjustmentDocument</code> accounting line parser
 * 
 * 
 */
public class BudgetAdjustmentAccountingLineParser extends AccountingLineParserBase {
    private static final String[] AD_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, OVERRIDE_CODE, CURRENT_BUDGET_ADJUSTMENT_AMOUNT, BASE_BUDGET_ADJUSTMENT_AMOUNT, FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT };

    /**
     * 
     * Constructs a AdvanceDepositAccountingLineParser.java.
     */
    public BudgetAdjustmentAccountingLineParser() {
        super();
    }

    /**
     * @see org.kuali.core.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return AD_FORMAT;
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    @Override
    public String[] getTargetAccountingLineFormat() {
        return AD_FORMAT;
    }
}
