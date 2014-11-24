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

import org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByOrganization;
import org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByReport;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
public class OutstandingCertificationsByReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
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
        
        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH));
        
        return new CollectionIncomplete(returnResults, new Long(0));
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> arg0) {
        return getSearchResults(arg0);
    }

    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        return getEmptyAnchorHtmlData();
    }
   
}
