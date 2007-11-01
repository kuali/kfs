/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;

public interface OriginEntryGroupDao {
    /**
     * @param sourceCode
     * @return
     */
    public Collection<OriginEntryGroup> getGroupsFromSource(String sourceCode);

    /**
     * Get all the groups that are older than a date
     * 
     * @param day
     * @return
     */
    public Collection<OriginEntryGroup> getOlderGroups(Date day);

    /**
     * Delete all the groups in the list
     * 
     * @params groups
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups);

    /**
     * Get all the groups that match the criteria
     * 
     * @param searchCriteria
     * @return
     */
    public Collection getMatchingGroups(Map searchCriteria);

    /**
     * Get all the groups for the poster
     * 
     * @param groupSourceCode
     * @return
     */
    public Collection getPosterGroups(String groupSourceCode);

    /**
     * Gets a collection of all backup groups that are scrubbable (i.e. valid, process, scrub indicators all set to true)
     * 
     * @return
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups();

    /**
     * Get all the Labor backup groups to scrub
     * 
     * @param groupDate
     * @return
     */
    public Collection getLaborBackupGroups(Date groupDate);


    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate
     * @return
     */
    public Collection getGroupsToBackup(Date groupDate);

    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate
     * @return
     */
    public Collection getLaborGroupsToBackup(Date groupDate);

    /**
     * Save a group
     * 
     * @param group
     */
    public void save(OriginEntryGroup group);

    /**
     * The the group for the ID passed. The EXACT one, not one that is close, it must be EXACTLY EXACT.
     * 
     * @param id
     * @return
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id);

    public Collection<OriginEntryGroup> getRecentGroups(Date day);
}
