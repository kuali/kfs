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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
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

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLookupableHelperServiceImpl.class);

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
    private AnchorHtmlData getCustomerOpenItemReportUrl(BusinessObject bo) {
        Customer customer = (Customer) bo;
        String href="../arCustomerOpenItemReportLookup.do" +
                "?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail" +
                "&returnLocation=&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable" +
                "&methodToCall=search&customerNumber="+customer.getCustomerNumber()+
                "&reportName=" + KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME +
                "&customerName=" +customer.getCustomerName()+
                "&reportName=Customer History Report&docFormKey=88888888";
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

