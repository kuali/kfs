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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

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
