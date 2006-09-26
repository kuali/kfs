/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants.CashReceipt;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.CreditCardDetail;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;
import static org.kuali.module.financial.rules.CreditCardReceiptDocumentRuleConstants.CREDIT_CARD_RECEIPT_PREFIX;

/**
 * Common Credit Card Receipt Document rule utilities.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class CreditCardReceiptDocumentRuleUtil {
    /**
     * This method method will invoke the data dictionary validation for a CreditCardDetail bo instance, in addition to checking
     * existence of the CreditCardType and CreditCardVendor attributes that hang off of it. This method assumes that the document
     * hierarchy for the error map path is managed outside of this call.
     * 
     * @param creditCardReceipt
     * @return boolean
     */
    public static boolean validateCreditCardReceipt(CreditCardDetail creditCardReceipt) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();

        // call the DD validation which checks basic data integrity
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(creditCardReceipt);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !creditCardReceipt.getCreditCardAdvanceDepositAmount().isZero();
            if (!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(CreditCardDetail.class, PropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT);
                errorMap.putError(PropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT, KeyConstants.ERROR_ZERO_AMOUNT, label);
            }
        }

        if (isValid) {
            isValid = SpringServiceLocator.getDictionaryValidationService().validateReferenceExists(creditCardReceipt, PropertyConstants.CREDIT_CARD_TYPE);
            if (!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(CreditCardDetail.class, PropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE);
                errorMap.putError(PropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE, KeyConstants.ERROR_EXISTENCE, label);
            }
        }
        if (isValid) {
            isValid = SpringServiceLocator.getDictionaryValidationService().validateReferenceExists(creditCardReceipt, PropertyConstants.CREDIT_CARD_VENDOR);
            if (!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(CreditCardDetail.class, PropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER);
                errorMap.putError(PropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER, KeyConstants.ERROR_EXISTENCE, label);
            }
        }

        return isValid;
    }


    /**
     * Checks whether the CashReceiptDocument's cash totals are invalid, generating global errors if so.
     * 
     * @param cashReceiptDocument
     * @return whether any of the CashReceiptDocument's cash totals are invalid
     */
    public static boolean areCashTotalsInvalid(CreditCardReceiptDocument ccrDocument) {
        String documentEntryName = ccrDocument.getDocumentHeader().getWorkflowDocument().getDocumentType();

        boolean isInvalid = isTotalInvalid(ccrDocument, ccrDocument.getSumTotalAmount(), documentEntryName, PropertyConstants.CREDIT_CARD_RECEIPTS_TOTAL);

        return isInvalid;
    }

    /**
     * puts an error message in the error map for that property if the amount is negative.
     * 
     * @param cashReceiptDocument
     * @param totalAmount
     * @param documentEntryName
     * @param propertyName
     * @return true if the totalAmount is an invalid value
     */
    private static boolean isTotalInvalid(CreditCardReceiptDocument ccrDocument, KualiDecimal totalAmount, String documentEntryName, String propertyName) {
        boolean isInvalid = false;
        String errorProperty = CREDIT_CARD_RECEIPT_PREFIX + propertyName;

        // treating null totalAmount as if it were a zero
        DataDictionaryService dds = SpringServiceLocator.getDataDictionaryService();
        String errorLabel = dds.getAttributeLabel(documentEntryName, propertyName);
        if ((totalAmount == null) || totalAmount.isZero()) {
            GlobalVariables.getErrorMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, errorLabel);

            isInvalid = true;
        }
        else {
            int precount = GlobalVariables.getErrorMap().size();

            DictionaryValidationService dvs = SpringServiceLocator.getDictionaryValidationService();
            dvs.validateDocumentAttribute(ccrDocument, propertyName, DOCUMENT_ERROR_PREFIX);

            // replace generic error message, if any, with something more readable
            GlobalVariables.getErrorMap().replaceError(errorProperty, KeyConstants.ERROR_MAX_LENGTH, CashReceipt.ERROR_EXCESSIVE_TOTAL, errorLabel);

            int postcount = GlobalVariables.getErrorMap().size();
            isInvalid = (postcount > precount);
        }

        return isInvalid;
    }
}