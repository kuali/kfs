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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;
import org.kuali.kfs.module.purap.document.dataaccess.ThresholdDao;
import org.kuali.kfs.module.purap.util.ThresholdField;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ThresholdDaoOjb extends PlatformAwareDaoBaseOjb implements ThresholdDao {

    private static Logger LOG = Logger.getLogger(ThresholdDaoOjb.class);
    
    public Collection<ReceivingThreshold> findByChart(String chartCode) {
        Map criteriaFields = new HashMap(1);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndFund(String chartCode, 
                                                    String fund) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.ACCOUNT_TYPE_CODE,fund);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndSubFund(String chartCode, 
                                                       String subFund) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.SUBFUND_GROUP_CODE,subFund);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndCommodity(String chartCode, 
                                                         String commodityCode) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.COMMODITY_CODE,commodityCode);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndObjectCode(String chartCode, 
                                                          String objectCode) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndOrg(String chartCode, 
                                                   String org) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.ORGANIZATION_CODE,org);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<ReceivingThreshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier) {
        Map criteriaFields = new HashMap(3);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.VENDOR_HEADER_GENERATED_ID,vendorHeaderGeneratedIdentifier);
        criteriaFields.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID,vendorDetailAssignedIdentifier);
        criteriaFields.put(ThresholdField.ACTIVE,Boolean.TRUE);
        return getThresholdEnum(criteriaFields);
    }
    
    protected Collection<ReceivingThreshold> getThresholdEnum(Map criteriaFields){
        
        if (criteriaFields == null || criteriaFields.size() == 0){
            return null;
        }

        Criteria criteria = new Criteria();
        List<ThresholdField> allFields = ThresholdField.getEnumList();
        for (int i = 0; i < allFields.size(); i++) {
            if (allFields.get(i).isPersistedField()){
                Object criteriaValue = criteriaFields.get(allFields.get(i));
                if (criteriaValue != null){
                    criteria.addEqualTo(allFields.get(i).getName(), criteriaValue);
                }else{
                     criteria.addIsNull(allFields.get(i).getName());
                }
            }
        }
        Query query = new QueryByCriteria(ReceivingThreshold.class, criteria);
        Collection c = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return c;
    }
    
    /**
     * Will return a mock object
     */
    protected ReceivingThreshold returnAMockObject(){
        ReceivingThreshold newOne = new ReceivingThreshold();
        newOne.setThresholdAmount(new KualiDecimal(10.00));
        return newOne;
    }

}
