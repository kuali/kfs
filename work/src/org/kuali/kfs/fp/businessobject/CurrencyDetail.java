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

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants;

/**
 * 
 */
public class CurrencyDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringRecordSource;
    private KualiDecimal financialDocumentHundredDollarAmount;
    private KualiDecimal financialDocumentFiftyDollarAmount;
    private KualiDecimal financialDocumentTwentyDollarAmount;
    private KualiDecimal financialDocumentTenDollarAmount;
    private KualiDecimal financialDocumentFiveDollarAmount;
    private KualiDecimal financialDocumentTwoDollarAmount;
    private KualiDecimal financialDocumentOneDollarAmount;
    private KualiDecimal financialDocumentOtherDollarAmount;

    /**
     * Default constructor.
     */
    public CurrencyDetail() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the cashieringRecordSource attribute.
     * 
     * @return Returns the cashieringRecordSource
     * 
     */
    public String getCashieringRecordSource() {
        return cashieringRecordSource;
    }

    /**
     * Sets the cashieringRecordSource attribute.
     * 
     * @param cashieringRecordSource The cashieringRecordSource to set.
     * 
     */
    public void setCashieringRecordSource(String financialDocumentColumnTypeCode) {
        this.cashieringRecordSource = financialDocumentColumnTypeCode;
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
        return financialDocumentHundredDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentFiftyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentTwentyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentTenDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentFiveDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentTwoDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT).intValue();
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
        return financialDocumentOneDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT).intValue();
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("cashieringRecordSource", this.cashieringRecordSource);
        return m;
    }
}
