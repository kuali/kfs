/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB specific implementation of OriginEntryGroupDao
 */
public class OriginEntryGroupDaoOjb extends PlatformAwareDaoBaseOjb implements OriginEntryGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupDaoOjb.class);

    private static final String DATE = "date";
    private static final String ID = "id";
    private static final String SOURCE_CODE = "sourceCode";
    private static final String PROCESS = "process";
    private static final String VALID = "valid";
    private static final String SCRUB = "scrub";
    private static final String ORIGIN_ENTRY_GRP_ID = "ORIGIN_ENTRY_GRP_ID";
    private static final String MAX_ORIGIN_ENTRY_GRP_ID = "max(ORIGIN_ENTRY_GRP_ID)";

    /**
     * Given an origin entry group source type (defined in OriginEntrySource)
     * 
     * @param sourceCode the source code of the groups to find
     * @return a OriginEntryGroup with the given source code and max ORIGIN_ENTRY_GRP_ID
     * @see org.kuali.kfs.gl.businessobject.OriginEntrySource
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getGroupWithMaxIdFromSource(java.lang.String)
     */
    public OriginEntryGroup getGroupWithMaxIdFromSource(String sourceCode) {
        LOG.debug("getGroupWithMaxIdFromSource() started");
        
        Criteria crit = new Criteria();
        
        Criteria subCrit = new Criteria();
        subCrit.addEqualTo(SOURCE_CODE, sourceCode);
        ReportQueryByCriteria subQuery = new ReportQueryByCriteria(OriginEntryGroup.class, subCrit);
        subQuery.setAttributes(new String[]{MAX_ORIGIN_ENTRY_GRP_ID});
        
        crit.addGreaterOrEqualThan(ORIGIN_ENTRY_GRP_ID, subQuery);
        
        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, crit);
        
        return (OriginEntryGroup) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Get all the groups that are older than a date
     * 
     * @param day the date groups returned should be older than
     * @return a Collection of origin entry groups older than that date
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getOlderGroups(Date)
     */
    public Collection<OriginEntryGroup> getOlderGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(DATE, day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
    }

    /**
     * Delete all the groups in the list.  Note...it doesn't delete the entries within them, you need
     * OriginEntryDao.deleteGroups for that
     * 
     * @params groups a Collection of origin entry groups to delete
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("deleteGroups() started");

        List ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }
        Criteria criteria = new Criteria();
        criteria.addIn(ID, ids);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * Builds an OJB query out of the given map of criteria and fetches all the groups that match the criteria
     * 
     * @param searchCriteria a Map of search criteria to form the query
     * @return a Collection of Origin Entry Groups that match that criteria
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getMatchingGroups(java.util.Map)
     */
    public Collection getMatchingGroups(Map searchCriteria) {
        LOG.debug("getMatchingGroups() started");

        Criteria criteria = new Criteria();
        for (Iterator iterator = searchCriteria.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next().toString();
            criteria.addEqualTo(key, searchCriteria.get(key));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Get all the groups for the poster (that is to say, Groups with "Process" being true)
     * 
     * @param groupSourceCode the source code of origin entry groups to return
     * @return a Collection of origin entry groups that should be processed by the poster
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getPosterGroups(java.lang.String)
     */
    public Collection getPosterGroups(String groupSourceCode) {
        LOG.debug("getPosterGroups() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(SOURCE_CODE, groupSourceCode);
        criteria.addEqualTo(PROCESS, Boolean.TRUE);
        criteria.addEqualTo(VALID, Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Gets a collection of all backup groups that are scrubbable (i.e. valid, process, scrub indicators all set to true)
     * 
     * @return a Collection of scrubbable origin entry groups
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getAllScrubbableBackupGroups()
     */
    public Collection<OriginEntryGroup> getAllScrubbableBackupGroups() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(SOURCE_CODE, OriginEntrySource.BACKUP);
        criteria.addEqualTo(SCRUB, Boolean.TRUE);
        criteria.addEqualTo(PROCESS, Boolean.TRUE);
        criteria.addEqualTo(VALID, Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Get all the groups to be copied into the backup group
     * 
     * @param groupDate the date returned origin entry groups must have been created on or before
     * @return a Collection of origin entry groups to backup
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getScrubberGroups(java.sql.Date)
     */
    public Collection getGroupsToBackup(Date groupDate) {
        LOG.debug("getGroupsToBackup() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(DATE, groupDate);
        criteria.addEqualTo(SCRUB, Boolean.TRUE);
        criteria.addEqualTo(PROCESS, Boolean.TRUE);
        criteria.addEqualTo(VALID, Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * We all know that computers aren't naturally exact.  They like to fudge things.  Databases especially.
     * If you send a database a table name and a primary key on that table, you really never know what you're
     * going to get, now do you?  But this method makes sure that that rascally database returns the origin
     * entry group with the primary key of the given group id.  Or null if it can't find anything.  It works
     * by magic.
     * 
     * @param id the id of the origin entry group to return
     * @return an Origin Entry Group, or null if the exact one couldn't be found
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        LOG.debug("getMatchingEntries() started");
        return (OriginEntryGroup) getPersistenceBrokerTemplate().getObjectById(OriginEntryGroup.class, id);
    }

    /**
     * Given a date, finds all origin entry groups that were created on or after that date
     * @param day the date that defines recency - all qualifying origin entries groups will have been created on or after that day
     * @return a Collection of OriginEntryGroup records
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryGroupDao#getRecentGroups(Date)
     */
    public Collection<OriginEntryGroup> getRecentGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(DATE, day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
    }


}
