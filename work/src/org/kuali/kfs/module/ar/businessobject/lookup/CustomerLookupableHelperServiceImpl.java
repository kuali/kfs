/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames){
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        htmlDataList.add(getCustomerOpenItemReportUrl(businessObject));
        return htmlDataList;
    }

    /**
     *
     * This method...
     * @param bo
     * @return
     */
    protected AnchorHtmlData getCustomerOpenItemReportUrl(BusinessObject bo) {
        Customer customer = (Customer) bo;

        Properties params = new Properties();
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CustomerOpenItemReportDetail.class.getName());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, StringUtils.EMPTY);
        params.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, ArConstants.CUSTOMER_OPEN_ITEM_REPORT_LOOKUPABLE_IMPL);
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customer.getCustomerNumber());
        params.put(KFSConstants.CustomerOpenItemReport.REPORT_NAME, KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
        params.put(ArPropertyConstants.CustomerFields.CUSTOMER_NAME, customer.getCustomerName());
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");

        String href = UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)+ "/" + ArConstants.UrlActions.CUSTOMER_OPEN_ITEM_REPORT_LOOKUP, params);

        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.CustomerConstants.ACTIONS_REPORT);
    }

    /**
     * This method was overridden to set force the actions to show up even if the user does not have permission to initiate a CUS document.
     */

    @Override
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection<ResultRow> resultTable, boolean bounded) {
        boolean isAuthorized = KimApiServiceLocator.getPermissionService().isAuthorized(GlobalVariables.getUserSession().getPerson().getPrincipalId(), ArConstants.AR_NAMESPACE_CODE, ArConstants.PermissionNames.REPORT, new HashMap<String,String>());
        if (isAuthorized) {
            lookupForm.setSuppressActions(false);
        }
        return super.performLookup(lookupForm, resultTable, bounded);
    }
}

