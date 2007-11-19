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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;

/**
 * This class represents a cash drawer used in cash management document. It contains amounts for 
 * different types of denominations for currency and coin. 
 */
public class CashDrawer extends PersistableBusinessObjectBase {
    private String workgroupName;
    private String statusCode;

    private KualiDecimal cashDrawerTotalAmount;

    private KualiDecimal financialDocumentHundredDollarAmount;
    private KualiDecimal financialDocumentFiftyDollarAmount;
    private KualiDecimal financialDocumentTwentyDollarAmount;
    private KualiDecimal financialDocumentTenDollarAmount;
    private KualiDecimal financialDocumentFiveDollarAmount;
    private KualiDecimal financialDocumentTwoDollarAmount;
    private KualiDecimal financialDocumentOneDollarAmount;
    private KualiDecimal financialDocumentOtherDollarAmount;

    private KualiDecimal financialDocumentHundredCentAmount;
    private KualiDecimal financialDocumentFiftyCentAmount;
    private KualiDecimal financialDocumentTwentyFiveCentAmount;
    private KualiDecimal financialDocumentTenCentAmount;
    private KualiDecimal financialDocumentFiveCentAmount;
    private KualiDecimal financialDocumentOneCentAmount;
    private KualiDecimal financialDocumentOtherCentAmount;

    private KualiDecimal financialDocumentMiscellaneousAdvanceAmount;

    private String referenceFinancialDocumentNumber;

    /**
     * Default constructor.
     */
    public CashDrawer() {

    }

