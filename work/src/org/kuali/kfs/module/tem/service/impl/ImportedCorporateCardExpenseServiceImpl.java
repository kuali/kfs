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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

public class ImportedCorporateCardExpenseServiceImpl extends ExpenseServiceBase implements TEMExpenseService {

    protected static Logger LOG = Logger.getLogger(ImportedCorporateCardExpenseServiceImpl.class);

    TravelDisbursementService travelDisbursementService;
    ImportedExpensePendingEntryService importedExpensePendingEntryService;
    CreditCardAgencyService creditCardAgencyService;

    /**
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (ImportedExpense expense : (List<ImportedExpense>) expenses) {

            if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                //update the expense detail's card type (if null) to the expense's card type
                for (ImportedExpense imported : (List<ImportedExpense>)expense.getExpenseDetails()){
                    if (imported.getExpenseParentId() != null && imported.getCardType() == null){
                        imported.setCardType(expense.getCardType());
                    }
                }
                calculateDistributionTotals(document, distributionMap, expense.getExpenseDetails());
            }
            else {
                if (expense.getCardType() != null
                        && !expense.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)
                        && !expense.getNonReimbursable()){
                    expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
                    ExpenseTypeObjectCode code = SpringContext.getBean(TravelExpenseService.class).getExpenseType(expense.getExpenseTypeCode(),
                            document.getFinancialDocumentTypeCode(), document.getTripTypeCode(), document.getTraveler().getTravelerTypeCode());

                    expense.setTravelExpenseTypeCode(code);
                    String financialObjectCode = expense.getExpenseTypeObjectCode() != null ? expense.getExpenseTypeObjectCode().getFinancialObjectCode() : null;

                    LOG.debug("Refreshed importedExpense with expense type code " + expense.getExpenseTypeObjectCode() +
                            " and financialObjectCode " + financialObjectCode);

                    final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, financialObjectCode);
                    if (objCode != null && code != null && !code.getExpenseType().isPrepaidExpense()){
                        AccountingDistribution distribution = null;
                        String key = objCode.getCode() + "-" + expense.getCardType();
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(expense.getCardType());
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
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TEMExpense> getExpenseDetails(TravelDocument document) {
        return document.getImportedExpenses();
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#validateExpenseCalculation(org.kuali.kfs.module.tem.businessobject.TEMExpense)
     */
    @Override
    public boolean validateExpenseCalculation(TEMExpense expense){
        return (expense instanceof ImportedExpense)
                && ((ImportedExpense)expense).getCardType() != null
                && !StringUtils.defaultString(((ImportedExpense)expense).getCardType()).equals(TemConstants.TRAVEL_TYPE_CTS);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#processExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        //create glpe's for just corp cards;
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
            if (creditCardAgencyService.getCorpCreditCardAgencyCodeList().contains(line.getCardType())){
                importedExpensePendingEntryService.generateDocumentImportedExpenseGeneralLedgerPendingEntries(travelDocument, line, sequenceHelper, false, travelDocument.getFinancialDocumentTypeCode());
            }
        }
    }

    /**
     * Spawn DV doc(s) for the imported corporate card expenses to pay back the bank
     *
     * @param document
     */
    private void createVendorDisbursementVouchers(TravelDocument document){
        Person principal = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());

        //build map of the accounting line info and amount by card type
        Collection<String> cardAgencyTypeSet = new TreeSet<String>();
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)document.getSourceAccountingLines()){
            //pick up all the corp card types
            if (creditCardAgencyService.getCorpCreditCardAgencyCodeList().contains(line.getCardType())){
                cardAgencyTypeSet.add(line.getCardType());
            }
        }

        //process DV for each of the card type
        for (String cardAgencyType : cardAgencyTypeSet){
            DisbursementVoucherDocument disbursementVoucherDocument = travelDisbursementService.createAndApproveDisbursementVoucherDocument(document, cardAgencyType);
            String docNumber = disbursementVoucherDocument.getDocumentNumber();

            if (!(document instanceof TravelRelocationDocument)){
                for (HistoricalTravelExpense expense : document.getHistoricalTravelExpenses()){
                    if (expense.getCreditCardStagingData() != null){
                        if (expense.getCreditCardStagingData().getCreditCardAgency().getTravelCardTypeCode().equals(cardAgencyType)){
                            expense.getCreditCardStagingData().setDisbursementVoucherDocumentNumber(docNumber);
                            getBusinessObjectService().save(expense.getCreditCardStagingData());
                        }
                    }
                }
            }

            //set relation from DV back to the travel doc
            String relationDescription = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() + " - DV";
            SpringContext.getBean(AccountingDocumentRelationshipService.class).save(new AccountingDocumentRelationship(document.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void updateExpense(TravelDocument travelDocument) {
        List<HistoricalTravelExpense> historicalTravelExpenses = travelDocument.getHistoricalTravelExpenses();
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            if (historicalTravelExpense.isCreditCardTravelExpense()){
                long time = (new java.util.Date()).getTime();
                historicalTravelExpense.setReconciliationDate(new Date(time));
                historicalTravelExpense.setReconciled(TemConstants.ReconciledCodes.RECONCILED);
            }
        }
        getBusinessObjectService().save(historicalTravelExpenses);
        boolean spawnDV = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.CORPORATE_CARD_PAYMENT_BY_DV_IND);
        if (spawnDV){
            createVendorDisbursementVouchers(travelDocument);
        }
    }

    public void setTravelDisbursementService(TravelDisbursementService travelDisbursementService) {
        this.travelDisbursementService = travelDisbursementService;
    }

    public void setCreditCardAgencyService(CreditCardAgencyService creditCardAgencyService) {
        this.creditCardAgencyService = creditCardAgencyService;
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }
}
