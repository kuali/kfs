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
package org.kuali.module.purap.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapService;

/**
 * Performs prompts and other pre business rule checks for the Accounts Payable Document (and its children).
 */
public class AccountsPayableDocumentPreRulesBase extends PreRulesContinuationBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableDocumentPreRulesBase.class);

    public AccountsPayableDocumentPreRulesBase() {
        super();
    }

    /**
     * Asks for an override if the document hasn't reached full entry and the entered amount does not
     * match the total amount of all items.
     * 
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    public boolean doRules(Document document) {

        boolean preRulesOK = true;

        AccountsPayableDocument accountsPayableDocument = (AccountsPayableDocument) document;

        // Ask the nomatch question if the document hasn't been completed.
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(accountsPayableDocument) == false) {
            preRulesOK = confirmInvoiceNoMatchOverride(accountsPayableDocument);
        }
        else if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(accountsPayableDocument)) {
            // if past full document entry complete, then set override to true to skip validation
            accountsPayableDocument.setUnmatchedOverride(true);
        }

        return preRulesOK;
    }

    /**
     * Checks whether the invoice from the initial screen and the document invoice are mismatched. If so, it prompts the
     * user for confirmation to proceed.
     * 
     * @param accountsPayableDocument - document to have its invoice/totals checked
     * @return
     */
    private boolean confirmInvoiceNoMatchOverride(AccountsPayableDocument accountsPayableDocument) {

        // If the values are mismatched, ask for confirmation.
        if (validateInvoiceTotalsAreMismatched(accountsPayableDocument)) {
            
            String questionText = createInvoiceNoMatchQuestionText(accountsPayableDocument);
            
            boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(PurapConstants.AP_OVERRIDE_INVOICE_NOMATCH_QUESTION, questionText);

            // Set a marker to record that this method has been used.
            if (confirmOverride && StringUtils.isBlank(event.getQuestionContext())) {
                event.setQuestionContext(PurapConstants.AP_OVERRIDE_INVOICE_NOMATCH_QUESTION);
                accountsPayableDocument.setUnmatchedOverride(true);
            }

            if (!confirmOverride) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

    /**
     * Creates the text for the invoice no match question being asked of the user.
     * 
     * @param accountsPayableDocument - to be used by overriding method.
     * @return
     */
    public String createInvoiceNoMatchQuestionText(AccountsPayableDocument accountsPayableDocument){

        String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.AP_QUESTION_CONFIRM_INVOICE_MISMATCH); 
        questionText = StringUtils.replace(questionText, "{0}", getDocumentName());
        
        return questionText;        
    }
    
    /**
     * Determines if the amount entered on the init tab is mismatched with the grand total of the document.
     * 
     * @param accountsPayableDocument
     * @return
     */
    private boolean validateInvoiceTotalsAreMismatched(AccountsPayableDocument accountsPayableDocument) {
        boolean mismatched = false;
        String[] excludeArray = { PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE };
        if (accountsPayableDocument.getTotalDollarAmountAllItems(excludeArray).compareTo(accountsPayableDocument.getInitialAmount()) != 0 && !accountsPayableDocument.isUnmatchedOverride()) {
            mismatched = true;
        }

        return mismatched;
    }

    /**
     * Exists to be overriden by the child class and return the name of the document.
     * 
     * @return
     */
    public String getDocumentName() {
        return "";
    }

}
