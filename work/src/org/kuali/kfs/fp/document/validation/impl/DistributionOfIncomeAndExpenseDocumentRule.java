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

import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;

/**
 * This class...
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DistributionOfIncomeAndExpenseDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * 
     */
    protected boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
    	// Negative amounts are not allowed.
    	if(accountingLine.getAmount().isNegative()) {
    		throw new IllegalStateException(objectTypeCodeIllegalStateExceptionMessage);
    	}
    	
    	if(isSourceAccountingLine(accountingLine)) {
    		return !super.isDebit(accountingLine);
    	} else {
    		return super.isDebit(accountingLine);
    	}
	}
	
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        TransactionalDocument tranDoc = (TransactionalDocument) document;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        // TODO: Fill this in when Tony finishes his Utility to apply all core business rules.
        List sLines = tranDoc.getSourceAccountingLines();
        List tLines = tranDoc.getTargetAccountingLines();

        // First, validate the accounting lines
        // then. check for expense/revenue balance
        // finally, do object code restrictions checking
        KualiDecimal sExpense = KualiDecimal.ZERO;
        KualiDecimal sRevenue = KualiDecimal.ZERO;
        KualiDecimal tExpense = KualiDecimal.ZERO;
        KualiDecimal tRevenue = KualiDecimal.ZERO;

        for (Iterator i = sLines.iterator(); i.hasNext();) {
            SourceAccountingLine sal = (SourceAccountingLine) i.next();
            if (isExpenseOrAsset(sal)) {
                sExpense = sExpense.add( sal.getAmount() );
            } else {
                sRevenue = sRevenue.add( sal.getAmount() );
            }
        }

        for (Iterator i = tLines.iterator(); i.hasNext();) {
            TargetAccountingLine tal = (TargetAccountingLine) i.next();
            if (isExpenseOrAsset(tal)) {
                tExpense = tExpense.add( tal.getAmount() );
            } else {
                tRevenue = tRevenue.add( tal.getAmount() );
            }
        }

        KualiDecimal sDiff = sRevenue.subtract( sExpense );
        KualiDecimal tDiff = tRevenue.subtract( tExpense );
        if (!sDiff.equals( tDiff ) ) {
            errorMap.put(Constants.DOCUMENT_ERRORS,
                    "Document does now balance. From revenue minus expense should equal To revenue minus expense!", "Amount");
            return false;
        }

        return errorMap.isEmpty();
    }

    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && checkAccountingLine(document, accountingLine);
    }

    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine ) && checkAccountingLine(document, accountingLine);
    }

    /**
     * TODO - Code reivew: Can checks in here be split out into the separate is<Entity>Allowed methods?
     * @param document
     * @param accountingLine
     */
    private boolean checkAccountingLine(TransactionalDocument document, AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject("objectCode");

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        boolean result = true;
        String consolidatedObjectCode = accountingLine.getObjectCode().getFinancialObjectLevel().getConsolidatedObjectCode();
        if ("FDBL".equals(consolidatedObjectCode)) {
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NUMBER, "Consolidated Object Code");
            result = false;
        }
        
        //object type code check
        String objectTypeCode = accountingLine.getObjectCode().getFinancialObjectTypeCode();
        if ("IC".equals(objectTypeCode) || "TF".equals(objectTypeCode)) {
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NUMBER, "Object Type Code");
            result = false;
        }
        return result;
    }
    
    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code is allowed.  This 
     * is called by the parent's processAddAccountingLine() method.
     * @param document
     * @param accountingLine
     * @return True if the object code's object sub-type code is allowed; false otherwise.
     */
    protected boolean isObjectCodeObjectSubTypeCodeAllowd(TransactionalDocument document, AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject("objectCode");

        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

        if ("CA".equals(objectSubTypeCode) || "HW".equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER.equals(objectSubTypeCode)
                || "PL".equals(objectSubTypeCode) || "SW".equals(objectSubTypeCode) || "SA".equals(objectSubTypeCode)
                || "OP".equals(objectSubTypeCode) || "LD".equals(objectSubTypeCode) || "FR".equals(objectSubTypeCode)
                || "BU".equals(objectSubTypeCode) || OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER.equals(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NUMBER, "Object Sub Type Code");
            return false;
        }
        
        return true;
    }
}
