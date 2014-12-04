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
