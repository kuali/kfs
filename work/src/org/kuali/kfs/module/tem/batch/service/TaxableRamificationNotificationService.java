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

import java.sql.Date;

import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;

/**
 * define the taxable ramification notification service
 */
public interface TaxableRamificationNotificationService {

    /**
     * send taxable ramification reports to the traveler, who have outstanding travel advance
     */
    void sendTaxableRamificationReport();
    
    /**
     * send taxable ramification reports to the traveler, who have outstanding travel advance
     * 
     * @param travelAdvance the given travel advance
     * @param taxableRamificationNotificationDate the given taxable ramification notification date, which will be applied to travel advance
     */
    void sendTaxableRamificationReport(TaxableRamificationDocument taxableRamificationDocument);

    /**
     * create and route taxable ramification reports to the traveler, who have outstanding travel advance
     * 
     * @param travelAdvance the given travel advance
     * @param taxableRamificationNotificationDate the given taxable ramification notification date, which will be applied to travel advance
     */    
    TaxableRamificationDocument createTaxableRamificationDocument(TravelAdvance travelAdvance, Date taxableRamificationNotificationDate);
}
