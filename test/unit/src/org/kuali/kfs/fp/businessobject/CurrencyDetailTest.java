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

public class CurrencyDetailTest extends KualiTestBase {
    public enum CurrencyDetailAmountFixture {
        GOOD_POSITIVE_AMOUNT(500.0, 250.0, 100.0, 50.0, 25.0, 10.0, 5.0), BAD_POSITIVE_AMOUNT(367.0, 367.0, 367.0, 367.0, 367.0, 367.0, 367.5), ALL_FIVES_AMOUNT(500.0, 500.0, 500.0, 500.0, 500.0, 500.0, 500.0), NULL_AMOUNT, ZERO_AMOUNT(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), NEGATIVE_AMOUNT(-500.0, -500.0, -500.0, -500.0, -500.0, -500.0, -500.0), ALL_TENS_AMOUNT(1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0);

        private KualiDecimal hundredDollarAmount;
        private KualiDecimal fiftyDollarAmount;
        private KualiDecimal twentyDollarAmount;
        private KualiDecimal tenDollarAmount;
        private KualiDecimal fiveDollarAmount;
        private KualiDecimal twoDollarAmount;
        private KualiDecimal oneDollarAmount;

        private CurrencyDetailAmountFixture() {
        }

        private CurrencyDetailAmountFixture(double hundredDollarAmount, double fiftyDollarAmount, double twentyDollarAmount, double tenDollarAmount, double fiveDollarAmount, double twoDollarAmount, double oneDollarAmount) {
            this.hundredDollarAmount = new KualiDecimal(hundredDollarAmount);
            this.fiftyDollarAmount = new KualiDecimal(fiftyDollarAmount);
            this.twentyDollarAmount = new KualiDecimal(twentyDollarAmount);
            this.tenDollarAmount = new KualiDecimal(tenDollarAmount);
            this.fiveDollarAmount = new KualiDecimal(fiveDollarAmount);
            this.twoDollarAmount = new KualiDecimal(twoDollarAmount);
            this.oneDollarAmount = new KualiDecimal(oneDollarAmount);
        }

        public CurrencyDetail convertToCurrencyDetail() {
            CurrencyDetail detail = new CurrencyDetail();
            detail.setFinancialDocumentHundredDollarAmount(this.hundredDollarAmount);
            detail.setFinancialDocumentFiftyDollarAmount(this.fiftyDollarAmount);
            detail.setFinancialDocumentTwentyDollarAmount(this.twentyDollarAmount);
            detail.setFinancialDocumentTenDollarAmount(this.tenDollarAmount);
            detail.setFinancialDocumentFiveDollarAmount(this.fiveDollarAmount);
            detail.setFinancialDocumentTwoDollarAmount(this.twoDollarAmount);
            detail.setFinancialDocumentOneDollarAmount(this.oneDollarAmount);

            return detail;
        }
    }

