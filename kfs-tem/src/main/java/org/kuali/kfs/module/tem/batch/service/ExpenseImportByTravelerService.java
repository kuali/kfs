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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
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

    public Map<String,ErrorMessage> validateAccountingInfoLine(TripAccountingInformation accountingLine);

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
