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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Presentation Controller for Equipment Loan Or Return Documents
 */
public class EquipmentLoanOrReturnDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;
        
        // setup new loan document
        if (equipmentLoanOrReturnDocument.isNewLoan()) {
            editModes.add(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_NEW_LOAN_TAB);
        }
        
        // setup return loan document
        if (equipmentLoanOrReturnDocument.isReturnLoan()) {
            editModes.add(CamsConstants.EquipmentLoanOrReturnEditMode.DISPLAY_RETURN_LOAN_FIELDS_READ_ONLY);
        }
        
        return editModes;
    }
    
    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = (WorkflowDocument) document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
            Set<String> nodeNames = SpringContext.getBean(AssetService.class).getCurrentRouteLevels(workflowDocument);

            if (nodeNames.contains(CamsConstants.RouteLevelNames.BORROWER)) {
                return false;
            }
        }
        return super.canEdit(document);
    }
}
