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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class KemidReportGroupDaoOjb extends PlatformAwareDaoBaseOjb implements KemidReportGroupDao {

    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains("*")) {
                c.addLike(attributeName, value.trim().replace('*', '%'));
            } else {
                c.addEqualTo(attributeName, value.trim());
            } 
            criteria.addOrCriteria(c);
        }
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidReportGroup.class, criteria, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        return (List<String>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    public List<String> getAttributeValues(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains("*")) {
                c.addLike(attributeName, value.trim().replace('*', '%'));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria.addOrCriteria(c);
        }        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidReportGroup.class, criteria, true);
        query.setAttributes(new String[] {attributeName});

        return (List<String>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
