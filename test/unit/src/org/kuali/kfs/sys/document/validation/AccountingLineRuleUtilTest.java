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

import org.kuali.module.chart.bo.Account;
import org.kuali.test.KualiTestBaseWithSpring;
import org.kuali.Constants;
import org.kuali.KeyConstants;

/**
 * This class tests some methods of AccountingLineRuleUtil.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AccountingLineRuleUtilTest extends KualiTestBaseWithSpring implements TransactionalDocumentRuleBaseConstants {

    public void testIsValidAccount_valid() {
        testIsValidAccount(getAccountFromFixture("activeAccount"), null, null);
    }

    public void testIsValidAccount_null() {
        testIsValidAccount(null, null, KeyConstants.ERROR_EXISTENCE);
    }

    public void testIsValidAccount_closed() {
        testIsValidAccount(getAccountFromFixture("closedAccount"), null,
            KeyConstants.ERROR_DOCUMENT_ACCOUNT_CLOSED);
    }

    public void testIsValidAccount_expired() {
        testIsValidAccount(getAccountFromFixture("expiredAccount"), null,
            KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED);
    }

    public void testIsValidAccount_expiredNoContinuation() {
        testIsValidAccount(getAccountFromFixture("expiredAccountNoContinuation"), null,
            KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION);
    }

    public void testIsValidAccount_expiredButOverridden() {
        testIsValidAccount(getAccountFromFixture("expiredAccount"), OVERRIDE_CODE.EXPIRED_ACCOUNT, null);
    }

    public void testIsValidAccount_expiredNoContinuationButOverridden() {
        testIsValidAccount(getAccountFromFixture("expiredAccountNoContinuation"), OVERRIDE_CODE.EXPIRED_ACCOUNT, null);
    }

    public void testIsValidAccount_expiredButMultipleOverridden() {
        testIsValidAccount(getAccountFromFixture("expiredAccount"), OVERRIDE_CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED,
            null);
    }

    private Account getAccountFromFixture(String fixtureName) {
        return (Account) getFixtureEntry(fixtureName).createObject();
    }

    private void testIsValidAccount(Account account, String overrideCode, String expectedErrorKey) {
        assertGlobalErrorMapEmpty();
        boolean actual = AccountingLineRuleUtil.isValidAccount(account, overrideCode,
            getDataDictionaryService().getDataDictionary());
        assertEquals("isValidAccount result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, expectedErrorKey);
        }
    }
}
