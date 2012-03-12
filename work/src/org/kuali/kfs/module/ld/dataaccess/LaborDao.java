/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
