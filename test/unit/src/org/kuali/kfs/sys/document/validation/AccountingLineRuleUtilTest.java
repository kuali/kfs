/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import static org.kuali.test.fixtures.AccountFixture.ACCOUNT_NON_PRESENCE_ACCOUNT;
import static org.kuali.test.fixtures.AccountFixture.ACCOUNT_PRESENCE_ACCOUNT_BUT_CLOSED;
import static org.kuali.test.fixtures.AccountFixture.ACCOUNT_PRESENCE_ACCOUNT_WITH_EXPIRED;
import static org.kuali.test.fixtures.AccountFixture.ACTIVE_ACCOUNT;
import static org.kuali.test.fixtures.AccountFixture.CLOSED_ACCOUNT;
import static org.kuali.test.fixtures.AccountFixture.EXPIRIED_ACCOUNT;
import static org.kuali.test.fixtures.AccountFixture.EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION;
import static org.kuali.test.fixtures.AccountFixture.EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION;
import static org.kuali.test.fixtures.AccountFixture.EXPIRIED_ACCOUNT_NO_CONTINUATION;
import static org.kuali.test.fixtures.ObjectCodeFixture.OBJECT_CODE_BUDGETED_OBJECT_CODE;
import static org.kuali.test.fixtures.ObjectCodeFixture.OBJECT_CODE_NON_BUDGET_OBJECT_CODE;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapContains;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.test.ConfigureContext;

/**
 * This class tests some methods of AccountingLineRuleUtil.
 */
@ConfigureContext
public class AccountingLineRuleUtilTest extends KualiTestBase {

    public void testLabelsAreInDataDictionary() {
        AccountingLineRuleHelperService accountingLineRuleUtil = SpringContext.getBean(AccountingLineRuleHelperService.class);
        assertNotNull(accountingLineRuleUtil.getChartLabel());
        assertNotNull(accountingLineRuleUtil.getAccountLabel());
        assertNotNull(accountingLineRuleUtil.getSubAccountLabel());
        assertNotNull(accountingLineRuleUtil.getObjectCodeLabel());
        assertNotNull(accountingLineRuleUtil.getSubObjectCodeLabel());
        assertNotNull(accountingLineRuleUtil.getProjectCodeLabel());
        assertNotNull(accountingLineRuleUtil.getObjectTypeCodeLabel());
        assertNotNull(accountingLineRuleUtil.getObjectSubTypeCodeLabel());
        assertNotNull(accountingLineRuleUtil.getOrganizationCodeLabel());
        assertNotNull(accountingLineRuleUtil.getFundGroupCodeLabel());
        assertNotNull(accountingLineRuleUtil.getSubFundGroupCodeLabel());
    }

    public void testIsValidAccount_valid() {
        testIsValidAccount(ACTIVE_ACCOUNT.createAccount(), null);
    }

    public void testIsValidAccount_null() {
        testIsValidAccount(null, KFSKeyConstants.ERROR_EXISTENCE);
    }

    public void testIsValidAccount_closed() {
        testIsValidAccount(CLOSED_ACCOUNT.createAccount(), KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_CLOSED);
    }

    private void testIsValidAccount(Account account, String expectedErrorKey) {
        assertGlobalErrorMapEmpty();
        AccountingLineRuleHelperService accountingLineRuleUtil = SpringContext.getBean(AccountingLineRuleHelperService.class);
        boolean actual = accountingLineRuleUtil.isValidAccount(account, SpringContext.getBean(DataDictionaryService.class).getDataDictionary());
        assertEquals("isValidAccount result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, expectedErrorKey);
        }
    }

