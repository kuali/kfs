/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.document.validation.impl;

import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;

public class AssetPaymentAccountingLineCountValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        AccountingDocument accountingDocument = (AccountingDocument) event.getDocument();
        if (0 == accountingDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(SOURCE_ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES, new String[] { accountingDocument.getSourceAccountingLinesSectionTitle() });
            valid = false;
        }
        else {
            valid = true;
        }
        return valid;
    }

}
