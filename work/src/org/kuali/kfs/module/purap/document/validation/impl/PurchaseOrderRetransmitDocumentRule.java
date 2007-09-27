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
package org.kuali.module.purap.rules;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * This class is purposely not extending PurchaseOrderDocumentRule becuase it does not need to since 
 * it does not allow the PO to be edited nor should it create GL entries.
 */
public class PurchaseOrderRetransmitDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(porDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(porDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) approveEvent.getDocument();
        return isValid;
    }

    private boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;

        // Check that the PO is not null.
        if (ObjectUtils.isNull(document)) {
            throw new ValidationException("Purchase Order Retransmit document was null on validation.");
        }
        else {
            // TODO: Get this from Business Rules.           
            // Check the PO status.
            /*  TODO: Is it really supposed to fail when the status is CLOSE ?
            if (StringUtils.equalsIgnoreCase(document.getStatusCode(), PurchaseOrderStatuses.CLOSED)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.ERROR_PURCHASE_ORDER_STATUS_INCORRECT, PurchaseOrderStatuses.CLOSED);
            }
            */
            // Check that the user is in purchasing workgroup.
            String initiatorNetworkId = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            UniversalUserService uus = SpringContext.getBean(UniversalUserService.class);
            UniversalUser user = null;
            try {
                user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
                String purchasingGroup = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
                if (!uus.isMember(user, purchasingGroup)) {
                    valid = false;
                }
            }
            catch (UserNotFoundException ue) {
                valid = false;
            }
        }

        return valid;
    }
}
