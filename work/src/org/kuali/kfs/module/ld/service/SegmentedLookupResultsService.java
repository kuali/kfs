/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Set;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.lookup.LookupResultsService;
import org.kuali.core.service.DateTimeService;

/**
 * Used for segemented lookup results
 */
public interface SegmentedLookupResultsService extends LookupResultsService { 

    /**
     * Retrieve the Date Time Service
     *
     * @return Date Time Service
     */
    public DateTimeService getDateTimeService();
    
    /**
     * Assign the Date Time Service
     *
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService);

    /**
     * 
     * @param lookupResultsSequenceNumber
     * @param universalUserId
     * @return Set<String> 
     */
    public Set<String> retrieveSetOfSelectedObjectIds(String lookupResultsSequenceNumber, String universalUserId) throws Exception ;

    /**
     *
     * @param lookupResultsSequenceNumber
     * @param setOfSelectedObjIds
     * @param boClass
     * @param universalUserId
     * @return Collection<PersistableBusinessObject>
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, 
                                                                           Set<String> setOfSelectedObjIds,
                                                                           Class boClass, String universalUserId) throws Exception;    
}
