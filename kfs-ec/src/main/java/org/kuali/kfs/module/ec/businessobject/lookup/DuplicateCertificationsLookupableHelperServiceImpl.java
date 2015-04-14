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
package org.kuali.kfs.module.ec.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Searches for documents that are not approved.
 */
public class DuplicateCertificationsLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LookupService lookupService = SpringContext.getBean(LookupService.class);
        List<EffortCertificationDocumentBuild> reportList = new ArrayList<EffortCertificationDocumentBuild>(lookupService.findCollectionBySearch(EffortCertificationDocumentBuild.class, fieldValues));
        
        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH));
        
        return findEmployeesWithPayOnMoreThanOneEffortCertificationReport(reportList);
    }
    
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        return getEmptyAnchorHtmlData();
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


