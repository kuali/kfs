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
package org.kuali.kfs.module.tem.document.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;

/**
 * define the service calls and operations on taxable ramification document
 */
public interface TaxableRamificationDocumentService {

    /**
     * create a taxable ramification document from the given travel advance, and blanket approve it
     * 
     * @param travelAdvance the given travel advance
     * @return a taxable ramification document created from the given travel advance
     */
    TaxableRamificationDocument createAndBlanketApproveRamificationDocument(TravelAdvance travelAdvance);

    /**
     * create a taxable ramification document from the given travel advance
     * 
     * @param travelAdvance the given travel advance
     * @return a taxable ramification document created from the given travel advance
     */
    TaxableRamificationDocument createRamificationDocument(TravelAdvance travelAdvance);

    /**
     * blanket approve the given taxable ramification document
     * 
     * @param taxableRamificationDocument the given taxable ramification document
     */
    void blanketApproveRamificationDocument(TaxableRamificationDocument taxableRamificationDocument);

    /**
     * check whether there is an existing taxable ramification created from the given travel advance
     * 
     * @param travelAdvance the given travel advance
     * @return true if there is an existing taxable ramification created from the given travel advance; otherwise, false
     */
    boolean hasTaxableRamification(TravelAdvance travelAdvance);
    
    /**
     * get all the outstanding travel advances, whose age is equal to the specified by a system parameter
     * 
     * @return all the outstanding travel advances of a specified age
     */
    List<TravelAdvance> getAllQualifiedOutstandingTravelAdvance();
}
