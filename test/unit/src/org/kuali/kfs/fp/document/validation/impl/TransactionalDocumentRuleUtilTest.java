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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Class for unit testing the functionality of <code>{@link TransactionalDocumentRuleUtil}</code>
 */
@ConfigureContext
public class TransactionalDocumentRuleUtilTest extends KualiTestBase {

    private static final String DOES_NOT_MATTER = "doesNotMatter";

    private static long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;


    private final String ANNUAL_BALANCE_PERIOD_CODE = "AB";


    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////
    /**
     * Accessor method to </code>errorPropertyName</code>
     * 
     * @return String
     */
    protected String getErrorPropertyName() {
        return KFSConstants.GLOBAL_ERRORS;
    }

    /**
     * Fixture accessor method to get <code>{@link String}</code> serialization instance of an active balance type.
     * 
     * @return String
     */
    protected String getActiveBalanceType() {
        return KFSConstants.BALANCE_TYPE_ACTUAL;
    }

    /**
     * Fixture accessor method to get <code>{@link String}</code> serialization instance of an inactive balance type.
     * 
     * @return String
     */
    protected String getInactiveBalanceType() {
        return KFSConstants.BALANCE_TYPE_ACTUAL;
    }


    /**
     * Fixture accessor method for the Annual Balance <code>{@link AccountingPeriod}</code>
     * 
     * @return AccountingPeriod
     */
    public AccountingPeriod getAnnualBalanceAccountingPeriod() {
        return SpringContext.getBean(AccountingPeriodService.class).getByPeriod(ANNUAL_BALANCE_PERIOD_CODE, TestUtils.getFiscalYearForTesting());
    }

    /**
     * Fixture accessor method for a closed <code>{@link AccountingPeriod}</code> instance.
     * 
     * @return AccountingPeriod
     */
    protected AccountingPeriod getClosedAccountingPeriod() {
        return null;
    }

    /**
     * Fixture accessor method for getting a <code>{@link java.sql.Date}</code> instance that is in the past.
     * 
     * @return Timestamp
     */
    private java.sql.Date getSqlDateYesterday() {
        return new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime() - ONE_DAY_MILLIS);
    }

    /**
     * Fixture accessor method for getting a <code>{@link java.sql.Date}</code> instance that is in the future.
     */
    private java.sql.Date getSqlDateTomorrow() {
        return new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime() + ONE_DAY_MILLIS);
    }

    /**
     * @return today's java.sql.Date
     */
    private java.sql.Date getSqlDateToday() {
        return new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
    }

    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here //
    // /////////////////////////////////////////////////////////////////////////


    // /////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////
    /**
     * test the <code>isValidBalanceType()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType
     */
    public void testIsValidBalanceType_Active() {
        testIsValidBalanceType(getActiveBalanceType(), true);
    }

    /**
     * test the <code>isValidBalanceType()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType
     */
    public void testIsValidBalanceType_Inactive() {
        testIsValidBalanceType(getInactiveBalanceType(), true);
    }

    /**
     * test the <code>isValidBalanceType()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType
     */
    public void testIsValidBalanceType_Null() {
        testIsValidBalanceType(null, false);
    }

    /**
     * test the <code>isValidBalanceType()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @param btStr
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType
     */
    protected void testIsValidBalanceType(String btStr, boolean expected) {
        BalanceType balanceType = null;

        if (btStr != null) {
            balanceType = SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(btStr);
        }
        assertGlobalMessageMapEmpty();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        boolean result = accountingDocumentRuleUtil.isValidBalanceType(balanceType, "code");
        if (expected) {
            assertGlobalMessageMapEmpty();
        }
        assertEquals("result", expected, result);
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod
     */
    // @RelatesTo(JiraIssue.KULRNE4926)
    public void testIsValidOpenAccountingPeriod_Open() {
        testIsValidOpenAccountingPeriod(getAnnualBalanceAccountingPeriod(), true);
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod
     */
    public void pendingTestIsValidOpenAccountingPeriod_Closed() {
        testIsValidOpenAccountingPeriod(getClosedAccountingPeriod(), false);
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod
     */
    // @RelatesTo(JiraIssue.KULRNE4926)
    public void testIsValidOpenAccountingPeriod_Null() {
        testIsValidOpenAccountingPeriod(null, false);
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @param period
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod
     */
    protected void testIsValidOpenAccountingPeriod(AccountingPeriod period, boolean expected) {
        assertGlobalMessageMapEmpty();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        boolean result = accountingDocumentRuleUtil.isValidOpenAccountingPeriod(period, JournalVoucherDocument.class, KFSPropertyConstants.ACCOUNTING_PERIOD, DOES_NOT_MATTER);
        if (expected) {
            assertGlobalMessageMapEmpty();
        }
        assertEquals("result", expected, result);
    }

    /**
     * test the <code>isValidReversalDate()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate
     */
    public void testIsValidReversalDate_Null() {
        testIsValidReversalDate(null, true);
    }

    /**
     * test the <code>isValidReversalDate()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate
     */
    public void testIsValidReversalDate_Past() {
        testIsValidReversalDate(getSqlDateYesterday(), false);
    }

    /**
     * test the <code>isValidReversalDate()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate
     */
    public void testIsValidReversalDate_Future() {
        testIsValidReversalDate(getSqlDateTomorrow(), true);
    }

    public void testIsValidReversalDate_Today() {
        testIsValidReversalDate(getSqlDateToday(), true);
    }

    /**
     * test the <code>isValidReversalDate()</code> method of <code>{@link TransactionalDocumentRuleUtil}</code>
     * 
     * @param reversalDate
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate
     */
    protected void testIsValidReversalDate(java.sql.Date reversalDate, boolean expected) {
        assertGlobalMessageMapEmpty();
        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        boolean result = accountingDocumentRuleUtil.isValidReversalDate(reversalDate, getErrorPropertyName());
        if (expected) {
            assertGlobalMessageMapEmpty();
        }
        assertEquals("result", expected, result);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Test Methods End Here //
    // /////////////////////////////////////////////////////////////////////////
}
