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
