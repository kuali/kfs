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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class DisbursementVoucherAccountingLineValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineValidation.class);

    private ParameterService parameterService;

    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;

    protected static final String DV_PAYMENT_REASON_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;
    protected static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        boolean valid = true;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) accountingDocumentForValidation;
        MessageMap errors = GlobalVariables.getMessageMap();

        // don't validate generated tax lines
        if (dvDocument.getDvNonResidentAlienTax() != null) {
            String lineText = dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText();
            List<Integer> taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(lineText);

            if (taxLineNumbers.contains(accountingLineForValidation.getSequenceNumber())) {
                return true;
            }
        }

        /* payment reason must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            if (!errors.containsMessageKey(KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON)) {
                errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + DV_PAYMENT_REASON_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON);
            }
            valid = false;
        }

        /*
         * payee must be selected before an accounting line can be entered NOTE: This should never be possible given the new flow
         * that requires selection of the payee prior to DV creation, but I'm leaving the code in for validity sake. See KFSMI-714
         * for details on new flow.
         */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            if (!errors.containsMessageKey(KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE)) {
                errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE);
            }
            valid = false;
        }

        if (valid) {            
            valid = valid & validateAccountNumber(accountingDocumentForValidation, accountingLineForValidation);
            valid = valid & validateObjectCode(accountingDocumentForValidation, accountingLineForValidation);
        }

        return valid;
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     * 
     * @param FinancialDocument submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @return true if object code exists, is active, and object level and code exist for a provided payment reason
     */
    public boolean validateObjectCode(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("beginning object code validation ");

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        MessageMap errors = GlobalVariables.getMessageMap();

        boolean objectCodeAllowed = true;

        // object code exist done in super, check we have a valid object
        if (ObjectUtils.isNull(accountingLineForValidation.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        // make sure object code is active
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_INACTIVE, "Object Code");
            objectCodeAllowed = false;
        }

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        // check object level is in permitted list for payment reason
        objectCodeAllowed = objectCodeAllowed && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        // check object code is in permitted list for payment reason
        objectCodeAllowed = objectCodeAllowed && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getFinancialObjectCode()).evaluateAndAddError(SourceAccountingLine.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        objectCodeAllowed = objectCodeAllowed && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, DisbursementVoucherConstants.INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode().getFinancialObjectSubTypeCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param FinancialDocument submitted financial document
     * @param accountingLine accounting line in submitted accounting document
     * @return true if account exists, falls within global function code restrictions, and account's sub fund is in permitted list
     *         for payment reason
     */
    public boolean validateAccountNumber(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("beginning account number validation ");

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        MessageMap errors = GlobalVariables.getMessageMap();

        String errorKey = KFSPropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        // account exist and object exist done in super, check we have a valid object
        if (ObjectUtils.isNull(accountingLine.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        // global function code restrictions
        if (accountNumberAllowed) {
            ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd());
            // accountNumberAllowed is true now
            accountNumberAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, "account.financialHigherEdFunctionCd", KFSPropertyConstants.ACCOUNT_NUMBER);
        }

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return accountNumberAllowed;
        }

        // check sub fund is in permitted list for payment reason
        accountNumberAllowed = accountNumberAllowed && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getAccount().getSubFundGroupCode()).evaluateAndAddError(SourceAccountingLine.class, "account.subFundGroupCode", KFSPropertyConstants.ACCOUNT_NUMBER);

        return accountNumberAllowed;
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
     * Sets the accountingLineForValidation attribute value.
     * 
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

}
