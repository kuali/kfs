/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Deprecated
public interface LookupResultsService extends Serializable {
    /**
     * Persists a list of result row objects into a database.  The lookup results sequence number acts like a key identifying the lookup
     * results set.  If results are persisted under the same sequence number, then the previously persisted list will be overwritten. 
     * 
     * @param lookupResultsSequenceNumber the lookup sequence number.  Every time a user clicks "search", a new sequence number should be generated
     * @param resultTable A list of result rows.  Note that this list does not contain BOs, but the data necessary to render a lookup results screen
     * @param personId the user that is performing the search.  This prevents a malicious user from passing someone else's sequence number 
     * (which he can guess) and eventually retrieving it, possibly exposing sensitive data
     * @throws Exception
     */
    public void persistResultsTable(String lookupResultsSequenceNumber, List<ResultRow> resultTable, String personId) throws Exception;
    
    /**
     * Persists a list of BO object IDs that have been selected for return to the calling document (the back location in lookup terminology).
     * The lookup results sequence number acts like a key identifying the selected object IDs.  If results are persisted under the same 
     * sequence number, then the previously persisted list will be overwritten. 
     * 
     * @param lookupResultsSequenceNumber the lookup sequence number.  Every time a user clicks "search", a new sequence number should be generated
     * @param selectedObjectIds A set of the object IDs of the selected rows.
     * @param personId the user that is performing the search.  This prevents a malicious user from passing someone else's sequence number 
     * (which he can guess) and eventually retrieving it, possibly exposing sensitive data
     * @throws Exception
     */
    public void persistSelectedObjectIds(String lookupResultsSequenceNumber, Set<String> selectedObjectIds, String personId) throws Exception;
    
    /**
     * Returns the list of result rows that was persisted under the passed in sequence number
     * 
     * @param lookupResultsSequenceNumber the lookup sequence number that was used to persist
     * @param personId the user id that was used to persist the results table.  This prevents a malicious user from passing someone else's sequence number 
     * (which he can guess) and eventually retrieving it, possibly exposing sensitive data
     * @return 
     * @throws Exception many reasons, including if the user id parameter does not match the user used to persist the results
     */
    public List<ResultRow> retrieveResultsTable(String lookupResultsSequenceNumber, String personId) throws Exception;
    
    /**
     * Returns the BOs that correspond to the selected objected IDs that were persisted under the given lookup results number
     * 
     * DB data may have changed since the time the user clicked the "search" button (e.g. someone may have changed a value that was
     * used as a query criterion).  If so, implementations may or may not choose to handle this situation.
     *
     * @param lookupResultsSequenceNumber the lookup sequence number that was used to persist
     * @param boClass The class of BO being retrieved from the lookup
     * @param personId the user id that was used to persist the results table.  This prevents a malicious user from passing someone else's sequence number 
     * (which he can guess) and eventually retrieving it, possibly exposing sensitive data
     * @return A list of BOs corresponding to the 
     * @throws Exception many reasons, including if the user id parameter does not match the user used to persist the results
     */
    public <T extends BusinessObject> Collection<T> retrieveSelectedResultBOs(String lookupResultsSequenceNumber, Class<T> boClass, String personId) throws Exception;
    
    /**
     * Returns whether a user is allowed to view the lookup results of the given sequence number
     * 
     * @param lookupResultsSequenceNumber the lookup sequence number that was used to persist the results table
     * @param personId the user id that was used to persist the results table.
     * @return if the user ID used to persist the lookup results is the same user ID as the parameter
     */
    public boolean isAuthorizedToAccessLookupResults(String lookupResultsSequenceNumber, String personId);
    
    /**
     * Returns whether a user is allowed to view the selected object IDs of the given sequence number
     * 
     * @param lookupResultsSequenceNumber the lookup sequence number that was used to persist the selected object IDs
     * @param personId the user id that was used to persist the selected object IDs
     * @return if the user ID used to persist the selected object IDs is the same user ID as the parameter
     */
    public boolean isAuthorizedToAccessSelectedObjectIds(String lookupResultsSequenceNumber, String personId);
    
    /**
     * Removes the lookup results that were persisted under this lookup results sequence number
     * 
     * @param lookupResultsSequenceNumber
     * @throws Exception
     */
    public void clearPersistedLookupResults(String lookupResultsSequenceNumber) throws Exception;
    
    /**
     * Removes the lookup results that were persisted under this selected object IDs
     * 
     * @param lookupResultsSequenceNumber
     * @throws Exception
     */
    public void clearPersistedSelectedObjectIds(String lookupResultsSequenceNumber) throws Exception;
    
    /**
     * removes all LookupResults BO where the lookup date attribute is older than
     * the parameter
     * 
     * @param expirationDate all LookupResults having a lookup date before this date 
     * will be removed
     */
    public void deleteOldLookupResults(Timestamp expirationDate);
    
    /**
     * removes all LookupResults BO where the lookup date attribute is older than
     * the parameter
     * 
     * @param expirationDate all LookupResults having a lookup date before this date 
     * will be removed
     */
    public void deleteOldSelectedObjectIds(Timestamp expirationDate);
    
    /**
     * Determines the lookup id for a given business object
     * 
     * @param businessObject the business object to get a lookup id for
     * @return the lookup id
     */
    public abstract String getLookupId(BusinessObject businessObject);
}

