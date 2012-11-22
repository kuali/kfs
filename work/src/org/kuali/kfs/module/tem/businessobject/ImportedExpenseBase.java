/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class ImportedExpenseBase extends PersistableBusinessObjectBase {

    protected Long id;
    protected Integer creditCardAgencyId;
    protected CreditCardAgency creditCardAgency;
    protected Date importDate;
    protected String travelCompany;
    protected String location;
    protected String travelExpenseType;
    protected KualiDecimal amount;
    protected Date transactionPostingDate;
    protected Date reconciliationDate;
    protected String reconciled;
    protected String description;
    protected KualiDecimal currencyRate = new KualiDecimal(1.000);
    protected KualiDecimal convertedAmount;
    protected Boolean reimbursable = Boolean.TRUE;
    protected Boolean missingReceipt = Boolean.FALSE;

    protected Boolean assigned = Boolean.FALSE;
    protected String documentNumber;

    protected Date expenseNotificationDate;

    /**
     * Gets the id attribute.
     * @return Returns the id.
     */
    @Column(name = "ID", nullable = false)
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the creditCardAgencyId attribute.
     * @return Returns the creditCardAgencyId.
     */
    @Column(name="CC_AGENCY_ID",nullable=false)
    public Integer getCreditCardAgencyId() {
        return creditCardAgencyId;
    }

    /**
     * Sets the creditCardAgencyId attribute value.
     * @param creditCardAgencyId The creditCardAgencyId to set.
     */
    public void setCreditCardAgencyId(Integer creditCardAgencyId) {
        this.creditCardAgencyId = creditCardAgencyId;
    }

    /**
     * Gets the creditCardAgency attribute.
     * @return Returns the creditCardAgency.
     */
    @ManyToOne
    @JoinColumn(name="CREDIT_AGENCY_CD")
    public CreditCardAgency getCreditCardAgency() {
        return creditCardAgency;
    }

    /**
     * Sets the creditCardAgency attribute value.
     * @param creditCardAgency The creditCardAgency to set.
     */
    public void setCreditCardAgency(CreditCardAgency creditCardAgency) {
        this.creditCardAgency = creditCardAgency;
    }

    /**
     * Gets the importDate attribute.
     * @return Returns the importDate.
     */
    @Column(name="IMPORT_DT")
    public Date getImportDate() {
        return importDate;
    }

    /**
     * Sets the importDate attribute value.
     * @param importDate The importDate to set.
     */
    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    /**
     * Gets the travelCompany attribute.
     * @return Returns the travelCompany.
     */
    @Column(name="COMPANY")
    public String getTravelCompany() {
        return travelCompany;
    }

    /**
     * Sets the travelCompany attribute value.
     * @param travelCompany The travelCompany to set.
     */
    public void setTravelCompany(String travelCompany) {
        this.travelCompany = travelCompany;
    }

    /**
     * Gets the location attribute.
     * @return Returns the location.
     */
    @Column(name="LOCATION")
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location attribute value.
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the travelExpenseType attribute.
     * @return Returns the travelExpenseType.
     */
    @Column(name="DV_EXP_CD")
    public String getTravelExpenseType() {
        return travelExpenseType;
    }

    /**
     * Sets the travelExpenseType attribute value.
     * @param travelExpenseType The travelExpenseType to set.
     */
    public void setTravelExpenseType(String travelExpenseType) {
        this.travelExpenseType = travelExpenseType;
    }

    /**
     * Gets the amount attribute.
     * @return Returns the amount.
     */
    @Column(name="AMOUNT")
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount attribute value.
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the transactionPostingDate attribute.
     * @return Returns the transactionPostingDate.
     */
    @Column(name="TRANS_POST_DT")
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute value.
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the reconciliationDate attribute.
     * @return Returns the reconciliationDate.
     */
    @Column(name="RECON_DT")
    public Date getReconciliationDate() {
        return reconciliationDate;
    }

    /**
     * Sets the reconciliationDate attribute value.
     * @param reconciliationDate The reconciliationDate to set.
     */
    public void setReconciliationDate(Date reconciliationDate) {
        this.reconciliationDate = reconciliationDate;
    }

    /**
     * Gets the reconciled attribute.
     * @return Returns the reconciled.
     */
    @Column(name="RECONCILED")
    public String getReconciled() {
        return reconciled;
    }

    /**
     * Sets the reconciled attribute value.
     * @param reconciled The reconciled to set.
     */
    public void setReconciled(String reconciled) {
        this.reconciled = reconciled;
    }

    /**
     * Gets the description attribute.
     * @return Returns the description.
     */
    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the currencyRate attribute.
     * @return Returns the currencyRate.
     */
    @Column(name="CURR_RT")
    public KualiDecimal getCurrencyRate() {
        return currencyRate;
    }

    /**
     * Sets the currencyRate attribute value.
     * @param currencyRate The currencyRate to set.
     */
    public void setCurrencyRate(KualiDecimal currencyRate) {
        this.currencyRate = currencyRate;
    }

    /**
     * Gets the convertedAmount attribute.
     * @return Returns the convertedAmount.
     */
    @Column(name="CONV_AMT")
    public KualiDecimal getConvertedAmount() {
        if (convertedAmount == null){
            convertedAmount = amount.multiply(currencyRate);
        }
        return convertedAmount;
    }

    /**
     * Sets the convertedAmount attribute value.
     * @param convertedAmount The convertedAmount to set.
     */
    public void setConvertedAmount(KualiDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    /**
     * Gets the reimbursable attribute.
     * @return Returns the reimbursable.
     */
    @Column(name="REIMB")
    public Boolean getReimbursable() {
        return reimbursable;
    }

    /**
     * Sets the reimbursable attribute value.
     * @param reimbursable The reimbursable to set.
     */
    public void setReimbursable(Boolean reimbursable) {
        this.reimbursable = reimbursable;
    }

    /**
     * Gets the missingReceipt attribute.
     * @return Returns the missingReceipt.
     */
    @Column(name="MISSING_RCPT")
    public Boolean getMissingReceipt() {
        return missingReceipt;
    }

    /**
     * Sets the missingReceipt attribute value.
     * @param missingReceipt The missingReceipt to set.
     */
    public void setMissingReceipt(Boolean missingReceipt) {
        this.missingReceipt = missingReceipt;
    }

    /**
     * Gets the assigned attribute.
     * @return Returns the assigned.
     */
    @Column(name="ASSIGNED")
    public Boolean getAssigned() {
        return assigned;
    }

    /**
     * Sets the assigned attribute value.
     * @param assigned The assigned to set.
     */
    public void setAssigned(Boolean assigned) {
        this.assigned = assigned;
    }

    /**
     * Gets the documentNumber attribute.
     * @return Returns the documentNumber.
     */
    @Column(name="DOC_NBR")
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the expenseNotificationDate attribute.
     * @return Returns the expenseNotificationDate.
     */
    @Column(name="EXP_NTF_DT")
    public Date getExpenseNotificationDate() {
        return expenseNotificationDate;
    }

    /**
     * Sets the expenseNotificationDate attribute value.
     * @param expenseNotificationDate The expenseNotificationDate to set.
     */
    public void setExpenseNotificationDate(Date expenseNotificationDate) {
        this.expenseNotificationDate = expenseNotificationDate;
    }


}
