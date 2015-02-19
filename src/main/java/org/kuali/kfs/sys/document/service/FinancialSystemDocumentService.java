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
package org.kuali.kfs.sys.document.service;

import java.util.Collection;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;

/**
 * This class is the financial system specific document service interface
 */
public interface FinancialSystemDocumentService {

    /**
     * Looks up all Documents of the given class that are in the state of the given KFS document status code
     * @param clazz the class of the document to look up
     * @param statusCode the KFS status code to look up
     * @return a Collection of matching documents
     * @throws WorkflowException if the workflow document cannot be accessed for any reason
     */
    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException;

    /**
     * Looks up all Documents of the given class that have the given workflow DocumentStatus
     * @param clazz the class of the document to look up
     * @param docStatus the KEW status code to look up
     * @return a Collection of matching documents
     * @throws WorkflowException if the workflow document cannot be accessed for any reason
     */
    public <T extends Document> Collection<T> findByWorkflowStatusCode(Class<T> clazz, DocumentStatus docStatus) throws WorkflowException;

    /**
     * Looks up all Documents of the given class that have the given application document status
     * @param clazz the class of the document to look up
     * @param applicationDocumentStatus the application document status to look up
     * @return a Collection of matching documents
     * @throws WorkflowException if the workflow document cannot be accessed for any reason
     */
    public <T extends Document> Collection<T> findByApplicationDocumentStatus(Class<T> clazz, String applicationDocumentStatus) throws WorkflowException;
    /**
     * This method retrieves the financial system document headers of all the documents having application document status passed in.
     *
     * @param applicationDocumentStatus
     * @return document headers list
     */
    public Collection<FinancialSystemDocumentHeader> findByApplicationDocumentStatus(String applicationDocumentStatus);
    public void prepareToCopy(FinancialSystemDocumentHeader oldDocumentHeader, FinancialSystemTransactionalDocument document);
    public Collection<FinancialSystemDocumentHeader> findByWorkflowStatusCode(DocumentStatus docStatus);
    /**
     * This method takes a document number in and returns the relevant document header
     *
     * @param documentNumber
     * @return document header
     */
    public FinancialSystemDocumentHeader findByDocumentNumber(String documentNumber);

    /**
     * Convenience method which turns the DocumentStatusCategory.PENDING document statuses into a Set of the status codes as Strings
     * As of the time of this commenting, the pending statuses are Initiated (I), Saved (S), Enroute (R), and Exception (E)
     * @return a Set of Statuses "in progress"/"pending" documents might have
     */
    public Set<String> getPendingDocumentStatuses();

    /**
     * Convenience method which turns the DocumentStatusCategory.SUCCESSFUL document statuses into a Set of status codes as Strings
     * As of the time of this commenting, the successful statuses are Processed (P) and Final (F)
     * @return a Set of Statuses which are considered "successful"
     */
    public Set<String> getSuccessfulDocumentStatuses();

    /**
     * Convenience method which turns the DocumentStatusCategory.UNSUCCESSFUL document statuses into a Set of status codes as Strings
     * As of the time of this commenting, the unsuccessful statuses are Canceled (X), Disapproved (D), and Recalled (L)
     * @return a Set of Statuses which are considered "unsuccessful"
     */
    public Set<String> getUnsuccessfulDocumentStatuses();

    /**
     * @param id
     * @return documentHeader of the document which corrects the document with the given documentId
     */
    public DocumentHeader getCorrectingDocumentHeader(String documentId);

    /**
     * @deprecated this method was created to support document searches for batch document processing.  Instead of using document searches,
     *             the FinancialSystemDocumentHeader should now have properties which allow the selection of documents without a document search.
     *             This method will be removed in KFS 6
     */
    @Deprecated
    public int getFetchMoreIterationLimit();
    /**
     * @deprecated this method was created to support document searches for batch document processing.  Instead of using document searches,
     *             the FinancialSystemDocumentHeader should now have properties which allow the selection of documents without a document search.
     *             This method will be removed in KFS 6
     */
    @Deprecated
    public int getMaxResultCap(DocumentSearchCriteria criteria);

}
