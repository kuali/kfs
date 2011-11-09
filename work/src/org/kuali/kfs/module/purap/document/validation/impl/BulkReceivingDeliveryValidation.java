/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

public class BulkReceivingDeliveryValidation extends GenericValidation {

    PostalCodeValidationService postalCodeValidationService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)event.getDocument();        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.BULK_RECEIVING_DELIVERY_TAB_ERRORS);        
        valid = postalCodeValidationService.validateAddress(bulkReceivingDocument.getDeliveryCountryCode(), bulkReceivingDocument.getDeliveryStateCode(), bulkReceivingDocument.getDeliveryPostalCode(), PurapPropertyConstants.DELIVERY_STATE_CODE, PurapPropertyConstants.DELIVERY_POSTAL_CODE);
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }
    
}
