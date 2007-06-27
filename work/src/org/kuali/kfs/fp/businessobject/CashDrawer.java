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
 * 
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
        return StringUtils.equals(KFSConstants.CashDrawerConstants.STATUS_CLOSED, statusCode);
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
     * 
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute.
     * 
     * @param workgroupName The workgroupName to set.
     * 
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * Gets the statusCode attribute.
     * 
     * @return Returns the statusCode
     * 
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute.
     * 
     * @param financialDocumentOpenDepositCode The statusCode to set.
     * 
     */
    public void setStatusCode(String financialDocumentOpenDepositCode) {
        this.statusCode = financialDocumentOpenDepositCode;
    }


    /**
     * Gets the cashDrawerTotalAmount attribute.
     * 
     * @return Returns the cashDrawerTotalAmount
     * 
     */
    public KualiDecimal getCashDrawerTotalAmount() {
        return cashDrawerTotalAmount;
    }

    /**
     * Sets the cashDrawerTotalAmount attribute.
     * 
     * @param cashDrawerTotalAmount The cashDrawerTotalAmount to set.
     * 
     */
    public void setCashDrawerTotalAmount(KualiDecimal cashDrawerTotalAmount) {
        this.cashDrawerTotalAmount = cashDrawerTotalAmount;
    }


    /**
     * Gets the financialDocumentHundredDollarAmount attribute.
     * 
     * @return Returns the financialDocumentHundredDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentHundredDollarAmount() {
        return financialDocumentHundredDollarAmount;
    }

    /**
     * Sets the financialDocumentHundredDollarAmount attribute.
     * 
     * @param financialDocumentHundredDollarAmount The financialDocumentHundredDollarAmount to set.
     * 
     */
    public void setFinancialDocumentHundredDollarAmount(KualiDecimal financialDocumentHundredDollarAmount) {
        this.financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount;
    }
    
    /**
     * Returns the actual count of hundred dollar bills
     * @return the number of hundred dollar bills present in the drawer
     */
    public int getHundredDollarCount() {
        return (financialDocumentHundredDollarAmount != null) ? financialDocumentHundredDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of hundred dollar bills present in the drawer
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setHundredDollarCount(int count) {
        this.financialDocumentHundredDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT);
    }
    
    /**
     * Gets the financialDocumentFiftyDollarAmount attribute.
     * 
     * @return Returns the financialDocumentFiftyDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentFiftyDollarAmount() {
        return financialDocumentFiftyDollarAmount;
    }

    /**
     * Sets the financialDocumentFiftyDollarAmount attribute.
     * 
     * @param financialDocumentFiftyDollarAmount The financialDocumentFiftyDollarAmount to set.
     * 
     */
    public void setFinancialDocumentFiftyDollarAmount(KualiDecimal financialDocumentFiftyDollarAmount) {
        this.financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount;
    }

    /**
     * Returns the actual count of fifty dollar bills
     * @return the number of fifty dollar bills present in the drawer
     */
    public int getFiftyDollarCount() {
        return (financialDocumentFiftyDollarAmount != null) ? financialDocumentFiftyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of hundred dollar bills present in the drawer
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setFiftyDollarCount(int count) {
        this.financialDocumentFiftyDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT);
    }
    
    /**
     * Gets the financialDocumentTwentyDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTwentyDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTwentyDollarAmount() {
        return financialDocumentTwentyDollarAmount;
    }

    /**
     * Sets the financialDocumentTwentyDollarAmount attribute.
     * 
     * @param financialDocumentTwentyDollarAmount The financialDocumentTwentyDollarAmount to set.
     * 
     */
    public void setFinancialDocumentTwentyDollarAmount(KualiDecimal financialDocumentTwentyDollarAmount) {
        this.financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount;
    }

    /**
     * Returns the actual count of twenty dollar bills
     * @return the number of twenty dollar bills present in the drawer
     */
    public int getTwentyDollarCount() {
        return (financialDocumentTwentyDollarAmount != null) ? financialDocumentTwentyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of twenty dollar bills present in the drawer
     * @param count the number of twenty dollar bills present in the drawer
     */
    public void setTwentyDollarCount(int count) {
        this.financialDocumentTwentyDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT);
    }

    /**
     * Gets the financialDocumentTenDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTenDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTenDollarAmount() {
        return financialDocumentTenDollarAmount;
    }

    /**
     * Sets the financialDocumentTenDollarAmount attribute.
     * 
     * @param financialDocumentTenDollarAmount The financialDocumentTenDollarAmount to set.
     * 
     */
    public void setFinancialDocumentTenDollarAmount(KualiDecimal financialDocumentTenDollarAmount) {
        this.financialDocumentTenDollarAmount = financialDocumentTenDollarAmount;
    }

    /**
     * Returns the actual count of ten dollar bills
     * @return the number of ten dollar bills present in the drawer
     */
    public int getTenDollarCount() {
        return (financialDocumentTenDollarAmount != null) ? financialDocumentTenDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of ten dollar bills present in the drawer
     * @param count the number of ten dollar bills present in the drawer
     */
    public void setTenDollarCount(int count) {
        this.financialDocumentTenDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT);
    }

    /**
     * Gets the financialDocumentFiveDollarAmount attribute.
     * 
     * @return Returns the financialDocumentFiveDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentFiveDollarAmount() {
        return financialDocumentFiveDollarAmount;
    }

    /**
     * Sets the financialDocumentFiveDollarAmount attribute.
     * 
     * @param financialDocumentFiveDollarAmount The financialDocumentFiveDollarAmount to set.
     * 
     */
    public void setFinancialDocumentFiveDollarAmount(KualiDecimal financialDocumentFiveDollarAmount) {
        this.financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount;
    }

    /**
     * Returns the actual count of five dollar bills
     * @return the number of five dollar bills present in the drawer
     */
    public int getFiveDollarCount() {
        return (financialDocumentFiveDollarAmount != null) ? financialDocumentFiveDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of five dollar bills present in the drawer
     * @param count the number of five dollar bills present in the drawer
     */
    public void setFiveDollarCount(int count) {
        this.financialDocumentFiveDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT);
    }

    /**
     * Gets the financialDocumentTwoDollarAmount attribute.
     * 
     * @return Returns the financialDocumentTwoDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTwoDollarAmount() {
        return financialDocumentTwoDollarAmount;
    }

    /**
     * Sets the financialDocumentTwoDollarAmount attribute.
     * 
     * @param financialDocumentTwoDollarAmount The financialDocumentTwoDollarAmount to set.
     * 
     */
    public void setFinancialDocumentTwoDollarAmount(KualiDecimal financialDocumentTwoDollarAmount) {
        this.financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount;
    }

    /**
     * Returns the actual count of two dollar bills
     * @return the number of two dollar bills present in the drawer
     */
    public int getTwoDollarCount() {
        return (financialDocumentTwoDollarAmount != null) ? financialDocumentTwoDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of two dollar bills present in the drawer
     * @param count the number of two dollar bills present in the drawer
     */
    public void setTwoDollarCount(int count) {
        this.financialDocumentTwoDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT);
    }

    /**
     * Gets the financialDocumentOneDollarAmount attribute.
     * 
     * @return Returns the financialDocumentOneDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentOneDollarAmount() {
        return financialDocumentOneDollarAmount;
    }

    /**
     * Sets the financialDocumentOneDollarAmount attribute.
     * 
     * @param financialDocumentOneDollarAmount The financialDocumentOneDollarAmount to set.
     * 
     */
    public void setFinancialDocumentOneDollarAmount(KualiDecimal financialDocumentOneDollarAmount) {
        this.financialDocumentOneDollarAmount = financialDocumentOneDollarAmount;
    }

    /**
     * Returns the actual count of one dollar bills
     * @return the number of one dollar bills present in the drawer
     */
    public int getOneDollarCount() {
        return (financialDocumentOneDollarAmount != null) ? financialDocumentOneDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT).intValue() : 0;
    }
    
    /**
     * This sets the count of one dollar bills present in the drawer
     * @param count the number of one dollar bills present in the drawer
     */
    public void setOneDollarCount(int count) {
        this.financialDocumentOneDollarAmount = new KualiDecimal(count).multiply(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT);
    }

    /**
     * Gets the financialDocumentOtherDollarAmount attribute.
     * 
     * @return Returns the financialDocumentOtherDollarAmount
     * 
     */
    public KualiDecimal getFinancialDocumentOtherDollarAmount() {
        return financialDocumentOtherDollarAmount;
    }

    /**
     * Sets the financialDocumentOtherDollarAmount attribute.
     * 
     * @param financialDocumentOtherDollarAmount The financialDocumentOtherDollarAmount to set.
     * 
     */
    public void setFinancialDocumentOtherDollarAmount(KualiDecimal financialDocumentOtherDollarAmount) {
        this.financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount;
    }


    /**
     * Gets the financialDocumentFiftyCentAmount attribute.
     * 
     * @return Returns the financialDocumentFiftyCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentFiftyCentAmount() {
        return financialDocumentFiftyCentAmount;
    }

    /**
     * Sets the financialDocumentFiftyCentAmount attribute.
     * 
     * @param financialDocumentFiftyCentAmount The financialDocumentFiftyCentAmount to set.
     * 
     */
    public void setFinancialDocumentFiftyCentAmount(KualiDecimal financialDocumentFiftyCentAmount) {
        this.financialDocumentFiftyCentAmount = financialDocumentFiftyCentAmount;
    }

    /**
     * Returns the number of half-cent coins in the drawer
     * @return the count of half cent coins in the drawer
     */
    public int getFiftyCentCount() {
        return (financialDocumentFiftyCentAmount != null) ? financialDocumentFiftyCentAmount.divide(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of fifty cent coins in the drawer. This is useful if, you know, you're in da club, with, say a bottle full of "bub"
     * @param count the number of fifty cent coins present in the drawer
     */
    public void setFiftyCentCount(int count) {
        financialDocumentFiftyCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentTwentyFiveCentAmount attribute.
     * 
     * @return Returns the financialDocumentTwentyFiveCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTwentyFiveCentAmount() {
        return financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Sets the financialDocumentTwentyFiveCentAmount attribute.
     * 
     * @param financialDocumentTwentyFiveCentAmount The financialDocumentTwentyFiveCentAmount to set.
     * 
     */
    public void setFinancialDocumentTwentyFiveCentAmount(KualiDecimal financialDocumentTwentyFiveCentAmount) {
        this.financialDocumentTwentyFiveCentAmount = financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Returns the number of quarters in the drawer
     * @return the count of quarters in the drawer
     */
    public int getTwentyFiveCentCount() {
        return (financialDocumentTwentyFiveCentAmount != null) ? financialDocumentTwentyFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of quarters in the drawer
     * @param count the number of quarters present in the drawer
     */
    public void setTwentyFiveCentCount(int count) {
        financialDocumentTwentyFiveCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentTenCentAmount attribute.
     * 
     * @return Returns the financialDocumentTenCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentTenCentAmount() {
        return financialDocumentTenCentAmount;
    }

    /**
     * Sets the financialDocumentTenCentAmount attribute.
     * 
     * @param financialDocumentTenCentAmount The financialDocumentTenCentAmount to set.
     * 
     */
    public void setFinancialDocumentTenCentAmount(KualiDecimal financialDocumentTenCentAmount) {
        this.financialDocumentTenCentAmount = financialDocumentTenCentAmount;
    }

    /**
     * Returns the number of dimes in the drawer
     * @return the count of dimes in the drawer
     */
    public int getTenCentCount() {
        return (financialDocumentTenCentAmount != null) ? financialDocumentTenCentAmount.divide(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of dimes in the drawer
     * @param count the number of dimes present in the drawer
     */
    public void setTenCentCount(int count) {
        financialDocumentTenCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentFiveCentAmount attribute.
     * 
     * @return Returns the financialDocumentFiveCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentFiveCentAmount() {
        return financialDocumentFiveCentAmount;
    }

    /**
     * Sets the financialDocumentFiveCentAmount attribute.
     * 
     * @param financialDocumentFiveCentAmount The financialDocumentFiveCentAmount to set.
     * 
     */
    public void setFinancialDocumentFiveCentAmount(KualiDecimal financialDocumentFiveCentAmount) {
        this.financialDocumentFiveCentAmount = financialDocumentFiveCentAmount;
    }

    /**
     * Returns the number of nickels in the drawer
     * @return the count of nickels in the drawer
     */
    public int getFiveCentCount() {
        return (financialDocumentFiveCentAmount != null) ? financialDocumentFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of nickels in the drawer
     * @param count the number of nickels present in the drawer
     */
    public void setFiveCentCount(int count) {
        financialDocumentFiveCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentOneCentAmount attribute.
     * 
     * @return Returns the financialDocumentOneCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentOneCentAmount() {
        return financialDocumentOneCentAmount;
    }

    /**
     * Sets the financialDocumentOneCentAmount attribute.
     * 
     * @param financialDocumentOneCentAmount The financialDocumentOneCentAmount to set.
     * 
     */
    public void setFinancialDocumentOneCentAmount(KualiDecimal financialDocumentOneCentAmount) {
        this.financialDocumentOneCentAmount = financialDocumentOneCentAmount;
    }

    /**
     * Returns the number of pennies in the drawer
     * @return the count of pennies in the drawer
     */
    public int getOneCentCount() {
        return (financialDocumentOneCentAmount != null) ? financialDocumentOneCentAmount.divide(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of pennies in the drawer
     * @param count the number of pennies present in the drawer
     */
    public void setOneCentCount(int count) {
        financialDocumentOneCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentOtherCentAmount attribute.
     * 
     * @return Returns the financialDocumentOtherCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentOtherCentAmount() {
        return financialDocumentOtherCentAmount;
    }

    /**
     * Sets the financialDocumentOtherCentAmount attribute.
     * 
     * @param financialDocumentOtherCentAmount The financialDocumentOtherCentAmount to set.
     * 
     */
    public void setFinancialDocumentOtherCentAmount(KualiDecimal financialDocumentOtherCentAmount) {
        this.financialDocumentOtherCentAmount = financialDocumentOtherCentAmount;
    }


    /**
     * Gets the financialDocumentHundredCentAmount attribute.
     * 
     * @return Returns the financialDocumentHundredCentAmount
     * 
     */
    public KualiDecimal getFinancialDocumentHundredCentAmount() {
        return financialDocumentHundredCentAmount;
    }

    /**
     * Sets the financialDocumentHundredCentAmount attribute.
     * 
     * @param financialDocumentHundredCentAmount The financialDocumentHundredCentAmount to set.
     * 
     */
    public void setFinancialDocumentHundredCentAmount(KualiDecimal financialDocumentHundredCentAmount) {
        this.financialDocumentHundredCentAmount = financialDocumentHundredCentAmount;
    }

    /**
     * Returns the number of dollar coins--Sacajawea, Susan B. Anthony, or otherwise--in the drawer
     * @return the count of dollar coins in the drawer
     */
    public int getHundredCentCount() {
        return (financialDocumentHundredCentAmount != null) ? financialDocumentHundredCentAmount.divide(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT).intValue() : 0;
    }
    
    /**
     * Sets the number of hundred cent coins in the drawer
     * @param count the number of hundred cent coins present in the drawer
     */
    public void setHundredCentCount(int count) {
        financialDocumentHundredCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT);
    }

    /**
     * Gets the financialDocumentMiscellaneousAdvanceAmount attribute.
     * 
     * @return Returns the financialDocumentMiscellaneousAdvanceAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMiscellaneousAdvanceAmount() {
        return financialDocumentMiscellaneousAdvanceAmount;
    }

    /**
     * Sets the financialDocumentMiscellaneousAdvanceAmount attribute.
     * 
     * @param financialDocumentMiscellaneousAdvanceAmount The financialDocumentMiscellaneousAdvanceAmount to set.
     * 
     */
    public void setFinancialDocumentMiscellaneousAdvanceAmount(KualiDecimal financialDocumentMiscellaneousAdvanceAmount) {
        this.financialDocumentMiscellaneousAdvanceAmount = financialDocumentMiscellaneousAdvanceAmount;
    }


    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     * 
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     * 
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
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
