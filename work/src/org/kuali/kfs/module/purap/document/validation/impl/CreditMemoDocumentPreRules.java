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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.format.CurrencyFormatter;

/**
 * Business rule(s) applicable to the Credit Memo document.
 */
public class CreditMemoDocumentPreRules extends AccountsPayableDocumentPreRulesBase {

    /**
     * Default constructor
     */
    public CreditMemoDocumentPreRules() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        return super.doPrompts(document);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.AccountsPayableDocumentPreRulesBase#getDocumentName()
     */
    @Override
    public String getDocumentName() {
        return "Credit Memo";
    }
    
    /** 
     * @see org.kuali.kfs.module.purap.document.validation.impl.AccountsPayableDocumentPreRulesBase#createInvoiceNoMatchQuestionText(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public String createInvoiceNoMatchQuestionText(AccountsPayableDocument accountsPayableDocument){
        
        String questionText = super.createInvoiceNoMatchQuestionText(accountsPayableDocument);                
        
        CurrencyFormatter cf = new CurrencyFormatter();
        VendorCreditMemoDocument cm = (VendorCreditMemoDocument) accountsPayableDocument;
        StringBuffer questionTextBuffer = new StringBuffer("");
        questionTextBuffer.append(questionText);
        // KULPURAP-3744: remove the html table tags
//        questionTextBuffer.append( "<style type=\"text/css\"> table.questionTable {border-collapse: collapse;} td.leftTd { border-bottom:1px solid #000000; border-right:1px solid #000000; padding:3px; width:300px; } td.rightTd { border-bottom:1px solid #000000; border-left:1px solid #000000; padding:3px; width:300px; } </style>" );
//                    
//        questionTextBuffer.append("<br/><br/>Summary Detail Below:<br/><br/><table class=\"questionTable\" align=\"center\">");
//        questionTextBuffer.append("<tr><td class=\"leftTd\">Credit Memo Amount entered on start screen:</td><td class=\"rightTd\">" + (String)cf.format(cm.getInitialAmount()) + "</td></tr>");
//        questionTextBuffer.append("<tr><td class=\"leftTd\">Total credit processed prior to restocking fee:</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandPreTaxTotalExcludingRestockingFee()) + "</td></tr>");
        
        questionTextBuffer.append("[br][br][b]Summary Detail Below[/b][br][br]");
        questionTextBuffer.append("Credit Memo Amount entered on start screen: ").append((String)cf.format(cm.getInitialAmount())).append("[br]");
        questionTextBuffer.append("Total credit processed prior to restocking fee: ").append((String)cf.format(cm.getLineItemTotal())).append("[br]");
        
        //if sales tax is enabled, show additional summary lines
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);                
        if(salesTaxInd){
//            questionTextBuffer.append("<tr><td class=\"leftTd\">Grand Total Prior to Tax:</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandPreTaxTotal()) + "</td></tr>");
//            questionTextBuffer.append("<tr><td class=\"leftTd\">Grand Total Tax :</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandTaxAmount()) + "</td></tr>");
            questionTextBuffer.append("Grand Total Prior to Tax: ").append((String)cf.format(cm.getGrandPreTaxTotal())).append("[br]");
            questionTextBuffer.append("Grand Total Tax: ").append((String)cf.format(cm.getGrandTaxAmount())).append("[br]");
        }
        
//        questionTextBuffer.append("<tr><td class=\"leftTd\">Grand Total:</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandTotal()) + "</td></tr></table>");
        questionTextBuffer.append("Grand Total: ").append((String)cf.format(cm.getGrandTotal())).append("[br][br]");

        return questionTextBuffer.toString();
        
    }
    
    @Override
    protected boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument) {
        return PurapConstants.CAMSWarningStatuses.CREDIT_MEMO_STATUS_WARNING_NO_CAMS_DATA.contains(purapDocument.getStatusCode());
    }
    
}
