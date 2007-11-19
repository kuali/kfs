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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.Guid;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of OriginEntryGroupService
 */
@Transactional
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);

    private OriginEntryGroupDao originEntryGroupDao;
    private OriginEntryDao originEntryDao;
    private DateTimeService dateTimeService;
    private LaborOriginEntryDao laborOriginEntryDao;

    /**
     * Constructs a OriginEntryGroupServiceImpl instance
     */
    public OriginEntryGroupServiceImpl() {
        super();
    }

    /**
     * Finds the group by the given group id, and then sets it to not process
     * @param groupId the id of the group to set
     * @see org.kuali.module.gl.service.OriginEntryGroupService#dontProcessGroup(java.lang.Integer)
     */
    public void dontProcessGroup(Integer groupId) {
        LOG.debug("dontProcessGroup() started");

        OriginEntryGroup oeg = getExactMatchingEntryGroup(groupId);
        if (oeg != null) {
            oeg.setProcess(false);
            save(oeg);
        }
    }


    /**
     * Sets all scrubbable backup groups's scrub attributes to false, so none will be scrubbed
     * @see org.kuali.module.gl.service.OriginEntryGroupService#markBackupGroupsUnscrubbable()
     */
    public void markScrubbableBackupGroupsAsUnscrubbable() {
        LOG.debug("markScrubbableBackupGroupsAsUnscrubbable() started");
        for (OriginEntryGroup scrubbableBackupGroup : getAllScrubbableBackupGroups()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("marking backup origin entry group as don't process: " + scrubbableBackupGroup.getId());
            }
            scrubbableBackupGroup.setProcess(Boolean.FALSE);
            save(scrubbableBackupGroup);
        }
    }

    /**
     * Sets all groups created by the scrubber and ready to be posted's process attribute to false, so they won't be posted
     * @see org.kuali.module.gl.service.OriginEntryGroupService#markPostableScrubberValidGroupsAsUnpostable()
     */
    public void markPostableScrubberValidGroupsAsUnpostable() {
        LOG.debug("markPostableScrubberValidGroupsAsUnpostable() started");
        Collection<OriginEntryGroup> postableGroups = getGroupsToPost();
        for (OriginEntryGroup postableGroup : postableGroups) {
            if (LOG.isInfoEnabled()) {
                LOG.info("marking postable SCV origin entry group as don't process: " + postableGroup.getId());
            }
            postableGroup.setProcess(Boolean.FALSE);
            save(postableGroup);
        }
    }

    /**
     * Marks any postable ICR group's process attribute as false, so they won't be posted
     * @see org.kuali.module.gl.service.OriginEntryGroupService#markPostableIcrGroupsAsUnpostable()
     */
    public void markPostableIcrGroupsAsUnpostable() {
        LOG.debug("markPostableIcrGroupsAsUnpostable() started");
        Collection<OriginEntryGroup> postableGroups = getIcrGroupsToPost();
        for (OriginEntryGroup postableGroup : postableGroups) {
            if (LOG.isInfoEnabled()) {
                LOG.info("marking postable ICR origin entry group as don't process: " + postableGroup.getId());
            }
            postableGroup.setProcess(Boolean.FALSE);
            save(postableGroup);
        }
    }

    /**
     * Returns the most recently created scrubber error group in the database
     * @return the most recently created scrubber error group
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getNewestScrubberErrorGroup()
     */
    public OriginEntryGroup getNewestScrubberErrorGroup() {
        LOG.debug("getNewestScrubberErrorGroup() started");

        OriginEntryGroup newest = null;

        Map crit = new HashMap();
        crit.put("sourceCode", OriginEntrySource.SCRUBBER_ERROR);

        Collection groups = originEntryGroupDao.getMatchingGroups(crit);
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();

            if (newest == null) {
                newest = element;
            }
            else {
                if (newest.getId().intValue() < element.getId().intValue()) {
                    newest = element;
                }
            }
        }

        return newest;
    }

    /**
     * Returns all groups created by a given origin entry Source
     * @param sourceCode the source of the origin entry group
     * @return a OriginEntryGroup with the given source code and max ORIGIN_ENTRY_GRP_ID
     * @see org.kuali.module.gl.bo.OriginEntrySource
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getGroupsFromSource(java.lang.String)
     */
    public OriginEntryGroup getGroupWithMaxIdFromSource(String sourceCode) {
        LOG.debug("getGroupWithMaxIdFromSource() started");

        return originEntryGroupDao.getGroupWithMaxIdFromSource(sourceCode);
    }

    /**
     * Returns all groups created by the backup source that can be scrubbed
     * @return a Collection of origin entry groups to scrub
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getAllScrubbableBackupGroups()
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups() {
        return originEntryGroupDao.getAllScrubbableBackupGroups();
    }

    /**
     * Returns all labor origin entry groups created on the given date to back them up
     * @param backupDate the date to find labor origin entry groups created on
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getLaborBackupGroups(java.sql.Date)
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getLaborBackupGroups(java.sql.Date)
     */
    public Collection getLaborBackupGroups(Date backupDate) {
        LOG.debug("getBackupGroups() started");

        return originEntryGroupDao.getLaborBackupGroups(backupDate);
    }

    /**
     * Retrieves all groups to be created today, and creates backup group versions of them
     * @see org.kuali.module.gl.service.OriginEntryGroupService#createBackupGroup()
     */
    public void createBackupGroup() {
        LOG.debug("createBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();
        Collection groups = originEntryGroupDao.getGroupsToBackup(today);

        // Create the new group
        OriginEntryGroup backupGroup = this.createGroup(today, OriginEntrySource.BACKUP, true, true, true);

        for (Iterator<OriginEntryGroup> iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup group = iter.next();

            for (Iterator<OriginEntryFull> entry_iter = originEntryDao.getEntriesByGroup(group, 0); entry_iter.hasNext();) {
                OriginEntryFull entry = entry_iter.next();

                entry.setEntryId(null);
                entry.setObjectId(new Guid().toString());
                entry.setGroup(backupGroup);
                originEntryDao.saveOriginEntry(entry);
            }

            group.setProcess(false);
            group.setScrub(false);
            originEntryGroupDao.save(group);
        }
    }


    /**
     * Retrieves all labor origin entry groups to be backed up today and creates backup versions of them
     * @see org.kuali.module.gl.service.OriginEntryGroupService#createLaborBackupGroup()
     */
    public void createLaborBackupGroup() {
        LOG.debug("createBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();
        Collection groups = originEntryGroupDao.getLaborGroupsToBackup(today);

        // Create the new group
        OriginEntryGroup backupGroup = this.createGroup(today, OriginEntrySource.LABOR_BACKUP, true, true, true);

        for (Iterator<OriginEntryGroup> iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup group = iter.next();
            // Get only LaborOriginEntryGroup
            if (group.getSourceCode().startsWith("L")) {
                Iterator entry_iter = laborOriginEntryDao.getLaborEntriesByGroup(group, 0);

                while (entry_iter.hasNext()) {
                    LaborOriginEntry entry = (LaborOriginEntry) entry_iter.next();

                    entry.setEntryId(null);
                    entry.setObjectId(new Guid().toString());
                    entry.setGroup(backupGroup);
                    laborOriginEntryDao.saveOriginEntry(entry);
                }


                group.setProcess(false);
                group.setScrub(false);
                originEntryGroupDao.save(group);
            }
        }
    }


    /**
     * Deletes all groups older than a given number of days
     * @param days the number of days that groups older than should be deleted
     * @see org.kuali.module.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderGroups(int days) {
        LOG.debug("deleteOlderGroups() started");

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        Collection groups = originEntryGroupDao.getOlderGroups(new java.sql.Date(today.getTime().getTime()));

        if (groups.size() > 0) {
            originEntryDao.deleteGroups(groups);
            originEntryGroupDao.deleteGroups(groups);
        }
    }


    /**
     * Deletes every origin entry group in the given collection.  Note: this method deletes all the origin entries
     * in each group and then deletes the group.
     * @param groupsToDelete a Collection of groups to delete
     * @see org.kuali.module.gl.service.OriginEntryGroupService#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groupsToDelete) {
        for (OriginEntryGroup groupToDelete : groupsToDelete) {
            if (groupToDelete.getId() == null) {
                throw new NullPointerException("Received null group ID trying to delete groups");
            }
        }

        if (groupsToDelete.size() > 0) {
            originEntryDao.deleteGroups(groupsToDelete);
            originEntryGroupDao.deleteGroups(groupsToDelete);
        }
    }

    /**
     * Return all the origin entry groups that have a process attribute of false.
     * @return a Collection of all origin entry groups that have a process indicator of false. collection is returned read-only.
     */
    public Collection getOriginEntryGroupsPendingProcessing() {
        LOG.debug("getOriginEntryGroupsPendingProcessing() started");

        Map criteria = new HashMap();
        criteria.put("process", Boolean.FALSE);
        Collection returnCollection = new ArrayList();
        returnCollection = originEntryGroupDao.getMatchingGroups(criteria);
        return returnCollection;
    }

    /**
     * Returns all origin entry groups currently in the databse
     * @return a Collection of all origin entry groups in the database
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getAllOriginEntryGroup()
     */
    public Collection getAllOriginEntryGroup() {
        LOG.debug("getAllOriginEntryGroup() started");
        Map criteria = new HashMap();

        Collection<OriginEntryGroup> c = originEntryGroupDao.getMatchingGroups(criteria);

        // GLCP and LLCP group filter exception
        String groupException = "";
        for (int i = 0; i < KFSConstants.LLCP_GROUP_FILTER_EXCEPTION.length; i++) {
            groupException += KFSConstants.LLCP_GROUP_FILTER_EXCEPTION[i] + " ";
        }

        // Get the row counts for each group

        for (OriginEntryGroup group : c) {

            if (group.getSourceCode().startsWith("L") && !groupException.contains(group.getSourceCode())) {
                group.setRows(laborOriginEntryDao.getGroupCount(group.getId()));
            }
            else {
                group.setRows(originEntryDao.getGroupCount(group.getId()));
            }

        }
        return c;
    }


    /**
     * Create a new OriginEntryGroup and persists it to the database.
     * @param date the date this group should list as its creation date
     * @param sourceCode the source of this origin entry group
     * @param valid whether this group is valid - ie, all entries within it are valid
     * @param process whether this group should be processed by the next step
     * @param scrub whether this group should be input to the scrubber
     * @return a new origin entry group to put origin entries into
     */
    public OriginEntryGroup createGroup(Date date, String sourceCode, boolean valid, boolean process, boolean scrub) {
        LOG.debug("createGroup() started");

        OriginEntryGroup oeg = new OriginEntryGroup();
        oeg.setDate(date);
        oeg.setProcess(Boolean.valueOf(process));
        oeg.setScrub(Boolean.valueOf(scrub));
        oeg.setSourceCode(sourceCode);
        oeg.setValid(Boolean.valueOf(valid));

        originEntryGroupDao.save(oeg);

        return oeg;
    }

    /**
     * Get all non-ICR-related OriginEntryGroups waiting to be posted as of postDate.
     * @return a Collection of origin entry groups to post
     */
    public Collection getGroupsToPost() {
        LOG.debug("getGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.SCRUBBER_VALID);
    }

    /**
     * Get all ICR-related OriginEntryGroups waiting to be posted as of postDate.
     * @return a Collection of origin entry groups with indirect cost recovery origin entries to post
     */
    public Collection getIcrGroupsToPost() {
        LOG.debug("getIcrGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.ICR_TRANSACTIONS);
    }

    /**
     * An alias for OriginEntryGroupDao.getScrubberGroups().
     * 
     * @param scrubDate the date to find backup groups for
     * @return a Collection of groups to scrub
     */
    public Collection getGroupsToBackup(Date scrubDate) {
        LOG.debug("getGroupsToScrub() started");

        return originEntryGroupDao.getGroupsToBackup(scrubDate);
    }

    /**
     * Returns all groups to post
     * @param entryGroupSourceCode the source code of origin entry groups to post
     * @return a Collection of groups to post
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getGroups(java.lang.String)
     */
    public Collection getGroupsToPost(String entryGroupSourceCode) {
        return originEntryGroupDao.getPosterGroups(entryGroupSourceCode);
    }

    /**
     * Retrieves all groups that match the given search criteria
     * @param criteria a Map of criteria to build a query from
     * @return a Collection of all qualifying origin entry groups
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getMatchingGroups(java.util.Map)
     */
    public Collection getMatchingGroups(Map criteria) {
        LOG.debug("getMatchingGroups() started");

        return originEntryGroupDao.getMatchingGroups(criteria);
    }

    /**
     * Persist an OriginEntryGroup to the database.
     * 
     * @param originEntryGroup
     */
    public void save(OriginEntryGroup originEntryGroup) {
        LOG.debug("save() started");

        originEntryGroupDao.save(originEntryGroup);
    }

    /**
     * Returns the origin entry group with the given id
     * @param id the id of the origin entry group to retreive
     * @return the origin entry group with the given id if found, otherwise null
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        return originEntryGroupDao.getExactMatchingEntryGroup(id);
    }

    /**
     * Returns groups created within the past number of given days
     * @param days the number of days returned groups must be younger than
     * @return a Collection of qualifying groups
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getRecentGroupsByDays(int)
     */
    public Collection getRecentGroupsByDays(int days) {

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        Collection groups = originEntryGroupDao.getRecentGroups(new java.sql.Date(today.getTime().getTime()));

        return groups;
    }


    /**
     * Returns whether or not a group with the given id exists in the database
     * @param groupId the id of the group to check for existence
     * @return true if such a group exists, false otherwise
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getGroupExists(java.lang.Integer)
     */
    public boolean getGroupExists(Integer groupId) {
        Map<String, Integer> criteria = new HashMap<String, Integer>();
        criteria.put("id", groupId);
        Collection groups = getMatchingGroups(criteria);
        return groups.size() > 0;
    }

    public void setOriginEntryGroupDao(OriginEntryGroupDao oegd) {
        originEntryGroupDao = oegd;
    }

    public void setOriginEntryDao(OriginEntryDao oed) {
        originEntryDao = oed;
    }

    public void setLaborOriginEntryDao(LaborOriginEntryDao loed) {
        laborOriginEntryDao = loed;
    }


    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
}
