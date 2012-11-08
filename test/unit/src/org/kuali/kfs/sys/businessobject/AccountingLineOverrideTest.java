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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * This class tests AccountingLineOverride.
 */
@ConfigureContext
public class AccountingLineOverrideTest extends KualiTestBase {
    private static final AccountingLineOverride EXPIRED = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.EXPIRED_ACCOUNT);
    private static final AccountingLineOverride NONE = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.NONE);
    private static final AccountingLineOverride EXPIRED_AND_NON_FRINGE = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED);
    private static final AccountingLineOverride NON_FRINGE = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED);

    public void testValueOf_String_none() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.NONE);
        assertEquals(AccountingLineOverride.CODE.NONE, expired.getCode());
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }

    public void testValueOf_String_expiredAccount() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.EXPIRED_ACCOUNT);
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, expired.getCode());
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }

    public void testValueOf_String_nonBudgetedObject() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.NON_BUDGETED_OBJECT);
        assertEquals(AccountingLineOverride.CODE.NON_BUDGETED_OBJECT, expired.getCode());
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT));
    }

    public void testValueOf_String_double() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED);
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, expired.getCode());
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT));
    }

    public void testValueOf_String_invalid() {
        try {
            AccountingLineOverride.valueOf("foo");
            fail("got invalid code");
        }
        catch (IllegalArgumentException e) {
            // good
        }
    }

    public void testValueOf_String_null() {
        try {
            AccountingLineOverride.valueOf((String) null);
            fail("got null code");
        }
        catch (IllegalArgumentException e) {
            // good
        }
    }

    public void testValueOf_IntegerArray_none() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(new Integer[] {});
        assertEquals(AccountingLineOverride.CODE.NONE, expired.getCode());
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }

    public void testValueOf_IntegerArray_expiredAccount() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(new Integer[] { AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT });
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, expired.getCode());
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
    }

    public void testValueOf_IntegerArray_double() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(new Integer[] { AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT, AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED });
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, expired.getCode());
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT));
    }

    public void testValueOf_IntegerArray_doubleReverse() {
        AccountingLineOverride expired = AccountingLineOverride.valueOf(new Integer[] { AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED, AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT });
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, expired.getCode());
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT));
        assertEquals(true, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED));
        assertEquals(false, expired.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT));
    }

    public void testValueOf_IntegerArray_invalid() {
        try {
            AccountingLineOverride.valueOf(new Integer[] { AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT, AccountingLineOverride.COMPONENT.NON_FRINGE_ACCOUNT_USED });
            fail("got invalid components");
        }
        catch (IllegalArgumentException e) {
            // good
        }
    }

    public void testValueOf_IntegerArray_null() {
        try {
            AccountingLineOverride.valueOf((Integer[]) null);
            fail("got null components");
        }
        catch (NullPointerException e) {
            // good
        }
    }

    public void testIsValidCode_valid() {
        assertEquals(true, AccountingLineOverride.isValidCode(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_BUDGETED_OBJECT));
    }

    public void testIsValidCode_invalid() {
        assertEquals(false, AccountingLineOverride.isValidCode("foo"));
    }

    public void testIsValidCode_empty() {
        assertEquals(false, AccountingLineOverride.isValidCode(""));
    }

    public void testIsValidCode_blank() {
        assertEquals(false, AccountingLineOverride.isValidCode(" "));
    }

    public void testIsValidCode_null() {
        assertEquals(false, AccountingLineOverride.isValidCode(null));
    }

    public void testIsValidComponentSet_valid() {
        assertEquals(true, AccountingLineOverride.isValidComponentSet(new Integer[] { AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT, AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT }));
    }

    public void testIsValidComponentSet_invalid() {
        assertEquals(false, AccountingLineOverride.isValidComponentSet(new Integer[] { new Integer(4) }));
    }

    public void testIsValidComponentSet_empty() {
        assertEquals(true, AccountingLineOverride.isValidComponentSet(new Integer[] {}));
    }

    public void testIsValidComponentSet_null() {
        try {
            AccountingLineOverride.isValidComponentSet(null);
            fail("null valid check didn't throw an exception");
        }
        catch (NullPointerException e) {
            // good
        }
    }

    public void testMask_expiredToNone() {
        assertSame(NONE, EXPIRED.mask(NONE));
    }

    public void testMask_noneToExpired() {
        assertSame(NONE, NONE.mask(EXPIRED));
    }

    public void testMask_expiredAndNonFringeToExpired() {
        assertSame(EXPIRED, EXPIRED_AND_NON_FRINGE.mask(EXPIRED));
    }

    public void testMask_nonFringeToExpired() {
        assertSame(NONE, NON_FRINGE.mask(EXPIRED));
    }

    public void testPopulateFromInput_none() {
        AccountingLine line = new SourceAccountingLine();
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        AccountingLineOverride.populateFromInput(line);
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    public void testPopulateFromInput_expiredAccount() {
        AccountingLine line = new SourceAccountingLine();
        line.setAccountExpiredOverride(true);
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        AccountingLineOverride.populateFromInput(line);
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, line.getOverrideCode());
    }

    public void testPopulateFromInput_nonBudgetedObject() {
        AccountingLine line = new SourceAccountingLine();
        line.setObjectBudgetOverride(true);
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        AccountingLineOverride.populateFromInput(line);
        assertEquals(AccountingLineOverride.CODE.NON_BUDGETED_OBJECT, line.getOverrideCode());
    }

    public void testProcessForOutput_none() {
        AccountingLine line = new SourceAccountingLine();
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_unexpired() {
        AccountingLine line = new SourceAccountingLine();
        line.setAccount(getUnexpiredAccount());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_expired() {
        AccountingLine line = new SourceAccountingLine();
        line.setOverrideCode(AccountingLineOverride.CODE.EXPIRED_ACCOUNT);
        Account expiredAccount = getExpiredAccount();
        line.setAccount(expiredAccount);
        line.setChartOfAccountsCode(expiredAccount.getChartOfAccountsCode());
        line.setAccountNumber(expiredAccount.getAccountNumber());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(true, line.getAccountExpiredOverride());
        assertEquals(true, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_nonBudgetedObject() {
        AccountingLine line = new SourceAccountingLine();
        Account closedAccount = getClosedAccount();
        line.setAccount(closedAccount);
        line.setChartOfAccountsCode(closedAccount.getChartOfAccountsCode());
        line.setAccountNumber(closedAccount.getAccountNumber());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_alreadyExpired() {
        AccountingLine line = new SourceAccountingLine();
        line.setOverrideCode(AccountingLineOverride.CODE.EXPIRED_ACCOUNT);
        Account expiredAccount = getExpiredAccount();
        line.setAccount(expiredAccount);
        line.setChartOfAccountsCode(expiredAccount.getChartOfAccountsCode());
        line.setAccountNumber(expiredAccount.getAccountNumber());
        line.setAccountExpiredOverride(true);
        line.setAccountExpiredOverrideNeeded(true);
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(true, line.getAccountExpiredOverride());
        assertEquals(true, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_becomingExpired() {
        AccountingLine line = new SourceAccountingLine();
        Account expiredAccount = getExpiredAccount();
        line.setAccount(expiredAccount);
        line.setChartOfAccountsCode(expiredAccount.getChartOfAccountsCode());
        line.setAccountNumber(expiredAccount.getAccountNumber());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(true, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_becomingUnexpired() {
        AccountingLine line = new SourceAccountingLine();
        line.setOverrideCode(AccountingLineOverride.CODE.EXPIRED_ACCOUNT);
        line.setAccount(getUnexpiredAccount());
        line.setAccountExpiredOverride(true);
        line.setAccountExpiredOverrideNeeded(true);
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(true, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, line.getOverrideCode());
    }

    @SuppressWarnings("deprecation")
    public void testProcessForOutput_becomingClosed() {
        AccountingLine line = new SourceAccountingLine();
        Account closedAccount = getClosedAccount();
        line.setAccount(closedAccount);
        line.setChartOfAccountsCode(closedAccount.getChartOfAccountsCode());
        line.setAccountNumber(closedAccount.getAccountNumber());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        AccountingLineOverride.processForOutput(null,line);
        assertEquals(false, line.getAccountExpiredOverride());
        assertEquals(false, line.getAccountExpiredOverrideNeeded());
        assertEquals(AccountingLineOverride.CODE.NONE, line.getOverrideCode());
    }

    private Account getClosedAccount() {
        Account account = getExpiredAccount();
        account.setActive(false);
        return account;
    }

    private Account getExpiredAccount() {
        Account account = new Account();
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("9999999");
        account.setAccountExpirationDate(new Date(42)); // this account expired near the beginning of the era (1970)
        return account;
    }

    private Account getUnexpiredAccount() {
        Account account = new Account();
        account.setAccountExpirationDate(new Date(Long.MAX_VALUE / 2)); // this account expires far in the future
        return account;
    }
}
