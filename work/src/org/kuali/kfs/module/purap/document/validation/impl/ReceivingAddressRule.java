/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

public class ReceivingAddressRule extends MaintenanceDocumentRuleBase {

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateAddress(document);
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateAddress(document);
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean validateAddress(MaintenanceDocument document) {
        ReceivingAddress newReceivingAddress = (ReceivingAddress) document.getNewMaintainableObject().getBusinessObject();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT);
        boolean valid = SpringContext.getBean(PostalCodeValidationService.class).validateAddress(newReceivingAddress.getReceivingCountryCode(), newReceivingAddress.getReceivingStateCode(), newReceivingAddress.getReceivingPostalCode(), PurapPropertyConstants.RECEIVING_ADDRESS_STATE, PurapPropertyConstants.RECEIVING_ADDRESS_POSTAL_CODE);
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

}
