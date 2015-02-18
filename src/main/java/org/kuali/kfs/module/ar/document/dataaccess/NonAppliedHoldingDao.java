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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;

public interface NonAppliedHoldingDao {

    /**
     * Retrieves all NonAppliedHolding objects that were created by the passed-in list of document numbers.
     *
     * @param docNumbers used for search criteria
     * @return Collection of NonAppliedHolding objects that match the search criteria.
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsByListOfDocumentNumbers(List<String> docNumbers);

    /**
     * Retrieves all NonAppliedHolding objects that correspond to the given customerNumber.
     *
     * @param customerNumber used for search criteria
     * @return Collection of NonAppliedHolding objects that match the search criteria.
     */
    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(String customerNumber);

}
