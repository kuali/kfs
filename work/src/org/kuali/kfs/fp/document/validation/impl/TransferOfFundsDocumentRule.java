/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.module.financial.document.TransferOfFundsDocument;

/**
 * Business rule(s) applicable to Transfer of Funds documents.
 */
public class TransferOfFundsDocumentRule extends AccountingDocumentRuleBase implements TransferOfFundsDocumentRuleConstants {

    /**
     * Overrides to check balances across mandatory transfers and non-mandatory transfers. Also checks balances across fund groups.
     * 
     * @param financialDocument The document to retrieve the balance from and validate against.
     * @return True if the document balance is valid based on a collection of validation checks performed, false otherwise.
     * 
     * @see FinancialDocumentRuleBase#isDocumentBalanceValid(FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        boolean isValid = super.isDocumentBalanceValid(financialDocument);

        TransferOfFundsDocument tofDoc = (TransferOfFundsDocument) financialDocument;
        // make sure accounting lines balance across mandatory and non-mandatory transfers
        if (isValid) {
            isValid = isMandatoryTransferTotalAndNonMandatoryTransferTotalBalanceValid(tofDoc);
        }

        // make sure accounting lines for a TOF balance across agency and clearing fund groups - IU specific
        if (isValid) {
            isValid = isFundGroupsBalanceValid(tofDoc);
        }

        return isValid;
    }

    /**
     * This is a helper method that wraps the fund group balancing check. This check can be configured by updating the 
     * application parameter table that is associated with this check. See the document's specification for details.
     * 
     * @param tofDoc The transfer of funds document the fund groups will be pulled from and validated.
     * @return True if the fund group balance if valid, false otherwise.
     * 
     * @see #isFundGroupSetBalanceValid(TransferOfFundsDocument)
     */
    private boolean isFundGroupsBalanceValid(TransferOfFundsDocument tofDoc) {
        return isFundGroupSetBalanceValid(tofDoc, TransferOfFundsDocument.class, APPLICATION_PARAMETER.FUND_GROUP_BALANCING_SET);
    }

    /**
     * This method checks the sum of all of the "From" accounting lines with mandatory transfer object codes against the sum of all
     * of the "To" accounting lines with mandatory transfer object codes. In addition, it does the same, but for accounting lines
     * with non-mandatory transfer object code. This is to enforce the rule that the document must balance within the object code
     * object sub-type codes of mandatory transfers and non-mandatory transfers.
     * 
     * @param tofDoc The transfer of funds document to be validated.
     * @return True if they balance; false otherwise.
     */
    private boolean isMandatoryTransferTotalAndNonMandatoryTransferTotalBalanceValid(TransferOfFundsDocument tofDoc) {
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

        // check that the amounts balance across mandatory transfers and non-mandatory transfers
        boolean isValid = true;

        if (mandatoryTransferFromAmount.compareTo(mandatoryTransferToAmount) != 0) {
            isValid = false;
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", KFSKeyConstants.ERROR_DOCUMENT_TOF_MANDATORY_TRANSFERS_DO_NOT_BALANCE);
        }

        if (nonMandatoryTransferFromAmount.compareTo(nonMandatoryTransferToAmount) != 0) {
            isValid = false;
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", KFSKeyConstants.ERROR_DOCUMENT_TOF_NON_MANDATORY_TRANSFERS_DO_NOT_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides the parent to make sure that the chosen object code's object sub-type code is either Mandatory Transfer or
     * Non-Mandatory Transfer. This is called by the parent's processAddAccountingLine() method.
     * 
     * @param documentClass A value required to override this method, but one that is not used in this class, so null can be passed.
     * @param accountingLine The accounting line the object code will be retrieved from for validation.
     * @return True if the object code's object sub-type code is a mandatory or non-mandatory transfer; false otherwise.
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isObjectSubTypeAllowed(Class, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject("objectCode");
        String objectSubTypeCode = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

        // make sure a object sub type code exists for this object code
        if (StringUtils.isBlank(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_IS_NULL, accountingLine.getFinancialObjectCode());
            return false;
        }

        if (!isMandatoryTransfersSubType(objectSubTypeCode) && !isNonMandatoryTransfersSubType(objectSubTypeCode)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_NOT_MANDATORY_OR_NON_MANDATORY_TRANSFER, new String[] { accountingLine.getObjectCode().getFinancialObjectSubType().getFinancialObjectSubTypeName(), accountingLine.getFinancialObjectCode() });
            return false;
        }

        return true;
    }

    /**
     * Overrides the parent to make sure that the chosen object code's object code is Income/Expense.
     * 
     * @param accountingLine The accounting line the object code will be retrieved from and validated.
     * @return True if the object code is income or expense, otherwise false.
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isObjectCodeAllowed(Class, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectCodeAllowed(Class documentClass, AccountingLine accountingLine) {
        boolean isObjectCodeAllowed = super.isObjectCodeAllowed(documentClass, accountingLine);

        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!isDebitUtils.isIncome(accountingLine) && !isDebitUtils.isExpense(accountingLine)) {
            GlobalVariables.getErrorMap().putError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_TOF_INVALID_OBJECT_TYPE_CODES, new String[] { accountingLine.getObjectCode().getFinancialObjectTypeCode(), accountingLine.getObjectCode().getFinancialObjectSubTypeCode() });
            isObjectCodeAllowed = false;
        }

        return isObjectCodeAllowed;
    }
}