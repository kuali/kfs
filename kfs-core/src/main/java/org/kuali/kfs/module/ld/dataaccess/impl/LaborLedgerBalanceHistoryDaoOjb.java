/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for labor balance history
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory
 */
public class LaborLedgerBalanceHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements LedgerBalanceHistoryBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceHistoryDaoOjb.class);

    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerBalanceHistoryBalancingDao#findDistinctFiscalYears()
     */
    public List<Integer> findDistinctFiscalYears() {
        Criteria crit = new Criteria();
        ReportQueryByCriteria q = QueryFactory.newReportQuery(LaborBalanceHistory.class, crit);
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
