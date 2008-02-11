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
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;

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
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        return super.doRules(document);
    }

    /**
     * @see org.kuali.module.purap.rules.AccountsPayableDocumentPreRulesBase#getDocumentName()
     */
    @Override
    public String getDocumentName() {
        return "Credit Memo";
    }
    
    /** 
     * @see org.kuali.module.purap.rules.AccountsPayableDocumentPreRulesBase#createInvoiceNoMatchQuestionText(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    @Override
    public String createInvoiceNoMatchQuestionText(AccountsPayableDocument accountsPayableDocument){
        
        String questionText = super.createInvoiceNoMatchQuestionText(accountsPayableDocument);                
        
        CurrencyFormatter cf = new CurrencyFormatter();
        CreditMemoDocument cm = (CreditMemoDocument) accountsPayableDocument;
        StringBuffer questionTextBuffer = new StringBuffer("");
        questionTextBuffer.append(questionText);
        questionTextBuffer.append( "<style type=\"text/css\"> table.questionTable {border-collapse: collapse;} td.leftTd { border-bottom:1px solid #000000; border-right:1px solid #000000; padding:3px; width:300px; } td.rightTd { border-bottom:1px solid #000000; border-left:1px solid #000000; padding:3px; width:300px; } </style>" );
                    
        questionTextBuffer.append("<br/><br/>Summary Detail Below:<br/><br/><table class=\"questionTable\" align=\"center\">");
        questionTextBuffer.append("<tr><td class=\"leftTd\">Credit Memo Amount entered on start screen:</td><td class=\"rightTd\">" + (String)cf.format(cm.getInitialAmount()) + "</td></tr>");
        questionTextBuffer.append("<tr><td class=\"leftTd\">Total credit processed prior to restocking fee:</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandTotalExcludingRestockingFee()) + "</td></tr>");
        questionTextBuffer.append("<tr><td class=\"leftTd\">Grand Total:</td><td class=\"rightTd\">" + (String)cf.format(cm.getGrandTotal()) + "</td></tr></table>");

        return questionTextBuffer.toString();
        
    }
    
}
