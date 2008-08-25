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
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.module.cam.document.AssetGlobalMaintainableImpl;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLookupableHelperServiceImpl.class);
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {

        FinancialSystemUser user = ValueFinderUtil.getCurrentFinancialSystemUser();
        if (!ARUtil.isUserInArSupervisorGroup(user))
            return "";

        StringBuffer actions = new StringBuffer();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
            actions.append(getMaintenanceUrl(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        }
    
        if (allowsMaintenanceNewOrCopyAction()) {
            actions.append("&nbsp;&nbsp;");
            actions.append(getMaintenanceUrl(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL));
        }
        
        actions.append("&nbsp;&nbsp;");
        actions.append(getCustomerOpenItemReportUrl(businessObject));
        
        return actions.toString();
    }
    
    private String getCustomerOpenItemReportUrl(BusinessObject bo) {

        Customer customer = (Customer) bo;

        StringBuffer anchor = new StringBuffer();
        anchor.append("<a href=\"arCustomerOpenItemReportLookup.do?");
        anchor.append("businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail&");
        anchor.append("returnLocation=portal.do&");
        anchor.append("lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable&");
        anchor.append("methodToCall=search&");
        anchor.append("inquiryFlag=true&");
        anchor.append("customerNumber="); 
        anchor.append(customer.getCustomerNumber());
        anchor.append("&customerName=");
        anchor.append(customer.getCustomerName());
        anchor.append("&docFormKey=88888888");
        anchor.append("\">");
        anchor.append(ArConstants.CustomerConstants.ACTIONS_REPORT);
        anchor.append("</a>");

        return anchor.toString();
    }
}
