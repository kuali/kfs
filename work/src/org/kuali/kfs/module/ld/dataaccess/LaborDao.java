/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is an interface to data access objects for labor balance inquiries 
 */
public interface LaborDao {
 
    /**
     * 
     * This method returns CSF Tracker data
     * @param fieldValues
     * @return
     */
    Collection getCSFTrackerData(Map fieldValues);

    /**
     * 
     * This method returns a CSF Tracker total for a given selection criteria
     * @param fieldValues
     * @return
     */
    Object getCSFTrackerTotal(Map fieldValues);

    /**
     * 
     * This method returns an encumberence total for a given selection criteria
     * @param fieldValues
     * @return
     */
    Object getEncumbranceTotal(Map fieldValues);
    
    /**
     * 
     * This method returns base funds data
     * @param fieldValues
     * @return
     */
    Collection getBaseFunds(Map fieldValues);
    
    /**
     * 
     * This method returns current funds data
     * @param fieldValues
     * @return
     */
    Collection getCurrentFunds(Map fieldValues);
    
    /**
     * 
     * This method returns current funds data
     * @param fieldValues
     * @return
     */
    Iterator getPersonFunding(Map fieldValues);
}