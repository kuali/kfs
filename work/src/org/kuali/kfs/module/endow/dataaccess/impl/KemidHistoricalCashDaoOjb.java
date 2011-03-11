/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.KualiInteger;

public class KemidHistoricalCashDaoOjb extends PlatformAwareDaoBaseOjb implements KemidHistoricalCashDao {
    
    public List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger medId) {
        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemids);
        criteria.addEqualTo(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        QueryByCriteria qbc = QueryFactory.newQuery(KemidHistoricalCash.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID);
        
        return (List<KemidHistoricalCash>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao#getHistoricalCashRecords(java.util.List, org.kuali.rice.kns.util.KualiInteger, org.kuali.rice.kns.util.KualiInteger)
     */
    public List<KemidHistoricalCash> getHistoricalCashRecords(List<String> kemids, KualiInteger beginningMed, KualiInteger endingMed) {
        Criteria criteria = new Criteria();
        Collection<KualiInteger> monthEndIds = new ArrayList();
        
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
    
}
