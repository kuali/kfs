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
package org.kuali.module.financial.rules;

import static org.kuali.core.util.AssertionUtils.assertThat;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.module.financial.service.ServiceBillingControlService;

/**
 * This class contains static helper methods for ServiceBillingDocumentRule and ServiceBillingDocumentAuthorizer.
 */
public class ServiceBillingDocumentRuleUtil {

    /**
     * Checks the account and current user against the service billing control table.
     * 
     * @param accountingLine The accounting line from the income section of the service billing document.
     * @param action The type of error messages to generate, if not null.
     * @return Whether the current user is authorized to use the given account in the service billing income section.
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, AccountingDocumentRuleBase.AccountingLineAction action) {
        return serviceBillingIncomeAccountIsAccessible(accountingLine, action, GlobalVariables.getUserSession().getUniversalUser());
    }

    /**
     * Checks the account and user against the service billing control table.
     * 
     * @param accountingLine The accounting line from the income section of the service billing document.
     * @param action The type of error messages to generate, if not null.
     * @param user The user for whom to check accessibility.
     * @return Whether the given user is authorized to use the given account in the service billing income section.
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, AccountingDocumentRuleBase.AccountingLineAction action, UniversalUser user) {
        assertThat(accountingLine.isSourceAccountingLine(), accountingLine);
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            // Ignore empty key because hasAccessibleAccountingLines() may not validate beforehand.
            return false;
        }
        ServiceBillingControl control = SpringContext.getBean(ServiceBillingControlService.class).getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (ObjectUtils.isNull(control)) {
            if (action != null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, noServiceBillingControlErrorKey(action), accountingLine.getAccountNumber());
            }
            return false;
        }

        if (user.isMember(control.getWorkgroupName())) {
            return true;
        }
        else {
            if (action != null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, notControlGroupMemberErrorKey(action), accountingLine.getAccountNumber(), user.getPersonUserIdentifier(), control.getWorkgroupName());
            }
            return false;
        }
    }

    /**
     * This method determines what error key to use when posting the associated error.  The error key is determined 
     * based on the constants passed in, which are values defined in the AccountingDocumentRuleBase.AccountingLineAction.
     * 
     * @param action The constant used to identify which error key to return.
     * @return The error key for not having a service billing control.
     * 
     * @see AccountingDocumentRuleBase.AccountingLineAction
     */
    private static String noServiceBillingControlErrorKey(AccountingDocumentRuleBase.AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NO_SB_CTRL;
            case UPDATE:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NO_SB_CTRL;
            case DELETE:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NO_SB_CTRL;
            default:
                throw new AssertionError(action);
        }
    }

    /**
     * This method determines what error key to use when posting the associated error.  The error key is determined 
     * based on the constants passed in, which are values defined in the AccountingDocumentRuleBase.AccountingLineAction.
     * 
     * @param action The constant used to identify which error key to return.
     * @return The error key for not being a member of the workgroup of the necessary service billing control.
     * 
     * @see AccountingDocumentRuleBase.AccountingLineAction
     */
    private static String notControlGroupMemberErrorKey(AccountingDocumentRuleBase.AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NOT_IN_SB_CTRL_GRP;
            case UPDATE:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NOT_IN_SB_CTRL_GRP;
            case DELETE:
                return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NOT_IN_SB_CTRL_GRP;
            default:
                throw new AssertionError(action);
        }
    }
}
