/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.dao.ojb;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.bo.Threshold;
import org.kuali.module.purap.dao.ThresholdDao;
import org.kuali.module.purap.util.ThresholdField;

public class ThresholdDaoOjb extends PlatformAwareDaoBaseOjb implements ThresholdDao {

    private static Logger LOG = Logger.getLogger(ThresholdDaoOjb.class);
    
    public Collection<Threshold> findByChart(String chartCode) {
        Map criteriaFields = new HashMap(1);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndFund(String chartCode, 
                                                    String fund) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.ACCOUNT_TYPE_CODE,fund);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndSubFund(String chartCode, 
                                                       String subFund) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.SUBFUND_GROUP_CODE,subFund);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndCommodity(String chartCode, 
                                                         String commodityCode) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.COMMODITY_CODE,commodityCode);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndObjectCode(String chartCode, 
                                                          String objectCode) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndOrg(String chartCode, 
                                                   String org) {
        Map criteriaFields = new HashMap(2);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.ORGANIZATION_CODE,org);
        return getThresholdEnum(criteriaFields);
    }

    public Collection<Threshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier) {
        Map criteriaFields = new HashMap(3);
        criteriaFields.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,chartCode);
        criteriaFields.put(ThresholdField.VENDOR_HEADER_GENERATED_ID,vendorHeaderGeneratedIdentifier);
        criteriaFields.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID,vendorDetailAssignedIdentifier);
        return getThresholdEnum(criteriaFields);
    }
    
    private Collection<Threshold> getThresholdEnum(Map criteriaFields){
        
        if (criteriaFields == null || criteriaFields.size() == 0){
            return null;
        }

        Criteria criteria = new Criteria();
        List<ThresholdField> allFields = ThresholdField.getEnumList();
        
        for (int i = 0; i < allFields.size(); i++) {
            Object criteriaValue = criteriaFields.get(allFields.get(i));
            if (criteriaValue != null){
                criteria.addEqualTo(allFields.get(i).getName(), criteriaValue);
            }else{
                criteria.addIsNull(allFields.get(i).getName());
            }
            
        }
        
        Query query = new QueryByCriteria(Threshold.class, criteria);
        Collection c = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return c;
    }
    
    /**
     * Will return a mock object
     */
    private Threshold returnAMockObject(){
        Threshold newOne = new Threshold();
        newOne.setThresholdAmount(new KualiDecimal(10.00));
        return newOne;
    }

}