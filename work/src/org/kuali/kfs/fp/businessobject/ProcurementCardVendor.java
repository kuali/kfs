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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardVendor extends BusinessObjectBase {

    private String financialDocumentNumber;
    private Integer financialDocumentTransactionLineNumber;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorZipCode;
    private String visaVendorIdentifier;
    private String vendorOrderNumber;
    private String transactionMerchantCategoryCode;

    /**
     * Default constructor.
     */
    public ProcurementCardVendor() {

    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the financialDocumentTransactionLineNumber attribute.
     * 
     * @return - Returns the financialDocumentTransactionLineNumber
     * 
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * Sets the financialDocumentTransactionLineNumber attribute.
     * 
     * @param financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     * 
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return - Returns the vendorName
     * 
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     * 
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return - Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }


    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return - Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }


    /**
     * Gets the vendorCityName attribute.
     * 
     * @return - Returns the vendorCityName
     * 
     */
    public String getVendorCityName() {
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }


    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return - Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }


    /**
     * Gets the vendorZipCode attribute.
     * 
     * @return - Returns the vendorZipCode
     * 
     */
    public String getVendorZipCode() {
        return vendorZipCode;
    }

    /**
     * Sets the vendorZipCode attribute.
     * 
     * @param vendorZipCode The vendorZipCode to set.
     * 
     */
    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }


    /**
     * Gets the visaVendorIdentifier attribute.
     * 
     * @return - Returns the visaVendorIdentifier
     * 
     */
    public String getVisaVendorIdentifier() {
        return visaVendorIdentifier;
    }

    /**
     * Sets the visaVendorIdentifier attribute.
     * 
     * @param visaVendorIdentifier The visaVendorIdentifier to set.
     * 
     */
    public void setVisaVendorIdentifier(String visaVendorIdentifier) {
        this.visaVendorIdentifier = visaVendorIdentifier;
    }


    /**
     * Gets the vendorOrderNumber attribute.
     * 
     * @return - Returns the vendorOrderNumber
     * 
     */
    public String getVendorOrderNumber() {
        return vendorOrderNumber;
    }

    /**
     * Sets the vendorOrderNumber attribute.
     * 
     * @param vendorOrderNumber The vendorOrderNumber to set.
     * 
     */
    public void setVendorOrderNumber(String vendorOrderNumber) {
        this.vendorOrderNumber = vendorOrderNumber;
    }


    /**
     * Gets the transactionMerchantCategoryCode attribute.
     * 
     * @return - Returns the transactionMerchantCategoryCode
     * 
     */
    public String getTransactionMerchantCategoryCode() {
        return transactionMerchantCategoryCode;
    }

    /**
     * Sets the transactionMerchantCategoryCode attribute.
     * 
     * @param transactionMerchantCategoryCode The transactionMerchantCategoryCode to set.
     * 
     */
    public void setTransactionMerchantCategoryCode(String transactionMerchantCategoryCode) {
        this.transactionMerchantCategoryCode = transactionMerchantCategoryCode;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.financialDocumentTransactionLineNumber != null) {
            m.put("financialDocumentTransactionLineNumber", this.financialDocumentTransactionLineNumber.toString());
        }
        return m;
    }
}
