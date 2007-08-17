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
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.KFSConstants.CurrencyCoinSources;
import org.kuali.kfs.KFSConstants.DepositConstants;
import org.kuali.kfs.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.CashieringItemInProcess;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.dao.CashManagementDao;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.module.financial.rules.CashieringTransactionRule;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.web.struts.form.CashDrawerStatusCodeFormatter;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class CashManagementServiceImpl implements CashManagementService {
    private BusinessObjectService businessObjectService;
    private CashDrawerService cashDrawerService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private CashManagementDao cashManagementDao;

    /**
     * If a CMD is found that is associated with the CR document, then that CMD is returned; otherwise null is returned. Currently
     * the relationships are:
     * <ul>
     * <li>(CashReceipt to CashReceiptHeader) is (1 to 1)
     * <li>(CashReceiptHeader to DepositCashReceiptControl) is (1 to 1)
     * <li>(DepositCashReceiptControl to Deposit) is (many to 1)
     * <li>(Deposit to CashManagementDocument) is (many to 1)
     * </ul>
     * 
     * @see org.kuali.module.financial.service.CashManagementService#getCashManagementDocumentForCashReceiptId(java.lang.String)
     */
    public CashManagementDocument getCashManagementDocumentForCashReceiptId(String documentId) {
        CashManagementDocument cmdoc = null;

        // get CashReceiptHeader for the CashReceipt, if any
        HashMap primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentId);
        CashReceiptHeader crh = (CashReceiptHeader) businessObjectService.findByPrimaryKey(CashReceiptHeader.class, primaryKeys);

        // get the DepositCashReceiptControl for the CashReceiptHeader
        if (crh != null) {
            List crcList = crh.getDepositCashReceiptControl();
            if (!crcList.isEmpty()) {
                DepositCashReceiptControl dpcrc = (DepositCashReceiptControl) crcList.get(0);

                // get the Deposit and follow it to the CashManagementDocument
                Deposit d = (Deposit) dpcrc.getDeposit();
                cmdoc = d.getCashManagementDocument();
            }
        }

        return cmdoc;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#createCashManagementDocument(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CashManagementDocument createCashManagementDocument(String unitName, String docDescription, String annotation) {
        if (StringUtils.isBlank(unitName)) {
            throw new IllegalArgumentException("invalid (blank) unitName");
        }
        if (StringUtils.isBlank(docDescription)) {
            throw new IllegalArgumentException("invalid (blank) docDescription");
        }

        // check user authorization
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CashManagementDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(documentTypeName);
        documentAuthorizer.canInitiate(documentTypeName, user);

        // check cash drawer
        CashDrawer cd = cashDrawerService.getByWorkgroupName(unitName, true);
        String controllingDocId = cd.getReferenceFinancialDocumentNumber();

        // KULEDOCS-1475: adding handling for two things which should never happen:
        // 1. CashDrawer is open or locked by document 'null'
        // 2. CashDrawer is open or locked by a document which doesn't exist
        if (!cd.isClosed()) {
            boolean forceDrawerClosed = false;

            if (StringUtils.isBlank(controllingDocId)) {
                forceDrawerClosed = true;
            }
            else if (!documentService.documentExists(controllingDocId)) {
                forceDrawerClosed = true;
            }

            if (forceDrawerClosed) {
                cashDrawerService.closeCashDrawer(cd);
                cd = cashDrawerService.getByWorkgroupName(unitName, true);
            }
        }


        CashManagementDocument cmDoc = null;
        if (cd.isClosed()) {
            // create the document
            try {
                cmDoc = (CashManagementDocument) documentService.getNewDocument(CashManagementDocument.class);
                cmDoc.getDocumentHeader().setFinancialDocumentDescription(docDescription);
                cmDoc.setWorkgroupName(unitName);
                cmDoc.setCashDrawer(cd);
                cmDoc.getCurrentTransaction().setWorkgroupName(cmDoc.getWorkgroupName());
                cmDoc.getCurrentTransaction().setReferenceFinancialDocumentNumber(cmDoc.getDocumentNumber());
                cmDoc.getCurrentTransaction().setOpenItemsInProcess(getOpenItemsInProcess(cmDoc));
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to create CashManagementDocument", e);
            }
        }
        else {
            CashDrawerStatusCodeFormatter f = new CashDrawerStatusCodeFormatter();

            throw new CashDrawerStateException(unitName, controllingDocId, (String) f.format(CashDrawerConstants.STATUS_CLOSED), (String) f.format(cd.getStatusCode()));
        }

        return cmDoc;
    }
    
    /**
     * This method creates new cumulative currency and coin details for a document
     * @param cmDoc the cash management document the cumulative details will be associated with
     * @param cashieringSource the cashiering record source for the new details
     */
    public void createNewCashDetails(CashManagementDocument cmDoc, String cashieringSource) {
        CoinDetail coinDetail = new CoinDetail();
        coinDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        coinDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        coinDetail.setCashieringRecordSource(cashieringSource);
        businessObjectService.save(coinDetail);
        
        CurrencyDetail currencyDetail = new CurrencyDetail();
        currencyDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        currencyDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        currencyDetail.setCashieringRecordSource(cashieringSource);
        businessObjectService.save(currencyDetail);
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#addInterimDeposit(org.kuali.module.financial.document.CashManagementDocument,
     *      java.lang.String, org.kuali.module.financial.bo.BankAccount, java.util.List)
     */
    @SuppressWarnings("deprecation")
    public void addDeposit(CashManagementDocument cashManagementDoc, String depositTicketNumber, BankAccount bankAccount, List selectedCashReceipts, List selectedCashieringChecks, boolean isFinalDeposit) {
        validateDepositParams(cashManagementDoc, bankAccount, selectedCashReceipts);

        String depositTypeCode = DepositConstants.DEPOSIT_TYPE_INTERIM;
        if (isFinalDeposit) {
            depositTypeCode = DepositConstants.DEPOSIT_TYPE_FINAL;
        }

        //
        // lock the cashDrawer
        cashDrawerService.lockCashDrawer(cashManagementDoc.getCashDrawer(), cashManagementDoc.getDocumentNumber());
        
        // turn the list of selected check sequence ids into a list of actual check records
        Map<Integer, Check> checks = getUndepositedChecksAsMap(cashManagementDoc);
        List<Check> checksToSave = new ArrayList<Check>();
        if (selectedCashieringChecks != null) {
            for (Object o: selectedCashieringChecks) {
                Integer sequenceId = (Integer)o;
                Check check = checks.get(sequenceId);
                checksToSave.add(check);
            }
        }

        //
        // create the Deposit
        Deposit deposit = buildDeposit(cashManagementDoc, depositTypeCode, depositTicketNumber, bankAccount, selectedCashReceipts, checksToSave);

        // attach it to the document
        List deposits = cashManagementDoc.getDeposits();
        deposits.add(deposit);
        documentService.updateDocument(cashManagementDoc);

        // associate the CashReceipts with it
        List dccList = new ArrayList();
        for (Iterator i = selectedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            DocumentHeader dh = crDoc.getDocumentHeader();

            String statusCode = isFinalDeposit ? DocumentStatusCodes.CashReceipt.FINAL : DocumentStatusCodes.CashReceipt.INTERIM;
            dh.setFinancialDocumentStatusCode(statusCode);
            documentService.updateDocument(crDoc);

            CashReceiptHeader crHeader = new CashReceiptHeader();
            crHeader.setDocumentNumber(crDoc.getDocumentNumber());
            crHeader.setCashReceiptDocument(crDoc);
            crHeader.setWorkgroupName(cashManagementDoc.getWorkgroupName());

            DepositCashReceiptControl dcc = new DepositCashReceiptControl();
            dcc.setFinancialDocumentCashReceiptNumber(crHeader.getDocumentNumber());
            dcc.setFinancialDocumentDepositNumber(deposit.getDocumentNumber());
            dcc.setFinancialDocumentDepositLineNumber(deposit.getFinancialDocumentDepositLineNumber());

            dcc.setCashReceiptHeader(crHeader);
            dcc.setDeposit(deposit);

            dccList.add(dcc);
        }
        // crHeaders get saved as side-effect of saving dccs
        businessObjectService.save(dccList);
        
        // make sure all checks have the right deposit line number
        for (Check check: checksToSave) {
            check.setFinancialDocumentDepositLineNumber(deposit.getFinancialDocumentDepositLineNumber());
        }
        businessObjectService.save(checksToSave);

        //
        // unlock the cashDrawer, if needed
        if (!isFinalDeposit) {
            cashDrawerService.unlockCashDrawer(cashManagementDoc.getCashDrawer(), cashManagementDoc.getDocumentNumber());
        }
    }

    /**
     * Validates the given Deposit parameters, throwing various (runtime) exceptions if errors exist
     * 
     * @param cashManagementDoc
     * @param bankAccount
     * @param selectedCashReceipts
     */
    private void validateDepositParams(CashManagementDocument cashManagementDoc, BankAccount bankAccount, List<CashReceiptDocument> selectedCashReceipts) {
        if (cashManagementDoc == null) {
            throw new IllegalArgumentException("invalid (null) cashManagementDoc");
        }
        else if (!cashManagementDoc.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
            throw new IllegalStateException("cashManagementDoc '" + cashManagementDoc.getDocumentNumber() + "' is not in 'saved' state");
        }
        else if (cashManagementDoc.hasFinalDeposit()) {
            throw new IllegalStateException("cashManagementDoc '" + cashManagementDoc.getDocumentNumber() + "' hasFinalDeposit");
        }
        if (bankAccount == null) {
            throw new IllegalArgumentException("invalid (null) bankAccount");
        }

        if (selectedCashReceipts == null) {
            throw new IllegalArgumentException("invalid (null) cashReceipts list");
        }
        else {
            for (CashReceiptDocument cashReceipt : selectedCashReceipts) {
                String statusCode = cashReceipt.getDocumentHeader().getFinancialDocumentStatusCode();
                if (!StringUtils.equals(statusCode, DocumentStatusCodes.CashReceipt.VERIFIED)) {
                    throw new InvalidCashReceiptState("cash receipt document " + cashReceipt.getDocumentNumber() + " has a status other than 'verified' ");
                }
            }
        }
    }

    private Deposit buildDeposit(CashManagementDocument cashManagementDoc, String depositTypeCode, String depositTicketNumber, BankAccount bankAccount, List<CashReceiptDocument> selectedCashReceipts, List selectedCashieringChecks) {
        Deposit deposit = new Deposit();
        deposit.setDocumentNumber(cashManagementDoc.getDocumentNumber());
        deposit.setCashManagementDocument(cashManagementDoc);

        deposit.setDepositTypeCode(depositTypeCode);

        deposit.setDepositDate(dateTimeService.getCurrentSqlDate());

        deposit.setBankAccount(bankAccount);
        deposit.setDepositBankCode(bankAccount.getBank().getFinancialDocumentBankCode());
        deposit.setDepositBankAccountNumber(bankAccount.getFinDocumentBankAccountNumber());

        // derive the line number
        int lineNumber = cashManagementDoc.getNextDepositLineNumber();
        deposit.setFinancialDocumentDepositLineNumber(new Integer(lineNumber));

        // trim depositTicketNumber to empty, because the field is optional
        deposit.setDepositTicketNumber(StringUtils.trimToEmpty(depositTicketNumber));

        // total up the cash receipts
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator i = selectedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            total = total.add(crDoc.getTotalCheckAmount());
        }
        Check currCheck;
        for (Object checkObj: selectedCashieringChecks) {
            currCheck = (Check)checkObj;
            total = total.add(currCheck.getAmount());
        }
        deposit.setDepositAmount(total);

        return deposit;
    }

    /**
     * @param bankCode
     * @return Bank associated with the given bankCode, or null if none is found
     */
    private Bank lookupBank(String bankCode) {
        Map keyMap = new HashMap();
        keyMap.put("financialDocumentBankCode", bankCode);

        Bank bank = (Bank) businessObjectService.findByPrimaryKey(Bank.class, keyMap);
        return bank;
    }

    /**
     * @param bankCode
     * @param accountNumber
     * @return BankAccount associated with the given bankCode and accountNumber, or null if none is found
     */
    private BankAccount lookupBankAccount(String bankCode, String accountNumber) {
        Map keyMap = new HashMap();
        keyMap.put("financialDocumentBankCode", bankCode);
        keyMap.put("finDocumentBankAccountNumber", accountNumber);

        BankAccount bankAccount = (BankAccount) businessObjectService.findByPrimaryKey(BankAccount.class, keyMap);
        return bankAccount;
    }

    /**
     * This method returns all undeposited checks as a map based on sequence id
     * @param cmDoc the cash management doc to find undeposited checks for
     * @return a map of checks keyed on sequence id
     */
    private Map<Integer, Check> getUndepositedChecksAsMap(CashManagementDocument cmDoc) {
        Map<Integer, Check> checks = new HashMap<Integer, Check>();
        List<Check> checkList = cashManagementDao.selectUndepositedCashieringChecks(cmDoc.getDocumentNumber());
        if (checkList != null && checkList.size() > 0) {
            for (Check check: checkList) {
                checks.put(check.getSequenceId(), check);
            }
        }
        return checks;
    }

    /**
     * Method should only be called after the appropriate CashManagementDocumentRule has been successfully passed
     * 
     * @see org.kuali.module.financial.service.CashManagementService#cancelCashManagementDocument(org.kuali.module.financial.document.CashManagementDocument)
     */
    public void cancelCashManagementDocument(CashManagementDocument cmDoc) {
        if (cmDoc == null) {
            throw new IllegalArgumentException("invalid (null) CashManagementDocument");
        }

        // cancel each deposit (which also deletes the records connecting the Deposit to a CashManagementDoc
        List deposits = cmDoc.getDeposits();
        for (Iterator i = deposits.iterator(); i.hasNext();) {
            Deposit deposit = (Deposit) i.next();

            cancelDeposit(deposit);
        }

        // reclose the cashDrawer
        String unitName = cmDoc.getWorkgroupName();
        cashDrawerService.closeCashDrawer(cmDoc.getCashDrawer());

        // cleanup the CMDoc, but let the postprocessor itself save it
        cmDoc.setDeposits(new ArrayList());
        cmDoc.getDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.CANCELLED);
        
        // kill off cumulative currency/coin detail records for this document (canceling the deposits kills the deposit records)
        String[] cashieringSourcesToDelete = { KFSConstants.CurrencyCoinSources.CASH_RECEIPTS, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT };
        for (String cashieringSourceToDelete : cashieringSourcesToDelete) {
            CurrencyDetail currencyDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, cashieringSourceToDelete);
            if (currencyDetail != null) {
                businessObjectService.delete(currencyDetail);
            }
            CoinDetail coinDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, cashieringSourceToDelete);
            if (coinDetail != null) {
                businessObjectService.delete(coinDetail);
            }
        }
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#cancelDeposit(org.kuali.module.financial.bo.Deposit)
     */
    public void cancelDeposit(Deposit deposit) {
        if (deposit == null) {
            throw new IllegalArgumentException("invalid (null) deposit");
        }

        // reload it, to forestall OptimisticLockExceptions
        deposit.refresh();

        // save workgroup name, for possible later use
        String depositWorkgroup = deposit.getCashManagementDocument().getWorkgroupName();

        // update every CashReceipt associated with this Deposit
        List depositCashReceiptControls = deposit.getDepositCashReceiptControl();
        for (Iterator j = depositCashReceiptControls.iterator(); j.hasNext();) {
            DepositCashReceiptControl dcc = (DepositCashReceiptControl) j.next();
            CashReceiptHeader crHeader = dcc.getCashReceiptHeader();

            // reset each CashReceipt status
            CashReceiptDocument crDoc = crHeader.getCashReceiptDocument();
            DocumentHeader crdh = crDoc.getDocumentHeader();
            crdh.setFinancialDocumentStatusCode(DocumentStatusCodes.CashReceipt.VERIFIED);
            documentService.updateDocument(crDoc);
        }
        
        // un-deposit all cashiering checks associated with the deposit
        List<Check> depositedChecks = selectCashieringChecksForDeposit(deposit.getDocumentNumber(), deposit.getFinancialDocumentDepositLineNumber());
        for (Check check: depositedChecks) {
            check.setFinancialDocumentDepositLineNumber(null);
        }
        businessObjectService.save(depositedChecks);

        // unlock the cashDrawer, if needed
        if (deposit.getDepositTypeCode() == DepositConstants.DEPOSIT_TYPE_FINAL) {
            CashDrawer drawer = cashDrawerService.getByWorkgroupName(deposit.getCashManagementDocument().getWorkgroupName(), false);
            CurrencyDetail currencyDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(deposit.getCashManagementDocument().getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
            if (currencyDetail != null) {
                drawer.addCurrency(currencyDetail);
                businessObjectService.delete(currencyDetail);
            }
            CoinDetail coinDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(deposit.getCashManagementDocument().getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
            if (coinDetail != null) {
                drawer.addCoin(coinDetail);
                businessObjectService.delete(coinDetail);
            }
            businessObjectService.save(drawer);
            cashDrawerService.unlockCashDrawer(drawer, deposit.getDocumentNumber());
        }

        // delete the Deposit from the database
        businessObjectService.delete(deposit);
    }

    /**
     * Method should only be called after the appropriate CashManagementDocumentRule has been successfully passed
     * 
     * @see org.kuali.module.financial.service.CashManagementService#finalizeCashManagementDocument(org.kuali.module.financial.document.CashManagementDocument)
     */
    public void finalizeCashManagementDocument(CashManagementDocument cmDoc) {
        if (cmDoc == null) {
            throw new IllegalArgumentException("invalid (null) CashManagementDocument");
        }
        if (!cmDoc.hasFinalDeposit()) {
            throw new IllegalStateException("cmDoc " + cmDoc.getDocumentNumber() + " is missing a FinalDeposit");
        }

        String workgroupName = cmDoc.getWorkgroupName();
        cashDrawerService.closeCashDrawer(workgroupName);
        CashDrawer cd = cashDrawerService.getByWorkgroupName(workgroupName, false);


        // finalize the CashReceipts
        List<Deposit> deposits = cmDoc.getDeposits();
        for (Deposit deposit : deposits) {
            List<CashReceiptDocument> receipts = retrieveCashReceipts(deposit);
            for (CashReceiptDocument receipt : receipts) {
                // marks GLPEs of CRs as APPROVED
                for (GeneralLedgerPendingEntry glpe : receipt.getGeneralLedgerPendingEntries()) {
                    glpe.setFinancialDocumentApprovedCode(DocumentStatusCodes.APPROVED);
                }

                // mark CRs themselves as APPROVED
                receipt.getDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.APPROVED);

                // persist
                documentService.updateDocument(receipt);
            }
        }
        
        // generate the master currency and coin details; save those
        CurrencyDetail masterCurrencyDetail = this.generateMasterCurrencyDetail(cmDoc);
        businessObjectService.save(masterCurrencyDetail);
        CoinDetail masterCoinDetail = this.generateMasterCoinDetail(cmDoc);
        businessObjectService.save(masterCoinDetail);

        // finalize the CMDoc, but let the postprocessor save it
        cmDoc.getDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.APPROVED);
    }
    
    /**
     * This method verifies that all cash receipts for the document are deposited
     * @param cmDoc the cash management document to verify
     * @return true if all CRs are deposited, false if otherwise
     */
    public boolean allVerifiedCashReceiptsAreDeposited(CashManagementDocument cmDoc) {
        boolean result = true;
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getWorkgroupName(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        for (Object o: verifiedReceipts) {
            if (!verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument)o)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#retrieveCashReceipts(org.kuali.module.financial.bo.Deposit)
     */
    public List retrieveCashReceipts(Deposit deposit) {
        List cashReceiptDocuments = null;

        // retrieve CashReceiptHeaders
        Map criteriaMap = new HashMap();
        criteriaMap.put("depositCashReceiptControl.financialDocumentDepositNumber", deposit.getDocumentNumber());
        criteriaMap.put("depositCashReceiptControl.financialDocumentDepositLineNumber", deposit.getFinancialDocumentDepositLineNumber());

        List crHeaders = new ArrayList(businessObjectService.findMatching(CashReceiptHeader.class, criteriaMap));
        if (!crHeaders.isEmpty()) {
            List idList = new ArrayList();
            for (Iterator i = crHeaders.iterator(); i.hasNext();) {
                CashReceiptHeader crHeader = (CashReceiptHeader) i.next();
                idList.add(crHeader.getDocumentNumber());
            }

            try {
                cashReceiptDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, idList);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve cashReceipts", e);
            }
        }
        else {
            cashReceiptDocuments = new ArrayList();
        }

        return cashReceiptDocuments;
    }

    /**
     * Verifies if a given cash receipt is deposited as part of the given cash management document
     * @param cmDoc the cash management document to search through
     * @param crDoc the cash receipt to check  the deposited status of
     * @return true if the given cash receipt document is deposited as part of the given cash management document, false if otherwise
     */
    public boolean verifyCashReceiptIsDeposited(CashManagementDocument cmDoc, CashReceiptDocument crDoc) {
        boolean thisCRDeposited = false;
        for (Deposit deposit: cmDoc.getDeposits()) {
            if (deposit.containsCashReceipt(crDoc)) {
                thisCRDeposited = true;
                break;
            }
        }
        return thisCRDeposited;
    }

    /**
     * This method turns the last interim deposit into the final deposit and locks the cash drawer 
     * @param cmDoc the cash management document to take deposits from for finalization
     */
    public void finalizeLastInterimDeposit(CashManagementDocument cmDoc) {
        // if there's already a final deposit, throw an IllegalStateException
        if (cmDoc.hasFinalDeposit()) {
            throw new IllegalStateException("CashManagementDocument #"+cmDoc.getDocumentNumber()+" already has a final deposit");
        }
        // if there are still verified un-deposited cash receipts, throw an IllegalStateException
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getWorkgroupName(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        for (Object o: verifiedReceipts) {
            CashReceiptDocument crDoc = (CashReceiptDocument)o;
            if (!verifyCashReceiptIsDeposited(cmDoc, crDoc)) {
                throw new IllegalStateException("Verified Cash Receipt Document #"+crDoc.getDocumentNumber()+" must be deposited for this to be a final deposit");
            }
        }
        // lock the cash drawer
        cashDrawerService.lockCashDrawer(cmDoc.getCashDrawer(), cmDoc.getDocumentNumber());
        // change the deposit type code for the last deposit
        List<Deposit> allDeposits = cmDoc.getDeposits();
        Deposit lastInterim = allDeposits.get(allDeposits.size() - 1);
        lastInterim.setDepositTypeCode(DepositConstants.DEPOSIT_TYPE_FINAL);
        finalizeCashReceiptsForDeposit(lastInterim);
        documentService.updateDocument(cmDoc);
    }
    
    /**
     * 
     * This method switches cash receipts to "final" status as opposed to "interim" status
     * @param deposit
     */
    private void finalizeCashReceiptsForDeposit(Deposit deposit) {
        List cashReceipts = this.retrieveCashReceipts(deposit);
        for (Object o: cashReceipts) {
            CashReceiptDocument crDoc = (CashReceiptDocument)o;
            crDoc.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL);
            documentService.updateDocument(crDoc);
        }
    }
    
    /**
     * @see org.kuali.module.financial.service.CashManagementService#applyCashieringTransaction(org.kuali.module.financial.document.CashManagementDocument, org.kuali.module.financial.bo.CashieringTransaction)
     */
    public void applyCashieringTransaction(CashManagementDocument cmDoc) {
        if (cmDoc.getCashDrawer() == null) {
            cmDoc.setCashDrawer(cashDrawerService.getByWorkgroupName(cmDoc.getWorkgroupName(), false));
        }
        CashieringTransactionRule transactionRule = new CashieringTransactionRule();
        transactionRule.setCashDrawerService(cashDrawerService);
        if (transactionRule.processCashieringTransactionApplicationRules(cmDoc)) {
            this.transferChecksToCashManagementDocument(cmDoc, cmDoc.getCurrentTransaction());
            this.saveChecks(cmDoc);
            this.completeNewItemInProcess(cmDoc.getCurrentTransaction());
            if (cmDoc.getCurrentTransaction().getNewItemInProcess() != null) {
                this.saveNewItemInProcess(cmDoc, cmDoc.getCurrentTransaction());
            }
            this.saveExisingItemsInProcess(cmDoc, cmDoc.getCurrentTransaction());
            this.saveMoneyInCash(cmDoc, cmDoc.getCurrentTransaction());
            this.saveMoneyOutCash(cmDoc, cmDoc.getCurrentTransaction());
            this.updateCashDrawer(cmDoc.getCashDrawer(), cmDoc.getCurrentTransaction());
            cmDoc.resetCurrentTransaction();
        }
    }
    
    /**
     * This method puts money from the money in portion of the transaction into the cash drawer, and takes money from the
     * money out portion of the cash drawer out
     * @param drawer the cash drawer to operate on
     * @param trans the transaction that is the operation
     */
    private void updateCashDrawer(CashDrawer drawer, CashieringTransaction trans) {
        // add money in to cash drawer
        if (!trans.getMoneyInCurrency().isEmpty()) {
            drawer.addCurrency(trans.getMoneyInCurrency());
        }
        if (!trans.getMoneyInCoin().isEmpty()) {
            drawer.addCoin(trans.getMoneyInCoin());
        }
        
        // subtract money out from cash drawer
        if (!trans.getMoneyOutCurrency().isEmpty()) {
            drawer.removeCurrency(trans.getMoneyOutCurrency());
        }
        if (!trans.getMoneyOutCoin().isEmpty()) {
            drawer.removeCoin(trans.getMoneyOutCoin());
        }
        
        businessObjectService.save(drawer);
    }
    
    private void completeNewItemInProcess(CashieringTransaction trans) {
        if (trans.getNewItemInProcess().isPopulated()) {
            trans.getNewItemInProcess().setItemRemainingAmount(trans.getNewItemInProcess().getItemAmount());
        } else {
            trans.setNewItemInProcess(null); // we don't want to save it or deal with it
        }
    }
    
    private void saveChecks(CashManagementDocument cmDoc) {
        if (cmDoc.getChecks() != null) {
            for (Check check: cmDoc.getChecks()) {
                check.setDocumentNumber(cmDoc.getDocumentNumber());
                check.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
                check.setCashieringRecordSource(KFSConstants.CheckSources.CASH_MANAGEMENT);
                businessObjectService.save(check);
            }
        }
    }
    
    private void transferChecksToCashManagementDocument(CashManagementDocument cmDoc, CashieringTransaction trans) {
        for (Check check: trans.getMoneyInChecks()) {
            check.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            check.setCashieringRecordSource(KFSConstants.CheckSources.CASH_MANAGEMENT);
            check.setDocumentNumber(cmDoc.getDocumentNumber());
            cmDoc.addCheck(check);
        }
    }
    
    /**
     * This methods checks if data was actually entered for the new item in process; if so, it saves that item in process
     * @param cmDoc the cash management doc that the new item in process will be associated with
     * @param trans the cashiering transaction that created the new item in process
     */
    private void saveNewItemInProcess(CashManagementDocument cmDoc, CashieringTransaction trans) {
        if (trans.getNewItemInProcess().isPopulated()) {
            trans.getNewItemInProcess().setItemRemainingAmount(trans.getNewItemInProcess().getItemAmount());
            trans.getNewItemInProcess().setItemReducedAmount(KualiDecimal.ZERO);
            trans.getNewItemInProcess().setWorkgroupName(cmDoc.getWorkgroupName());
            businessObjectService.save(trans.getNewItemInProcess());
            
            // put it in the list of open items in process
            trans.getOpenItemsInProcess().add(trans.getNewItemInProcess());
            
            CashDrawer drawer = cmDoc.getCashDrawer();
            if (drawer.getFinancialDocumentMiscellaneousAdvanceAmount() == null) {
                drawer.setFinancialDocumentMiscellaneousAdvanceAmount(trans.getNewItemInProcess().getItemAmount());
            } else {
                drawer.setFinancialDocumentMiscellaneousAdvanceAmount(drawer.getFinancialDocumentMiscellaneousAdvanceAmount().add(trans.getNewItemInProcess().getItemAmount()));
            }
        }
    }
    
    /**
     * This method checks the cashiering transaction to see if any open items in process were at least partially paid back;
     * it then saves the changes
     * @param cmDoc the cash management document that the items in process will be associated with
     * @param trans the cashiering transaction
     */
    private void saveExisingItemsInProcess(CashManagementDocument cmDoc, CashieringTransaction trans) {
        if (trans.getOpenItemsInProcess() != null) {
            CashDrawer drawer = cmDoc.getCashDrawer();
            
            for (CashieringItemInProcess itemInProc: trans.getOpenItemsInProcess()) {
                if (itemInProc.getCurrentPayment() != null && !itemInProc.getCurrentPayment().equals(KualiDecimal.ZERO)) {
                    itemInProc.setItemRemainingAmount(itemInProc.getItemRemainingAmount().subtract(itemInProc.getCurrentPayment()));
                    itemInProc.setItemReducedAmount(itemInProc.getItemReducedAmount().add(itemInProc.getCurrentPayment()));
                    if (drawer.getFinancialDocumentMiscellaneousAdvanceAmount() != null) {
                        drawer.setFinancialDocumentMiscellaneousAdvanceAmount(drawer.getFinancialDocumentMiscellaneousAdvanceAmount().subtract(itemInProc.getCurrentPayment()));
                    }
                    itemInProc.setCurrentPayment(new KualiDecimal(0));
                    if (itemInProc.getItemRemainingAmount().equals(KualiDecimal.ZERO)) {
                        itemInProc.setItemClosedDate(new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
                    }
                    businessObjectService.save(itemInProc);
                }
            }
        }
    }
    
    private void saveMoneyInCash(CashManagementDocument cmDoc, CashieringTransaction trans) {
        // get the cumulative money in coin for this doc
        CoinDetail cumulativeMoneyInCoin = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        // add the new money in coin
        cumulativeMoneyInCoin.add(trans.getMoneyInCoin());
        // save the cumulative
        businessObjectService.save(cumulativeMoneyInCoin);
        
        CurrencyDetail cumulativeMoneyInCurrency = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        cumulativeMoneyInCurrency.add(trans.getMoneyInCurrency());
        businessObjectService.save(cumulativeMoneyInCurrency);
    }
    
    private void saveMoneyOutCash(CashManagementDocument cmDoc, CashieringTransaction trans) {
        CoinDetail cumulativeMoneyOutCoin = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        cumulativeMoneyOutCoin.add(trans.getMoneyOutCoin());
        businessObjectService.save(cumulativeMoneyOutCoin);

        CurrencyDetail cumulativeMoneyOutCurrency = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        cumulativeMoneyOutCurrency.add(trans.getMoneyOutCurrency());
        businessObjectService.save(cumulativeMoneyOutCurrency);
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#getOpenItemsInProcess(org.kuali.module.financial.document.CashManagementDocument)
     */
    public List<CashieringItemInProcess> getOpenItemsInProcess(CashManagementDocument cmDoc) {
        List<CashieringItemInProcess> itemsInProcess = cashManagementDao.findOpenItemsInProcessByWorkgroupName(cmDoc.getWorkgroupName());
        return (itemsInProcess == null) ? new ArrayList<CashieringItemInProcess>() : itemsInProcess;
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#getRecentlyClosedItemsInProcess(org.kuali.module.financial.document.CashManagementDocument)
     */
    public List<CashieringItemInProcess> getRecentlyClosedItemsInProcess(CashManagementDocument cmDoc) {
        return cashManagementDao.findRecentlyClosedItemsInProcess(cmDoc.getWorkgroupName());
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#generateMasterCoinDetail(org.kuali.module.financial.document.CashManagementDocument)
     */
    public CoinDetail generateMasterCoinDetail(CashManagementDocument cmDoc) {
        CoinDetail masterDetail = new CoinDetail();
        masterDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        masterDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        masterDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_MASTER);
        
        masterDetail.zeroOutAmounts();

        CoinDetail cashReceiptDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        if (cashReceiptDetail != null) {
            masterDetail.add(cashReceiptDetail);
        }

        CoinDetail depositDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        if (depositDetail != null) {
            masterDetail.subtract(depositDetail);
        }
        
        CoinDetail moneyInDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        if (moneyInDetail != null) {
            masterDetail.add(moneyInDetail);
        }
        
        CoinDetail moneyOutDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        if (moneyOutDetail != null) {
            masterDetail.subtract(moneyOutDetail);
        }
        
        return masterDetail;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#generateMasterCurrencyDetail(org.kuali.module.financial.document.CashManagementDocument)
     */
    public CurrencyDetail generateMasterCurrencyDetail(CashManagementDocument cmDoc) {
        CurrencyDetail masterDetail = new CurrencyDetail();
        masterDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        masterDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        masterDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_MASTER);
        
        masterDetail.zeroOutAmounts();
        
        CurrencyDetail cashReceiptDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        if (cashReceiptDetail != null) {
            masterDetail.add(cashReceiptDetail);
        }

        CurrencyDetail depositDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        if (depositDetail != null) {
            masterDetail.add(depositDetail);
        }
        
        CurrencyDetail moneyInDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        if (moneyInDetail != null) {
            masterDetail.add(moneyInDetail);
        }
        
        CurrencyDetail moneyOutDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        if (moneyOutDetail != null) {
            masterDetail.add(moneyOutDetail);
        }
        
        return masterDetail;
    }
    
    /**
     * Grab the currency and coin detail for final deposits
     * @param cmDoc the cash management document which has deposits to populate
     */
    public void populateCashDetailsForDeposit(CashManagementDocument cmDoc) {
        // if this ever gets changed so that each deposit has currency/coin lines, then
        // we can just do this with the ORM, which would be *much* easier
        for (Deposit d: cmDoc.getDeposits()) {
            if (d.getDepositTypeCode().equals(DepositConstants.DEPOSIT_TYPE_FINAL)) {
                if (d.getDepositedCurrency() == null) {
                    d.setDepositedCurrency(cashManagementDao.findCurrencyDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, CurrencyCoinSources.DEPOSITS));
                }
                if (d.getDepositedCoin() == null) {
                    d.setDepositedCoin(cashManagementDao.findCoinDetailByCashieringRecordSource(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, CurrencyCoinSources.DEPOSITS));
                }
            }
        }
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#selectCashieringChecksForDeposit(java.lang.String, java.lang.Integer)
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber) {
        return cashManagementDao.selectCashieringChecksForDeposit(documentNumber, depositLineNumber);
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#selectUndepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber) {
        return cashManagementDao.selectUndepositedCashieringChecks(documentNumber);
    }
    
    /**
     * @see org.kuali.module.financial.service.CashManagementService#selectDepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectDepositedCashieringChecks(String documentNumber) {
        return cashManagementDao.selectDepositedCashieringChecks(documentNumber);
    }


    /**
     * Total up the amounts of all checks so far deposited as part of the given cash management document
     * @param documentNumber the id of a cash management document
     * @return the total of cashiering checks deposited so far as part of that document
     */
    public KualiDecimal calculateDepositedCheckTotal(String documentNumber) {
        KualiDecimal total = new KualiDecimal(0);
        for (Check check: cashManagementDao.selectDepositedCashieringChecks(documentNumber)) {
            if (check != null && check.getAmount() != null && check.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                total = total.add(check.getAmount());
            }
        }
        return total;
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#calculateUndepositedCheckTotal(java.lang.String)
     */
    public KualiDecimal calculateUndepositedCheckTotal(String documentNumber) {
        KualiDecimal total = new KualiDecimal(0);
        for (Check check: cashManagementDao.selectUndepositedCashieringChecks(documentNumber)) {
            if (check != null && check.getAmount() != null && check.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                total = total.add(check.getAmount());
            }
        }
        return total;
    }


    /**
     * @see org.kuali.module.financial.service.CashManagementService#allowDocumentCancellation(org.kuali.module.financial.document.CashManagementDocument)
     */
    public boolean allowDocumentCancellation(CashManagementDocument cmDoc) {
        return !existCashReceipts(cmDoc) && !existCashieringChecks(cmDoc) && !existCashDetails(cmDoc);
    }
    
    /**
     * 
     * This method determines if any verified, interim, or final cash receipts currently exist
     * @param cmDoc the cash management document to find cash receipts associated with the workgroup of
     * @return true if there's some cash receipts that verified, interim, or final in this workgroup; false if otherwise
     */
    private boolean existCashReceipts(CashManagementDocument cmDoc) {
        List<CashReceiptDocument> cashReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getWorkgroupName(), new String[] {KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, KFSConstants.DocumentStatusCodes.CashReceipt.FINAL} );
        return cashReceipts != null && cashReceipts.size() > 0;
    }
    
    /**
     * 
     * This method determines if any populated currency or coin details exist for the given document
     * @param cmDoc a cash management document to find details
     * @return true if it finds populated currency or coin details, false if otherwise
     */
    private boolean existCashDetails(CashManagementDocument cmDoc) {
        boolean result = false;
        List<CurrencyDetail> currencyDetails = cashManagementDao.getAllCurrencyDetails(cmDoc.getDocumentNumber());
        if (currencyDetails != null && currencyDetails.size() > 0) {
            for (CurrencyDetail detail: currencyDetails) {
                result |= !detail.isEmpty();
            }
        }
        if (!result) {
            List<CoinDetail> coinDetails = cashManagementDao.getAllCoinDetails(cmDoc.getDocumentNumber());
            if (coinDetails != null && coinDetails.size() > 0) {
                for (CoinDetail detail: coinDetails) {
                    result |= !detail.isEmpty();
                }
            }
        }
        return result;
    }
    
    /**
     * 
     * This method determines if cashiering checks exist for the cash management document
     * @param cmDoc the cash management document to test
     * @return true if it finds some checks, false if otherwise
     */
    private boolean existCashieringChecks(CashManagementDocument cmDoc) {
        List<Check> undepositedChecks = this.selectUndepositedCashieringChecks(cmDoc.getDocumentNumber());
        List<Check> depositedChecks = cashManagementDao.selectDepositedCashieringChecks(cmDoc.getDocumentNumber());
        return (undepositedChecks != null && undepositedChecks.size() > 0) || (depositedChecks != null && depositedChecks.size() > 0);
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#selectNextAvailableCheckLineNumber(java.lang.String)
     */
    public Integer selectNextAvailableCheckLineNumber(String documentNumber) {
        return cashManagementDao.selectNextAvailableCheckLineNumber(documentNumber);
    }

    /**
     * @see org.kuali.module.financial.service.CashManagementService#getCashDetailsForFinalDeposit(java.lang.String)
     */
    public Map<Class, Object> getCashDetailsForFinalDeposit(String documentNumber) {
        CurrencyDetail finalDepositCurrencyDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        CoinDetail finalDepositCoinDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        Map<Class, Object> result = new HashMap<Class, Object>();
        if (finalDepositCurrencyDetail != null) {
            result.put(CurrencyDetail.class, finalDepositCurrencyDetail);
        }
        if (finalDepositCoinDetail != null) {
            result.put(CoinDetail.class, finalDepositCoinDetail);
        }
        return result;
    }


    // injected dependencies
    /**
     * @return current value of businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
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
     * @return current value of cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }

    /**
     * Sets the cashDrawerService attribute value.
     * 
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }

    /**
     * @return current value of documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return current value of dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    /**
     * Gets the cashManagementDao attribute. 
     * @return Returns the cashManagementDao.
     */
    public CashManagementDao getCashManagementDao() {
        return cashManagementDao;
    }

    /**
     * Sets the cashManagementDao attribute value.
     * @param cashManagementDao The cashManagementDao to set.
     */
    public void setCashManagementDao(CashManagementDao cashManagementDao) {
        this.cashManagementDao = cashManagementDao;
    }
}
