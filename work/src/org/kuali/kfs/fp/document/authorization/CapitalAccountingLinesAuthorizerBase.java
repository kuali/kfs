/*
 * Copyright 2013 The Kuali Foundation
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

