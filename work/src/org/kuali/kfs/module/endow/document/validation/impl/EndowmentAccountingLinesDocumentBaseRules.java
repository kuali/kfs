/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument;
import org.kuali.kfs.module.endow.document.validation.AddEndowmentAccountingLineRule;
import org.kuali.kfs.module.endow.document.validation.DeleteEndowmentAccountingLineRule;

public class EndowmentAccountingLinesDocumentBaseRules extends EndowmentTransactionLinesDocumentBaseRules implements AddEndowmentAccountingLineRule<EndowmentAccountingLinesDocument, EndowmentAccountingLine>, DeleteEndowmentAccountingLineRule<EndowmentAccountingLinesDocument, EndowmentAccountingLine> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddEndowmentAccountingLineRule#processAddEndowmentAccountingLineRules(org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine)
     */
    public boolean processAddEndowmentAccountingLineRules(EndowmentAccountingLinesDocument EndowmentAccountingLinesDocument, EndowmentAccountingLine EndowmentAccountingLine) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteEndowmentAccountingLineRule#processDeleteAccountingLineRules(org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine)
     */
    public boolean processDeleteAccountingLineRules(EndowmentAccountingLinesDocument EndowmentAccountingLinesDocument, EndowmentAccountingLine EndowmentAccountingLine) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * Validates that the document has at least one accounting line.
     * 
     * @param document
     * @param isSource
     * @return true if valid, false otherwise
     */
    protected boolean validateAccountingLinesSizeGreaterThanZero(EndowmentAccountingLinesDocument document, boolean isSource) {
        boolean isValid = true;
        if (isSource) {
            isValid &= (document.getSourceAccountingLines().size() > 0);
        }
        else {
            isValid &= (document.getTargetAccountingLines().size() > 0);
        }

        if (!isValid) {
            putFieldError(EndowConstants.ACCOUNTING_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ACCT_LINE_COUNT_INSUFFICIENT);
        }
        return isValid;
    }
}
