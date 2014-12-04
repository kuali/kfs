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

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * Methods shared among payment extraction services for various travel documents
 */
public interface TravelPaymentsHelperService {
    /**
     * Retrieves the campus code associated with the initiator of a passed in authorization document
     * @param document the authorization document to find a campus for
     * @param initiatorCampuses the cache of document initiator principal keys to campus codes
     * @return the campus code associated with the initiator of the given document
     */
    public abstract String findCampusForDocument(TravelDocument document, Map<String, String> initiatorCampuses);

    /**
     * Retrieves the Person record for the initiator of the document
     * @param document the document to get an initiator for
     * @return the Person record for the initiator
     */
    public abstract Person getInitiator(TravelDocument document);

    /**
     * Builds a generic payment group which can be customized for the specific travel document's needs
     * @param traveler the detail about the traveler
     * @param travelerProfile the profile for the traveler
     * @param payment the payment information from the document
     * @param bankCode the code of the bank from the document
     * @return a generically built PaymentGroup
     */
    public abstract PaymentGroup buildGenericPaymentGroup(TravelerDetail traveler, TemProfile travelerProfile, TravelPayment payment, String bankCode);

    /**
     * Builds a generic payment detail which can be customized for a specific travel document's needs
     * @param documentHeader the header of the document
     * @param processDate the date when the extraction processing is running
     * @param travelPayment the payment information from the document
     * @param initiator the Person record for the initiator of the document
     * @param achCheckDocumentType the ACH/check document type associated with the extracted document
     * @return a generically build PaymentDetail
     */
    public abstract PaymentDetail buildGenericPaymentDetail(DocumentHeader documentHeader, Date processDate, TravelPayment travelPayment, Person initiator, String achCheckDocumentType);

    /**
     * Builds a List of PaymentAccountDetails, one for each line of the given List of AccountingLines
     * @param accountingLines a List of AccountingLines
     * @return a List of generically generated PaymentAccountDetails
     */
    public abstract List<PaymentAccountDetail> buildGenericPaymentAccountDetails(List<? extends AccountingLine> accountingLines);
}
