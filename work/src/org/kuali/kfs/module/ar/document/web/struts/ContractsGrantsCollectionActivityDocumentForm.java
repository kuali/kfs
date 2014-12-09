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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsCollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Form class for Collection Activity Document.
 */
public class ContractsGrantsCollectionActivityDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    protected String selectedInvoiceDocumentNumber;
    protected String selectedProposalNumber;
    protected String selectedAgencyNumber;
    protected String selectedAgencyName;
    protected String selectedCustomerNumber;
    protected String selectedCustomerName;
    protected ContractsGrantsInvoiceDocument selectedInvoiceApplication = null;
    protected transient static volatile ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;

    // Indicates which result set we are using when refreshing/returning from a multi-value lookup.
    protected String lookupResultsSequenceNumber;

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * Gets the list of cgInvoices attribute.
     *
     * @return Returns the list of cgInvoices.
     */
    public List<ContractsGrantsCollectionActivityInvoiceDetail> getInvoiceDetails() {
        ContractsGrantsCollectionActivityDocument colActDoc = getCollectionActivityDocument();
        if (ObjectUtils.isNotNull(colActDoc) && ObjectUtils.isNotNull(colActDoc.getInvoiceDetails())) {
            return colActDoc.getInvoiceDetails();
        }
        else {
            return new ArrayList<ContractsGrantsCollectionActivityInvoiceDetail>();
        }
    }

    /**
     * Gets the selectedInvoiceDocumentNumber attribute.
     *
     * @return Returns the selectedInvoiceDocumentNumber.
     */
    public String getSelectedInvoiceDocumentNumber() {
        return selectedInvoiceDocumentNumber;
    }

    /**
     * Sets the selectedInvoiceDocumentNumber attribute.
     *
     * @param selectedInvoiceDocumentNumber The selectedInvoiceDocumentNumber to set.
     */
    public void setSelectedInvoiceDocumentNumber(String selectedInvoiceDocumentNumber) {
        this.selectedInvoiceDocumentNumber = selectedInvoiceDocumentNumber;
    }

    /**
     * Gets the selectedProposalNumber attribute.
     *
     * @return Returns the selectedProposalNumber.
     */
    public String getSelectedProposalNumber() {
        return selectedProposalNumber;
    }

    /**
     * Sets the selectedProposalNumber attribute value.
     *
     * @param selectedProposalNumber The selectedProposalNumber to set.
     */
    public void setSelectedProposalNumber(String selectedProposalNumber) {
        this.selectedProposalNumber = selectedProposalNumber;
    }

    /**
     * Gets the selectedAgencyNumber attribute.
     *
     * @return Returns the selected agency number.
     */
    public String getSelectedAgencyNumber() {
        return selectedAgencyNumber;
    }

    /**
     * Sets the selectedAgencyNumber attribute.
     *
     * @param selectedAgencyNumber The selected agency number to set.
     */
    public void setSelectedAgencyNumber(String selectedAgencyNumber) {
        this.selectedAgencyNumber = selectedAgencyNumber;
    }

    /**
     * Gets the selectedAgencyName attribute.
     *
     * @return Returns the seleted agency name.
     */
    public String getSelectedAgencyName() {
        return selectedAgencyName;
    }

    /**
     * Sets the selectedAgencyName attribute.
     *
     * @param selectedAgencyName The selected agency name to set.
     */
    public void setSelectedAgencyName(String selectedAgencyName) {
        this.selectedAgencyName = selectedAgencyName;
    }

    /**
     * Gets the selectedCustomerNumber attribute.
     *
     * @return Returns the selected customer number.
     */
    public String getSelectedCustomerNumber() {
        return selectedCustomerNumber;
    }

    /**
     * Sets the selectedCustomerNumber attribute.
     *
     * @param selectedCustomerNumber The selected customer number to set.
     */
    public void setSelectedCustomerNumber(String selectedCustomerNumber) {
        this.selectedCustomerNumber = selectedCustomerNumber;
    }

    /**
     * Gets the selectedCustomerName attribute.
     *
     * @return Returns the selected customer name.
     */
    public String getSelectedCustomerName() {
        return selectedCustomerName;
    }

    /**
     * Sets the selectedCustomerName attribute.
     *
     * @param selectedCustomerName The selected customer name to set.
     */
    public void setSelectedCustomerName(String selectedCustomerName) {
        this.selectedCustomerName = selectedCustomerName;
    }

    /**
     * This method gets the collection activity document
     *
     * @return the collection activity document
     */
    public ContractsGrantsCollectionActivityDocument getCollectionActivityDocument() {
        return (ContractsGrantsCollectionActivityDocument) getDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_COLLECTION_ACTIVTY;
    }

    public ContractsGrantsCollectionActivityDocumentService getCollectionActivityDocumentService() {
        if (contractsGrantsCollectionActivityDocumentService == null) {
            contractsGrantsCollectionActivityDocumentService = SpringContext.getBean(ContractsGrantsCollectionActivityDocumentService.class);
        }
        return contractsGrantsCollectionActivityDocumentService;
    }
}
