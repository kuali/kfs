/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/CreditCardDetail.java,v $
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.PropertyConstants;

/**
 * This class represents the CreditCardDetail business object, which is a single record on the Credit Card Receipts Document
 * representing a single credit card receipt. This is a type of advance deposit.
 * 
 * 
 */
public class CreditCardDetail extends BusinessObjectBase {
    private String documentNumber;
    private String financialDocumentTypeCode;
    private String financialDocumentColumnTypeCode;
    private Integer financialDocumentLineNumber;
    private String financialDocumentCreditCardTypeCode;
    private String financialDocumentCreditCardVendorNumber;
    private Date creditCardDepositDate;
    private String creditCardDepositReferenceNumber;
    private KualiDecimal creditCardAdvanceDepositAmount;

    private CreditCardType financialDocumentCreditCardType;
    private CreditCardVendor financialDocumentCreditCardVendor;

    /**
     * Default constructor.
     */
    public CreditCardDetail() {
        super();
        this.financialDocumentLineNumber = new Integer(1);
        this.creditCardAdvanceDepositAmount = new KualiDecimal(0);
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
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     * 
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     * 
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }


    /**
     * Gets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @return Returns the financialDocumentCreditCardTypeCode
     * 
     */
    public String getFinancialDocumentCreditCardTypeCode() {
        return financialDocumentCreditCardTypeCode;
    }

    /**
     * Sets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @param financialDocumentCreditCardTypeCode The financialDocumentCreditCardTypeCode to set.
     * 
     */
    public void setFinancialDocumentCreditCardTypeCode(String financialDocumentCreditCardTypeCode) {
        this.financialDocumentCreditCardTypeCode = financialDocumentCreditCardTypeCode;
    }


    /**
     * Gets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @return Returns the financialDocumentCreditCardVendorNumber
     * 
     */
    public String getFinancialDocumentCreditCardVendorNumber() {
        return financialDocumentCreditCardVendorNumber;
    }

    /**
     * Sets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @param financialDocumentCreditCardVendorNumber The financialDocumentCreditCardVendorNumber to set.
     * 
     */
    public void setFinancialDocumentCreditCardVendorNumber(String financialDocumentCreditCardVendorNumber) {
        this.financialDocumentCreditCardVendorNumber = financialDocumentCreditCardVendorNumber;
    }


    /**
     * Gets the creditCardDepositDate attribute.
     * 
     * @return Returns the creditCardDepositDate
     * 
     */
    public Date getCreditCardDepositDate() {
        return creditCardDepositDate;
    }

    /**
     * Sets the creditCardDepositDate attribute.
     * 
     * @param creditCardDepositDate The creditCardDepositDate to set.
     * 
     */
    public void setCreditCardDepositDate(Date creditCardDepositDate) {
        this.creditCardDepositDate = creditCardDepositDate;
    }


    /**
     * Gets the creditCardDepositReferenceNumber attribute.
     * 
     * @return Returns the creditCardDepositReferenceNumber
     * 
     */
    public String getCreditCardDepositReferenceNumber() {
        return creditCardDepositReferenceNumber;
    }

    /**
     * Sets the creditCardDepositReferenceNumber attribute.
     * 
     * @param creditCardDepositReferenceNumber The creditCardDepositReferenceNumber to set.
     * 
     */
    public void setCreditCardDepositReferenceNumber(String creditCardDepositReferenceNumber) {
        this.creditCardDepositReferenceNumber = creditCardDepositReferenceNumber;
    }


    /**
     * Gets the creditCardAdvanceDepositAmount attribute.
     * 
     * @return Returns the creditCardAdvanceDepositAmount
     * 
     */
    public KualiDecimal getCreditCardAdvanceDepositAmount() {
        return creditCardAdvanceDepositAmount;
    }

    /**
     * Sets the creditCardAdvanceDepositAmount attribute.
     * 
     * @param creditCardAdvanceDepositAmount The creditCardAdvanceDepositAmount to set.
     * 
     */
    public void setCreditCardAdvanceDepositAmount(KualiDecimal creditCardAdvanceDepositAmount) {
        this.creditCardAdvanceDepositAmount = creditCardAdvanceDepositAmount;
    }

    /**
     * @return Returns the financialDocumentCreditCardType.
     */
    public CreditCardType getFinancialDocumentCreditCardType() {
        return financialDocumentCreditCardType;
    }

    /**
     * @param financialDocumentCreditCardType The financialDocumentCreditCardType to set.
     * @deprecated
     */
    public void setFinancialDocumentCreditCardType(CreditCardType financialDocumentCreditCardType) {
        this.financialDocumentCreditCardType = financialDocumentCreditCardType;
    }

    /**
     * @return Returns the financialDocumentCreditCardVendor.
     */
    public CreditCardVendor getFinancialDocumentCreditCardVendor() {
        return financialDocumentCreditCardVendor;
    }

    /**
     * @param financialDocumentCreditCardVendor The financialDocumentCreditCardVendor to set.
     */
    public void setFinancialDocumentCreditCardVendor(CreditCardVendor financialDocumentCreditCardVendor) {
        this.financialDocumentCreditCardVendor = financialDocumentCreditCardVendor;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialDocumentColumnTypeCode", this.financialDocumentColumnTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}