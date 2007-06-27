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

import org.kuali.kfs.KFSConstants;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */
public class CoinDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringRecordSource;
    private KualiDecimal financialDocumentFiftyCentAmount;
    private KualiDecimal financialDocumentTwentyFiveCentAmount;
    private KualiDecimal financialDocumentTenCentAmount;
    private KualiDecimal financialDocumentFiveCentAmount;
    private KualiDecimal financialDocumentOneCentAmount;
    private KualiDecimal financialDocumentOtherCentAmount;
    private KualiDecimal financialDocumentHundredCentAmount;

    /**
     * Default constructor.
     */
    public CoinDetail() {

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
        return financialDocumentFiftyCentAmount.divide(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT).intValue();
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
        return financialDocumentTwentyFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT).intValue();
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
        return financialDocumentTenCentAmount.divide(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT).intValue();
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
        return financialDocumentFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT).intValue();
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
        return financialDocumentOneCentAmount.divide(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT).intValue();
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
        return financialDocumentHundredCentAmount.divide(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT).intValue();
    }
    
    /**
     * Sets the number of hundred cent coins in the drawer
     * @param count the number of hundred cent coins present in the drawer
     */
    public void setHundredCentCount(int count) {
        financialDocumentHundredCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT);
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
