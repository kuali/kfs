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
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.kns.util.ErrorMessage;

public interface ExpenseImportByTripService {

    /**
     * 
     * This method checks to see if the following fields are present in the {@link AgencyStagingData}:
     * Trip ID, Accounting Info, Expense Amount, Invoice Number, Transaction Posting Date, Alternate Trip ID, 
     * Trip Info (contains air ticket number, air service fee number, lodging itinerary number, rental car itinerary number)
     * @param agencyData
     * @return 
     */
    public boolean areMandatoryFieldsPresent(AgencyStagingData agencyData);
    
    public boolean isTripDataMissing(AgencyStagingData agencyData);
    
    public boolean isAccountingInfoMissing(AgencyStagingData agencyData);
    
    /**
     * 
     * This method performs the validation on the {@link AgencyStagingData} object.
     * @param agencyData
     * @return
     */
    public AgencyStagingData validateAgencyData(AgencyStagingData agencyData);
    
    /**
     * retrieve Error messages list
     * 
     * @return
     */
    public List<ErrorMessage> getErrorMessages();
    
    /**
     * 
     * @param errorMessages
     */
    public void setErrorMessages(List<ErrorMessage> errorMessages);
    
    /**
     * 
     * This method looks up Travel Documents based on the trip id. It returns the TA if found.
     * @param agencyData
     * @return
     */
    public TravelDocument validateTripId(AgencyStagingData agencyData);
    
    /**
     * 
     * This method validates the Account Number Sub-account Number, Project Code, Object Code and Sub-object Code 
     * for each of the {@link TripAccountingInformation} objects. It uses the VALIDATION_ACCOUNTING_LINE parameter 
     * to determine which fields are to be validated.
     * @param profile
     * @param agencyData
     * @param ta
     * @return
     */
    public AgencyStagingData validateAccountingInfo(TEMProfile profile, AgencyStagingData agencyData, TravelAuthorizationDocument ta);
    
    /**
     * 
     * This method checks to see if the agencyData already exists in the staging table, based on the following properties: 
     * Trip ID, Agency Name, Transaction Date, Transaction Amount
     * @param agencyData
     * @return
     */
    public boolean isDuplicate(AgencyStagingData agencyData);

    /**
     * 
     * This method performs the "matching process" where it matches agency data with credit card data. If the agency matches a credit card, 
     * GLPEs are created, and {@link HistoricalTravelExpense} is created (hanging off of the tripId's travel document).
     * @param agencyData
     * @param sequenceHelper
     * @return
     */
    public boolean reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper); 

}
