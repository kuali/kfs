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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ld.document.authorization.LaborExpenseDocumentAuthorizerBase;
import org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.DocumentAuthorizationService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewActionDefinition;
import org.kuali.kfs.sys.service.ParameterService;

/**
 * Data dictionary definition that includes metadata for an accounting document about one of its groups of accounting lines (typically source vs. target, but this should open things up).
 */
public class SalaryExpenseTransferAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    
    /**
     * Returns the basic actions - add for new lines, delete and balance inquiry for existing lines
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getActions(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, boolean, java.lang.String)
     */
    @Override
    public List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, Person currentUser, Map editModesForDocument, String groupTitle) {
        List<AccountingLineViewAction> actions = new ArrayList<AccountingLineViewAction>();

        String editMode = this.getEditModeForAccountingLine(accountingDocument, line, (accountingLineIndex == null), currentUser, editModesForDocument);        
        if (AuthorizationConstants.EditMode.FULL_ENTRY.equals(editMode)) {
            String riceImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("kr.externalizable.images.url");
            String kfsImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("externalizable.images.url");
            if (accountingLineIndex == null || accountingLineIndex.intValue() < 0) {
                // actions.add(new AccountingLineViewAction(getAddMethod(line, accountingLineProperty), getAddLabel(groupTitle), riceImagesPath+"tinybutton-add1.gif"));
            } else {
                String groupName = getActionInfixForExtantAccountingLine(line, accountingLineProperty);
                
                if (groupName == KFSConstants.SOURCE) {
                    actions.add(new AccountingLineViewAction(this.getCopyLineMethod(line, accountingLineProperty, accountingLineIndex), this.getCopyLineLabel(accountingLineIndex, groupTitle), kfsImagesPath+"tinybutton-copy2.gif"));
                }
                actions.add(new AccountingLineViewAction(getDeleteLineMethod(line, accountingLineProperty, accountingLineIndex), getDeleteLineLabel(accountingLineIndex, groupTitle), riceImagesPath+"tinybutton-delete1.gif"));
                actions.add(new AccountingLineViewAction(getBalanceInquiryMethod(line, accountingLineProperty, accountingLineIndex), getBalanceInquiryLabel(accountingLineIndex, groupTitle), kfsImagesPath+"tinybutton-balinquiry.gif"));
            }            
        }
        return actions;
    }
 
    /**
     * Builds the action method name of the method that deletes accounting lines for this group
     * @param accountingLine the accounting line an action is being checked for
     * @param accountingLinePropertyName the property name of the accounting line
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that deletes accounting lines for this group
     */
    protected String getCopyLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        return "copyAccountingLine.line"+accountingLineIndex.toString()+".anchoraccounting"+infix+"Anchor";
    }
    /**
     * Builds the label for the buttons that copies accounting lines in this group
     * @param accountingLineIndex the index of the given accounting line within the the group being rendered
     * @param groupTitle title of the group from the data dictionary
     * @return the label for the button that deletes accounting lines in this group
     */
    protected String getCopyLineLabel(Integer accountingLineIndex, String groupTitle) {
        return "Copy "+groupTitle+" Accounting Line "+(accountingLineIndex.intValue()+1);
    }
}
