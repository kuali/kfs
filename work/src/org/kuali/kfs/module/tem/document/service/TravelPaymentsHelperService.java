/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

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
     * @param payment the payment information from the document
     * @param bankCode the code of the bank from the document
     * @return a generically built PaymentGroup
     */
    public abstract PaymentGroup buildGenericPaymentGroup(TravelerDetail traveler, TravelPayment payment, String bankCode);

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
