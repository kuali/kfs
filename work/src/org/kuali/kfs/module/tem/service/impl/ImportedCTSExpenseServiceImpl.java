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
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DEFAULT_CHART_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.impl.GeneralLedgerPendingEntryServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

public class ImportedCTSExpenseServiceImpl implements TEMExpenseService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ImportedCTSExpenseServiceImpl.class);

    @Override
    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document) {
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        for (ImportedExpense expense : document.getImportedExpenses()){
            String cardType = expense.getCardType();
            if (cardType != null && cardType.equals(TemConstants.CARD_TYPE_CTS) && !expense.getNonReimbursable()){
                expense.refreshReferenceObject("travelExpenseTypeCode");
                String financialObjectCode= "";
                expense.getTravelExpenseTypeCode();
                if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.AIRFARE)){
                    financialObjectCode= getParameterService().getParameterValue(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_AIR_OBJECT_CODE);
                }
                else if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.LODGING)){
                    financialObjectCode= getParameterService().getParameterValue(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_LODGING_OBJECT_CODE);             
                }
                else if (expense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.RENTAL_CAR)){
                    financialObjectCode= getParameterService().getParameterValue(PARAM_NAMESPACE, TemConstants.AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE, TemConstants.AgencyMatchProcessParameter.CTS_RENTAL_CAR_OBJECT_CODE);
                }
                 
                debug("Refreshed importedExpense with expense type code ", expense.getTravelExpenseTypeCode(),
                        " and financialObjectCode ", financialObjectCode);

                final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, expense.getTravelExpenseTypeCode().getFinancialObjectCode());
                if (objCode != null && expense.getTravelExpenseTypeCode() != null && !expense.getTravelExpenseTypeCode().isPrepaidExpense()){
                    AccountingDistribution distribution = null;
                    String key = objCode.getCode() + "-" + TemConstants.CARD_TYPE_CTS;
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
        
        return distributionMap;
    }

    @Override
    public String getExpenseType() {
        return null;
    }

    @Override
    public List<TEMExpense> getExpenseDetails(TravelDocument document) {
       
        
        return null;
    }
    
    /**
     * Gets the objectCodeService attribute.
     * 
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return SpringContext.getBean(ObjectCodeService.class);
    }
    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    @Override
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable) {
        KualiDecimal total = KualiDecimal.ZERO;

        if (includeNonReimbursable){
            total = calculateTotals(total, document.getImportedExpenses(), TemConstants.ExpenseTypeReimbursementCodes.ALL);
        }
        else{
            total = calculateTotals(total, document.getImportedExpenses(), TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE);
        }
        return total;
    }

    @Override
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;
        total = calculateTotals(total, document.getImportedExpenses(), TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE);
        return total;
    }

    private KualiDecimal calculateTotals(KualiDecimal total, List<ImportedExpense> expenses, String code){
        for (TEMExpense expense : expenses){
            if (expense instanceof ImportedExpense
                    && ((ImportedExpense)expense).getCardType() != null
                    && ((ImportedExpense)expense).getCardType().equals(TemConstants.CARD_TYPE_CTS)){
                if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.ALL)){
                    total = total.add(expense.getConvertedAmount());
                }
                else if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE)){
                    if ((expense.getTravelExpenseTypeCode() != null && expense.getTravelExpenseTypeCode().isPrepaidExpense()) || expense.getNonReimbursable()) {
                        total = total.add(expense.getExpenseAmount());
                    }
                }
                else if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE)){
                    if ((expense.getTravelExpenseTypeCode() != null && !expense.getTravelExpenseTypeCode().isPrepaidExpense()) && !expense.getNonReimbursable()) {
                        total = total.add(expense.getExpenseAmount());
                    }
                }
            }
            
        }
        return total;
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
           if (line.getCardType().equals(TemConstants.CARD_TYPE_CTS)){
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
        Iterator<String> it = tripAccountMap.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
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
                ExpenseUtils.generateImportedExpenseGeneralLedgerPendingEntries(travelDocument, creditLine, sequenceHelper, true, distributionIncomeAndExpenseDocumentType);
                
                accountingLineMap.remove(key);
            }
        }
        
        /*
         * Iterate through the rest of the accounting lines.
         * Create normal debit glpe's.
         */
        it = accountingLineMap.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            
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
            ExpenseUtils.generateImportedExpenseGeneralLedgerPendingEntries(travelDocument, debitLine, sequenceHelper, false, distributionIncomeAndExpenseDocumentType);
        }
    }

     
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
}
