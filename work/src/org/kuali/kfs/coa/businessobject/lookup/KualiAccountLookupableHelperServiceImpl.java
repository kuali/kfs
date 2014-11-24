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
package org.kuali.kfs.coa.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class overrides the base getActionUrls method
 */
public class KualiAccountLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     *
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        StringBuffer actions = new StringBuffer();
        Account theAccount = (Account) businessObject;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        Person user = GlobalVariables.getUserSession().getPerson();
        AnchorHtmlData urlDataCopy = getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames);

        if (theAccount.isActive()) {
            anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        else {
            String principalId = user.getPrincipalId();
            String namespaceCode = KFSConstants.PermissionNames.EDIT_INACTIVE_ACCOUNT.namespace;
            String permissionName = KFSConstants.PermissionNames.EDIT_INACTIVE_ACCOUNT.name;

            IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
            boolean isAuthorized = identityManagementService.hasPermission(principalId, namespaceCode, permissionName);

            if (isAuthorized) {
                anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
            }
            else {
                urlDataCopy.setPrependDisplayText("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
        }
        anchorHtmlDataList.add(urlDataCopy);

        return anchorHtmlDataList;
    }

    /**
     * Overridden to changed the "closed" parameter to an "active" parameter
     *
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
        if (parameters.containsKey(KFSPropertyConstants.CLOSED)) {
            final String closedValue = parameters.get(KFSPropertyConstants.CLOSED);

            if (closedValue != null && closedValue.length() != 0) {
                if ("Y1T".indexOf(closedValue) > -1) {
                    parameters.put(KFSPropertyConstants.ACTIVE, "N");
                }
                else if ("N0F".indexOf(closedValue) > -1) {
                    parameters.put(KFSPropertyConstants.ACTIVE, "Y");
                }
            }

            parameters.remove(KFSPropertyConstants.CLOSED);
        }
        return super.getSearchResults(parameters);
    }

}
