/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalExpenseAsTemExpenseWrapper;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TemExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ImportedCTSExpenseServiceImpl extends ExpenseServiceBase implements TemExpenseService {

    private static Logger LOG = Logger.getLogger(ImportedCTSExpenseServiceImpl.class);

    protected ImportedExpensePendingEntryService importedExpensePendingEntryService;
    protected TravelExpenseService travelExpenseService;
    protected DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TemExpense> expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (TemExpense temExpense : expenses) {

            if (temExpense instanceof ImportedExpense) {
                ImportedExpense expense = (ImportedExpense)temExpense;

                String cardType = expense.getCardType();
                if (cardType != null && cardType.equals(TemConstants.TRAVEL_TYPE_CTS) && !expense.getNonReimbursable()){
                    expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
                    String financialObjectCode= "";
                    expense.getExpenseTypeObjectCode();
                    if (expense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.AIRFARE)){
                        financialObjectCode= getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, TemConstants.AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_AIRFARE_OBJECT_CODE);
                    }
                    else if (expense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.LODGING)){
                        financialObjectCode= getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, TemConstants.AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_LODGING_OBJECT_CODE);
                    }
                    else if (expense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.RENTAL_CAR)){
                        financialObjectCode= getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, TemConstants.AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_RENTAL_CAR_OBJECT_CODE);
                    }

                    LOG.debug("Refreshed importedExpense with expense type code " + expense.getExpenseTypeObjectCode().getExpenseTypeCode() + " and financialObjectCode " + financialObjectCode);

                    final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, expense.getExpenseTypeObjectCode().getFinancialObjectCode());
                    if (objCode != null && expense.getExpenseTypeObjectCode() != null && !expense.getExpenseTypeObjectCode().getExpenseType().isPrepaidExpense()){
                        AccountingDistribution distribution = null;
                        String key = objCode.getCode() + "-" + TemConstants.TRAVEL_TYPE_CTS;
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(cardType);
                            distribution.setRemainingAmount(expense.getConvertedAmount());
                            distribution.setSubTotal(expense.getConvertedAmount());
                            distributionMap.put(key, distribution);
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TemExpense> getExpenseDetails(TravelDocument document) {
        final List<ImportedExpense> importedExpenses = document.getImportedExpenses();
        Set<Long> importedHistoricalExpenseIds = new HashSet<Long>();
        List<TemExpense> ctsExpenses = new ArrayList<TemExpense>();
        for (ImportedExpense expense : importedExpenses) {
            if (StringUtils.equals(expense.getCardType(), TemConstants.TRAVEL_TYPE_CTS)) {
                ctsExpenses.add(expense);
                importedHistoricalExpenseIds.add(expense.getHistoricalTravelExpenseId());
            }
        }
        // now include all HistoricalExpenses hung on the document
        final List<HistoricalTravelExpense> hungExpenses = document.getHistoricalTravelExpenses();
        for (HistoricalTravelExpense expense : hungExpenses) {
            if (StringUtils.equals(expense.getCreditCardAgency().getTravelCardTypeCode(), TemConstants.TRAVEL_TYPE_CTS) && !importedHistoricalExpenseIds.contains(expense.getId())) {
                ctsExpenses.add(new HistoricalExpenseAsTemExpenseWrapper(expense));
            }
        }
        return ctsExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#validateExpenseCalculation(org.kuali.kfs.module.tem.businessobject.TemExpense)
     */
    @Override
    public boolean validateExpenseCalculation(TemExpense expense){
        return (expense instanceof ImportedExpense)
                && StringUtils.defaultString(((ImportedExpense)expense).getCardType()).equals(TemConstants.TRAVEL_TYPE_CTS) || (expense instanceof HistoricalExpenseAsTemExpenseWrapper && StringUtils.equals(((HistoricalExpenseAsTemExpenseWrapper)expense).getCardType(), TemConstants.TRAVEL_TYPE_CTS));
    }

    /**
     * Used to create GLPE's for CTS imports.
     *
     * compares the entered accounting lines with what has been created in the trip account info table.
     *
     * if there are differences, create a credit glpe for the original account and a debit for the new one.
     *
     * If no change, original account info is correct and nothing needs to be done.
     */
    @Override
    public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper){

        String distributionIncomeAndExpenseDocumentType = getDataDictionaryService().getDocumentTypeNameByClass(DistributionOfIncomeAndExpenseDocument.class);

        //build map of the accounting line info and amount
        List<TemSourceAccountingLine> lines = travelDocument.getSourceAccountingLines();
        Map<AccountingInfoKey,KualiDecimal> accountingLineMap = new HashMap<AccountingInfoKey, KualiDecimal>();
        for (TemSourceAccountingLine line : lines){
           if (line.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)){
               AccountingInfoKey key = new AccountingInfoKey(line);
               KualiDecimal amount = line.getAmount();
               if (accountingLineMap.containsKey(key)){
                   amount = accountingLineMap.get(key).add(line.getAmount());
               }
               accountingLineMap.put(key, amount);
           }
        }
        //build map of expected amounts from CTS expenses
        Map<AccountingInfoKey,KualiDecimal> tripAccountMap = new HashMap<AccountingInfoKey, KualiDecimal>();
        for (HistoricalTravelExpense historicalTravelExpense : travelDocument.getHistoricalTravelExpenses()){
            //get historical travel expenses that are CTS for this document.
            if (travelDocument.getDocumentNumber().equals(historicalTravelExpense.getDocumentNumber())
                    && historicalTravelExpense.getAgencyStagingData() != null
                    && historicalTravelExpense.getReconciled().equals(TemConstants.ReconciledCodes.UNRECONCILED)){
                for (TripAccountingInformation tripAccountingInformation : historicalTravelExpense.getAgencyStagingData().getTripAccountingInformation()){
                    AccountingInfoKey key = new AccountingInfoKey(tripAccountingInformation, historicalTravelExpense);
                    KualiDecimal amount = tripAccountingInformation.getAmount();
                    if (amount == null) {
                        amount = historicalTravelExpense.getAmount().divide(new KualiDecimal(historicalTravelExpense.getAgencyStagingData().getTripAccountingInformation().size()));
                    }
                    if (tripAccountMap.containsKey(key)){
                        amount = tripAccountMap.get(key).add(amount);
                    }
                    tripAccountMap.put(key, amount);
                }
            }
        }
        /*
         * Iterate through imported expense accounts and match them to accounting line accounts.
         * process any changes by creating a new credit glpe
         */
        for (AccountingInfoKey key : tripAccountMap.keySet()) {
            if (!accountingLineMap.containsKey(key) || accountingLineMap.get(key).isLessThan(tripAccountMap.get(key))){
                //There is a difference in the accounts used
                //Either the account was completely switched, or was supplemented with another account.

                //create the credit glpe
                TemSourceAccountingLine creditLine = new TemSourceAccountingLine();
                creditLine.setChartOfAccountsCode(key.getChartOfAccountsCode());
                creditLine.setAccountNumber(key.getAccountNumber());
                creditLine.setSubAccountNumber(key.getSubAccountNumber());

                final ExpenseTypeObjectCode expenseTypeObjectCode = getExpenseTypeObjectCode(travelDocument, key.getHistoricalTravelExpense());
                creditLine.setFinancialObjectCode(expenseTypeObjectCode.getFinancialObjectCode());

                creditLine.setProjectCode(key.getProjectCode());
                creditLine.setOrganizationReferenceId(key.getOrganizationReferenceId());
                KualiDecimal amount = (accountingLineMap.get(key) == null?tripAccountMap.get(key):tripAccountMap.get(key).subtract(accountingLineMap.get(key)));
                creditLine.setAmount(amount);
                creditLine.setReferenceOriginCode(TemConstants.ORIGIN_CODE);
                importedExpensePendingEntryService.generateDocumentImportedExpenseGeneralLedgerPendingEntries(travelDocument, creditLine, sequenceHelper, true, distributionIncomeAndExpenseDocumentType);

                accountingLineMap.remove(key);
            }
        }

        /*
         * Iterate through the rest of the accounting lines.
         * Create normal debit glpe's.
         */
        for (AccountingInfoKey key : accountingLineMap.keySet()){
            TemSourceAccountingLine debitLine = new TemSourceAccountingLine();
            debitLine.setChartOfAccountsCode(key.getChartOfAccountsCode());
            debitLine.setAccountNumber(key.getAccountNumber());
            debitLine.setSubAccountNumber(key.getSubAccountNumber());
            debitLine.setFinancialObjectCode(key.getFinancialObjectCode());
            debitLine.setFinancialSubObjectCode(key.getFinancialSubObjectCode());
            debitLine.setProjectCode(key.getProjectCode());
            debitLine.setOrganizationReferenceId(key.getOrganizationReferenceId());
            debitLine.setAmount(accountingLineMap.get(key));
            debitLine.setReferenceOriginCode(TemConstants.ORIGIN_CODE);
            importedExpensePendingEntryService.generateDocumentImportedExpenseGeneralLedgerPendingEntries(travelDocument, debitLine, sequenceHelper, false, distributionIncomeAndExpenseDocumentType);
        }
    }

    /**
     * Looks up the expense type object for the given TravelDocument and HistoricalTravelExpense
     * @param document the document to find an expense type object code for
     * @param expense the historical travel expense, which has agency staging data that has an expense type
     * @return the expense type object code
     */
    protected ExpenseTypeObjectCode getExpenseTypeObjectCode(TravelDocument document, HistoricalTravelExpense expense) {
        final TemConstants.ExpenseTypeMetaCategory expenseTypeCategory = expense.getAgencyStagingData().getExpenseTypeCategory();
        final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(expenseTypeCategory);
        final String documentType = document.getDocumentTypeName();
        final String tripType = StringUtils.isBlank(document.getTripTypeCode()) ? TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE : document.getTripTypeCode();
        final String travelerType = ObjectUtils.isNull(document.getTraveler()) || StringUtils.isBlank(document.getTraveler().getTravelerTypeCode()) ? TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE : document.getTraveler().getTravelerTypeCode();
        final ExpenseTypeObjectCode expenseTypeObjectCode = getTravelExpenseService().getExpenseType(expenseType.getCode(), documentType, tripType, travelerType);
        return expenseTypeObjectCode;
    }

    /**
     * Key which represents (and holds) fields of either a TripAccountingInformation or TemSourceAccountingLine, so that they can be matched against each other
     */
    class AccountingInfoKey {
        protected String chartOfAccountsCode;
        protected String accountNumber;
        protected String subAccountNumber;
        protected String financialObjectCode;
        protected String financialSubObjectCode;
        protected String projectCode;
        protected String organizationReferenceId;
        protected HistoricalTravelExpense historicalTravelExpense;

        public AccountingInfoKey(TripAccountingInformation info, HistoricalTravelExpense expense) {
            chartOfAccountsCode = info.getTripChartCode();
            accountNumber = info.getTripAccountNumber();
            subAccountNumber = info.getTripSubAccountNumber();
            projectCode = info.getProjectCode();
            organizationReferenceId = info.getOrganizationReference();
            historicalTravelExpense = expense;
        }

        public AccountingInfoKey(TemSourceAccountingLine accountingLine) {
            chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
            accountNumber = accountingLine.getAccountNumber();
            subAccountNumber = accountingLine.getSubAccountNumber();
            financialObjectCode = accountingLine.getFinancialObjectCode();
            financialSubObjectCode = accountingLine.getFinancialSubObjectCode();
            projectCode = accountingLine.getProjectCode();
            organizationReferenceId = accountingLine.getOrganizationReferenceId();
        }

        public String getChartOfAccountsCode() {
            return chartOfAccountsCode;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getSubAccountNumber() {
            if (subAccountNumber == null) {
                return KFSConstants.EMPTY_STRING;
            }
            return subAccountNumber;
        }

        public String getFinancialObjectCode() {
            return financialObjectCode;
        }

        public String getFinancialSubObjectCode() {
            if (financialSubObjectCode == null) {
                return KFSConstants.EMPTY_STRING;
            }
            return financialSubObjectCode;
        }

        public String getProjectCode() {
            if (projectCode == null) {
                return KFSConstants.EMPTY_STRING;
            }
            return projectCode;
        }

        public String getOrganizationReferenceId() {
            if (organizationReferenceId == null) {
                return KFSConstants.EMPTY_STRING;
            }
            return organizationReferenceId;
        }

        public HistoricalTravelExpense getHistoricalTravelExpense() {
            return historicalTravelExpense;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof AccountingInfoKey)) {
                return false;
            }
            AccountingInfoKey key = (AccountingInfoKey)o;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(getChartOfAccountsCode(), key.getChartOfAccountsCode());
            equalsBuilder.append(getAccountNumber(), key.getAccountNumber());
            equalsBuilder.append(getSubAccountNumber(), key.getSubAccountNumber());
            equalsBuilder.append(getProjectCode(), key.getProjectCode());
            equalsBuilder.append(getOrganizationReferenceId(), key.getOrganizationReferenceId());
            return equalsBuilder.isEquals();
        }

        @Override
        public int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(getChartOfAccountsCode());
            hashCodeBuilder.append(getAccountNumber());
            hashCodeBuilder.append(getSubAccountNumber());
            hashCodeBuilder.append(getProjectCode());
            hashCodeBuilder.append(getOrganizationReferenceId());
            return hashCodeBuilder.toHashCode();
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void updateExpense(TravelDocument travelDocument) {
        List<HistoricalTravelExpense> historicalTravelExpenses = travelDocument.getHistoricalTravelExpenses();
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            if (historicalTravelExpense.getAgencyStagingDataId() != null && (StringUtils.isBlank(historicalTravelExpense.getReconciled()) || StringUtils.equals(historicalTravelExpense.getReconciled(), TemConstants.ReconciledCodes.UNRECONCILED))) {
                long time = (new java.util.Date()).getTime();
                historicalTravelExpense.setReconciliationDate(new Date(time));
                historicalTravelExpense.setReconciled(TemConstants.ReconciledCodes.RECONCILED);
            }
        }
        getBusinessObjectService().save(historicalTravelExpenses);
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }

    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
