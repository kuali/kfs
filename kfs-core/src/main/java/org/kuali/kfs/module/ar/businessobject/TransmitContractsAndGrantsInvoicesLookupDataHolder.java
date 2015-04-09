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
