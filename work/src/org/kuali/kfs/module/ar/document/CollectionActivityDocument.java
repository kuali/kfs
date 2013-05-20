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
package org.kuali.kfs.module.ar.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Collection Activity Document class. This transactional document is used to store events related to customers.
 */
public class CollectionActivityDocument extends FinancialSystemTransactionalDocumentBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityDocument.class);

    private Long proposalNumber;
    private String agencyNumber;
    private String agencyName;
    private String customerNumber;
    private String customerName;
    protected String selectedInvoiceDocumentNumber;
    protected transient ContractsAndGrantsCGBAward award;
    protected transient Customer customer;
    protected Event newEvent;

    private List<ContractsGrantsInvoiceDocument> invoices;
    private List<Event> events;

    /**
     * Default constructor for CollectionActivityDocument.
     */
    public CollectionActivityDocument() {
        super();
        invoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        events = new ArrayList<Event>();
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agency number.
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
     * Gets the agencyName attribute.
     * 
     * @return Returns the agency name.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute.
     * 
     * @param agencyName The agency name to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customer number.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customer number to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerName attribute.
     * 
     * @return Returns the customer name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute.
     * 
     * @param customerName The customerName attribute to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customer attribute.
     * 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute.
     * 
     * @param customer The customer to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward() {
        return award;
    }

    /**
     * Sets the award attribute.
     * 
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    /**
     * @return Returns true if document is in final state.
     */
    public boolean isFinal() {
        return isApproved();
    }

    /**
     * 
     * @return Returns true if document is in Approved.
     */
    public boolean isApproved() {
        return getDocumentHeader().getWorkflowDocument().isApproved();
    }

    /**
     * Gets the invoices attribute.
     * 
     * @return Returns the invoices.
     */
    public List<ContractsGrantsInvoiceDocument> getInvoices() {
        return invoices;
    }

    /**
     * Sets the invoices attribute.
     * 
     * @param invoices The invoices to set.
     */
    public void setInvoices(List<ContractsGrantsInvoiceDocument> invoices) {
        this.invoices = invoices;
    }

    /**
     * Gets the events attribute.
     * 
     * @return Returns the events.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the events attribute.
     * 
     * @param events The events to set.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Sets the events from invoices list of this class.
     * 
     * @param addOnlyFinalEvents True if final events are to be added from invoices. False if all the events are to be added
     *        irrespective of route state of event.
     */
    public void setEventsFromCGInvoices() {
        if (ObjectUtils.isNotNull(invoices) && !invoices.isEmpty()) {
            events = new ArrayList<Event>();
            for (ContractsGrantsInvoiceDocument invoice : invoices) {
                List<Event> invoiceEvents = invoice.getEvents();
                if (ObjectUtils.isNotNull(invoiceEvents) && !invoiceEvents.isEmpty() && invoice.isShowEvents()) {
                    events.addAll(invoiceEvents);
                }
            }
        }
    }

    /**
     * Gets the newEvent attribute.
     * 
     * @return Returns the newEvent.
     */
    public Event getNewEvent() {
        return newEvent;
    }

    /**
     * Sets the newEvent attribute.
     * 
     * @param newEvent The newEvent to set.
     */
    public void setNewEvent(Event newEvent) {
        this.newEvent = newEvent;
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
     * @return Returns the selected invoice application.
     */
    public ContractsGrantsInvoiceDocument getSelectedInvoiceApplication() {
        String docNumber = getSelectedInvoiceDocumentNumber();
        if (ObjectUtils.isNotNull(docNumber)) {
            return getInvoiceApplicationsByDocumentNumber().get(docNumber);
        }
        else {
            List<ContractsGrantsInvoiceDocument> i = invoices;
            if (i.isEmpty()) {
                return null;
            }
            else {
                return invoices.get(0);
            }
        }
    }

    /**
     * Gets the map of invoices with documentNumber as key.
     * 
     * @return Returns the map of invoices with documentNumber as key.
     */
    public Map<String, ContractsGrantsInvoiceDocument> getInvoiceApplicationsByDocumentNumber() {
        Map<String, ContractsGrantsInvoiceDocument> m = new HashMap<String, ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument i : invoices) {
            m.put(i.getDocumentNumber(), i);
        }
        return m;
    }

    /**
     * Gets the events of selected invoice.
     * 
     * @return Returns the events.
     */
    public List<Event> getSelectedInvoiceEvents() {
        List<Event> selectedEvents = new ArrayList<Event>();
        for (Event event : events) {
            if (ObjectUtils.isNotNull(events) && !events.isEmpty()) {
                if (event.getInvoiceNumber().equals(getSelectedInvoiceDocumentNumber())) {
                    selectedEvents.add(event);
                }
            }
        }
        return selectedEvents;
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isFinal()) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            this.setInvoiceListByProposalNumber(this.getProposalNumber());
            this.setEventsFromCGInvoices();
            if (ObjectUtils.isNotNull(events) && !events.isEmpty()) {
                for (Event event : events) {
                    // If the document is final, do the required changes.
                    if (ObjectUtils.isNull(event.getEventCode())) {
                        int lastEventCode = this.getFinalEventsCount(event.getInvoiceDocument().getEvents()) + 1;
                        String eventCode = event.getInvoiceNumber() + "-" + String.format("%03d", lastEventCode);
                        event.setEventCode(eventCode);
                    }
                    event.setEventRouteStatus(KewApiConstants.ROUTE_HEADER_FINAL_CD);
                    boService.save(event);
                }
            }
        }
    }

    /**
     * Gets the number of final events in list.
     * 
     * @param events The list of events.
     * @return Returns the number of final events.
     */
    private int getFinalEventsCount(List<Event> events) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(events)) {
            for (Event event : events) {
                if (ObjectUtils.isNull(event.getEventRouteStatus()) || event.getEventRouteStatus().equals(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Sets the invoices list for this class from given proposal number.
     * 
     * @param proposalNumber The proposal number for which invoices list to be fetched.
     */
    public void setInvoiceListByProposalNumber(Long proposalNumber) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        // To retrieve the batch file directory name as "reports/ar"
        ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);

        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

        String errOutputFile = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.EVT_CREATION_CLN_ACT_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(proposalNumber, errOutputFile);

        if (!CollectionUtils.isEmpty(cgInvoices)) {
            cgInvoices = this.validateInvoices(cgInvoices);
            this.setInvoices(new ArrayList<ContractsGrantsInvoiceDocument>(cgInvoices));
        }
        else {
            LOG.error("There were no invoices retreived. Please refer to the log file " + ArConstants.BatchFileSystem.EVT_CREATION_CLN_ACT_ERROR_OUTPUT_FILE + ArConstants.BatchFileSystem.EXTENSION + " for more details.");
            this.setInvoices(new ArrayList<ContractsGrantsInvoiceDocument>());
        }
    }

    /**
     * Validates the invoices based on the events which are under process.
     * 
     * @param cgInvoices List of invoices to validate.
     * @return Returns list of invoices with value true or false based on state of events in invoices.
     */
    private Collection<ContractsGrantsInvoiceDocument> validateInvoices(Collection<ContractsGrantsInvoiceDocument> cgInvoices) {
        CollectionActivityDocumentService collectionActivityDocumentService = SpringContext.getBean(CollectionActivityDocumentService.class);
        Iterator<ContractsGrantsInvoiceDocument> invoiceItr = cgInvoices.iterator();
        for (ContractsGrantsInvoiceDocument invoice : cgInvoices) {
            if (!collectionActivityDocumentService.validateInvoiceForSavedEvents(invoice.getDocumentNumber(), this.getDocumentNumber())) {
                invoice.setShowEvents(false);
            }
            else {
                invoice.setShowEvents(true);
            }
        }
        return cgInvoices;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }
}
