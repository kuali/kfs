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
package org.kuali.module.purap.service;

import java.util.Collection;

import org.kuali.module.purap.bo.Threshold;

public interface ThresholdService {

    public Collection<Threshold> findByChart(String chartCode);
    
    public Collection<Threshold> findByChartAndFund(String chartCode,String fund);
    
    public Collection<Threshold> findByChartAndSubFund(String chartCode,String subFund);
    
    public Collection<Threshold> findByChartAndCommodity(String chartCode,String commodity);
    
    public Collection<Threshold> findByChartAndObjectCode(String chartCode,String objectCode);
    
    public Collection<Threshold> findByChartAndOrg(String chartCode,String org);
    
    public Collection<Threshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier);
}
