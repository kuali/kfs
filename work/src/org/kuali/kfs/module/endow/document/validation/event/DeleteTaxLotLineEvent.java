/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.event;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public class DeleteTaxLotLineEvent extends KualiDocumentEventBase {
    private EndowmentTransactionLine transactionLine;
    private int transLineIndex;
    private EndowmentTransactionTaxLotLine taxLotLine;
    private int taxLotLineIndex;

    public DeleteTaxLotLineEvent(String errorPathPrefix, EndowmentTaxLotLinesDocument document, EndowmentTransactionTaxLotLine taxLotLine, EndowmentTransactionLine transactionLine, int index, int taxLotLineIndex) {
        super("Deleting Tax Lot Line from document " + getDocumentId(document), errorPathPrefix, document);
        this.document = document;
        this.transactionLine = transactionLine;
        this.transLineIndex = index;
        this.taxLotLine = taxLotLine;
        this.taxLotLineIndex = taxLotLineIndex;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return DeleteTaxLotLineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteTaxLotLineRule<EndowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine, EndowmentTransactionLine, Number, Number>) rule).processDeleteTaxLotLineRules((EndowmentTaxLotLinesDocument) document, taxLotLine, transactionLine, transLineIndex, taxLotLineIndex);
    }

}
