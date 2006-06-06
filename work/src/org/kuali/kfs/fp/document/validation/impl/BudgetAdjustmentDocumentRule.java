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
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;


/**
 * Business rule(s) applicable to Procurement Card document.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentRule extends TransactionalDocumentRuleBase {

  /** 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
   */
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine) {
    boolean allow = true;

    LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

    LOG.debug("beginning monthly lines validation ");
    allow = allow && validateMonthlyLines(transactionalDocument, accountingLine);

    LOG.debug("beginning object code validation ");
    allow = allow && validateObjectCode(transactionalDocument, accountingLine);

    LOG.debug("end validating accounting line, has errors: " + allow);

    return allow;
  }

  /**
     * The budget adjustment document creates GL pending entries much differently that common tp-edocs. The glpes are created for
     * BB, CB, and MB balance types. Up to 14 entries per line can be created. Along with this, the BA will to TOF entries if needed
     * to move funding.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry();

        // determine if we are on increase or decrease side
        KualiDecimal amountSign = new KualiDecimal(1);
        if (accountingLine instanceof SourceAccountingLine) {
            amountSign = new KualiDecimal(-1);
        }

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) accountingLine;
        /* Create Base Budget GLPE if base amount != 0 */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount() != null
                && budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero()) {
            populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_BASE_BUDGET);
            explicitEntry
                    .setTransactionLedgerEntryAmount(budgetAccountingLine.getBaseBudgetAdjustmentAmount().multiply(amountSign));
            // TODO: set fiscal period, if next fiscal year set to 01, else leave empty

            // add the new explicit entry to the document now
            transactionalDocument.addGeneralLedgerPendingEntry(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();

            // handle the offset entry
            offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
            success &= processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLine, explicitEntry,
                    offsetEntry);

            // increment the sequence counter
            sequenceHelper.increment();
        }

        /* Create Current Budget GLPE if current amount != 0 */
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount() != null
                && budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_CURRENT_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getCurrentBudgetAdjustmentAmount().multiply(
                    amountSign));
            // TODO: set fiscal period, if next fiscal year set to 01, else leave empty

            // add the new explicit entry to the document now
            transactionalDocument.addGeneralLedgerPendingEntry(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();

            // handle the offset entry
            offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
            success &= processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLine, explicitEntry,
                    offsetEntry);

            // increment the sequence counter
            sequenceHelper.increment();

            // create montly lines (MB)
            if (budgetAccountingLine.getFinancialDocumentMonth1LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth1LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "01", budgetAccountingLine
                        .getFinancialDocumentMonth1LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth2LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth2LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "02", budgetAccountingLine
                        .getFinancialDocumentMonth2LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth3LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth3LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "03", budgetAccountingLine
                        .getFinancialDocumentMonth3LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth4LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth4LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "04", budgetAccountingLine
                        .getFinancialDocumentMonth4LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth5LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth5LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "05", budgetAccountingLine
                        .getFinancialDocumentMonth5LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth6LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth6LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "06", budgetAccountingLine
                        .getFinancialDocumentMonth6LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth7LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth7LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "07", budgetAccountingLine
                        .getFinancialDocumentMonth7LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth8LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth8LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "08", budgetAccountingLine
                        .getFinancialDocumentMonth8LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth9LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth9LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "09", budgetAccountingLine
                        .getFinancialDocumentMonth9LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth10LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth10LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "10", budgetAccountingLine
                        .getFinancialDocumentMonth10LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth11LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth11LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "11", budgetAccountingLine
                        .getFinancialDocumentMonth11LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth12LineAmount() != null
                    && budgetAccountingLine.getFinancialDocumentMonth12LineAmount().isNonZero()) {
                success &= createMontlyBudgetGLPE(transactionalDocument, accountingLine, sequenceHelper, "12", budgetAccountingLine
                        .getFinancialDocumentMonth12LineAmount().multiply(amountSign));
            }
        }
        
        // create TOF entries
        success &= processTransferOfFundsGeneralLedgerPendingEntries(transactionalDocument,accountingLine,sequenceHelper);

        // handle the situation where the document is an error correction or is corrected
        // super.handleDocumentErrorCorrection(transactionalDocument, accountingLine);

        return success;
    }

    /**
     * Helper method for creating monthly buget pending entry lines.
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param fiscalPeriod
     * @param monthAmount
     * @return
     */
    private boolean createMontlyBudgetGLPE(TransactionalDocument transactionalDocument, AccountingLine accountingLine,
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String fiscalPeriod, KualiDecimal monthAmount) {
        boolean success = true;

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry();

        populateExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, sequenceHelper, explicitEntry);

        /* D/C code is empty for BA, set correct balance type, correct amount */
        explicitEntry.setTransactionDebitCreditCode("");
        explicitEntry.setFinancialBalanceTypeCode(Constants.BALANCE_TYPE_MONTHLY_BUDGET);
        explicitEntry.setTransactionLedgerEntryAmount(monthAmount);
        explicitEntry.setUniversityFiscalPeriodCode(fiscalPeriod);

        // add the new explicit entry to the document now
        transactionalDocument.addGeneralLedgerPendingEntry(explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        success &= processOffsetGeneralLedgerPendingEntry(transactionalDocument, sequenceHelper, accountingLine, explicitEntry,
                offsetEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        return success;
    }
    
    /**
     * Generates any necessary tof entries to transfer funds needed to make the budget adjustments.
     * Based on income chart and accounts.
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @return
     */
    public boolean processTransferOfFundsGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        
        return success;
    }


    /**
   * Checks object codes restrictions, including restrictions in parameters table.
   */
  public boolean validateMonthlyLines(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
      BudgetAdjustmentAccountingLine baAccountingLine = (BudgetAdjustmentAccountingLine)accountingLine;
      ErrorMap errors = GlobalVariables.getErrorMap();

      boolean validMonthlyLines = true;

      KualiDecimal monthlyTotal = baAccountingLine.getMonthlyLinesTotal();
      if (baAccountingLine.getCurrentBudgetAdjustmentAmount() != null &&
              KualiDecimal.ZERO.compareTo(baAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0) {
          //if current amt is entered check monthly amt
          if (monthlyTotal != null &&
                  KualiDecimal.ZERO.compareTo(monthlyTotal) != 0 &&
                  monthlyTotal.compareTo(baAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0 ) {
              errors.put(PropertyConstants.BA_CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
              validMonthlyLines = false;
          }
      } else {
          //if current amt is not entered, monthly amts cannot be entered
          if (monthlyTotal != null || KualiDecimal.ZERO.compareTo(monthlyTotal) != 0 ) {
              errors.put(PropertyConstants.BA_CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_ALLOWED_EMPTY_CURRENT);
              validMonthlyLines = false;
          }
      }

      return validMonthlyLines;
  }

  /**
   * Checks object codes restrictions, including restrictions in parameters table.
   */
  public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
      BudgetAdjustmentDocument baDocument = (BudgetAdjustmentDocument) transactionalDocument;
      ErrorMap errors = GlobalVariables.getErrorMap();

      String errorKey = PropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
      boolean objectCodeAllowed = true;


      return objectCodeAllowed;
  }

  @Override
  public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
      //TODO add validation
      return true;
  }

  /**
     * BA document does not have to have source accounting lines. In the case of setting up a budget for
     * a new account, only targets line (increase section) are setup.
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * In order for the BA document to balance:
     * 
     * Total of Base Income Adjustments - Base Expense Adjustments on Descrease side must equal
     *  Total of Base Income Adjustments - Base Expense Adjustments on increase side
     * Total of Current Income Adjustments - Base Current Adjustments on Descrease side must equal
     *  Total of Current Income Adjustments - Base Current Adjustments on increase side
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        // TODO Auto-generated method stub
        return super.isDocumentBalanceValid(transactionalDocument);
    }


    /**
   * Checks the given field value against a restriction defined in the application parameters table. If the rule fails, an error
   * is added to the global error map.
   * 
   * @param parameterGroupName - Security Group name
   * @param parameterName - Parameter Name
   * @param restrictedFieldValue - Value to check
   * @param errorField - Key to associate error with in error map
   * @param errorParameter - String parameter for the restriction error message
   * @return boolean indicating whether or not the rule passed
   */
  private boolean executeApplicationParameterRestriction(String parameterGroupName, String parameterName,
          String restrictedFieldValue, String errorField, String errorParameter) {
      boolean rulePassed = true;

      if (SpringServiceLocator.getKualiConfigurationService().hasApplicationParameter(parameterGroupName, parameterName)) {
          KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                  parameterGroupName, parameterName);
          if (rule.failsRule(restrictedFieldValue)) {
              GlobalVariables.getErrorMap().put(
                      errorField,
                      rule.getErrorMessageKey(),
                      new String[] { errorParameter, restrictedFieldValue, parameterName, parameterGroupName,
                              rule.getParameterText() });
              rulePassed = false;
          }
      }
      else {
          LOG.warn("Did not find apc parameter record for group " + parameterGroupName + " with parm name " + parameterName);
      }

      return rulePassed;
  }

}