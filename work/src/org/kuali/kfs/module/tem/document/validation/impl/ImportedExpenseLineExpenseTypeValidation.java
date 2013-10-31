/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class ImportedExpenseLineExpenseTypeValidation extends GenericValidation {
    protected ImportedExpense importedExpenseForValidation;
    protected TravelExpenseService travelExpenseService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        success &= checkExpenseTypeExistsForDocument(event.getDocument());

        return success;
    }

    /**
     * Determines if an expense type object code will be found for the given importedExpenseForValidation for this document
     * @param doc the document to check
     * @return true if an expense type object code is found; false otherwise
     */
    protected boolean checkExpenseTypeExistsForDocument(Document doc) {
        boolean success = true;

        final TravelDocument travelDoc = (TravelDocument)doc;
        final String documentTypeName = travelDoc.getDocumentTypeName();
        if (!StringUtils.isBlank(documentTypeName)) {
            final String travelerType = (ObjectUtils.isNull(travelDoc.getTraveler())) ? null : travelDoc.getTraveler().getTravelerTypeCode();
            final ExpenseTypeObjectCode expenseTypeObjectCode = getTravelExpenseService().getExpenseType(getImportedExpenseForValidation().getExpenseTypeCode(), documentTypeName, travelDoc.getTripTypeCode(), travelerType);
            if (expenseTypeObjectCode == null) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(TemPropertyConstants.NEW_IMPORTED_EXPENSE_LINES, TemKeyConstants.ERROR_TEM_IMPORT_EXPENSE_TYPE_NOT_ALLOWED, getImportedExpenseForValidation().getExpenseTypeCode());
                success = false;
            }
        }
        return success;
    }

    public ImportedExpense getImportedExpenseForValidation() {
        return importedExpenseForValidation;
    }

    public void setImportedExpenseForValidation(ImportedExpense importedExpenseForValidation) {
        this.importedExpenseForValidation = importedExpenseForValidation;
    }

    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

}
