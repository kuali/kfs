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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.July1PositionFunding;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class is an interface to data access objects for general labor related inquiries. It will be deprecated after the data
 * access methods here are put down to business object level.
 */
public interface LaborDao {

    /**
     * This method returns an encumberence total for a given selection criteria
     * 
     * @param fieldValues
     * @return
     */
    Object getEncumbranceTotal(Map fieldValues);

    /**
     * This method returns current funds data
     * 
     * @param fieldValues
     * @param isConsolidated
     * @return Collection
     */
    Iterator getCurrentFunds(Map fieldValues, boolean isConsolidated);

    /**
     * This method returns current July1 Position Funding data
     * 
     * @param fieldValues
     * @return Collection
     */
    Collection<July1PositionFunding> getJuly1PositionFunding(Map<String, String> fieldValues);

    Collection getJuly1(Map fieldValues);

    /**
     * Stores a business object without doing a update query.
     * 
     * @param businessObject - Business Object to Store.
     */
    public void insert(BusinessObject businessObject);
}
