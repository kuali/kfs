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
package org.kuali.kfs.sys.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentAuthorizerBase.class);

    /**
     * Adds settings for KFS transactional-document-specific flags.
     * 
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(Document,
     *      Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        LOG.debug("calling FinancialSystemTransactionalDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        FinancialSystemTransactionalDocument transactionalDocument = (FinancialSystemTransactionalDocument) document;
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (canCopy(workflowDocument.getDocumentType(), user)) {
            flags.setCanErrorCorrect(transactionalDocument.getAllowsErrorCorrection() && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal()));
        }

        // if document implements AmountTotaling interface, then we should display the total
        if (document instanceof AmountTotaling) {
            flags.setHasAmountTotal(true);
        }
        else {
            flags.setHasAmountTotal(false);
        }

        // check bank specification is enabled and the code should be viewable for this document type. set flag accordingly
        boolean bankSpecificationEnabled = SpringContext.getBean(BankService.class).isBankSpecificationEnabled();
        if (bankSpecificationEnabled) {
            String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(document.getClass());
            ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(Bank.class, KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES, documentTypeCode);

            flags.setCanViewBank(evaluator.evaluationSucceeds());
        }

        // check user can edit bank and set flag accordingly
        if (flags.getCanViewBank()) {
            String editBankGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(Bank.class, KFSParameterKeyConstants.BANK_EDITABLE_GROUP);
            KimGroup editBankGroup = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, editBankGroupName);
            if (editBankGroup != null) {
                flags.setCanEditBank(org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), editBankGroup.getGroupId()));
            } else {
                LOG.error("Group given for parameter" + KFSParameterKeyConstants.BANK_EDITABLE_GROUP + " is not valid: " + editBankGroupName);
                throw new RuntimeException("Group given for parameter" + KFSParameterKeyConstants.BANK_EDITABLE_GROUP + " is not valid: " + editBankGroupName);
            }
        }

        return flags;
    }


}

