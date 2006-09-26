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
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.AccountingLineOverride;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests some methods of AccountingLineRuleUtil.
 * 
 * @author Kuali Financial Transactions Team ()
 */
@WithTestSpringContext
public class AccountingLineRuleUtilTest extends KualiTestBaseWithFixtures {
    private BusinessObjectService businessObjectService;

    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }

    public void testLabelsAreInDataDictionary() {
        assertNotNull(AccountingLineRuleUtil.getChartLabel());
        assertNotNull(AccountingLineRuleUtil.getAccountLabel());
        assertNotNull(AccountingLineRuleUtil.getSubAccountLabel());
        assertNotNull(AccountingLineRuleUtil.getObjectCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getSubObjectCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getProjectCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getObjectTypeCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getObjectSubTypeCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getOrganizationCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getFundGroupCodeLabel());
        assertNotNull(AccountingLineRuleUtil.getSubFundGroupCodeLabel());
    }

    public void testIsValidAccount_valid() {
        testIsValidAccount(getAccountFromFixture("activeAccount"), null);
    }

    public void testIsValidAccount_null() {
        testIsValidAccount(null, KeyConstants.ERROR_EXISTENCE);
    }

    public void testIsValidAccount_closed() {
        testIsValidAccount(getAccountFromFixture("closedAccount"), KeyConstants.ERROR_DOCUMENT_ACCOUNT_CLOSED);
    }

    private void testIsValidAccount(Account account, String expectedErrorKey) {
        assertGlobalErrorMapEmpty();
        boolean actual = AccountingLineRuleUtil.isValidAccount(account, getDataDictionaryService().getDataDictionary());
        assertEquals("isValidAccount result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, expectedErrorKey);
        }
    }

    public void testHasRequiredOverrides_valid() {
        testHasRequiredOverrides(getAccountFromFixture("activeAccount"), AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_null() {
        testHasRequiredOverrides(null, AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_closed() {
        testHasRequiredOverrides(getAccountFromFixture("closedAccount"), AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_expired() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccount"), AccountingLineOverride.CODE.NONE, KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { getStringFromFixture("blExpiredAccountNumber"), "BL", getStringFromFixture("blUnexpiredContinuationAccountNumber") });
    }

    public void testHasRequiredOverrides_expiredContinuationsClosedAndExpired() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccountExpiredAndClosedContinuation"), AccountingLineOverride.CODE.NONE, KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { "fixture1", "BL", getStringFromFixture("blUnexpiredContinuationAccountNumber") });
    }

    public void testHasRequiredOverrides_expiredContinuationExpired() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccountExpiredAndOpenContinuation"), AccountingLineOverride.CODE.NONE, KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { "fixture1", "BL", getStringFromFixture("blUnexpiredContinuationAccountNumber") });
    }

    public void testHasRequiredOverrides_expiredNoContinuation() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccountNoContinuation"), AccountingLineOverride.CODE.NONE, KeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION, null);
    }

    public void testHasRequiredOverrides_expiredButOverridden() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccount"), AccountingLineOverride.CODE.EXPIRED_ACCOUNT, null, null);
    }

    public void testHasRequiredOverrides_expiredNoContinuationButOverridden() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccountNoContinuation"), AccountingLineOverride.CODE.EXPIRED_ACCOUNT, null, null);
    }

    public void testHasRequiredOverrides_expiredButMultipleOverridden() {
        testHasRequiredOverrides(getAccountFromFixture("expiredAccount"), AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, null, null);
    }

    private String getStringFromFixture(String fixtureName) {
        return (String) getFixtureEntry(fixtureName).createObject();
    }

    private Account getAccountFromFixture(String fixtureName) {
        return (Account) getFixtureEntry(fixtureName).createObject();
    }

    private ObjectCode getObjectCodeFromFixture(String fixtureName) {
        return (ObjectCode) getFixtureEntry(fixtureName).createObject();
    }

    // public void testHasRequiredOverrides_AccountPresenceBudgetedObject() {
    // testHasRequiredOverrides(getAccountWithPresenceControl(), getBudgetedObjectCode(),
    // AccountingLineOverride.CODE.NONE, null);
    // }
    //
    // public void testHasRequiredOverrides_AccountPresenceNonBudgetObject() {
    // testHasRequiredOverrides(getAccountWithPresenceControl(), getNonBudgetedObjectCode(),
    // AccountingLineOverride.CODE.NON_BUDGETED_OBJECT,
    // KeyConstants.ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE);
    // }

    public void testHasRequiredOverrides_NoAccountPresenceBudgetedObject() {
        testHasRequiredOverrides(getAccountWithoutPresenceControl(), getBudgetedObjectCode(), AccountingLineOverride.CODE.NONE, null);
    }

    public void testHasRequiredOverrides_NoAccountPresenceNonBudgetedObject() {
        testHasRequiredOverrides(getAccountWithoutPresenceControl(), getNonBudgetedObjectCode(), AccountingLineOverride.CODE.NONE, null);
    }

    public void testHasRequiredOverrides_NoAccountPresenceNonBudgetedObjectAccountExpired() {
        testHasRequiredOverrides(getAccountWithPresenceControlWithExpired(), getNonBudgetedObjectCode(), AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT, null);
    }

    public void testHasRequiredOverrides_closedAccountNonBudgetedObject() {
        // This account would require a non-budgeted override if it were not closed. But, the closed validation takes precedence.
        testHasRequiredOverrides(getAccountWithPresenceControlButClosed(), getNonBudgetedObjectCode(), AccountingLineOverride.CODE.NONE, null);
    }

    @SuppressWarnings("deprecation")
    private void testHasRequiredOverrides(Account account, String overrideCode, String expectedErrorKey, String[] expectedErrorParameters) {
        AccountingLine line = new SourceAccountingLine();
        line.setAccount(account);

        assertGlobalErrorMapEmpty();
        boolean actual = AccountingLineRuleUtil.hasRequiredOverrides(line, overrideCode);
        assertEquals("hasRequiredOverrides result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, expectedErrorKey, expectedErrorParameters);
        }
    }

    @SuppressWarnings("deprecation")
    private void testHasRequiredOverrides(Account account, ObjectCode objectCode, String overrideCode, String expectedErrorKey) {
        AccountingLine line = new SourceAccountingLine();
        line.setAccount(account);
        line.setObjectCode(objectCode);

        assertGlobalErrorMapEmpty();
        boolean actual = AccountingLineRuleUtil.hasRequiredOverrides(line, overrideCode);
        assertEquals("hasRequiredOverrides result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, expectedErrorKey);
        }
    }

    private Account getAccountWithPresenceControl() {
        Account account = getAccountFromFixture("accountPresenceAccount");
        return (Account) businessObjectService.retrieve(account);
    }

    private Account getAccountWithPresenceControlWithExpired() {
        Account account = getAccountFromFixture("accountPresenceAccountWithExpired");
        return (Account) businessObjectService.retrieve(account);
    }

    private Account getAccountWithPresenceControlButClosed() {
        Account account = getAccountFromFixture("accountPresenceAccountButClosed");
        return (Account) businessObjectService.retrieve(account);
    }

    private Account getAccountWithoutPresenceControl() {
        Account account = getAccountFromFixture("accountNonPresenceAccount");
        return (Account) businessObjectService.retrieve(account);
    }

    private ObjectCode getNonBudgetedObjectCode() {
        ObjectCode objectCode = getObjectCodeFromFixture("objectCodeNonBudgetedObjectCode");
        return (ObjectCode) businessObjectService.retrieve(objectCode);
    }

    private ObjectCode getBudgetedObjectCode() {
        ObjectCode objectCode = getObjectCodeFromFixture("objectCodeBudgetedObjectCode");
        return (ObjectCode) businessObjectService.retrieve(objectCode);
    }
}
