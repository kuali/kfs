/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;

/**
 * The default implementation of AccountingLineAuthorizer
 */
public class FinancialTransactionalDocumentAccountingLineAuthorizerBase extends AccountingLineAuthorizerBase {
    
    /**
     * adds refresh method to the action map.
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionMap(org.kuali.kfs.sys.document.web.AccountingLineRenderingContext, java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
    
        Map<String, AccountingLineViewAction> actionMap = super.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);

        if (accountingLineIndex != null) {
            AccountingLineViewAction refreshAction = this.getRefreshAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KFSConstants.RETURN_METHOD_TO_CALL, refreshAction);
        }
        
        return actionMap;
    }
    
    /**
     * constructs a refresh action image and action
     * 
     * @param accountingLine
     * @param accountingLinePropertyName
     * @param accountingLineIndex
     * @param groupTitle
     * @return
     */
    protected AccountingLineViewAction getRefreshAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getRefreshLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_REFRESH_ACTION_LABEL, groupTitle, accountingLineIndex + 1);

        String actionImageName = getRiceImagePath() + "tinybutton-refresh.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }
    
    /**
     * constructs a refresh line method
     * 
     * @param accountingLine
     * @param accountingLineProperty
     * @param accountingLineIndex
     * @return
     */
    protected String getRefreshLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return "refresh.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }
}
