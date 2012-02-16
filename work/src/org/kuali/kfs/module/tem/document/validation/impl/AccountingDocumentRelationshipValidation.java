/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;

public class AccountingDocumentRelationshipValidation extends MaintenanceDocumentRuleBase{

    @Override
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {
        boolean success = super.dataDictionaryValidate(document);
        
        AccountingDocumentRelationship adr = (AccountingDocumentRelationship)document.getNewMaintainableObject().getBusinessObject();
        DocumentService documentService = (DocumentService) SpringContext.getBean(DocumentService.class);
        
        if(adr.getDocumentNumber() != null){
            if(!(documentService.documentExists(adr.getDocumentNumber()))){
                success = false;
                GlobalVariables.getMessageMap().putError("document.newMaintainableObject.documentNumber", TemKeyConstants.ERROR_ADR_DOCUMENT_NOT_EXIST, new String[]{"Document", adr.getDocumentNumber()});
            }
        }
        
        if(adr.getRelDocumentNumber() != null){
            if(!(documentService.documentExists(adr.getRelDocumentNumber()))){
                success = false;
                GlobalVariables.getMessageMap().putError("document.newMaintainableObject.relDocumentNumber", TemKeyConstants.ERROR_ADR_DOCUMENT_NOT_EXIST, new String[]{"Related Document", adr.getRelDocumentNumber()});
            }            
        }        
        
        return success;
    }

}
