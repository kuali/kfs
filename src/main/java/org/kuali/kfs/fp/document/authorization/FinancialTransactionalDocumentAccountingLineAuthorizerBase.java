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
