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
import org.kuali.kfs.module.endow.businessobject.KemidReportGroup;
import org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class KemidReportGroupDaoOjb extends PlatformAwareDaoBaseOjb implements KemidReportGroupDao {

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao#getKemidsByAttribute(java.lang.String, java.util.List)
     */
    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
            } else {
                c.addEqualTo(attributeName, value.trim());
            } 
            criteria.addOrCriteria(c);
        }
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidReportGroup.class, criteria, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});       
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        //return (List<String>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao#getAttributeValues(java.lang.String, java.util.List)
     */
    public List<String> getAttributeValues(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria.addOrCriteria(c);
        }        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidReportGroup.class, criteria, true);
        query.setAttributes(new String[] {attributeName});
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        //return (List<String>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        List<String> attributeValues = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            attributeValues.add(data[0].toString());
        }
        
        return attributeValues;
    }
}
