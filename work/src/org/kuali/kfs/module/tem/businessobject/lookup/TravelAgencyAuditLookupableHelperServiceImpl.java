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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.PermissionTemplate;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

public class TravelAgencyAuditLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<AgencyStagingData> agencyData = new ArrayList<AgencyStagingData>();
        if (fieldValues != null) {
            String searchAccount = fieldValues.get(TemConstants.TEM_AGENCY_DATA_SEARCH_ACCOUNT).trim();
            String searchSubAccount = fieldValues.get(TemConstants.TEM_AGENCY_DATA_SEARCH_SUB_ACCOUNT).trim();
            
            fieldValues.remove(TemConstants.TEM_AGENCY_DATA_SEARCH_ACCOUNT);
            fieldValues.remove(TemConstants.TEM_AGENCY_DATA_SEARCH_SUB_ACCOUNT);
            fieldValues.remove(TemConstants.TEM_AGENCY_DATA_SEARCH_CHART_CODE);
            
            agencyData = (List<AgencyStagingData>) super.getSearchResults(fieldValues);
            
            if(StringUtils.isNotBlank(searchAccount) || StringUtils.isNotBlank(searchSubAccount)) {
                //loop through and find any records that have matching account and subaccount
                List<AgencyStagingData> temp = new ArrayList<AgencyStagingData>();
                for(AgencyStagingData agency: agencyData) {
                    for (TripAccountingInformation acctgInfo: agency.getTripAccountingInformation() ) {
                        String acct = acctgInfo.getTripAccountNumber();
                        String subAcct = acctgInfo.getTripSubAccountNumber();
                        if(StringUtils.isNotBlank(searchAccount) && StringUtils.isNotBlank(acct) && acct.equals(searchAccount)) {
                            if(StringUtils.isNotBlank(searchSubAccount) && StringUtils.isNotBlank(subAcct) && subAcct.equals(searchSubAccount)) {
                                temp.add(agency);
                            } else if(searchSubAccount == null) {
                                temp.add(agency);
                            }
                            
                        } else if(StringUtils.isNotBlank(searchSubAccount) && StringUtils.isNotBlank(subAcct) && subAcct.equals(searchSubAccount)) {
                            temp.add(agency);
                        }
                    }
                }
                agencyData = temp;
                
            }
        } else {
            agencyData = (List<AgencyStagingData>) super.getSearchResults(fieldValues); 
        }
        
        return agencyData;
        
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean allows = super.allowsMaintenanceNewOrCopyAction();
        
        Person user = GlobalVariables.getUserSession().getPerson();
        IdentityManagementService idm = KIMServiceLocator.getIdentityManagementService();
        
        //if user does not have the permission, do not allow the to creating new
        allows &= idm.isAuthorizedByTemplateName(user.getPrincipalId(), TemConstants.NAMESPACE, PermissionTemplate.FULL_EDIT_AGENCY_DATA, null, null);
        return allows;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, @SuppressWarnings("rawtypes") List pkNames) {
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(bo, pkNames);
        AgencyStagingData agencyStagingData = (AgencyStagingData) bo;
        boolean isTravelManager = isUserTravelManager();
        // For matched records or if user is not travel manager edit and delete link will not be displayed .
        if (agencyStagingData.getMoveToHistoryIndicator()||!isTravelManager) {
            // clear 'edit' and delete links
            anchorHtmlDataList.clear();
        }
        
        if(isTravelManager) {
            anchorHtmlDataList.add(getViewUrl(agencyStagingData));
        }
        return anchorHtmlDataList;
    }

    /**
     * 
     * @return
     */
    private boolean isUserTravelManager() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean isTravelManager= SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser);
        return isTravelManager;
    }

    /**
     * 
     * @param agencyStagingData
     * @return
     */
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
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        if(isUserTravelManager()) {
            lookupForm.setSuppressActions(false);
        } else {
            lookupForm.setSuppressActions(true);
        }
        
        return super.performLookup(lookupForm, resultTable, bounded);
    }
    
   
}
