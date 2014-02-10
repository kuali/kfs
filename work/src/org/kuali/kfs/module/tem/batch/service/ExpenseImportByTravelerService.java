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
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.rice.krad.util.ErrorMessage;

public interface ExpenseImportByTravelerService {

    /**
     *
     * This method checks to see if the following fields are present in the {@link AgencyStagingData}:
     * Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateMandatoryFieldsPresent(AgencyStagingData agencyData);

    /**
     *
     * This method performs the validation on the {@link AgencyStagingData} object.
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateAgencyData(AgencyStagingData agencyData);

    /**
     * This method performs validation for on the {@link AgencyStagingData} object for traveler information.
     *
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateTraveler(AgencyStagingData agencyData);

    /**
     * This method looks up the {@link TemProfile} by Employee ID first, and then Customer Number if it is not found.
     *
     * @param agencyData
     * @return
     */
    public TemProfile getTraveler(AgencyStagingData agencyData);

    /**
     *
     * This method validates the Account Number Sub-account Number, Project Code, Object Code and Sub-object Code.
     * It also creates a TripAccountingInfo and adds it to the AgencyStagingData.
     * @param agencyData
     * @return
     */
    public List<ErrorMessage> validateAccountingInfo(AgencyStagingData agencyData);

    /**
     *
     * This method checks to see if the agencyData already exists in the staging table, based on the following properties:
     * Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number
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
     *
     * This method a {@link HistoricalTravelExpense} based on the imported {@link AgencyStagingData}.
     * @param agencyData
     * @param sequenceHelper
     * @return
     */
    public List<ErrorMessage> distributeExpense(AgencyStagingData agencyData);


    /**
     * This method checks to see whether at least one of an air, lodging, or rental car itinerary number exists
     *
     * @param agencyData
     * @return
     */
    public boolean isTripDataMissing(AgencyStagingData agencyData);

}
