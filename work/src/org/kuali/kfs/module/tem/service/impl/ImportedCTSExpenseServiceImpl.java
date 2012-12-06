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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DataDictionaryService;

public class ImportedCTSExpenseServiceImpl extends ExpenseServiceBase implements TEMExpenseService {

    private static Logger LOG = Logger.getLogger(ImportedCTSExpenseServiceImpl.class);

    ImportedExpensePendingEntryService importedExpensePendingEntryService;

    /**
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (ImportedExpense expense : (List<ImportedExpense>) expenses) {

            String cardType = expense.getCardType();
            if (cardType != null && cardType.equals(TemConstants.TRAVEL_TYPE_CTS) && !expense.getNonReimbursable()){
                expense.refreshReferenceObject(TemPropertyConstants.TRAVEL_EXEPENSE_TYPE_CODE);
                String financialObjectCode= "";
                expense.getTravelExpenseTypeCode();
                if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.AIRFARE)){
                    financialObjectCode= getParameterService().getParameterValueAsString(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_AIR_OBJECT_CODE);
                }
                else if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.LODGING)){
                    financialObjectCode= getParameterService().getParameterValueAsString(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_LODGING_OBJECT_CODE);
                }
                else if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.RENTAL_CAR)){
                    financialObjectCode= getParameterService().getParameterValueAsString(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_RENTAL_CAR_OBJECT_CODE);
                }

                LOG.debug("Refreshed importedExpense with expense type code " + expense.getTravelExpenseTypeCode() + " and financialObjectCode " + financialObjectCode);

                final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, expense.getTravelExpenseTypeCode().getFinancialObjectCode());
                if (objCode != null && expense.getTravelExpenseTypeCode() != null && !expense.getTravelExpenseTypeCode().isPrepaidExpense()){
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

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TEMExpense> getExpenseDetails(TravelDocument document) {
        return document.getImportedExpenses();
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#validateExpenseCalculation(org.kuali.kfs.module.tem.businessobject.TEMExpense)
     */
    @Override
    public boolean validateExpenseCalculation(TEMExpense expense){
        return (expense instanceof ImportedExpense)
                && StringUtils.defaultString(((ImportedExpense)expense).getCardType()).equals(TemConstants.TRAVEL_TYPE_CTS);
    }

