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

import java.util.Set;
import java.util.TreeSet;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to IndirectCostAdjustment documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostAdjustmentDocumentRule extends TransactionalDocumentRuleBase {
	private final static Set _invalidSubTypes = new TreeSet();
	private final static Set _invalidObjectTypes = new TreeSet();
	private final static Set _invalidSubFundGroupTypes = new TreeSet();
	
	static {
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.CASH);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.FRINGE_BEN);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER);
		_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
		
		_invalidObjectTypes.add(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
		_invalidObjectTypes.add(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
		
		_invalidSubFundGroupTypes.add(SUB_FUND_GROUP_CODE.RENEWAL_AND_REPLACEMENT);
	}
	
    /**
     * @see org.kuali.core.rule.ReviewAccountingLineRule#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine line) {
        return super.processCustomReviewAccountingLineBusinessRules( document, line ) && validateAccountingLine(document, line);
    }
    /**
     * TODO - as part of code review clean up, push any of this into the proper "is" methods.
     * @see org.kuali.core.rule.AddAccountingLineRule#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine line) {
        return super.processCustomAddAccountingLineBusinessRules( document, line ) && validateAccountingLine(document, line);
    }
    
    public boolean validateAccountingLine(TransactionalDocument document, AccountingLine line) {
        boolean retval = true;

        line.refreshReferenceObject("objectType");
        retval &= !_invalidObjectTypes.contains(line.getObjectType().getCode());
        line.refreshReferenceObject("objectCode");
        retval &= !_invalidSubTypes.contains(line.getObjectCode().getFinancialObjectType().getCode());
        line.refreshReferenceObject("account");
        retval &= !_invalidSubFundGroupTypes.contains(line.getAccount().getSubFundGroup().getSubFundGroupCode());
        
        return retval;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
		boolean retval = true;
		
		retval &= documentDescriptionExists(document);
		retval &= isDocumentBalanceValid((TransactionalDocument)document);
		return retval;
    }
    
    /**
     * 
     */
	protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_CODE.ACTUAL );
        explicitEntry.setReferenceFinDocumentTypeCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE);
        explicitEntry.setFinancialDocumentReferenceNbr(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE);
        explicitEntry.setTransactionEncumbranceUpdtCd(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SPACE);
	}

	/**
	 * 
	 */
	public boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
		KualiDecimal amount = accountingLine.getAmount();
		if(!amount.isNegative()) {
			return (!(isIncomeOrLiability(accountingLine) && isTargetAccountingLine(accountingLine)));
		} else {
			return isIncomeOrLiability(accountingLine) && isTargetAccountingLine(accountingLine);
		}
	}

	/**
	 * Checks if the description of the <code>{@link Document}</code>
	 * 
	 * @param document <code>{@link Document}</code> to check for description
	 * @return whether description exists or not.
	 */
    private boolean documentDescriptionExists( Document document ) {
        boolean retval = false;
        
        if( document.getDocumentHeader().getFinancialDocumentDescription() != null
        		&& document.getDocumentHeader().getFinancialDocumentDescription().trim().length() > 0 ) {
        	retval = true;
        }
        
        return retval;
    }
}