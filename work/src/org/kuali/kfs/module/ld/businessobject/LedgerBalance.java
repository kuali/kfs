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

package org.kuali.module.labor.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.Balance;

/**
 * 
 */
public class LedgerBalance extends Balance {

    private String positionNumber;
    private String emplid;

    private Balance financialBalance;

    /**
     * Default constructor.
     */
    public LedgerBalance() {
        super();
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the financialBalance attribute.
     * 
     * @return Returns the financialBalance.
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute value.
     * 
     * @param financialBalance The financialBalance to set.
     */
    @Deprecated
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = super.toStringMapper();
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
        return m;
    }

    
    //Methods for backwards compatibility
    // TODO: Other classes should eventually be changed to use the
    // newly named methods and not the backwards compatible ones.
    /**
     * Gets the month1AccountLineAmount attribute.
     * 
     * @return Returns the month1AccountLineAmount
     */
    public KualiDecimal getMonth1AccountLineAmount() {
        return getMonth1Amount();
    }

    /**
     * Sets the month1AccountLineAmount attribute.
     * 
     * @param month1AccountLineAmount The month1AccountLineAmount to set.
     */
    public void setMonth1AccountLineAmount(KualiDecimal month1AccountLineAmount) {
        setMonth1Amount(month1AccountLineAmount);
    }


    /**
     * Gets the month2AccountLineAmount attribute.
     * 
     * @return Returns the month2AccountLineAmount
     */
    public KualiDecimal getMonth2AccountLineAmount() {
        return getMonth2Amount();
    }

    /**
     * Sets the month2AccountLineAmount attribute.
     * 
     * @param month2AccountLineAmount The month2AccountLineAmount to set.
     */
    public void setMonth2AccountLineAmount(KualiDecimal month2AccountLineAmount) {
        setMonth2Amount(month2AccountLineAmount);
    }


    /**
     * Gets the month3AccountLineAmount attribute.
     * 
     * @return Returns the month3AccountLineAmount
     */
    public KualiDecimal getMonth3AccountLineAmount() {
        return getMonth3Amount();
    }

    /**
     * Sets the month3AccountLineAmount attribute.
     * 
     * @param month3AccountLineAmount The month3AccountLineAmount to set.
     */
    public void setMonth3AccountLineAmount(KualiDecimal month3AccountLineAmount) {
        setMonth3AccountLineAmount(month3AccountLineAmount);
    }


    /**
     * Gets the month4AccountLineAmount attribute.
     * 
     * @return Returns the month4AccountLineAmount
     */
    public KualiDecimal getMonth4AccountLineAmount() {
        return getMonth4Amount();
    }

    /**
     * Sets the month4AccountLineAmount attribute.
     * 
     * @param month4AccountLineAmount The month4AccountLineAmount to set.
     */
    public void setMonth4AccountLineAmount(KualiDecimal month4AccountLineAmount) {
        setMonth4AccountLineAmount(month4AccountLineAmount);
    }


    /**
     * Gets the month5AccountLineAmount attribute.
     * 
     * @return Returns the month5AccountLineAmount
     */
    public KualiDecimal getMonth5AccountLineAmount() {
        return getMonth5Amount();
    }

    /**
     * Sets the month5AccountLineAmount attribute.
     * 
     * @param month5AccountLineAmount The month5AccountLineAmount to set.
     */
    public void setMonth5AccountLineAmount(KualiDecimal month5AccountLineAmount) {
        setMonth5AccountLineAmount(month5AccountLineAmount);
    }


    /**
     * Gets the month6AccountLineAmount attribute.
     * 
     * @return Returns the month6AccountLineAmount
     */
    public KualiDecimal getMonth6AccountLineAmount() {
        return getMonth6Amount();
    }

    /**
     * Sets the month6AccountLineAmount attribute.
     * 
     * @param month6AccountLineAmount The month6AccountLineAmount to set.
     */
    public void setMonth6AccountLineAmount(KualiDecimal month6AccountLineAmount) {
        setMonth6AccountLineAmount(month6AccountLineAmount);
    }


