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
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.LookupService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.DuplicateCertificationsReport;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.OutstandingCertificationsByOrganization;

/**
 * Searches for documents that are not approved.
 */
public class DuplicateCertificationsLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, "!" + KFSConstants.DocumentStatusCodes.APPROVED);
        
        LookupService lookupService = SpringContext.getBean(LookupService.class);
        List<EffortCertificationDocumentBuild> reportList = new ArrayList<EffortCertificationDocumentBuild>(lookupService.findCollectionBySearch(EffortCertificationDocumentBuild.class, fieldValues));
        //ArrayList<DuplicateCertificationsReport> returnResults = new ArrayList<DuplicateCertificationsReport>();
        
        /*for (OutstandingCertificationsByOrganization outstandingReportByOrganization : reportList) {
            
            DuplicateCertificationsReport temp = new DuplicateCertificationsReport();
            
            //TODO: are these the correct field property names
            
            temp.setEffortCertificationReportNumber(outstandingReportByOrganization.getEffortCertificationReportNumber());
            temp.setUniversityFiscalYear(outstandingReportByOrganization.getUniversityFiscalYear());
            temp.setEmplid(outstandingReportByOrganization.getEmplid());
            
            returnResults.add(temp);
        }*/
        
        setBackLocation(fieldValues.get(RiceConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(RiceConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(RiceConstants.REFERENCES_TO_REFRESH));
        
        return findEmployeesWithPayOnMoreThanOneEffortCertificationReport(reportList);
    }
    
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        
        return "";
    }
    
    /**
     * This method is called when user did not enter report number in search criteria. 
     * In this case records should only be displayed if employee appears on more than one effort certification report
     * 
     * @param reportList
     * @return
     */
    private List<DuplicateCertificationsReport> findEmployeesWithPayOnMoreThanOneEffortCertificationReport(List<EffortCertificationDocumentBuild> reportList) {
        ArrayList<DuplicateCertificationsReport> returnResults = new ArrayList<DuplicateCertificationsReport>();
        HashMap<String, List<String>> employeeIdReportNumberMap = new HashMap<String, List<String>>();
        
        //build up map of emplid/ report number list to use later to determine which records should be returned
        for (EffortCertificationDocumentBuild effortCertificationDocumentBuild : reportList) {
            String reportNumber = effortCertificationDocumentBuild.getEffortCertificationReportNumber();
            String emplid = effortCertificationDocumentBuild.getEmplid();
            
            if (employeeIdReportNumberMap.containsKey(emplid)) {
                List<String> reportNumbers = employeeIdReportNumberMap.get(emplid);
                if (!reportNumbers.contains(reportNumber)) {
                    reportNumbers.add(reportNumber);
                    //TODO: !!!REMOVE THIS BEFORE COMMITTING !!!
                    if (employeeIdReportNumberMap.get(emplid).contains(reportNumber)) System.out.println("true!!!");
                    else System.out.println("false!!!");
                }
            } else {
                List<String> reportNumbers = new ArrayList<String>();
                reportNumbers.add(reportNumber);
                employeeIdReportNumberMap.put(emplid, reportNumbers);
            }
        }
        
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
        return returnResults;
    }
    
    /**
     * This method is called when user did not enters report numbers in search criteria. 
     * If the user enters more than one report number the results will include all employees that appear in two or more effort certification report.
     * If the user only enters one report number, 
     * 
     * @param reportNumbers
     * @param reportList
     * @return
     */
    private List<DuplicateCertificationsReport> findEmployeesWithPayOnMoreThanOneEffortCertificationReport(List<String> reportNumbers, List<OutstandingCertificationsByOrganization> reportList) {
        ArrayList<DuplicateCertificationsReport> returnResults = new ArrayList<DuplicateCertificationsReport>();
        
        return returnResults;
    }
}


