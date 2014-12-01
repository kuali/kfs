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

