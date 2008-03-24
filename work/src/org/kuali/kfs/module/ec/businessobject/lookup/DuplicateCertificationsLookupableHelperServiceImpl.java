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
import java.util.Iterator;
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
import org.kuali.module.effort.bo.OutstandingCertificationsByReport;
import org.kuali.module.effort.bo.OutstandingReportsByOrganization;

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
        List<OutstandingReportsByOrganization> reportList = new ArrayList<OutstandingReportsByOrganization>(lookupService.findCollectionBySearch(OutstandingReportsByOrganization.class, fieldValues));
        ArrayList<DuplicateCertificationsReport> returnResults = new ArrayList<DuplicateCertificationsReport>();
        
        for (OutstandingReportsByOrganization outstandingReportByOrganization : reportList) {
            
            DuplicateCertificationsReport temp = new DuplicateCertificationsReport();
            
            //TODO: are these the correct field property names
            
            temp.setEffortCertificationReportNumber(outstandingReportByOrganization.getEffortCertificationReportNumber());
            temp.setUniversityFiscalYear(outstandingReportByOrganization.getUniversityFiscalYear());
            temp.setEmplid(outstandingReportByOrganization.getEmplid());
            
            returnResults.add(temp);
        }
        
        setBackLocation(fieldValues.get(RiceConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(RiceConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(RiceConstants.REFERENCES_TO_REFRESH));
        return returnResults;
    }
    
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        
        return "";
    } 
}
