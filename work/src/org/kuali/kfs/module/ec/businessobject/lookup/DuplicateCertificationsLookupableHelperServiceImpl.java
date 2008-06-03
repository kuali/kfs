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

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.LookupService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.DuplicateCertificationsReport;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Searches for documents that are not approved.
 */
public class DuplicateCertificationsLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LookupService lookupService = SpringContext.getBean(LookupService.class);
        List<EffortCertificationDocumentBuild> reportList = new ArrayList<EffortCertificationDocumentBuild>(lookupService.findCollectionBySearch(EffortCertificationDocumentBuild.class, fieldValues));
        
        setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
        
        return findEmployeesWithPayOnMoreThanOneEffortCertificationReport(reportList);
    }
    
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        
        return "";
    }
    
    /**
     * Returns the records for employees who appear on more than one report.
     * 
     * @param reportList
     * @return
     */
    private List<DuplicateCertificationsReport> findEmployeesWithPayOnMoreThanOneEffortCertificationReport(List<EffortCertificationDocumentBuild> reportList) {
        ArrayList<DuplicateCertificationsReport> returnResults = new ArrayList<DuplicateCertificationsReport>();
        HashMap<String, List<String>> employeeIdReportNumberMap = findDuplicatesMap(reportList);
        
        //return only records for employees that appear on more than one report
        for (EffortCertificationDocumentBuild effortCertificationDocumentBuild : reportList) {
            String reportNumber = effortCertificationDocumentBuild.getEffortCertificationReportNumber();
            String emplid = effortCertificationDocumentBuild.getEmplid();
            
            if ( employeeIdReportNumberMap.get(emplid).size() >1 ) {
                DuplicateCertificationsReport temp = new DuplicateCertificationsReport();
                
                temp.setEffortCertificationReportNumber(effortCertificationDocumentBuild.getEffortCertificationReportNumber());
                temp.setUniversityFiscalYear(effortCertificationDocumentBuild.getUniversityFiscalYear());
                temp.setEmplid(effortCertificationDocumentBuild.getEmplid());
                
                returnResults.add(temp);
            }
            
        }
        
        return new CollectionIncomplete(returnResults, new Long(0));
    }
    
    /**
     * Returns a map of emplids and the effort certification report numbers they are associated with. To be used to determine which employees appear in more than one report.
     * 
     * @param reportList
     * @return
     */
    private HashMap<String, List<String>> findDuplicatesMap(List<EffortCertificationDocumentBuild> reportList) {
        HashMap<String, List<String>> employeeIdReportNumberMap = new HashMap<String, List<String>>();
        
        //build up map of emplid/ report number list 
        for (EffortCertificationDocumentBuild effortCertificationDocumentBuild : reportList) {
            String reportNumber = effortCertificationDocumentBuild.getEffortCertificationReportNumber();
            String emplid = effortCertificationDocumentBuild.getEmplid();
            
            if (employeeIdReportNumberMap.containsKey(emplid)) {
                List<String> reportNumbers = employeeIdReportNumberMap.get(emplid);
                if (!reportNumbers.contains(reportNumber)) {
                    reportNumbers.add(reportNumber);
                }
            } else {
                List<String> reportNumbers = new ArrayList<String>();
                reportNumbers.add(reportNumber);
                employeeIdReportNumberMap.put(emplid, reportNumbers);
            }
        }
        
        return employeeIdReportNumberMap;
    }
}


