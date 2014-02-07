/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.krad.util.ErrorMessage;

public interface ExpenseImportByTripService {

    /**
     *
     * This method checks to see if the following fields are present in the {@link AgencyStagingData}:
     * Trip ID, Accounting Info, Expense Amount, Invoice Number, Transaction Posting Date, Alternate Trip ID,
     * Trip Info (contains air ticket number, air service fee number, lodging itinerary number, rental car itinerary number)
     * @param agencyData
     * @return List of ErrorMessage for fields which are not present
     */
    public List<ErrorMessage> validateMandatoryFieldsPresent(AgencyStagingData agencyData);

    /**
     * This method checks to see whether at least one of an air, lodging, or rental car itinerary number exists
     *
     * @param agencyData
     * @return
     */
    public boolean isTripDataMissing(AgencyStagingData agencyData);

    public List<ErrorMessage> validateMissingAccountingInfo(AgencyStagingData agencyData);

    /**
     *
     * This method performs the validation on the {@link AgencyStagingData} object.
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateAgencyData(AgencyStagingData agencyData);

    /**
     *
     * This method looks up Travel Documents based on the trip id. It returns the TA if found.
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateTripId(AgencyStagingData agencyData);

    /**
     *
     * This method validates the Account Number Sub-account Number, Project Code, Object Code and Sub-object Code
     * for each of the {@link TripAccountingInformation} objects. It uses the ACCOUNTING_LINE_VALIDATION parameter
     * to determine which fields are to be validated.
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateAccountingInfo(AgencyStagingData agencyData);

    /**
     *
     * This method checks to see if the agencyData already exists in the staging table, based on the following properties:
     * Trip ID, Agency Name, Transaction Date, Transaction Amount
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateDuplicateData(AgencyStagingData agencyData);

    /**
     * This method checks to see if the credit card agency specified is valid
     *
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateCreditCardAgency(AgencyStagingData agencyData);

    /**
     * This method checks to see whether the distribution code has a match in the AgencyServiceFee table.
     * It is not a required field so it does not complain if there is no data; it only complains if the data provided doesn't have a match.
     *
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateDistributionCode(AgencyStagingData agencyData);

    /**
     *
     * This method performs the "matching process" where it matches agency data with credit card data. If the agency matches a credit card,
     * GLPEs are created, and {@link HistoricalTravelExpense} is created (hanging off of the tripId's travel document).
     * @param agencyData
     * @param sequenceHelper
     * @return
     */
    public List<ErrorMessage> reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

}
