package edu.arizona.kfs.sys.document.validation.impl;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.service.DataDictionaryService;


/**
 * Performs bank code validation.
 * 
 * Modified to add conditional validation based on the payment method code
 * 
 */
public class BankCodeValidation extends org.kuali.kfs.sys.document.validation.impl.BankCodeValidation {

    
    protected static PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    public static boolean validate(String bankCode, String bankCodeProperty, boolean requireDeposit, boolean requireDisbursement) {
        return validate(bankCode, bankCodeProperty, null, requireDeposit, requireDisbursement);
    }
    
    /**
     * Performs required, exists, and active validation of bank code. Also validates bank for deposit or disbursement indicator if
     * requested.
     * 
     * @param bankCode value to validate
     * @param bankCodeProperty property to associate errors with
     * @param paymentMethodCode the given payment method code
     * @param requireDeposit true if the bank code should support deposits
     * @param requireDisbursement true if the bank code should support disbursements
     * @return true if bank code passes all validations, false if any fail
     */
    public static boolean validate(String bankCode, String bankCodeProperty, String paymentMethodCode, boolean requireDeposit, boolean requireDisbursement) {

        // if bank specification is not enabled, no need to validate bank code
        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return true;
        }

        Bank bank = SpringContext.getBean(BankService.class).getByPrimaryId(bankCode);
        // required check
        // if the payment method code is blank, then revert to the baseline behavior
        if ( StringUtils.isBlank(paymentMethodCode) ) {
            if (StringUtils.isBlank(bankCode)) {
                String bankCodeLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Bank.class, KFSPropertyConstants.BANK_CODE);
                GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.ERROR_REQUIRED, bankCodeLabel);    
                return false;
            }            
            if (ObjectUtils.isNull(bank)) {
                GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK);
                return false;
            }            
        } else {
            if ( !checkBankCodePopulation(bankCode, paymentMethodCode, bankCodeProperty, true) ) {
                return false;
            }
        }

        
        // validate deposit
        if (bank != null && requireDeposit && !bank.isBankDepositIndicator()) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.Bank.ERROR_DEPOSIT_NOT_SUPPORTED);

            return false;
        }

        // validate disbursement
        if (bank != null && requireDisbursement && !bank.isBankDisbursementIndicator()) {
            GlobalVariables.getMessageMap().putError(bankCodeProperty, KFSKeyConstants.Bank.ERROR_DISBURSEMENT_NOT_SUPPORTED);

            return false;
        }

        return true;
    }

    public static boolean doesBankCodeNeedToBePopulated( String paymentMethodCode ) {
        return getPaymentMethodGeneralLedgerPendingEntryService().getBankForPaymentMethod(paymentMethodCode) != null;
    }
    
    public static boolean checkBankCodePopulation( String bankCode, String paymentMethodCode, String bankCodeProperty, boolean addMessages ) {
        boolean bankCodeNeedsPopulation = doesBankCodeNeedToBePopulated(paymentMethodCode);
        // if the payment method uses a bank code and none has been filled in (the user blanked it), throw an error
        if ( bankCodeNeedsPopulation && StringUtils.isBlank( bankCode ) ) {
            // error
            if ( addMessages ) {
                GlobalVariables.getMessageMap().putError( bankCodeProperty, KFSKeyConstants.ERROR_BANK_REQUIRED_PER_PAYMENT_METHOD, paymentMethodCode);
            }
            return false;
        } else if ( !bankCodeNeedsPopulation && StringUtils.isNotBlank( bankCode ) ) {
            // if the bank code on the document is not blank but no bank code is specified for the payment method, blank and warn the user.
            if ( addMessages ) {
                GlobalVariables.getMessageMap().putWarning( bankCodeProperty, KFSKeyConstants.WARNING_BANK_NOT_REQUIRED, paymentMethodCode);
            }
        }
        return true;
    }

    protected static PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        if ( paymentMethodGeneralLedgerPendingEntryService == null ) {
            paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
        }
        return paymentMethodGeneralLedgerPendingEntryService;
    }
    
}

