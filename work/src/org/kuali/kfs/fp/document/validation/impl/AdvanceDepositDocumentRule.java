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

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

/**
 * Business rules applicable to Credit Card Receipt documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AdvanceDepositDocumentRule extends CashReceiptDocumentRule {
    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts
     * equals the sum total of the accounting lines.
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
//        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) transactionalDocument;
//
//        // make sure that the credit card total is greater than zero
//        boolean isValid = ccr.getSumTotalAmount().compareTo(Constants.ZERO) > 0;
//        if (!isValid) {
//            GlobalVariables.getErrorMap().put(PropertyConstants.NEW_CREDIT_CARD_RECEIPT, 
//                    KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_TOTAL_INVALID);
//        }
//
//        if (isValid) {
//            // make sure the document is in balance
//            isValid = ccr.getSourceTotal().compareTo(ccr.getSumTotalAmount()) == 0;
//
//            if (!isValid) {
//                GlobalVariables.getErrorMap().put(PropertyConstants.NEW_CREDIT_CARD_RECEIPT,
//                        KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
//            }
//        }
//
//        return isValid;
        return true;
    }
    
    /**
     * Overrides to call super and then make sure the minimum number of credit card receipt 
     * lines exist on this document.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
//        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
//        
//        if(isValid) {
//            isValid = isMinimumNumberOfCreditCardReceiptsMet(document, isValid);
//        }
//        
//        return isValid;
        return true;
    }

    /**
     * This method is a helper that checks to make sure that at least one credit card receipt 
     * line exists for the document.
     * 
     * @param document
     * @param isValid
     * @return boolean
     */
    private boolean isMinimumNumberOfCreditCardReceiptsMet(Document document, boolean isValid) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) document;
        
        if(ccr.getCreditCardReceipts().size() == 0) {
            GlobalVariables.getErrorMap().put(DOCUMENT_ERROR_PREFIX,
                    KeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET);
            isValid = false;
        }
        return isValid;
    }
    
    /**
     * Overrides to call super and then to validate all of the credit card receipts associated with this document.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
//        if(isValid) {
//            isValid = validateCreditCardReceipts((CreditCardReceiptDocument) document);
//        }
        
        return isValid;
    }
    
    /**
     * Validates all the CreditCardReceipts in the given Document, adding global errors for invalid items. It just uses the
     * DataDictionary validation.
     *
     * @param creditCardReceiptDocument
     * @return boolean
     */
    private boolean validateCreditCardReceipts(CreditCardReceiptDocument creditCardReceiptDocument) {
        final ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.addToErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        int originalErrorCount = errorMap.getErrorCount();
        for (int i = 0; i < creditCardReceiptDocument.getCreditCardReceipts().size(); i++) {
            String propertyName = PropertyConstants.CREDIT_CARD_RECEIPT + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(creditCardReceiptDocument.getCreditCardReceipt(i));
            // todo - check existence - use a rule util class like the ccr doc rule util
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }
        int currentErrorCount = errorMap.getErrorCount();
        errorMap.removeFromErrorPath(Constants.DOCUMENT_PROPERTY_NAME);
        return currentErrorCount == originalErrorCount;
    }
}