    /**
     * Used to create GLPE's for CTS imports.
     *
     * compares the entered accounting lines with what has been created in the trip acccount info table.
     *
     * if there are differences, create a credit glpe for the original account and a debit for the new one.
     *
     * If no change, original account info is correct and nothing needs to be done.
     */
    @Override
    public void processExpense(TravelDocument travelDocument){

        String distributionIncomeAndExpenseDocumentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(DistributionOfIncomeAndExpenseDocument.class);

        //build map of the accounting line info and amount
        List<TemSourceAccountingLine> lines = travelDocument.getSourceAccountingLines();
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(travelDocument.getGeneralLedgerPendingEntries().size()+1);
        Map<String,KualiDecimal> accountingLineMap = new HashMap<String, KualiDecimal>();
        for (TemSourceAccountingLine line : lines){
           if (line.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)){
               String key = line.getChartOfAccountsCode() + "_"
                   + line.getAccountNumber() + "_"
                   + line.getSubAccountNumber() + "_"
                   + line.getFinancialObjectCode() + "_"
                   + line.getFinancialSubObjectCode() + "_"
                   + line.getProjectCode() + "_"
                   + line.getOrganizationReferenceId();
               KualiDecimal amount = line.getAmount();
               if (accountingLineMap.containsKey(key)){
                   amount = accountingLineMap.get(key).add(line.getAmount());
               }
               accountingLineMap.put(key, amount);
           }
        }
        //build map of expected amounts from CTS expenses
        Map<String,KualiDecimal> tripAccountMap = new HashMap<String, KualiDecimal>();
        for (HistoricalTravelExpense historicalTravelExpense : travelDocument.getHistoricalTravelExpenses()){
            //get historical travel expenses that are CTS for this document.
            if (travelDocument.getDocumentNumber().equals(historicalTravelExpense.getDocumentNumber())
                    && historicalTravelExpense.getAgencyStagingData() != null
                    && historicalTravelExpense.getReconciled().equals(TemConstants.ReconciledCodes.UNRECONCILED)){
                for (TripAccountingInformation tripAccountingInformation : historicalTravelExpense.getAgencyStagingData().getTripAccountingInformation()){
                    String key = tripAccountingInformation.getTripChartCode() + "_"
                    + tripAccountingInformation.getTripAccountNumber() + "_"
                    + tripAccountingInformation.getTripSubAccountNumber() + "_"
                    + tripAccountingInformation.getObjectCode() + "_"
                    + tripAccountingInformation.getSubObjectCode() + "_"
                    + tripAccountingInformation.getProjectCode() + "_"
                    + tripAccountingInformation.getOrganizationReference();
                KualiDecimal amount = tripAccountingInformation.getAmount();
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
        for (String key : tripAccountMap.keySet()) {
            if (accountingLineMap.containsKey(key)
                    && accountingLineMap.get(key).equals(tripAccountMap.get(key))){
                //do nothing.  The accounting line(s) haven't made a change that warrants new glpe creation
                accountingLineMap.remove(key);
            }
            else if (!accountingLineMap.containsKey(key)
                    || accountingLineMap.get(key).isLessThan(tripAccountMap.get(key))){
                //There is a difference in the accounts used
                //Either the account was completely switched, or was supplemented with another account.

                //create the credit glpe
                TemSourceAccountingLine creditLine = new TemSourceAccountingLine();
                String[] accountInfo = key.split("_");
                creditLine.setChartOfAccountsCode(accountInfo[0]);
                creditLine.setAccountNumber(accountInfo[1]);
                creditLine.setSubAccountNumber((accountInfo[2].toLowerCase().equals("null")?"":accountInfo[2]));
                creditLine.setFinancialObjectCode(accountInfo[3]);
                creditLine.setFinancialSubObjectCode((accountInfo[4].toLowerCase().equals("null")?"":accountInfo[4]));
                creditLine.setProjectCode((accountInfo[5].toLowerCase().equals("null")?"":accountInfo[5]));
                creditLine.setOrganizationReferenceId((accountInfo[6].toLowerCase().equals("null")?"":accountInfo[6]));
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
        for (String key : accountingLineMap.keySet()){
            TemSourceAccountingLine debitLine = new TemSourceAccountingLine();
            String[] accountInfo = key.split("_");
            debitLine.setChartOfAccountsCode(accountInfo[0]);
            debitLine.setAccountNumber(accountInfo[1]);
            debitLine.setSubAccountNumber((accountInfo[2].toLowerCase().equals("null")?"":accountInfo[2]));
            debitLine.setFinancialObjectCode(accountInfo[3]);
            debitLine.setFinancialSubObjectCode((accountInfo[4].toLowerCase().equals("null")?"":accountInfo[4]));
            debitLine.setProjectCode((accountInfo[5].toLowerCase().equals("null")?"":accountInfo[5]));
            debitLine.setOrganizationReferenceId((accountInfo[6].toLowerCase().equals("null")?"":accountInfo[6]));
            debitLine.setAmount(accountingLineMap.get(key));
            debitLine.setReferenceOriginCode(TemConstants.ORIGIN_CODE);
            importedExpensePendingEntryService.generateDocumentImportedExpenseGeneralLedgerPendingEntries(travelDocument, debitLine, sequenceHelper, false, distributionIncomeAndExpenseDocumentType);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void updateExpense(TravelDocument travelDocument) {
        List<HistoricalTravelExpense> historicalTravelExpenses = travelDocument.getHistoricalTravelExpenses();
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            if (historicalTravelExpense.getAgencyStagingDataId() != null){
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

}
