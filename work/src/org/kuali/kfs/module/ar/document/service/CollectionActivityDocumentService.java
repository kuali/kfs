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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Service class for Collection Activity Document.
 */
public interface CollectionActivityDocumentService {

    /**
     * Adds the new event for invoice.
     * 
     * @param description The document description.
     * @param colActDoc The Collection Activity Document object.
     * @param newEvent The event object to be added.
     * @throws WorkflowException
     */
    public void addNewEvent(String description, CollectionActivityDocument colActDoc, Event newEvent) throws WorkflowException;

    /**
     * Edits the existing event.
     * 
     * @param description The document description.
     * @param colActDoc The Collection Activity Document object.
     * @param event The event object to be edited.
     * @throws WorkflowException
     */
    public void editEvent(String description, CollectionActivityDocument colActDoc, Event event) throws WorkflowException;

    /**
     * Retrieves the award information from proposal number of given CollectionActivityDocument object.
     * 
     * @param colActDoc The Collection Activity Document object with proposal number set.
     */
    public void loadAwardInformationForCollectionActivityDocument(CollectionActivityDocument colActDoc);

    /**
     * Retrieves the events based on the criteria.
     * 
     * @param criteria The criteria to filter out events.
     * @return Returns the collection of Event which match the criteria.
     */
    public Collection<Event> retrieveEventsByCriteria(Criteria criteria);

    /**
     * Retrieves the award by given proposal number.
     * 
     * @param proposalNumber The proposal number of award.
     * @return Returns the award object.
     */
    public ContractsAndGrantsCGBAward retrieveAwardByProposalNumber(Long proposalNumber);

    /**
     * Validates the invoices for events with saved state for that particular document.
     * 
     * @param invoiceNumber The invoiceNumber of event.
     * @param documentNumber The collection activity document number for event.
     * @return Returns true if no event with saved state is available for this invoice. Returns false if any event with saved state
     *         and different document number found.
     */
    public boolean validateInvoiceForSavedEvents(String invoiceNumber, String documentNumber);
}
