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

import java.util.Iterator;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 * 
 * 
 */
public class BenefitExpenseTransferDocumentRule extends AccountingDocumentRuleBase {

    public BenefitExpenseTransferDocumentRule() {
    }   
    
    protected boolean AddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);
    }
    

    /**
     * Set attributes of an offset pending entry according to rules specific to TransferOfFundsDocument.
     * 
     * @see org.kuali.module.financial.rules.AccountingDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean customizeOffsetGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        //offsetEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        return true;
    }

    /**
     * adds the following restrictions in addtion to those provided by
     * <code>IsDebitUtils.isDebitConsideringNothingPositiveOnly</code>
     * <ol>
     * <li> only allow income or expense object type codes
     * <li> target lines have the oposite debit/credit codes as the source lines
     * </ol>
     * 
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        // only allow income or expense
        /*if (!isIncome(accountingLine) && !isExpense(accountingLine)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
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
     * @see AccountingDocumentRuleBase#isDocumentBalanceValid(AccountingDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument accountingDocument) {
        
        /*
        boolean isValid = super.isDocumentBalanceValid(accountingDocument);

        TransferOfFundsDocument tofDoc = (TransferOfFundsDocument) accountingDocument;
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
    

    private String createPayFYPeriodKey(Integer payFiscalYear, String payPeriodCode){
    
        StringBuffer payFYPeriodKey = new StringBuffer();
        
        payFYPeriodKey.append(payFiscalYear);
        payFYPeriodKey.append(payPeriodCode);
        
        return payFYPeriodKey.toString();
    }
    
    
    private boolean compareAccountingLineTotalsByPayFYAndPayPeriod(Map sourceLinesMap, Map targetLinesMap){
    
        boolean isValid = true;
        Map.Entry entry = null;
        String currentKey = null;
        KualiDecimal sourceLinesAmount = new KualiDecimal(0);
        KualiDecimal targetLinesAmount = new KualiDecimal(0);

        
        //Loop through source lines comparing against target lines
        for(Iterator i=sourceLinesMap.entrySet().iterator(); i.hasNext() && isValid;){
            //initialize
            entry = (Map.Entry)i.next();
            currentKey = (String)entry.getKey();
            sourceLinesAmount = (KualiDecimal)entry.getValue();
            
            if( targetLinesMap.containsKey( currentKey ) ){
                targetLinesAmount = (KualiDecimal)targetLinesMap.get(currentKey);

                if ( sourceLinesAmount.compareTo(targetLinesAmount) != 0 ) {
                    isValid = false;                
                }

            }else{
                isValid = false;                
            }            
        }
        
        /* Now loop through target lines comparing against source lines.
         * This finds missing entries from either direction (source or target)
         */
        for(Iterator i=targetLinesMap.entrySet().iterator(); i.hasNext() && isValid;){
            //initialize
            entry = (Map.Entry)i.next();
            currentKey = (String)entry.getKey();
            targetLinesAmount = (KualiDecimal)entry.getValue();
            
            if( sourceLinesMap.containsKey( currentKey ) ){
                sourceLinesAmount = (KualiDecimal)sourceLinesMap.get(currentKey);

                if ( targetLinesAmount.compareTo(sourceLinesAmount) != 0 ) {
                    isValid = false;                                
                }
                
            }else{
                isValid = false;                
            }            
        }
        
        
        return isValid;    
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

    public boolean isCredit(AccountingLine accountingLine, AccountingDocument financialDocument) {
        // TODO Auto-generated method stub
        return false;
    }
}