/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/CurrencyDetail.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class CurrencyDetail extends BusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String financialDocumentColumnTypeCode;
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
     * Gets the financialDocumentColumnTypeCode attribute.
     * 
     * @return Returns the financialDocumentColumnTypeCode
     * 
     */
    public String getFinancialDocumentColumnTypeCode() {
        return financialDocumentColumnTypeCode;
    }

    /**
     * Sets the financialDocumentColumnTypeCode attribute.
     * 
     * @param financialDocumentColumnTypeCode The financialDocumentColumnTypeCode to set.
     * 
     */
    public void setFinancialDocumentColumnTypeCode(String financialDocumentColumnTypeCode) {
        this.financialDocumentColumnTypeCode = financialDocumentColumnTypeCode;
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
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialDocumentColumnTypeCode", this.financialDocumentColumnTypeCode);
        return m;
    }
}
