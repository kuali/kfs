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
package org.kuali.kfs.sys.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Performs bank code validation.
 */
public class BankCodeValidation {
    protected static volatile DataDictionaryService dataDictionaryService;
    protected static volatile BankService bankService;

    /**
     * Performs required, exists, and active validation of bank code. Also validates bank for deposit or disbursement indicator if
     * requested. .
     *
     * @param bankCode value to validate
     * @param bankCodeProperty property to associate errors with
     * @param requireDeposit true if the bank code should support deposits
     * @param requireDisbursement true if the bank code should support disbursements
     * @return true if bank code passes all validations, false if any fail
     */
    public static boolean validate(String bankCode, String bankCodeProperty, boolean requireDeposit, boolean requireDisbursement) {
        String bankCodeLabel = getDataDictionaryService().getAttributeLabel(Bank.class, KFSPropertyConstants.BANK_CODE);

        // if bank specification is not enabled, no need to validate bank code
        if (!getBankService().isBankSpecificationEnabled()) {
            return true;
        }

        // required check
        if (StringUtils.isBlank(bankCode)) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.ERROR_REQUIRED, bankCodeLabel);

            return false;
        }

        Bank bank = getBankService().getByPrimaryId(bankCode);

        if (ObjectUtils.isNull(bank)) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK);
            return false;
        }

        // validate deposit
        if (requireDeposit && !bank.isBankDepositIndicator()) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.Bank.ERROR_DEPOSIT_NOT_SUPPORTED);

            return false;
        }

        // validate disbursement
        if (requireDisbursement && !bank.isBankDisbursementIndicator()) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.Bank.ERROR_DISBURSEMENT_NOT_SUPPORTED);

            return false;
        }

        return true;
    }

    /**
     * Performs required, exists, and active validation of bank code. Also validates bank for deposit or disbursement indicator if
     * requested.
     *
     * @param document the document that is being validated
     * @param bankCode value to validate
     * @param bankCodeProperty property to associate errors with
     * @param requireDeposit true if the bank code should support deposits
     * @param requireDisbursement true if the bank code should support disbursements
     * @return true if bank code passes all validations, false if any fail
     */
    public static boolean validate(Document document, String bankCode, String bankCodeProperty, boolean requireDeposit, boolean requireDisbursement) {
        if (document != null && !getBankService().isBankSpecificationEnabledForDocument(document.getClass())) {
            return true;
        }
        return BankCodeValidation.validate(bankCode, bankCodeProperty, requireDeposit, requireDisbursement);
    }

    /**
     * @return the default implementatino of the DataDictionaryService
     */
    protected static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    /**
     * @return the default implementation of the BankService
     */
    protected static BankService getBankService() {
        if (bankService == null) {
            bankService = SpringContext.getBean(BankService.class);
        }
        return bankService;
    }
}
