/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.CreateRecurringCashTransferTransactionsStep;
import org.kuali.kfs.module.endow.batch.reporter.ReportDocumentStatistics;
import org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.RecurringCashTransferTransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.RecurringCashTransferTransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentToGLTransferOfFundsDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashService;
import org.kuali.kfs.module.endow.document.validation.event.AddEndowmentAccountingLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateRecurringCashTransferTransactionsServiceImpl implements CreateRecurringCashTransferTransactionsService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);

    private Map<String, ReportDocumentStatistics> statistics = new HashMap<String, ReportDocumentStatistics>();
    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private KualiRuleService kualiRuleService;
    private KemidCurrentCashService kemidCurrentCashService;
    private HoldingTaxLotService holdingTaxLotService;

    private ConfigurationService configService;
    private DataDictionaryService dataDictionaryService;

    private ReportWriterService recurringCashTransferTransactionsExceptionReportWriterService;
    private ReportWriterService recurringCashTransferTransactionsTotalReportWriterService;

    private RecurringCashTransferTransactionDocumentExceptionReportLine exceptionReportLine = null;
    private RecurringCashTransferTransactionDocumentTotalReportLine totalReportLine = null;

    private RecurringCashTransferTransactionDocumentTotalReportLine subTotalReportLine = null;
    private RecurringCashTransferTransactionDocumentTotalReportLine allTotalReportLine = null;

    private boolean isFistTimeForWritingExceptionReport = true;
    private boolean isFistTimeForWritingTotalReport = true;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService#createCashSweepTransactions()
     */
    public boolean createRecurringCashTransferTransactions() {

        LOG.debug("Starting \"Create Recurring Cash Transfer Transactions\" batch job...");

        Collection<EndowmentRecurringCashTransfer> recurringCashTransfers = getAllRecurringCashTransferTransactionsForCurrentDate();

        // get lists of each case of transaction type
        Collection<EndowmentRecurringCashTransfer> cashTransfers = new ArrayList();
        Collection<EndowmentRecurringCashTransfer> glCashTransfers = new ArrayList();

        for (EndowmentRecurringCashTransfer endowmentRecurringCashTransfer : recurringCashTransfers) {
            String sourceTransactionType = endowmentRecurringCashTransfer.getTransactionType();
            if (sourceTransactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)) {
                cashTransfers.add(endowmentRecurringCashTransfer);
            }
            else {
                glCashTransfers.add(endowmentRecurringCashTransfer);
            }
        }

        // initiate total report
        allTotalReportLine = new RecurringCashTransferTransactionDocumentTotalReportLine();

        // cashTransfers (ECT) exist, then calculate
        if (!cashTransfers.isEmpty()) {
            subTotalReportLine = new RecurringCashTransferTransactionDocumentTotalReportLine();
            calculateCashTransfers(cashTransfers);
            writeSubTotalReportLine();
        }

        // cashTransfers (EGLT) exist, then calculate
        if (!glCashTransfers.isEmpty()) {
            subTotalReportLine = new RecurringCashTransferTransactionDocumentTotalReportLine();
            calculateGlCashTransfers(glCashTransfers);
            writeSubTotalReportLine();
        }

        writeGrandTotalReportLine();

        writeStatistics();

        LOG.debug("Finished \"Create Recurring Cash Transfer Transactions\" batch job!");

        return true;
    }

    // calculate when ECT
    protected void calculateCashTransfers(Collection<EndowmentRecurringCashTransfer> cashTransfers) {

        for (EndowmentRecurringCashTransfer cashTransfer : cashTransfers) {
            String sourceKemid = cashTransfer.getSourceKemid();
            String transferNumber = cashTransfer.getTransferNumber();
            Date lastProcessDate = cashTransfer.getLastProcessDate();

            KualiDecimal totalSourceTransaction = KualiDecimal.ZERO;
            KualiDecimal totalTargetTransaction = KualiDecimal.ZERO;
            KualiDecimal cashEquivalents = calculateTotalCashEquivalents(cashTransfer);

            CashTransferDocument cashTransferDoc = createCashTransferDocument(sourceKemid, transferNumber);
            List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargets = cashTransfer.getKemidTarget();


            for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargets) {
                KualiDecimal transactionAmount = calculateCashTransferTransactionAmount(kemidTarget, cashEquivalents, sourceKemid, lastProcessDate);

                // from spec, total of source and target are same, but totalTargetTransaction will be useful for implementing
                // total part.
                totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                totalTargetTransaction = totalTargetTransaction.add(transactionAmount);

                // add target line
                boolean addTransactionLine = addKemidTargetTransactionLine(kemidTarget, cashTransferDoc, transactionAmount, sourceKemid, transferNumber);
                if (!addTransactionLine) {
                    totalTargetTransaction = totalTargetTransaction.subtract(transactionAmount);
                    totalSourceTransaction = totalSourceTransaction.subtract(transactionAmount);
                }
            }
            // check ALLOW_NEGATIVE_BALANCE_IND and if it is ok then route
            boolean allowNegativeBalanceInd = parameterService.getParameterValueAsBoolean(CreateRecurringCashTransferTransactionsStep.class, EndowParameterKeyConstants.ALLOW_NEGATIVE_BALANCE_IND);
            // boolean allowNegativeBalanceInd = true;
            if (!allowNegativeBalanceInd && totalSourceTransaction.isLessEqual(cashEquivalents)) {
                // report exception
                // constants??
                writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, sourceKemid, transferNumber, "", "calculated source total is not less than the total available cash equivalents");

            }
            else {
                // add source line
                addSourceTransactionLineForCashTransferDoc(cashTransfer, cashTransferDoc, totalSourceTransaction);
                if (routeCashTransferDoc(cashTransferDoc, sourceKemid, transferNumber, totalTargetTransaction)) {
                    subTotalReportLine.incrementTotalTransferAmount(totalTargetTransaction);
                    subTotalReportLine.incrementTargetLinesGenerated(cashTransferDoc.getTargetTransactionLines().size());

                    updatePostingStatsForCashTransferDoc(cashTransferDoc);
                }
            }
        }
        allTotalReportLine.incrementTotalTransferAmount(subTotalReportLine.getTotalTransferAmount());
        allTotalReportLine.incrementTargetLinesGenerated(subTotalReportLine.getTargetLinesGenerated());
    }

    protected KualiDecimal calculateCashTransferTransactionAmount(EndowmentRecurringCashTransferKEMIDTarget kemidTarget, KualiDecimal cashEquivalents, String sourceKemid, Date lastProcessDate) {

        // check if it is calculation scenario 1
        if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent()) && ObjectUtils.isNotNull(kemidTarget.getTargetUseEtranCode())) {
            // retrieves transactionArchives and calculated source cash
            KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;

            List<TransactionArchive> transactionArchiveList = retrieveTransactionArchives(sourceKemid, lastProcessDate, kemidTarget.getTargetUseEtranCode());
            // if transactionArchives exist, then calculate total income and total percent of same etran code in target
            if (transactionArchiveList.size() > 0) {
                // need to change name...like cashIncrease..
                totalCashIncomeEtranCode = calculateTotalIncomeTransactionArchives(transactionArchiveList);
            }
            return totalCashIncomeEtranCode.multiply(kemidTarget.getTargetPercent());
        }
        // check if it is calculation scenario 2
        else if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())) {
            return cashEquivalents.multiply(kemidTarget.getTargetPercent());
        }
        // check if it is calculation scenario 3
        else if (ObjectUtils.isNotNull(kemidTarget.getTargetAmount())) {
            return kemidTarget.getTargetAmount();
        }
        else
            return KualiDecimal.ZERO;
    }


    // calculate when EGLT
    protected void calculateGlCashTransfers(Collection<EndowmentRecurringCashTransfer> glCashTransfers) {
        for (EndowmentRecurringCashTransfer glCashTransfer : glCashTransfers) {

            String sourceTransactionType = glCashTransfer.getTransactionType();
            String sourceKemid = glCashTransfer.getSourceKemid();
            String transferNumber = glCashTransfer.getTransferNumber();
            Date lastProcessDate = glCashTransfer.getLastProcessDate();

            KualiDecimal totalSourceTransaction = KualiDecimal.ZERO;
            KualiDecimal totalTargetTransaction = KualiDecimal.ZERO;
            KualiDecimal cashEquivalents = calculateTotalCashEquivalents(glCashTransfer);

            EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument = createEndowmentToGLTransferOfFundsDocument(sourceKemid, transferNumber);
            List<EndowmentRecurringCashTransferGLTarget> glTargets = glCashTransfer.getGlTarget();

            for (EndowmentRecurringCashTransferGLTarget glTarget : glTargets) {
                KualiDecimal transactionAmount = calculateGlCashTransferTransactionAmount(glTarget, cashEquivalents, sourceKemid, lastProcessDate);

                totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                totalTargetTransaction = totalTargetTransaction.add(transactionAmount);

                // add target line
                boolean addTransactionLine = addGlTransactionLine(glTarget, gLTransferOfFundsDocument, transactionAmount, sourceKemid, transferNumber);
                if (!addTransactionLine) {
                    totalTargetTransaction = totalTargetTransaction.subtract(transactionAmount);
                    totalSourceTransaction = totalSourceTransaction.subtract(transactionAmount);
                }
            }

            // check ALLOW_NEGATIVE_BALANCE_IND and if it is ok then route
            boolean allowNegativeBalanceInd = parameterService.getParameterValueAsBoolean(CreateRecurringCashTransferTransactionsStep.class, EndowParameterKeyConstants.ALLOW_NEGATIVE_BALANCE_IND);
            // boolean allowNegativeBalanceInd = true;
            if (!allowNegativeBalanceInd && totalSourceTransaction.isLessEqual(cashEquivalents)) {
                // report exception
                writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, sourceKemid, transferNumber, "calculated source total is less than the total available cash equivalents");
            }
            else {
                // add source... line
                addSourceTransactionLineForGLTransferOfFundsDocument(glCashTransfer, gLTransferOfFundsDocument, totalSourceTransaction);
                // route doc
                if (routeGLTransferOfFundsDocument(gLTransferOfFundsDocument, sourceKemid, transferNumber, totalTargetTransaction)) {

                    subTotalReportLine.incrementTotalTransferAmount(totalTargetTransaction);
                    subTotalReportLine.incrementTargetLinesGenerated(gLTransferOfFundsDocument.getTargetAccountingLines().size());

                    updatePostingStatsForGlTransferDoc(gLTransferOfFundsDocument);
                }
            }
        }
        allTotalReportLine.incrementTotalTransferAmount(subTotalReportLine.getTotalTransferAmount());
        allTotalReportLine.incrementTargetLinesGenerated(subTotalReportLine.getTargetLinesGenerated());
    }

    protected KualiDecimal calculateGlCashTransferTransactionAmount(EndowmentRecurringCashTransferGLTarget glTarget, KualiDecimal cashEquivalents, String sourceKemid, Date lastProcessDate) {
        // check if it is calculation scenario 1
        if (ObjectUtils.isNotNull(glTarget.getTargetPercent()) && ObjectUtils.isNotNull(glTarget.getTargetUseEtranCode())) {
            // retrieves transactionArchives and calculated source cash
            KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;

            List<TransactionArchive> transactionArchiveList = retrieveTransactionArchives(sourceKemid, lastProcessDate, glTarget.getTargetUseEtranCode());
            // if transactionArchives exist, then calculate total income and total percent of same etran code in target
            if (transactionArchiveList.size() > 0) {
                totalCashIncomeEtranCode = calculateTotalIncomeTransactionArchives(transactionArchiveList);
            }
            return totalCashIncomeEtranCode.multiply(glTarget.getTargetPercent());
        }
        // check if it is calculation scenario 2
        else if (ObjectUtils.isNotNull(glTarget.getTargetPercent())) {
            return cashEquivalents.multiply(glTarget.getTargetPercent());
        }
        // check if it is calculation scenario 3
        else if (ObjectUtils.isNotNull(glTarget.getTargetFdocLineAmount())) {
            return glTarget.getTargetFdocLineAmount();
        }
        else
            return KualiDecimal.ZERO;
    }

    protected KualiDecimal calculateTotalCashEquivalents(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        // spec 10.2
        KualiDecimal totalCashEquivalents = KualiDecimal.ZERO;
        String kemid = endowmentRecurringCashTransfer.getSourceKemid();
        KemidCurrentCash currentCash = kemidCurrentCashService.getByPrimaryKey(kemid);
        if (endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal().equals(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
            totalCashEquivalents = currentCash.getCurrentIncomeCash().add(new KualiDecimal(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailableIncomeCash(kemid)));
        }
        else {
            totalCashEquivalents = currentCash.getCurrentPrincipalCash().add(new KualiDecimal(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailablePrincipalCash(kemid)));
        }
        return totalCashEquivalents;
    }

    /**
     * This method retrieves all the recurring cash transfer transactions whose frequency code matches the current date.
     * 
     * @return List of CashSweepModel business objects
     */
    protected Collection<EndowmentRecurringCashTransfer> getAllRecurringCashTransferTransactionsForCurrentDate() {
        LOG.info("Getting all EndowmentRecurringCashTransfer with Next process date = current date");

        List<EndowmentRecurringCashTransfer> endowmentRecurringCashTransfer = new ArrayList<EndowmentRecurringCashTransfer>();
        Date currentDate = kemService.getCurrentDate();
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_NEXT_PROC_DATE, currentDate);
        Collection<EndowmentRecurringCashTransfer> recurringCashTransfers = businessObjectService.findMatching(EndowmentRecurringCashTransfer.class, fieldValues);
        LOG.info("Number of EndowmentRecurringCashTransfer with Next process date = current date" + recurringCashTransfers.size());
        return recurringCashTransfers;
    }

    // protected List getSortFieldList(){
    // List returnList = new Arraylist();
    // returnList.add(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE);
    // returnList.add(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE);
    //        
    // return returnList();
    // }

    // List<EndowmentRecurringCashTransfer> recurringCashTransfers = (List)
    // businessObjectService.findMatching(EndowmentRecurringCashTransfer.class, fieldValues);
    // //Collection<EndowmentRecurringCashTransfer> recurringCashTransfers =
    // businessObjectService.findMatching(EndowmentRecurringCashTransfer.class, fieldValues);
    // List sortFieldList = new getSortFieldList();
    // Collections.sort(recurringCashTransfers, new BeanPropertyComparator(getDefaultSortColumns(), true));


    // spec 6.2 3.1.a
    protected List<TransactionArchive> retrieveTransactionArchives(String sourceKemid, Date lastProcessDate, String targetEtranCode) {
        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;

        List<TransactionArchive> transactionArchiveList = null;
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID, sourceKemid);
        fieldValues.put(EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, targetEtranCode);
        transactionArchiveList = (List) businessObjectService.findMatching(TransactionArchive.class, fieldValues);
        for (TransactionArchive transactionArchive : transactionArchiveList) {
            if (!transactionArchive.getPostedDate().after(lastProcessDate)) {
                transactionArchiveList.remove(transactionArchive);
            }
        }
        return transactionArchiveList;
    }

    // spec 6.2 3.1.a
    protected KualiDecimal calculateTotalIncomeTransactionArchives(List<TransactionArchive> transactionArchiveList) {
        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
        for (TransactionArchive transactionArchive : transactionArchiveList) {
            if (transactionArchive.getIncomePrincipalIndicatorCode().equals(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
                totalCashIncomeEtranCode = totalCashIncomeEtranCode.add(new KualiDecimal(transactionArchive.getIncomeCashAmount()));
            }
            else {
                totalCashIncomeEtranCode = totalCashIncomeEtranCode.add(new KualiDecimal(transactionArchive.getPrincipalCashAmount()));
            }
        }
        return totalCashIncomeEtranCode;
    }

    /**
     * This method...
     * 
     * @param assetIncreaseDoc
     * @param assetSaleOffsetCode
     * @param kemid
     * @param cashLimit
     * @param currentCash
     */

    protected void addSourceTransactionLineForCashTransferDoc(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, CashTransferDocument cashTransferDoc, KualiDecimal totalAmount) {
        boolean rulesPassed = true;
        EndowmentSourceTransactionLine transactionLine = new EndowmentSourceTransactionLine();
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, cashTransferDoc, transactionLine));

        if (rulesPassed) {
            cashTransferDoc.addSourceTransactionLine(transactionLine);
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, endowmentRecurringCashTransfer.getSourceKemid(), endowmentRecurringCashTransfer.getTransferNumber(), EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME);
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(cashTransferDoc.getClass());
            updateErrorStats(documentTypeName);
        }
    }

    protected void addSourceTransactionLineForGLTransferOfFundsDocument(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument, KualiDecimal totalAmount) {
        boolean rulesPassed = true;
        EndowmentSourceTransactionLine transactionLine = new EndowmentSourceTransactionLine();
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, gLTransferOfFundsDocument, transactionLine));

        if (rulesPassed) {
            gLTransferOfFundsDocument.addSourceTransactionLine(transactionLine);
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, endowmentRecurringCashTransfer.getSourceKemid(), endowmentRecurringCashTransfer.getTransferNumber(), EndowConstants.EXISTING_SOURCE_TRAN_LINE_PROPERTY_NAME);
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(gLTransferOfFundsDocument.getClass());
            updateErrorStats(documentTypeName);
        }
    }

    protected boolean addKemidTargetTransactionLine(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget, CashTransferDocument cashTransferDoc, KualiDecimal totalAmount, String sourceKemid, String transferNumber) {
        boolean rulesPassed = true;
        EndowmentTargetTransactionLine transactionLine = new EndowmentTargetTransactionLine();
        // set all necessary fields
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        transactionLine.setKemid(endowmentRecurringCashTransferKEMIDTarget.getTargetKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransferKEMIDTarget.getTargetEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransferKEMIDTarget.getTargetLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransferKEMIDTarget.getTargetIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        // check rules
        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashTransferDoc, transactionLine));

        if (rulesPassed) {
            cashTransferDoc.addTargetTransactionLine(transactionLine);
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, sourceKemid, endowmentRecurringCashTransferKEMIDTarget.getTransferNumber(), endowmentRecurringCashTransferKEMIDTarget.getTargetSequenceNumber().toString());
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(cashTransferDoc.getClass());
            updateErrorStats(documentTypeName);
        }

        return rulesPassed;
    }

    protected boolean addGlTransactionLine(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget, EndowmentToGLTransferOfFundsDocument endowmentToGLTransferOfFundsDocument, KualiDecimal totalAmount, String sourceKemid, String transferNumber) {
        boolean rulesPassed = true;
        TargetEndowmentAccountingLine endowmentAccountingLine = new TargetEndowmentAccountingLine();
        // set all necessary fields
        endowmentAccountingLine.setChartOfAccountsCode(endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        endowmentAccountingLine.setAccountNumber(endowmentRecurringCashTransferGLTarget.getTargetAccountsNumber());
        endowmentAccountingLine.setFinancialObjectCode(endowmentRecurringCashTransferGLTarget.getTargetFinancialObjectCode());
        endowmentAccountingLine.setAmount(totalAmount);
        // number.setScale(digit, BigDecimal.ROUND_HALF_UP)

        // check rules
        rulesPassed = kualiRuleService.applyRules(new AddEndowmentAccountingLineEvent(EndowConstants.NEW_TARGET_ACC_LINE_PROPERTY_NAME, endowmentToGLTransferOfFundsDocument, endowmentAccountingLine));

        if (rulesPassed) {
            endowmentToGLTransferOfFundsDocument.addTargetAccountingLine(endowmentAccountingLine);
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, sourceKemid, endowmentRecurringCashTransferGLTarget.getTransferNumber(), endowmentRecurringCashTransferGLTarget.getTargetSequenceNumber().toString());
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(endowmentToGLTransferOfFundsDocument.getClass());
            updateErrorStats(documentTypeName);

        }

        return rulesPassed;
    }

    protected CashTransferDocument createCashTransferDocument(String sourceKemid, String transferNumber) {

        CashTransferDocument cashTransferDoc = null;
        try {
            cashTransferDoc = (CashTransferDocument) documentService.getNewDocument(CashTransferDocument.class);

            // Set values for doc
            DocumentHeader docHeader = cashTransferDoc.getDocumentHeader();
            String description = parameterService.getParameterValueAsString(CreateRecurringCashTransferTransactionsStep.class, EndowParameterKeyConstants.DESCRIPTION);
            docHeader.setDocumentDescription(description);
            cashTransferDoc.setDocumentHeader(docHeader);
            cashTransferDoc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.RECURRING);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
            writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, sourceKemid, transferNumber, "", ex.getLocalizedMessage() + " from createCashTransferDocument()");
        }

        return cashTransferDoc;
    }

    protected boolean routeCashTransferDoc(CashTransferDocument cashTransferDoc, String sourceKemid, String transferNumber, KualiDecimal totalAmount) {
        boolean routeSuccessInd = true;
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(cashTransferDoc));

        if (rulesPassed) {
            // no adhoc recipient need to add when submit doc. doc will route to the doc uploader, i.e. initiator automtically.
            List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
            try {
                cashTransferDoc.setNoRouteIndicator(getNoRouteParameterAsBoolean());
                documentService.routeDocument(cashTransferDoc, "Route CashIncreaseDocument", adHocRoutingRecipients);
                writeTotalReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, cashTransferDoc.getDocumentNumber(), transferNumber, sourceKemid, new Integer(cashTransferDoc.getTargetTransactionLines().size()), totalAmount);

            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
                writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, sourceKemid, transferNumber, "", ex.getLocalizedMessage() + " from routeCashTransferDoc()");
                String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(cashTransferDoc.getClass());
                updateErrorStats(documentTypeName);
                routeSuccessInd = false;
            }
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, sourceKemid, transferNumber, "");
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(cashTransferDoc.getClass());
            updateErrorStats(documentTypeName);
            routeSuccessInd = false;
        }

        return routeSuccessInd;
    }

    protected boolean routeGLTransferOfFundsDocument(EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument, String sourceKemid, String transferNumber, KualiDecimal totalAmount) {
        boolean routeSuccessInd = true;
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(gLTransferOfFundsDocument));

        if (rulesPassed) {
            // no adhoc recipient need to add when submit doc. doc will route to the doc uploader, i.e. initiator automtically.
            List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
            try {
                gLTransferOfFundsDocument.setNoRouteIndicator(getNoRouteParameterAsBoolean());
                documentService.routeDocument(gLTransferOfFundsDocument, "Route gLTransferOfFundsDocument", adHocRoutingRecipients);

                writeTotalReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, gLTransferOfFundsDocument.getDocumentNumber(), transferNumber, sourceKemid, new Integer(gLTransferOfFundsDocument.getTargetAccountingLines().size()), totalAmount);

            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());

                writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, sourceKemid, transferNumber, "", ex.getLocalizedMessage() + " from routeGLTransferOfFundsDocument()");
                String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(gLTransferOfFundsDocument.getClass());
                updateErrorStats(documentTypeName);
                routeSuccessInd = false;
            }
        }
        else {
            // report to error
            writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, sourceKemid, transferNumber, "");
            String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(gLTransferOfFundsDocument.getClass());
            updateErrorStats(documentTypeName);
            routeSuccessInd = false;
        }

        return routeSuccessInd;
    }

    protected EndowmentToGLTransferOfFundsDocument createEndowmentToGLTransferOfFundsDocument(String sourceKemid, String transferNumber) {
        EndowmentToGLTransferOfFundsDocument endowmentToGLTransferOfFundsDocument = null;
        try {
            endowmentToGLTransferOfFundsDocument = (EndowmentToGLTransferOfFundsDocument) documentService.getNewDocument(EndowmentToGLTransferOfFundsDocument.class);

            // Set values for doc
            DocumentHeader docHeader = endowmentToGLTransferOfFundsDocument.getDocumentHeader();
            String description = parameterService.getParameterValueAsString(CreateRecurringCashTransferTransactionsStep.class, EndowParameterKeyConstants.DESCRIPTION);
            docHeader.setDocumentDescription(description);
            endowmentToGLTransferOfFundsDocument.setDocumentHeader(docHeader);
            endowmentToGLTransferOfFundsDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.RECURRING);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());

            writeExceptionReportLine(EndowConstants.ENDOWMENT_GENERAL_LEDGER_CASH_TRANSFER_TRANSACTION_TYPE, sourceKemid, transferNumber, "", ex.getLocalizedMessage() + " from createEndowmentToGLTransferOfFundsDocument()");
        }

        return endowmentToGLTransferOfFundsDocument;

    }

    protected boolean getNoRouteParameterAsBoolean() {
        String noRouteIndParm = parameterService.getParameterValueAsString(CreateRecurringCashTransferTransactionsStep.class, EndowParameterKeyConstants.NO_ROUTE_IND);
        if (noRouteIndParm.equals(EndowConstants.YES)) {
            return true;
        }
        else
            return false;
    }

    /**
     * Extracts errors for error report writing.
     * 
     * @return a list of error messages
     */
    protected List<String> extractGlobalVariableErrors() {
        List<String> result = new ArrayList<String>();

        MessageMap errorMap = GlobalVariables.getMessageMap();

        Set<String> errorKeys = errorMap.getAllPropertiesWithErrors();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;

        for (String errorProperty : errorKeys) {
            errorMessages = (List<ErrorMessage>) errorMap.getErrorMessagesForProperty(errorProperty);
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = configService.getPropertyValueAsString(errorMessage.getErrorKey());
                messageParams = errorMessage.getMessageParameters();

                // MessageFormat.format only seems to replace one
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                System.out.println(errorString);
                while (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                result.add(errorString);
            }
        }

        // clear the stuff out of globalvars, as we need to reformat it and put it back
        GlobalVariables.getMessageMap().clearErrorMessages();
        return result;
    }

    protected void writeExceptionReportLine(String documentType, String sourceKemid, String transferNumber, String targetSeqNumber) {
        writeExceptionReportLine(documentType, sourceKemid, transferNumber, targetSeqNumber, "");
    }

    protected void writeExceptionReportLine(String documentType, String sourceKemid, String transferNumber, String targetSeqNumber, String reason) {
        // write an exception line when a transaction line fails to pass the validation.
        exceptionReportLine = new RecurringCashTransferTransactionDocumentExceptionReportLine(documentType, sourceKemid, transferNumber, targetSeqNumber);

        if (isFistTimeForWritingExceptionReport) {
            recurringCashTransferTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);
            isFistTimeForWritingExceptionReport = false;
        }
        recurringCashTransferTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);

        if (StringUtils.isBlank(reason)) {
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                recurringCashTransferTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
                recurringCashTransferTransactionsExceptionReportWriterService.writeNewLines(1);
            }
        }
        else {
            recurringCashTransferTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", reason);
            recurringCashTransferTransactionsExceptionReportWriterService.writeNewLines(1);
        }
    }

    protected void writeTotalReportLine(String documentType, String documentId, String transferNumber, String sourcekemid, Integer targetLinesGenerated, KualiDecimal totalTransferAmount) {
        totalReportLine = new RecurringCashTransferTransactionDocumentTotalReportLine(documentType, documentId, transferNumber, sourcekemid, targetLinesGenerated, totalTransferAmount);

        if (isFistTimeForWritingTotalReport) {
            recurringCashTransferTransactionsTotalReportWriterService.writeTableHeader(totalReportLine);
            isFistTimeForWritingTotalReport = false;
        }

        recurringCashTransferTransactionsTotalReportWriterService.writeTableRow(totalReportLine);
    }

    protected void writeSubTotalReportLine() {
        writeTotalReportLine("SubTotal", "", "", "", subTotalReportLine.getTargetLinesGenerated(), subTotalReportLine.getTotalTransferAmount());
        recurringCashTransferTransactionsTotalReportWriterService.writeNewLines(1);
    }

    protected void writeGrandTotalReportLine() {
        writeTotalReportLine("Total", "", "", "", allTotalReportLine.getTargetLinesGenerated(), allTotalReportLine.getTotalTransferAmount());
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the kemService attribute value.
     * 
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the kualiRuleService attribute value.
     * 
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the kemidCurrentCashOpenRecordsService
     * 
     * @param kemidCurrentCashOpenRecordsService The kemidCurrentCashOpenRecordsService to set.
     */
    public void setKemidCurrentCashService(KemidCurrentCashService kemidCurrentCashService) {
        this.kemidCurrentCashService = kemidCurrentCashService;
    }

    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    public void setRecurringCashTransferTransactionsExceptionReportWriterService(ReportWriterService recurringCashTransferTransactionsExceptionReportWriterService) {
        this.recurringCashTransferTransactionsExceptionReportWriterService = recurringCashTransferTransactionsExceptionReportWriterService;
    }

    public void setRecurringCashTransferTransactionsTotalReportWriterService(ReportWriterService recurringCashTransferTransactionsTotalReportWriterService) {
        this.recurringCashTransferTransactionsTotalReportWriterService = recurringCashTransferTransactionsTotalReportWriterService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the configService.
     * 
     * @param configService
     */
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    private void updatePostingStatsForCashTransferDoc(CashTransferDocument cashTransferDoc) {

        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(cashTransferDoc.getClass());
        ReportDocumentStatistics stats = statistics.get(documentTypeName);

        // If null that means there isn't one in the map, so create it and add
        // it to the map.
        if (stats == null) {
            stats = new ReportDocumentStatistics(documentTypeName);
            statistics.put(documentTypeName, stats);
        }
        stats.addNumberOfSourceTransactionLines(cashTransferDoc.getSourceTransactionLines().size());
        stats.addNumberOfTargetTransactionLines(cashTransferDoc.getTargetTransactionLines().size());

        stats.incrementNumberOfDocuments();
    }

    private void updatePostingStatsForGlTransferDoc(EndowmentToGLTransferOfFundsDocument glTransferDoc) {

        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(glTransferDoc.getClass());
        ReportDocumentStatistics stats = statistics.get(documentTypeName);

        // If null that means there isn't one in the map, so create it and add
        // it to the map.
        if (stats == null) {
            stats = new ReportDocumentStatistics(documentTypeName);
            statistics.put(documentTypeName, stats);
        }
        stats.addNumberOfSourceTransactionLines(glTransferDoc.getSourceTransactionLines().size());
        stats.addNumberOfTargetTransactionLines(glTransferDoc.getTargetAccountingLines().size());

        stats.incrementNumberOfDocuments();
    }

    private void updateErrorStats(String documentTypeName) {
        ReportDocumentStatistics stats = statistics.get(documentTypeName);

        // If null that means there isn't one in the map, so create it and add
        // it to the map.
        if (stats == null) {
            stats = new ReportDocumentStatistics(documentTypeName);
            statistics.put(documentTypeName, stats);
        }

        stats.incrementNumberOfErrors();
    }

    /**
     * Write out the statistics.
     */
    private void writeStatistics() {

        for (Map.Entry<String, ReportDocumentStatistics> entry : statistics.entrySet()) {

            ReportDocumentStatistics stats = entry.getValue();

            recurringCashTransferTransactionsTotalReportWriterService.writeStatisticLine("%s Documents:", stats.getDocumentTypeName());
            recurringCashTransferTransactionsTotalReportWriterService.writeStatisticLine("   Number of Documents Generated:            %d", stats.getNumberOfDocuments());
            recurringCashTransferTransactionsTotalReportWriterService.writeStatisticLine("   Number of Transaction Lines Generated:    %d", stats.getTotalNumberOfTransactionLines());
            recurringCashTransferTransactionsTotalReportWriterService.writeStatisticLine("   Number of Error Records Written:          %d", stats.getNumberOfErrors());
            recurringCashTransferTransactionsTotalReportWriterService.writeStatisticLine("", "");
        }

    }


}
