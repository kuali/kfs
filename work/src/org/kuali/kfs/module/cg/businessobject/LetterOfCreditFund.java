/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Letter Of Credit Fund under Contracts and Grants section.
 */
public class LetterOfCreditFund extends PersistableBusinessObjectBase implements ContractsAndGrantsLetterOfCreditFund {

    private String letterOfCreditFundCode;
    private String fundDescription;

    private String letterOfCreditFundGroupCode;
    private KualiDecimal letterOfCreditFundAmount;

    private Date letterOfCreditFundStartDate;
    private Date letterOfCreditFundExpirationDate;

    private LetterOfCreditFundGroup letterOfCreditFundGroup;
    private boolean active;


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the letterOfCreditFundGroup attribute.
     * 
     * @return Returns the letterOfCreditFundGroup.
     */
    public LetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup;
    }


    /**
     * Sets the letterOfCreditFundGroup attribute value.
     * 
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     */
    public void setLetterOfCreditFundGroup(LetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }


    /**
     * Gets the fundCode attribute.
     * 
     * @return Returns the fundCode.
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }


    /**
     * Sets the fundCode attribute value.
     * 
     * @param fundCode The fundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }


    /**
     * Gets the fundDescr attribute.
     * 
     * @return Returns the fundDescr.
     */
    public String getFundDescription() {
        return fundDescription;
    }


    /**
     * Sets the fundDescr attribute value.
     * 
     * @param fundDescr The fundDescr to set.
     */
    public void setFundDescription(String fundDescr) {
        this.fundDescription = fundDescr;
    }


    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     * 
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }


    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     * 
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundAmount attribute.
     * 
     * @return Returns the letterOfCreditFundAmount.
     */
    public KualiDecimal getLetterOfCreditFundAmount() {
        return letterOfCreditFundAmount;
    }


    /**
     * Sets the letterOfCreditFundAmount attribute value.
     * 
     * @param letterOfCreditFundAmount The letterOfCreditFundAmount to set.
     */
    public void setLetterOfCreditFundAmount(KualiDecimal letterOfCreditFundAmount) {
        this.letterOfCreditFundAmount = letterOfCreditFundAmount;
    }


    /**
     * Gets the letterOfCreditFundStartDate attribute.
     * 
     * @return Returns the letterOfCreditFundStartDate.
     */
    public Date getLetterOfCreditFundStartDate() {
        return letterOfCreditFundStartDate;
    }


    /**
     * Sets the letterOfCreditFundStartDate attribute value.
     * 
     * @param letterOfCreditFundStartDate The letterOfCreditFundStartDate to set.
     */
    public void setLetterOfCreditFundStartDate(Date letterOfCreditFundStartDate) {
        this.letterOfCreditFundStartDate = letterOfCreditFundStartDate;
    }


    /**
     * Gets the letterOfCreditFundExpirationDate attribute.
     * 
     * @return Returns the letterOfCreditFundExpirationDate.
     */
    public Date getLetterOfCreditFundExpirationDate() {
        return letterOfCreditFundExpirationDate;
    }


    /**
     * Sets the letterOfCreditFundExpirationDate attribute value.
     * 
     * @param letterOfCreditFundExpirationDate The letterOfCreditFundExpirationDate to set.
     */
    public void setLetterOfCreditFundExpirationDate(Date letterOfCreditFundExpirationDate) {
        this.letterOfCreditFundExpirationDate = letterOfCreditFundExpirationDate;
    }


    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("letterOfCreditFundCode", this.letterOfCreditFundCode.toString());
        m.put("fundDescr", this.fundDescription.toString());
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode.toString());
        m.put("letterOfCreditFundAmount", this.letterOfCreditFundAmount.toString());
        m.put("letterOfCreditFundStartDate", this.letterOfCreditFundStartDate.toString());
        m.put("letterOfCreditFundExpirationDate", this.letterOfCreditFundExpirationDate.toString());
        return m;
    }


}
