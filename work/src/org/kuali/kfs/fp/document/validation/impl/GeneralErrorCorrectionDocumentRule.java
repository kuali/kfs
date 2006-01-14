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
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class GeneralErrorCorrectionDocumentRule extends TransactionalDocumentRuleBase {

	/**
	 * 
	 */
	public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine( document, accountingLine );
    }
    
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine( document, accountingLine );
    }
    
    protected boolean validateAccountingLine(TransactionalDocument document, AccountingLine accountingLine ) {
	    //get a new instance so we don't affect the original accounting line's values
        AccountingLine accountingLineCopy = (AccountingLine) ObjectUtils.deepCopy(accountingLine);
        
        //now make sure all the necessary business objects are fully populated
        SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(accountingLineCopy);
        
		String objectCode = accountingLineCopy.getObjectCode().getFinancialObjectCode();
		String objectTypeCode = accountingLineCopy.getObjectCode().getFinancialObjectType().getCode();
		String objectSubTypeCode = accountingLineCopy.getObjectCode().getFinancialObjectSubType().getCode();
		
		// Check for failure cases.
		if(OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectTypeCode)) {
			// NOTE: Check for objectTypeCode of "FDBL" is done in TransactionalDocumentRuleBase.
			GlobalVariables.getErrorMap().put(
				Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_TYPE,
				new String[] {objectCode, objectTypeCode});
			return false;
		}
		
		if(OBJECT_SUB_TYPE_CODE.CASH.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.FRINGE_BEN.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.LOSS_ON_DISPOSAL_OF_ASSETS.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.OTHER_PROVISIONS.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.SALARIES.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.HOURLY_WAGES.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.BUDGET_ONLY.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.PLANT.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.DEPRECIATION.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.LOSS_ON_RETIREMENT_OF_ASSETS.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.VALUATIONS_AND_ADJUSTMENTS.equals(objectSubTypeCode)
			|| OBJECT_SUB_TYPE_CODE.ASSESSMENT.equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER.equals(objectSubTypeCode)) {
			GlobalVariables.getErrorMap().put(
				Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE,
				new String[] {objectSubTypeCode});
			return false;
		}
		
		if(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectTypeCode) 
			&& ((!OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND.equals(objectSubTypeCode) 
					&& !OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP.equals(objectSubTypeCode) 
					&& !OBJECT_TYPE_CODE.WRITE_OFF_EXPENSE.equals(objectSubTypeCode)))) {
			GlobalVariables.getErrorMap().put(
				Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_TYPE_WITH_OBJ_SUB_TYPE,
				new String[] {objectTypeCode, objectSubTypeCode});
			return false;
		}
		
		return true;
	}
}
