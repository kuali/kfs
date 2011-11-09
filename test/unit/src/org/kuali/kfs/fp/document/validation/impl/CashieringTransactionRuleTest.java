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
package org.kuali.kfs.fp.document.validation.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashDrawerTest;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CoinDetailTest;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetailTest;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext(session = UserNameFixture.twatson)
public class CashieringTransactionRuleTest extends KualiTestBase {
    static final String CMST_CAMPUS = "KO";

    public void testMoneyInOutBalanceRule() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testMoneyInNoNegatives");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        // currency and currency
        CashieringTransaction transaction = cmDoc.getCurrentTransaction();
        resetTransaction(transaction);

        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));
        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        // checks and currency
        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        Check justEnoughCheck = new CheckBase();
        justEnoughCheck.setAmount(new KualiDecimal(3530.0));
        transaction.getMoneyInChecks().add(justEnoughCheck);
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        Check notEnoughCheck = new CheckBase();
        notEnoughCheck.setAmount(new KualiDecimal(3.0));
        transaction.getMoneyInChecks().add(notEnoughCheck);
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        Check tooMuchCheck = new CheckBase();
        tooMuchCheck.setAmount(new KualiDecimal(1500000.0));
        transaction.getMoneyInChecks().add(tooMuchCheck);
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        // misc advance and currency
        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        CashieringItemInProcess currAdvance = transaction.getNewItemInProcess();
        currAdvance.setItemAmount(new KualiDecimal(3530.0));
        currAdvance.setItemIdentifier(new Integer(27));
        currAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        currAdvance = transaction.getNewItemInProcess();
        currAdvance.setItemAmount(new KualiDecimal(30.0));
        currAdvance.setItemIdentifier(new Integer(27));
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        currAdvance = transaction.getNewItemInProcess();
        currAdvance.setItemAmount(new KualiDecimal(1000000.0));
        currAdvance.setItemIdentifier(new Integer(27));
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        // currency and misc advance
        resetTransaction(transaction);
        CashieringItemInProcess oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        transaction.getOpenItemsInProcess().add(oldAdvance);
        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        transaction.getOpenItemsInProcess().add(oldAdvance);
        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        transaction.getOpenItemsInProcess().add(oldAdvance);
        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail());
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        // checks and misc advance
        resetTransaction(transaction);
        oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(50.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        justEnoughCheck = new CheckBase();
        justEnoughCheck.setAmount(new KualiDecimal(50.0));
        transaction.getMoneyInChecks().add(justEnoughCheck);
        transaction.getOpenItemsInProcess().add(oldAdvance);
        assertTrue(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        transaction.getOpenItemsInProcess().add(oldAdvance);
        notEnoughCheck = new CheckBase();
        notEnoughCheck.setAmount(new KualiDecimal(3.0));
        transaction.getMoneyInChecks().add(notEnoughCheck);
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));

        resetTransaction(transaction);
        oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemRemainingAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(58));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        transaction.getOpenItemsInProcess().add(oldAdvance);
        tooMuchCheck = new CheckBase();
        tooMuchCheck.setAmount(new KualiDecimal(1500000.0));
        transaction.getMoneyInChecks().add(tooMuchCheck);
        assertFalse(rule.checkMoneyInMoneyOutBalance(transaction));
    }

    public void testMoneyInNoNegatives() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testMoneyInNoNegatives");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail());
        assertFalse("Hundred Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentHundredDollarAmount(new KualiDecimal(100));
        assertFalse("Fifty Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentFiftyDollarAmount(new KualiDecimal(100));
        assertFalse("Twenty Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentTwentyDollarAmount(new KualiDecimal(100));
        assertFalse("Ten Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentTenDollarAmount(new KualiDecimal(100));
        assertFalse("Five Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentFiveDollarAmount(new KualiDecimal(100));
        assertFalse("Two Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentTwoDollarAmount(new KualiDecimal(100));
        assertFalse("One Dollar", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCurrency().setFinancialDocumentOneDollarAmount(new KualiDecimal(100));
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail());
        assertFalse("Hundred Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentHundredCentAmount(new KualiDecimal(1));
        assertFalse("Fifty Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentFiftyCentAmount(new KualiDecimal(1));
        assertFalse("Twenty Five Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(1));
        assertFalse("Ten Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentTenCentAmount(new KualiDecimal(1));
        assertFalse("Five Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentFiveCentAmount(new KualiDecimal(1));
        assertFalse("One Cent", rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyInCoin().setFinancialDocumentOneCentAmount(new KualiDecimal(1));
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyInNoNegatives(cmDoc.getCurrentTransaction()));
    }

    public void testMoneyOutNoNegatives() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testMoneyOutNoNegatives");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        cmDoc.getCurrentTransaction().setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail());
        assertFalse("Hundred Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentHundredDollarAmount(new KualiDecimal(100));
        assertFalse("Fifty Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentFiftyDollarAmount(new KualiDecimal(100));
        assertFalse("Twenty Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTwentyDollarAmount(new KualiDecimal(100));
        assertFalse("Ten Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTenDollarAmount(new KualiDecimal(100));
        assertFalse("Five Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentFiveDollarAmount(new KualiDecimal(100));
        assertFalse("Two Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTwoDollarAmount(new KualiDecimal(100));
        assertFalse("One Dollar", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentOneDollarAmount(new KualiDecimal(100));
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail());
        assertFalse("Hundred Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentHundredCentAmount(new KualiDecimal(1));
        assertFalse("Fifty Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentFiftyCentAmount(new KualiDecimal(1));
        assertFalse("Twenty Five Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(1));
        assertFalse("Ten Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentTenCentAmount(new KualiDecimal(1));
        assertFalse("Five Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentFiveCentAmount(new KualiDecimal(1));
        assertFalse("One Cent", rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentOneCentAmount(new KualiDecimal(1));
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkMoneyOutNoNegatives(cmDoc.getCurrentTransaction()));

    }

    public void testCheckNewItemInProcessDoesNotExceedCashDrawer() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testCheckNewItemInProcessDoesNotExceedCashDrawer");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER);
        resetTransaction(cmDoc.getCurrentTransaction());
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemAmount(new KualiDecimal(100));
        assertTrue(rule.checkNewItemInProcessDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemAmount(new KualiDecimal(10000000));
        assertFalse(rule.checkNewItemInProcessDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ZERO_CASH_DRAWER);
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemAmount(new KualiDecimal(100));
        assertFalse(rule.checkNewItemInProcessDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
    }

    public void testCheckTransactionCheckTotalDoesNotExceedCashDrawer() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testCheckTransactionCheckTotalDoesNotExceedCashDrawer");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER);
        resetTransaction(cmDoc.getCurrentTransaction());
        Check smallCheck = new CheckBase();
        smallCheck.setAmount(new KualiDecimal(50.0));
        cmDoc.getCurrentTransaction().addCheck(smallCheck);
        assertTrue(rule.checkTransactionCheckTotalDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        Check largeCheck = new CheckBase();
        largeCheck.setAmount(new KualiDecimal(1000000000.0));
        cmDoc.getCurrentTransaction().addCheck(largeCheck);
        assertFalse(rule.checkTransactionCheckTotalDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ZERO_CASH_DRAWER);
        resetTransaction(cmDoc.getCurrentTransaction());
        cmDoc.getCurrentTransaction().addCheck(smallCheck);
        assertFalse(rule.checkTransactionCheckTotalDoesNotExceedCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
    }

    public void testCheckPaidBackItemInProcessDoesNotExceedTotal() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testCheckPaidBackItemInProcessDoesNotExceedTotal");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        CashieringItemInProcess oldAdvance = new CashieringItemInProcess();
        oldAdvance.setCurrentPayment(new KualiDecimal(3530.0));
        oldAdvance.setItemAmount(new KualiDecimal(10000.0));
        oldAdvance.setItemIdentifier(new Integer(82));
        oldAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        assertTrue(rule.checkPaidBackItemInProcessDoesNotExceedTotal(oldAdvance, 0));
        oldAdvance.setCurrentPayment(null);
        assertTrue(rule.checkPaidBackItemInProcessDoesNotExceedTotal(oldAdvance, 0));
        oldAdvance.setCurrentPayment(new KualiDecimal(20000.0));
        assertFalse(rule.checkPaidBackItemInProcessDoesNotExceedTotal(oldAdvance, 0));
    }

    public void testEnoughCashOnHand() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testEnoughCashOnHand");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        CurrencyDetail goodCurrency = CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail excessiveCurrency = CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail();
        CoinDetail goodCoin = CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail excessiveCoin = CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail();
        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ALL_SEVENS_CASH_DRAWER);
        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(goodCurrency);
        cmDoc.getCurrentTransaction().setMoneyOutCoin(goodCoin);
        assertTrue(rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCurrency(excessiveCurrency);
        assertFalse("Hundred Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentHundredDollarAmount(new KualiDecimal(100));
        assertFalse("Fifty Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentFiftyDollarAmount(new KualiDecimal(100));
        assertFalse("Twenty Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTwentyDollarAmount(new KualiDecimal(100));
        assertFalse("Ten Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTenDollarAmount(new KualiDecimal(100));
        assertFalse("Five Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentFiveDollarAmount(new KualiDecimal(100));
        assertFalse("Two Dollar", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentTwoDollarAmount(new KualiDecimal(100));
        assertFalse("One", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCurrency().setFinancialDocumentOneDollarAmount(new KualiDecimal(100));
        assertTrue("Dollars are good", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        cmDoc.getCurrentTransaction().setMoneyOutCoin(excessiveCoin);
        assertFalse("Hundred Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentHundredCentAmount(new KualiDecimal(1));
        assertFalse("Fifty Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentFiftyCentAmount(new KualiDecimal(1));
        assertFalse("Twenty Five Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(1));
        assertFalse("Ten Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentTenCentAmount(new KualiDecimal(1));
        assertFalse("Five Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentFiveCentAmount(new KualiDecimal(1));
        assertFalse("One Cent", rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getMoneyOutCoin().setFinancialDocumentOneCentAmount(new KualiDecimal(1));

        assertTrue(rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));

        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.ZERO_CASH_DRAWER);
        cmDoc.getCurrentTransaction().setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        cmDoc.getCurrentTransaction().setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        cmDoc.getCurrentTransaction().setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        cmDoc.getCurrentTransaction().setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertTrue(rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
        putFixtureDataIntoCashDrawer(cmDoc.getCashDrawer(), CashDrawerTest.CashDrawerAmountFixture.NULL_CASH_DRAWER);
        assertTrue(rule.checkEnoughCashForMoneyOut(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction()));
    }

    public void testAdvancesDoNotPayOffOtherAdvances() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testAdvancesDoNotPayOffOtherAdvances");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        assertTrue(rule.checkItemInProcessIsNotPayingOffItemInProcess(cmDoc.getCurrentTransaction()));
        CashieringItemInProcess openAdvance = new CashieringItemInProcess();
        openAdvance.setItemAmount(new KualiDecimal(10000.0));
        openAdvance.setItemIdentifier(new Integer(82));
        openAdvance.setCurrentPayment(new KualiDecimal(25.0));
        openAdvance.setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        cmDoc.getCurrentTransaction().getOpenItemsInProcess().add(openAdvance);
        assertTrue(rule.checkItemInProcessIsNotPayingOffItemInProcess(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemIdentifier(new Integer(80));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemAmount(new KualiDecimal(52));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemOpenDate(new Date(new GregorianCalendar().getTimeInMillis()));
        assertFalse(rule.checkItemInProcessIsNotPayingOffItemInProcess(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getOpenItemsInProcess().remove(0);
        assertTrue(rule.checkItemInProcessIsNotPayingOffItemInProcess(cmDoc.getCurrentTransaction()));
    }

    public void testNewAdvanceNotInFuture() {
        CashManagementDocument cmDoc = this.cashManagementDocumentFixture("testAdvancesDoNotPayOffOtherAdvances");
        CashManagementDocumentRule rule = new CashManagementDocumentRule();

        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemIdentifier(new Integer(80));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemAmount(new KualiDecimal(52));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemOpenDate(oneWeekAgo());
        assertTrue(rule.checkNewItemInProcessInPast(cmDoc.getCurrentTransaction()));
        cmDoc.getCurrentTransaction().getNewItemInProcess().setItemOpenDate(oneWeekFromNow());
        assertFalse(rule.checkNewItemInProcessInPast(cmDoc.getCurrentTransaction()));
    }

    private CashManagementDocument cashManagementDocumentFixture(String testName) {
        // create a new cash drawer
        CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS);

        CashManagementDocument cmDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS, "Document Created for " + testName, "CMD");
        return cmDoc;
    }

    private void putFixtureDataIntoCashDrawer(CashDrawer drawer, CashDrawerTest.CashDrawerAmountFixture fixture) {
        CashDrawer fixtureDrawer = fixture.convertToCashDrawer();
        drawer.setFinancialDocumentHundredDollarAmount(fixtureDrawer.getFinancialDocumentHundredDollarAmount());
        drawer.setFinancialDocumentFiftyDollarAmount(fixtureDrawer.getFinancialDocumentFiftyDollarAmount());
        drawer.setFinancialDocumentTwentyDollarAmount(fixtureDrawer.getFinancialDocumentTwentyDollarAmount());
        drawer.setFinancialDocumentTenDollarAmount(fixtureDrawer.getFinancialDocumentTenDollarAmount());
        drawer.setFinancialDocumentFiveDollarAmount(fixtureDrawer.getFinancialDocumentFiveDollarAmount());
        drawer.setFinancialDocumentTwoDollarAmount(fixtureDrawer.getFinancialDocumentTwoDollarAmount());
        drawer.setFinancialDocumentOneDollarAmount(fixtureDrawer.getFinancialDocumentOneDollarAmount());
        drawer.setFinancialDocumentOtherDollarAmount(fixtureDrawer.getFinancialDocumentOtherDollarAmount());
        drawer.setFinancialDocumentHundredCentAmount(fixtureDrawer.getFinancialDocumentHundredCentAmount());
        drawer.setFinancialDocumentFiftyCentAmount(fixtureDrawer.getFinancialDocumentFiftyCentAmount());
        drawer.setFinancialDocumentTwentyFiveCentAmount(fixtureDrawer.getFinancialDocumentTwentyFiveCentAmount());
        drawer.setFinancialDocumentTenCentAmount(fixtureDrawer.getFinancialDocumentTenCentAmount());
        drawer.setFinancialDocumentFiveCentAmount(fixtureDrawer.getFinancialDocumentFiveCentAmount());
        drawer.setFinancialDocumentOneCentAmount(fixtureDrawer.getFinancialDocumentOneCentAmount());
        drawer.setFinancialDocumentOtherCentAmount(fixtureDrawer.getFinancialDocumentOtherCentAmount());
    }

    private void resetTransaction(CashieringTransaction transaction) {
        transaction.setMoneyInCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyInCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        transaction.setMoneyOutCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        transaction.setMoneyOutCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        transaction.setMoneyInChecks(new ArrayList<Check>());
        transaction.setBaselineChecks(new ArrayList());
        transaction.getNewItemInProcess().setItemAmount(KualiDecimal.ZERO);
        transaction.getNewItemInProcess().setItemReducedAmount(KualiDecimal.ZERO);
        transaction.getNewItemInProcess().setItemRemainingAmount(KualiDecimal.ZERO);
        transaction.setOpenItemsInProcess(new ArrayList<CashieringItemInProcess>());
    }

    private Date oneWeekAgo() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, -7);
        return new Date(cal.getTimeInMillis());
    }

    private Date oneWeekFromNow() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 7);
        return new Date(cal.getTimeInMillis());
    }
}

