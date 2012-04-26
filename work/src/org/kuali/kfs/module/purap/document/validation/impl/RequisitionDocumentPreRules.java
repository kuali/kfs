/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Business PreRules applicable to Purchasing documents
 */
public class RequisitionDocumentPreRules extends PurchasingDocumentPreRulesBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = super.doPrompts(document);
        
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        if (!purapDocument.isUseTaxIndicator()){
            preRulesOK &= checkForTaxRecalculation(purapDocument);
        }
        
        return preRulesOK;
    }

    @Override
    protected boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument) {
        return PurapConstants.CAMSWarningStatuses.REQUISITION_STATUS_WARNING_NO_CAMS_DATA.contains(purapDocument.getApplicationDocumentStatus());
    }
    
}
