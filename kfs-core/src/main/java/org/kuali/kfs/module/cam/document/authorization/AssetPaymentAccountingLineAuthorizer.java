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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.KRADConstants;

public class AssetPaymentAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return false;
        }

        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#isGroupReadOnly(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) accountingDocument;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return false;
        }

        return super.isGroupEditable(accountingDocument, accountingLineRenderingContexts, currentUser);
    }

    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        boolean isFromCab = false;
        AccountingDocument accountingDocument = accountingLineRenderingContext.getAccountingDocument();
        if (accountingDocument != null && AssetPaymentDocument.class.isAssignableFrom(accountingDocument.getClass())) {
            isFromCab = ((AssetPaymentDocument) accountingDocument).isCapitalAssetBuilderOriginIndicator();
        }
        Map<String, AccountingLineViewAction> actionMap = new HashMap<String, AccountingLineViewAction>();

        if (accountingLineIndex == null || accountingLineIndex < 0) {
            AccountingLineViewAction addAction = this.getAddAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, groupTitle);
            actionMap.put(KFSConstants.INSERT_METHOD, addAction);
        }
        else {
            if (!isFromCab && accountingLineRenderingContext.allowDelete()) {
                AccountingLineViewAction deleteAction = this.getDeleteAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
                actionMap.put(KRADConstants.DELETE_METHOD, deleteAction);
            }

            AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);
        }

        return actionMap;
    }
}
