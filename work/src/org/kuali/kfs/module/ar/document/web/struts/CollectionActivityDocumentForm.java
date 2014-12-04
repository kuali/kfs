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

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Form class for Collection Activity Document.
 */
public class CollectionActivityDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    protected String selectedInvoiceDocumentNumber;
    protected String selectedProposalNumber;
    protected String selectedAgencyNumber;
    protected String selectedAgencyName;
    protected String selectedCustomerNumber;
    protected String selectedCustomerName;
    protected ContractsGrantsInvoiceDocument selectedInvoiceApplication = null;
    protected transient static volatile CollectionActivityDocumentService collectionActivityDocumentService;

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
    public List<ContractsGrantsInvoiceDocument> getCgInvoices() {
        CollectionActivityDocument colActDoc = getCollectionActivityDocument();
        if (ObjectUtils.isNotNull(colActDoc) && ObjectUtils.isNotNull(colActDoc.getInvoices())) {
            return colActDoc.getInvoices();
        }
        else {
            return new ArrayList<ContractsGrantsInvoiceDocument>();
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
     * This method retrieves a specific contracts grants invoice from the list, by array index
     *
     * @param index the index of the contracts grants invoice to retrieve
     * @return a ContractsGrantsInvoiceDocument
     */
    public ContractsGrantsInvoiceDocument getInvoiceApplication(int index) {
        return getCgInvoices().get(index);
    }

    /**
     * This method gets the collection activity document
     *
     * @return the collection activity document
     */
    public CollectionActivityDocument getCollectionActivityDocument() {
        return (CollectionActivityDocument) getDocument();
    }

    /**
     * Gets the selectedInvoiceBalance attribute.
     *
     * @return Returns the selectedInvoiceBalance.
     */
    public KualiDecimal getSelectedInvoiceBalance() {
        return getSelectedInvoiceApplication().getOpenAmount();
    }

    /**
     * Gets the selectedInvoiceTotalAmount attribute.
     *
     * @return Returns the selectedInvoiceTotalAmount.
     */
    public KualiDecimal getSelectedInvoiceTotalAmount() {
        return getSelectedInvoiceApplication().getSourceTotal();
    }

    /**
     * Gets the selectedInvoicePaymentAmount attribute.
     *
     * @return Returns the selectedInvoicePaymentAmount.
     */
    public KualiDecimal getSelectedInvoicePaymentAmount() {
        return getCollectionActivityDocumentService().retrievePaymentAmountByDocumentNumber(getSelectedInvoiceApplication().getDocumentNumber());
    }

    /**
     * Gets the selectedProposalNumber attribute.
     *
     * @return Returns the selectedProposalNumber.
     */
    public Date getSelectedInvoicePaymentDate() {
        return getCollectionActivityDocumentService().retrievePaymentDateByDocumentNumber(getSelectedInvoiceApplication().getDocumentNumber());
    }

    /**
     * Gets the selectedInvoiceBalanceDue attribute.
     *
     * @return Returns the selectedInvoiceBalanceDue.
     */
    public KualiDecimal getSelectedInvoiceBalanceDue() {
        return getSelectedInvoiceTotalAmount().subtract(getSelectedInvoicePaymentAmount());
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArConstants.ArDocumentTypeCodes.COLLECTION_ACTIVTY;
    }

    /**
     * This method gets the previous invoice document number
     *
     * @return the previous invoice document number
     */
    public String getPreviousInvoiceDocumentNumber() {
        ContractsGrantsInvoiceDocument previousInvoiceDocument = null;

        ContractsGrantsInvoiceDocument selectedCGInvoiceDocument = getSelectedInvoiceApplication();
        if (ObjectUtils.isNull(selectedCGInvoiceDocument) || getCgInvoices().size() < 2) {
            previousInvoiceDocument = null;
        }
        else {
            Iterator<ContractsGrantsInvoiceDocument> iterator = getCgInvoices().iterator();
            ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedCGInvoiceDocument.getDocumentNumber();
            if (null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(contractsGrantsInvoiceDocument.getDocumentNumber())) {
                previousInvoiceDocument = null;
            }
            else {
                while (iterator.hasNext()) {
                    ContractsGrantsInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if (null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedCGInvoiceDocument.getDocumentNumber())) {
                        previousInvoiceDocument = contractsGrantsInvoiceDocument;
                    }
                    else {
                        contractsGrantsInvoiceDocument = currentInvoiceDocument;
                    }
                }
            }
        }

        return null == previousInvoiceDocument ? "" : previousInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the next invoice document number
     *
     * @return the next invoice document number
     */
    public String getNextInvoiceDocumentNumber() {
        ContractsGrantsInvoiceDocument nextInvoiceDocument = null;

        ContractsGrantsInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceApplication();
        if (ObjectUtils.isNull(selectedInvoiceDocument) || getCgInvoices().size() < 2) {
            nextInvoiceDocument = null;
        }
        else {
            Iterator<ContractsGrantsInvoiceDocument> iterator = getCgInvoices().iterator();
            while (iterator.hasNext()) {
                ContractsGrantsInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if (currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if (iterator.hasNext()) {
                        nextInvoiceDocument = iterator.next();
                    }
                    else {
                        nextInvoiceDocument = null;
                    }
                }
            }
        }

        return null == nextInvoiceDocument ? "" : nextInvoiceDocument.getDocumentNumber();
    }

    /**
     * @return Returns the selected invoice application.
     */
    public ContractsGrantsInvoiceDocument getSelectedInvoiceApplication() {
        String docNumber = getSelectedInvoiceDocumentNumber();
        if (ObjectUtils.isNotNull(docNumber)) {
            return getInvoiceApplicationsByDocumentNumber().get(docNumber);
        }
        else {
            List<ContractsGrantsInvoiceDocument> i = getCgInvoices();
            if (CollectionUtils.isEmpty(i)) {
                return null;
            }
            else {
                return getCgInvoices().get(0);
            }
        }
    }

    public Map<String, ContractsGrantsInvoiceDocument> getInvoiceApplicationsByDocumentNumber() {
        Map<String, ContractsGrantsInvoiceDocument> m = new HashMap<String, ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument i : getCgInvoices()) {
            m.put(i.getDocumentNumber(), i);
        }
        return m;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        CollectionActivityDocument colActDoc = getCollectionActivityDocument();
        // Set the proposal number for the document to be saved.
        if (ObjectUtils.isNotNull(colActDoc)) {
            if (ObjectUtils.isNotNull(this.getSelectedProposalNumber())) {
                colActDoc.setProposalNumber(new Long(this.getSelectedProposalNumber()));
            }
            SpringContext.getBean(CollectionActivityDocumentService.class).loadAwardInformationForCollectionActivityDocument(colActDoc);
        }
    }

    public CollectionActivityDocumentService getCollectionActivityDocumentService() {
        if (collectionActivityDocumentService == null) {
            collectionActivityDocumentService = SpringContext.getBean(CollectionActivityDocumentService.class);
        }
        return collectionActivityDocumentService;
    }
}
