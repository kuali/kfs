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
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;


/**
 * Business rule(s) applicable to Procurement Card document.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentRule extends TransactionalDocumentRuleBase {

  /** 
   * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
   */
  protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
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
   * Checks object codes restrictions, including restrictions in parameters table.
   */
  public boolean validateMonthlyLines(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
      //TODO fix this: should not cast into source type
      BudgetAdjustmentSourceAccountingLine baAccountingLine = (BudgetAdjustmentSourceAccountingLine)accountingLine;
      ErrorMap errors = GlobalVariables.getErrorMap();

      boolean validMonthlyLines = true;

      if (baAccountingLine.getCurrentBudgetAdjustmentAmount() != null) {
          KualiDecimal monthlyTotal = baAccountingLine.getMonthlyLinesTotal();
          if ((monthlyTotal != null) && (monthlyTotal.compareTo(baAccountingLine.getCurrentBudgetAdjustmentAmount()) != 0)) {
              errors.put(PropertyConstants.BA_CURRENT_BUDGET_ADJUSTMENT_AMOUNT, KeyConstants.ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT);
              validMonthlyLines = false;
          }
      } else {
          //what validation is needed if the current amt is null?
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