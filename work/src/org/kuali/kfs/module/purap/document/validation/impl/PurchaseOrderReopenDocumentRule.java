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
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PurchaseOrderService;

public class PurchaseOrderReopenDocumentRule extends TransactionalDocumentRuleBase {

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

        //check that the user is in purchasing workgroup
        String initiatorNetworkId = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        UniversalUserService uus = SpringServiceLocator.getUniversalUserService();
        UniversalUser user = null;
        try {
            user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
            String purchasingGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("PurapAdminGroup", PurapConstants.Workgroups.WORKGROUP_PURCHASING);
            if (!uus.isMember(user, purchasingGroup )) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_USER_NONPURCHASING);
            }
        } catch (UserNotFoundException ue) {
            valid = false;
        }

        //check the PO status
        String poStatus = document.getStatus().getStatusCode();        
        if (!poStatus.equals(PurapConstants.PurchaseOrderStatuses.CLOSED)) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.PURCHASE_ORDER_REOPEN_STATUS);
        }
        
        return valid;
    }


}
