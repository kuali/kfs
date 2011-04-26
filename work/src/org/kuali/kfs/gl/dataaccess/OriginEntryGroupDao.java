/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;

public interface OriginEntryGroupDao {
    /**
     * Given an origin entry group source type (defined in OriginEntrySource)
     * 
     * @param sourceCode the source code of the groups to find
     * @return a OriginEntryGroup with the given source code and max ORIGIN_ENTRY_GRP_ID
     * @see org.kuali.kfs.gl.businessobject.OriginEntrySource
     */
    public OriginEntryGroup getGroupWithMaxIdFromSource(String sourceCode);

    /**
     * Get all the groups that are older than a date
     * 
     * @param day the date groups returned should be older than
     * @return a Collection of origin entry groups older than that date
     */
    public Collection<OriginEntryGroup> getOlderGroups(Date day);

    /**
     * Delete all the groups in the list.  Note...it doesn't delete the entries within them, you need
     * OriginEntryDao.deleteGroups for that
     * 
     * @params groups a Collection of origin entry groups to delete
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups);

    /**
     * Fetch all the groups that match the criteria
     * 
     * @param searchCriteria a Map of search criteria to form the query
     * @return a Collection of Origin Entry Groups that match that criteria
     */
    public Collection getMatchingGroups(Map searchCriteria);

    /**
     * Get all the groups for the poster (that is to say, Groups with "Process" being true)
     * 
     * @param groupSourceCode the source code of origin entry groups to return
     * @return a Collection of origin entry groups that should be processed by the poster
     */
    public Collection getPosterGroups(String groupSourceCode);

    /**
     * Gets a collection of all backup groups that are scrubbable (i.e. valid, process, scrub indicators all set to true)
     * 
     * @return a Collection of scrubbable origin entry groups
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups();

    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate the date returned origin entry groups must have been created on or before
     * @return a Collection of origin entry groups to backup
     */
    public Collection getGroupsToBackup(Date groupDate);

    /**
     * The the group for the ID passed. The EXACT one, not one that is close, it must be EXACTLY EXACT.
     * 
     * @param id the group id of the group to return
     * @return a highly exact origin entry group, or, if not found, null
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id);

    /**
     * Fetches groups created on or after the given date
     * 
     * @param day the date origin entry groups to return must have been created on or after
     * @return a Collection of origin entry groups created on or after that day
     */
    public Collection<OriginEntryGroup> getRecentGroups(Date day);
}
