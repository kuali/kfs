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
package org.kuali.kfs.module.cam.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class validates to make sure that at least one payment line is available for the document
 */
public class AssetPaymentAccountingLineCountValidation extends GenericValidation {

    /**
     * Validate accounting line count to ensure minimum of one line
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        AccountingDocument accountingDocument = (AccountingDocument) event.getDocument();
        if (0 == accountingDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getMessageMap().putError(SOURCE_ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES, new String[] { accountingDocument.getSourceAccountingLinesSectionTitle() });
            valid = false;
        }
        else {
            valid = true;
        }
        return valid;
    }
}
