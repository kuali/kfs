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
package org.kuali.kfs.module.purap.document.service;

import java.util.Collection;

import org.kuali.kfs.module.purap.businessobject.ReceivingThreshold;

public interface ThresholdService {

    public Collection<ReceivingThreshold> findByChart(String chartCode);
    
    public Collection<ReceivingThreshold> findByChartAndFund(String chartCode,String fund);
    
    public Collection<ReceivingThreshold> findByChartAndSubFund(String chartCode,String subFund);
    
    public Collection<ReceivingThreshold> findByChartAndCommodity(String chartCode,String commodity);
    
    public Collection<ReceivingThreshold> findByChartAndObjectCode(String chartCode,String objectCode);
    
    public Collection<ReceivingThreshold> findByChartAndOrg(String chartCode,String org);
    
    public Collection<ReceivingThreshold> findByChartAndVendor(String chartCode, 
                                                      String vendorHeaderGeneratedIdentifier,
                                                      String vendorDetailAssignedIdentifier);
}
