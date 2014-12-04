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
