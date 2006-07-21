/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.financial.bo;

import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.PropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT;
import static org.kuali.PropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT;
import static org.kuali.PropertyConstants.BUDGET_YEAR;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
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
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentAccountingLineParser extends AccountingLineParserBase {
    private static final String[] AD_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, BUDGET_YEAR, OVERRIDE_CODE, CURRENT_BUDGET_ADJUSTMENT_AMOUNT, BASE_BUDGET_ADJUSTMENT_AMOUNT, 
        FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT,
        FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT,FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT};

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
