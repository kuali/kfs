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
package org.kuali.module.purap.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.module.purap.bo.Threshold;
import org.kuali.module.purap.dao.ThresholdDao;
import org.kuali.module.purap.service.ThresholdService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ThresholdServiceImpl implements ThresholdService{

    private static Logger LOG = Logger.getLogger(ThresholdServiceImpl.class);

    private ThresholdDao dao;

    public void setThresholdDao(ThresholdDao dao) {
        this.dao = dao;
    }
    
    public Collection<Threshold> findByChart(String chartCode) {
        return dao.findByChart(chartCode);
    }

    public Collection<Threshold> findByChartAndFund(String chartCode, String fund) {
        return dao.findByChartAndFund(chartCode,fund);
    }

    public Collection<Threshold> findByChartAndSubFund(String chartCode, String subFund) {
        return dao.findByChartAndSubFund(chartCode,subFund);
    }

    public Collection<Threshold> findByChartAndCommodity(String chartCode, String commodity) {
        return dao.findByChartAndCommodity(chartCode,commodity);
    }

    public Collection<Threshold> findByChartAndObjectCode(String chartCode, String objectCode) {
        return dao.findByChartAndObjectCode(chartCode,objectCode);
    }

    public Collection<Threshold> findByChartAndOrg(String chartCode, String org) {
        return dao.findByChartAndOrg(chartCode,org);
    }

    public Collection<Threshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier){
        return dao.findByChartAndVendor(chartCode,vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier);
    }

}
