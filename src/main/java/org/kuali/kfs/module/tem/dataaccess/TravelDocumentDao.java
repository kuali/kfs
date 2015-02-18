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
package org.kuali.kfs.module.tem.dataaccess;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;

/**
 * This is the data access interface for Document objects.
 *
 */
public interface TravelDocumentDao {

    /**
     *
     * @param travelDocumentNumber to refer to for travel document results
     * @return
     */
    List<TravelDocument> findDocuments(final Class<?> travelDocumentClass, final String travelDocumentNumber);

    /**
     *
     * @param travelDocumentNumber to refer to for travel document results
     * @return
     */
	List<String> findDocumentNumbers(final Class<?> travelDocumentClass, final String travelDocumentNumber);

	/**
	 * PerDiem lookup base on search values
	 *
	 * PER_DIEM_LOOKUP_DATE can also be passed in for a custom date duration search
	 *
	 * @param primaryDestinationId the id of the primary destination to find dates for
	 * @param perDiemDate the date of the per diem to find a PerDiem record for
	 * @param effectiveDate the date we should use against the effective dates
	 * @return
	 *
	 * deprecated because we're looking for a better way to do this
	 */
	@Deprecated
	public List<PerDiem> findEffectivePerDiems(int primaryDestinationId, java.sql.Date effectiveDate);

    /**
     * get all outstanding travel advances by the given invoice document numbers. The advances must have not been used to generate
     * taxable ramification
     *
     * @param arInvoiceDocNumbers the given AR invoice document numbers
     * @return a list of outstanding travel advances
     */

    List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumber);

    /**
     * find the latest taxable ramification notification date
     *
     * @return the latest taxable ramification notification date
     */
    Object[] findLatestTaxableRamificationNotificationDate();

    /**
     * Retrieves all Travel Reimbursement documents with the given financial system document header status
     * @param statusCode the financial system document header status of the documents to retrieve
     * @param immediatesOnly true if only those documents marked for immediate payment are to be retrieved, false if all documents at the status are to be retrieved
     * @return a Collection of qualifying Travel Reimbursement documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getReimbursementDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly);

    /**
     * Retrieves all Travel Relocation documents with the given financial system document header status
     * @param statusCode the financial system document header status of the documents to retrieve
     * @param immediatesOnly true if only those documents marked for immediate payment are to be retrieved, false if all documents at the status are to be retrieved
     * @return a Collection of qualifying Travel Relocation documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getRelocationDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly);

    /**
     * Retrieves all Entertainment documents with the given financial system document header status
     * @param statusCode the financial system document header status of the documents to retrieve
     * @param immediatesOnly true if only those documents marked for immediate payment are to be retrieved, false if all documents at the status are to be retrieved
     * @return a Collection of qualifying Entertainment documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getEntertainmentDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly);

    /**
     * Retrieves all TravelAuthorization and TravelAuthorizationAmendment documents with the given financial system document header status
     * @param statusCode the financial system document header status of the documents to retrieve
     * @param immediatesOnly true if only those documents marked for immediate payment are to be retrieved, false if all documents at the status are to be retrieved
     * @return a Collection of qualifying authorization documents
     */
    public abstract Collection<? extends TravelAuthorizationDocument> getAuthorizationsAndAmendmentsByHeaderStatus(String statusCode, boolean immediatesOnly);

    /**
     * Retrieves all TravelReimbursement documents which have corporate card expenses and have not yet been extracted
     * @return a Collection of qualifying reimbursement documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getReimbursementDocumentsNeedingCorporateCardExtraction();

    /**
     * Retrieves all Entertainment documents which have corporate card expenses and have not yet been extracted
     * @return a Collection of qualifying reimbursement documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getEntertainmentDocumentsNeedingCorporateCardExtraction();

    /**
     * Retrieves all Moving & Relocation documents which have corporate card expenses and have not yet been extracted
     * @return a Collection of qualifying reimbursement documents
     */
    public abstract Collection<? extends TEMReimbursementDocument> getRelocationDocumentsNeedingCorporateCardExtraction();

    /**
     * Retrieves duplicate trips for traveler with given trip begin and end date
     *
     * @param temProfileId
     * @param tripBegin
     * @param tripEnd
     * @return
     */
    public Collection<? extends TEMReimbursementDocument> findMatchingTrips (Integer temProfileId ,Timestamp tripBegin, Timestamp tripEnd) ;
}
