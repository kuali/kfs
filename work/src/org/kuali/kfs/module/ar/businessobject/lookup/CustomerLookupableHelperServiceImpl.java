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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLookupableHelperServiceImpl.class);

    /***
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {

        FinancialSystemUser user = ValueFinderUtil.getCurrentFinancialSystemUser();
        if (!ARUtil.isUserInArSupervisorGroup(user))
            return super.getEmptyActionUrls();

        /*StringBuffer actions = new StringBuffer();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
            actions.append(getMaintenanceUrl(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        }

        if (allowsMaintenanceNewOrCopyAction()) {
            actions.append("&nbsp;&nbsp;");
            actions.append(getMaintenanceUrl(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL));
        }*/
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(businessObject, pkNames);

        //actions.append("&nbsp;&nbsp;");
        anchorHtmlDataList.add(getCustomerOpenItemReportUrl(businessObject));

        return anchorHtmlDataList;
    }

    private AnchorHtmlData getCustomerOpenItemReportUrl(BusinessObject bo) {

        Customer customer = (Customer) bo;
        String href="../arCustomerOpenItemReportLookup.do" +
                "?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail" +
                "&returnLocation=portal.do&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable" +
                "&methodToCall=search&customerNumber="+customer.getCustomerNumber()+
                "&customerName="+customer.getCustomerName()+
                "&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArConstants.CustomerConstants.ACTIONS_REPORT);
    }
}
