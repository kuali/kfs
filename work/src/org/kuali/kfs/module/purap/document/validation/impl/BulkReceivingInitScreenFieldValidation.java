/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
