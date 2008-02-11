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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapService;

/**
 * Business pre rule(s) applicable to Payment Request documents.
 */
public class PaymentRequestDocumentPreRules extends AccountsPayableDocumentPreRulesBase {

    /**
     * Default Constructor
     */
    public PaymentRequestDocumentPreRules() {
        super();
    }

    /**
     * Main hook point to perform rules check.
     * 
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;

        PaymentRequestDocument preq = (PaymentRequestDocument) document;
        if ((!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(preq)) || (StringUtils.equals(preq.getStatusCode(), PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW))) {
            if (!confirmPayDayNotOverThresholdDaysAway(preq)) {
                return false;
            }
        }
        preRulesOK &= super.doRules(document);
        return preRulesOK;
    }

    /**
     * Prompts user to confirm with a Yes or No to a question being asked.
     * 
     * @param questionType - type of question
     * @param messageConstant - key to retrieve message
     * @return - true if overriding, false otherwise
     */
    private boolean askForConfirmation(String questionType, String messageConstant) {

        String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(messageConstant);
        if (questionText.contains("{")) {
            questionText = prepareQuestionText(questionType, questionText);
        }

        boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(questionType, questionText);

        if (!confirmOverride) {
            event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            return false;
        }
        return true;
    }

    /**
     * Creates the actual text of the question, replacing place holders like pay date threshold with an actual constant value.
     * 
     * @param questionType - type of question
     * @param questionText - actual text of question pulled from resource file
     * @return - question text with place holders replaced
     */
    private String prepareQuestionText(String questionType, String questionText) {
        if (StringUtils.equals(questionType, PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION)) {
            questionText = StringUtils.replace(questionText, "{0}", new Integer(PurapConstants.PREQ_PAY_DATE_DAYS_BEFORE_WARNING).toString());
        }
        return questionText;
    }

    /**
     * Validates if the pay date threshold has not been passed, if so confirmation is required by the user to
     * exceed the threshold.
     * 
     * @param preq - payment request document
     * @return - true if threshold has not been surpassed or if user confirmed ok to override, false otherwise
     */
    public boolean confirmPayDayNotOverThresholdDaysAway(PaymentRequestDocument preq) {
        // If the pay date is more than the threshold number of days in the future, ask for confirmation.
        PaymentRequestDocumentRule rule = new PaymentRequestDocumentRule();
        if (!rule.validatePayDateNotOverThresholdDaysAway(preq)) {
            return askForConfirmation(PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION, PurapKeyConstants.MESSAGE_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
        }
        return true;
    }

    /**
     * @see org.kuali.module.purap.rules.AccountsPayableDocumentPreRulesBase#getDocumentName()
     */
    @Override
    public String getDocumentName() {
        return "Payment Request";
    }

    /**
     * @see org.kuali.module.purap.rules.AccountsPayableDocumentPreRulesBase#createInvoiceNoMatchQuestionText(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public String createInvoiceNoMatchQuestionText(AccountsPayableDocument accountsPayableDocument){
                
        String questionText = super.createInvoiceNoMatchQuestionText(accountsPayableDocument);
        
        CurrencyFormatter cf = new CurrencyFormatter();
        PaymentRequestDocument preq = (PaymentRequestDocument) accountsPayableDocument;        
        
        StringBuffer questionTextBuffer = new StringBuffer("");        
        questionTextBuffer.append(questionText);
        questionTextBuffer.append( "<style type=\"text/css\"> table.questionTable {border-collapse: collapse;} td.leftTd { border-bottom:1px solid #000000; border-right:1px solid #000000; padding:3px; width:300px; } td.rightTd { border-bottom:1px solid #000000; border-left:1px solid #000000; padding:3px; width:300px; } </style>" );
                    
        questionTextBuffer.append("<br/><br/>Summary Detail Below:<br/><br/><table class=\"questionTable\" align=\"center\">");
        questionTextBuffer.append("<tr><td class=\"leftTd\">Vendor Invoice Amount entered on start screen:</td><td class=\"rightTd\">" + (String)cf.format(preq.getInitialAmount()) + "</td></tr>");
        questionTextBuffer.append("<tr><td class=\"leftTd\">Invoice Total Prior to Additional Charges:</td><td class=\"rightTd\">" + (String)cf.format(preq.getTotalDollarAmountAboveLineItems()) + "</td></tr>");

        //only add this line if payment request has a discount
        if( preq.isDiscount() ){
            questionTextBuffer.append("<tr><td class=\"leftTd\">Total Before Discount:</td><td class=\"rightTd\">" + (String)cf.format(preq.getGrandTotalExcludingDiscount()) + "</td></tr>");
        }
        
        questionTextBuffer.append("<tr><td class=\"leftTd\">Grand Total:</td><td class=\"rightTd\">" + (String)cf.format(preq.getGrandTotal()) + "</td></tr></table>");
                        
        return questionTextBuffer.toString();
        
    }

}
