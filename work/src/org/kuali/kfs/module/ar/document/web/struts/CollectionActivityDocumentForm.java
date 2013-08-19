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
package org.kuali.kfs.module.ar.document.web.struts;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import java.util.ArrayList;

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
    protected ContractsGrantsInvoiceDocument selectedInvoiceApplication;
    protected transient ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#search(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */

    protected String lookupResultsSequenceNumber; // Indicates which result set we are using when refreshing/returning from a
    // multi-value lookup.
    protected String lookupResultsBOClassName; // Type of result returned by the multi-value lookup. ?to be persisted in the lookup
    // results service instead?
    protected String lookedUpCollectionName; // The name of the collection looked up (by a multiple value lookup)

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    
    /**
     * Default constructor for CollectionActivityDocumentForm.
     */
    public CollectionActivityDocumentForm() {
        super();
        selectedInvoiceApplication = null;
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
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
        return (ContractsGrantsInvoiceDocument) getCgInvoices().get(index);
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
        ContractsGrantsInvoiceDocument invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getOpenAmount();
    }

    /**
     * Gets the selectedInvoiceTotalAmount attribute.
     * 
     * @return Returns the selectedInvoiceTotalAmount.
     */
    public KualiDecimal getSelectedInvoiceTotalAmount() {
        ContractsGrantsInvoiceDocument invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getSourceTotal();
    }

    /**
     * Gets the selectedInvoicePaymentAmount attribute.
     * 
     * @return Returns the selectedInvoicePaymentAmount.
     */
    public KualiDecimal getSelectedInvoicePaymentAmount() {
        ContractsGrantsInvoiceDocument invoiceDoc = this.getSelectedInvoiceApplication();
        return contractsGrantsInvoiceDocumentService.retrievePaymentAmountByDocumentNumber(invoiceDoc.getDocumentNumber());
    }

    /**
     * Gets the selectedProposalNumber attribute.
     * 
     * @return Returns the selectedProposalNumber.
     */
    public Date getSelectedInvoicePaymentDate() {
        ContractsGrantsInvoiceDocument invoiceDoc = this.getSelectedInvoiceApplication();
        return contractsGrantsInvoiceDocumentService.retrievePaymentDateByDocumentNumber(invoiceDoc.getDocumentNumber());
    }

    /**
     * Gets the selectedInvoiceBalanceDue attribute.
     * 
     * @return Returns the selectedInvoiceBalanceDue.
     */
    public KualiDecimal getSelectedInvoiceBalanceDue() {
        ContractsGrantsInvoiceDocument invoiceApplication = getSelectedInvoiceApplication();
        return getSelectedInvoiceTotalAmount().subtract(getSelectedInvoicePaymentAmount());
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return ArPropertyConstants.COLLECTION_ACTIVTY_DOC_TYPE;
    }

    /**
     * This method gets the previous invoice document number
     * 
     * @return the previous invoice document number
     */
    public String getPreviousInvoiceDocumentNumber() {
        ContractsGrantsInvoiceDocument _previousInvoiceDocument = null;

        ContractsGrantsInvoiceDocument selectedCGInvoiceDocument = getSelectedInvoiceApplication();
        if (null == selectedCGInvoiceDocument || 2 > getCgInvoices().size()) {
            _previousInvoiceDocument = null;
        }
        else {
            Iterator<ContractsGrantsInvoiceDocument> iterator = getCgInvoices().iterator();
            ContractsGrantsInvoiceDocument previousInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedCGInvoiceDocument.getDocumentNumber();
            if (null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(previousInvoiceDocument.getDocumentNumber())) {
                _previousInvoiceDocument = null;
            }
            else {
                while (iterator.hasNext()) {
                    ContractsGrantsInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if (null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedCGInvoiceDocument.getDocumentNumber())) {
                        _previousInvoiceDocument = previousInvoiceDocument;
                    }
                    else {
                        previousInvoiceDocument = currentInvoiceDocument;
                    }
                }
            }
        }

        return null == _previousInvoiceDocument ? "" : _previousInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the next invoice document number
     * 
     * @return the next invoice document number
     */
    public String getNextInvoiceDocumentNumber() {
        ContractsGrantsInvoiceDocument _nextInvoiceDocument = null;

        ContractsGrantsInvoiceDocument selectedInvoiceDocument = getSelectedInvoiceApplication();
        if (null == selectedInvoiceDocument || 2 > getCgInvoices().size()) {
            _nextInvoiceDocument = null;
        }
        else {
            Iterator<ContractsGrantsInvoiceDocument> iterator = getCgInvoices().iterator();
            while (iterator.hasNext()) {
                ContractsGrantsInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if (currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if (iterator.hasNext()) {
                        _nextInvoiceDocument = iterator.next();
                    }
                    else {
                        _nextInvoiceDocument = null;
                    }
                }
            }
        }

        return null == _nextInvoiceDocument ? "" : _nextInvoiceDocument.getDocumentNumber();
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
            if (i.isEmpty()) {
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
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
     */
    @Override
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
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
}
