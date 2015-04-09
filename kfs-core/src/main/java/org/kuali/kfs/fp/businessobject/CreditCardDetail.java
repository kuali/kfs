/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents the CreditCardDetail business object, which is a single record on the Credit Card Receipts Document
 * representing a single credit card receipt. This is a type of advance deposit.
 */
public class CreditCardDetail extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String financialDocumentTypeCode;
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
        this.creditCardAdvanceDepositAmount = KualiDecimal.ZERO;
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
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }


    /**
     * Gets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @return Returns the financialDocumentCreditCardTypeCode
     */
    public String getFinancialDocumentCreditCardTypeCode() {
        return financialDocumentCreditCardTypeCode;
    }

    /**
     * Sets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @param financialDocumentCreditCardTypeCode The financialDocumentCreditCardTypeCode to set.
     */
    public void setFinancialDocumentCreditCardTypeCode(String financialDocumentCreditCardTypeCode) {
        this.financialDocumentCreditCardTypeCode = financialDocumentCreditCardTypeCode;
    }


    /**
     * Gets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @return Returns the financialDocumentCreditCardVendorNumber
     */
    public String getFinancialDocumentCreditCardVendorNumber() {
        return financialDocumentCreditCardVendorNumber;
    }

    /**
     * Sets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @param financialDocumentCreditCardVendorNumber The financialDocumentCreditCardVendorNumber to set.
     */
    public void setFinancialDocumentCreditCardVendorNumber(String financialDocumentCreditCardVendorNumber) {
        this.financialDocumentCreditCardVendorNumber = financialDocumentCreditCardVendorNumber;
    }


    /**
     * Gets the creditCardDepositDate attribute.
     * 
     * @return Returns the creditCardDepositDate
     */
    public Date getCreditCardDepositDate() {
        return creditCardDepositDate;
    }

    /**
     * Sets the creditCardDepositDate attribute.
     * 
     * @param creditCardDepositDate The creditCardDepositDate to set.
     */
    public void setCreditCardDepositDate(Date creditCardDepositDate) {
        this.creditCardDepositDate = creditCardDepositDate;
    }


    /**
     * Gets the creditCardDepositReferenceNumber attribute.
     * 
     * @return Returns the creditCardDepositReferenceNumber
     */
    public String getCreditCardDepositReferenceNumber() {
        return creditCardDepositReferenceNumber;
    }

    /**
     * Sets the creditCardDepositReferenceNumber attribute.
     * 
     * @param creditCardDepositReferenceNumber The creditCardDepositReferenceNumber to set.
     */
    public void setCreditCardDepositReferenceNumber(String creditCardDepositReferenceNumber) {
        this.creditCardDepositReferenceNumber = creditCardDepositReferenceNumber;
    }


    /**
     * Gets the creditCardAdvanceDepositAmount attribute.
     * 
     * @return Returns the creditCardAdvanceDepositAmount
     */
    public KualiDecimal getCreditCardAdvanceDepositAmount() {
        return creditCardAdvanceDepositAmount;
    }

    /**
     * Sets the creditCardAdvanceDepositAmount attribute.
     * 
     * @param creditCardAdvanceDepositAmount The creditCardAdvanceDepositAmount to set.
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
