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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherEmployeeInformationValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherEmployeeInformationValidation.class);

    protected AccountingDocument accountingDocumentForValidation;

    public static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if(!payeeDetail.isEmployee() || payeeDetail.isVendor() || !(document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved())) {
            return true;
        }

        String employeeId = payeeDetail.getDisbVchrPayeeIdNumber();
        Person employee = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(employeeId);

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        // check existence of employee
        if (employee == null) { // If employee is not found, report existence error
            String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER);
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, label);
            isValid = false;
        }
        else if(!KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(employee.getEmployeeStatusCode())) {
            // If employee is found, then check that employee is active
            String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER);
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, label);
            isValid = false;
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}

