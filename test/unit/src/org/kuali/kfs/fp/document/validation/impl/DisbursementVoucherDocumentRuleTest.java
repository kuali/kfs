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
package org.kuali.module.financial.rules;

import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.parameters.AccountingLineParameter;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentRuleTest extends KualiTestBaseWithFixtures {

    private static final String COLLECTION_NAME = "DisbursementVoucherDocumentRuleTest.collection1";
    private static final String SOURCE_LINE_1 = "sourceLine1";


    // /////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_notNull() {
        boolean failedAsExpected = false;
        DisbursementVoucherDocumentRule rule = new DisbursementVoucherDocumentRule();
        try {
            TargetAccountingLine line = new TargetAccountingLine();

            rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_null() {
        boolean failedAsExpected = false;
        DisbursementVoucherDocumentRule rule = new DisbursementVoucherDocumentRule();

        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, null);

        assertNull(item);
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation() throws Exception {
        SourceAccountingLine line = (SourceAccountingLine)((AccountingLineParameter) getFixtureEntryFromCollection(COLLECTION_NAME, SOURCE_LINE_1)
                .createObject()).createLine();

        DisbursementVoucherDocumentRule rule = new DisbursementVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertNotNull(item);
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertEquals(line.getAccountNumber(), item.getAccountNumber());
        assertEquals(line.getAccount().getAccountSufficientFundsCode(), item.getAccountSufficientFundsCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
        assertEquals(line.getChartOfAccountsCode(), item.getChartOfAccountsCode());
        assertNotNull(item.getSufficientFundsObjectCode());
        assertEquals(line.getFinancialObjectCode(), item.getFinancialObjectCode());
        assertEquals(line.getObjectCode().getFinancialObjectLevelCode(), item.getFinancialObjectLevelCode());
        assertEquals(line.getPostingYear(), item.getFiscalYear());
        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
    }

    // /////////////////////////////////////////////////////////////////////////
    // Test Methods End Here //
    // /////////////////////////////////////////////////////////////////////////
}
