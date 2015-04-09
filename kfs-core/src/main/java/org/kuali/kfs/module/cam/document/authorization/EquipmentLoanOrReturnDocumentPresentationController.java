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