    /**
     * Gets the month7AccountLineAmount attribute.
     * 
     * @return Returns the month7AccountLineAmount
     */
    public KualiDecimal getMonth7AccountLineAmount() {
        return getMonth7Amount();
    }

    /**
     * Sets the month7AccountLineAmount attribute.
     * 
     * @param month7AccountLineAmount The month7AccountLineAmount to set.
     */
    public void setMonth7AccountLineAmount(KualiDecimal month7AccountLineAmount) {
        setMonth7AccountLineAmount(month7AccountLineAmount);
    }


    /**
     * Gets the month8AccountLineAmount attribute.
     * 
     * @return Returns the month8AccountLineAmount
     */
    public KualiDecimal getMonth8AccountLineAmount() {
        return getMonth8Amount();
    }

    /**
     * Sets the month8AccountLineAmount attribute.
     * 
     * @param month8AccountLineAmount The month8AccountLineAmount to set.
     */
    public void setMonth8AccountLineAmount(KualiDecimal month8AccountLineAmount) {
        setMonth8AccountLineAmount(month8AccountLineAmount);
    }


    /**
     * Gets the month9AccountLineAmount attribute.
     * 
     * @return Returns the month9AccountLineAmount
     */
    public KualiDecimal getMonth9AccountLineAmount() {
        return getMonth9Amount();
    }

    /**
     * Sets the month9AccountLineAmount attribute.
     * 
     * @param month9AccountLineAmount The month9AccountLineAmount to set.
     */
    public void setMonth9AccountLineAmount(KualiDecimal month9AccountLineAmount) {
        setMonth9AccountLineAmount(month9AccountLineAmount);
    }


    /**
     * Gets the month10AccountLineAmount attribute.
     * 
     * @return Returns the month10AccountLineAmount
     */
    public KualiDecimal getMonth10AccountLineAmount() {
        return getMonth10Amount();
    }

    /**
     * Sets the month10AccountLineAmount attribute.
     * 
     * @param month10AccountLineAmount The month10AccountLineAmount to set.
     */
    public void setMonth10AccountLineAmount(KualiDecimal month10AccountLineAmount) {
        setMonth10AccountLineAmount(month10AccountLineAmount);
    }


    /**
     * Gets the month11AccountLineAmount attribute.
     * 
     * @return Returns the month11AccountLineAmount
     */
    public KualiDecimal getMonth11AccountLineAmount() {
        return getMonth11Amount();
    }

    /**
     * Sets the month11AccountLineAmount attribute.
     * 
     * @param month11AccountLineAmount The month11AccountLineAmount to set.
     */
    public void setMonth11AccountLineAmount(KualiDecimal month11AccountLineAmount) {
        setMonth11AccountLineAmount(month11AccountLineAmount);
    }


    /**
     * Gets the month12AccountLineAmount attribute.
     * 
     * @return Returns the month12AccountLineAmount
     */
    public KualiDecimal getMonth12AccountLineAmount() {
        return getMonth12Amount();
    }

    /**
     * Sets the month12AccountLineAmount attribute.
     * 
     * @param month12AccountLineAmount The month12AccountLineAmount to set.
     */
    public void setMonth12AccountLineAmount(KualiDecimal month12AccountLineAmount) {
        setMonth12AccountLineAmount(month12AccountLineAmount);
    }


    /**
     * Gets the month13AccountLineAmount attribute.
     * 
     * @return Returns the month13AccountLineAmount
     */
    public KualiDecimal getMonth13AccountLineAmount() {
        return getMonth13Amount();
    }

    /**
     * Sets the month13AccountLineAmount attribute.
     * 
     * @param month13AccountLineAmount The month13AccountLineAmount to set.
     */
    public void setMonth13AccountLineAmount(KualiDecimal month13AccountLineAmount) {
        setMonth13AccountLineAmount(month13AccountLineAmount);
    }

    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp() {
        return new Timestamp( getTimestamp().getTime() );
    }

    /**
     * Sets the transactionDateTimeStamp attribute.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        setTimestamp( new Date(transactionDateTimeStamp.getTime()) );
    }
}