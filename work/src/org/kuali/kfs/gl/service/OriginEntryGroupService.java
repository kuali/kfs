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

/**
 * An interface of methods to interact with Origin Entry Groups
 */
public interface OriginEntryGroupService {
    public OriginEntryGroup getGroupWithMaxIdFromSource(String sourceCode);

    /**
     * Mark a group as don't process
     * 
     * @param groupId the id of the group to mark
     */
    public void dontProcessGroup(Integer groupId);

    /**
     * Marks all backup groups (source code BACK) in the database so that they will not be scrubbed when the nightly scrubber step
     * runs again.
     */
    public void markScrubbableBackupGroupsAsUnscrubbable();

    /**
     * Marks all postable scrubber valid groups (source code SCV) in the database so that they will not be posted when the main
     * posted runs
     */
    public void markPostableScrubberValidGroupsAsUnpostable();

    /**
     * Marks all of the origin entry groups that would be returned from getIcrGroupsToPost() as don't process
     */
    public void markPostableIcrGroupsAsUnpostable();

    /**
     * Get the newest scrubber error group
     * 
     * @return the origin entry group that was most recently created, or null if there are no origin entry groups in the database
     */
    public OriginEntryGroup getNewestScrubberErrorGroup();

    /**
     * Create the backup group which has all the entries from all the groups where all the flags are set Y.
     */
    public void createBackupGroup();

    /**
     * Create the backup group which has all the entries from all the groups where all the flags are set Y.
     */
    public void createLaborBackupGroup();


    /**
     * Delete all the groups (and entries) where the group is this many days old or older
     * 
     * @param days groups older than the given days will be deleted by this method
     */
    public void deleteOlderGroups(int days);

    /**
     * Deletes several origin entry groups
     * 
     * @param groupsToDelete a Collection of origin entry groups to delete
     */
    public void deleteGroups(Collection<OriginEntryGroup> groupsToDelete);

    /**
     * Get groups that match
     * 
     * @param criteria a map of criteria to build a query from
     * @return a Collection of qualifying origin entry groups
     */
    public Collection getMatchingGroups(Map criteria);

    /**
     * Retrieves all groups which are not marked to be processed
     * 
     * @return a Collection of qualifying origin entry groups
     */
    public Collection getOriginEntryGroupsPendingProcessing();

    /**
     * Returns all groups of entries that should be processed by the poster
     * 
     * @return a Collection of qualifying origin entry groups
     */
    public Collection getGroupsToPost();

    /**
     * get entry groups to be posted that have the given group source code
     * 
     * @param entryGroupSourceCode the given group source code
     * @return the entry groups to be posted that have the given group source code
     */
    public Collection getGroupsToPost(String entryGroupSourceCode);

    /**
     * Returns all groups with indirect cost recovery entries ready for positing
     * 
     * @return a Collection of origin entry groups of indirect cost recovery entries to post
     */
    public Collection getIcrGroupsToPost();

    /**
     * Gets a collection of all scrubbable backup groups (i.e. scrub, valid, process indicators all true)
     * 
     * @return a Collection of origin entry backup groups that should be scrubbed
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups();

    /**
     * Get all the unscrubbed backup groups for Labor
     * 
     * @param backupDate the date all groups created on or before should be return to be backed up
     * @return a Collection of labor origin entry groups to backup
     */
    public Collection getLaborBackupGroups(Date backupDate);


    /**
     * Get all the groups that need to be put into the backup group
     * 
     * @param backupDate the date all groups created on or before should be return to be backed up
     * @return a Collection of origin entry groups to backup
     */
    public Collection getGroupsToBackup(Date backupDate);

    /**
     * Creates a brand new group
     * 
     * @param date the date this group should list as its creation date
     * @param sourceCode the source of this origin entry group
     * @param valid whether this group is valid - ie, all entries within it are valid
     * @param process whether this group should be processed by the next step
     * @param scrub whether this group should be input to the scrubber
     * @return a new origin entry group to put origin entries into
     */
    public OriginEntryGroup createGroup(Date date, String sourceCode, boolean valid, boolean process, boolean scrub);

    /**
     * save a group
     * 
     * @param group the group to save
     */
    public void save(OriginEntryGroup group);

    /**
     * Returns the origin entry group by the given id
     * 
     * @param id the id of the group to retrieve
     * @return an origin entry group with the given id, or null if nothing could be found
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id);

    /**
     * Retrieves all origin entry groups currently in the persistence store
     * 
     * @return a Collection of all the origin entry groups in the persistence store
     */
    public Collection getAllOriginEntryGroup();

    /**
     * Retrieves origin entry groups created within the past number of given days
     * 
     * @param days groups created within the past number of days will be retrieved
     * @return a Collection of retrieved origin entry groups
     */
    public Collection getRecentGroupsByDays(int days);

    /**
     * Returns whether the group indicated with the group ID still exists within the system
     * 
     * @param groupId the id of the group to check for existence
     * @return true if it still exists in the persistence mechanism, false otherwise
     */
    public boolean getGroupExists(Integer groupId);
}
