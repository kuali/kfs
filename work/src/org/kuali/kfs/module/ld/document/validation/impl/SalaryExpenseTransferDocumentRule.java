/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.labor.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBase;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborObject;

/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 * 
 * 
 */
public class SalaryExpenseTransferDocumentRule extends TransactionalDocumentRuleBase {

    public SalaryExpenseTransferDocumentRule() {
    }   
    
    protected boolean AddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
    }
    
    /** Account must be valid.
      * Object code must be valid.
      * Object code must be a labor object code.
             Object code must exist in the ld_labor_obj_t table.
             The field finobj_frngslry_cd for the object code in the ld_labor_obj_t table must have a value of "S".
      * Sub-account, if specified, must be valid for account.
      * Sub-object, if specified, must be valid for account and object code.
      * Enforce the A21-report-related business rules for the "SAVE" action.
      * Position must be valid for fiscal year. FIS enforces this by a direct lookup of the PeopleSoft HRMS position data table. Kuali cannot do this. (See issue 12.)
      * Amount must not be zero. 
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        if (accountingLine.isSourceAccountingLine()) {
            
            // Retrieve the Fringe or Salary Code for the object code in the ld_labor_obj_t table. 
            // It must have a value of "S".
            
            ErrorMap errorMap = GlobalVariables.getErrorMap();
            Map fieldValues = new HashMap();
            fieldValues.put("financialObjectCode", accountingLine.getFinancialObjectCode().toString());
            ArrayList laborObjects = (ArrayList) SpringServiceLocator.getBusinessObjectService().findMatching(LaborObject.class, fieldValues);
            if (laborObjects.size() == 0) {
                reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
                return false;
            }
            LaborObject laborObject = (LaborObject) laborObjects.get(0);    
            String FringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();

            if (!FringeOrSalaryCode.equals("S")) {
                LOG.info("FringeOrSalaryCode not equal S");
                  reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.FRINGE_OR_SALARY_CODE_MISSING_ERROR, accountingLine.getAccountNumber());
                return false;
            }
        }
        return true;
    }

    /**
     * Set attributes of an offset pending entry according to rules specific to TransferOfFundsDocument.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        //offsetEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        return true;
    }

    /**
     * Set attributes of an explicit pending entry according to rules specific to TransferOfFundsDocument.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        /*Options options = SpringServiceLocator.getOptionsService().getCurrentYearOptions();

        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        if (isExpense(accountingLine)) {
            explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferExpenseCode());
        }
        else {
            if (isIncome(accountingLine)) {
                explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferIncomeCode());
            }
            else {
                explicitEntry.setFinancialObjectTypeCode(TransactionalDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(accountingLine));
            }
        }*/
    }

    /**
     * adds the following restrictions in addtion to those provided by
     * <code>IsDebitUtils.isDebitConsideringNothingPositiveOnly</code>
     * <ol>
     * <li> only allow income or expense object type codes
     * <li> target lines have the oposite debit/credit codes as the source lines
     * </ol>
     * 
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // only allow income or expense
        /*if (!isIncome(accountingLine) && !isExpense(accountingLine)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
        }
        else {
            throw new IllegalStateException(IsDebitUtils.isInvalidLineTypeIllegalArgumentExceptionMessage);
        }

        return isDebit;*/
        
        return true;
    }

    /**
     * Overrides to check balances across mandator transfers and non-mandatory transfers. Also checks balances across fund groups.
     * 
     * @see TransactionalDocumentRuleBase#isDocumentBalanceValid(TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        
        /*
        boolean isValid = super.isDocumentBalanceValid(transactionalDocument);

        TransferOfFundsDocument tofDoc = (TransferOfFundsDocument) transactionalDocument;
        // make sure accounting lines balance across mandatory and non-mandatory transfers
        if (isValid) {
            isValid = isMandatoryTransferTotalAndNonMandatoryTransferTotalBalanceValid(tofDoc);
        }

        // make sure accounting lines for a TOF balance across agency and clearing fund groups - IU specific
        if (isValid) {
            isValid = isFundGroupsBalanceValid(tofDoc);
        }

        return isValid;
        */
        
        return true;
    }

    /**
     * This document specific routing business rule check calls the check that makes sure that the budget year is consistent for all
     * accounting lines.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        /*
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        TransferOfFundsDocument tofDoc = (TransferOfFundsDocument) document;

        if (isValid) {
            isValid = isAllAccountingLinesMatchingBudgetYear(tofDoc);
        }

        return isValid;
        */
        
        return true;
    }

    /**
     * This is a helper method that wraps the fund group balancing check. This check can be configured by updating the APC that is
     * associated with this check. See the document's specification for details.
     * 
     * @param tofDoc
     * @return boolean
     */
    /*private boolean isFundGroupsBalanceValid(TransferOfFundsDocument tofDoc) {
        String[] fundGroupCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, APPLICATION_PARAMETER.FUND_GROUP_BALANCING_SET);
        return isFundGroupSetBalanceValid(tofDoc, fundGroupCodes);
    }*/

    /**
     * This method checks the sum of all of the "From" accounting lines with mandatory transfer object codes against the sum of all
     * of the "To" accounting lines with mandatory transfer object codes. In addition, it does the same, but for accounting lines
     * with non-mandatory transfer object code. This is to enforce the rule that the document must balance within the object code
     * object sub-type codes of mandatory transfers and non-mandatory transfers.
     * 
     * @param tofDoc
     * @return True if they balance; false otherwise.
     */
    /*private boolean isMandatoryTransferTotalAndNonMandatoryTransferTotalBalanceValid(TransferOfFundsDocument tofDoc) {
        List lines = new ArrayList();

        lines.addAll(tofDoc.getSourceAccountingLines());
        lines.addAll(tofDoc.getTargetAccountingLines());

        // sum the from lines.
        KualiDecimal mandatoryTransferFromAmount = new KualiDecimal(0);
        KualiDecimal nonMandatoryTransferFromAmount = new KualiDecimal(0);
        KualiDecimal mandatoryTransferToAmount = new KualiDecimal(0);
        KualiDecimal nonMandatoryTransferToAmount = new KualiDecimal(0);

        for (Iterator i = lines.iterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            String objectSubTypeCode = line.getObjectCode().getFinancialObjectSubTypeCode();

            if (isNonMandatoryTransfersSubType(objectSubTypeCode)) {
                if (line.isSourceAccountingLine()) {
                    nonMandatoryTransferFromAmount = nonMandatoryTransferFromAmount.add(line.getAmount());
                }
                else {
                    nonMandatoryTransferToAmount = nonMandatoryTransferToAmount.add(line.getAmount());
                }
            }
            else if (isMandatoryTransfersSubType(objectSubTypeCode)) {
                if (line.isSourceAccountingLine()) {
                    mandatoryTransferFromAmount = mandatoryTransferFromAmount.add(line.getAmount());
                }
                else {
                    mandatoryTransferToAmount = mandatoryTransferToAmount.add(line.getAmount());
                }
            }
        }

        // check that the amounts balance across mandatory transfers and
        // non-mandatory transfers
        boolean isValid = true;

        if (mandatoryTransferFromAmount.compareTo(mandatoryTransferToAmount) != 0) {
            isValid = false;
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", KeyConstants.ERROR_DOCUMENT_TOF_MANDATORY_TRANSFERS_DO_NOT_BALANCE);
        }

        if (nonMandatoryTransferFromAmount.compareTo(nonMandatoryTransferToAmount) != 0) {
            isValid = false;
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", KeyConstants.ERROR_DOCUMENT_TOF_NON_MANDATORY_TRANSFERS_DO_NOT_BALANCE);
        }

        return isValid;
    }*/

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code is either Mandatory Transfer or
     * Non-Mandatory Transfer. This is called by the parent's processAddAccountingLine() method.
     * 
     * @param accountingLine
     * @return True if the object code's object sub-type code is a mandatory or non-mandatory transfer; false otherwise.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        /*accountingLine.refreshReferenceObject("objectCode");
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

        // make sure a object sub type code exists for this object code
        if (StringUtils.isBlank(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_IS_NULL, accountingLine.getFinancialObjectCode());
            return false;
        }

        if (!isMandatoryTransfersSubType(objectSubTypeCode) && !isNonMandatoryTransfersSubType(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_NOT_MANDATORY_OR_NON_MANDATORY_TRANSFER, new String[] { accountingLine.getObjectCode().getFinancialObjectSubType().getFinancialObjectSubTypeName(), accountingLine.getFinancialObjectCode() });
            return false;
        }*/

        return true;
    }
}