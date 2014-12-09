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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Service class for Collection Activity Document.
 */
public interface ContractsGrantsCollectionActivityDocumentService {

    /**
     * Creates and saves new collection events for the Collection Activity Document.
     *
     * @param colActDoc The Collection Activity Document object.
     */
    public void createAndSaveCollectionEvents(ContractsGrantsCollectionActivityDocument colActDoc);

    /**
     * Adds the new collection event for invoice.
     *
     * @param description The document description.
     * @param colActDoc The Collection Activity Document object.
     * @param newCollectionEvent The collection event object to be added.
     * @throws WorkflowException
     */
    public void addNewCollectionEvent(String description, ContractsGrantsCollectionActivityDocument colActDoc, CollectionEvent newCollectionEvent) throws WorkflowException;

    /**
     * Edits the existing collection event.
     *
     * @param description The document description.
     * @param colActDoc The Collection Activity Document object.
     * @param event The event object to be edited.
     * @throws WorkflowException
     */
    public void editCollectionEvent(String description, ContractsGrantsCollectionActivityDocument colActDoc, CollectionEvent event) throws WorkflowException;

    /**
     * Retrieves the award information from proposal number of given ContractsGrantsCollectionActivityDocument object.
     *
     * @param colActDoc The Collection Activity Document object with proposal number set.
     */
    public void loadAwardInformationForCollectionActivityDocument(ContractsGrantsCollectionActivityDocument colActDoc);

    /**
     * Retrieves the collection events based on the field values passed in. Results are furthered filtered
     * by document number to exclude.
     *
     * @param fieldValues The fieldValues to filter out collection events.
     * @param documentNumberToExclude Document number that will be filtered out of the results.
     * @return Returns the collection of CollectionEvent which match the criteria.
     */
    public Collection<CollectionEvent> retrieveCollectionEvents(Map fieldValues, String documentNumberToExclude);

    /**
     * Retrieves the award by given proposal number.
     *
     * @param proposalNumber The proposal number of award.
     * @return Returns the award object.
     */
    public ContractsAndGrantsBillingAward retrieveAwardByProposalNumber(Long proposalNumber);

    /**
     * To retrieve the first payment date by given document number.
     *
     * @param documentNumber The invoice number of the document.
     * @return Returns the first payment date.
     */
    public java.sql.Date retrievePaymentDateByDocumentNumber(String documentNumber);

    /**
     * To retrieve the payment amount by given document number.
     *
     * @param documentNumber The invoice number of the document.
     * @return Returns the total payment amount.
     */
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber);
}
