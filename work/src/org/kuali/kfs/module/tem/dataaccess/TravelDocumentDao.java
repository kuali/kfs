/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;

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
	List<String> findDocumentNumbers(final Class<?> travelDocumentClass, final String travelDocumentNumber);

	/**
	 * PerDiem lookup base on search values
	 *
	 * PER_DIEM_LOOKUP_DATE can also be passed in for a custom date duration search
	 *
	 * @param fieldValues
	 * @return
	 */
	public PerDiem findPerDiem(Map<String, Object> fieldValues);

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

}
