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
package org.kuali.module.gl.dao.ojb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryGroupDao;

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
     * @see org.kuali.module.gl.bo.OriginEntrySource
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getGroupWithMaxIdFromSource(java.lang.String)
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getOlderGroups(Date)
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#deleteGroups(java.util.Collection)
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getMatchingGroups(java.util.Map)
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getPosterGroups(java.lang.String)
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getAllScrubbableBackupGroups()
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getScrubberGroups(java.sql.Date)
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
     * Saves an origin entry group
     * 
     * @param group the group to save
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#save(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void save(OriginEntryGroup group) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(group);
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
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        LOG.debug("getMatchingEntries() started");
        return (OriginEntryGroup) getPersistenceBrokerTemplate().getObjectById(OriginEntryGroup.class, id);
    }

    /**
     * Run a sql command.  This method takes its connection from the given persistence broker.
     * 
     * @param sql the sql to execute; it must be an update state
     * @return the result of the execute update
     */
    private int sqlCommand(String sql) {
        LOG.info("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    /**
     * Given a date, finds all origin entry groups that were created on or after that date
     * @param day the date that defines recency - all qualifying origin entries groups will have been created on or after that day
     * @return a Collection of OriginEntryGroup records
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getRecentGroups(Date)
     */
    public Collection<OriginEntryGroup> getRecentGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(DATE, day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
    }


}
