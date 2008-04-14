/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch.closing.year.util;

import java.sql.Date;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.EncumbranceForwardStep;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.util.OriginEntryOffsetPair;

/**
 * A helper to create origin entries to carry forward different types of encumbrances.
 */

public class EncumbranceClosingOriginEntryFactory {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceClosingOriginEntryFactory.class);

    /**
     * Create a pair of cost share entries, one explicit and one offset to carry forward an encumbrance after validating the
     * encumbrance.
     * 
     * @param encumbrance the encumbrance to create origin entry and offset for
     * @param transactionDate the date all origin entries should have as their transaction date
     * @return a cost share entry/offset pair to carry forward the given encumbrance.
     */
    static final public OriginEntryOffsetPair createCostShareBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Date transactionDate) {

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        OffsetDefinitionService offsetDefinitionService = SpringContext.getBean(OffsetDefinitionService.class);
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        final String GL_ACLO = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        final String GL_ORIGINATION_CODE = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE);

        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Generate the entry ...

        OriginEntryFull entry = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());

        String description = encumbrance.getTransactionEncumbranceDescription();
        String fromDesc = "FR-" + encumbrance.getChartOfAccountsCode() + encumbrance.getAccountNumber();
        int descLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        if ((description.length() + fromDesc.length()) < descLength) {
            int padLength = descLength - (description.length() + fromDesc.length());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < padLength; i++) {
                sb.append(' ');
            }
            sb.append(fromDesc);
            fromDesc = sb.toString();
            description += fromDesc;
        }
        else if ((description.length() + fromDesc.length()) > descLength) {
            description = description.substring(0, (descLength - fromDesc.length())) + fromDesc;
        }
        else {
            description += fromDesc;
        }
        entry.setTransactionLedgerEntryDescription(description);

        // SpringContext is used because this method is static.
        A21SubAccountService a21SubAccountService = SpringContext.getBean(A21SubAccountService.class);
        A21SubAccount a21SubAccount = a21SubAccountService.getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber());

        entry.setUniversityFiscalYear(new Integer(encumbrance.getUniversityFiscalYear().intValue() + 1));
        entry.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        entry.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        entry.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());

        // The subAccountNumber is set to dashes in the OriginEntryFull constructor.
        if (entry.getSubAccountNumber() == null || KFSConstants.EMPTY_STRING.equals(entry.getSubAccountNumber().trim())) {
            entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        entry.setFinancialObjectCode(encumbrance.getObjectCode());
        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_COST_SHARE_ENCUMBRANCE);

        ObjectCode finObjCode = objectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        entry.setFinancialObjectTypeCode(finObjCode.getFinancialObjectTypeCode());

        entry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(0));
        entry.setDocumentNumber(encumbrance.getDocumentNumber());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_COST_SHARE_ENCUMBRANCE);

        KualiDecimal delta = encumbrance.getAccountLineEncumbranceAmount().subtract(encumbrance.getAccountLineEncumbranceClosedAmount());
        if (delta.isPositive()) {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            entry.setTransactionLedgerEntryAmount(delta);
        }
        else {
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            entry.setTransactionLedgerEntryAmount(delta.negated());
        }
        entry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);
        entry.setTransactionDate(transactionDate);

        pair.setEntry(entry);

        // And now the offset ...

        OriginEntryFull offset = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());
        final String GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = parameterService.getParameterValue(EncumbranceForwardStep.class, GLConstants.EncumbranceClosingOriginEntry.GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
        offset.setTransactionLedgerEntryDescription(GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

        offset.setUniversityFiscalYear(new Integer(encumbrance.getUniversityFiscalYear().intValue() + 1));
        offset.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        offset.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        offset.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());
        if (offset.getSubAccountNumber() == null || KFSConstants.EMPTY_STRING.equals(offset.getSubAccountNumber().trim())) {
            offset.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        // Lookup the offset definition for the explicit entry we just created.
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialDocumentTypeCode(), entry.getFinancialBalanceTypeCode());
        // Set values from the offset definition if it was found.
        if (null != offsetDefinition) {

            offset.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            offset.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else { // Log an exception if the offset definition was not found.

            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t" + "- OFFSET DEFINITION NOT FOUND\n\t" + "- ERROR ACCESSING OFSD TABLE");
            pair.setFatalErrorFlag(true);
            return pair;

        }
        offset.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_COST_SHARE_ENCUMBRANCE);
        // Validate the object code for the explicit entry.
        ObjectCode objectCode = objectCodeService.getByPrimaryId(offset.getUniversityFiscalYear(), offset.getChartOfAccountsCode(), offset.getFinancialObjectCode());
        if (null != objectCode) {
            offset.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        }
        else {
            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t" + "- NO OBJECT FOR OBJECT ON OFSD\n\t" + "- ERROR ACCESSING OBJECT TABLE");
            pair.setFatalErrorFlag(true);
            return pair;
        }
        offset.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        offset.setDocumentNumber(encumbrance.getDocumentNumber());
        offset.setTransactionLedgerEntrySequenceNumber(new Integer(0));
        if (delta.isPositive()) {
            offset.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offset.setTransactionLedgerEntryAmount(delta);
        }
        else {
            offset.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offset.setTransactionLedgerEntryAmount(delta.negated());
        }

        offset.setTransactionEncumbranceUpdateCode(null);
        offset.setOrganizationDocumentNumber(null);
        offset.setProjectCode(KFSConstants.getDashProjectCode());
        offset.setTransactionDate(transactionDate);
        offset.setOrganizationReferenceId(null);
        offset.setReferenceFinancialDocumentTypeCode(null);
        offset.setReferenceFinancialSystemOriginationCode(null);
        offset.setReferenceFinancialDocumentNumber(null);
        offset.setReversalDate(null);
        
        SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(offset);

        pair.setOffset(offset);

        return pair;

    }

    /**
     * Create a pair of OriginEntries, one explicit and one offset to carry forward an encumbrance.
     * 
     * @param encumbrance the encumbrance to create origin entries for
     * @param closingFiscalYear the fiscal year that's closing
     * @param transactionDate the transaction date these entries should have
     * @return a entry/offset pair for the given encumbrance
     */
    static final public OriginEntryOffsetPair createBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Integer closingFiscalYear, Date transactionDate) {

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Build the entry ...
        OriginEntryFull entry = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());

        Integer thisFiscalYear = new Integer(closingFiscalYear.intValue() + 1);
        entry.setUniversityFiscalYear(thisFiscalYear);
        entry.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        entry.setAccountNumber(encumbrance.getAccountNumber());
        entry.setSubAccountNumber(encumbrance.getSubAccountNumber());

        // SpringContext is used because this method is static.
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getObjectCode());

        if (null != objectCode) {

            entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

            if (null != objectCode.getNextYearFinancialObjectCode() && !KFSConstants.EMPTY_STRING.equals(objectCode.getNextYearFinancialObjectCode().trim())) {

                entry.setFinancialObjectCode(objectCode.getNextYearFinancialObjectCode());

            }
            else {

                entry.setFinancialObjectCode(encumbrance.getObjectCode());

            }

        }
        else {

            LOG.info("FATAL ERROR: ERROR ACCESSING OBJECT TABLE FOR CAOBJT-FIN-OBJECT-CD");
            pair.setFatalErrorFlag(true);
            return pair;

        }

        // SpringContext is used because this method is static.
        SubObjectCodeService subObjectCodeService = SpringContext.getBean(SubObjectCodeService.class);
        SubObjCd subObjectCode = subObjectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getObjectCode(), encumbrance.getSubObjectCode());

        if (null != subObjectCode) {

            entry.setFinancialSubObjectCode(subObjectCode.getFinancialSubObjectCode());

        }
        else {

            entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

        }

        entry.setFinancialBalanceTypeCode(encumbrance.getBalanceTypeCode());
        entry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        entry.setDocumentNumber(encumbrance.getDocumentNumber());
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        entry.setTransactionLedgerEntryDescription(encumbrance.getTransactionEncumbranceDescription());
        entry.setTransactionLedgerEntryAmount(encumbrance.getAccountLineEncumbranceAmount().subtract(encumbrance.getAccountLineEncumbranceClosedAmount()));

        if (entry.getTransactionLedgerEntryAmount().isNegative()) {

            entry.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount().negated());
            entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

        }
        else {

            entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

        }

        entry.setTransactionDate(transactionDate);
        entry.setOrganizationDocumentNumber(null);
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        entry.setOrganizationReferenceId(null);
        entry.setReferenceFinancialDocumentTypeCode(null);
        entry.setReferenceFinancialSystemOriginationCode(null);
        entry.setReferenceFinancialDocumentNumber(null);
        entry.setReversalDate(null);
        entry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);

        pair.setEntry(entry);

        final String OBJECT_CODE_FOR_BALANCE_TYPE_INTERNAL_ENCUMBRANCE = parameterService.getParameterValue(EncumbranceForwardStep.class, GLConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_INTERNAL_ENCUMBRANCE);
        final String OBJECT_CODE_FOR_BALANCE_TYPE_PRE_ENCUMBRANCE = parameterService.getParameterValue(EncumbranceForwardStep.class, GLConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_PRE_ENCUMBRANCE);
        final String OBJECT_CODE_FOR_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE = parameterService.getParameterValue(EncumbranceForwardStep.class, GLConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_EXTERNAL_ENCUMBRANCE);
        final String BEGINNING_FUND_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = parameterService.getParameterValue(EncumbranceForwardStep.class, GLConstants.EncumbranceClosingOriginEntry.BEGINNING_FUND_BALANCE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

        // And now build the offset.
        OriginEntryFull offset = new OriginEntryFull(entry);
        offset.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount());
        // KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE case...
        offset.setFinancialObjectCode(OBJECT_CODE_FOR_BALANCE_TYPE_INTERNAL_ENCUMBRANCE);

        if (KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE.equals(entry.getFinancialBalanceTypeCode())) {

            offset.setFinancialObjectCode(OBJECT_CODE_FOR_BALANCE_TYPE_PRE_ENCUMBRANCE);

        }
        else if (KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE.equals(entry.getFinancialBalanceTypeCode())) {

            offset.setFinancialObjectCode(OBJECT_CODE_FOR_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE);

        }

        offset.setFinancialObjectTypeCode(SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinObjectTypeFundBalanceCd());
        offset.setTransactionLedgerEntryDescription(BEGINNING_FUND_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

        if (KFSConstants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {

            offset.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

        }
        else {

            offset.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

        }
        SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(offset);
        
        pair.setOffset(offset);

        return pair;

    }

}
