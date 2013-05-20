/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Persistent class for detailed fields of Referral to collections TD.
 */
public class ReferralToCollectionsDetail extends PersistableBusinessObjectBase {

    private Long docDetailId;
    private String documentNumber;

    private String agencyNumber;
    private Long proposalNumber;
    private String chart;
    private String accountNumber;
    private String invoiceNumber;
    private Date billingDate;
    private KualiDecimal invoiceTotal;
    private KualiDecimal invoiceBalance;
    private String finalDispositionCode;
    private Integer age;
    private String referralTypeCode;

    private ContractsGrantsInvoiceDocument invoiceDocument;

    /**
     * Gets the docDetailId attribute.
     * 
     * @return Returns the docDetailId field.
     */
    public Long getDocDetailId() {
        return docDetailId;
    }

    /**
     * Sets the docDetailId attribute.
     * 
     * @param docDetailId The docDetaillId to set.
     */
    public void setDocDetailId(Long docDetailId) {
        this.docDetailId = docDetailId;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber field.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber attribute to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber attribute.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agency number to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber attribute.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber to set.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart attribute.
     */
    public String getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     * 
     * @param chart The chart code to set.
     */
    public void setChart(String chart) {
        this.chart = chart;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber attribute.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber attribute to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     * 
     * @return Returns the invoiceNumber attribute.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     * 
     * @param invoiceNumber The invoiceNumber attribute to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the billingDate attribute.
     * 
     * @return Returns the billingDate attribute.
     */
    public Date getBillingDate() {
        return billingDate;
    }

    /**
     * Sets the billingDate attribute.
     * 
     * @param billingDate The billingDate attribute to set.
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * Gets the invoiceTotal attribute.
     * 
     * @return Returns the invoiceTotal attribute.
     */
    public KualiDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    /**
     * Sets the invoiceTotal attribute.
     * 
     * @param invoiceTotal The invoiceTotal attribute to set.
     */
    public void setInvoiceTotal(KualiDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    /**
     * Gets the invoiceBalance attribute.
     * 
     * @return Returns the invoiceBalance attribute.
     */
    public KualiDecimal getInvoiceBalance() {
        return invoiceBalance;
    }

    /**
     * Sets the invoiceBalance attribute.
     * 
     * @param invoiceBalance The invoiceBalance attribute to set.
     */
    public void setInvoiceBalance(KualiDecimal invoiceBalance) {
        this.invoiceBalance = invoiceBalance;
    }

    /**
     * Gets the finalDispositionCode attribute.
     * 
     * @return Returns the finalDispositionCode attribute.
     */
    public String getFinalDispositionCode() {
        return finalDispositionCode;
    }

    /**
     * Sets the finalDispositionCode attribute.
     * 
     * @param finalDispositionCode The finalDispositionCode attribute to set.
     */
    public void setFinalDispositionCode(String finalDispositionCode) {
        this.finalDispositionCode = finalDispositionCode;
    }

    /**
     * Gets the age attribute.
     * 
     * @return Returns the age attribute.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age attribute.
     * 
     * @param age The age attribute to set.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Gets the invoiceDocument object.
     * 
     * @return Returns the invoiceDocument object.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        if (ObjectUtils.isNotNull(invoiceNumber)) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            invoiceDocument = boService.findBySinglePrimaryKey(ContractsGrantsInvoiceDocument.class, invoiceNumber);
        }
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument object.
     * 
     * @param invoiceDocument The invoiceDocument object to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }

    /**
     * Gets the referralTypeCode attribute.
     * 
     * @return Returns the referralTypeCode attribute.
     */
    public String getReferralTypeCode() {
        return referralTypeCode;
    }

    /**
     * Sets the referralTypeCode attribute.
     * 
     * @param referralTypeCode The referralTypeCode attribute to set.
     */
    public void setReferralTypeCode(String referralTypeCode) {
        this.referralTypeCode = referralTypeCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("docDetailId", this.docDetailId);
        m.put("documentNumber", this.documentNumber);
        m.put("agencyNumber", this.agencyNumber);
        m.put("invoiceNumber", this.invoiceNumber);
        return m;
    }
}
