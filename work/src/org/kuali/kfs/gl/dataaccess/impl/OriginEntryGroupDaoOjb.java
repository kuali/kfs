/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/impl/OriginEntryGroupDaoOjb.java,v $
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
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 *  
 */
public class OriginEntryGroupDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntryGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupDaoOjb.class);

    private static final String DATE = "date";
    private static final String ID = "id";
    private static final String SOURCE_CODE = "sourceCode";
    private static final String PROCESS = "process";
    private static final String VALID = "valid";
    private static final String SCRUB = "scrub";

    public OriginEntryGroupDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#copyGroup(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void copyGroup(OriginEntryGroup fromGroup, OriginEntryGroup toGroup) {
        LOG.debug("copyGroup() started");

        String sql = "insert into GL_ORIGIN_ENTRY_T (ORIGIN_ENTRY_ID, OBJ_ID, VER_NBR, ORIGIN_ENTRY_GRP_ID, ACCOUNT_NBR,FDOC_NBR, FDOC_REF_NBR, FDOC_REF_TYP_CD, " + "FDOC_REVERSAL_DT, FDOC_TYP_CD, FIN_BALANCE_TYP_CD, FIN_COA_CD,FIN_OBJ_TYP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FS_ORIGIN_CD, FS_REF_ORIGIN_CD, " + "ORG_DOC_NBR, ORG_REFERENCE_ID,PROJECT_CD, SUB_ACCT_NBR, TRANSACTION_DT, TRN_DEBIT_CRDT_CD, TRN_ENCUM_UPDT_CD, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_AMT," + "TRN_LDGR_ENTR_DESC, UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, TRN_SCRBBR_OFST_GEN_IND, BDGT_YR) " + "select gl_origin_entry_t_seq.nextval, sys_guid(), 1, " + toGroup.getId() + ", ACCOUNT_NBR, FDOC_NBR, FDOC_REF_NBR, FDOC_REF_TYP_CD, FDOC_REVERSAL_DT, " + "FDOC_TYP_CD, FIN_BALANCE_TYP_CD, FIN_COA_CD, FIN_OBJ_TYP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FS_ORIGIN_CD, FS_REF_ORIGIN_CD, ORG_DOC_NBR,ORG_REFERENCE_ID, "
                + "PROJECT_CD, SUB_ACCT_NBR, TRANSACTION_DT, TRN_DEBIT_CRDT_CD, TRN_ENCUM_UPDT_CD, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_AMT,TRN_LDGR_ENTR_DESC, UNIV_FISCAL_PRD_CD, " + "UNIV_FISCAL_YR, TRN_SCRBBR_OFST_GEN_IND, BDGT_YR from GL_ORIGIN_ENTRY_T where ORIGIN_ENTRY_GRP_ID = " + fromGroup.getId();

        sqlCommand(sql);
    }

    public Collection<OriginEntryGroup> getGroupsFromSourceForDate(String sourceCode, Date date) {
        LOG.debug("getGroupsFromSourceForDay() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(DATE, date);
        criteria.addEqualTo(SOURCE_CODE, sourceCode);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
        
    }
    
    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getOlderGroups(Date)
     */
    public Collection<OriginEntryGroup> getOlderGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(DATE, day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
    }

    /**
     * 
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
     * 
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
     * 
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
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getBackupGroups(java.sql.Date)
     */
    public Collection getBackupGroups(Date groupDate) {
        LOG.debug("getGroupsToBackup() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(DATE, groupDate);
        criteria.addEqualTo(SOURCE_CODE, OriginEntrySource.BACKUP);
        criteria.addEqualTo(SCRUB, Boolean.TRUE);
        criteria.addEqualTo(PROCESS, Boolean.TRUE);
        criteria.addEqualTo(VALID, Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getScrubberGroups(java.sql.Date)
     */
    public Collection getGroupsToBackup(Date groupDate) {
        LOG.debug("getScrubberGroups() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(DATE, groupDate);
        criteria.addEqualTo(SCRUB, Boolean.TRUE);
        criteria.addEqualTo(PROCESS, Boolean.TRUE);
        criteria.addEqualTo(VALID, Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#save(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void save(OriginEntryGroup group) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(group);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        LOG.debug("getMatchingEntries() started");
        return (OriginEntryGroup) getPersistenceBrokerTemplate().getObjectById(OriginEntryGroup.class, id);
    }

    /**
     * Run a sql command
     * 
     * @param sql
     * @return
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
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getRecentGroups(Date)
     */
    public Collection<OriginEntryGroup> getRecentGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(DATE, day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class, criteria));
    }


}
