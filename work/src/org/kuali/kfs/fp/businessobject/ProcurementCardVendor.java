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

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a procurement card vendor business object.
 */
public class ProcurementCardVendor extends PersistableBusinessObjectBase {

    private String documentNumber;
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
     * Gets the financialDocumentTransactionLineNumber attribute.
     * 
     * @return Returns the financialDocumentTransactionLineNumber
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * Sets the financialDocumentTransactionLineNumber attribute.
     * 
     * @param financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     */
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }


    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     */
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }


    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     */
    public String getVendorCityName() {
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }


    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     */
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }


    /**
     * Gets the vendorZipCode attribute.
     * 
     * @return Returns the vendorZipCode
     */
    public String getVendorZipCode() {
        return vendorZipCode;
    }

    /**
     * Sets the vendorZipCode attribute.
     * 
     * @param vendorZipCode The vendorZipCode to set.
     */
    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }


    /**
     * Gets the visaVendorIdentifier attribute.
     * 
     * @return Returns the visaVendorIdentifier
     */
    public String getVisaVendorIdentifier() {
        return visaVendorIdentifier;
    }

    /**
     * Sets the visaVendorIdentifier attribute.
     * 
     * @param visaVendorIdentifier The visaVendorIdentifier to set.
     */
    public void setVisaVendorIdentifier(String visaVendorIdentifier) {
        this.visaVendorIdentifier = visaVendorIdentifier;
    }


    /**
     * Gets the vendorOrderNumber attribute.
     * 
     * @return Returns the vendorOrderNumber
     */
    public String getVendorOrderNumber() {
        return vendorOrderNumber;
    }

    /**
     * Sets the vendorOrderNumber attribute.
     * 
     * @param vendorOrderNumber The vendorOrderNumber to set.
     */
    public void setVendorOrderNumber(String vendorOrderNumber) {
        this.vendorOrderNumber = vendorOrderNumber;
    }


    /**
     * Gets the transactionMerchantCategoryCode attribute.
     * 
     * @return Returns the transactionMerchantCategoryCode
     */
    public String getTransactionMerchantCategoryCode() {
        return transactionMerchantCategoryCode;
    }

    /**
     * Sets the transactionMerchantCategoryCode attribute.
     * 
     * @param transactionMerchantCategoryCode The transactionMerchantCategoryCode to set.
     */
    public void setTransactionMerchantCategoryCode(String transactionMerchantCategoryCode) {
        this.transactionMerchantCategoryCode = transactionMerchantCategoryCode;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.financialDocumentTransactionLineNumber != null) {
            m.put("financialDocumentTransactionLineNumber", this.financialDocumentTransactionLineNumber.toString());
        }
        return m;
    }
}
