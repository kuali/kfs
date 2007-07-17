/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;

public interface OriginEntryGroupService {
    public Collection getGroupsFromSourceForDate(String sourceCode, Date date);

    /**
     * Mark a group as don't process
     * 
     * @param groupId
     */
    public void dontProcessGroup(Integer groupId);

    /**
     * Get the newest scrubber error group
     * 
     * @return
     */
    public OriginEntryGroup getNewestScrubberErrorGroup();

    /**
     * Create the backup group which has all the entries from all the groups where all the flags are set Y.
     * 
     */
    public void createBackupGroup();

    /**
     * Create the backup group which has all the entries from all the groups where all the flags are set Y.
     * 
     */
    public void createLaborBackupGroup();
    
    
    /**
     * Delete all the groups (and entries) where the group is this many days old or older
     * 
     * @param days
     */
    public void deleteOlderGroups(int days);
    
    public void deleteGroups(Collection<OriginEntryGroup> groupsToDelete);

    /**
     * Get groups that match
     * 
     * @param criteria
     * @return
     */
    public Collection getMatchingGroups(Map criteria);

    public Collection getOriginEntryGroupsPendingProcessing();

    public Collection getGroupsToPost();
    
    /**
     * get entry groups to be posted that have the given group source code
     * @param entryGroupSourceCode the given group source code
     * @return the entry groups to be posted that have the given group source code
     */
    public Collection getGroupsToPost(String entryGroupSourceCode);

    public Collection getIcrGroupsToPost();

    /**
     * Get all the unscrubbed backup groups
     * 
     * @param backupDate
     * @return
     */
    public Collection getBackupGroups(Date backupDate);

    /**
     * Get all the unscrubbed backup groups for Labor
     * 
     * @param backupDate
     * @return
     */
    public Collection getLaborBackupGroups(Date backupDate);
    
    
    /**
     * Get all the groups that need to be put into the backup group
     * 
     * @param backupDate
     * @return
     */
    public Collection getGroupsToBackup(Date backupDate);

    /**
     * Create a new group
     * 
     * @param date
     * @param sourceCode
     * @param valid
     * @param process
     * @param scrub
     * @return
     */
    public OriginEntryGroup createGroup(Date date, String sourceCode, boolean valid, boolean process, boolean scrub);

    /**
     * save a group
     * 
     * @param group
     */
    public void save(OriginEntryGroup group);

    public OriginEntryGroup getExactMatchingEntryGroup(Integer id);

    public Collection getAllOriginEntryGroup();

    public Collection getRecentGroupsByDays(int days);
    
    /**
     * Returns whether the group indicated with the group ID still exists within the system
     * 
     * @param groupId
     * @return
     */
    public boolean getGroupExists(Integer groupId);
}
