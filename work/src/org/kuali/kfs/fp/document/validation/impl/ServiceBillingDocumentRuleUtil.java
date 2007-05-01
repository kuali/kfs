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
import org.kuali.core.bo.user.KualiGroup;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.ServiceBillingControl;

/**
 * This class contains static helper methods for ServiceBillingDocumentRule and ServiceBillingDocumentAuthorizer.
 * 
 * 
 */
public class ServiceBillingDocumentRuleUtil {

    /**
     * Checks the account and current user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @param action kind of error messages to generate, if not null
     * @return whether the current user is authorized to use the given account in the SB income section
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, AccountingDocumentRuleBase.AccountingLineAction action) {
        return serviceBillingIncomeAccountIsAccessible(accountingLine, action, GlobalVariables.getUserSession().getUniversalUser());
    }

    /**
     * Checks the account and user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @param action kind of error messages to generate, if not null
     * @param user the user for whom to check accessibility
     * @return whether the given user is authorized to use the given account in the SB income section
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, AccountingDocumentRuleBase.AccountingLineAction action, UniversalUser user) {
        assertThat(accountingLine.isSourceAccountingLine(), accountingLine);
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            // Ignore empty key because hasAccessibleAccountingLines() may not validate beforehand.
            return false;
        }
        ServiceBillingControl control = SpringServiceLocator.getServiceBillingControlService().getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (ObjectUtils.isNull(control)) {
            if (action != null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, noServiceBillingControlErrorKey(action), accountingLine.getAccountNumber());
            }
            return false;
        }

        if (user.isMember( control.getWorkgroupName() )) {
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
     * @param action
     * @return the error key for not having an SB control
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
     * @param action
     * @return the error key for not being a member of the Workgroup of the necessary SB control
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
