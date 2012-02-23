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
