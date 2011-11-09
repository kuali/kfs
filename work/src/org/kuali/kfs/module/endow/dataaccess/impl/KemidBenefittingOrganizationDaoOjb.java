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
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

public class KemidBenefittingOrganizationDaoOjb extends PlatformAwareDaoBaseOjb implements KemidBenefittingOrganizationDao {

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getKemidsByAttribute(java.lang.String, java.util.List)
     */
    public List<String> getKemidsByAttribute(String attributeName, List<String> values) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria2.addOrCriteria(c);
        }        
        criteria1.addAndCriteria(criteria2);
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidBenefittingOrganization.class, criteria1, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        // get kemids
        List<String> kemids = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getAttributeValues(java.lang.String, java.util.List)
     */
    public List<String> getAttributeValues(String attributeName, List<String> values) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
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

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getKemidsWithMultipleBenefittingOrganizations(java.util.List)
     */
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids) {
        
        if (kemids == null || kemids.isEmpty()) {
            return null;
        }
        
        List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kmbodhs = new ArrayList<KemidsWithMultipleBenefittingOrganizationsDataHolder>(); 
        
        // get the kemids that are more than one
        List<String> kemidsInMultipl = getIdsForMultipleBenefittingOrganizations(kemids);
        
        if (kemidsInMultipl != null && !kemidsInMultipl.isEmpty()) {
            // get the benefitting organizations and populate the data holder
            List<KemidBenefittingOrganization> kemidBenefittingOrganizationList = getBenefittingOrganizations(kemidsInMultipl);
            for (KemidBenefittingOrganization kemidBenefittingOrganization : kemidBenefittingOrganizationList) {
                KemidsWithMultipleBenefittingOrganizationsDataHolder kmbo = new KemidsWithMultipleBenefittingOrganizationsDataHolder();
                kmbo.setKemid(kemidBenefittingOrganization.getKemid());
                kmbo.setCampus(getCampusCode(kemidBenefittingOrganization.getBenefittingChartCode(), kemidBenefittingOrganization.getBenefittingOrgCode()));  
                kmbo.setChart(kemidBenefittingOrganization.getBenefittingChartCode());
                kmbo.setOrganization(kemidBenefittingOrganization.getBenefittingOrgCode());
                kmbo.setPercent(kemidBenefittingOrganization.getBenefitPrecent());
                kmbodhs.add(kmbo);
            }
        }
        
        return kmbodhs;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getBenefittingOrganizations(java.util.List)
     */
    public List<KemidBenefittingOrganization> getBenefittingOrganizations(List<String> kemids) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String kemid : kemids) {
            Criteria c = new Criteria();
            c.addEqualTo(EndowPropertyConstants.KEMID, kemid.trim());
            criteria2.addOrCriteria(c);
        }
        criteria1.addAndCriteria(criteria2);
        QueryByCriteria queryByCriteria = QueryFactory.newQuery(KemidBenefittingOrganization.class, criteria1);
        queryByCriteria.addOrderByAscending(EndowPropertyConstants.KEMID);
        
        return (List<KemidBenefittingOrganization>) getPersistenceBrokerTemplate().getCollectionByQuery(queryByCriteria);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getIdsForMultipleBenefittingOrganizations(java.util.List)
     */
    public List<String> getIdsForMultipleBenefittingOrganizations(List<String> kemids) {
        
        Criteria subCrit = new Criteria();
        subCrit.addIn(EndowPropertyConstants.KEMID, kemids);        
        ReportQueryByCriteria reportQueryByCriteria = QueryFactory.newReportQuery(KemidBenefittingOrganization.class, subCrit);
        reportQueryByCriteria.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        reportQueryByCriteria.addGroupBy(EndowPropertyConstants.KEMID);  
        Criteria havingCriteria = new Criteria();
        havingCriteria.addGreaterThan("count(" + EndowPropertyConstants.KEMID + ")", new Integer(1));
        reportQueryByCriteria.setHavingCriteria(havingCriteria);
        reportQueryByCriteria.addOrderByAscending(EndowPropertyConstants.KEMID);

        Iterator<?> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQueryByCriteria);          
        List<String> kemidsMultiple = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            kemidsMultiple.add(data[0].toString());
        }
        return kemidsMultiple;        
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getKemidsByCampusCode(java.util.List)
     */
    public List<String> getKemidsByCampusCode(List<String> campusCodes) {
        
        // get organizations with campuseCodes
        String attributeName = EndowPropertyConstants.CA_ORG_CAMPUS_CD;
        
        Criteria criteria1 = new Criteria();
        for (String campusCode : campusCodes) {
            Criteria c = new Criteria();
            if (campusCode.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, campusCode.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
            } else {
                c.addEqualTo(attributeName, campusCode.trim());
            }            
            criteria1.addOrCriteria(c);
        }                
        ReportQueryByCriteria reportQueryByCriteria = new ReportQueryByCriteria(Organization.class, criteria1, true);
        reportQueryByCriteria.setAttributes(new String[] {EndowPropertyConstants.CA_ORG_CHRT_CD, EndowPropertyConstants.CA_ORG_CD});        
        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQueryByCriteria);
        
        // get the matching benefitting organization kemids
        Criteria criteria2 = new Criteria();
        Criteria subCriteria = new Criteria();
        criteria2.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);        
        while (result.hasNext()) {
            Criteria c = new Criteria();
            Object[] data = (Object[]) result.next();
            String chartCode = data[0].toString();
            String organziationCode = data[1].toString();
            c.addEqualTo(EndowPropertyConstants.KEMID_BENE_CHRT_CD, chartCode);
            c.addEqualTo(EndowPropertyConstants.KEMID_BENE_ORG_CD, organziationCode);
            subCriteria.addOrCriteria(c);            
        }
        criteria2.addAndCriteria(subCriteria);
        
        ReportQueryByCriteria query = new ReportQueryByCriteria(KemidBenefittingOrganization.class, criteria2, true);
        query.setAttributes(new String[] {EndowPropertyConstants.KEMID});
        
        Iterator<Object> result2 = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        List<String> kemids = new ArrayList<String>();
        while (result2.hasNext()) {
            Object[] data = (Object[]) result2.next();
            kemids.add(data[0].toString());
        }
        
        return kemids;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getCampusCodes(java.lang.String, java.util.List)
     */
    public List<String> getCampusCodes(String attributeName, List<String> values) {
        
        Criteria criteria1 = new Criteria();
        Criteria criteria2 = new Criteria();
        criteria1.addEqualTo(EndowPropertyConstants.ENDOWCODEBASE_ACTIVE_INDICATOR, true);
        for (String value : values) {
            Criteria c = new Criteria();
            if (value.contains(KFSConstants.WILDCARD_CHARACTER)) {
                c.addLike(attributeName, value.trim().replace(KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN));
            } else {
                c.addEqualTo(attributeName, value.trim());
            }            
            criteria2.addOrCriteria(c);
        }        
        criteria1.addAndCriteria(criteria2);
        ReportQueryByCriteria query = new ReportQueryByCriteria(Organization.class, criteria1, true);
        query.setAttributes(new String[] {attributeName});

        Iterator<Object> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query); 
        
        List<String> attributeValues = new ArrayList<String>();
        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            attributeValues.add(data[0].toString());
        }
        
        return attributeValues;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao#getCampusCode(java.lang.String, java.lang.String)
     */
    public String getCampusCode(String charCode, String organziationCode) {
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.CA_ORG_CHRT_CD, charCode);
        criteria.addEqualTo(EndowPropertyConstants.CA_ORG_CD, organziationCode);
        
        QueryByCriteria qbc = QueryFactory.newQuery(Organization.class, criteria);
        
        Organization organization =  (Organization) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
        
        String campusCode = "";
        if (ObjectUtils.isNotNull(organization)) {
            campusCode = organization.getOrganizationPhysicalCampusCode();
        }
        return campusCode;
    }
}
