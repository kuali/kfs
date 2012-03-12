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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of EntryHistoryDao
 */
public class BalanceHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements LedgerBalanceHistoryBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceHistoryDaoOjb.class);
    
    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao#findDistinctFiscalYears()
     */
    public List<Integer> findDistinctFiscalYears() {
        Criteria crit = new Criteria();
        ReportQueryByCriteria q = QueryFactory.newReportQuery(BalanceHistory.class, crit);
        q.setAttributes(new String[] { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR });
        q.setDistinct(true);

        Iterator<Object[]> years = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        List<Integer> yearList = new ArrayList<Integer>();
        
        while (years != null && years.hasNext()) {
            Object[] year = years.next();
            yearList.add(new Integer(year[0].toString()));
        }
        
        return yearList;
    }
}
