/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.document.authorization;

import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.exceptions.InactiveDocumentTypeAuthorizationException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;

/**
 * Document Authorizer for the Budget Adjustment document.
 */
public class BudgetAdjustmentDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * Overrides to call super and then blanketly reset the actions not allowed on the procurment card document.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        TransactionalDocumentActionFlags flags = new TransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        return flags;
    }

    /**
     * Check if base amount can be edited for the posting year, if so export base amount entry mode.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document document, UniversalUser user, List sourceLines, List targetLines) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        Map editModeMap = super.getEditMode(document, user, sourceLines, targetLines);
        if (SpringContext.getBean(FiscalYearFunctionControlService.class).isBaseAmountChangeAllowed(((BudgetAdjustmentDocument) document).getPostingYear())) {
            editModeMap.put(KfsAuthorizationConstants.BudgetAdjustmentEditMode.BASE_AMT_ENTRY, "TRUE");
        }

        return editModeMap;
    }

    /**
     * Checks whether the BA document is active and allowed for any fiscal years.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        List allowedYears = SpringContext.getBean(FiscalYearFunctionControlService.class).getBudgetAdjustmentAllowedYears();

        // if no allowed years found, BA document is not allowed to be initiated
        if (allowedYears == null || allowedYears.isEmpty()) {
            throw new InactiveDocumentTypeAuthorizationException("initiate", "BudgetAdjustmentDocument");
        }

        super.canInitiate(documentTypeName, user);
    }
}