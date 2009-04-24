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
package org.kuali.kfs.module.ld.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ld.LaborAuthorizationConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Presentation Controller for the Effort Certification document. allowsErrorCorrection property has been set to false in
 * data dictionary entry setHasAmountTotal property has been set to true in data dictionary entry
 */

public class LaborExpensesDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if(workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            editModes.add(LaborAuthorizationConstants.ExpenseTransferEditMode.LEDGER_BALANCE_IMPORTING);
        }
        
        return editModes;
    }
    
    
}