/*
 * Copyright 2008 The Kuali Foundation
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
