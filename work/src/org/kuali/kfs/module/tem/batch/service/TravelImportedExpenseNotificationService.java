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

import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;

public interface TravelImportedExpenseNotificationService {

    /**
     * send notifications to the travelers of newly imported or unused imported expenses from corporate card, CTS, or pre-trip
     * payments that need to be reconciled
     */
    public void sendImportedExpenseNotification();
    
    /**
     * send notifications to the given traveler of newly imported or unused imported expenses from corporate card, CTS, or pre-trip
     * payments that need to be reconciled
     * 
     * @param travelerProfileId the profile id of a traveler
     * @param expensesOfTraveler the expenses of the given traveler
     */
    public void sendImportedExpenseNotification(Integer travelerProfileId, List<HistoricalTravelExpense> expensesOfTraveler);

    /**
     * send notifications to the given traveler of newly imported or unused imported expenses from corporate card, CTS, or pre-trip
     * payments that need to be reconciled
     * 
     * @param travelerProfileId the profile id of a traveler
     */
    public void sendImportedExpenseNotification(Integer travelerProfileId);
}
