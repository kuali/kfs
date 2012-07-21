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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.LedgerPostingDocumentBase;
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.kfs.sys.document.validation.impl.AccountingRuleEngineRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;

/**
 * A rule that uses the accounting rule engine to perform rule validations.
 */
public class LaborDocumentRuleEngineRuleBase extends AccountingRuleEngineRuleBase implements AccountingRuleEngineRule {
    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean result = super.processCustomSaveDocumentBusinessRules(document);
        
        refreshReferenceObjectsInLaborLedgerPendingEntries(document);
        
        return result;
    }
    
    /**
     * convenience method to take BET or SET and go through its llpes to refresh reference objectgs...
     * @param document
     */
    protected void refreshReferenceObjectsInLaborLedgerPendingEntries(Document document) {
        if (document instanceof LedgerPostingDocumentBase) {
            LaborLedgerPostingDocumentBase laborExpenseDocument = (LaborLedgerPostingDocumentBase) document;
            
            for (LaborLedgerPendingEntry llpe : laborExpenseDocument.getLaborLedgerPendingEntries()) {
                llpe.refreshNonUpdateableReferences();
            }
        }
    }
}
