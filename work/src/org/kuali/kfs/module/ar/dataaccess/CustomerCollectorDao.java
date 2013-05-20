/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;

/**
 * DAO interface for Customer Collector.
 */
public interface CustomerCollectorDao {

    /**
     * Saves the customerCollector object.
     * 
     * @param customerCollector The customer collector object to save.
     */
    public void save(CustomerCollector customerCollector);

    /**
     * This method retrieves the list of customer numbers for the given criteria.
     * 
     * @param criteria criteria to match with
     * @return Returns the list of customernumbers according to the matched criteria
     */
    public List<String> retrieveCustomerNmbersByCriteria(Criteria criteria);
}
