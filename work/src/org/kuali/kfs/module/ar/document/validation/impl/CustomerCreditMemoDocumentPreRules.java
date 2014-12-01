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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

public class CustomerCreditMemoDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = conditionallyAskQuestion(document);
        return preRulesOK;
    }
    
    /**
     * This method checks if there is at least one discount applied to the invoice and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    protected boolean conditionallyAskQuestion(Document document) {
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        boolean shouldAskQuestion = customerCreditMemoDocument.getInvoice().hasAtLeastOneDiscount();
        if (shouldAskQuestion) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.WARNING_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVOICE_HAS_DISCOUNT);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(ArConstants.CustomerCreditMemoConstants.GENERATE_CUSTOMER_CREDIT_MEMO_DOCUMENT_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return true;
    }

}
