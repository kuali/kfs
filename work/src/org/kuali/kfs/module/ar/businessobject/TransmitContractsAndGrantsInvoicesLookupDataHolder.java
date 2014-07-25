/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.Date;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Lookup Data Holder class for Transmit Contracts & Grants Invoices.
 */
public class TransmitContractsAndGrantsInvoicesLookupDataHolder extends TransientBusinessObjectBase {

    private String billByChartOfAccountCode;
    private String billedByOrganizationCode;
    private String invoiceInitiatorPrincipalName;
    private Date invoicePrintDate;
    private String invoiceTransmissionMethodCode;
    private Long proposalNumber;
    private String invoiceAmount;
    private String documentNumber;

    private Person documentInitiatorUser;
    private ContractsAndGrantsBillingAward award;
    private Chart chart;
    private Organization organization;

    public String getBillByChartOfAccountCode() {
        return billByChartOfAccountCode;
    }

    public void setBillByChartOfAccountCode(String billByChartOfAccountCode) {
        this.billByChartOfAccountCode = billByChartOfAccountCode;
    }

    public String getBilledByOrganizationCode() {
        return billedByOrganizationCode;
    }

    public void setBilledByOrganizationCode(String billedByOrganizationCode) {
        this.billedByOrganizationCode = billedByOrganizationCode;
    }

    public String getInvoiceInitiatorPrincipalName() {
        return invoiceInitiatorPrincipalName;
    }

    public void setInvoiceInitiatorPrincipalName(String invoiceInitiatorPrincipalName) {
        this.invoiceInitiatorPrincipalName = invoiceInitiatorPrincipalName;
    }

    public Date getInvoicePrintDate() {
        return invoicePrintDate;
    }

    public void setInvoicePrintDate(Date invoicePrintDate) {
        this.invoicePrintDate = invoicePrintDate;
    }

    public String getInvoiceTransmissionMethodCode() {
        return invoiceTransmissionMethodCode;
    }

    public void setInvoiceTransmissionMethodCode(String invoiceTransmissionMethodCode) {
        this.invoiceTransmissionMethodCode = invoiceTransmissionMethodCode;
    }

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Person getDocumentInitiatorUser() {
        return documentInitiatorUser;
    }

    public void setDocumentInitiatorUser(Person documentInitiatorUser) {
        this.documentInitiatorUser = documentInitiatorUser;
    }

    public ContractsAndGrantsBillingAward getAward() {
        return award;
    }

    public void setAward(ContractsAndGrantsBillingAward award) {
        this.award = award;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

}
