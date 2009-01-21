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
package org.kuali.kfs.module.ec.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.module.ec.EffortConstants.EffortCertificationEditMode;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;

/**
 * Document Presentation Controller for the Effort Certification document.
 * allowsErrorCorrection property has been set to false in data dictionary entry
 * setHasAmountTotal property has been set to true in data dictionary entry
 */

public class EffortDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    
    @Override
    public boolean canCancel(Document document) {
        return false ;
    }
    
    @Override
    public boolean canSave(Document document) {
        return false ;
    }
    
    @Override
    public boolean canCopy(Document document) {
            return false;
    }
    
    @Override
    public boolean canBlanketApprove(Document document) {
        boolean canBlanketApproveValue = super.canBlanketApprove(document);

        boolean initiated = document.getDocumentHeader().getWorkflowDocument().stateIsInitiated();
        if (initiated) {         
            return false;
        }
        
        return canBlanketApproveValue ;
    }
 
    @Override
    public boolean canDisapprove(Document document) {
        boolean canDisapproveValue = super.canDisapprove(document);
        
        boolean initiated = document.getDocumentHeader().getWorkflowDocument().stateIsEnroute();
        if (initiated) {         
            return false;
        }
        
        return canDisapproveValue;
    }
}