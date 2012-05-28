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
import org.kuali.kfs.sys.document.validation.AccountingRuleEngineRule;
import org.kuali.kfs.sys.document.validation.impl.AccountingRuleEngineRuleBase;
import org.kuali.rice.krad.document.Document;

/**
 * A rule that uses the accounting rule engine to perform rule validations.
 */
public class LaborDocumentRuleEngineRuleBase extends AccountingRuleEngineRuleBase implements AccountingRuleEngineRule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborDocumentRuleEngineRuleBase.class);
    
    /**
     * Constructs a AccountingRuleEngineRuleBase.java.
     */
    public LaborDocumentRuleEngineRuleBase() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#isDocumentAttributesValid(org.kuali.rice.krad.document.Document, boolean)
     */
    @Override
    public boolean isDocumentAttributesValid(Document document, boolean validateRequired) {

        //refresh LLPE nonupdateable business object references....
        if (document instanceof LaborLedgerPostingDocumentBase) {
            LaborLedgerPostingDocumentBase llpeDocument = (LaborLedgerPostingDocumentBase) document;
            for (LaborLedgerPendingEntry llpe : llpeDocument.getLaborLedgerPendingEntries()) {
                llpe.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            }
        }

        return super.isDocumentAttributesValid(document, validateRequired);
    }
}
