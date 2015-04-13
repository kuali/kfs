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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Fixture class for ContractsGrantsInvoiceDocument
 */
public enum ContractsGrantsInvoiceDocumentFixture {
    CG_INV_DOC1(new Long(11),  "2011-12-23"), CG_INV_DOC2(new Long(1234), "2011-12-23");

    private Long proposalNumber;
    private String billingDate;

    private ContractsGrantsInvoiceDocumentFixture(Long proposalNumber, String billingDate) {
        this.billingDate = billingDate;
        this.proposalNumber = proposalNumber;
    }

    private void setValues(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
        invoiceGeneralDetail.setProposalNumber(proposalNumber);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);
        contractsGrantsInvoiceDocument.setBillingDate(Date.valueOf(billingDate));
    }


    public ContractsGrantsInvoiceDocument createContractsGrantsInvoiceDocument(DocumentService documentService) {
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = null;
        try {
            contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument) documentService.getNewDocument("CINV");
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument)) {
            setValues(contractsGrantsInvoiceDocument);
        }

        contractsGrantsInvoiceDocument.getFinancialSystemDocumentHeader().setWorkflowCreateDate(new java.sql.Timestamp(new java.util.Date().getTime()));

        return contractsGrantsInvoiceDocument;
    }

}
