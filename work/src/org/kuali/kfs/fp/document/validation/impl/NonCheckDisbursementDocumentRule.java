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

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;

import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.KeyConstants.NonCheckDisbursement.*;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.module.financial.rules.NonCheckDisbursementDocumentRuleConstants.*;

/**
 * Business rule(s) applicable to NonCheckDisbursement documents.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocumentRule extends TransactionalDocumentRuleBase {
       
    /**
     * Convenience method for accessing the most-likely requested
     * security grouping
     *
     * @return String
     */
    protected String getDefaultSecurityGrouping() {
        return NON_CHECK_DISBURSEMENT_SECURITY_GROUPING;
    }

    /**
     * Overrides to consider the object types.
     *
     * @see TransactionalDocumentRuleBase#isDocumentBalanceValid(TransactionalDocument)
     */
	@Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return isDocumentBalancedConsideringObjectTypes(transactionalDocument);
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(org.kuali.core.bo.AccountingLine)
     */
	@Override
    public boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
        return isDebitConsideringSection(accountingLine);
    }
    
    /**
     * Overrides the parent to return true, because NonCheckDisbursement documents only use the SourceAccountingLines data structures.
     * The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or submitted to
     * post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
	@Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }
    
    /**
     * Overrides to perform the universal rule in the super class in addition 
     * to Non-Check Disbursement specific rules. This method leverages the 
     * APC for checking restricted object type values.
     *
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
	@Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectTypeAllowed(accountingLine);

		ObjectCode objectCode = accountingLine.getObjectCode();
			
		if (valid) {
			valid = succeedsRule(RESTRICTED_OBJECT_TYPE_CODES,
								 objectCode.getFinancialObjectTypeCode());
		}

		if (!valid) {
			// add message
			GlobalVariables.getErrorMap()
				.put(FINANCIAL_OBJECT_CODE,
					 ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE,
					 new String[] {objectCode.getFinancialObjectCode(), 
								   objectCode.getFinancialObjectTypeCode()});
		}
		
        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition 
     * to Non-Check Disbursement specific rules. This method leverages the 
     * APC for checking restricted object sub type values.
     *
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
	@Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = super.isObjectSubTypeAllowed(accountingLine);

		ObjectCode objectCode = accountingLine.getObjectCode();
			
		if (valid) {
			valid = succeedsRule(RESTRICTED_OBJECT_SUB_TYPE_CODES,
                              objectCode.getFinancialObjectSubTypeCode());
		}

        if (!valid) {
            // add message
            GlobalVariables.getErrorMap()
                .put(FINANCIAL_OBJECT_CODE,
                     ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_SUB_TYPE_CODE,
                     new String[] {objectCode.getFinancialObjectCode(), 
                                   objectCode.getFinancialObjectSubTypeCode()});
        }

        return valid;
    }

    /**
	 * @see TransactionalDocumentRuleBase#isSubFundGroupAllowed(AccountingLine accountingLine)
     */
	@Override
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine) {
        boolean valid = super.isSubFundGroupAllowed(accountingLine);
 
		String subFundGroupTypeCode = accountingLine
			.getAccount().getSubFundGroup().getSubFundGroupTypeCode();
		ObjectCode objectCode = accountingLine.getObjectCode();
        
		if (valid) {
			valid = succeedsRule(RESTRICTED_SUB_FUND_GROUP_TYPE_CODES,
								 subFundGroupTypeCode);
		}
		
		if (!valid) {
            // add message
            GlobalVariables.getErrorMap()
                .put(FINANCIAL_OBJECT_CODE,
                     ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_SUB_FUND_GROUP,
                     new String[] {objectCode.getFinancialObjectCode(), 
                                   subFundGroupTypeCode});
        }

        return valid;
    }

    /**
	 * @see TransactionalDocumentRuleBase#isObjectConsolidationAllowed(AccountingLine accountingLine)
     */
	@Override
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        ObjectCode objectCode = accountingLine.getObjectCode();
        String consolidationCode = objectCode
            .getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        valid &= succeedsRule(RESTRICTED_CONSOLIDATION_CODES, consolidationCode);
        
        if (!valid) {            
            // add message
            GlobalVariables.getErrorMap()
                .put(FINANCIAL_OBJECT_CODE,
                     ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_CONSOLIDATION_CODE,
                     new String[] {objectCode.getFinancialObjectCode(), 
                                   consolidationCode});
        }

        return valid;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
	@Override
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument,
                 SourceAccountingLine accountingLine) {
        SufficientFundsItem item = null;
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        String accountSufficientFundsCode = 
            accountingLine.getAccount().getAccountSufficientFundsCode();
        String financialObjectCode = 
            accountingLine.getObjectCode().getFinancialObjectCode();
        String financialObjectLevelCode = 
            accountingLine.getObjectCode().getFinancialObjectLevelCode();
        KualiDecimal lineAmount = 
            getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine);
        Integer fiscalYear = accountingLine.getPostingYear();
        String financialObjectTypeCode = accountingLine.getObjectTypeCode();

        // always credit
        String debitCreditCode = null;

        if (isDebit(accountingLine)) {
            debitCreditCode = GL_CREDIT_CODE;
        }
        else {
            debitCreditCode = GL_DEBIT_CODE;
        }

        String sufficientFundsObjectCode = 
			SpringServiceLocator.getSufficientFundsService()
            .getSufficientFundsObjectCode(chartOfAccountsCode, 
                                          financialObjectCode, 
                                          accountSufficientFundsCode, 
                                          financialObjectLevelCode);

        item = buildSufficentFundsItem(accountNumber, 
                                       accountSufficientFundsCode, 
                                       lineAmount, chartOfAccountsCode,
                                       sufficientFundsObjectCode, 
                                       debitCreditCode, financialObjectCode, 
                                       financialObjectLevelCode, fiscalYear,
                                       financialObjectTypeCode);

        return item;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
	@Override
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument transactionalDocument,
                 TargetAccountingLine targetAccountingLine) {
        return null;
    }
}
