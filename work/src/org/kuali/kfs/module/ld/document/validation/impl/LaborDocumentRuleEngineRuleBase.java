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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocumentBase;
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.kfs.sys.document.validation.impl.AccountingRuleEngineRuleBase;
import org.kuali.rice.krad.document.Document;

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
        if (document instanceof LaborExpenseTransferDocumentBase) {
            LaborLedgerPostingDocumentBase laborExpenseDocument = (LaborLedgerPostingDocumentBase) document;
            
            for (LaborLedgerPendingEntry llpe : laborExpenseDocument.getLaborLedgerPendingEntries()) {
                llpe.refreshNonUpdateableReferences();
            }
        }
    }
}
