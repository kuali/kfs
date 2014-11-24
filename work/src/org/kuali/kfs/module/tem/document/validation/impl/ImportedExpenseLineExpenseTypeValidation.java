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
