/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class helps generate the entries for the nominal activity closing year end job.
 */
public class NominalActivityClosingHelper {
    private Integer fiscalYear;
    private Date transactionDate;
    private String varNetExpenseObjectCode;
    private String varNetRevenueObjectCode;
    private String varFundBalanceObjectCode;
    private String varFundBalanceObjectTypeCode;
    private String[] expenseObjectCodeTypes;
    private ParameterService parameterService;
    private ConfigurationService configurationService;
    private FlexibleOffsetAccountService flexibleOffsetService;
    private Logger LOG = Logger.getLogger(NominalActivityClosingHelper.class);
    private int nonFatalErrorCount;
    private List<String> varCharts;

    /**
     * Constructs a NominalActivityClosingHelper
     * @param fiscalYear the fiscal year this job is being run for
     * @param transactionDate the transaction date that origin entries should hit the ledger
     * @param parameterService an implementation of the ParameterService
     * @param configurationService an implementation of the ConfigurationService
     */
    public NominalActivityClosingHelper(Integer fiscalYear, Date transactionDate, ParameterService parameterService, ConfigurationService configurationService, ObjectTypeService objectTypeService) {
        this.fiscalYear = fiscalYear;
        this.transactionDate = transactionDate;
        this.parameterService = parameterService;
        this.configurationService = configurationService;
        this.flexibleOffsetService = SpringContext.getBean(FlexibleOffsetAccountService.class);
        this.nonFatalErrorCount = 0;

        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(fiscalYear);
        expenseObjectCodeTypes = objectTypes.toArray(new String[0]);

        varNetExpenseObjectCode = parameterService.getParameterValueAsString(NominalActivityClosingStep.class, "NET_EXPENSE_OBJECT_CODE");
        varNetRevenueObjectCode = parameterService.getParameterValueAsString(NominalActivityClosingStep.class, "NET_REVENUE_OBJECT_CODE");
        varFundBalanceObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM);
        varFundBalanceObjectTypeCode = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_TYPE_PARM);

        //Obtain list of charts to close from Parameter ANNUAL_CLOSING_CHARTS_PARAM.
        //If no parameter value exists, act on all charts which is the default action in the delivered foundation code.
        varCharts = new ArrayList<String>();

        Collection<String> annualClosingCharts = parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_CHARTS_PARAM);

        if (ObjectUtils.isNotNull(annualClosingCharts) && (!annualClosingCharts.isEmpty())) {
            //transfer charts from parameter to List
            varCharts.addAll(annualClosingCharts);

            LOG.info("NominalActivityClosingJob ANNUAL_CLOSING_CHARTS parameter value = " + varCharts.toString());
        }

    }

    /**
     * Generates an origin entry that will summarize close out of nominal items (income and expense)
     * @param balance the balance this activity closing entry needs to be created for
     * @param sequenceNumber the sequence number of the origin entry
     * @return an origin entry which will close out nominal activity on a balance
     * @throws FatalErrorException thrown if the given balance lacks an object type code
     */
    public OriginEntryFull generateActivityEntry(Balance balance, Integer sequenceNumber) throws FatalErrorException {

        OriginEntryFull activityEntry = new OriginEntryFull();

        activityEntry.setUniversityFiscalYear(fiscalYear);
        activityEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
        activityEntry.setAccountNumber(balance.getAccountNumber());
        activityEntry.setSubAccountNumber(balance.getSubAccountNumber());

        if (ObjectHelper.isOneOf(balance.getObjectTypeCode(), expenseObjectCodeTypes)) {
            activityEntry.setFinancialObjectCode(varNetExpenseObjectCode);
        }
        else {
            activityEntry.setFinancialObjectCode(varNetRevenueObjectCode);
        }

        activityEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        activityEntry.setFinancialBalanceTypeCode(balance.getOption().getNominalFinancialBalanceTypeCd());

        if (null == balance.getObjectTypeCode()) {
            throw new FatalErrorException(" BALANCE SELECTED FOR PROCESSING IS MISSING ITS OBJECT TYPE CODE ");

        }

        activityEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());
        activityEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        activityEntry.setFinancialDocumentTypeCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));
        activityEntry.setFinancialSystemOriginationCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));
        activityEntry.setDocumentNumber(new StringBuffer(balance.getOption().getActualFinancialBalanceTypeCd()).append(balance.getAccountNumber()).toString());
        activityEntry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);

        if (ObjectHelper.isOneOf(balance.getObjectTypeCode(), expenseObjectCodeTypes)) {
            activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_EXPENSE), balance));
        }
        else {
            activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_REVENUE), balance));
        }

        activityEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount());
        activityEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

        String debitCreditCode = null;

        if (null != balance.getObjectType()) {

            if (ObjectHelper.isOneOf(balance.getObjectType().getFinObjectTypeDebitcreditCd(), new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {
                debitCreditCode = balance.getObjectType().getFinObjectTypeDebitcreditCd();
            }
            else {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }

            // NOTE (laran) The amount on the origin entry is set to this value above.
            // NOTE (laran) I'm using the balance value here because to me it was easier than remembering the
            // indirection.
            if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                if (ObjectHelper.isOneOf(balance.getObjectType().getFinObjectTypeDebitcreditCd(), new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {
                    activityEntry.setTransactionDebitCreditCode(balance.getObjectType().getFinObjectTypeDebitcreditCd());
                }
                else {
                    activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                }

            }
            else {
                if (KFSConstants.GL_CREDIT_CODE.equals(balance.getObjectType().getFinObjectTypeDebitcreditCd())) {
                    activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                }
                else {
                    if (KFSConstants.GL_DEBIT_CODE.equals(balance.getObjectType().getFinObjectTypeDebitcreditCd())) {
                        activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                    }
                    else {
                        activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                    }
                }
            }
        }
        else {
            if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {
                activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            nonFatalErrorCount += 1;

            LOG.info(new StringBuffer("FIN OBJ TYP CODE ").append(balance.getObjectTypeCode()).append(" NOT IN TABLE").toString());

        }

        activityEntry.setTransactionDate(transactionDate);
        activityEntry.setOrganizationDocumentNumber(null);
        activityEntry.setProjectCode(KFSConstants.getDashProjectCode());
        activityEntry.setOrganizationReferenceId(null);
        activityEntry.setReferenceFinancialDocumentTypeCode(null);
        activityEntry.setReferenceFinancialSystemOriginationCode(null);
        activityEntry.setReferenceFinancialDocumentNumber(null);
        activityEntry.setReversalDate(null);
        activityEntry.setTransactionEncumbranceUpdateCode(null);

        if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {
            activityEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().negated());
        }

        return activityEntry;

    }

    /**
     * Genereates an origin entry to update a fund balance as a result of closing income and expense
     * @param balance the balance this offset needs to be created for
     * @param sequenceNumber the sequence number of the origin entry full
     * @return an origin entry which will offset the nominal closing activity
     * @throws FatalErrorException thrown if the given balance lacks an object type code
     */
    public OriginEntryFull generateOffset(Balance balance, Integer sequenceNumber) throws FatalErrorException {
        String debitCreditCode = null;

        if (null == balance.getObjectTypeCode()) {
            throw new FatalErrorException(" BALANCE SELECTED FOR PROCESSING IS MISSING ITS OBJECT TYPE CODE ");

        }

        if (ObjectHelper.isOneOf(balance.getObjectType().getFinObjectTypeDebitcreditCd(), new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {
            debitCreditCode = balance.getObjectType().getFinObjectTypeDebitcreditCd();
        }
        else {
            debitCreditCode = KFSConstants.GL_CREDIT_CODE;
        }

        OriginEntryFull offsetEntry = new OriginEntryFull();
        offsetEntry.setUniversityFiscalYear(fiscalYear);
        offsetEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
        offsetEntry.setAccountNumber(balance.getAccountNumber());
        offsetEntry.setSubAccountNumber(balance.getSubAccountNumber());
        offsetEntry.setFinancialObjectCode(varFundBalanceObjectCode);
        offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        offsetEntry.setFinancialBalanceTypeCode(balance.getOption().getNominalFinancialBalanceTypeCd());
        offsetEntry.setFinancialObjectTypeCode(varFundBalanceObjectTypeCode);
        offsetEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);
        offsetEntry.setFinancialDocumentTypeCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));
        offsetEntry.setFinancialSystemOriginationCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));
        offsetEntry.setDocumentNumber(new StringBuffer(balance.getOption().getActualFinancialBalanceTypeCd()).append(balance.getAccountNumber()).toString());
        offsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceNumber.intValue()));
        offsetEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_FUND_BALANCE), balance));
        offsetEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount());
        offsetEntry.setTransactionDebitCreditCode(debitCreditCode);
        offsetEntry.setTransactionDate(transactionDate);
        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinancialDocumentTypeCode(null);
        offsetEntry.setReferenceFinancialSystemOriginationCode(null);
        offsetEntry.setReferenceFinancialDocumentNumber(null);
        offsetEntry.setFinancialDocumentReversalDate(null);
        offsetEntry.setTransactionEncumbranceUpdateCode(null);

        if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {
            if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
                offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            else {
                offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }

        }

        if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {
            offsetEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().negated());
        }

        flexibleOffsetService.updateOffset(offsetEntry);

        return offsetEntry;
    }

    /**
     * Adds the job parameters used to generate the origin entries to the given map
     * @param nominalClosingJobParameters a map of batch job parameters to add nominal activity closing parameters to
     */
    public void addNominalClosingJobParameters(Map nominalClosingJobParameters) {
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.NET_EXP_OBJECT_CD, varNetExpenseObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.NET_REV_OBJECT_CD, varNetRevenueObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.FUND_BAL_OBJECT_CD, varFundBalanceObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.FUND_BAL_OBJ_TYP_CD, varFundBalanceObjectTypeCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE, varCharts);
    }

    /**
     * Generates the transaction ledger entry description for a given balance
     *
     * @param descriptorIntro the introduction to the description
     * @param balance the balance the transaction description will refer to
     * @return the generated transaction ledger entry description
     */
    private String createTransactionLedgerEntryDescription(String descriptorIntro, Balance balance) {
        StringBuilder description = new StringBuilder();
        description.append(descriptorIntro.trim()).append(' ');
        return description.append(getSizedField(5, balance.getSubAccountNumber())).append("-").append(getSizedField(4, balance.getObjectCode())).append("-").append(getSizedField(3, balance.getSubObjectCode())).append("-").append(getSizedField(2, balance.getObjectTypeCode())).toString();
    }

    /**
     * Pads out a string so that it will be a certain length
     *
     * @param size the size to pad to
     * @param value the String being padded
     * @return the padded String
     */
    private StringBuilder getSizedField(int size, String value) {
        StringBuilder fieldString = new StringBuilder();
        if (value != null) {
            fieldString.append(value);
            while (fieldString.length() < size) {
                fieldString.append(' ');
            }
        }
        else {
            while (fieldString.length() < size) {
                fieldString.append('-');
            }
        }
        return fieldString;
    }

    /**
     * Returns the count of non-fatal errors encountered during the process by this helper
     * @return the count of non-fatal errors
     */
    public Integer getNonFatalErrorCount() {
        return new Integer(this.nonFatalErrorCount);
    }

    /**
     * Returns the boolean from the chart parameter list being empty
     * @return isEmpty boolean value for chart List
     */
    public boolean isAnnualClosingChartParamterBlank(){
        return varCharts.isEmpty();
    }

}
