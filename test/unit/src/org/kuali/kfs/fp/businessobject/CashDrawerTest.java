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
package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CashDrawerTest extends KualiTestBase {
    public enum CashDrawerAmountFixture {
        GOOD_CASH_DRAWER(500.0, 250.0, 100.0, 50.0, 25.0, 10.0, 5.0, 5.0, 5.0, 2.5, 1.25, 0.5, 0.25, 0.05, 0.5), ALL_FIVES_CASH_DRAWER(500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00, 5.00), ALL_SEVENS_CASH_DRAWER(700.0, 700.0, 700.0, 700.0, 700.0, 700.0, 700.0, 700.0, 7.00, 7.00, 7.00, 7.00, 7.00, 7.00, 7.00), ZERO_CASH_DRAWER(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), NULL_CASH_DRAWER;

        private KualiDecimal hundredDollarAmount;
        private KualiDecimal fiftyDollarAmount;
        private KualiDecimal twentyDollarAmount;
        private KualiDecimal tenDollarAmount;
        private KualiDecimal fiveDollarAmount;
        private KualiDecimal twoDollarAmount;
        private KualiDecimal oneDollarAmount;
        private KualiDecimal otherDollarAmount;
        private KualiDecimal hundredCentAmount;
        private KualiDecimal fiftyCentAmount;
        private KualiDecimal twentyFiveCentAmount;
        private KualiDecimal tenCentAmount;
        private KualiDecimal fiveCentAmount;
        private KualiDecimal oneCentAmount;
        private KualiDecimal otherCentAmount;

        private CashDrawerAmountFixture() {
        }

        private CashDrawerAmountFixture(double hundredDollarAmount, double fiftyDollarAmount, double twentyDollarAmount, double tenDollarAmount, double fiveDollarAmount, double twoDollarAmount, double oneDollarAmount, double otherDollarAmount, double hundredCentAmount, double fiftyCentAmount, double twentyFiveCentAmount, double tenCentAmount, double fiveCentAmount, double oneCentAmount, double otherCentAmount) {
            this.hundredDollarAmount = new KualiDecimal(hundredDollarAmount);
            this.fiftyDollarAmount = new KualiDecimal(fiftyDollarAmount);
            this.twentyDollarAmount = new KualiDecimal(twentyDollarAmount);
            this.tenDollarAmount = new KualiDecimal(tenDollarAmount);
            this.fiveDollarAmount = new KualiDecimal(fiveDollarAmount);
            this.twoDollarAmount = new KualiDecimal(twoDollarAmount);
            this.oneDollarAmount = new KualiDecimal(oneDollarAmount);
            this.otherDollarAmount = new KualiDecimal(otherDollarAmount);
            this.hundredCentAmount = new KualiDecimal(hundredCentAmount);
            this.fiftyCentAmount = new KualiDecimal(fiftyCentAmount);
            this.twentyFiveCentAmount = new KualiDecimal(twentyFiveCentAmount);
            this.tenCentAmount = new KualiDecimal(tenCentAmount);
            this.fiveCentAmount = new KualiDecimal(fiveCentAmount);
            this.oneCentAmount = new KualiDecimal(oneCentAmount);
            this.otherCentAmount = new KualiDecimal(otherCentAmount);
        }

        public CashDrawer convertToCashDrawer() {
            CashDrawer drawer = new CashDrawer();
            drawer.setFinancialDocumentHundredDollarAmount(this.hundredDollarAmount);
            drawer.setFinancialDocumentFiftyDollarAmount(this.fiftyDollarAmount);
            drawer.setFinancialDocumentTwentyDollarAmount(this.twentyDollarAmount);
            drawer.setFinancialDocumentTenDollarAmount(this.tenDollarAmount);
            drawer.setFinancialDocumentFiveDollarAmount(this.fiveDollarAmount);
            drawer.setFinancialDocumentTwoDollarAmount(this.twoDollarAmount);
            drawer.setFinancialDocumentOneDollarAmount(this.oneDollarAmount);
            drawer.setFinancialDocumentOtherDollarAmount(this.otherDollarAmount);
            drawer.setFinancialDocumentHundredCentAmount(this.hundredCentAmount);
            drawer.setFinancialDocumentFiftyCentAmount(this.fiftyCentAmount);
            drawer.setFinancialDocumentTwentyFiveCentAmount(this.twentyFiveCentAmount);
            drawer.setFinancialDocumentTenCentAmount(this.tenCentAmount);
            drawer.setFinancialDocumentFiveCentAmount(this.fiveCentAmount);
            drawer.setFinancialDocumentOneCentAmount(this.oneCentAmount);
            drawer.setFinancialDocumentOtherCentAmount(this.otherCentAmount);
            return drawer;
        }
    }

    public enum CashDrawerCountFixture {
        GOOD_CASH_DRAWER(new Integer(5), new Integer(10), new Integer(25), new Integer(50), new Integer(100), new Integer(250), new Integer(500), new Integer(5), new Integer(10), new Integer(20), new Integer(50), new Integer(100), new Integer(500)), ALL_FIVES_CASH_DRAWER(new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5)), ZERO_CASH_DRAWER(new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)), NULL_CASH_DRAWER;

        private Integer hundredDollarCount;
        private Integer fiftyDollarCount;
        private Integer twentyDollarCount;
        private Integer tenDollarCount;
        private Integer fiveDollarCount;
        private Integer twoDollarCount;
        private Integer oneDollarCount;
        private Integer hundredCentCount;
        private Integer fiftyCentCount;
        private Integer twentyFiveCentCount;
        private Integer tenCentCount;
        private Integer fiveCentCount;
        private Integer oneCentCount;

        private CashDrawerCountFixture() {
        }

        private CashDrawerCountFixture(Integer hundredDollarCount, Integer fiftyDollarCount, Integer twentyDollarCount, Integer tenDollarCount, Integer fiveDollarCount, Integer twoDollarCount, Integer oneDollarCount, Integer hundredCentCount, Integer fiftyCentCount, Integer twentyFiveCentCount, Integer tenCentCount, Integer fiveCentCount, Integer oneCentCount) {
            this.hundredDollarCount = hundredDollarCount;
            this.fiftyDollarCount = fiftyDollarCount;
            this.twentyDollarCount = twentyDollarCount;
            this.tenDollarCount = tenDollarCount;
            this.fiveDollarCount = fiveDollarCount;
            this.twoDollarCount = twoDollarCount;
            this.oneDollarCount = oneDollarCount;
            this.hundredCentCount = hundredCentCount;
            this.fiftyCentCount = fiftyCentCount;
            this.twentyFiveCentCount = twentyFiveCentCount;
            this.tenCentCount = tenCentCount;
            this.fiveCentCount = fiveCentCount;
            this.oneCentCount = oneCentCount;
        }

        public CashDrawer convertToCashDrawer() {
            CashDrawer drawer = new CashDrawer();
            drawer.setHundredDollarCount(this.hundredDollarCount);
            drawer.setFiftyDollarCount(this.fiftyDollarCount);
            drawer.setTwentyDollarCount(this.twentyDollarCount);
            drawer.setTenDollarCount(this.tenDollarCount);
            drawer.setFiveDollarCount(this.fiveDollarCount);
            drawer.setTwoDollarCount(this.twoDollarCount);
            drawer.setOneDollarCount(this.oneDollarCount);
            drawer.setHundredCentCount(this.hundredCentCount);
            drawer.setFiftyCentCount(this.fiftyCentCount);
            drawer.setTwentyFiveCentCount(this.twentyFiveCentCount);
            drawer.setTenCentCount(this.tenCentCount);
            drawer.setFiveCentCount(this.fiveCentCount);
            drawer.setOneCentCount(this.oneCentCount);
            return drawer;
        }
    }

    public void testAmountToCount() {
        CashDrawer drawer = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        assertEquals(drawer.getHundredDollarCount(), new Integer(5));
        assertEquals(drawer.getFiftyDollarCount(), new Integer(10));
        assertEquals(drawer.getTwentyDollarCount(), new Integer(25));
        assertEquals(drawer.getTenDollarCount(), new Integer(50));
        assertEquals(drawer.getFiveDollarCount(), new Integer(100));
        assertEquals(drawer.getTwoDollarCount(), new Integer(250));
        assertEquals(drawer.getOneDollarCount(), new Integer(500));
        assertEquals(drawer.getHundredCentCount(), new Integer(5));
        assertEquals(drawer.getFiftyCentCount(), new Integer(10));
        assertEquals(drawer.getTwentyFiveCentCount(), new Integer(20));
        assertEquals(drawer.getTenCentCount(), new Integer(50));
        assertEquals(drawer.getFiveCentCount(), new Integer(100));
        assertEquals(drawer.getOneCentCount(), new Integer(500));
    }

    public void testCountToAmount() {
        CashDrawer drawer = CashDrawerCountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        assertEquals(drawer.getFinancialDocumentHundredDollarAmount(), new KualiDecimal(500.0));
        assertEquals(drawer.getFinancialDocumentFiftyDollarAmount(), new KualiDecimal(250.0));
        assertEquals(drawer.getFinancialDocumentTwentyDollarAmount(), new KualiDecimal(100.0));
        assertEquals(drawer.getFinancialDocumentTenDollarAmount(), new KualiDecimal(50.0));
        assertEquals(drawer.getFinancialDocumentFiveDollarAmount(), new KualiDecimal(25.0));
        assertEquals(drawer.getFinancialDocumentTwoDollarAmount(), new KualiDecimal(10.0));
        assertEquals(drawer.getFinancialDocumentOneDollarAmount(), new KualiDecimal(5.0));
        assertEquals(drawer.getFinancialDocumentHundredCentAmount(), new KualiDecimal(5.0));
        assertEquals(drawer.getFinancialDocumentFiftyCentAmount(), new KualiDecimal(2.5));
        assertEquals(drawer.getFinancialDocumentTwentyFiveCentAmount(), new KualiDecimal(1.25));
        assertEquals(drawer.getFinancialDocumentTenCentAmount(), new KualiDecimal(0.5));
        assertEquals(drawer.getFinancialDocumentFiveCentAmount(), new KualiDecimal(0.25));
        assertEquals(drawer.getFinancialDocumentOneCentAmount(), new KualiDecimal(0.05));
    }

    public void testCurrencyTotal() {
        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        assertEquals(zeroDrawer.getCurrencyTotalAmount(), KualiDecimal.ZERO);
        CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
        assertEquals(nullDrawer.getCurrencyTotalAmount(), KualiDecimal.ZERO);
        CashDrawer goodDrawer = CashDrawerAmountFixture.GOOD_CASH_DRAWER.convertToCashDrawer();
        assertEquals(goodDrawer.getCurrencyTotalAmount(), new KualiDecimal(945));
    }

    public void testCoinTotal() {
        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        assertEquals(zeroDrawer.getCoinTotalAmount(), KualiDecimal.ZERO);
        CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
        assertEquals(nullDrawer.getCoinTotalAmount(), KualiDecimal.ZERO);
        CashDrawer goodDrawer = CashDrawerAmountFixture.GOOD_CASH_DRAWER.convertToCashDrawer();
        assertEquals(goodDrawer.getCoinTotalAmount(), new KualiDecimal(10.05));
    }

    public void testDrawerTotal() {
        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        assertEquals(zeroDrawer.getTotalAmount(), KualiDecimal.ZERO);
        CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
        assertEquals(nullDrawer.getTotalAmount(), KualiDecimal.ZERO);
        CashDrawer goodDrawer = CashDrawerAmountFixture.GOOD_CASH_DRAWER.convertToCashDrawer();
        assertEquals(goodDrawer.getTotalAmount(), new KualiDecimal(955.05));
    }

    public void testAddCurrency() {
        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        zeroDrawer.addCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(zeroDrawer, CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());

        CashDrawer nullDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        nullDrawer.addCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(nullDrawer, CurrencyDetailTest.CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail());

        CashDrawer allFivesDrawerA = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerA.addCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerA, CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());

        CashDrawer allFivesDrawerB = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerB.addCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerB, CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());

        CashDrawer allFivesDrawerC = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerC.addCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerC, CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail());
    }

    public void testAddCoin() {
        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        zeroDrawer.addCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(zeroDrawer, CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
        nullDrawer.addCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(nullDrawer, CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer allFivesDrawerA = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerA.addCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerA, CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer allFivesDrawerB = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerB.addCoin(CoinDetailTest.CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerB, CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer allFivesDrawerC = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerC.addCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerC, CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail());
    }

    public void testSubtractCurrency() {
        boolean caught;

        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        zeroDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(zeroDrawer, CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Hundred Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Fifty Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Twenty Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Ten Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Five Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiveDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer Two Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiveDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwoDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Null Drawer One Dollar", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiveDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwoDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentOneDollarAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentOtherDollarAmount(KualiDecimal.ZERO);
            nullDrawer.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertFalse("Null Drawer now all zeroes", caught);

        CashDrawer allFivesDrawerA = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerA.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerA, CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());

        CashDrawer allFivesDrawerB = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerB.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerB, CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());

        CashDrawer allFivesDrawerC = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerC.removeCurrency(CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail());
        assertCurrencyAmountsEqual(allFivesDrawerC, CurrencyDetailTest.CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail());
        // test excessive currency (more currency requested than in drawer)

        CashDrawer allFivesDrawerD = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        CurrencyDetail excessiveDetail = CurrencyDetailTest.CurrencyDetailAmountFixture.ALL_TENS_AMOUNT.convertToCurrencyDetail();

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Hundred Dollar", caught);
        excessiveDetail.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Fifty Dollar", caught);
        excessiveDetail.setFinancialDocumentFiftyDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Twenty Dollar", caught);
        excessiveDetail.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Ten Dollar", caught);
        excessiveDetail.setFinancialDocumentTenDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Five Dollar", caught);
        excessiveDetail.setFinancialDocumentFiveDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Two Dollar", caught);
        excessiveDetail.setFinancialDocumentTwoDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("One Dollar", caught);
        excessiveDetail.setFinancialDocumentOneDollarAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCurrency(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertFalse("We should be good...", caught);
    }

    public void testSubtractCoin() {
        boolean caught;

        CashDrawer zeroDrawer = CashDrawerAmountFixture.ZERO_CASH_DRAWER.convertToCashDrawer();
        zeroDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(zeroDrawer, CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer hundred cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer fifty cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer twenty five cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer ten cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer five cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("null drawer one cent amount", caught);

        caught = false;
        try {
            CashDrawer nullDrawer = CashDrawerAmountFixture.NULL_CASH_DRAWER.convertToCashDrawer();
            nullDrawer.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTwentyFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentTenCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentFiveCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentOneCentAmount(KualiDecimal.ZERO);
            nullDrawer.setFinancialDocumentOtherCentAmount(KualiDecimal.ZERO);
            nullDrawer.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        }
        catch (IllegalArgumentException iae) {
            caught = true;
            iae.printStackTrace();
        }
        assertFalse("null drawer all is now zero", caught);

        CashDrawer allFivesDrawerA = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerA.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerA, CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer allFivesDrawerB = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerB.removeCoin(CoinDetailTest.CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerB, CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());

        CashDrawer allFivesDrawerC = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();
        allFivesDrawerC.removeCoin(CoinDetailTest.CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail());
        assertCoinAmountsEqual(allFivesDrawerC, CoinDetailTest.CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail());

        // test excessive coin (more coin requested than in drawer)
        CoinDetail excessiveDetail = CoinDetailTest.CoinDetailAmountFixture.ALL_TENS_COIN_AMOUNT.convertToCoinDetail();
        CashDrawer allFivesDrawerD = CashDrawerAmountFixture.ALL_FIVES_CASH_DRAWER.convertToCashDrawer();

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Hundred Cent", caught);
        excessiveDetail.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Fifty Cent", caught);
        excessiveDetail.setFinancialDocumentFiftyCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Twenty Five Cent", caught);
        excessiveDetail.setFinancialDocumentTwentyFiveCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Ten Cent", caught);
        excessiveDetail.setFinancialDocumentTenCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("Five Cent", caught);
        excessiveDetail.setFinancialDocumentFiveCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertTrue("One Cent", caught);
        excessiveDetail.setFinancialDocumentOneCentAmount(KualiDecimal.ZERO);

        caught = false;
        try {
            allFivesDrawerD.removeCoin(excessiveDetail);
        }
        catch (IllegalArgumentException iae) {
            caught = true;
        }
        assertFalse("We should be good....", caught);
    }

    private void assertCurrencyAmountsEqual(CashDrawer drawer, CurrencyDetail detail) {
        assertEquals(drawer.getFinancialDocumentHundredDollarAmount(), detail.getFinancialDocumentHundredDollarAmount());
        assertEquals(drawer.getFinancialDocumentFiftyDollarAmount(), detail.getFinancialDocumentFiftyDollarAmount());
        assertEquals(drawer.getFinancialDocumentTwentyDollarAmount(), detail.getFinancialDocumentTwentyDollarAmount());
        assertEquals(drawer.getFinancialDocumentTenDollarAmount(), detail.getFinancialDocumentTenDollarAmount());
        assertEquals(drawer.getFinancialDocumentFiveDollarAmount(), detail.getFinancialDocumentFiveDollarAmount());
        assertEquals(drawer.getFinancialDocumentTwoDollarAmount(), detail.getFinancialDocumentTwoDollarAmount());
        assertEquals(drawer.getFinancialDocumentOneDollarAmount(), detail.getFinancialDocumentOneDollarAmount());
    }

    private void assertCoinAmountsEqual(CashDrawer drawer, CoinDetail detail) {
        assertEquals(drawer.getFinancialDocumentHundredCentAmount(), detail.getFinancialDocumentHundredCentAmount());
        assertEquals(drawer.getFinancialDocumentFiftyCentAmount(), detail.getFinancialDocumentFiftyCentAmount());
        assertEquals(drawer.getFinancialDocumentTwentyFiveCentAmount(), detail.getFinancialDocumentTwentyFiveCentAmount());
        assertEquals(drawer.getFinancialDocumentTenCentAmount(), detail.getFinancialDocumentTenCentAmount());
        assertEquals(drawer.getFinancialDocumentFiveCentAmount(), detail.getFinancialDocumentFiveCentAmount());
        assertEquals(drawer.getFinancialDocumentOneCentAmount(), detail.getFinancialDocumentOneCentAmount());
    }
}
