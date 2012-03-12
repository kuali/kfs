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

import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

public class BulkReceivingInitScreenFieldValidation extends GenericValidation {

    private DictionaryValidationService dictionaryValidationService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)event.getDocument();
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        dictionaryValidationService.validateAttributeFormat("BulkReceivingDocument","shipmentPackingSlipNumber",bulkReceivingDocument.getShipmentPackingSlipNumber(),KFSKeyConstants.ERROR_INVALID_FORMAT);
        dictionaryValidationService.validateAttributeFormat("BulkReceivingDocument","shipmentBillOfLadingNumber",bulkReceivingDocument.getShipmentBillOfLadingNumber(),KFSKeyConstants.ERROR_INVALID_FORMAT);
        
        return true;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

}
