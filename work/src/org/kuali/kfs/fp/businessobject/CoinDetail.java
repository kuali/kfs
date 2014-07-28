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

package org.kuali.kfs.fp.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class contains the coin breakdown for coin inserted into a cash drawer
 */
public class CoinDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringStatus;

    private KualiDecimal financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTenCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentFiveCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOneCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOtherCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentHundredCentAmount = KualiDecimal.ZERO;

    // list of coin counts per roll for all 6 denominations, defined by parameter COUNT_PER_ROLL_BY_DENOMINATION
    private List<Integer> countsPerRoll;

    // list of coin/roll counts for all 6 denominations, used as temporary buffer when populating counts to amount
    private List<Integer> coinCounts = new ArrayList<Integer>();
    private List<Integer> rollCounts = new ArrayList<Integer>();

    /**
     * Default constructor.
     */
    public CoinDetail() {
        // initialize countsPerRoll so we don't have to read the parameter from DB each time count/amount is calculated
        countsPerRoll = SpringContext.getBean(CashDrawerService.class).getCoinCountsPerRoll();

        // initialize the coin/roll count lists to have a null value for each denomination;
        // null is used to indicate that the coin/roll count for that denomination hasn't been populated since last update
        for (int i=0; i < KFSConstants.COIN_DENOMINATIONS.length; i++) {
            coinCounts.add(null);
            rollCounts.add(null);
        }
    }

    /**
     * Constructs a new CoinDetail with the given documentNumber, financialDocumentTypeCode, and cashieringStatus.
     * with all amount fields default to zero.
     */
    public CoinDetail(String documentNumber, String financialDocumentTypeCode, String cashieringStatus) {
        this();
        this.documentNumber = documentNumber;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.cashieringStatus = cashieringStatus;
    }

    /**
     * Constructs a new CoinDetail by copying the coin amounts from the given CashDrawer, ignoring other non-amount fields.
     */
    public CoinDetail(CashDrawer cashDrawer) {
        this();
        financialDocumentHundredCentAmount = cashDrawer.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = cashDrawer.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = cashDrawer.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = cashDrawer.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = cashDrawer.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = cashDrawer.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = cashDrawer.getFinancialDocumentOtherCentAmount();
    }

    /**
     * Constructs a new CoinDetail as a complete copy of the given CoinDetail.
     */
    public CoinDetail(CoinDetail coinDetail) {
        this();
        documentNumber = coinDetail.getDocumentNumber();
        financialDocumentTypeCode = coinDetail.getFinancialDocumentTypeCode();
        cashieringStatus = coinDetail.getCashieringStatus();
        financialDocumentHundredCentAmount = coinDetail.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = coinDetail.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = coinDetail.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = coinDetail.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = coinDetail.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = coinDetail.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = coinDetail.getFinancialDocumentOtherCentAmount();
    }

    /**
     * Sets the non-amount primary key fields with the given documentNumber, financialDocumentTypeCode, and cashieringStatus.
     */
    public void setKeys(String documentNumber, String financialDocumentTypeCode, String cashieringStatus) {
        this.documentNumber = documentNumber;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.cashieringStatus = cashieringStatus;
    }

    /**
     * Copies all amounts from the given CoinDetail to this CoinDetail.
     */
    public void copyAmounts(CoinDetail coinDetail) {
        financialDocumentHundredCentAmount = coinDetail.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = coinDetail.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = coinDetail.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = coinDetail.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = coinDetail.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = coinDetail.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = coinDetail.getFinancialDocumentOtherCentAmount();
    }

    /*
     * Notes on introducing roll count into CoinDetail:
     * In CoinDetail, only amounts for all 6 denominations are persisted in DB; their corresponding coin/roll counts are not saved.
     * Instead, the counts need to be derived from the associated amounts when CoinDetail is retrieved from DB, or when amounts are changed;
     * vice versa, when the coin/roll counts are change by user input, the associated amounts need to be updated as well.
     * Since the calculation involves reading coin counts per roll from parameter, we could save some time by using some transient fields
     * to save the list of coin/roll counts. However, the down side is that we need to update this list each time any amount is updated,
     * which happens a lot. So it's a compromise between saving time on getters or setters. Overall, the decision is that it's cleaner
     * not to use the transient fields, but rather, improve the calculation speed by reusing the parameter value during previous read.
     * Also, since both coin count and roll count are derived from amount, we need to make some assumption on how the amount is
     * distributed between coin and roll. Basically, the following rule is used:
     *  1. We always assume maximum number of rolls are used to reach the amount;
     *  2. and the number of coins will fill up the remainder value of the amount.
     *
     * For convenience, in the helper getters/setters, we assign an index for each coin denomination (as defined in COIN_DENOMINATIONS),
     * and associate the amount and its derived coin/roll count corresponding of each denomination with this index.
     *
     * One special note on adding the coin/roll count lists: These 2 lists are not meant to be used as a parallel storage for amounts;
     * rather, they are used solely as a temporary buffer and indicator when only the first one of a coin/roll count pair is populated,
     * at which point, we may not be able to fully update the amount yet, since if/when the second one of the pair is also populated
     * during the same update, we would need to calculate and update the amount again based on the current values of coin/roll count pair.
     * Without these 2 count lists, we would have to derive the coin/roll count from the amount itself when we update the roll/coin count
     * and thus the amount, which would work in all but one case:
     * When the newly entered coin count >= its roll size, and it happens to be populated before the corresponding roll count is populated,
     * we would lose that roll count contributed from the overflowed part of the coin count, when the roll count itself is populated during
     * the same form submit. Since we don't have control over the random order in which fields are populated on a form, and we do allow
     * coin count to be equal to or bigger than its roll size, we have no choice but to utilize some temporary storage.
     * One also needs to bear in mind one important assumption: this solution assumes that in each update to the amount through
     * coin/roll counts, both counts will be updated, not just one; otherwise the amount will not get updated. Fortunately, the only cases
     * when amounts are updated through counts are form submit and unit tests, and in both cases, this assumption is true.
     * Still, we have a better solution to avoid the above assumption, i.e. we would not require each time amount is updated from counts,
     * both coin and roll counts need to be set; i.e. we could allow code (for ex, unit tests) to set one count only to update the amount.
     * On the other hand, if coin/roll counts are set in a sequence then they would be paired as one update to the amount.
     * The solution would be as follows:
     *  - If when we populate coin (roll) count, the roll (coin) count is not populated yet, we can derive the latter from the current amount,
     *  then use the new coin (roll) count and the derived roll (coin) count to compute the new amount.
     *  - Otherwise, we use the buffered roll (coin) count plus the new coin (roll) count to compute the new amount.
     *  In this combined approach, we still have to make one (less demanding) assumption though: we
     */

    /**
     * Calculates amount based on the associated coin/roll count for the specified coin denomination index.
     * If the passed in coinCount or rollCount is null, it's treated as 0.
     * The calculation is done with the following formula: amount = (coinCount + rollCount * countPerRoll) * denomination.
     * @param index the specified coin denomination index
     * @param coinCount the associated coin count
     * @param rollCount the associated roll count
     * @return calculated amount
     */
    protected KualiDecimal getAmountByCoinRollCount(int index, Integer coinCount, Integer rollCount) {
        int coincount = (coinCount == null) ? 0 : coinCount.intValue();
        int rollcount = (rollCount == null) ? 0 : rollCount.intValue();
        int count = coincount + rollcount * countsPerRoll.get(index);
        KualiDecimal amount = KFSConstants.COIN_AMOUNTS[index].multiply(new KualiDecimal(count));
        return amount;
    }

   /**
    * Calculates coin count based on the associated amount for the specified coin denomination index.
    * The calculation is done with the following formula: coinCount = (amount / denomination) % countPerRoll
    * @param index the specified coin denomination index
    * @return calculated coin count
    */
    protected Integer getCoinCountByAmount(int index) {
       KualiDecimal amount = getAmount(index);
       int count = (amount == null) ? 0 : amount.divide(KFSConstants.COIN_AMOUNTS[index]).intValue();
       int coincount = count % countsPerRoll.get(index);
       return new Integer(coincount);
    }

   /**
    * Calculates roll count based on the associated amount for the specified coin denomination index.
    * The calculation is done with the following formula: rollCount = (amount / denomination) / countPerRoll
    * @param index the specified coin denomination index
    * @return calculated roll count
    */
    protected Integer getRollCountByAmount(int index) {
       KualiDecimal amount = getAmount(index);
       int count = (amount == null) ? 0 : amount.divide(KFSConstants.COIN_AMOUNTS[index]).intValue();
       int rollcount = count / countsPerRoll.get(index);
       return new Integer(rollcount);
    }

    /**
     * Calculates and updates the corresponding amount based on the given coinCount and up-to-date rollCount, either read from buffer or derived from the pre-update amount,
     * and sets coinCount to its buffer or clears rollCount from its buffer based on whether the latter was changed in pair with the former in the current amount update.
     * @param index the specified coin denomination index
     * @param coinCount the new value for coin count
     */
    protected void setCoinCountToAmount(int index, Integer coinCount) {
        Integer rollCount = rollCounts.get(index);
        KualiDecimal amount;

        // if the currently buffered rollCount is still null when we set the coinCount, that means coinCount is the first
        // (or only) one being changed during current update, and rollCount hasn't been changed yet since last update
        if (rollCount == null) {
            // buffer the new coinCount, but make sure not to set it to null, as null is used to indicate not being changed yet
            coinCount = (coinCount == null) ? 0 : coinCount;
            coinCounts.set(index, coinCount);

            // derive the up-to-date rollCount from the current amount
            rollCount = getRollCountByAmount(index);
        }
        // otherwise, rollCount must have been just changed and buffered during current update, so we will use the buffered value
        else {
            // clear rollCount from the buffer, to get ready for next update;
            // note that at this point, coinCount should be null, so we don't even need to set or clear its buffer
            rollCounts.set(index, null);
        }

        // use the up-to-date coin/roll count pair to compute and update the new amount
        // note, the coinCount here could be null, but that's OK, getAmountByCoinRollCount will handle it
        amount = getAmountByCoinRollCount(index, coinCount, rollCount);
        setAmount(index, amount);
    }

    /**
     * Calculates and updates the corresponding amount based on the given rollCount and up-to-date coinCount, either read from buffer or derived from the pre-update amount,
     * and sets rollCount to its buffer or clears coinCount from its buffer based on whether the latter was changed in pair with the former in the current amount update.
     * @param index the specified coin denomination index
     * @param rollCount the new value for roll count
     */
    protected void setRollCountToAmount(int index, Integer rollCount) {
        Integer coinCount = coinCounts.get(index);
        KualiDecimal amount;

        // if the currently buffered coinCount is still null when we set the rollCount, that means rollCount is the first
        // (or only) one being changed during current update, and coinCount hasn't been changed yet since last update
        if (coinCount == null) {
            // buffer the new rollCount, but make sure not to set it to null, as null is used to indicate not being changed yet
            rollCount = (rollCount == null) ? 0 : rollCount;
            rollCounts.set(index, rollCount);

            // derive the up-to-date coinCount from the current amount
            coinCount = getCoinCountByAmount(index);
        }
        // otherwise, coinCount must have been just changed and buffered during current update, so we will use the buffered value
        else {
            // clear coinCount from the buffer, to get ready for next update;
            // note that at this point, rollCount should be null, so we don't even need to set or clear its buffer
            coinCounts.set(index, null);
        }

        // use the up-to-date coin/roll count pair to compute and update the new amount
        // note, the rollCount here could be null, but that's OK, getAmountByCoinRollCount will handle it
        amount = getAmountByCoinRollCount(index, coinCount, rollCount);
        setAmount(index, amount);
    }

    /**
     * Gets the amount corresponding to the specified coin denomination index.
     * @param index the specified coin denomination index
     * @return the amount
     */
    protected KualiDecimal getAmount(int index) {
        switch (index) {
            case 0:
                return financialDocumentHundredCentAmount;
            case 1:
                return financialDocumentFiftyCentAmount;
            case 2:
                return financialDocumentTwentyFiveCentAmount;
            case 3:
                return financialDocumentTenCentAmount;
            case 4:
                return financialDocumentFiveCentAmount;
            case 5:
                return financialDocumentOneCentAmount;
            default:
                return new KualiDecimal(0);
        }
    }

    /**
     * Sets the amount corresponding to the specified coin denomination index.
     * Note: We don't need to update the associated coin/roll count, since they are derived from the amount.
     * @param index the specified coin denomination index
     * @param amount the new value for amount
     */
    protected void setAmount(int index, KualiDecimal amount) {
        switch (index) {
            case 0:
                financialDocumentHundredCentAmount = amount;
                break;
            case 1:
                financialDocumentFiftyCentAmount = amount;
                break;
            case 2:
                financialDocumentTwentyFiveCentAmount = amount;
                break;
            case 3:
                financialDocumentTenCentAmount = amount;
                break;
            case 4:
                financialDocumentFiveCentAmount = amount;
                break;
            case 5:
                financialDocumentOneCentAmount = amount;
                break;
        }
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     *
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the cashieringStatus attribute.
     *
     * @return Returns the cashieringStatus
     */
    public String getCashieringStatus() {
        return cashieringStatus;
    }

    /**
     * Sets the cashieringStatus attribute.
     *
     * @param cashieringStatus The cashieringStatus to set.
     */
    public void setCashieringStatus(String financialDocumentColumnTypeCode) {
        this.cashieringStatus = financialDocumentColumnTypeCode;
    }

//    public Integer getFinancialDocumentTransactionLineNumber() {
//        return financialDocumentTransactionLineNumber;
//    }
//
//    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
//        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
//    }

    /**
     * Gets the financialDocumentHundredCentAmount attribute.
     *
     * @return Returns the financialDocumentHundredCentAmount
     */
    public KualiDecimal getFinancialDocumentHundredCentAmount() {
        return financialDocumentHundredCentAmount;
    }

    /**
     * Sets the financialDocumentHundredCentAmount attribute.
     *
     * @param financialDocumentHundredCentAmount The financialDocumentHundredCentAmount to set.
     */
    public void setFinancialDocumentHundredCentAmount(KualiDecimal financialDocumentHundredCentAmount) {
        this.financialDocumentHundredCentAmount = financialDocumentHundredCentAmount;
    	//updateAmount(0, financialDocumentHundredCentAmount);
    }

    /**
     * Returns the count of hundred cent coins in the drawer
     * @return the count of hundred cent coins in the drawer
     */
    public Integer getHundredCentCount() {
        return getCoinCountByAmount(0);
        //return coinCounts.get(0);
    }

    /**
     * Sets the count of hundred cent coins in the drawer.
     * @param coinCount the count of hundred cent coins present in the drawer
     */
    public void setHundredCentCount(Integer coinCount) {
        setCoinCountToAmount(0, coinCount);
        //updateCoinCount(0, coinCount);
    }

    /**
     * Returns the count of hundred cent rolls in the drawer
     * @return the count of hundred cent rolls in the drawer
     */
    public Integer getHundredCentRollCount() {
        return getRollCountByAmount(0);
        //return rollCounts.get(0);
    }

    /**
     * Sets the count of hundred cent rolls in the drawer.
     * @param rollCount the count of hundred cent rolls present in the drawer
     */
    public void setHundredCentRollCount(Integer rollCount) {
        setRollCountToAmount(0, rollCount);
        //updateRollCount(0, rollCount);
    }

    /**
     * Gets the financialDocumentFiftyCentAmount attribute.
     *
     * @return Returns the financialDocumentFiftyCentAmount
     */
    public KualiDecimal getFinancialDocumentFiftyCentAmount() {
        return financialDocumentFiftyCentAmount;
    }

    /**
     * Sets the financialDocumentFiftyCentAmount attribute.
     *
     * @param financialDocumentFiftyCentAmount The financialDocumentFiftyCentAmount to set.
     */
    public void setFinancialDocumentFiftyCentAmount(KualiDecimal financialDocumentFiftyCentAmount) {
        this.financialDocumentFiftyCentAmount = financialDocumentFiftyCentAmount;
    }

    /**
     * Returns the count of fifty cent coins in the drawer
     * @return the count of fifty cent coins in the drawer
     */
    public Integer getFiftyCentCount() {
        return getCoinCountByAmount(1);
    }

    /**
     * Sets the count of fifty cent coins in the drawer.
     * @param coinCount the count of fifty cent coins present in the drawer
     */
    public void setFiftyCentCount(Integer coinCount) {
        setCoinCountToAmount(1, coinCount);
    }

    /**
     * Returns the count of fifty cent rolls in the drawer
     * @return the count of fifty cent rolls in the drawer
     */
    public Integer getFiftyCentRollCount() {
        return getRollCountByAmount(1);
    }

    /**
     * Sets the count of fifty cent rolls in the drawer.
     * @param rollCount the count of fifty cent rolls present in the drawer
     */
    public void setFiftyCentRollCount(Integer rollCount) {
        setRollCountToAmount(1, rollCount);
    }

    /**
     * Gets the financialDocumentTwentyFiveCentAmount attribute.
     *
     * @return Returns the financialDocumentTwentyFiveCentAmount
     */
    public KualiDecimal getFinancialDocumentTwentyFiveCentAmount() {
        return financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Sets the financialDocumentTwentyFiveCentAmount attribute.
     *
     * @param financialDocumentTwentyFiveCentAmount The financialDocumentTwentyFiveCentAmount to set.
     */
    public void setFinancialDocumentTwentyFiveCentAmount(KualiDecimal financialDocumentTwentyFiveCentAmount) {
        this.financialDocumentTwentyFiveCentAmount = financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Returns the count of twenty-five cent coins in the drawer
     * @return the count of twenty-five cent coins in the drawer
     */
    public Integer getTwentyFiveCentCount() {
        return getCoinCountByAmount(2);
    }

    /**
     * Sets the count of twenty-five cent coins in the drawer.
     * @param coinCount the count of twenty-five cent coins present in the drawer
     */
    public void setTwentyFiveCentCount(Integer coinCount) {
        setCoinCountToAmount(2, coinCount);
    }

    /**
     * Returns the count of twenty-five cent rolls in the drawer
     * @return the count of twenty-five cent rolls in the drawer
     */
    public Integer getTwentyFiveCentRollCount() {
        return getRollCountByAmount(2);
    }

    /**
     * Sets the count of twenty-five cent rolls in the drawer.
     * @param rollCount the count of twenty-five cent rolls present in the drawer
     */
    public void setTwentyFiveCentRollCount(Integer rollCount) {
        setRollCountToAmount(2, rollCount);
    }

    /**
     * Gets the financialDocumentTenCentAmount attribute.
     *
     * @return Returns the financialDocumentTenCentAmount
     */
    public KualiDecimal getFinancialDocumentTenCentAmount() {
        return financialDocumentTenCentAmount;
    }

    /**
     * Sets the financialDocumentTenCentAmount attribute.
     *
     * @param financialDocumentTenCentAmount The financialDocumentTenCentAmount to set.
     */
    public void setFinancialDocumentTenCentAmount(KualiDecimal financialDocumentTenCentAmount) {
        this.financialDocumentTenCentAmount = financialDocumentTenCentAmount;
    }

    /**
     * Returns the count of ten cent coins in the drawer
     * @return the count of ten cent coins in the drawer
     */
    public Integer getTenCentCount() {
        return getCoinCountByAmount(3);
    }

    /**
     * Sets the count of ten cent coins in the drawer.
     * @param coinCount the count of ten cent coins present in the drawer
     */
    public void setTenCentCount(Integer coinCount) {
        setCoinCountToAmount(3, coinCount);
    }

    /**
     * Returns the count of ten cent rolls in the drawer
     * @return the count of ten cent rolls in the drawer
     */
    public Integer getTenCentRollCount() {
        return getRollCountByAmount(3);
    }

    /**
     * Sets the count of ten cent rolls in the drawer.
     * @param rollCount the count of ten cent rolls present in the drawer
     */
    public void setTenCentRollCount(Integer rollCount) {
        setRollCountToAmount(3, rollCount);
    }

    /**
     * Gets the financialDocumentFiveCentAmount attribute.
     *
     * @return Returns the financialDocumentFiveCentAmount
     */
    public KualiDecimal getFinancialDocumentFiveCentAmount() {
        return financialDocumentFiveCentAmount;
    }

    /**
     * Sets the financialDocumentFiveCentAmount attribute.
     *
     * @param financialDocumentFiveCentAmount The financialDocumentFiveCentAmount to set.
     */
    public void setFinancialDocumentFiveCentAmount(KualiDecimal financialDocumentFiveCentAmount) {
        this.financialDocumentFiveCentAmount = financialDocumentFiveCentAmount;
    }

    /**
     * Returns the count of five cent coins in the drawer
     * @return the count of five cent coins in the drawer
     */
    public Integer getFiveCentCount() {
        return getCoinCountByAmount(4);
    }

    /**
     * Sets the count of five cent coins in the drawer.
     * @param coinCount the count of five cent coins present in the drawer
     */
    public void setFiveCentCount(Integer coinCount) {
        setCoinCountToAmount(4, coinCount);
    }

    /**
     * Returns the count of five cent rolls in the drawer
     * @return the count of five cent rolls in the drawer
     */
    public Integer getFiveCentRollCount() {
        return getRollCountByAmount(4);
    }

    /**
     * Sets the count of five cent rolls in the drawer.
     * @param rollCount the count of five cent rolls present in the drawer
     */
    public void setFiveCentRollCount(Integer rollCount) {
        setRollCountToAmount(4, rollCount);
    }

    /**
     * Gets the financialDocumentOneCentAmount attribute.
     *
     * @return Returns the financialDocumentOneCentAmount
     */
    public KualiDecimal getFinancialDocumentOneCentAmount() {
        return financialDocumentOneCentAmount;
    }

    /**
     * Sets the financialDocumentOneCentAmount attribute.
     *
     * @param financialDocumentOneCentAmount The financialDocumentOneCentAmount to set.
     */
    public void setFinancialDocumentOneCentAmount(KualiDecimal financialDocumentOneCentAmount) {
        this.financialDocumentOneCentAmount = financialDocumentOneCentAmount;
    }

    /**
     * Returns the count of one cent coins in the drawer
     * @return the count of one cent coins in the drawer
     */
    public Integer getOneCentCount() {
        return getCoinCountByAmount(5);
    }

    /**
     * Sets the count of one cent coins in the drawer.
     * @param coinCount the count of one cent coins present in the drawer
     */
    public void setOneCentCount(Integer coinCount) {
        setCoinCountToAmount(5, coinCount);
    }

    /**
     * Returns the count of one cent rolls in the drawer
     * @return the count of one cent rolls in the drawer
     */
    public Integer getOneCentRollCount() {
        return getRollCountByAmount(5);
    }

    /**
     * Sets the count of one cent rolls in the drawer.
     * @param rollCount the count of one cent rolls present in the drawer
     */
    public void setOneCentRollCount(Integer rollCount) {
        setRollCountToAmount(5, rollCount);
    }

    /**
     * Gets the financialDocumentOtherCentAmount attribute.
     *
     * @return Returns the financialDocumentOtherCentAmount
     */
    public KualiDecimal getFinancialDocumentOtherCentAmount() {
        return financialDocumentOtherCentAmount;
    }

    /**
     * Sets the financialDocumentOtherCentAmount attribute.
     *
     * @param financialDocumentOtherCentAmount The financialDocumentOtherCentAmount to set.
     */
    public void setFinancialDocumentOtherCentAmount(KualiDecimal financialDocumentOtherCentAmount) {
        this.financialDocumentOtherCentAmount = financialDocumentOtherCentAmount;
    }

    /**
     * Returns the total amount represented by this coin detail record.
     *
     * @return total amount of this detail
     */
    public KualiDecimal getTotalAmount() {
        KualiDecimal result = KualiDecimal.ZERO;
        if (this.financialDocumentHundredCentAmount != null) {
            result = result.add(this.financialDocumentHundredCentAmount);
        }
        if (this.financialDocumentFiftyCentAmount != null) {
            result = result.add(this.financialDocumentFiftyCentAmount);
        }
        if (this.financialDocumentTwentyFiveCentAmount != null) {
            result = result.add(this.financialDocumentTwentyFiveCentAmount);
        }
        if (this.financialDocumentTenCentAmount != null) {
            result = result.add(this.financialDocumentTenCentAmount);
        }
        if (this.financialDocumentFiveCentAmount != null) {
            result = result.add(this.financialDocumentFiveCentAmount);
        }
        if (this.financialDocumentOneCentAmount != null) {
            result = result.add(this.financialDocumentOneCentAmount);
        }
        if (this.financialDocumentOtherCentAmount != null) {
            result = result.add(this.financialDocumentOtherCentAmount);
        }
        return result;
    }

    /**
     * This method sets all amounts in this record to zero
     */
    public void zeroOutAmounts() {
        this.financialDocumentHundredCentAmount = KualiDecimal.ZERO;
        this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
        this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
        this.financialDocumentTenCentAmount = KualiDecimal.ZERO;
        this.financialDocumentFiveCentAmount = KualiDecimal.ZERO;
        this.financialDocumentOneCentAmount = KualiDecimal.ZERO;
        this.financialDocumentOtherCentAmount = KualiDecimal.ZERO;
    }

    /**
     * This method sets all amounts that are null to zero
     */
    public void zeroOutUnpopulatedAmounts() {
        if (this.financialDocumentHundredCentAmount == null) {
            this.financialDocumentHundredCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentFiftyCentAmount == null) {
            this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentTwentyFiveCentAmount == null) {
            this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentTenCentAmount == null) {
            this.financialDocumentTenCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentFiveCentAmount == null) {
            this.financialDocumentFiveCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentOneCentAmount == null) {
            this.financialDocumentOneCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentOtherCentAmount == null) {
            this.financialDocumentOtherCentAmount = KualiDecimal.ZERO;
        }
    }

    public void add(CoinDetail detail) {
        if (detail.financialDocumentHundredCentAmount != null) {
            if (this.financialDocumentHundredCentAmount == null) {
                this.financialDocumentHundredCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentHundredCentAmount);
            }
            else {
                this.financialDocumentHundredCentAmount = this.financialDocumentHundredCentAmount.add(detail.financialDocumentHundredCentAmount);
            }
        }
        if (detail.financialDocumentFiftyCentAmount != null) {
            if (this.financialDocumentFiftyCentAmount == null) {
                this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiftyCentAmount);
            }
            else {
                this.financialDocumentFiftyCentAmount = this.financialDocumentFiftyCentAmount.add(detail.financialDocumentFiftyCentAmount);
            }
        }
        if (detail.financialDocumentTwentyFiveCentAmount != null) {
            if (this.financialDocumentTwentyFiveCentAmount == null) {
                this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentTwentyFiveCentAmount);
            }
            else {
                this.financialDocumentTwentyFiveCentAmount = this.financialDocumentTwentyFiveCentAmount.add(detail.financialDocumentTwentyFiveCentAmount);
            }
        }
        if (detail.financialDocumentTenCentAmount != null) {
            if (this.financialDocumentTenCentAmount == null) {
                this.financialDocumentTenCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentTenCentAmount);
            }
            else {
                this.financialDocumentTenCentAmount = this.financialDocumentTenCentAmount.add(detail.financialDocumentTenCentAmount);
            }
        }
        if (detail.financialDocumentFiveCentAmount != null) {
            if (this.financialDocumentFiveCentAmount == null) {
                this.financialDocumentFiveCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiveCentAmount);
            }
            else {
                this.financialDocumentFiveCentAmount = this.financialDocumentFiveCentAmount.add(detail.financialDocumentFiveCentAmount);
            }
        }
        if (detail.financialDocumentOneCentAmount != null) {
            if (this.financialDocumentOneCentAmount == null) {
                this.financialDocumentOneCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentOneCentAmount);
            }
            else {
                this.financialDocumentOneCentAmount = this.financialDocumentOneCentAmount.add(detail.financialDocumentOneCentAmount);
            }
        }
        if (detail.financialDocumentOtherCentAmount != null) {
            if (this.financialDocumentOtherCentAmount == null) {
                this.financialDocumentOtherCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentOtherCentAmount);
            }
            else {
                this.financialDocumentOtherCentAmount = this.financialDocumentOtherCentAmount.add(detail.financialDocumentOtherCentAmount);
            }
        }
    }

    public void subtract(CoinDetail detail) {
        if (detail.financialDocumentHundredCentAmount != null) {
            if (this.financialDocumentHundredCentAmount == null) {
                this.financialDocumentHundredCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentHundredCentAmount);
            }
            else {
                this.financialDocumentHundredCentAmount = this.financialDocumentHundredCentAmount.subtract(detail.financialDocumentHundredCentAmount);
            }
        }
        if (detail.financialDocumentFiftyCentAmount != null) {
            if (this.financialDocumentFiftyCentAmount == null) {
                this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiftyCentAmount);
            }
            else {
                this.financialDocumentFiftyCentAmount = this.financialDocumentFiftyCentAmount.subtract(detail.financialDocumentFiftyCentAmount);
            }
        }
        if (detail.financialDocumentTwentyFiveCentAmount != null) {
            if (this.financialDocumentTwentyFiveCentAmount == null) {
                this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTwentyFiveCentAmount);
            }
            else {
                this.financialDocumentTwentyFiveCentAmount = this.financialDocumentTwentyFiveCentAmount.subtract(detail.financialDocumentTwentyFiveCentAmount);
            }
        }
        if (detail.financialDocumentTenCentAmount != null) {
            if (this.financialDocumentTenCentAmount == null) {
                this.financialDocumentTenCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTenCentAmount);
            }
            else {
                this.financialDocumentTenCentAmount = this.financialDocumentTenCentAmount.subtract(detail.financialDocumentTenCentAmount);
            }
        }
        if (detail.financialDocumentFiveCentAmount != null) {
            if (this.financialDocumentFiveCentAmount == null) {
                this.financialDocumentFiveCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiveCentAmount);
            }
            else {
                this.financialDocumentFiveCentAmount = this.financialDocumentFiveCentAmount.subtract(detail.financialDocumentFiveCentAmount);
            }
        }
        if (detail.financialDocumentOneCentAmount != null) {
            if (this.financialDocumentOneCentAmount == null) {
                this.financialDocumentOneCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOneCentAmount);
            }
            else {
                this.financialDocumentOneCentAmount = this.financialDocumentOneCentAmount.subtract(detail.financialDocumentOneCentAmount);
            }
        }
        if (detail.financialDocumentOtherCentAmount != null) {
            if (this.financialDocumentOtherCentAmount == null) {
                this.financialDocumentOtherCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOtherCentAmount);
            }
            else {
                this.financialDocumentOtherCentAmount = this.financialDocumentOtherCentAmount.subtract(detail.financialDocumentOtherCentAmount);
            }
        }
    }

    /**
     * Is this coin detail empty of any value?
     *
     * @return true if any field at all is neither null nor the amount is zero
     */
    public boolean isEmpty() {
        return ((this.financialDocumentHundredCentAmount == null || this.financialDocumentHundredCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiftyCentAmount == null || this.financialDocumentFiftyCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTwentyFiveCentAmount == null || this.financialDocumentTwentyFiveCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTenCentAmount == null || this.financialDocumentTenCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiveCentAmount == null || this.financialDocumentFiveCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOneCentAmount == null || this.financialDocumentOneCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOtherCentAmount == null || this.financialDocumentOtherCentAmount.equals(KualiDecimal.ZERO)));
    }

    /**
     * Checks if this CoinDetail contains any negative amount field.
     * @return true if any amount is negative
     */
    public boolean hasNegativeAmount() {
        return ((financialDocumentHundredCentAmount != null && financialDocumentHundredCentAmount.isNegative()) ||
                (financialDocumentFiftyCentAmount != null && financialDocumentFiftyCentAmount.isNegative()) ||
                (financialDocumentTwentyFiveCentAmount != null && financialDocumentTwentyFiveCentAmount.isNegative()) ||
                (financialDocumentTenCentAmount != null && financialDocumentTenCentAmount.isNegative()) ||
                (financialDocumentFiveCentAmount != null && financialDocumentFiveCentAmount.isNegative()) ||
                (financialDocumentOneCentAmount != null && financialDocumentOneCentAmount.isNegative()) ||
                (financialDocumentOtherCentAmount != null && financialDocumentOtherCentAmount.isNegative()));
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("cashieringStatus", this.cashieringStatus);
        return m;
    }
}
