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

package org.kuali.kfs.sys.service;

import java.util.Collection;
import java.util.Set;

import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

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
     * @param lookupResultsSequenceNumber
     * @param personId
     * @return Set<String>
     */
    public Set<String> retrieveSetOfSelectedObjectIds(String lookupResultsSequenceNumber, String personId) throws Exception;

    /**
     * @param lookupResultsSequenceNumber
     * @param setOfSelectedObjIds
     * @param boClass
     * @param personId
     * @return Collection<PersistableBusinessObject>
     */
    public Collection<PersistableBusinessObject> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Set<String> setOfSelectedObjIds, Class boClass, String personId) throws Exception;
}

