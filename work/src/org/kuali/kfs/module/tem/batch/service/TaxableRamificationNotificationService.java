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
