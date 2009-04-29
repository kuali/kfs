/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Override of KualiLookupableHelperServiceImpl to prevent the editing and copying of Cash Drawers.  Also to
 * keep the hobbitses away from my precious.
 */
public class CashDrawerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl implements LookupableHelperService {
    private CashReceiptService cashReceiptService;
    private CashDrawerService cashDrawerService;

    /**
     * Return an empty list - you can't edit or copy cash drawers.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject) && isEditOfCashDrawerAuthorized((CashDrawer)businessObject) && ((CashDrawer)businessObject).isClosed()) {
            htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }

    /**
     * 
     * @param cashDrawer
     * @return
     */
    protected boolean isEditOfCashDrawerAuthorized(CashDrawer cashDrawer) {
        final FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer("CDS");
        final boolean isAuthorized = documentAuthorizer.isAuthorized(cashDrawer, "KFS-FP", "Initiate Document", GlobalVariables.getUserSession().getPerson().getPrincipalId());
        
        return isAuthorized;

    }

    /**
     * Overridden to see if the current user already has a cash drawer created associated with their campus - if 
     * there is a cash drawer already, then no new or copy is allowed
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        final String currentUserCampus = getCashReceiptService().getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getPerson());
        final CashDrawer cashDrawer = getCashDrawerService().getByCampusCode(currentUserCampus);
        
        if (cashDrawer != null) return false;
        return super.allowsMaintenanceNewOrCopyAction();
    }

    /**
     * Gets the cashDrawerService attribute. 
     * @return Returns the cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }

    /**
     * Sets the cashDrawerService attribute value.
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    /**
     * Gets the cashReceiptService attribute. 
     * @return Returns the cashReceiptService.
     */
    public CashReceiptService getCashReceiptService() {
        return cashReceiptService;
    }

    /**
     * Sets the cashReceiptService attribute value.
     * @param cashReceiptService The cashReceiptService to set.
     */
    public void setCashReceiptService(CashReceiptService cashReceiptService) {
        this.cashReceiptService = cashReceiptService;
    }

}
