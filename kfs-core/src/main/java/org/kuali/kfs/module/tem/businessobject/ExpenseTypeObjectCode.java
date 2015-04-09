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
package org.kuali.kfs.module.tem.businessobject;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A record which matches an expense type, document type, trip type, and traveler type with an object code and other information about the expense in the specific context
 */
public class ExpenseTypeObjectCode extends PersistableBusinessObjectBase implements MutableInactivatable {
    private Long expenseTypeObjectCodeId;

    private String expenseTypeCode;
    private String tripTypeCode;
    private String travelerTypeCode;
    private String documentTypeName;

    private String financialObjectCode;
    private KualiDecimal maximumAmount;
    private String maximumAmountSummationCode;
    private String errorTypeCode;
    private boolean noteRequired;
    private boolean receiptRequired;
    private KualiDecimal receiptRequirementThreshold;
    private boolean taxable;
    private boolean specialRequestRequired;
    private boolean active;

    /* fields for lookup, set to access="readonly" in ojb descriptor */
    private String tripTypeCodeForLookup;
    private String travelerTypeCodeForLookup;
    private String documentTypeNameForLookup;
    private transient DocumentTypeEBO documentTypeForLookup;
    private transient TripType tripTypeForLookup;
    private transient TravelerType travelerTypeForLookup;

    private transient DocumentTypeEBO documentType;

    private ExpenseType expenseType;

    public Long getExpenseTypeObjectCodeId() {
        return expenseTypeObjectCodeId;
    }
    public void setExpenseTypeObjectCodeId(Long expenseTypeObjectCodeId) {
        this.expenseTypeObjectCodeId = expenseTypeObjectCodeId;
    }

    public String getExpenseTypeCode() {
        return expenseTypeCode;
    }
    public void setExpenseTypeCode(String expenseTypeCode) {
        this.expenseTypeCode = expenseTypeCode;
    }
    public String getTripTypeCode() {
        return tripTypeCode;
    }
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }
    public String getTravelerTypeCode() {
        return travelerTypeCode;
    }
    public void setTravelerTypeCode(String travelerTypeCode) {
        this.travelerTypeCode = travelerTypeCode;
    }
    public String getDocumentTypeName() {
        return documentTypeName;
    }
    public void setDocumentTypeName(String documentType) {
        this.documentTypeName = documentType;
    }
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
    public KualiDecimal getMaximumAmount() {
        return maximumAmount;
    }
    public void setMaximumAmount(KualiDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }
    public String getMaximumAmountSummationCode() {
        return maximumAmountSummationCode;
    }
    public void setMaximumAmountSummationCode(String maximumAmountSummationCode) {
        this.maximumAmountSummationCode = maximumAmountSummationCode;
    }
    public String getErrorTypeCode() {
        return errorTypeCode;
    }
    public void setErrorTypeCode(String errorTypeCode) {
        this.errorTypeCode = errorTypeCode;
    }
    public boolean isNoteRequired() {
        return noteRequired;
    }
    public void setNoteRequired(boolean noteRequired) {
        this.noteRequired = noteRequired;
    }
    public boolean isReceiptRequired() {
        return receiptRequired;
    }
    public void setReceiptRequired(boolean receiptRequired) {
        this.receiptRequired = receiptRequired;
    }
    public KualiDecimal getReceiptRequirementThreshold() {
        return receiptRequirementThreshold;
    }
    public void setReceiptRequirementThreshold(KualiDecimal receiptRequirementThreshold) {
        this.receiptRequirementThreshold = receiptRequirementThreshold;
    }
    public boolean isTaxable() {
        return taxable;
    }
    public void setTaxable(boolean taxableIndicator) {
        this.taxable = taxableIndicator;
    }
    public boolean isSpecialRequestRequired() {
        return specialRequestRequired;
    }
    public void setSpecialRequestRequired(boolean specialRequestRequired) {
        this.specialRequestRequired = specialRequestRequired;
    }
    @Override
    public boolean isActive() {
        return active;
    }
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    public ExpenseType getExpenseType() {
        return expenseType;
    }
    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    /* fields for lookup */
    public String getTripTypeCodeForLookup() {
        return tripTypeCodeForLookup;
    }
    public void setTripTypeCodeForLookup(String tripTypeCodeForLookup) {
        this.tripTypeCodeForLookup = tripTypeCodeForLookup;
    }
    public String getTravelerTypeCodeForLookup() {
        return travelerTypeCodeForLookup;
    }
    public void setTravelerTypeCodeForLookup(String travelerTypeCodeForLookup) {
        this.travelerTypeCodeForLookup = travelerTypeCodeForLookup;
    }
    public String getDocumentTypeNameForLookup() {
        return documentTypeNameForLookup;
    }
    public void setDocumentTypeNameForLookup(String documentTypeNameForLookup) {
        this.documentTypeNameForLookup = documentTypeNameForLookup;
    }
    public DocumentTypeEBO getDocumentTypeForLookup() {
        return documentTypeForLookup;
    }
    public void setDocumentTypeForLookup(DocumentTypeEBO documentTypeForLookup) {
        this.documentTypeForLookup = documentTypeForLookup;
    }
    public TripType getTripTypeForLookup() {
        return tripTypeForLookup;
    }
    public void setTripTypeForLookup(TripType tripTypeForLookup) {
        this.tripTypeForLookup = tripTypeForLookup;
    }
    public TravelerType getTravelerTypeForLookup() {
        return travelerTypeForLookup;
    }
    public void setTravelerTypeForLookup(TravelerType travelerTypeForLookup) {
        this.travelerTypeForLookup = travelerTypeForLookup;
    }

    /* calculated properties */
    @Transient
    public boolean isPerDaily(){
        return !StringUtils.isBlank(getMaximumAmountSummationCode()) && ExpenseTypeAmountSummation.PER_DAILY.getCode().equals(getMaximumAmountSummationCode());
    }

    @Transient
    public Boolean isPerOccurrence(){
        return !StringUtils.isBlank(getMaximumAmountSummationCode()) && ExpenseTypeAmountSummation.PER_OCCURRENCE.getCode().equals(getMaximumAmountSummationCode());
    }
    public DocumentTypeEBO getDocumentType() {
        return documentType;
    }
    public void setDocumentType(DocumentTypeEBO documentType) {
        this.documentType = documentType;
    }
    @Override
    public String toString() {
        final String idAsString = (expenseTypeObjectCodeId == null) ? "?" : expenseTypeObjectCodeId.toString();
        return idAsString +": "+expenseTypeCode+"-"+documentTypeName+"-"+travelerTypeCode+"-"+tripTypeCode;
    }

    /**
     * Convenience method that exists really to support DWR, which evidently doesn't like nested properties
     * @return the expense type meta category code for the related expense type if there is one
     */
    public String getExpenseTypeMetaCategoryCode() {
        if (!ObjectUtils.isNull(expenseType)) {
            return expenseType.getExpenseTypeMetaCategoryCode();
        }
        return KFSConstants.EMPTY_STRING;
    }
}
