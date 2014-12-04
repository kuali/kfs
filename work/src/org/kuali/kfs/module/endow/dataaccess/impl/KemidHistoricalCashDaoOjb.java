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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class KemidHistoricalCashDaoOjb extends PlatformAwareDaoBaseOjb implements KemidHistoricalCashDao {
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao#getHistoricalCashRecords(java.util.List, org.kuali.rice.core.api.util.type.KualiInteger)
     */
    public List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger medId) {
        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemids);
        criteria.addEqualTo(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        QueryByCriteria qbc = QueryFactory.newQuery(KemidHistoricalCash.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID);
        
        return (List<KemidHistoricalCash>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao#getHistoricalCashRecords(java.util.List, org.kuali.rice.core.api.util.type.KualiInteger, org.kuali.rice.core.api.util.type.KualiInteger)
     */
    public List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger beginningMed, KualiInteger endingMed) {
        Criteria criteria = new Criteria();
        Collection<KualiInteger> monthEndIds = new ArrayList<KualiInteger>();
        
        monthEndIds.add(beginningMed);
        monthEndIds.add(endingMed);
        
        if (kemids != null) {
            for (String kemid : kemids) {
                Criteria c = new Criteria();
                if (kemid.contains(KFSConstants.WILDCARD_CHARACTER)) {
                    c.addLike(EndowPropertyConstants.KEMID, kemid.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
                } else {
                    c.addEqualTo(EndowPropertyConstants.KEMID, kemid.trim());
                }
                criteria.addOrCriteria(c);
            }
        }
        
        criteria.addIn(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, monthEndIds);
        QueryByCriteria qbc = QueryFactory.newQuery(KemidHistoricalCash.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID);
        
        return (List<KemidHistoricalCash>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao#getKemidsFromHistoryCash(org.kuali.rice.core.api.util.type.KualiInteger, org.kuali.rice.core.api.util.type.KualiInteger)
     */
    public List<KemidHistoricalCash> getKemidsFromHistoryCash(KualiInteger beginningMed, KualiInteger endingMed) {
        return getKemidsFromHistoryCash("", beginningMed, endingMed);
    }
   
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao#getKemidsFromHistoryCash(java.lang.String, org.kuali.rice.core.api.util.type.KualiInteger, org.kuali.rice.core.api.util.type.KualiInteger)
     */
    public List<KemidHistoricalCash> getKemidsFromHistoryCash(String kemid, KualiInteger beginningMed, KualiInteger endingMed) {
        Criteria criteria = new Criteria();
        if (kemid != null && !kemid.isEmpty()) {
            criteria.addGreaterOrEqualThanField(EndowPropertyConstants.KEMID, kemid);
        }
        criteria.addGreaterOrEqualThanField(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, beginningMed);
        criteria.addLessOrEqualThan(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, endingMed);
        QueryByCriteria qbc = QueryFactory.newQuery(KemidHistoricalCash.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID);        
        return (List<KemidHistoricalCash>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
}
