/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants;
import org.apache.commons.lang.StringUtils;

/**
 * This class contains static helper methods for ServiceBillingDocumentRule and ServiceBillingDocumentAuthorizer.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ServiceBillingDocumentRuleUtil {
    
    /**
     * Checks the account and current user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @param action kind of error messages to generate, if not null
     * @return whether the current user is authorized to use the given account in the SB income section
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, TransactionalDocumentRuleBase.AccountingLineAction action) {
        return serviceBillingIncomeAccountIsAccessible(accountingLine, action, GlobalVariables.getUserSession().getKualiUser());
    }

    /**
     * Checks the account and user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @param action kind of error messages to generate, if not null
     * @param user the user for whom to check accessibility
     * @return whether the given user is authorized to use the given account in the SB income section
     */
    public static boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, TransactionalDocumentRuleBase.AccountingLineAction action, KualiUser user) {
        assert accountingLine.isSourceAccountingLine();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            // Ignore empty key because hasAccessibleAccountingLines() may not validate beforehand.
            return false;
        }
        ServiceBillingControl control = SpringServiceLocator.getServiceBillingControlService().getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (ObjectUtils.isNull(control)) {
            if (action != null) {
                GlobalVariables.getErrorMap().putError(PropertyConstants.ACCOUNT_NUMBER, noServiceBillingControlErrorKey(action), accountingLine.getAccountNumber());
            }
            return false;
        }
        try {
            // todo: isMember(String) instead of going through KualiGroupService?
            KualiGroup group = SpringServiceLocator.getKualiGroupService().getByGroupName(control.getWorkgroupName());
            if (user.isMember(group)) {
                return true;
            }
            else {
                if (action != null) {
                    GlobalVariables.getErrorMap().putError(PropertyConstants.ACCOUNT_NUMBER, notControlGroupMemberErrorKey(action), accountingLine.getAccountNumber(), user.getPersonUserIdentifier(), group.getGroupName());
                }
                return false;
            }
        }
        catch (GroupNotFoundException e) {
            TransactionalDocumentRuleBase.LOG.error("invalid workgroup in SB control for " + chartOfAccountsCode + accountNumber, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param action
     * @return the error key for not having an SB control
     */
    private static String noServiceBillingControlErrorKey(TransactionalDocumentRuleBase.AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NO_SB_CTRL;
            case UPDATE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NO_SB_CTRL;
            case DELETE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NO_SB_CTRL;
            default:
                throw new AssertionError(action);
        }
    }

    /**
     * @param action
     * @return the error key for not being a member of the Workgroup of the necessary SB control
     */
    private static String notControlGroupMemberErrorKey(TransactionalDocumentRuleBase.AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NOT_IN_SB_CTRL_GRP;
            case UPDATE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NOT_IN_SB_CTRL_GRP;
            case DELETE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NOT_IN_SB_CTRL_GRP;
            default:
                throw new AssertionError(action);
        }
    }
}
