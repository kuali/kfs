/*
 * Copyright 2005-2009 The Kuali Foundation
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

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * An OJB implementation of LedgerEntryHistoryBalancingDao
 */
public class EntryHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements LedgerEntryHistoryBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryHistoryDaoOjb.class);
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao#findSumRowCountGreaterOrEqualThan(java.lang.Integer)
     */
    public Integer findSumRowCountGreaterOrEqualThan(Integer year) {
        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        
        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(EntryHistory.class, criteria);
        reportQuery.setAttributes(new String[] { "sum(" + KFSPropertyConstants.ROW_COUNT  + ")"});
        
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        Object[] returnResult = TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
        
        return ObjectUtils.isNull(returnResult[0]) ? 0 : ((BigDecimal) returnResult[0]).intValue();
    }
}
