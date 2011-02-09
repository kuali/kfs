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
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class KemidBenefittingOrganizationDaoOjb extends PlatformAwareDaoBaseOjb implements KemidBenefittingOrganizationDao {

    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains("*")) {
                c.addLike(attributeName, value.trim().replace('*', '%'));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria2.addOrCriteria(c);
        }        
        criteria1.addAndCriteria(criteria2);
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidBenefittingOrganization.class, criteria1, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }
    
    public List<String> getAttributeValues(String attributeName, List<String> values) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains("*")) {
                c.addLike(attributeName, value.trim().replace('*', '%'));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria2.addOrCriteria(c);
        }        
        criteria1.addAndCriteria(criteria2);
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidBenefittingOrganization.class, criteria1, true);
        query.setAttributes(new String[] {attributeName});

        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> attributeValues = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            attributeValues.add(data[0].toString());
        }
        
        return attributeValues;
        
    }
    
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids) {
        
        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.KEMID, kemids);
        
        QueryByCriteria qbc = QueryFactory.newQuery(KemidBenefittingOrganization.class, criteria, true);
        qbc.addOrderByAscending(EndowPropertyConstants.KEMID);
        
        List<KemidBenefittingOrganization> kemidBenefittingOrganizationList = (List<KemidBenefittingOrganization>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kmbodhs = new ArrayList<KemidsWithMultipleBenefittingOrganizationsDataHolder>();
        for (KemidBenefittingOrganization kemidBenefittingOrganization : kemidBenefittingOrganizationList) {
            KemidsWithMultipleBenefittingOrganizationsDataHolder kmbo = new KemidsWithMultipleBenefittingOrganizationsDataHolder();
            kmbo.setKemid(kemidBenefittingOrganization.getKemid());
            kmbo.setChart(kemidBenefittingOrganization.getBenefittingChartCode());
            kmbo.setOrganization(kemidBenefittingOrganization.getBenefittingOrgCode());
            kmbo.setPercent(kemidBenefittingOrganization.getBenefitPrecent());
            kmbodhs.add(kmbo);
        }
        
        return kmbodhs;
    }
}
