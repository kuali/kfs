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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.module.endow.dataaccess.KemidDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class KemidDaoOjb extends PlatformAwareDaoBaseOjb implements KemidDao {

    public List<KEMID> getKemidRecordsByIds(List<String> kemids, String endowmentOption, Date currentDate) {
        
        Criteria subCrit = new Criteria();
        if (endowmentOption.equalsIgnoreCase("Y") || endowmentOption.equalsIgnoreCase("N")) {
            subCrit.addEqualTo("permanentIndicator", endowmentOption);
        }
        subCrit.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(TypeRestrictionCode.class, subCrit); 
        subQuery.setAttributes(new String[] {"code"});
        
        
        Criteria criteria = new Criteria();
        if (kemids.isEmpty()) {
            // all records
            criteria.addIn("principalRestrictionCode", subQuery); 
        }
        for (String kemid : kemids) {
            Criteria c = new Criteria();
            if (kemid.contains("*")) {
                c.addLike(EndowPropertyConstants.KEMID, kemid.trim().replace('*', '%'));
            } else {
                c.addEqualTo(EndowPropertyConstants.KEMID, kemid.trim());
            }
            //c.addEqualTo("dateOpened", currentDate);
            c.addIn("principalRestrictionCode", subQuery);            
            criteria.addOrCriteria(c);
        }
        return (List<KEMID>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(KEMID.class, criteria));  
    }
    
    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            c.addEqualTo(attributeName, value.trim());
            criteria.addOrCriteria(c);
        }
        ReportQueryByCriteria query = new ReportQueryByCriteria(KEMID.class, criteria);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<String> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            kemids.add(result.next().toString());
        }
        
        return kemids;
    }

}
