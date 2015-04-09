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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.krad.document.Document;
/**
 * Business PreRules applicable to Purchasing documents
 */
public class RequisitionDocumentPreRules extends PurchasingDocumentPreRulesBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doPrompts(org.kuali.rice.krad.document.Document)
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
