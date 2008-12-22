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
package org.kuali.kfs.module.ld.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Data dictionary definition that includes metadata for an accounting document about one of its groups of accounting lines
 * (typically source vs. target, but this should open things up).
 */
public class LaborExpenseTransferAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionMap(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> actionMap = new HashMap<String, AccountingLineViewAction>();

        if (accountingLine.isSourceAccountingLine()) {
            AccountingLineViewAction copyAction = this.getCopyAction(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
            actionMap.put(KFSConstants.COPY_METHOD, copyAction);
        }

        AccountingLineViewAction deleteAction = this.getDeleteAction(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
        actionMap.put(KNSConstants.DELETE_METHOD, deleteAction);

        AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLine, accountingLinePropertyName, accountingLineIndex, groupTitle);
        actionMap.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);

        return actionMap;
    }

    /**
     * construct the copy action for the given accounting line, typically, a new accounting line
     * 
     * @param accountingLine the given accounting line
     * @param accountingLinePropertyName the property name of the given account line, typically, the form name
     * @param accountingLineIndex the index of the given accounting line in its accounting line group
     * @param groupTitle the title of the accounting line group
     * @return the copy action for the given accounting line
     */
    protected AccountingLineViewAction getCopyAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getCopyLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_COPY_ACTION_LABEL, groupTitle, accountingLineIndex + 1);

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String imagesPath = kualiConfigurationService.getPropertyString(KNSConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY);
        String actionImageName = imagesPath + "tinybutton-copy2.gif";

        return new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
    }

    /**
     * Builds the action method name of the method that deletes accounting lines for this group
     * 
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that deletes accounting lines for this group
     */
    protected String getCopyLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return "copyAccountingLine.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }
}
