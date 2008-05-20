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
package org.kuali.module.effort.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.LookupService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.OutstandingCertificationsByOrganization;
import org.kuali.module.effort.bo.OutstandingCertificationsByReport;

/**
 * Searches for documents that are not approved.
 */
public class OutstandingCertificationsByReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, "!" + KFSConstants.DocumentStatusCodes.APPROVED);
        
        LookupService lookupService = SpringContext.getBean(LookupService.class);
        List<OutstandingCertificationsByOrganization> reportList = new ArrayList<OutstandingCertificationsByOrganization>(lookupService.findCollectionBySearch(OutstandingCertificationsByOrganization.class, fieldValues));
        HashMap<String, HashMap<String, Integer>> reportNumberCountMap = new HashMap<String, HashMap<String, Integer>>();
        
        for (OutstandingCertificationsByOrganization outstandingReportByOrganization : reportList) {
            String reportNumber = outstandingReportByOrganization.getEffortCertificationReportNumber();
            String[] chartOrgArray = outstandingReportByOrganization.getCertificationOrganizations().split(",");
            for (String chartOrg : chartOrgArray) {
                if (reportNumberCountMap.containsKey(reportNumber)) {
                    HashMap<String, Integer> countForReportNumberByCharOrg = reportNumberCountMap.get(reportNumber);
                    if (countForReportNumberByCharOrg.containsKey(chartOrg)) countForReportNumberByCharOrg.put(chartOrg, ( countForReportNumberByCharOrg.get(chartOrg) + 1 ) );
                    else countForReportNumberByCharOrg.put(chartOrg, 1);
                } else {
                    HashMap<String, Integer> countForReportNumberByCharOrg = new HashMap<String, Integer>();
                    countForReportNumberByCharOrg.put(chartOrg, 1);
                    reportNumberCountMap.put(reportNumber, countForReportNumberByCharOrg);
                }
                
            }
        }
        
        ArrayList<OutstandingCertificationsByReport> returnResults = new ArrayList<OutstandingCertificationsByReport>();
        ArrayList<String> reportNumberList = new ArrayList(reportNumberCountMap.keySet());
        
        for (String reportNumber : reportNumberList) {
            HashMap<String, Integer> countForReportNumberByCharOrg = reportNumberCountMap.get(reportNumber);
            ArrayList<String> chartOrgList = new ArrayList<String>(countForReportNumberByCharOrg.keySet());
            for (String chartOrg : chartOrgList) {
                OutstandingCertificationsByReport temp = new OutstandingCertificationsByReport();
                String[] chartAndOrg = chartOrg.split("-");
                
                temp.setEffortCertificationReportNumber(reportNumber);
                temp.setUniversityFiscalYear( Integer.parseInt(fieldValues.get(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME)) );
                temp.setChartOfAccountsCode(chartAndOrg[0]);
                temp.setOrganizationCode(chartAndOrg[1]);
                temp.setOutstandingCertificationCount(countForReportNumberByCharOrg.get(chartOrg));
                returnResults.add(temp);
            }
        }
        
        setBackLocation(fieldValues.get(RiceConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(RiceConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(RiceConstants.REFERENCES_TO_REFRESH));
        
        return new CollectionIncomplete(returnResults, new Long(0));
    }
    
    /**
     * @see org.kuali.core.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> arg0) {
        return getSearchResults(arg0);
    }

    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        
        return "";
    }
   
}
