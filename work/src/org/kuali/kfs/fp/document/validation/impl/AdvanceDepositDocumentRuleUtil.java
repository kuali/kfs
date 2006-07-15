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
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.AdvanceDepositDetail;

/**
 * Common Advance Deposit Document rule utilities.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AdvanceDepositDocumentRuleUtil {
    /**
     * This method method will invoke the data dictionary validation for a AdvanceDepositDetail bo instance, in addition to checking
     * existence of the Bank and BankAccount attributes that hang off of it. This method assumes that the document hierarchy for the
     * error map path is managed outside of this call.
     * 
     * @param advanceDeposit
     * @return boolean
     */
    public static boolean validateAdvanceDeposit(AdvanceDepositDetail advanceDeposit) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();

        // call the DD validation which checks basic data integrity
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(advanceDeposit);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);
        
        // check that dollar amount is not zero before continuing
        if(isValid) {
            isValid = !advanceDeposit.getFinancialDocumentAdvanceDepositAmount().isZero();
            if(!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(AdvanceDepositDetail.class, PropertyConstants.ADVANCE_DEPOSIT_AMOUNT);
                errorMap.putError(PropertyConstants.ADVANCE_DEPOSIT_AMOUNT, KeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT, label);
            }
        }
        
        if (isValid) {
            isValid = SpringServiceLocator.getDictionaryValidationService().validateReferenceExists(advanceDeposit, PropertyConstants.FINANCIAL_DOCUMENT_BANK);
            if (!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(AdvanceDepositDetail.class, PropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE);
                errorMap.putError(PropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE, KeyConstants.ERROR_EXISTENCE, label);
            }
        }
        if (isValid) {
            isValid = SpringServiceLocator.getDictionaryValidationService().validateReferenceExists(advanceDeposit, PropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT);
            if (!isValid) {
                String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(AdvanceDepositDetail.class, PropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT_NUMBER);
                errorMap.putError(PropertyConstants.FINANCIAL_DOCUMENT_BANK_ACCOUNT_NUMBER, KeyConstants.ERROR_EXISTENCE, label);
            }
        }

        return isValid;
    }
}