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
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

public class PurchaseOrderPaymentHoldDocumentRule extends TransactionalDocumentRuleBase {

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
        //return true for now, we need to check EPIC to see if there are any rules for this doc
        
        //check that the PO is not null
        if (ObjectUtils.isNull(document)) {
          //ServiceError se = new ServiceError("invalid PO", "purchaseOrder.invalid");
          valid = false;
        }

        //check that the user is in purchasing workgroup
        String initiatorNetworkId = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        UniversalUserService uus = SpringServiceLocator.getUniversalUserService();
        UniversalUser user = null;
        try {
            user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
            String purchasingGroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("PurapAdminGroup", PurapConstants.Workgroups.WORKGROUP_PURCHASING);
            if (!uus.isMember(user, purchasingGroup )) {
                valid = false;
            }
        } catch (UserNotFoundException ue) {
            valid = false;
        }

        //check the PO status
        String poStatus = document.getStatus().getStatusCode();
        if (poStatus.equals(PurapConstants.PurchaseOrderStatuses.CLOSED)) {
            valid = false;
        }
        
        return valid;
    }


}
