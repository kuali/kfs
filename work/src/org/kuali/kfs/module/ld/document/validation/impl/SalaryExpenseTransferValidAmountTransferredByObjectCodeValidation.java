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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting document's balances by object codes are unchanged
 */
public class SalaryExpenseTransferValidAmountTransferredByObjectCodeValidation extends GenericValidation {
    private Document documentForValidation;

    /**
     * Validates that an accounting document's unbalanced object code balances exist <strong>Expects an accounting document as the
     * first a parameter</strong>
     * 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {        
        SalaryExpenseTransferDocument expenseTransferDocument = (SalaryExpenseTransferDocument) documentForValidation;
        WorkflowDocument workflowDocument = expenseTransferDocument.getDocumentHeader().getWorkflowDocument();
        
        // check if user is allowed to edit the object code.
        if(this.hasEditPermissionOnObjectCode(expenseTransferDocument, workflowDocument)) {
            return true;
        }

        // if approving document, check the object code balances match when document was inititated, else check the balance
        boolean isValid = true;
        if (workflowDocument.isApprovalRequested()) {
            if (!isObjectCodeBalancesUnchanged(expenseTransferDocument)) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_BY_OBJECT_APPROVAL_CHANGE);
                isValid = false;
            }
        }
        else {
            if (!expenseTransferDocument.getUnbalancedObjectCodes().isEmpty()) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
                isValid = false;
            }
        }

        return isValid;
    }

    // check if user is allowed to edit the object code.
    protected boolean hasEditPermissionOnObjectCode(SalaryExpenseTransferDocument expenseTransferDocument, WorkflowDocument workflowDocument) {
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(expenseTransferDocument);
        String templateName = KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name;
        
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, workflowDocument.getDocumentTypeName());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, "targetAccountingLines.financialObjectCode");

        return documentAuthorizer.isAuthorizedByTemplate(expenseTransferDocument, KFSConstants.ParameterNamespaces.KFS, templateName, principalId, additionalPermissionDetails, null);
    }

    /**
     * Checks the current object code balance map of the document against the balances captured before the document was returned for
     * approval.
     * 
     * @param accountingDocument SalaryExpenseTransferDocument to check
     * @return true if the balances have not changed, false if they have
     */
    protected boolean isObjectCodeBalancesUnchanged(AccountingDocument accountingDocument) {
        boolean isUnchanged = true;

        Map<String, KualiDecimal> initiatedObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getApprovalObjectCodeBalances();
        Map<String, KualiDecimal> currentObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getUnbalancedObjectCodes();

        Set<Entry<String, KualiDecimal>> initiatedObjectCodes = initiatedObjectCodeBalances.entrySet();
        Set<Entry<String, KualiDecimal>> currentObjectCodes = currentObjectCodeBalances.entrySet();

        if (initiatedObjectCodes == null) {
            if (currentObjectCodes != null) {
                isUnchanged = false;
            }
        }
        else {
            if (!initiatedObjectCodes.equals(currentObjectCodes)) {
                isUnchanged = false;
            }
        }

        return isUnchanged;
    }

    /**
     * Sets the documentForValidation attribute value.
     * 
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }
}
