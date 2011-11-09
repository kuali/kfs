/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *  This class represents customer credit card information
 */
public class CustomerCreditCardInformation extends PersistableBusinessObjectBase {

    private String customerCreditCardNumber;
    private String customerCreditCardHolderName;
    private String customerCreditCardIssuerName;
    private String financialDocumentCreditCardTypeCode;
    private Date customerCreditCardExpenditureDate;
    private String customerNumber;
    private String financialDocumentCreditCardVendorNumber;
    private String customerCreditCardInformationNoteText;

    private CreditCardVendor financialDocumentCreditCardVendor;

    /**
     * Default constructor.
     */
    public CustomerCreditCardInformation() {

    }

    /**
     * Gets the customerCreditCardNumber attribute.
     * 
     * @return Returns the customerCreditCardNumber
     */
    public String getCustomerCreditCardNumber() {
        return customerCreditCardNumber;
    }

    /**
     * Sets the customerCreditCardNumber attribute.
     * 
     * @param customerCreditCardNumber The customerCreditCardNumber to set.
     */
    public void setCustomerCreditCardNumber(String customerCreditCardNumber) {
        this.customerCreditCardNumber = customerCreditCardNumber;
    }


    /**
     * Gets the customerCreditCardHolderName attribute.
     * 
     * @return Returns the customerCreditCardHolderName
     */
    public String getCustomerCreditCardHolderName() {
        return customerCreditCardHolderName;
    }

    /**
     * Sets the customerCreditCardHolderName attribute.
     * 
     * @param customerCreditCardHolderName The customerCreditCardHolderName to set.
     */
    public void setCustomerCreditCardHolderName(String customerCreditCardHolderName) {
        this.customerCreditCardHolderName = customerCreditCardHolderName;
    }


    /**
     * Gets the customerCreditCardIssuerName attribute.
     * 
     * @return Returns the customerCreditCardIssuerName
     */
    public String getCustomerCreditCardIssuerName() {
        return customerCreditCardIssuerName;
    }

    /**
     * Sets the customerCreditCardIssuerName attribute.
     * 
     * @param customerCreditCardIssuerName The customerCreditCardIssuerName to set.
     */
    public void setCustomerCreditCardIssuerName(String customerCreditCardIssuerName) {
        this.customerCreditCardIssuerName = customerCreditCardIssuerName;
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
     * Gets the customerCreditCardExpenditureDate attribute.
     * 
     * @return Returns the customerCreditCardExpenditureDate
     */
    public Date getCustomerCreditCardExpenditureDate() {
        return customerCreditCardExpenditureDate;
    }

    /**
     * Sets the customerCreditCardExpenditureDate attribute.
     * 
     * @param customerCreditCardExpenditureDate The customerCreditCardExpenditureDate to set.
     */
    public void setCustomerCreditCardExpenditureDate(Date customerCreditCardExpenditureDate) {
        this.customerCreditCardExpenditureDate = customerCreditCardExpenditureDate;
    }


    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
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
     * Gets the customerCreditCardInformationNoteText attribute.
     * 
     * @return Returns the customerCreditCardInformationNoteText
     */
    public String getCustomerCreditCardInformationNoteText() {
        return customerCreditCardInformationNoteText;
    }

    /**
     * Sets the customerCreditCardInformationNoteText attribute.
     * 
     * @param customerCreditCardInformationNoteText The customerCreditCardInformationNoteText to set.
     */
    public void setCustomerCreditCardInformationNoteText(String customerCreditCardInformationNoteText) {
        this.customerCreditCardInformationNoteText = customerCreditCardInformationNoteText;
    }


    /**
     * Gets the financialDocumentCreditCardVendor attribute.
     * 
     * @return Returns the financialDocumentCreditCardVendor
     */
    public CreditCardVendor getFinancialDocumentCreditCardVendor() {
        return financialDocumentCreditCardVendor;
    }

    /**
     * Sets the financialDocumentCreditCardVendor attribute.
     * 
     * @param financialDocumentCreditCardVendor The financialDocumentCreditCardVendor to set.
     * @deprecated
     */
    public void setFinancialDocumentCreditCardVendor(CreditCardVendor financialDocumentCreditCardVendor) {
        this.financialDocumentCreditCardVendor = financialDocumentCreditCardVendor;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerCreditCardNumber", this.customerCreditCardNumber);
        return m;
    }
}
