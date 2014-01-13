/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation which checks a one-sided Travel document source accounting line match the minimum criteria base on
 * 1) Trip type with encumbrance
 * 2) Imported Expenses allowed in Travel Authorization
 */
public class TravelAuthOneSidedRequiredAccountingLinesCountValidation extends GenericValidation {

    /**
     * Validates that the Travel Authorization  has at least the requiredMinimumCount accounting lines
     * in its sourceAccountingLines (yep, it's assumed that one-sided accounting docs *always* use source...isn't that dumb?)
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {

        boolean validated = true;
        TravelAuthorizationDocument travelDocument = (TravelAuthorizationDocument) event.getDocument();

        if (travelDocument.isTripGenerateEncumbrance()){
            validated = validateSourceAccountingLineMeetMinimum(travelDocument);
        }else{
            //accounting line is required for non-enumbrance trip if there are imported expenses in the document
            if (!travelDocument.getImportedExpenses().isEmpty()){
                validated = validateSourceAccountingLineMeetMinimum(travelDocument);
            }
        }
        return validated;
    }

    /**
     * Validate
     *
     * @param travelDocument
     * @return
     */
    private boolean validateSourceAccountingLineMeetMinimum(TravelDocument travelDocument){
        final int REQUIRED_MINIMUM= 1;
        boolean validated = true;

        if (travelDocument.getSourceAccountingLines().size() < REQUIRED_MINIMUM && !travelDocument.getBlanketTravel()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_NO_SINGLE_SECTION_ACCOUNTING_LINES);
            validated = false;
        }
        return validated;
    }

}
