/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
     * Delete all the groups (and entries) where the group is this many days old or older
     * 
     * @param days
     */
    public void deleteOlderGroups(int days);

    /**
     * Get groups that match
     * 
     * @param criteria
     * @return
     */
    public Collection getMatchingGroups(Map criteria);

    public Collection getOriginEntryGroupsPendingProcessing();

    public Collection getGroupsToPost();

    public Collection getIcrGroupsToPost();

    /**
     * Get all the unscrubbed backup groups
     * 
     * @param backupDate
     * @return
     */
    public Collection getBackupGroups(Date backupDate);

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
}
