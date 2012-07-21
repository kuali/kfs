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
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;

public interface ExpenseImportByTravelerService {

    /**
     * 
     * This method checks to see if the following fields are present in the {@link AgencyStagingData}:
     * Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number
     * @param agencyData
     * @return 
     */
    public boolean areMandatoryFieldsPresent(AgencyStagingData agencyData);
    
    /**
     * 
     * This method performs the validation on the {@link AgencyStagingData} object.
     * @param agencyData
     * @return
     */
    public AgencyStagingData validateAgencyData(AgencyStagingData agencyData);
    
    /**
     * 
     * This method looks up the {@link TEMProfile} by Employee ID first, and then Customer Number if it is not found.
     * @param agencyData
     * @return
     */
    public TEMProfile validateTraveler(AgencyStagingData agencyData);
    
    public List<String> getErrorMessages();
    public void setErrorMessages(List<String> errorMessages);
    
    /**
     * 
     * This method validates the Account Number Sub-account Number, Project Code, Object Code and Sub-object Code. 
     * It also creates a TripAccountingInfo and adds it to the AgencyStagingData. 
     * @param profile
     * @param agencyData
     * @return
     */
    public AgencyStagingData validateAccountingInfo(TEMProfile profile, AgencyStagingData agencyData);
        
    /**
     * 
     * This method checks to see if the agencyData already exists in the staging table, based on the following properties: 
     * Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number
     * @param agencyData
     * @return
     */
    public boolean isDuplicate(AgencyStagingData agencyData);
    
    /**
     * 
     * This method creates GLPEs and a {@link HistoricalTravelExpense} based on the imported {@link AgencyStagingData}.
     * @param agencyData
     * @param sequenceHelper
     * @return
     */
    public boolean distributeExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper); 

}
