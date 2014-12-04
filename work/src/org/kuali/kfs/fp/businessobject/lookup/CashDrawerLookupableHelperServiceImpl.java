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
package org.kuali.kfs.fp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Override of KualiLookupableHelperServiceImpl to prevent the editing and copying of Cash Drawers.  Also to
 * keep the hobbitses away from my precious.
 */
public class CashDrawerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected CashReceiptService cashReceiptService;
    protected CashDrawerService cashDrawerService;

    /**
     * Return an empty list - you can't edit or copy cash drawers.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject) && isEditOfCashDrawerAuthorized((CashDrawer)businessObject) && ((CashDrawer)businessObject).isClosed()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }

    /**
     * 
     * @param cashDrawer
     * @return
     */
    protected boolean isEditOfCashDrawerAuthorized(CashDrawer cashDrawer) {
        String cashDrawerDocType = KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentTypeName(CashDrawer.class);
        DocumentAuthorizer documentAuthorizer = KRADServiceLocatorWeb.getDocumentDictionaryService().getDocumentAuthorizer(cashDrawerDocType);
        return documentAuthorizer.canInitiate(cashDrawerDocType, GlobalVariables.getUserSession().getPerson());
    }

    /**
     * Overridden to see if the current user already has a cash drawer created associated with their campus - if 
     * there is a cash drawer already, then no new or copy is allowed
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        final String currentUserCampus = cashReceiptService.getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getPerson());
        final CashDrawer cashDrawer = cashDrawerService.getByCampusCode(currentUserCampus);
        
        if (cashDrawer != null) return false;
        return super.allowsMaintenanceNewOrCopyAction();
    }

    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    public void setCashReceiptService(CashReceiptService cashReceiptService) {
        this.cashReceiptService = cashReceiptService;
    }

}