    /**
     * This method returns true if the cash drawer is open.
     * 
     * @return boolean
     */
    public boolean isOpen() {
        return StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_OPEN, statusCode);
    }

    /**
     * This method returns true if the cash drawer is closed.
     * 
     * @return boolean
     */
    public boolean isClosed() {
        return (statusCode == null || StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_CLOSED, statusCode) || referenceFinancialDocumentNumber == null);
    }

    /**
     * This method returns true if the cash drawer is locked.
     * 
     * @return boolean
     */
    public boolean isLocked() {
        return StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_LOCKED, statusCode);
    }


    /**
     * Gets the workgroupName attribute.
     * 
     * @return Returns the workgroupName
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * Gets the statusCode attribute.
     * 
     * @return Returns the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute.
     * 
     * @param financialDocumentOpenDepositCode The statusCode to set.
     */
    public void setStatusCode(String financialDocumentOpenDepositCode) {
        this.statusCode = financialDocumentOpenDepositCode;
    }


    /**
     * Gets the cashDrawerTotalAmount attribute.
     * 
     * @return Returns the cashDrawerTotalAmount
     */
    public KualiDecimal getCashDrawerTotalAmount() {
        return cashDrawerTotalAmount;
    }

    /**
     * Sets the cashDrawerTotalAmount attribute.
     * 
     * @param cashDrawerTotalAmount The cashDrawerTotalAmount to set.
     */
    public void setCashDrawerTotalAmount(KualiDecimal cashDrawerTotalAmount) {
        this.cashDrawerTotalAmount = cashDrawerTotalAmount;
    }


    /**
     * Gets the financialDocumentHundredDollarAmount attribute.
     * 
     * @return Returns the financialDocumentHundredDollarAmount
     */
    public KualiDecimal getFinancialDocumentHundredDollarAmount() {
        return financialDocumentHundredDollarAmount;
    }

    /**
     * Sets the financialDocumentHundredDollarAmount attribute.
     * 
     * @param financialDocumentHundredDollarAmount The financialDocumentHundredDollarAmount to set.
     */
    public void setFinancialDocumentHundredDollarAmount(KualiDecimal financialDocumentHundredDollarAmount) {
        this.financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount;
    }

    /**
     * Returns the actual count of hundred dollar bills
     * 
     * @return the number of hundred dollar bills present in the drawer
     */
    public Integer getHundredDollarCount() {
        return (financialDocumentHundredDollarAmount != null) ? new Integer(financialDocumentHundredDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of hundred dollar bills present in the drawer
     * 
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setHundredDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentHundredDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentFiftyDollarAmount attribute.
     * 
     * @return Returns the financialDocumentFiftyDollarAmount
     */
    public KualiDecimal getFinancialDocumentFiftyDollarAmount() {
        return financialDocumentFiftyDollarAmount;
    }

    /**
     * Sets the financialDocumentFiftyDollarAmount attribute.
     * 
     * @param financialDocumentFiftyDollarAmount The financialDocumentFiftyDollarAmount to set.
     */
    public void setFinancialDocumentFiftyDollarAmount(KualiDecimal financialDocumentFiftyDollarAmount) {
        this.financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount;
    }

    /**
     * Returns the actual count of fifty dollar bills
     * 
     * @return the number of fifty dollar bills present in the drawer
     */
    public Integer getFiftyDollarCount() {
        return (financialDocumentFiftyDollarAmount != null) ? new Integer(financialDocumentFiftyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of hundred dollar bills present in the drawer
     * 
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setFiftyDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentFiftyDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTwentyDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTwentyDollarAmount
     */
    public KualiDecimal getFinancialDocumentTwentyDollarAmount() {
        return financialDocumentTwentyDollarAmount;
    }

    /**
     * Sets the financialDocumentTwentyDollarAmount attribute.
     * 
     * @param financialDocumentTwentyDollarAmount The financialDocumentTwentyDollarAmount to set.
     */
    public void setFinancialDocumentTwentyDollarAmount(KualiDecimal financialDocumentTwentyDollarAmount) {
        this.financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount;
    }

    /**
     * Returns the actual count of twenty dollar bills
     * 
     * @return the number of twenty dollar bills present in the drawer
     */
    public Integer getTwentyDollarCount() {
        return (financialDocumentTwentyDollarAmount != null) ? new Integer(financialDocumentTwentyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of twenty dollar bills present in the drawer
     * 
     * @param count the number of twenty dollar bills present in the drawer
     */
    public void setTwentyDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTwentyDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTenDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTenDollarAmount
     */
    public KualiDecimal getFinancialDocumentTenDollarAmount() {
        return financialDocumentTenDollarAmount;
    }

    /**
     * Sets the financialDocumentTenDollarAmount attribute.
     * 
     * @param financialDocumentTenDollarAmount The financialDocumentTenDollarAmount to set.
     */
    public void setFinancialDocumentTenDollarAmount(KualiDecimal financialDocumentTenDollarAmount) {
        this.financialDocumentTenDollarAmount = financialDocumentTenDollarAmount;
    }

    /**
     * Returns the actual count of ten dollar bills
     * 
     * @return the number of ten dollar bills present in the drawer
     */
    public Integer getTenDollarCount() {
        return (financialDocumentTenDollarAmount != null) ? new Integer(financialDocumentTenDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of ten dollar bills present in the drawer
     * 
     * @param count the number of ten dollar bills present in the drawer
     */
    public void setTenDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTenDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentFiveDollarAmount attribute.
     * 
     * @return Returns the financialDocumentFiveDollarAmount
     */
    public KualiDecimal getFinancialDocumentFiveDollarAmount() {
        return financialDocumentFiveDollarAmount;
    }

    /**
     * Sets the financialDocumentFiveDollarAmount attribute.
     * 
     * @param financialDocumentFiveDollarAmount The financialDocumentFiveDollarAmount to set.
     */
    public void setFinancialDocumentFiveDollarAmount(KualiDecimal financialDocumentFiveDollarAmount) {
        this.financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount;
    }

    /**
     * Returns the actual count of five dollar bills
     * 
     * @return the number of five dollar bills present in the drawer
     */
    public Integer getFiveDollarCount() {
        return (financialDocumentFiveDollarAmount != null) ? new Integer(financialDocumentFiveDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of five dollar bills present in the drawer
     * 
     * @param count the number of five dollar bills present in the drawer
     */
    public void setFiveDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentFiveDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTwoDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTwoDollarAmount
     */
    public KualiDecimal getFinancialDocumentTwoDollarAmount() {
        return financialDocumentTwoDollarAmount;
    }

    /**
     * Sets the financialDocumentTwoDollarAmount attribute.
     * 
     * @param financialDocumentTwoDollarAmount The financialDocumentTwoDollarAmount to set.
     */
    public void setFinancialDocumentTwoDollarAmount(KualiDecimal financialDocumentTwoDollarAmount) {
        this.financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount;
    }

    /**
     * Returns the actual count of two dollar bills
     * 
     * @return the number of two dollar bills present in the drawer
     */
    public Integer getTwoDollarCount() {
        return (financialDocumentTwoDollarAmount != null) ? new Integer(financialDocumentTwoDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of two dollar bills present in the drawer
     * 
     * @param count the number of two dollar bills present in the drawer
     */
    public void setTwoDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTwoDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOneDollarAmount attribute.
     * 
     * @return Returns the financialDocumentOneDollarAmount
     */
    public KualiDecimal getFinancialDocumentOneDollarAmount() {
        return financialDocumentOneDollarAmount;
    }

    /**
     * Sets the financialDocumentOneDollarAmount attribute.
     * 
     * @param financialDocumentOneDollarAmount The financialDocumentOneDollarAmount to set.
     */
    public void setFinancialDocumentOneDollarAmount(KualiDecimal financialDocumentOneDollarAmount) {
        this.financialDocumentOneDollarAmount = financialDocumentOneDollarAmount;
    }

    /**
     * Returns the actual count of one dollar bills
     * 
     * @return the number of one dollar bills present in the drawer
     */
    public Integer getOneDollarCount() {
        return (financialDocumentOneDollarAmount != null) ? new Integer(financialDocumentOneDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * This sets the count of one dollar bills present in the drawer
     * 
     * @param count the number of one dollar bills present in the drawer
     */
    public void setOneDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentOneDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOtherDollarAmount attribute.
     * 
     * @return Returns the financialDocumentOtherDollarAmount
     */
    public KualiDecimal getFinancialDocumentOtherDollarAmount() {
        return financialDocumentOtherDollarAmount;
    }

    /**
     * Sets the financialDocumentOtherDollarAmount attribute.
     * 
     * @param financialDocumentOtherDollarAmount The financialDocumentOtherDollarAmount to set.
     */
    public void setFinancialDocumentOtherDollarAmount(KualiDecimal financialDocumentOtherDollarAmount) {
        this.financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount;
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
     * Returns the number of half-cent coins in the drawer
     * 
     * @return the count of half cent coins in the drawer
     */
    public Integer getFiftyCentCount() {
        return (financialDocumentFiftyCentAmount != null) ? new Integer(financialDocumentFiftyCentAmount.divide(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of fifty cent coins in the drawer. This is useful if, you know, you're in da club, with, say a bottle full of
     * "bub"
     * 
     * @param count the number of fifty cent coins present in the drawer
     */
    public void setFiftyCentCount(Integer count) {
        if (count != null) {
            financialDocumentFiftyCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT);
        }
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
     * Returns the number of quarters in the drawer
     * 
     * @return the count of quarters in the drawer
     */
    public Integer getTwentyFiveCentCount() {
        return (financialDocumentTwentyFiveCentAmount != null) ? new Integer(financialDocumentTwentyFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of quarters in the drawer
     * 
     * @param count the number of quarters present in the drawer
     */
    public void setTwentyFiveCentCount(Integer count) {
        if (count != null) {
            financialDocumentTwentyFiveCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT);
        }
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
     * Returns the number of dimes in the drawer
     * 
     * @return the count of dimes in the drawer
     */
    public Integer getTenCentCount() {
        return (financialDocumentTenCentAmount != null) ? new Integer(financialDocumentTenCentAmount.divide(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of dimes in the drawer
     * 
     * @param count the number of dimes present in the drawer
     */
    public void setTenCentCount(Integer count) {
        if (count != null) {
            financialDocumentTenCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT);
        }
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
     * Returns the number of nickels in the drawer
     * 
     * @return the count of nickels in the drawer
     */
    public Integer getFiveCentCount() {
        return (financialDocumentFiveCentAmount != null) ? new Integer(financialDocumentFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of nickels in the drawer
     * 
     * @param count the number of nickels present in the drawer
     */
    public void setFiveCentCount(Integer count) {
        if (count != null) {
            financialDocumentFiveCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT);
        }
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
     * Returns the number of pennies in the drawer
     * 
     * @return the count of pennies in the drawer
     */
    public Integer getOneCentCount() {
        return (financialDocumentOneCentAmount != null) ? new Integer(financialDocumentOneCentAmount.divide(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of pennies in the drawer
     * 
     * @param count the number of pennies present in the drawer
     */
    public void setOneCentCount(Integer count) {
        if (count != null) {
            financialDocumentOneCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT);
        }
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
    }

    /**
     * Returns the number of dollar coins--Sacajawea, Susan B. Anthony, or otherwise--in the drawer
     * 
     * @return the count of dollar coins in the drawer
     */
    public Integer getHundredCentCount() {
        return (financialDocumentHundredCentAmount != null) ? new Integer(financialDocumentHundredCentAmount.divide(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT).intValue()) : new Integer(0);
    }

    /**
     * Sets the number of hundred cent coins in the drawer
     * 
     * @param count the number of hundred cent coins present in the drawer
     */
    public void setHundredCentCount(Integer count) {
        if (count != null) {
            financialDocumentHundredCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentMiscellaneousAdvanceAmount attribute.
     * 
     * @return Returns the financialDocumentMiscellaneousAdvanceAmount
     */
    public KualiDecimal getFinancialDocumentMiscellaneousAdvanceAmount() {
        return financialDocumentMiscellaneousAdvanceAmount;
    }

    /**
     * Sets the financialDocumentMiscellaneousAdvanceAmount attribute.
     * 
     * @param financialDocumentMiscellaneousAdvanceAmount The financialDocumentMiscellaneousAdvanceAmount to set.
     */
    public void setFinancialDocumentMiscellaneousAdvanceAmount(KualiDecimal financialDocumentMiscellaneousAdvanceAmount) {
        this.financialDocumentMiscellaneousAdvanceAmount = financialDocumentMiscellaneousAdvanceAmount;
    }


    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * This method calculates the total amount of currency in the cash drawer
     * 
     * @return the total amount of currency
     */
    public KualiDecimal getCurrencyTotalAmount() {
        KualiDecimal result = new KualiDecimal(0);
        if (this.financialDocumentHundredDollarAmount != null) {
            result = result.add(this.financialDocumentHundredDollarAmount);
        }
        if (this.financialDocumentFiftyDollarAmount != null) {
            result = result.add(this.financialDocumentFiftyDollarAmount);
        }
        if (this.financialDocumentTwentyDollarAmount != null) {
            result = result.add(this.financialDocumentTwentyDollarAmount);
        }
        if (this.financialDocumentTenDollarAmount != null) {
            result = result.add(this.financialDocumentTenDollarAmount);
        }
        if (this.financialDocumentFiveDollarAmount != null) {
            result = result.add(this.financialDocumentFiveDollarAmount);
        }
        if (this.financialDocumentTwoDollarAmount != null) {
            result = result.add(this.financialDocumentTwoDollarAmount);
        }
        if (this.financialDocumentOneDollarAmount != null) {
            result = result.add(this.financialDocumentOneDollarAmount);
        }
        if (this.financialDocumentOtherDollarAmount != null) {
            result = result.add(this.financialDocumentOtherDollarAmount);
        }
        return result;
    }

    /**
     * This method calculates the total amount of coin in the cash drawer
     * 
     * @return the total amount of coin
     */
    public KualiDecimal getCoinTotalAmount() {
        KualiDecimal result = new KualiDecimal(0);
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
     * This calculates the total amount of money currently in the cash drawer
     * 
     * @return the amount currently in the cash drawer
     */
    public KualiDecimal getTotalAmount() {
        return this.getCurrencyTotalAmount().add(this.getCoinTotalAmount());
    }

    /**
     * This method adds currency to the cash drawer
     * 
     * @param detail the record indicating how much of each denomination of currency to add
     */
    public void addCurrency(CurrencyDetail detail) {
        if (detail.getFinancialDocumentHundredDollarAmount() != null) {
            if (financialDocumentHundredDollarAmount == null) {
                financialDocumentHundredDollarAmount = new KualiDecimal(detail.getFinancialDocumentHundredDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount.add(detail.getFinancialDocumentHundredDollarAmount());
            }
        }
        if (detail.getFinancialDocumentFiftyDollarAmount() != null) {
            if (financialDocumentFiftyDollarAmount == null) {
                financialDocumentFiftyDollarAmount = new KualiDecimal(detail.getFinancialDocumentFiftyDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount.add(detail.getFinancialDocumentFiftyDollarAmount());
            }
        }
        if (detail.getFinancialDocumentTwentyDollarAmount() != null) {
            if (financialDocumentTwentyDollarAmount == null) {
                financialDocumentTwentyDollarAmount = new KualiDecimal(detail.getFinancialDocumentTwentyDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount.add(detail.getFinancialDocumentTwentyDollarAmount());
            }
        }
        if (detail.getFinancialDocumentTenDollarAmount() != null) {
            if (financialDocumentTenDollarAmount == null) {
                financialDocumentTenDollarAmount = new KualiDecimal(detail.getFinancialDocumentTenDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentTenDollarAmount = financialDocumentTenDollarAmount.add(detail.getFinancialDocumentTenDollarAmount());
            }
        }
        if (detail.getFinancialDocumentFiveDollarAmount() != null) {
            if (financialDocumentFiveDollarAmount == null) {
                financialDocumentFiveDollarAmount = new KualiDecimal(detail.getFinancialDocumentFiveDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount.add(detail.getFinancialDocumentFiveDollarAmount());
            }
        }
        if (detail.getFinancialDocumentTwoDollarAmount() != null) {
            if (financialDocumentTwoDollarAmount == null) {
                financialDocumentTwoDollarAmount = new KualiDecimal(detail.getFinancialDocumentTwoDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount.add(detail.getFinancialDocumentTwoDollarAmount());
            }
        }
        if (detail.getFinancialDocumentOneDollarAmount() != null) {
            if (financialDocumentOneDollarAmount == null) {
                financialDocumentOneDollarAmount = new KualiDecimal(detail.getFinancialDocumentOneDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentOneDollarAmount = financialDocumentOneDollarAmount.add(detail.getFinancialDocumentOneDollarAmount());
            }
        }
        if (detail.getFinancialDocumentOtherDollarAmount() != null) {
            if (financialDocumentOtherDollarAmount == null) {
                financialDocumentOtherDollarAmount = new KualiDecimal(detail.getFinancialDocumentOtherDollarAmount().bigDecimalValue());
            }
            else {
                financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount.add(detail.getFinancialDocumentOtherDollarAmount());
            }
        }
    }

    /**
     * This method puts coin into the cash drawer
     * 
     * @param detail the record indicating how much coin of each denomiation to add
     */
    public void addCoin(CoinDetail detail) {
        if (detail.getFinancialDocumentHundredCentAmount() != null) {
            if (getFinancialDocumentHundredCentAmount() == null) {
                setFinancialDocumentHundredCentAmount(new KualiDecimal(detail.getFinancialDocumentHundredCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentHundredCentAmount(getFinancialDocumentHundredCentAmount().add(detail.getFinancialDocumentHundredCentAmount()));
            }
        }
        if (detail.getFinancialDocumentFiftyCentAmount() != null) {
            if (getFinancialDocumentFiftyCentAmount() == null) {
                setFinancialDocumentFiftyCentAmount(new KualiDecimal(detail.getFinancialDocumentFiftyCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentFiftyCentAmount(getFinancialDocumentFiftyCentAmount().add(detail.getFinancialDocumentFiftyCentAmount()));
            }
        }
        if (detail.getFinancialDocumentTwentyFiveCentAmount() != null) {
            if (getFinancialDocumentTwentyFiveCentAmount() == null) {
                setFinancialDocumentTwentyFiveCentAmount(new KualiDecimal(detail.getFinancialDocumentTwentyFiveCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentTwentyFiveCentAmount(getFinancialDocumentTwentyFiveCentAmount().add(detail.getFinancialDocumentTwentyFiveCentAmount()));
            }
        }
        if (detail.getFinancialDocumentTenCentAmount() != null) {
            if (getFinancialDocumentTenCentAmount() == null) {
                setFinancialDocumentTenCentAmount(new KualiDecimal(detail.getFinancialDocumentTenCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentTenCentAmount(getFinancialDocumentTenCentAmount().add(detail.getFinancialDocumentTenCentAmount()));
            }
        }
        if (detail.getFinancialDocumentFiveCentAmount() != null) {
            if (getFinancialDocumentFiveCentAmount() == null) {
                setFinancialDocumentFiveCentAmount(new KualiDecimal(detail.getFinancialDocumentFiveCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentFiveCentAmount(getFinancialDocumentFiveCentAmount().add(detail.getFinancialDocumentFiveCentAmount()));
            }
        }
        if (detail.getFinancialDocumentOneCentAmount() != null) {
            if (getFinancialDocumentOneCentAmount() == null) {
                setFinancialDocumentOneCentAmount(new KualiDecimal(detail.getFinancialDocumentOneCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentOneCentAmount(getFinancialDocumentOneCentAmount().add(detail.getFinancialDocumentOneCentAmount()));
            }
        }
        if (detail.getFinancialDocumentOtherCentAmount() != null) {
            if (getFinancialDocumentOtherCentAmount() == null) {
                setFinancialDocumentOtherCentAmount(new KualiDecimal(detail.getFinancialDocumentOtherCentAmount().bigDecimalValue()));
            }
            else {
                setFinancialDocumentOtherCentAmount(getFinancialDocumentOtherCentAmount().add(detail.getFinancialDocumentOtherCentAmount()));
            }
        }
    }

    /**
     * This method removes currency amounts from the cash drawer
     * 
     * @param detail the record indicating how much currency of each denomination to remove
     */
    public void removeCurrency(CurrencyDetail detail) {
        if (detail.getFinancialDocumentHundredDollarAmount() != null) {
            if (this.getFinancialDocumentHundredDollarAmount() == null || detail.getFinancialDocumentHundredDollarAmount().isGreaterThan(this.getFinancialDocumentHundredDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of hundred dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentHundredDollarAmount(getFinancialDocumentHundredDollarAmount().subtract(detail.getFinancialDocumentHundredDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentFiftyDollarAmount() != null) {
            if (this.getFinancialDocumentFiftyDollarAmount() == null || detail.getFinancialDocumentFiftyDollarAmount().isGreaterThan(this.getFinancialDocumentFiftyDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of fifty dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentFiftyDollarAmount(getFinancialDocumentFiftyDollarAmount().subtract(detail.getFinancialDocumentFiftyDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentTwentyDollarAmount() != null) {
            if (this.getFinancialDocumentTwentyDollarAmount() == null || detail.getFinancialDocumentTwentyDollarAmount().isGreaterThan(this.getFinancialDocumentTwentyDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of twenty dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentTwentyDollarAmount(getFinancialDocumentTwentyDollarAmount().subtract(detail.getFinancialDocumentTwentyDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentTenDollarAmount() != null) {
            if (this.getFinancialDocumentTenDollarAmount() == null || detail.getFinancialDocumentTenDollarAmount().isGreaterThan(this.getFinancialDocumentTenDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of ten dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentTenDollarAmount(getFinancialDocumentTenDollarAmount().subtract(detail.getFinancialDocumentTenDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentFiveDollarAmount() != null) {
            if (this.getFinancialDocumentFiveDollarAmount() == null || detail.getFinancialDocumentFiveDollarAmount().isGreaterThan(this.getFinancialDocumentFiveDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of five dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentFiveDollarAmount(getFinancialDocumentFiveDollarAmount().subtract(detail.getFinancialDocumentFiveDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentTwoDollarAmount() != null) {
            if (this.getFinancialDocumentTwoDollarAmount() == null || detail.getFinancialDocumentTwoDollarAmount().isGreaterThan(this.getFinancialDocumentTwoDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of two dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentTwoDollarAmount(getFinancialDocumentTwoDollarAmount().subtract(detail.getFinancialDocumentTwoDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentOneDollarAmount() != null) {
            if (this.getFinancialDocumentOneDollarAmount() == null || detail.getFinancialDocumentOneDollarAmount().isGreaterThan(this.getFinancialDocumentOneDollarAmount())) {
                throw new IllegalArgumentException("The requested amount of one dollar bills exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentOneDollarAmount(getFinancialDocumentOneDollarAmount().subtract(detail.getFinancialDocumentOneDollarAmount()));
            }
        }
        if (detail.getFinancialDocumentOtherDollarAmount() != null) {
            if (this.getFinancialDocumentOtherDollarAmount() == null || detail.getFinancialDocumentOtherDollarAmount().isGreaterThan(this.getFinancialDocumentOtherDollarAmount())) {
                throw new IllegalArgumentException("The requested other dollar amount exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentOtherDollarAmount(getFinancialDocumentOtherDollarAmount().subtract(detail.getFinancialDocumentOtherDollarAmount()));
            }
        }
    }

    /**
     * This method takes coin out of the cash drawer
     * 
     * @param detail the record indicating how much coin of each denomination to remove
     */
    public void removeCoin(CoinDetail detail) {
        if (detail.getFinancialDocumentHundredCentAmount() != null) {
            if (this.getFinancialDocumentHundredCentAmount() == null || detail.getFinancialDocumentHundredCentAmount().isGreaterThan(this.getFinancialDocumentHundredCentAmount())) {
                throw new IllegalArgumentException("The requested amount of hundred cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentHundredCentAmount(getFinancialDocumentHundredCentAmount().subtract(detail.getFinancialDocumentHundredCentAmount()));
            }
        }
        if (detail.getFinancialDocumentFiftyCentAmount() != null) {
            if (this.getFinancialDocumentFiftyCentAmount() == null || detail.getFinancialDocumentFiftyCentAmount().isGreaterThan(this.getFinancialDocumentFiftyCentAmount())) {
                throw new IllegalArgumentException("The requested amount of fifty cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentFiftyCentAmount(getFinancialDocumentFiftyCentAmount().subtract(detail.getFinancialDocumentFiftyCentAmount()));
            }
        }
        if (detail.getFinancialDocumentTwentyFiveCentAmount() != null) {
            if (this.getFinancialDocumentTwentyFiveCentAmount() == null || detail.getFinancialDocumentTwentyFiveCentAmount().isGreaterThan(this.getFinancialDocumentTwentyFiveCentAmount())) {
                throw new IllegalArgumentException("The requested amount of twenty five cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentTwentyFiveCentAmount(getFinancialDocumentTwentyFiveCentAmount().subtract(detail.getFinancialDocumentTwentyFiveCentAmount()));
            }
        }
        if (detail.getFinancialDocumentTenCentAmount() != null) {
            if (this.getFinancialDocumentTenCentAmount() == null || detail.getFinancialDocumentTenCentAmount().isGreaterThan(this.getFinancialDocumentTenCentAmount())) {
                throw new IllegalArgumentException("The requested amount of ten cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentTenCentAmount(getFinancialDocumentTenCentAmount().subtract(detail.getFinancialDocumentTenCentAmount()));
            }
        }
        if (detail.getFinancialDocumentFiveCentAmount() != null) {
            if (this.getFinancialDocumentFiveCentAmount() == null || detail.getFinancialDocumentFiveCentAmount().isGreaterThan(this.getFinancialDocumentFiveCentAmount())) {
                throw new IllegalArgumentException("The requested amount of five cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentFiveCentAmount(getFinancialDocumentFiveCentAmount().subtract(detail.getFinancialDocumentFiveCentAmount()));
            }
        }
        if (detail.getFinancialDocumentOneCentAmount() != null) {
            if (this.getFinancialDocumentOneCentAmount() == null || detail.getFinancialDocumentOneCentAmount().isGreaterThan(this.getFinancialDocumentOneCentAmount())) {
                throw new IllegalArgumentException("The requested amount of one cent pieces exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentOneCentAmount(getFinancialDocumentOneCentAmount().subtract(detail.getFinancialDocumentOneCentAmount()));
            }
        }
        if (detail.getFinancialDocumentOtherCentAmount() != null) {
            if (this.getFinancialDocumentOtherCentAmount() == null || detail.getFinancialDocumentOtherCentAmount().isGreaterThan(this.getFinancialDocumentOtherCentAmount())) {
                throw new IllegalArgumentException("The requested other cent amount exceeds the amount in the cash drawer");
            }
            else {
                setFinancialDocumentOtherCentAmount(getFinancialDocumentOtherCentAmount().subtract(detail.getFinancialDocumentOtherCentAmount()));
            }
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("workgroupName", this.workgroupName);
        return m;
    }
}