    public void testHasRequiredOverrides_valid() {
        testHasRequiredOverrides(ACTIVE_ACCOUNT.createAccount(), AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_null() {
        testHasRequiredOverrides(null, AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_closed() {
        testHasRequiredOverrides(CLOSED_ACCOUNT.createAccount(), AccountingLineOverride.CODE.NONE, null, null);
    }

    public void testHasRequiredOverrides_expired() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT.createAccount(), AccountingLineOverride.CODE.NONE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { EXPIRIED_ACCOUNT.accountNumber, EXPIRIED_ACCOUNT.chartOfAccountsCode, EXPIRIED_ACCOUNT.continuationAccountNumber });
    }

    public void testHasRequiredOverrides_expiredContinuationsClosedAndExpired() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION.createAccount(), AccountingLineOverride.CODE.NONE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION.accountNumber, EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION.continuationFinChrtOfAcctCd, EXPIRIED_ACCOUNT.continuationAccountNumber });
    }

    public void testHasRequiredOverrides_expiredContinuationExpired() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION.createAccount(), AccountingLineOverride.CODE.NONE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION.accountNumber, EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION.continuationFinChrtOfAcctCd, EXPIRIED_ACCOUNT.continuationAccountNumber });
    }

    public void testHasRequiredOverrides_expiredNoContinuation() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT_NO_CONTINUATION.createAccount(), AccountingLineOverride.CODE.NONE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION, null);
    }

    public void testHasRequiredOverrides_expiredButOverridden() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT.createAccount(), AccountingLineOverride.CODE.EXPIRED_ACCOUNT, null, null);
    }

    public void testHasRequiredOverrides_expiredNoContinuationButOverridden() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT_NO_CONTINUATION.createAccount(), AccountingLineOverride.CODE.EXPIRED_ACCOUNT, null, null);
    }

    public void testHasRequiredOverrides_expiredButMultipleOverridden() {
        testHasRequiredOverrides(EXPIRIED_ACCOUNT.createAccount(), AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, null, null);
    }

    // public void testHasRequiredOverrides_AccountPresenceBudgetedObject() {
    // testHasRequiredOverrides(ACCOUNT_PRESENCE_ACCOUNT.createAccount(businessObjectService), getBudgetedObjectCode(),
    // AccountingLineOverride.CODE.NONE, null);
    // }
    //
    // public void testHasRequiredOverrides_AccountPresenceNonBudgetObject() {
    // testHasRequiredOverrides(ACCOUNT_PRESENCE_ACCOUNT.createAccount(businessObjectService), getNonBudgetedObjectCode(),
    // AccountingLineOverride.CODE.NON_BUDGETED_OBJECT,
    // KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE);
    // }

    public void testHasRequiredOverrides_NoAccountPresenceBudgetedObject() {
        testHasRequiredOverrides(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_BUDGETED_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class)), AccountingLineOverride.CODE.NONE, null);
    }

    public void testHasRequiredOverrides_NoAccountPresenceNonBudgetedObject() {
        testHasRequiredOverrides(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class)), AccountingLineOverride.CODE.NONE, null);
    }

    public void testHasRequiredOverrides_NoAccountPresenceNonBudgetedObjectAccountExpired() {
        testHasRequiredOverrides(ACCOUNT_PRESENCE_ACCOUNT_WITH_EXPIRED.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class)), AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT, null);
    }

    public void testHasRequiredOverrides_closedAccountNonBudgetedObject() {
        // This account would require a non-budgeted override if it were not closed. But, the closed validation takes precedence.
        testHasRequiredOverrides(ACCOUNT_PRESENCE_ACCOUNT_BUT_CLOSED.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class)), AccountingLineOverride.CODE.NONE, null);
    }

    @SuppressWarnings("deprecation")
    private void testHasRequiredOverrides(Account account, String overrideCode, String expectedErrorKey, String[] expectedErrorParameters) {
        AccountingLine line = new SourceAccountingLine();
        line.setAccount(account);

        assertGlobalErrorMapEmpty();
        AccountingLineRuleHelperService accountingLineRuleUtil = SpringContext.getBean(AccountingLineRuleHelperService.class);
        boolean actual = accountingLineRuleUtil.hasRequiredOverrides(line, overrideCode);
        assertEquals("hasRequiredOverrides result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, expectedErrorKey, expectedErrorParameters);
        }
    }

    @SuppressWarnings("deprecation")
    private void testHasRequiredOverrides(Account account, ObjectCode objectCode, String overrideCode, String expectedErrorKey) {
        AccountingLine line = new SourceAccountingLine();
        line.setAccount(account);
        line.setObjectCode(objectCode);

        assertGlobalErrorMapEmpty();
        AccountingLineRuleHelperService accountingLineRuleUtil = SpringContext.getBean(AccountingLineRuleHelperService.class);
        boolean actual = accountingLineRuleUtil.hasRequiredOverrides(line, overrideCode);
        assertEquals("hasRequiredOverrides result", expectedErrorKey == null, actual);
        if (expectedErrorKey == null) {
            assertGlobalErrorMapEmpty();
        }
        else {
            assertGlobalErrorMapContains(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, expectedErrorKey);
        }
    }

}
