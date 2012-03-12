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
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.TypeRestrictionCode;
import org.kuali.kfs.module.endow.dataaccess.KemidDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class KemidDaoOjb extends PlatformAwareDaoBaseOjb implements KemidDao {

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidDao#getKemidRecordsByIds(java.util.List, java.lang.String, java.sql.Date)
     */
    public List<KEMID> getKemidRecordsByIds(List<String> kemids, String endowmentOption, String closedIndicator) {
        
        // get valid type restriction codes
        Criteria subCrit = new Criteria();
        if (endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.YES) || endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
            subCrit.addEqualTo(EndowPropertyConstants.TYPE_RESTR_PERM_IND, endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.YES) ? true : false);
        }
        subCrit.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true); 
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(TypeRestrictionCode.class, subCrit, true); 
        subQuery.setAttributes(new String[] {EndowPropertyConstants.ENDOWCODEBASE_CODE});
                
        // set criteria for kemid
        Criteria criteria = new Criteria();
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
        criteria.addIn(EndowPropertyConstants.KEMID_TYP_PRIN_RESTR_CD, subQuery);
        if (closedIndicator.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
            criteria.addEqualTo(EndowPropertyConstants.KEMID_CLOSED_IND, true);
        } else if (closedIndicator.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
            criteria.addEqualTo(EndowPropertyConstants.KEMID_CLOSED_IND, false);
        }
        QueryByCriteria qbc = QueryFactory.newQuery(KEMID.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.KEMID);
        
        return (List<KEMID>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
   
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidDao#getKemidsByAttributeWithEndowmentOption(java.lang.String, java.util.List, java.lang.String, java.lang.String)
     */
    public List<String> getKemidsByAttributeWithEndowmentOption(String attributeName, List<String> values, String endowmentOption, String closedIndicator) {
        
        // get valid type restriction codes
        Criteria subCrit = new Criteria();
        if (endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.YES) || endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
            subCrit.addEqualTo(EndowPropertyConstants.TYPE_RESTR_PERM_IND, endowmentOption.equalsIgnoreCase(KFSConstants.ParameterValues.YES) ? true : false);
        }
        subCrit.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(TypeRestrictionCode.class, subCrit, true); 
        subQuery.setAttributes(new String[] {EndowPropertyConstants.ENDOWCODEBASE_CODE});
        
        // set criteria for kemid
        Criteria criteria = new Criteria();
        if (values != null) {
            for (String value : values) {
                Criteria c = new Criteria();
                if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                    c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
                } else {
                    c.addEqualTo(attributeName, value.trim());
                }            
                criteria.addOrCriteria(c);
            }    
        }
        criteria.addIn(EndowPropertyConstants.KEMID_TYP_PRIN_RESTR_CD, subQuery);
        if (closedIndicator.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
            criteria.addEqualTo(EndowPropertyConstants.KEMID_CLOSED_IND, true);
        } else if (closedIndicator.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
            criteria.addEqualTo(EndowPropertyConstants.KEMID_CLOSED_IND, false);
        }        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KEMID.class, criteria, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidDao#getKemidsByAttribute(java.lang.String, java.util.List)
     */
    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        if (values != null) {
            for (String value : values) {
                Criteria c = new Criteria();
                if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                    c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
                } else {
                    c.addEqualTo(attributeName, value.trim());
                }            
                criteria.addOrCriteria(c);
            }        
        }
        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KEMID.class, criteria, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidDao#getAttributeValues(java.lang.String, java.util.List)
     */
    public List<String> getAttributeValues(String attributeName, List<String> values) {
        
        Criteria criteria = new Criteria();
        if (values != null) {
            for (String value : values) {
                Criteria c = new Criteria();
                if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                    c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
                } else {
                    c.addEqualTo(attributeName, value.trim());
                }            
                criteria.addOrCriteria(c);
            }        
        }
        ReportQueryByCriteria query = new ReportQueryByCriteria(KEMID.class, criteria, true);
        query.setAttributes(new String[] {attributeName});

        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> attributeValues = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            attributeValues.add(data[0].toString());
        }
        
        return attributeValues;
    }
}
