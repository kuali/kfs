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
package org.kuali.kfs.module.tem.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;

public interface TravelAuthorizationService {

    public void createCustomerInvoice(TravelAuthorizationDocument travelAuthorizationDocument);

    /**
     *
     * @param documentNumber
     * @return
     */
    public TravelAuthorizationDocument getTravelAuthorizationBy(String documentNumber);

    /**
     * Locate all {@link TravelAuthorizationDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationDocument} instances
     */
    public Collection<TravelAuthorizationDocument> find(String travelDocumentIdentifier);

    /**
     * Locate all {@link TravelAuthorizationAmendmentDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationAmendmentDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationAmendmentDocument} instances
     */
    public Collection<TravelAuthorizationAmendmentDocument> findAmendment(Integer travelDocumentIdentifier);


    /**
     * Closing the Travel Authorization
     *
     * 1. Retired the current TA
     * 2. Copy a new TAC from the TA and route it
     *
     * @param authorization
     * @param annotation
     * @param initiatorPrincipalName
     * @param reimbursementDocumentNumber the document number of the final travel reimbursement spawning the TAC
     */
    public TravelAuthorizationCloseDocument closeAuthorization(TravelAuthorizationDocument authorization, String annotation, String initiatorPrincipalName, String reimbursementDocumentNumber);

    /**
     * Get any Travel Reimbursement documen that is enroute or processed/final from the given Travel Authorization Document
     *
     * @param authorization
     * @return
     */
    public TravelReimbursementDocument findEnrouteOrProcessedTravelReimbursement(TravelAuthorizationDocument authorization);

    /**
     * find matching trips for the same traveler, dates
     *
     */
    public List<String> findMatchingTrips(TravelAuthorizationDocument authorization) ;
}
