/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import org.kuali.rice.krad.bo.AttributeReferenceDummy;

public class GenericAttributes extends AttributeReferenceDummy {

    private String namespaceCode;
    private String searchType;
    private String displayType;
    private String documentTotalAmount;
    private String routingAttributeTitle;

    private Integer transactionEntrySequenceId;
    private Integer sequenceNumber;
    private Integer itemSequenceId;
    private Integer transactionLedgerEntrySequenceNumber;
    private String universityFiscalAccountingPeriod;
    private Integer genericFiscalYear;
    private String maxDollarAmount;
    private String minDollarAmount;
    private String totalDollarAmount;
    private String financialDocumentStatusName;
    private String financialSystemDocumentTypeCode;
    private String referenceTypeCode;

    public GenericAttributes() {
        super();
    }

    public String getFinancialDocumentStatusName() {
        return financialDocumentStatusName;
    }

    public void setFinancialDocumentStatusName(String financialDocumentStatusName) {
        this.financialDocumentStatusName = financialDocumentStatusName;
    }

    public String getDocumentTotalAmount() {
        return documentTotalAmount;
    }

    public void setDocumentTotalAmount(String documentTotalAmount) {
        this.documentTotalAmount = documentTotalAmount;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String changedNamespaceCodes) {
        this.namespaceCode = changedNamespaceCodes;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getRoutingAttributeTitle() {
        return routingAttributeTitle;
    }

    public void setRoutingAttributeTitle(String routingAttributeTitle) {
        this.routingAttributeTitle = routingAttributeTitle;
    }

    public Integer getTransactionEntrySequenceId() {
        return transactionEntrySequenceId;
    }

    public void setTransactionEntrySequenceId(Integer transactionEntrySequenceId) {
        this.transactionEntrySequenceId = transactionEntrySequenceId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getItemSequenceId() {
        return itemSequenceId;
    }
    
    public void setItemSequenceId(Integer itemSequenceId) {
        this.itemSequenceId = itemSequenceId;
    }
    
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }
    
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }
    
    public String getUniversityFiscalAccountingPeriod() {
        return universityFiscalAccountingPeriod;
    }

    public void setUniversityFiscalAccountingPeriod(String universityFiscalAccountingPeriod) {
        this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
    }

    public Integer getGenericFiscalYear() {
        return genericFiscalYear;
    }

    public void setGenericFiscalYear(Integer genericFiscalYear) {
        this.genericFiscalYear = genericFiscalYear;
    }

    public String getMaxDollarAmount() {
        return maxDollarAmount;
    }

    public void setMaxDollarAmount(String maxDollarAmount) {
        this.maxDollarAmount = maxDollarAmount;
    }

    public String getMinDollarAmount() {
        return minDollarAmount;
    }

    public void setMinDollarAmount(String minDollarAmount) {
        this.minDollarAmount = minDollarAmount;
    }

    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute. 
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public String getFinancialSystemDocumentTypeCode() {
        return financialSystemDocumentTypeCode;
    }

    /**
     * Sets the financialSystemDocumentTypeCode attribute value.
     * @param financialSystemDocumentTypeCode The financialSystemDocumentTypeCode to set.
     */
    public void setFinancialSystemDocumentTypeCode(String financialSystemDocumentTypeCode) {
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
    }

    /**
     * Gets the referenceDocumentTypeCode attribute. 
     * @return Returns the referenceDocumentTypeCode.
     */
    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    /**
     * Sets the referenceDocumentTypeCode attribute value.
     * @param referenceDocumentTypeCode The referenceDocumentTypeCode to set.
     */
    public void setReferenceTypeCode(String referenceDocumentTypeCode) {
        this.referenceTypeCode = referenceDocumentTypeCode;
    }
}
