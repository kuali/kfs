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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class unfinalizes the invoices that have previously been finalized.
 */
public class FinalBilledIndicatorDocument extends TransactionalDocumentBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalBilledIndicatorDocument.class);

    private List<FinalBilledIndicatorEntry> invoiceEntries = new ArrayList<FinalBilledIndicatorEntry>();

    /**
     * Gets the invoiceEntries attribute.
     *
     * @return Returns the invoiceEntries.
     */
    public List<FinalBilledIndicatorEntry> getInvoiceEntries() {
        return invoiceEntries;
    }

    /**
     * Sets the invoiceEntries attribute value.
     *
     * @param invoiceEntries The invoiceEntries to set.
     */
    public void setInvoiceEntries(List<FinalBilledIndicatorEntry> invoiceEntries) {
        this.invoiceEntries = invoiceEntries;
    }

    /**
     * @throws WorkflowException
     */
    public void updateContractsGrantsInvoiceDocument() throws WorkflowException {
        for (FinalBilledIndicatorEntry entry : this.getInvoiceEntries()) {
            ContractsGrantsInvoiceDocument invoiceDocument;
            invoiceDocument = (ContractsGrantsInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(entry.getInvoiceDocumentNumber());
            if ( ObjectUtils.isNotNull(invoiceDocument) ) {
                invoiceDocument.getInvoiceGeneralDetail().setFinalBillIndicator(false);
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.updateUnfinalizationToAwardAccount(invoiceDocument.getAccountDetails(),invoiceDocument.getInvoiceGeneralDetail().getProposalNumber());
                invoiceDocument.refresh();
                SpringContext.getBean(DocumentService.class).updateDocument(invoiceDocument);
            }
        }
    }



    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        String newRouteStatus = statusChangeEvent.getNewRouteStatus();
        if (newRouteStatus.equalsIgnoreCase(KFSConstants.DocumentStatusCodes.PROCESSED) || newRouteStatus.equalsIgnoreCase(KFSConstants.DocumentStatusCodes.FINAL)) {
            try {
                updateContractsGrantsInvoiceDocument();
            }
            catch (WorkflowException ex) {
                LOG.error("problem during FinalBilledIndicatorDocumentAction.doProcessingAfterPost()", ex);
                throw new RuntimeException("Could not update Contracts & Grants Invoice Document for Final Billed Indicator Document",ex);
            }
        }
    }

    /**
     * @param invoiceEntry
     */
    public void addInvoiceEntry(FinalBilledIndicatorEntry invoiceEntry) {
        invoiceEntry.setDocumentId(getDocumentNumber());
        invoiceEntries.add(invoiceEntry);
    }

    /**
     * @param deleteIndex
     */
    public void removeInvoiceEntry(int deleteIndex) {
        invoiceEntries.remove(deleteIndex);
    }
}
