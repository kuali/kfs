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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class DisbursementVoucherAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    private static Log LOG = LogFactory.getLog(DisbursementVoucherAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getEditableBlocksInReadOnlyLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person)
     */
    /*@Override
    public Set<String> getEditableBlocksInReadOnlyLine(AccountingDocument accountingDocument, AccountingLine accountingLine, Person currentUser) {
        Set<String> editableFields = super.getEditableBlocksInReadOnlyLine(accountingDocument, accountingLine, currentUser);

        DocumentAuthorizationService documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class);
        DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) documentAuthorizer.getDocumentAuthorizer(accountingDocument);
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();

        // only check special expense edits if we are in special routing
        if (!workflowDocument.stateIsEnroute() || !workflowDocument.isApprovalRequested() || !dvAuthorizer.isSpecialRouting(accountingDocument, currentUser)) {
            return editableFields;
        }

        DisbursementVoucherWorkGroupService workGroupService = SpringContext.getBean(DisbursementVoucherWorkGroupService.class);

        boolean isObjectCodeEditable = workGroupService.isUserInDvAdminGroup(currentUser);
        isObjectCodeEditable |= workGroupService.isUserInTravelGroup(currentUser);
        isObjectCodeEditable &= SpringContext.getBean(ParameterService.class).getIndicatorParameter(DisbursementVoucherDocument.class, DisbursementVoucherConstants.ALLOW_OBJECT_CODE_EDITS);
        if (isObjectCodeEditable) {
            editableFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }

        boolean isAmountEditable = workGroupService.isUserInTaxGroup(currentUser);
        isAmountEditable |= workGroupService.isUserInFRNGroup(currentUser);
        isAmountEditable |= workGroupService.isUserInWireGroup(currentUser);
        isAmountEditable |= workGroupService.isUserInTravelGroup(currentUser);
        if (isAmountEditable) {
            editableFields.add(KFSPropertyConstants.AMOUNT);
        }

        return editableFields;
    } */
}

