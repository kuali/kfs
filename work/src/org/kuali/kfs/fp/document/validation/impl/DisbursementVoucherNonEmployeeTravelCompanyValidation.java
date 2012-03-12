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
package org.kuali.kfs.fp.document.validation.impl;

import java.text.MessageFormat;
import java.util.List;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class DisbursementVoucherNonEmployeeTravelCompanyValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonEmployeeTravelCompanyValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    public final static String DV_PRE_PAID_EMPLOYEE_EXPENSES_PROPERTY_PATH = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL + "." + KFSPropertyConstants.DV_PRE_PAID_EMPLOYEE_EXPENSES;
    public final static String DV_NON_EMPLOYEE_EXPENSES_PROPERTY_PATH = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_EXPENSES;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        boolean isValid = true;
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherNonEmployeeTravel nonEmployeeTravel = disbursementVoucherDocument.getDvNonEmployeeTravel();

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        // check non employee travel company exists
        int index = 0;
        List<DisbursementVoucherNonEmployeeExpense> expenses = nonEmployeeTravel.getDvNonEmployeeExpenses();
        for (DisbursementVoucherNonEmployeeExpense expense : expenses) {
            TravelCompanyCode travelCompanyCode = retrieveCompany(expense.getDisbVchrExpenseCode(), expense.getDisbVchrExpenseCompanyName());

            if (ObjectUtils.isNull(travelCompanyCode)) {
                String fullPropertyName = this.buildFullPropertyName(DV_NON_EMPLOYEE_EXPENSES_PROPERTY_PATH, index, KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME);
                errors.putErrorWithoutFullErrorPath(fullPropertyName, KFSKeyConstants.ERROR_EXISTENCE, "Company ");
                isValid = false;
            }

            index++;
        }

        // check prepaid expenses company exists
        index = 0;
        List<DisbursementVoucherNonEmployeeExpense> prePaidExpenses = nonEmployeeTravel.getDvPrePaidEmployeeExpenses();
        for (DisbursementVoucherNonEmployeeExpense prePaidExpense : prePaidExpenses) {
            TravelCompanyCode travelCompanyCode = retrieveCompany(prePaidExpense.getDisbVchrExpenseCode(), prePaidExpense.getDisbVchrExpenseCompanyName());

            if (ObjectUtils.isNull(travelCompanyCode)) {
                String fullPropertyName = this.buildFullPropertyName(DV_PRE_PAID_EMPLOYEE_EXPENSES_PROPERTY_PATH, index, KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME);
                errors.putErrorWithoutFullErrorPath(fullPropertyName, KFSKeyConstants.ERROR_EXISTENCE, "Company ");
                isValid = false;
            }

            index++;
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    // build the full name of a document property
    protected String buildFullPropertyName(String propertyPath, int index, String propertyName) {
        String fileNamePattern = "{0}[{1}].{2}";

        return MessageFormat.format(fileNamePattern, propertyPath, index, propertyName);
    }

    // Retrieves the Company object from the company name
    protected TravelCompanyCode retrieveCompany(String companyCode, String companyName) {
        TravelCompanyCode travelCompanyCode = new TravelCompanyCode();
        travelCompanyCode.setCode(companyCode);
        travelCompanyCode.setName(companyName);
        return (TravelCompanyCode) SpringContext.getBean(BusinessObjectService.class).retrieve(travelCompanyCode);
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}
