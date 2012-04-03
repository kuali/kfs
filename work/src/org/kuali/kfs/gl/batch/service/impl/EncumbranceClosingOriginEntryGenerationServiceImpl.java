/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.A21SubAccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.EncumbranceForwardStep;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.EncumbranceClosingOriginEntryGenerationService;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * The default implementation of the EncumbranceClosingOriginEntryGenerationService
 */
public class EncumbranceClosingOriginEntryGenerationServiceImpl implements EncumbranceClosingOriginEntryGenerationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceClosingOriginEntryGenerationServiceImpl.class);
    protected ParameterService parameterService;
    protected OffsetDefinitionService offsetDefinitionService;
    protected ObjectCodeService objectCodeService;
    protected DataDictionaryService dataDictionaryService;
    protected FlexibleOffsetAccountService flexibleOffsetAccountService;
    protected A21SubAccountService a21SubAccountService;
    protected SubObjectCodeService subObjectCodeService;
    protected OptionsService optionsService;
    protected SubFundGroupService subFundGroupService;
    protected BusinessObjectService businessObjectService;
    protected AccountingCycleCachingService accountingCycleCachingService;

    /**
     * @see org.kuali.kfs.gl.batch.service.EncumbranceClosingOriginEntryGenerationService#createBeginningBalanceEntryOffsetPair(org.kuali.kfs.gl.businessobject.Encumbrance, java.lang.Integer, java.sql.Date)
     */
    @Override
    public OriginEntryOffsetPair createCostShareBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Date transactionDate) {
        final String GL_ACLO = getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        final String GL_ORIGINATION_CODE = getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE);

        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Generate the entry ...

        OriginEntryFull entry = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());

        String description = encumbrance.getTransactionEncumbranceDescription();
        String fromDesc = "FR-" + encumbrance.getChartOfAccountsCode() + encumbrance.getAccountNumber();
        int descLength = getDataDictionaryService().getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
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
        A21SubAccount a21SubAccount = getA21SubAccountService().getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber());

        entry.setUniversityFiscalYear(new Integer(encumbrance.getUniversityFiscalYear().intValue() + 1));
        entry.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        entry.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        entry.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());

        // The subAccountNumber is set to dashes in the OriginEntryFull constructor.
        if (entry.getSubAccountNumber() == null || KFSConstants.EMPTY_STRING.equals(entry.getSubAccountNumber().trim())) {
            entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

//        ObjectCode finObjCode = accountingCycleCachingService.getObjectCode(encumbrance.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
//        if (finObjCode != null)
//            entry.setFinancialObjectTypeCode(finObjCode.getFinancialObjectTypeCode());
//

        ObjectCode encumbranceObjectCode = accountingCycleCachingService.getObjectCode(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), encumbrance.getObjectCode());

        if (null != encumbranceObjectCode) {

            String financialObjectLevelCode = encumbranceObjectCode.getFinancialObjectLevelCode();
            String financialObjectCode = encumbrance.getObjectCode();

            String overriddenObjectCode = overrideCostShareObjectCode(financialObjectLevelCode, financialObjectCode);
            final ObjectCode overriddenObject = this.getAccountingCycleCachingService().getObjectCode(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), overriddenObjectCode);

            String param = parameterService.getSubParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, overriddenObject.getFinancialObjectLevelCode());
            if (param == null) {
                param = parameterService.getSubParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, "DEFAULT");
                if (param == null) {
                    throw new RuntimeException("Unable to determine cost sharing object code from object level (" + overriddenObject.getFinancialObjectLevelCode() + ").  Default entry missing.");
                }
            }
            financialObjectCode = param;

         // Lookup the new object code
            ObjectCode newObjectCode = accountingCycleCachingService.getObjectCode(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), financialObjectCode);
            if (newObjectCode != null) {
                entry.setFinancialObjectTypeCode(newObjectCode.getFinancialObjectTypeCode());
                entry.setFinancialObjectCode(financialObjectCode);
            }
            else {
                LOG.error("Error retrieving ObjectCode("+entry.getUniversityFiscalYear()+"/"+entry.getChartOfAccountsCode()+"/"+financialObjectCode+")");
                pair.setFatalErrorFlag(true);
                return pair;
            }
        } else {

            LOG.error("Error retrieving ObjectCode("+entry.getUniversityFiscalYear()+"/"+entry.getChartOfAccountsCode()+"/"+entry.getFinancialObjectCode()+")");
            pair.setFatalErrorFlag(true);
            return pair;

        }


        entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_COST_SHARE_ENCUMBRANCE);

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
        entry.setProjectCode(KFSConstants.getDashProjectCode());
        entry.setTransactionDate(transactionDate);

        pair.setEntry(entry);

        // And now the offset ...

        OriginEntryFull offset = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());
        final String GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = getParameterService().getParameterValueAsString(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
        offset.setTransactionLedgerEntryDescription(GENERATED_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

        offset.setUniversityFiscalYear(new Integer(encumbrance.getUniversityFiscalYear().intValue() + 1));
        offset.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        offset.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        offset.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());
        if (offset.getSubAccountNumber() == null || KFSConstants.EMPTY_STRING.equals(offset.getSubAccountNumber().trim())) {
            offset.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        // Lookup the offset definition for the explicit entry we just created.
        OffsetDefinition offsetDefinition = getOffsetDefinitionService().getByPrimaryId(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getFinancialDocumentTypeCode(), entry.getFinancialBalanceTypeCode());
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
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(offset.getUniversityFiscalYear(), offset.getChartOfAccountsCode(), offset.getFinancialObjectCode());
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

        getFlexibleOffsetAccountService().updateOffset(offset);

        pair.setOffset(offset);

        return pair;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.EncumbranceClosingOriginEntryGenerationService#createCostShareBeginningBalanceEntryOffsetPair(org.kuali.kfs.gl.businessobject.Encumbrance, java.sql.Date)
     */
    @Override
    public OriginEntryOffsetPair createBeginningBalanceEntryOffsetPair(Encumbrance encumbrance, Integer closingFiscalYear, Date transactionDate) {
        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();

        // Build the entry ...
        OriginEntryFull entry = new OriginEntryFull(encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode());

        Integer thisFiscalYear = new Integer(closingFiscalYear.intValue() + 1);
        entry.setUniversityFiscalYear(thisFiscalYear);
        entry.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        entry.setAccountNumber(encumbrance.getAccountNumber());
        entry.setSubAccountNumber(encumbrance.getSubAccountNumber());

        ObjectCode objectCode = accountingCycleCachingService.getObjectCode(entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), encumbrance.getObjectCode());

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

            LOG.error("Error retrieving ObjectCode("+entry.getUniversityFiscalYear()+"/"+entry.getChartOfAccountsCode()+"/"+entry.getFinancialObjectCode()+")");
            pair.setFatalErrorFlag(true);
            return pair;

        }

        SubObjectCode subObjectCode = getSubObjectCodeService().getByPrimaryId(encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getObjectCode(), encumbrance.getSubObjectCode());

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

        final String OBJECT_CODE_FOR_BALANCE_TYPE_INTERNAL_ENCUMBRANCE = getParameterService().getParameterValueAsString(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_INTERNAL_ENCUMBRANCE);
        final String OBJECT_CODE_FOR_BALANCE_TYPE_PRE_ENCUMBRANCE = getParameterService().getParameterValueAsString(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_PRE_ENCUMBRANCE);
        final String OBJECT_CODE_FOR_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE = getParameterService().getParameterValueAsString(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.OFFSET_OBJECT_CODE_FOR_EXTERNAL_ENCUMBRANCE);
        final String BEGINNING_FUND_TRANSACTION_LEDGER_ENTRY_DESCRIPTION = getParameterService().getParameterValueAsString(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.BEGINNING_FUND_BALANCE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

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

        offset.setFinancialObjectTypeCode(getOptionsService().getCurrentYearOptions().getFinObjectTypeFundBalanceCd());
        offset.setTransactionLedgerEntryDescription(BEGINNING_FUND_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);

        if (KFSConstants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {

            offset.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

        }
        else {

            offset.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

        }
        getFlexibleOffsetAccountService().updateOffset(offset);

        pair.setOffset(offset);

        return pair;
    }

    /**
     * Determine whether or not an encumbrance should be carried forward from one fiscal year to the next.
     *
     * @param encumbrance the encumbrance to qualify
     * @return true if the encumbrance should be rolled forward from the closing fiscal year to the opening fiscal year.
     */
    @Override
    public boolean shouldForwardEncumbrance(Encumbrance encumbrance) {
        // null guard
        if (null == encumbrance) {
            return false;
        }

        if (encumbrance.getAccountLineEncumbranceAmount().equals(encumbrance.getAccountLineEncumbranceClosedAmount())) {
            return false;
        }

        if (getEncumbranceBalanceTypeCodes().contains(encumbrance.getBalanceTypeCode())) {

            ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.FORWARD_ENCUMBRANCE_BALANCE_TYPE_AND_ORIGIN_CODE,encumbrance.getBalanceTypeCode(),  encumbrance.getOriginCode());
            if (!evaluator.evaluationSucceeds()) {
                return false;
            }
            else if (KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE.equals(encumbrance.getBalanceTypeCode())) {
                // pre-encumbrances are forwarded, but only if they're related to contracts and grants accounts
                PriorYearAccount priorYearAccount = retrievePriorYearAccount(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());
                // the account on the encumbrance must be valid
                if (null == priorYearAccount) {
                    LOG.info("No prior year account for chart \"" + encumbrance.getChartOfAccountsCode() + "\" and account \"" + encumbrance.getAccountNumber() + "\"");
                    return false;
                }
                // the sub fund group must exist for the prior year account and the
                // encumbrance must not be closed.
                return priorYearAccount.isForContractsAndGrants();
            }
            else {
                // we're still here? because we're an external encumbrance, and we always get forwarded
                return true;
            }
        }
        // we're still here? because we're not of a valid encumbrance balance type; we don't get forwarded
        return false;

    }

    /**
     * @return a list of BalanceType codes which correspond to encumbrance balance types
     */
    protected List<String> getEncumbranceBalanceTypeCodes() {
        List<String> balanceTypeCodes = new ArrayList<String>();


        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("active", Boolean.TRUE);
        keys.put("finBalanceTypeEncumIndicator", Boolean.TRUE);
        Collection balanceTypes = businessObjectService.findMatching(BalanceType.class, keys);
        for (Object balanceTypeAsObject : balanceTypes) {
            ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(EncumbranceForwardStep.class, GeneralLedgerConstants.EncumbranceClosingOriginEntry.FORWARDING_ENCUMBRANCE_BALANCE_TYPES, ((BalanceType)balanceTypeAsObject).getCode());
            if (evaluator.evaluationSucceeds())
                balanceTypeCodes.add(((BalanceType)balanceTypeAsObject).getCode());
        }

        return balanceTypeCodes;
    }

    /**
     * Determine whether or not the encumbrance has been fully relieved.
     *
     * @param encumbrance the encumbrance to qualify
     * @return true if the amount closed on the encumbrance is NOT equal to the amount of the encumbrance itself, e.g. if the
     *         encumbrance has not yet been paid off.
     */
    @Override
    public boolean isEncumbranceClosed(Encumbrance encumbrance) {
        if (encumbrance.getAccountLineEncumbranceAmount().doubleValue() == encumbrance.getAccountLineEncumbranceClosedAmount().doubleValue()) {
            return false;
        }
        return true;
    }

    /**
     * Do some validation and make sure that the encumbrance A21SubAccount is a cost share sub-account.
     *
     * @param entry not used in this implementation
     * @param offset not used in this implementation
     * @param encumbrance the encumbrance whose A21SubAccount must be qualified
     * @param objectTypeCode the object type code of the generated entries
     * @return true if the encumbrance is eligible for cost share.
     * @throws FatalErrorException thrown if a given A21SubAccount, SubFundGroup, or PriorYearAccount record is not found in the database
     */
    @Override
    public boolean shouldForwardCostShareForEncumbrance(OriginEntryFull entry, OriginEntryFull offset, Encumbrance encumbrance, String objectTypeCode) throws FatalErrorException {
        PriorYearAccount priorYearAccount = retrievePriorYearAccount(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());

        // the sub fund group for the prior year account must exist.
        String subFundGroupCode = null;
        if (null != priorYearAccount) {
            subFundGroupCode = priorYearAccount.getSubFundGroupCode();
        }
        else {
            // this message was carried over from the cobol.
            throw new FatalErrorException("ERROR ACCESSING PRIOR YR ACCT TABLE FOR " + encumbrance.getAccountNumber());
        }

        SubFundGroup subFundGroup = getSubFundGroupService().getByPrimaryId(subFundGroupCode);
        if (null != subFundGroup) {
            if (!priorYearAccount.isForContractsAndGrants()) {
                return false;
            }
        }
        else {
            throw new FatalErrorException("ERROR ACCESSING SUB FUND GROUP TABLE FOR " + subFundGroupCode);
        }

        // I think this is redundant to the statement a few lines above here.
        // In any case, the sub fund group must not be contracts and grants.
        if (!priorYearAccount.isForContractsAndGrants()) {
            return false;
        }

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> expenseObjectCodeTypes = objectTypeService.getCurrentYearExpenseObjectTypes();

        String[] encumbranceBalanceTypeCodes = new String[] { KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE, KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE };

        // the object type code must be an expense and the encumbrance balance type code must correspond to an internal, external or
        // pre-encumbrance
        if (!expenseObjectCodeTypes.contains(objectTypeCode) || !ArrayUtils.contains(encumbranceBalanceTypeCodes, encumbrance.getBalanceTypeCode())) {
            return false;
        }
        else if (!encumbrance.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
            A21SubAccount a21SubAccount = getA21SubAccountService().getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber());
            if (null == a21SubAccount) {
                // Error message carried over from cobol. not very well descriptive.
                // Just indicates that the a21 sub account doesn't exist.
                throw new FatalErrorException("ERROR ACCESSING A21 SUB ACCOUNT TABLE FOR ENCUMBRANCE " + encumbrance.getChartOfAccountsCode() + "-" + encumbrance.getAccountNumber() + " " + encumbrance.getSubAccountNumber());
            }
            // everything is valid, return true if the a21 sub account is a cost share sub-account
            return KFSConstants.SubAccountType.COST_SHARE.equals(a21SubAccount.getSubAccountTypeCode());
        }
        else {
            return false;
        }

    }

    /**
     * Retrieves a prior year account from the persistence store
     * @param chartOfAccountsCode the chart of accounts for the prior year account
     * @param accountNumber the account number for the prior year account
     * @return the PriorYearAccount
     */
    protected PriorYearAccount retrievePriorYearAccount(String chartOfAccountsCode, String accountNumber) {
        Map pks = new HashMap();
        pks.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        pks.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);

        return (PriorYearAccount)this.getBusinessObjectService().findByPrimaryKey(PriorYearAccount.class, pks);
    }

    /**
     *
     * This method eases the institutional customization for Cost Sharing Object Codes for OriginEntries
     * @param levelCode of the originEntry
     * @param objectCode of the originEntry
     * @return the new objectCode
     */

    protected String overrideCostShareObjectCode(String levelCode, String objectCode){
        return objectCode;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the offsetDefinitionService attribute.
     * @return Returns the offsetDefinitionService.
     */
    public OffsetDefinitionService getOffsetDefinitionService() {
        return offsetDefinitionService;
    }

    /**
     * Sets the offsetDefinitionService attribute value.
     * @param offsetDefinitionService The offsetDefinitionService to set.
     */
    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    /**
     * Gets the objectCodeService attribute.
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Gets the dataDictionaryService attribute.
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the flexibleOffsetAccountService attribute.
     * @return Returns the flexibleOffsetAccountService.
     */
    public FlexibleOffsetAccountService getFlexibleOffsetAccountService() {
        return flexibleOffsetAccountService;
    }

    /**
     * Sets the flexibleOffsetAccountService attribute value.
     * @param flexibleOffsetAccountService The flexibleOffsetAccountService to set.
     */
    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    /**
     * Gets the a21SubAccountService attribute.
     * @return Returns the a21SubAccountService.
     */
    public A21SubAccountService getA21SubAccountService() {
        return a21SubAccountService;
    }

    /**
     * Sets the a21SubAccountService attribute value.
     * @param subAccountService The a21SubAccountService to set.
     */
    public void setA21SubAccountService(A21SubAccountService subAccountService) {
        a21SubAccountService = subAccountService;
    }

    /**
     * Gets the subObjectCodeService attribute.
     * @return Returns the subObjectCodeService.
     */
    public SubObjectCodeService getSubObjectCodeService() {
        return subObjectCodeService;
    }

    /**
     * Sets the subObjectCodeService attribute value.
     * @param subObjectCodeService The subObjectCodeService to set.
     */
    public void setSubObjectCodeService(SubObjectCodeService subObjectCodeService) {
        this.subObjectCodeService = subObjectCodeService;
    }

    /**
     * Gets the optionsService attribute.
     * @return Returns the optionsService.
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * Sets the optionsService attribute value.
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Gets the subFundGroupService attribute.
     * @return Returns the subFundGroupService.
     */
    public SubFundGroupService getSubFundGroupService() {
        return subFundGroupService;
    }

    /**
     * Sets the subFundGroupService attribute value.
     * @param subFundGroupService The subFundGroupService to set.
     */
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the accountingCycleCachingService attribute.
     * @return Returns the accountingCycleCachingService.
     */
    public AccountingCycleCachingService getAccountingCycleCachingService() {
        return accountingCycleCachingService;
    }

    /**
     * Sets the accountingCycleCachingService attribute value.
     * @param accountingCycleCachingService The accountingCycleCachingService to set.
     */
    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

}
