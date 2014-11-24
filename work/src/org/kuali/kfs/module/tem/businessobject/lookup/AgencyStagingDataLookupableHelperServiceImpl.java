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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

public class AgencyStagingDataLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

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
        return super.allowsMaintenanceNewOrCopyAction();
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