    public enum CurrencyDetailCountFixture {
        GOOD_POSITIVE_COUNT(new Integer(5), new Integer(10), new Integer(25), new Integer(50), new Integer(100), new Integer(250), new Integer(500)), ALL_FIVE_HUNDREDS_COUNT(new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5)), NULL_COUNT, ZERO_COUNT(new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)), NEGATIVE_COUNT(new Integer(-5), new Integer(-5), new Integer(-5), new Integer(-5), new Integer(-5), new Integer(-5), new Integer(-5));

        private Integer hundredDollarCount;
        private Integer fiftyDollarCount;
        private Integer twentyDollarCount;
        private Integer tenDollarCount;
        private Integer fiveDollarCount;
        private Integer twoDollarCount;
        private Integer oneDollarCount;

        private CurrencyDetailCountFixture() {
        }

        private CurrencyDetailCountFixture(Integer hundredDollarCount, Integer fiftyDollarCount, Integer twentyDollarCount, Integer tenDollarCount, Integer fiveDollarCount, Integer twoDollarCount, Integer oneDollarCount) {
            this.hundredDollarCount = hundredDollarCount;
            this.fiftyDollarCount = fiftyDollarCount;
            this.twentyDollarCount = twentyDollarCount;
            this.tenDollarCount = tenDollarCount;
            this.fiveDollarCount = fiveDollarCount;
            this.twoDollarCount = twoDollarCount;
            this.oneDollarCount = oneDollarCount;
        }

        public CurrencyDetail convertToCurrencyDetail() {
            CurrencyDetail detail = new CurrencyDetail();
            detail.setHundredDollarCount(this.hundredDollarCount);
            detail.setFiftyDollarCount(this.fiftyDollarCount);
            detail.setTwentyDollarCount(this.twentyDollarCount);
            detail.setTenDollarCount(this.tenDollarCount);
            detail.setFiveDollarCount(this.fiveDollarCount);
            detail.setTwoDollarCount(this.twoDollarCount);
            detail.setOneDollarCount(this.oneDollarCount);

            return detail;
        }
    }

    public void testAmountToCountConversion() {
        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();
        assertEquals(goodAmount.getHundredDollarCount(), new Integer(5));
        assertEquals(goodAmount.getFiftyDollarCount(), new Integer(5));
        assertEquals(goodAmount.getTwentyDollarCount(), new Integer(5));
        assertEquals(goodAmount.getTenDollarCount(), new Integer(5));
        assertEquals(goodAmount.getFiveDollarCount(), new Integer(5));
        assertEquals(goodAmount.getTwoDollarCount(), new Integer(5));
        assertEquals(goodAmount.getOneDollarCount(), new Integer(5));
    }

    public void testCountToAmountConversion() {
        CurrencyDetail goodCount = CurrencyDetailCountFixture.GOOD_POSITIVE_COUNT.convertToCurrencyDetail();
        assertEquals(goodCount.getFinancialDocumentHundredDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentFiftyDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentTwentyDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentTenDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentFiveDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentTwoDollarAmount(), new KualiDecimal(500));
        assertEquals(goodCount.getFinancialDocumentOneDollarAmount(), new KualiDecimal(500));
    }

    public void testZeroOutAmounts() {
        CurrencyDetail zeroAmountControl = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();

        zeroAmount.zeroOutAmounts();
        assertDetailAmountsEqual(zeroAmount, zeroAmountControl);
        nullAmount.zeroOutAmounts();
        assertDetailAmountsEqual(nullAmount, zeroAmountControl);
        goodAmount.zeroOutAmounts();
        assertDetailAmountsEqual(goodAmount, zeroAmountControl);
        negativeAmount.zeroOutAmounts();
        assertDetailAmountsEqual(negativeAmount, zeroAmountControl);

        CurrencyDetail goodCount = CurrencyDetailCountFixture.GOOD_POSITIVE_COUNT.convertToCurrencyDetail();
        CurrencyDetail zeroCount = CurrencyDetailCountFixture.ZERO_COUNT.convertToCurrencyDetail();
        CurrencyDetail negativeCount = CurrencyDetailCountFixture.NEGATIVE_COUNT.convertToCurrencyDetail();
        CurrencyDetail nullCount = CurrencyDetailCountFixture.NULL_COUNT.convertToCurrencyDetail();

        zeroCount.zeroOutAmounts();
        assertDetailAmountsEqual(zeroCount, zeroAmountControl);
        nullCount.zeroOutAmounts();
        assertDetailAmountsEqual(nullCount, zeroAmountControl);
        goodCount.zeroOutAmounts();
        assertDetailAmountsEqual(goodCount, zeroAmountControl);
        negativeCount.zeroOutAmounts();
        assertDetailAmountsEqual(negativeCount, zeroAmountControl);
    }

    public void testZeroOutUnpopulatedAmounts() {
        CurrencyDetail zeroAmountControl = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();

        zeroAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(zeroAmount, zeroAmountControl);
        nullAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(nullAmount, zeroAmountControl);
        goodAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsNotEqual(goodAmount, zeroAmountControl);

        goodAmount.setFinancialDocumentHundredDollarAmount(null);
        goodAmount.setFinancialDocumentTwentyDollarAmount(null);
        goodAmount.setFinancialDocumentFiveDollarAmount(null);
        goodAmount.setFinancialDocumentTwoDollarAmount(null);
        CurrencyDetail semiPopulatedAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();
        semiPopulatedAmount.setFinancialDocumentHundredDollarAmount(KualiDecimal.ZERO);
        semiPopulatedAmount.setFinancialDocumentTwentyDollarAmount(KualiDecimal.ZERO);
        semiPopulatedAmount.setFinancialDocumentFiveDollarAmount(KualiDecimal.ZERO);
        semiPopulatedAmount.setFinancialDocumentTwoDollarAmount(KualiDecimal.ZERO);
        goodAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(goodAmount, semiPopulatedAmount);

        CurrencyDetail goodCount = CurrencyDetailCountFixture.GOOD_POSITIVE_COUNT.convertToCurrencyDetail();
        CurrencyDetail zeroCount = CurrencyDetailCountFixture.ZERO_COUNT.convertToCurrencyDetail();
        CurrencyDetail nullCount = CurrencyDetailCountFixture.NULL_COUNT.convertToCurrencyDetail();

        zeroCount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(zeroCount, zeroAmountControl);
        nullCount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(nullCount, zeroAmountControl);
        goodCount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsNotEqual(goodCount, zeroAmountControl);
    }

    public void testAdd() {
        CurrencyDetail zeroAmountControl = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmountControl = CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail negativeAmountControl = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();

        zeroAmount.add(zeroAmountControl);
        assertDetailAmountsEqual(zeroAmount, zeroAmountControl);
        nullAmount.add(zeroAmountControl);
        assertDetailAmountsEqual(nullAmount, zeroAmountControl);
        goodAmount.add(zeroAmountControl);
        assertDetailAmountsEqual(goodAmount, goodAmountControl);
        negativeAmount.add(zeroAmountControl);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);
        goodAmount.add(negativeAmount);
        assertDetailAmountsEqual(goodAmount, zeroAmountControl);
    }

    public void testSubtract() {
        CurrencyDetail zeroAmountControl = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmountControl = CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail negativeAmountControl = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();

        zeroAmount.subtract(zeroAmountControl);
        assertDetailAmountsEqual(zeroAmount, zeroAmountControl);
        nullAmount.subtract(zeroAmountControl);
        assertDetailAmountsEqual(nullAmount, zeroAmountControl);
        goodAmount.subtract(zeroAmountControl);
        assertDetailAmountsEqual(goodAmount, goodAmountControl);
        negativeAmount.subtract(zeroAmountControl);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);

        CurrencyDetail doublePositives = new CurrencyDetail();
        doublePositives.setFinancialDocumentHundredDollarAmount(goodAmount.getFinancialDocumentHundredDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentFiftyDollarAmount(goodAmount.getFinancialDocumentFiftyDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentTwentyDollarAmount(goodAmount.getFinancialDocumentTwentyDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentTenDollarAmount(goodAmount.getFinancialDocumentTenDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentFiveDollarAmount(goodAmount.getFinancialDocumentFiveDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentTwoDollarAmount(goodAmount.getFinancialDocumentTwoDollarAmount().multiply(new KualiDecimal(2.0)));
        doublePositives.setFinancialDocumentOneDollarAmount(goodAmount.getFinancialDocumentOneDollarAmount().multiply(new KualiDecimal(2.0)));
        goodAmount.subtract(negativeAmount);
        assertDetailAmountsEqual(goodAmount, doublePositives);

        negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();
        goodAmount = CurrencyDetailAmountFixture.ALL_FIVES_AMOUNT.convertToCurrencyDetail();
        CurrencyDetail doubleNegatives = new CurrencyDetail();
        doubleNegatives.setFinancialDocumentHundredDollarAmount(negativeAmount.getFinancialDocumentHundredDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentFiftyDollarAmount(negativeAmount.getFinancialDocumentFiftyDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentTwentyDollarAmount(negativeAmount.getFinancialDocumentTwentyDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentTenDollarAmount(negativeAmount.getFinancialDocumentTenDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentFiveDollarAmount(negativeAmount.getFinancialDocumentFiveDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentTwoDollarAmount(negativeAmount.getFinancialDocumentTwoDollarAmount().multiply(new KualiDecimal(2.0)));
        doubleNegatives.setFinancialDocumentOneDollarAmount(negativeAmount.getFinancialDocumentOneDollarAmount().multiply(new KualiDecimal(2.0)));
        negativeAmount.subtract(goodAmount);
        assertDetailAmountsEqual(negativeAmount, doubleNegatives);
    }

    public void testTotal() {
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        assertEquals(zeroAmount.getTotalAmount(), new KualiDecimal(0.0));
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        assertEquals(nullAmount.getTotalAmount(), new KualiDecimal(0.0));

        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();
        assertEquals(goodAmount.getTotalAmount(), new KualiDecimal(940.0));
        CurrencyDetail negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();
        assertEquals(negativeAmount.getTotalAmount(), new KualiDecimal(-3500.0));
    }

    public void testIsEmpty() {
        CurrencyDetail zeroAmount = CurrencyDetailAmountFixture.ZERO_AMOUNT.convertToCurrencyDetail();
        assertTrue(zeroAmount.isEmpty());
        CurrencyDetail nullAmount = CurrencyDetailAmountFixture.NULL_AMOUNT.convertToCurrencyDetail();
        assertTrue(nullAmount.isEmpty());

        CurrencyDetail goodAmount = CurrencyDetailAmountFixture.GOOD_POSITIVE_AMOUNT.convertToCurrencyDetail();
        assertFalse(goodAmount.isEmpty());
        CurrencyDetail negativeAmount = CurrencyDetailAmountFixture.NEGATIVE_AMOUNT.convertToCurrencyDetail();
        assertFalse(negativeAmount.isEmpty());
    }

    private void assertDetailAmountsEqual(CurrencyDetail tweedleDee, CurrencyDetail tweedleDum) {
        assertEquals(tweedleDee.getFinancialDocumentHundredDollarAmount(), tweedleDum.getFinancialDocumentHundredDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentFiftyDollarAmount(), tweedleDum.getFinancialDocumentFiftyDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentTwentyDollarAmount(), tweedleDum.getFinancialDocumentTwentyDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentTenDollarAmount(), tweedleDum.getFinancialDocumentTenDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentFiveDollarAmount(), tweedleDum.getFinancialDocumentFiveDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentTwoDollarAmount(), tweedleDum.getFinancialDocumentTwoDollarAmount());
        assertEquals(tweedleDee.getFinancialDocumentOneDollarAmount(), tweedleDum.getFinancialDocumentOneDollarAmount());
    }

    public void assertDetailAmountsNotEqual(CurrencyDetail tweedleDee, CurrencyDetail tweedleDum) {
        assertFalse(tweedleDee.getFinancialDocumentHundredDollarAmount().equals(tweedleDum.getFinancialDocumentHundredDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentFiftyDollarAmount().equals(tweedleDum.getFinancialDocumentFiftyDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentTwentyDollarAmount().equals(tweedleDum.getFinancialDocumentTwentyDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentTenDollarAmount().equals(tweedleDum.getFinancialDocumentTenDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentFiveDollarAmount().equals(tweedleDum.getFinancialDocumentFiveDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentTwoDollarAmount().equals(tweedleDum.getFinancialDocumentTwoDollarAmount()));
        assertFalse(tweedleDee.getFinancialDocumentOneDollarAmount().equals(tweedleDum.getFinancialDocumentOneDollarAmount()));
    }
}
