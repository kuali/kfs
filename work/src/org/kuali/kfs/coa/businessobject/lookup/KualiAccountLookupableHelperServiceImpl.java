/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class overrids the base getActionUrls method
 */
public class KualiAccountLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    ThreadLocal<FinancialSystemUser> currentUser = new ThreadLocal<FinancialSystemUser>();
    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     *
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        StringBuffer actions = new StringBuffer();
        Account theAccount = (Account) businessObject;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        FinancialSystemUser user = currentUser.get();
        if ( user == null ) {
            user = SpringContext.getBean(FinancialSystemUserService.class).convertUniversalUserToFinancialSystemUser( GlobalVariables.getUserSession().getFinancialSystemUser() );
            currentUser.set(user);
        }
        AnchorHtmlData urlDataCopy = getURLData(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames);
        if (theAccount.isActive() || user.isAdministratorUser()) {
            anchorHtmlDataList.add(getURLData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        else {
            urlDataCopy.setPrependDisplayText("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        anchorHtmlDataList.add(urlDataCopy);
        return anchorHtmlDataList;
    }
    /**
     * Overridden to changed the "closed" parameter to an "active" parameter
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
        if (parameters.containsKey("closed")) {
            final String closedValue = parameters.get("closed");
            if ("Y1T".indexOf(closedValue) > -1) {
                parameters.put("active", "N");
            } else if ("N0F".indexOf(closedValue) > -1){
                parameters.put("active", "Y");
            }
            parameters.remove("closed");
        }
        return super.getSearchResults(parameters);
    }
    
    
}
