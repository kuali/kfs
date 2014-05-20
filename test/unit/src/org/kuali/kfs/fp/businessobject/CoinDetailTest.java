/*
 * Copyright 2007-2008 The Kuali Foundation
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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.List;

import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSParameterKeyConstants.FpParameterConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

// We need to add @ConfigureContext now since most tests here involve calling a service bean to load some parameter.
@ConfigureContext(session = khuntley)
public class CoinDetailTest extends KualiTestBase {
    public enum CoinDetailAmountFixture {
        GOOD_COIN_AMOUNT(30.0, 15.5, 10.75, 5.60, 2.45, 0.57),
        BAD_COIN_AMOUNT(5.5, 5.25, 0.80, 0.65, 0.63, 0.07),
        ALL_TENS_COIN_AMOUNT(10.0, 10.0, 10.0, 10.0, 10.0, 10.0),
        ALL_FIVES_COIN_AMOUNT(5.0, 5.0, 5.0, 5.0, 5.0, 5.0),
        ZERO_COIN_AMOUNT(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        NEGATIVE_COIN_AMOUNT(-5.0, -5.0, -5.0, -5.0, -5.0, -5.0),
        NULL_COIN_AMOUNT();

        private KualiDecimal hundredCentAmount;
        private KualiDecimal fiftyCentAmount;
        private KualiDecimal twentyFiveCentAmount;
        private KualiDecimal tenCentAmount;
        private KualiDecimal fiveCentAmount;
        private KualiDecimal oneCentAmount;

        private CoinDetailAmountFixture() {
        }

        private CoinDetailAmountFixture(double hundredCentAmount, double fiftyCentAmount, double twentyFiveCentAmount, double tenCentAmount, double fiveCentAmount, double oneCentAmount) {
            this.hundredCentAmount = new KualiDecimal(hundredCentAmount);
            this.fiftyCentAmount = new KualiDecimal(fiftyCentAmount);
            this.twentyFiveCentAmount = new KualiDecimal(twentyFiveCentAmount);
            this.tenCentAmount = new KualiDecimal(tenCentAmount);
            this.fiveCentAmount = new KualiDecimal(fiveCentAmount);
            this.oneCentAmount = new KualiDecimal(oneCentAmount);
        }

        public CoinDetail convertToCoinDetail() {
            CoinDetail coinDetail = new CoinDetail();
            coinDetail.setFinancialDocumentHundredCentAmount(this.hundredCentAmount);
            coinDetail.setFinancialDocumentFiftyCentAmount(this.fiftyCentAmount);
            coinDetail.setFinancialDocumentTwentyFiveCentAmount(this.twentyFiveCentAmount);
            coinDetail.setFinancialDocumentTenCentAmount(this.tenCentAmount);
            coinDetail.setFinancialDocumentFiveCentAmount(this.fiveCentAmount);
            coinDetail.setFinancialDocumentOneCentAmount(this.oneCentAmount);

            return coinDetail;
        }
    }

    public enum CoinDetailCountRollFixture {
        GOOD_COIN_AMOUNT(new Integer(5), new Integer(7), new Integer(3), new Integer(6), new Integer(9), new Integer(7),
                new Integer(1), new Integer(1), new Integer(1), new Integer(1), new Integer(1), new Integer(1)),
        ALL_FIVES_COIN_AMOUNT(new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5), new Integer(5),
                new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)),
        ZERO_COIN_AMOUNT(new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0),
                    new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0), new Integer(0)),
        NEGATIVE_COIN_AMOUNT(new Integer(-5), new Integer(-7), new Integer(-3), new Integer(-6), new Integer(-9), new Integer(-7),
                new Integer(-1), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(-1), new Integer(-1)),
        NULL_COIN_AMOUNT(null, null, null, null, null, null, null, null, null, null, null, null);

        private Integer hundredCentCount;
        private Integer fiftyCentCount;
        private Integer twentyFiveCentCount;
        private Integer tenCentCount;
        private Integer fiveCentCount;
        private Integer oneCentCount;
        // Since we introduced roll counts for coins, we need to include them in test
        private Integer hundredCentRollCount;
        private Integer fiftyCentRollCount;
        private Integer twentyFiveCentRollCount;
        private Integer tenCentRollCount;
        private Integer fiveCentRollCount;
        private Integer oneCentRollCount;

        private CoinDetailCountRollFixture(Integer hundredCentCount, Integer fiftyCentCount, Integer twentyFiveCentCount,
                Integer tenCentCount, Integer fiveCentCount, Integer oneCentCount,
                Integer hundredCentRollCount, Integer fiftyCentRollCount, Integer twentyFiveCentRollCount,
                Integer tenCentRollCount, Integer fiveCentRollCount, Integer oneCentRollCount) {
            this.hundredCentCount = hundredCentCount;
            this.fiftyCentCount = fiftyCentCount;
            this.twentyFiveCentCount = twentyFiveCentCount;
            this.tenCentCount = tenCentCount;
            this.fiveCentCount = fiveCentCount;
            this.oneCentCount = oneCentCount;
            this.hundredCentRollCount = hundredCentRollCount;
            this.fiftyCentRollCount = fiftyCentRollCount;
            this.twentyFiveCentRollCount = twentyFiveCentRollCount;
            this.tenCentRollCount = tenCentRollCount;
            this.fiveCentRollCount = fiveCentRollCount;
            this.oneCentRollCount = oneCentRollCount;
        }

        public CoinDetail convertToCoinDetail() {
            CoinDetail coinDetail = new CoinDetail();
            coinDetail.setHundredCentCount(this.hundredCentCount);
            coinDetail.setFiftyCentCount(this.fiftyCentCount);
            coinDetail.setTwentyFiveCentCount(this.twentyFiveCentCount);
            coinDetail.setTenCentCount(this.tenCentCount);
            coinDetail.setFiveCentCount(this.fiveCentCount);
            coinDetail.setOneCentCount(this.oneCentCount);
            coinDetail.setHundredCentRollCount(this.hundredCentRollCount);
            coinDetail.setFiftyCentRollCount(this.fiftyCentRollCount);
            coinDetail.setTwentyFiveCentRollCount(this.twentyFiveCentRollCount);
            coinDetail.setTenCentRollCount(this.tenCentRollCount);
            coinDetail.setFiveCentRollCount(this.fiveCentRollCount);
            coinDetail.setOneCentRollCount(this.oneCentRollCount);
            return coinDetail;
        }
    }

    public void testAmountToCountConversion() {
        CoinDetail goodDetail = CoinDetailAmountFixture.GOOD_COIN_AMOUNT.convertToCoinDetail();

        // since coin count and roll count is computed based on amount and count per roll values, and the latter is stored in parameter,
        // we can only test by hard coded values if the parameter is consistent with  assumed default values
        if (isCountPerRollParameterSameAsDefault()) {
            assertEquals(goodDetail.getHundredCentCount(), new Integer(5));
            assertEquals(goodDetail.getFiftyCentCount(), new Integer(11));
            assertEquals(goodDetail.getTwentyFiveCentCount(), new Integer(3));
            assertEquals(goodDetail.getTenCentCount(), new Integer(6));
            assertEquals(goodDetail.getFiveCentCount(), new Integer(9));
            assertEquals(goodDetail.getOneCentCount(), new Integer(7));
            assertEquals(goodDetail.getHundredCentRollCount(), new Integer(1));
            assertEquals(goodDetail.getFiftyCentRollCount(), new Integer(1));
            assertEquals(goodDetail.getTwentyFiveCentRollCount(), new Integer(1));
            assertEquals(goodDetail.getTenCentRollCount(), new Integer(1));
            assertEquals(goodDetail.getFiveCentRollCount(), new Integer(1));
            assertEquals(goodDetail.getOneCentRollCount(), new Integer(1));
        }


//        // but we can always test based on the invariant formula by which count/roll/amount are computed, as follows:
//        // create CoinDetail with good amounts, retrieve counts from it, and convert
//        Integer hundredCentCount = goodDetail.getHundredCentCount();
//        Integer fiftyCentCount = goodDetail.getFiftyCentCount();
//        Integer twentyFiveCentCount = goodDetail.getTwentyFiveCentCount();
//        Integer tenCentCount = goodDetail.getTenCentCount();
//        Integer fiveCentCount = goodDetail.getFiftyCentCount();
//        Integer oneCentCount = goodDetail.getOneCentCount();
//        Integer hundredCentRollCount = goodDetail.getHundredCentRollCount();
//        Integer fiftyCentRollCount = goodDetail.getFiftyCentRollCount();
//        Integer twentyFiveCentRollCount = goodDetail.getTwentyFiveCentRollCount();
//        Integer tenCentRollCount = goodDetail.getTenCentRollCount();
//        Integer fiveCentRollCount = goodDetail.getFiftyCentRollCount();
//        Integer oneCentRollCount = goodDetail.getOneCentRollCount();
//
//        // use the above counts to create CoinDetail copy, and compute amounts of the copy;
//        // the result amounts should equal to the original amounts.

    }

    public void testCountToAmountConversion() {
        CoinDetail goodDetail = CoinDetailCountRollFixture.GOOD_COIN_AMOUNT.convertToCoinDetail();

        // since coin count and roll count is computed based on amount and count per roll values, and the latter is stored in parameter,
        // we can only test by hard coded values if the parameter is consistent with  assumed default values
        if (isCountPerRollParameterSameAsDefault()) {
            assertEquals(goodDetail.getFinancialDocumentHundredCentAmount(), new KualiDecimal(30.0));
            assertEquals(goodDetail.getFinancialDocumentFiftyCentAmount(), new KualiDecimal(13.5));
            assertEquals(goodDetail.getFinancialDocumentTwentyFiveCentAmount(), new KualiDecimal(10.75));
            assertEquals(goodDetail.getFinancialDocumentTenCentAmount(), new KualiDecimal(5.6));
            assertEquals(goodDetail.getFinancialDocumentFiveCentAmount(), new KualiDecimal(2.45));
            assertEquals(goodDetail.getFinancialDocumentOneCentAmount(), new KualiDecimal(0.57));
        }
    }

    public void testZeroOutAmounts() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail goodAmount = CoinDetailAmountFixture.GOOD_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail goodZeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        goodZeroAmount.zeroOutAmounts();
        assertDetailAmountsEqual(goodZeroAmount, zeroAmount);

        goodAmount.zeroOutAmounts();
        assertDetailAmountsEqual(goodAmount, zeroAmount);

        nullAmount.zeroOutAmounts();
        assertDetailAmountsEqual(nullAmount, zeroAmount);

        CoinDetail goodCount = CoinDetailCountRollFixture.GOOD_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail goodZeroCount = CoinDetailCountRollFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullCount = CoinDetailCountRollFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        goodZeroCount.zeroOutAmounts();
        assertDetailAmountsEqual(goodZeroCount, zeroAmount);
        goodCount.zeroOutAmounts();
        assertDetailAmountsEqual(goodCount, zeroAmount);
        nullCount.zeroOutAmounts();
        assertDetailAmountsEqual(nullCount, zeroAmount);
    }

    public void testZeroOutUnpopulatedAmounts() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();

        CoinDetail goodAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        nullAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(nullAmount, zeroAmount);

        goodAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsNotEqual(goodAmount, nullAmount);

        goodAmount.setFinancialDocumentHundredCentAmount(null);
        goodAmount.setFinancialDocumentTenCentAmount(null);
        goodAmount.setFinancialDocumentOneCentAmount(null);
        CoinDetail semiPopulatedAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        semiPopulatedAmount.setFinancialDocumentHundredCentAmount(KualiDecimal.ZERO);
        semiPopulatedAmount.setFinancialDocumentTenCentAmount(KualiDecimal.ZERO);
        semiPopulatedAmount.setFinancialDocumentOneCentAmount(KualiDecimal.ZERO);
        goodAmount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(goodAmount, semiPopulatedAmount);

        CoinDetail goodCount = CoinDetailCountRollFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullCount = CoinDetailCountRollFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        nullCount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsEqual(nullCount, zeroAmount);
        goodCount.zeroOutUnpopulatedAmounts();
        assertDetailAmountsNotEqual(goodCount, zeroAmount);
    }

    public void testAdd() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail allFivesAmountControl = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail allFivesAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail negativeAmountControl = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail negativeAmount = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        allFivesAmount.add(zeroAmount);
        assertDetailAmountsEqual(allFivesAmount, allFivesAmountControl);
        allFivesAmount.add(nullAmount);
        assertDetailAmountsEqual(allFivesAmount, allFivesAmountControl);
        allFivesAmount.add(negativeAmount);
        assertDetailAmountsEqual(allFivesAmount, zeroAmount);
        negativeAmount.add(zeroAmount);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);
        negativeAmount.add(nullAmount);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);
        negativeAmount.add(allFivesAmountControl);
        assertDetailAmountsEqual(negativeAmount, zeroAmount);
    }

    public void testSubtract() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail allFivesAmountControl = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail allFivesAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail negativeAmountControl = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail negativeAmount = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();

        allFivesAmount.subtract(zeroAmount);
        assertDetailAmountsEqual(allFivesAmount, allFivesAmountControl);
        allFivesAmount.subtract(nullAmount);
        assertDetailAmountsEqual(allFivesAmount, allFivesAmountControl);

        allFivesAmount.subtract(negativeAmount);
        CoinDetail doublePositives = new CoinDetail();
        doublePositives.setFinancialDocumentHundredCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentHundredCentAmount()));
        doublePositives.setFinancialDocumentFiftyCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentFiftyCentAmount()));
        doublePositives.setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentTwentyFiveCentAmount()));
        doublePositives.setFinancialDocumentTenCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentTenCentAmount()));
        doublePositives.setFinancialDocumentFiveCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentFiveCentAmount()));
        doublePositives.setFinancialDocumentOneCentAmount(new KualiDecimal(2).multiply(allFivesAmountControl.getFinancialDocumentOneCentAmount()));
        assertDetailAmountsEqual(allFivesAmount, doublePositives);

        negativeAmount.subtract(zeroAmount);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);
        negativeAmount.subtract(nullAmount);
        assertDetailAmountsEqual(negativeAmount, negativeAmountControl);

        negativeAmount.subtract(allFivesAmountControl);
        CoinDetail doubleNegatives = new CoinDetail();
        doubleNegatives.setFinancialDocumentHundredCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentHundredCentAmount()));
        doubleNegatives.setFinancialDocumentFiftyCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentFiftyCentAmount()));
        doubleNegatives.setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentTwentyFiveCentAmount()));
        doubleNegatives.setFinancialDocumentTenCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentTenCentAmount()));
        doubleNegatives.setFinancialDocumentFiveCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentFiveCentAmount()));
        doubleNegatives.setFinancialDocumentOneCentAmount(new KualiDecimal(2).multiply(negativeAmountControl.getFinancialDocumentOneCentAmount()));
        assertDetailAmountsEqual(negativeAmount, doubleNegatives);
    }

    public void testTotal() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        assertEquals(zeroAmount.getTotalAmount(), KualiDecimal.ZERO);
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();
        assertEquals(nullAmount.getTotalAmount(), KualiDecimal.ZERO);
        CoinDetail allFivesAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        assertEquals(allFivesAmount.getTotalAmount(), new KualiDecimal(30));
        CoinDetail negativeAmount = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        assertEquals(negativeAmount.getTotalAmount(), new KualiDecimal(-30));
    }

    public void testIsEmpty() {
        CoinDetail zeroAmount = CoinDetailAmountFixture.ZERO_COIN_AMOUNT.convertToCoinDetail();
        assertTrue(zeroAmount.isEmpty());
        CoinDetail nullAmount = CoinDetailAmountFixture.NULL_COIN_AMOUNT.convertToCoinDetail();
        assertTrue(nullAmount.isEmpty());
        CoinDetail allFivesAmount = CoinDetailAmountFixture.ALL_FIVES_COIN_AMOUNT.convertToCoinDetail();
        assertFalse(allFivesAmount.isEmpty());
        CoinDetail negativeAmount = CoinDetailAmountFixture.NEGATIVE_COIN_AMOUNT.convertToCoinDetail();
        assertFalse(allFivesAmount.isEmpty());
    }

    private void assertDetailAmountsEqual(CoinDetail tweedleDee, CoinDetail tweedleDum) {
        assertEquals(tweedleDee.getFinancialDocumentHundredCentAmount(), tweedleDum.getFinancialDocumentHundredCentAmount());
        assertEquals(tweedleDee.getFinancialDocumentFiftyCentAmount(), tweedleDum.getFinancialDocumentFiftyCentAmount());
        assertEquals(tweedleDee.getFinancialDocumentTwentyFiveCentAmount(), tweedleDum.getFinancialDocumentTwentyFiveCentAmount());
        assertEquals(tweedleDee.getFinancialDocumentTenCentAmount(), tweedleDum.getFinancialDocumentTenCentAmount());
        assertEquals(tweedleDee.getFinancialDocumentFiveCentAmount(), tweedleDum.getFinancialDocumentFiveCentAmount());
        assertEquals(tweedleDee.getFinancialDocumentOneCentAmount(), tweedleDum.getFinancialDocumentOneCentAmount());
    }

    private void assertDetailAmountsNotEqual(CoinDetail tweedleDee, CoinDetail tweedleDum) {
        assertFalse(tweedleDee.getFinancialDocumentHundredCentAmount().equals(tweedleDum.getFinancialDocumentHundredCentAmount()));
        assertFalse(tweedleDee.getFinancialDocumentFiftyCentAmount().equals(tweedleDum.getFinancialDocumentFiftyCentAmount()));
        assertFalse(tweedleDee.getFinancialDocumentTwentyFiveCentAmount().equals(tweedleDum.getFinancialDocumentTwentyFiveCentAmount()));
        assertFalse(tweedleDee.getFinancialDocumentTenCentAmount().equals(tweedleDum.getFinancialDocumentTenCentAmount()));
        assertFalse(tweedleDee.getFinancialDocumentFiveCentAmount().equals(tweedleDum.getFinancialDocumentFiveCentAmount()));
        assertFalse(tweedleDee.getFinancialDocumentOneCentAmount().equals(tweedleDum.getFinancialDocumentOneCentAmount()));
    }

    private boolean isCountPerRollParameterSameAsDefault() {
        List<Integer> countsPerRoll = SpringContext.getBean(CashDrawerService.class).getCoinCountsPerRoll();
        return (countsPerRoll.get(0) == 25 && countsPerRoll.get(1) == 20 && countsPerRoll.get(2) == 40 &&
                countsPerRoll.get(3) == 50 && countsPerRoll.get(4) == 40 && countsPerRoll.get(5) == 50);
    }

    /**
     * Force COUNT_PER_ROLL_BY_DENOMINATION parameter to be set up exactly as the tests needed.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        TestUtils.setSystemParameter(CoinDetail.class, FpParameterConstants.COUNT_PER_ROLL_BY_DENOMINATION, "100c=25;50c=20;25c=40;10c=50;5c=40;1c=50");
        super.setUp();
    }

}
