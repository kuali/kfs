/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

import org.kuali.Constants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.util.OriginEntryOffsetPair;

/**
 * A helper to create origin entries to carry forward different types of encumbrances.
 * 
 */

public class EncumbranceClosingOriginEntryFactory {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceClosingOriginEntryFactory.class);

    /**
     * Create a pair of cost share entries, one explicit and one offset to carry forward an encumbrance after validating the
     * encumbrance.
     * 
     * @param encumbrance
     * @param debitCreditCode
     * @return a cost share entry/offset pair to carry forward the given encumbrance.
     */
    static final public OriginEntryOffsetPair createCostShareBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, String debitCreditCode) {

        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Generate the entry ...

        OriginEntry entry = new OriginEntry("ACLO", "MF");

        String description = encumbrance.getTransactionEncumbranceDescription();
        description += "FR-" + encumbrance.getChartOfAccountsCode() + encumbrance.getAccountNumber();
        entry.setTransactionLedgerEntryDescription(description);

        // SpringServiceLocator is used because this method is static.
        A21SubAccountService a21SubAccountService = SpringServiceLocator.getA21SubAccountService();
        A21SubAccount a21SubAccount = a21SubAccountService.getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber());

        entry.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        entry.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        entry.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());

        // The subAccountNumber is set to dashes in the OriginEntry constructor.
        if ("".equals(encumbrance.getSubAccountNumber().trim())) {

            entry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

        }

        entry.setFinancialBalanceTypeCode("CE");
        entry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        if (null == debitCreditCode || "".equals(debitCreditCode.trim())) {

            if (encumbrance.getAccountLineEncumbranceAmount().isPositive()) {

                entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

            }

        }

        // If the debit/credit code is set on the
        if (null == debitCreditCode || "".equals(debitCreditCode.trim())) {

            if (encumbrance.getAccountLineEncumbranceAmount().isNegative()) {

                entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                entry.setTransactionLedgerEntryAmount(encumbrance.getAccountLineEncumbranceAmount().negated());

            }

        }

        pair.setEntry(entry);

        // And now the offset ...

        OriginEntry offset = new OriginEntry("ACLO", "MF");
        offset.setTransactionLedgerEntryDescription("GENERATED OFFSET");

        // Lookup the offset definition for the explicit entry we just created.
        // SpringServiceLocator is used because this method is static.
        OffsetDefinitionService offsetDefinitionService = SpringServiceLocator.getOffsetDefinitionService();
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialDocumentTypeCode(), entry.getFinancialBalanceTypeCode());

        // Set values from the offset definition if it was found.
        if (null != offsetDefinition) {

            offset.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            offset.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        }
        else { // Log an exception if the offset definition was not found.

            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t" + "- OFFSET DEFINITION NOT FOUND\n\t" + "- ERROR ACCESSING OFSD TABLE");
            pair.setFatalErrorFlag(true);
            return pair;

        }

        // Validate the object code for the explicit entry.
        // SpringServiceLocator is used because this method is static.
        ObjectCodeService objectCodeService = SpringServiceLocator.getObjectCodeService();
        ObjectCode objectCode = objectCodeService.getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());

        if (null != objectCode) {

            offset.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

        }
        else {

            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t" + "- NO OBJECT FOR OBJECT ON OFSD\n\t" + "- ERROR ACCESSING OBJECT TABLE");
            pair.setFatalErrorFlag(true);
            return pair;

        }

        // If the explicit entry is a credit, make the offset a debit and vice/versa.
        if (Constants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {

            offset.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

        }
        else {

            offset.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

        }

        offset.setTransactionEncumbranceUpdateCode(null);
        offset.setOrganizationDocumentNumber(null);
        offset.setProjectCode(Constants.DASHES_PROJECT_CODE);
        offset.setOrganizationReferenceId(null);
        offset.setReferenceFinancialDocumentTypeCode(null);
        offset.setReferenceFinancialSystemOriginationCode(null);
        offset.setReferenceFinancialDocumentNumber(null);
        offset.setReversalDate(null);

        pair.setOffset(offset);

        return pair;

    }

    /**
     * Create a pair of OriginEntries, one explicit and one offset to carry forward an encumbrance.
     * 
     * @param encumbrance
     * @param closingFiscalYear
     * @param transactionDate
     * @return a entry/offset pair for the given encumbrance
     */
    static final public OriginEntryOffsetPair createBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Integer closingFiscalYear, Date transactionDate) {

        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Build the entry ...
        OriginEntry entry = new OriginEntry("ACLO", "MF");

        Integer thisFiscalYear = new Integer(closingFiscalYear.intValue() + 1);
        entry.setUniversityFiscalYear(thisFiscalYear);
        entry.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        entry.setAccountNumber(encumbrance.getAccountNumber());
        entry.setSubAccountNumber(encumbrance.getSubAccountNumber());

        // SpringServiceLocator is used because this method is static.
        ObjectCodeService objectCodeService = SpringServiceLocator.getObjectCodeService();
        ObjectCode objectCode = objectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getObjectCode());

        if (null != objectCode) {

            entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

            if (null != objectCode.getNextYearFinancialObjectCode() && !"".equals(objectCode.getNextYearFinancialObjectCode().trim())) {

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

        // SpringServiceLocator is used because this method is static.
        SubObjectCodeService subObjectCodeService = SpringServiceLocator.getSubObjectCodeService();
        SubObjCd subObjectCode = subObjectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getObjectCode(), encumbrance.getSubObjectCode());

        if (null != subObjectCode) {

            entry.setFinancialSubObjectCode(subObjectCode.getFinancialSubObjectCode());

        }
        else {

            entry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

        }

        entry.setFinancialBalanceTypeCode(encumbrance.getBalanceTypeCode());
        entry.setUniversityFiscalPeriodCode("BB");
        entry.setFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        entry.setFinancialSystemOriginationCode(encumbrance.getOriginCode());
        entry.setDocumentNumber(encumbrance.getDocumentNumber());
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        entry.setTransactionLedgerEntryDescription(encumbrance.getTransactionEncumbranceDescription());
        entry.setTransactionLedgerEntryAmount(encumbrance.getAccountLineEncumbranceAmount().subtract(encumbrance.getAccountLineEncumbranceClosedAmount()));

        if (entry.getTransactionLedgerEntryAmount().isNegative()) {

            entry.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount().negated());
            entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

        }
        else {

            entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

        }

        entry.setTransactionDate(transactionDate);
        entry.setOrganizationDocumentNumber(null);
        entry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        entry.setOrganizationReferenceId(null);
        entry.setReferenceFinancialDocumentTypeCode(null);
        entry.setReferenceFinancialSystemOriginationCode(null);
        entry.setReferenceFinancialDocumentNumber(null);
        entry.setReversalDate(null);
        entry.setTransactionEncumbranceUpdateCode(Constants.ENCUMB_UPDT_DOCUMENT_CD);

        pair.setEntry(entry);

        // And now build the offset.
        OriginEntry offset = new OriginEntry(entry);
        offset.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount());
        offset.setFinancialObjectCode("9891");

        if ("PE".equals(entry.getFinancialBalanceTypeCode())) {

            offset.setFinancialObjectCode("9890");

        }
        else if ("EX".equals(entry.getFinancialBalanceTypeCode())) {

            offset.setFinancialObjectCode("9892");

        }

        offset.setFinancialObjectTypeCode(SpringServiceLocator.getOptionsService().getCurrentYearOptions().getFinObjectTypeFundBalanceCd());
        offset.setTransactionLedgerEntryDescription("BEGINNING FUND BALANCE OFFSET");

        if (Constants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {

            offset.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

        }
        else {

            offset.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

        }

        pair.setOffset(offset);

        return pair;

    }

}
