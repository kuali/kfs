/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditCardInformation extends BusinessObjectBase {

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
     * @return - Returns the customerCreditCardNumber
     * 
     */
    public String getCustomerCreditCardNumber() {
        return customerCreditCardNumber;
    }

    /**
     * Sets the customerCreditCardNumber attribute.
     * 
     * @param customerCreditCardNumber The customerCreditCardNumber to set.
     * 
     */
    public void setCustomerCreditCardNumber(String customerCreditCardNumber) {
        this.customerCreditCardNumber = customerCreditCardNumber;
    }


    /**
     * Gets the customerCreditCardHolderName attribute.
     * 
     * @return - Returns the customerCreditCardHolderName
     * 
     */
    public String getCustomerCreditCardHolderName() {
        return customerCreditCardHolderName;
    }

    /**
     * Sets the customerCreditCardHolderName attribute.
     * 
     * @param customerCreditCardHolderName The customerCreditCardHolderName to set.
     * 
     */
    public void setCustomerCreditCardHolderName(String customerCreditCardHolderName) {
        this.customerCreditCardHolderName = customerCreditCardHolderName;
    }


    /**
     * Gets the customerCreditCardIssuerName attribute.
     * 
     * @return - Returns the customerCreditCardIssuerName
     * 
     */
    public String getCustomerCreditCardIssuerName() {
        return customerCreditCardIssuerName;
    }

    /**
     * Sets the customerCreditCardIssuerName attribute.
     * 
     * @param customerCreditCardIssuerName The customerCreditCardIssuerName to set.
     * 
     */
    public void setCustomerCreditCardIssuerName(String customerCreditCardIssuerName) {
        this.customerCreditCardIssuerName = customerCreditCardIssuerName;
    }


    /**
     * Gets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @return - Returns the financialDocumentCreditCardTypeCode
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
     * Gets the customerCreditCardExpenditureDate attribute.
     * 
     * @return - Returns the customerCreditCardExpenditureDate
     * 
     */
    public Date getCustomerCreditCardExpenditureDate() {
        return customerCreditCardExpenditureDate;
    }

    /**
     * Sets the customerCreditCardExpenditureDate attribute.
     * 
     * @param customerCreditCardExpenditureDate The customerCreditCardExpenditureDate to set.
     * 
     */
    public void setCustomerCreditCardExpenditureDate(Date customerCreditCardExpenditureDate) {
        this.customerCreditCardExpenditureDate = customerCreditCardExpenditureDate;
    }


    /**
     * Gets the customerNumber attribute.
     * 
     * @return - Returns the customerNumber
     * 
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     * 
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @return - Returns the financialDocumentCreditCardVendorNumber
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
     * Gets the customerCreditCardInformationNoteText attribute.
     * 
     * @return - Returns the customerCreditCardInformationNoteText
     * 
     */
    public String getCustomerCreditCardInformationNoteText() {
        return customerCreditCardInformationNoteText;
    }

    /**
     * Sets the customerCreditCardInformationNoteText attribute.
     * 
     * @param customerCreditCardInformationNoteText The customerCreditCardInformationNoteText to set.
     * 
     */
    public void setCustomerCreditCardInformationNoteText(String customerCreditCardInformationNoteText) {
        this.customerCreditCardInformationNoteText = customerCreditCardInformationNoteText;
    }


    /**
     * Gets the financialDocumentCreditCardVendor attribute.
     * 
     * @return - Returns the financialDocumentCreditCardVendor
     * 
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerCreditCardNumber", this.customerCreditCardNumber);
        return m;
    }
}
