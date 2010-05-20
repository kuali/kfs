/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.sql.Date;

public class AccountParameters implements Serializable {
    
    private String unit;
    private String accountumber;
    private String accountName;
    private String higherEdFunctionCode;
    private String indirectCostTypeCode;
    private String indirectCostRate;
    private String expenseGuidelineText;
    private String incomeGuidelineText;
    private String purposeText;
    private String cfdaNumber;
    
    private Date expirationDate;
    private Date effectiveDate;
    
    private boolean offCampusIndicator;
    
    public AccountParameters() {}

    /**
     * Gets the unit attribute. 
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit attribute value.
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets the accountumber attribute. 
     * @return Returns the accountumber.
     */
    public String getAccountumber() {
        return accountumber;
    }

    /**
     * Sets the accountumber attribute value.
     * @param accountumber The accountumber to set.
     */
    public void setAccountumber(String accountumber) {
        this.accountumber = accountumber;
    }

    /**
     * Gets the accountName attribute. 
     * @return Returns the accountName.
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the accountName attribute value.
     * @param accountName The accountName to set.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the higherEdFunctionCode attribute. 
     * @return Returns the higherEdFunctionCode.
     */
    public String getHigherEdFunctionCode() {
        return higherEdFunctionCode;
    }

    /**
     * Sets the higherEdFunctionCode attribute value.
     * @param higherEdFunctionCode The higherEdFunctionCode to set.
     */
    public void setHigherEdFunctionCode(String higherEdFunctionCode) {
        this.higherEdFunctionCode = higherEdFunctionCode;
    }

    /**
     * Gets the indirectCostTypeCode attribute. 
     * @return Returns the indirectCostTypeCode.
     */
    public String getIndirectCostTypeCode() {
        return indirectCostTypeCode;
    }

    /**
     * Sets the indirectCostTypeCode attribute value.
     * @param indirectCostTypeCode The indirectCostTypeCode to set.
     */
    public void setIndirectCostTypeCode(String indirectCostTypeCode) {
        this.indirectCostTypeCode = indirectCostTypeCode;
    }

    /**
     * Gets the indirectCostRate attribute. 
     * @return Returns the indirectCostRate.
     */
    public String getIndirectCostRate() {
        return indirectCostRate;
    }

    /**
     * Sets the indirectCostRate attribute value.
     * @param indirectCostRate The indirectCostRate to set.
     */
    public void setIndirectCostRate(String indirectCostRate) {
        this.indirectCostRate = indirectCostRate;
    }

    /**
     * Gets the expenseGuidelineText attribute. 
     * @return Returns the expenseGuidelineText.
     */
    public String getExpenseGuidelineText() {
        return expenseGuidelineText;
    }

    /**
     * Sets the expenseGuidelineText attribute value.
     * @param expenseGuidelineText The expenseGuidelineText to set.
     */
    public void setExpenseGuidelineText(String expenseGuidelineText) {
        this.expenseGuidelineText = expenseGuidelineText;
    }

    /**
     * Gets the incomeGuidelineText attribute. 
     * @return Returns the incomeGuidelineText.
     */
    public String getIncomeGuidelineText() {
        return incomeGuidelineText;
    }

    /**
     * Sets the incomeGuidelineText attribute value.
     * @param incomeGuidelineText The incomeGuidelineText to set.
     */
    public void setIncomeGuidelineText(String incomeGuidelineText) {
        this.incomeGuidelineText = incomeGuidelineText;
    }

    /**
     * Gets the purposeText attribute. 
     * @return Returns the purposeText.
     */
    public String getPurposeText() {
        return purposeText;
    }

    /**
     * Sets the purposeText attribute value.
     * @param purposeText The purposeText to set.
     */
    public void setPurposeText(String purposeText) {
        this.purposeText = purposeText;
    }

    /**
     * Gets the cfdaNumber attribute. 
     * @return Returns the cfdaNumber.
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute value.
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    /**
     * Gets the expirationDate attribute. 
     * @return Returns the expirationDate.
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expirationDate attribute value.
     * @param expirationDate The expirationDate to set.
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the effectiveDate attribute. 
     * @return Returns the effectiveDate.
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effectiveDate attribute value.
     * @param effectiveDate The effectiveDate to set.
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * Gets the offCampusIndicator attribute. 
     * @return Returns the offCampusIndicator.
     */
    public boolean isOffCampusIndicator() {
        return offCampusIndicator;
    }

    /**
     * Sets the offCampusIndicator attribute value.
     * @param offCampusIndicator The offCampusIndicator to set.
     */
    public void setOffCampusIndicator(boolean offCampusIndicator) {
        this.offCampusIndicator = offCampusIndicator;
    }
    
}
