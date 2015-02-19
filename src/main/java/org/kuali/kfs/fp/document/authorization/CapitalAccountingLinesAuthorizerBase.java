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
package org.kuali.kfs.fp.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;


/**
 * Authorizer which deals with financial processing documents that have the capital accounting lines tab. It deals with
 * making source and target accounting lines read only and limiting their actions (buttons) when a user is working on capital lines.
 */
public class CapitalAccountingLinesAuthorizerBase extends FinancialProcessingAccountingLineAuthorizer implements CapitalAccountingLinesAuthorizer {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionOnField(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);

        // Check if this is a capital line and capital entries already exist on the doc. If yes, they shouldn't edit this line. Note that there is code to disallow
        // adding capital lines in this scenario as well but that is handled in CapitalAccountingLinesActionBase.insertAccountingLine
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingDocument;
        if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(accountingLine) && caldb.getCapitalAccountingLines().size() > 0) {
            // Don't return false if it's a new line
            if (accountingLine.getSequenceNumber() == null) {
                return true;
            }

            return false;
        }

        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.CapitalAccountingLinesAuthorizer#determineEditPermissionOnFieldBypassCapitalCheck(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public boolean determineEditPermissionOnFieldBypassCapitalCheck(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
    }

    /**
     * @see org.kuali.kfs.fp.document.authorization.FinancialProcessingAccountingLineAuthorizer#getActionMap(org.kuali.kfs.sys.document.web.AccountingLineRenderingContext, java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);

        // If this is a capital line and capital entries already exist on the doc then only allow balance inquiry on this line
        CapitalAccountingLinesDocumentBase caldb = (CapitalAccountingLinesDocumentBase) accountingLineRenderingContext.getAccountingDocument();
        AccountingLine accountingLine = accountingLineRenderingContext.getAccountingLine();
        if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(accountingLine) && caldb.getCapitalAccountingLines().size() > 0) {
            // Also skip if this is a new line, those are handled by the super
            if (accountingLineIndex != null && accountingLineIndex >= 0) {
                Map<String, AccountingLineViewAction> actionMap = new HashMap<String, AccountingLineViewAction>();
                AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
                actionMap.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);

                return actionMap;
            }
        }

        return super.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);
    }

}

