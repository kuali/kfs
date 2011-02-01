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
package org.kuali.kfs.module.endow.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.report.TrialBalanceReport;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

public class TrialBalanceReportServiceImpl implements TrialBalanceReportService {
       
    protected BusinessObjectService businessObjectService;
    
    public List<TrialBalanceReport> getTrialBalanceReports(List<String> kemids) {
        
        List<TrialBalanceReport> trialBalanceReportList = new ArrayList<TrialBalanceReport>();
        for (String kemid : kemids) {
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(EndowPropertyConstants.KEMID, kemid);
            KEMID kemidObj = (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, primaryKeys);
            if (kemidObj != null) {
                TrialBalanceReport trialBalanceReport = new TrialBalanceReport(); 
                trialBalanceReport.setKemid(kemidObj.getKemid());
                trialBalanceReport.setKemidName(kemidObj.getShortTitle());
                
                // for test
                trialBalanceReport.setInocmeCashBalance(new KualiDecimal(1000000000000000000.01));
                trialBalanceReport.setPrincipalcashBalance(new KualiDecimal(2000000000000000000.02));
                trialBalanceReport.setKemidTotalMarketValue(new KualiDecimal(3000000000000000000.03));
                trialBalanceReport.setAvailableExpendableFunds(new KualiDecimal(4000000000000000000.04));
                trialBalanceReport.setFyRemainderEstimatedIncome(new KualiDecimal(5000000000000000000.05));
                
                trialBalanceReportList.add(trialBalanceReport);
            }
        }
        
        return trialBalanceReportList;
    }

    /**
     * 
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    
}
