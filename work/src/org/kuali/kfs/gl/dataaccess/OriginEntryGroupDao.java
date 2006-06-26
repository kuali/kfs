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
package org.kuali.module.gl.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;

public interface OriginEntryGroupDao {
    /**
     * Copy all the entries from the from group to the to group
     * 
     * @param fromGroup
     * @param toGroup
     */
    public void copyGroup(OriginEntryGroup fromGroup,OriginEntryGroup toGroup);

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
     * Get all the backup groups to scrub
     * 
     * @param groupDate
     * @return
     */
    public Collection getBackupGroups(Date groupDate);

    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate
     * @return
     */
    public Collection getGroupsToBackup(Date groupDate);

    /**
     * Save a group
     * 
     * @param group
     */
    public void save(OriginEntryGroup group);

    /**
     * The the group for the ID passed.  The EXACT one, not one that is close, it
     * must be EXACTLY EXACT.
     * 
     * @param id
     * @return
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id);
}
