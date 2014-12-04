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
