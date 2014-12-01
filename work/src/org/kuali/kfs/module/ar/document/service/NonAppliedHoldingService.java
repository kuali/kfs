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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;

public interface NonAppliedHoldingService {
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(String customerNumber);
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(Customer customer);
    
    /**
     * 
     * Retrieves all NonAppliedHolding objects that were created by the 
     * passed-in list of document numbers.
     * 
     * @param docNumbers
     * @return
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsByListOfDocumentNumbers(List<String> docNumbers);
}
