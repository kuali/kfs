package edu.arizona.kfs.sys.document.validation.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.List;

public class AccountingLineGlobalTransactionValidation extends AccountingLineValueAllowedValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineGlobalTransactionValidation.class);

    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    private List<String> docTypesToValidate;
    private GlobalTransactionEditService globalTransactionEditService;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        boolean valid = true;

        String currentClass = event.getDocument().getClass().getSimpleName();

        if (docTypesToValidate.contains(currentClass)) {
            valid = validateAccountingLine(accountingDocumentForValidation, accountingLineForValidation);
        }

        return valid;
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     *
     * @param FinancialDocument submitted accounting document
     * @param accountingLine    accounting line in accounting document
     * @return true if object code is allowed, given the account's sub fund
     */
    private boolean validateAccountingLine(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("beginning object code validation ");

        boolean lineAllowed = true;
        if (isAccoutingLineComplete(accountingLine)) {
            Message msg = globalTransactionEditService.isAccountingLineAllowable((AccountingLineBase) accountingLine, financialDocument.getFinancialDocumentTypeCode());
            if (msg != null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_CUSTOM, msg.getMessage());
                lineAllowed = false;
            }
        }

        return lineAllowed;
    }

    /**
     * Tests the accounting line to see if there are sufficient values in it to validate.
     * <p>
     * For example, there needs to be at least a chartOfAccountsCode, an accountNumber, and
     * an objectCode, otherwise the validation will fail.
     *
     * @param accountingLine
     * @return
     */
    private boolean isAccoutingLineComplete(AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject("account");
        if (ObjectUtils.isNull(accountingLine.getAccount())) {
            return false;
        }

        accountingLine.getAccount().refreshReferenceObject("subFundGroup");
        if (ObjectUtils.isNull(accountingLine.getAccount().getSubFundGroup())) {
            return false;
        }

        accountingLine.refreshReferenceObject("objectCode");
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        return true;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     *
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

    /**
     * Sets the accountingLineForValidation attribute value.
     *
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the docTypesToValidate attribute.
     *
     * @return Returns the docTypesToValidate.
     */
    public List<String> getDocTypesToValidate() {
        return docTypesToValidate;
    }

    /**
     * Sets the docTypesToValidate attribute value.
     *
     * @param docTypesToValidate The docTypesToValidate to set.
     */
    public void setDocTypesToValidate(List<String> docTypesToValidate) {
        this.docTypesToValidate = docTypesToValidate;
    }

    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}
