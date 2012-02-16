/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

public class TravelAgencyAuditLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(bo, pkNames);
        AgencyStagingData agencyStagingData = (AgencyStagingData) bo;
        boolean isTravelManager = isUserTravelManager();
        // For matched records or if user is not travel manager edit and delete link will not be displayed .
        if (agencyStagingData.getMoveToHistoryIndicator()||!isTravelManager) {
            // clear 'edit' and delete links
            anchorHtmlDataList.clear();
        }
        if(isTravelManager)
        anchorHtmlDataList.add(getViewUrl(agencyStagingData));
        return anchorHtmlDataList;
    }

    private boolean isUserTravelManager() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean isTravelManager= SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser);
        return isTravelManager;
    }

    private HtmlData getViewUrl(AgencyStagingData agencyStagingData) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put("id", agencyStagingData.getId().toString());
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AgencyStagingData.class.getName());
        String href = UrlFactory.parameterizeUrl(TemConstants.INQUIRY_URL, parameters);
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KFSConstants.START_METHOD, TemConstants.VIEW);
        anchorHtmlData.setTarget("blank");
        return anchorHtmlData;
    }
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        if(isUserTravelManager())
        lookupForm.setSuppressActions(false);
        else
        lookupForm.setSuppressActions(true);
        return super.performLookup(lookupForm, resultTable, bounded);
    }
    
   
}
