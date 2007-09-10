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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.Guid;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);

    private OriginEntryGroupDao originEntryGroupDao;
    private OriginEntryDao originEntryDao;
    private DateTimeService dateTimeService;
    private LaborOriginEntryDao laborOriginEntryDao;

    public OriginEntryGroupServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#dontProcessGroup(java.lang.Integer)
     */
    public void dontProcessGroup(Integer groupId) {
        LOG.debug("dontProcessGroup() started");

        OriginEntryGroup oeg = getExactMatchingEntryGroup(groupId);
        if ( oeg != null ) {
            oeg.setProcess(false);
            save(oeg);
        }
    }

    
    /**
     * @see org.kuali.module.gl.service.OriginEntryGroupService#markBackupGroupsUnscrubbable()
     */
    public void markScrubbableBackupGroupsAsUnscrubbable() {
        for (OriginEntryGroup scrubbableBackupGroup : getAllScrubbableBackupGroups()) {
            scrubbableBackupGroup.setProcess(Boolean.FALSE);
            save(scrubbableBackupGroup);
        }
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryGroupService#markPostableScrubberValidGroupsAsUnpostable()
     */
    public void markPostableScrubberValidGroupsAsUnpostable() {
        Collection<OriginEntryGroup> postableGroups = getGroupsToPost();
        for (OriginEntryGroup postableGroup : postableGroups) {
            postableGroup.setProcess(Boolean.FALSE);
            save(postableGroup);
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getNewestScrubberErrorGroup()
     */
    public OriginEntryGroup getNewestScrubberErrorGroup() {
        LOG.debug("getNewestScrubberErrorGroup() started");

        OriginEntryGroup newest = null;

        Map crit = new HashMap();
        crit.put("sourceCode", OriginEntrySource.SCRUBBER_ERROR);

        Collection groups = originEntryGroupDao.getMatchingGroups(crit);
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup)iter.next();

            if ( newest == null ) {
                newest = element;
            } else {
                if ( newest.getId().intValue() < element.getId().intValue() ) {
                    newest = element;
                }
            }
        }

        return newest;
    }
    
    public Collection getGroupsFromSourceForDate(String sourceCode, Date date) {
        LOG.debug("getGroupsFromSourceForDate() started");
        
        return originEntryGroupDao.getGroupsFromSourceForDate(sourceCode, date);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getAllScrubbableBackupGroups()
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups() {
        return originEntryGroupDao.getAllScrubbableBackupGroups();
    }
    
    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getLaborBackupGroups(java.sql.Date)
     */
    public Collection getLaborBackupGroups(Date backupDate) {
        LOG.debug("getBackupGroups() started");

        return originEntryGroupDao.getLaborBackupGroups(backupDate);
    }
    
    /**
     * 
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

            for (Iterator<OriginEntry> entry_iter = originEntryDao.getEntriesByGroup(group, 0); entry_iter.hasNext();) {
                OriginEntry entry = entry_iter.next();
                
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
     * 
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
            //Get only LaborOriginEntryGroup
            if (group.getSourceCode().startsWith("L")){
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
     * 
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
     * 
     * @return the List of all origin entry groups that have a process indicator of false. collection is returned read-only.
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
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getAllOriginEntryGroup()
     */
    public Collection getAllOriginEntryGroup() {
        LOG.debug("getAllOriginEntryGroup() started");
        Map criteria = new HashMap();

        Collection<OriginEntryGroup> c = originEntryGroupDao.getMatchingGroups(criteria);

        // Get the row counts for each group

        for (OriginEntryGroup group: c){
            
            if (group.getSourceCode().startsWith("L")){
                group.setRows(laborOriginEntryDao.getGroupCount(group.getId()));    
            } else {
                group.setRows(originEntryDao.getGroupCount(group.getId()));
            }
            
        }
          return c;
    }


    /**
     * Create a new OriginEntryGroup and persist it to the database.
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
     */
    public Collection getGroupsToPost() {
        LOG.debug("getGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.SCRUBBER_VALID);
    }

    /**
     * Get all ICR-related OriginEntryGroups waiting to be posted as of postDate.
     */
    public Collection getIcrGroupsToPost() {
        LOG.debug("getIcrGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.ICR_TRANSACTIONS);
    }

    /**
     * An alias for OriginEntryGroupDao.getScrubberGroups().
     * 
     * @param scrubDate
     */
    public Collection getGroupsToBackup(Date scrubDate) {
        LOG.debug("getGroupsToScrub() started");

        return originEntryGroupDao.getGroupsToBackup(scrubDate);
    }
    
    /**
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getGroups(java.lang.String)
     */
    public Collection getGroupsToPost(String entryGroupSourceCode) {
        return originEntryGroupDao.getPosterGroups(entryGroupSourceCode);
    }

    /**
     * 
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
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        return originEntryGroupDao.getExactMatchingEntryGroup(id);
        }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getRecentGroupsByDays(int)
     */
    public Collection getRecentGroupsByDays(int days) {

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        Collection groups = originEntryGroupDao.getRecentGroups(new java.sql.Date(today.getTime().getTime()));

        return groups;
    }
    

    /**
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
