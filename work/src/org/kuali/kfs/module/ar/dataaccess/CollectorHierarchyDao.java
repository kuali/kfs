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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;

/**
 * DAO interface for CollectorHierarchy.
 */
public interface CollectorHierarchyDao {

    /**
     * Gets the Collectors assigned to the head.
     * 
     * @param criteria
     * @return Returns the collection of CollectorHierarchy which matches the given criteria.
     */
    public Collection<CollectorHierarchy> getCollectorHierarchyByCriteria(Criteria criteria);

    /**
     * This method checks if given user is collector or not.
     * 
     * @param collector
     * @return Returns true if given user found collector.
     */
    public boolean isCollector(String collector);
}
