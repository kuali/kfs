/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.fp.businessobject.format.CashDrawerStatusCodeFormatter;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.dataaccess.CashManagementDao;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.exception.CashDrawerStateException;
import org.kuali.kfs.fp.exception.InvalidCashReceiptState;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.KFSConstants.CurrencyCoinSources;
import org.kuali.kfs.sys.KFSConstants.DepositConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the default implementation of the CashManagementService interface.
 */
@Transactional
public class CashManagementServiceImpl implements CashManagementService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashManagementServiceImpl.class); 
    
    private BusinessObjectService businessObjectService;
    private CashDrawerService cashDrawerService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private CashManagementDao cashManagementDao;
    private DataDictionaryService dataDictionaryService;

    /**
     * If a CMD is found that is associated with the CR document, then that CMD is returned; otherwise null is returned. 
     * Currently the relationships are:
     * <ul>
     * <li>(CashReceipt to CashReceiptHeader) is (1 to 1)</li>
     * <li>(CashReceiptHeader to DepositCashReceiptControl) is (1 to 1)</li>
     * <li>(DepositCashReceiptControl to Deposit) is (many to 1)</li>
     * <li>(Deposit to CashManagementDocument) is (many to 1)</li>
     * </ul>
     * 
     * @param documentId The id of the cash receipt document linked to the cash management document.
     * @return An instance of a CashManagementDocument matching the provided search criteria, or null if no value is found.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#getCashManagementDocumentForCashReceiptId(java.lang.String)
     */
    public CashManagementDocument getCashManagementDocumentForCashReceiptId(String documentId) {
        CashManagementDocument cmdoc = null;

        // get CashReceiptHeader for the CashReceipt, if any
        HashMap primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentId);
        CashReceiptDocument crDoc = (CashReceiptDocument) businessObjectService.findByPrimaryKey(getDataDictionaryService().getDocumentClassByTypeName(KFSConstants.FinancialDocumentTypeCodes.CASH_RECEIPT), primaryKeys);

        // get the DepositCashReceiptControl for the CashReceiptHeader
        if (crDoc != null) {
            List crcList = crDoc.getDepositCashReceiptControl();
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
     * This method creates a new cash management document and sets the provided values as attributes to the document.  
     * The steps followed to create a new cash management document are as follows:
     * <ul>
     * <li>Find the drawer for the campus code given.</li>
     * <li>Make sure the drawer is closed, force the drawer closed if it is not already closed.</li>
     * <li>Create the cash management document, set the provided values to the document and link it to the cash drawer</li>
     * </ul> 
     * 
     * If the campusCode or docDescription values are null, an IllegalArgumentException will be thrown.
     * 
     * TODO - annotation is not used or set at all in this method, remove it if appropriate.
     * 
     * @param campusCode The campus code of the cash drawer.
     * @param docDescription The document description to be set on the new cash management document.
     * @param annotation 
     * @return A new instance of a CashManagementDocument (not persisted).
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#createCashManagementDocument(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CashManagementDocument createCashManagementDocument(String campusCode, String docDescription, String annotation) {
        if (StringUtils.isBlank(campusCode)) {
            throw new IllegalArgumentException("invalid (blank) campus code");
        }
        if (StringUtils.isBlank(docDescription)) {
            throw new IllegalArgumentException("invalid (blank) docDescription");
        }

        // check user authorization
        Person user = GlobalVariables.getUserSession().getPerson();
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT);
        documentAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT, user);

        // check cash drawer
        CashDrawer cd = cashDrawerService.getByCampusCode(campusCode);
        if (cd == null) {
            throw new RuntimeException("No cash drawer exists for campus code "+campusCode+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus "+campusCode);
        }
        String controllingDocId = cd.getReferenceFinancialDocumentNumber();

        // KULEDOCS-1475: adding handling for two things which should never happen:
        // 1. CashDrawer is open or locked by document 'null'
        // 2. CashDrawer is open or locked by a document which doesn't exist
        if (!cd.isClosed() || cd.getStatusCode() == null) {
            boolean forceDrawerClosed = false;
            
            if (cd.getStatusCode() == null) {
                forceDrawerClosed = true;
            }

            if (StringUtils.isBlank(controllingDocId)) {
                forceDrawerClosed = true;
            }
            else if (!documentService.documentExists(controllingDocId)) {
                forceDrawerClosed = true;
            }

            if (forceDrawerClosed) {
                cashDrawerService.closeCashDrawer(cd);
                cd = cashDrawerService.getByCampusCode(campusCode);
                if (cd == null) {
                    throw new RuntimeException("No cash drawer exists for campus code "+campusCode+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus "+campusCode);
                }
            }
        }


        CashManagementDocument cmDoc = null;
        if (cd.isClosed()) {
            // create the document
            try {
                cmDoc = (CashManagementDocument) documentService.getNewDocument(CashManagementDocument.class);
                cmDoc.getDocumentHeader().setDocumentDescription(docDescription);
                cmDoc.setCampusCode(campusCode);
                cmDoc.setCashDrawer(cd);
                cmDoc.getCurrentTransaction().setCampusCode(cmDoc.getCampusCode());
                cmDoc.getCurrentTransaction().setReferenceFinancialDocumentNumber(cmDoc.getDocumentNumber());
                cmDoc.getCurrentTransaction().setOpenItemsInProcess(getOpenItemsInProcess(cmDoc));
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to create CashManagementDocument", e);
            }
        }
        else {
            CashDrawerStatusCodeFormatter f = new CashDrawerStatusCodeFormatter();

            throw new CashDrawerStateException(campusCode, controllingDocId, (String) f.format(CashDrawerConstants.STATUS_CLOSED), (String) f.format(cd.getStatusCode()));
        }

        return cmDoc;
    }
    
    /**
     * This method creates new cumulative currency and coin details for the document given.
     * 
     * @param cmDoc The cash management document the cumulative details will be associated with.
     * @param cashieringSource The cashiering record source for the new details.
     */
    public void createNewCashDetails(CashManagementDocument cmDoc, String cashieringStatus) {
        CoinDetail coinDetail = new CoinDetail();
        coinDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        coinDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        coinDetail.setCashieringStatus(cashieringStatus);
        businessObjectService.save(coinDetail);
        
        CurrencyDetail currencyDetail = new CurrencyDetail();
        currencyDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        currencyDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        currencyDetail.setCashieringStatus(cashieringStatus);
        businessObjectService.save(currencyDetail);
    }

    /**
     * This method adds a new deposit to a the given CashManagementDocument.  
     * <br/>
     * The following steps go into adding a deposit to a cash management document.
     * <ul>
     * <li>The given deposit parameters are validated.  
     * <li>The corresponding cash drawer is locked
     * <li>The given cashiering check records are turned into check records 
     * <li>The new deposit is created
     * <li>The deposit is added to the cash management document and persisted
     * <li>The list of cash receipts are associated with the new deposit
     * <li>The deposit is saved again to ensure all links and attributes of the deposit are set appropriately and persisted
     * <li>The drawer is unlocked
     * <ul>
     * 
     * @param cashManagementDoc The document to have the deposit added to.
     * @param depositTicketNumber The ticket number of the deposit being added.
     * @param bankAccount The bank account on the deposit.
     * @param selectedCashReceipts The collection of cash receipts associated with the new deposit.
     * @param selectedCashieringChecks The collection of checks associated with the new deposit.
     * @param isFinalDeposit A flag used to identify if a deposit is the final deposit to be added to a cash management document.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#addInterimDeposit(org.kuali.kfs.fp.document.CashManagementDocument,
     *      java.lang.String, org.kuali.kfs.fp.businessobject.BankAccount, java.util.List)
     */
    @SuppressWarnings("deprecation")
    public void addDeposit(CashManagementDocument cashManagementDoc, String depositTicketNumber, Bank bank, List selectedCashReceipts, List selectedCashieringChecks, boolean isFinalDeposit) {
        validateDepositParams(cashManagementDoc, bank, selectedCashReceipts);

        String depositTypeCode = DepositConstants.DEPOSIT_TYPE_INTERIM;
        if (isFinalDeposit) {
            depositTypeCode = DepositConstants.DEPOSIT_TYPE_FINAL;
        }

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

        // create the Deposit
        Deposit deposit = buildDeposit(cashManagementDoc, depositTypeCode, depositTicketNumber, bank, selectedCashReceipts, checksToSave);

        // attach the deposit to the document
        List deposits = cashManagementDoc.getDeposits();
        deposits.add(deposit);
        documentService.updateDocument(cashManagementDoc);

        // associate the CashReceipts with the deposit
        List dccList = new ArrayList();
        for (Iterator i = selectedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            FinancialSystemDocumentHeader dh = crDoc.getFinancialSystemDocumentHeader();

            // change the doc status if it is not interim
            String statusCode = isFinalDeposit ? DocumentStatusCodes.CashReceipt.FINAL : DocumentStatusCodes.CashReceipt.INTERIM;
            if (!dh.getFinancialDocumentStatusCode().equalsIgnoreCase(DocumentStatusCodes.CashReceipt.INTERIM)) {
                dh.setFinancialDocumentStatusCode(statusCode);
                documentService.updateDocument(crDoc);
            }

            DepositCashReceiptControl dcc = new DepositCashReceiptControl();
            dcc.setFinancialDocumentCashReceiptNumber(crDoc.getDocumentNumber());
            dcc.setFinancialDocumentDepositNumber(deposit.getDocumentNumber());
            dcc.setFinancialDocumentDepositLineNumber(deposit.getFinancialDocumentDepositLineNumber());
            
            dcc.setCashReceiptDocument(crDoc);
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

        // unlock the cashDrawer, if needed
        if (!isFinalDeposit) {
            cashDrawerService.unlockCashDrawer(cashManagementDoc.getCashDrawer(), cashManagementDoc.getDocumentNumber());
        }
    }

    /**
     * Validates the given Deposit parameters, throwing various (runtime) exceptions if errors exist.
     * 
     * @param cashManagementDoc The document the deposit will be added to.
     * @param bank The bank account of the deposit being added.
     * @param selectedCashReceipts The collection of cash receipts associated with the new deposit.
     */
    protected void validateDepositParams(CashManagementDocument cashManagementDoc, Bank bank, List<CashReceiptDocument> selectedCashReceipts) {
        if (cashManagementDoc == null) {
            throw new IllegalArgumentException("invalid (null) cashManagementDoc");
        }
        else if (!cashManagementDoc.getDocumentHeader().getWorkflowDocument().isSaved()) {
            throw new IllegalStateException("cashManagementDoc '" + cashManagementDoc.getDocumentNumber() + "' is not in 'saved' state");
        }
        else if (cashManagementDoc.hasFinalDeposit()) {
            throw new IllegalStateException("cashManagementDoc '" + cashManagementDoc.getDocumentNumber() + "' hasFinalDeposit");
        }
        if (bank == null) {
            throw new IllegalArgumentException("invalid (null) bank");
        }

        if (selectedCashReceipts == null) {
            throw new IllegalArgumentException("invalid (null) cashReceipts list");
        }
        else {
            for (CashReceiptDocument cashReceipt : selectedCashReceipts) {
                String statusCode = cashReceipt.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
                //if (!StringUtils.equals(statusCode, DocumentStatusCodes.CashReceipt.VERIFIED)) {
                if (!StringUtils.equals(statusCode, DocumentStatusCodes.CashReceipt.VERIFIED) && !StringUtils.equals(statusCode, DocumentStatusCodes.CashReceipt.INTERIM)) {
                    throw new InvalidCashReceiptState("cash receipt document " + cashReceipt.getDocumentNumber() + " has a status other than 'verified' or 'interim' ");
                }
            }
        }
    }

    /**
     * 
     * This method builds a new deposit object from the parameters provided.
     * 
     * @param cashManagementDoc The cash management document the deposit will be added to.
     * @param depositTypeCode The type code associated with the deposit.
     * @param depositTicketNumber The deposit ticket number to be set on the deposit object.
     * @param bank The bank account of the deposit.
     * @param selectedCashReceipts The cash receipts that make up the deposit.
     * @param selectedCashieringChecks The cashiering checks that make up the deposit.
     * @return A new instance of a deposit generated from all the parameters provided.
     */
    protected Deposit buildDeposit(CashManagementDocument cashManagementDoc, String depositTypeCode, String depositTicketNumber, Bank bank, List<CashReceiptDocument> selectedCashReceipts, List selectedCashieringChecks) {
        Deposit deposit = new Deposit();
        deposit.setDocumentNumber(cashManagementDoc.getDocumentNumber());
        deposit.setCashManagementDocument(cashManagementDoc);

        deposit.setDepositTypeCode(depositTypeCode);

        deposit.setDepositDate(dateTimeService.getCurrentSqlDate());

        deposit.setBank(bank);
        deposit.setDepositBankCode(bank.getBankCode());

        // derive the line number
        int lineNumber = cashManagementDoc.getNextDepositLineNumber();
        deposit.setFinancialDocumentDepositLineNumber(new Integer(lineNumber));

        // trim depositTicketNumber to empty, because the field is optional
        deposit.setDepositTicketNumber(StringUtils.trimToEmpty(depositTicketNumber));

        // total up the cash receipts
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator i = selectedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            if (crDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equalsIgnoreCase(CashReceipt.VERIFIED)) {
                total = total.add(crDoc.getTotalConfirmedCheckAmount());
            }
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
     * This method returns all undeposited checks as a map with the key of each value in the map equal to the sequence id
     * of the corresponding check.  
     * 
     * @param cmDoc The cash management doc to find undeposited checks for.
     * @return A map of checks keyed on sequence id.
     */
    protected Map<Integer, Check> getUndepositedChecksAsMap(CashManagementDocument cmDoc) {
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
     * This method cancels a cash management document, effectively nullifying all values and attributes associated with 
     * the document.  Canceling a CashManagementDocument results in the following:
     * <ul>
     * <li>Cancels (deletes) all deposits associated with the document.</li>
     * <li>Recloses the drawer</li>
     * <li>Remove all currency and coin records generated by the document.</li>
     * </ul>
     * <br>
     * NOTE: Method should only be called after the appropriate CashManagementDocumentRule has been successfully passed.
     * 
     * @param cmDoc The CashManagementDocument to be canceled.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#cancelCashManagementDocument(org.kuali.kfs.fp.document.CashManagementDocument)
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
        String unitName = cmDoc.getCampusCode();
        cashDrawerService.closeCashDrawer(cmDoc.getCashDrawer());

        // cleanup the CMDoc, but let the postprocessor itself save it
        cmDoc.setDeposits(new ArrayList());
        cmDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.CANCELLED);
        
        // kill off cumulative currency/coin detail records for this document (canceling the deposits kills the deposit records)
        String[] cashieringSourcesToDelete = { KFSConstants.CurrencyCoinSources.CASH_RECEIPTS, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT };
        for (String cashieringSourceToDelete : cashieringSourcesToDelete) {
            CurrencyDetail currencyDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, cashieringSourceToDelete);
            if (currencyDetail != null) {
                businessObjectService.delete(currencyDetail);
            }
            CoinDetail coinDetail = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, cashieringSourceToDelete);
            if (coinDetail != null) {
                businessObjectService.delete(coinDetail);
            }
        }
    }


    /**
     * This method cancels a given deposit.  This equates to the following:
     * <ul>
     * <li>Resetting all associated CashReceipts to a state of VERIFIED.</li>
     * <li>Update all associated cashiering checks to a be un-deposited.</li>
     * <li>Unlock the cash drawer if needed.</li>
     * <li>Delete the deposit.</li>
     * </ul>
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#cancelDeposit(org.kuali.kfs.fp.businessobject.Deposit)
     */
    public void cancelDeposit(Deposit deposit) {
        if (deposit == null) {
            throw new IllegalArgumentException("invalid (null) deposit");
        }

        // reload it, to forestall OptimisticLockExceptions
        deposit.refresh();

        // save campus name, for possible later use
        String depositCampus = deposit.getCashManagementDocument().getCampusCode();

        // update every CashReceipt associated with this Deposit
        List depositCashReceiptControls = deposit.getDepositCashReceiptControl();
        for (Iterator j = depositCashReceiptControls.iterator(); j.hasNext();) {
            DepositCashReceiptControl dcc = (DepositCashReceiptControl) j.next();
            if (!ObjectUtils.isNull(dcc)) {
                dcc.refreshReferenceObject("cashReceiptDocument");
                CashReceiptDocument crDoc = dcc.getCashReceiptDocument();
                if (!ObjectUtils.isNull(crDoc)) {
                    crDoc.refreshReferenceObject("documentHeader");
                    FinancialSystemDocumentHeader crdh = crDoc.getFinancialSystemDocumentHeader();
                    if (!ObjectUtils.isNull(crdh)) {
                        if (!(deposit.getDepositTypeCode().equalsIgnoreCase(DocumentStatusCodes.CashReceipt.FINAL) 
                                && crdh.getFinancialDocumentStatusCode().equalsIgnoreCase(DocumentStatusCodes.CashReceipt.INTERIM))) {
                            crdh.setFinancialDocumentStatusCode(DocumentStatusCodes.CashReceipt.VERIFIED);
                        }
                        documentService.updateDocument(crDoc);
                    }
                }
            }
        }
        
        // un-deposit all cashiering checks associated with the deposit
        List<Check> depositedChecks = selectCashieringChecksForDeposit(deposit.getDocumentNumber(), deposit.getFinancialDocumentDepositLineNumber());
        for (Check check: depositedChecks) {
            check.setFinancialDocumentDepositLineNumber(null);
        }
        businessObjectService.save(depositedChecks);

        // unlock the cashDrawer, if needed
        if (LOG.isDebugEnabled()) {
            LOG.debug("deposit deposit type = "+deposit.getDepositTypeCode()+"; constant = "+KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL+"; are they equal? "+deposit.getDepositTypeCode().equals(KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL));
        }
        if (deposit.getDepositTypeCode().equals(KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL)) {
            CashDrawer drawer = cashDrawerService.getByCampusCode(deposit.getCashManagementDocument().getCampusCode());
            CurrencyDetail currencyDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(deposit.getCashManagementDocument().getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
            if (currencyDetail != null) {
                drawer.addCurrency(currencyDetail);
                businessObjectService.delete(currencyDetail);
            }
            CoinDetail coinDetail = cashManagementDao.findCoinDetailByCashieringStatus(deposit.getCashManagementDocument().getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
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
     * This method performs the necessary steps to finalize a cash management document.  These steps include:
     * <ul>
     * <li>Finalize all associated cash receipts.
     * <li>Generate the master currency and coin details and persist them.
     * <li>Update the CashManagementDocument status to APPROVED.
     * </ul>
     * 
     * <br>
     * NOTE: Method should only be called after the appropriate CashManagementDocumentRule has been successfully passed
     * 
     * @param cmDoc The CashManagementDocument to be finalized.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#finalizeCashManagementDocument(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public void finalizeCashManagementDocument(CashManagementDocument cmDoc) {
        if (cmDoc == null) {
            throw new IllegalArgumentException("invalid (null) CashManagementDocument");
        }
        if (!cmDoc.hasFinalDeposit()) {
            throw new IllegalStateException("cmDoc " + cmDoc.getDocumentNumber() + " is missing a FinalDeposit");
        }

        String campusCode = cmDoc.getCampusCode();
        cashDrawerService.closeCashDrawer(campusCode);
        CashDrawer cd = cashDrawerService.getByCampusCode(campusCode);


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
                receipt.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.APPROVED);

                // persist
                documentService.updateDocument(receipt);
            }
        }
        
        // generate the master currency and coin details; save those
        CurrencyDetail masterCurrencyDetail = this.generateMasterCurrencyDetail(cmDoc);
        //businessObjectService.save(masterCurrencyDetail);
        CoinDetail masterCoinDetail = this.generateMasterCoinDetail(cmDoc);
        //businessObjectService.save(masterCoinDetail);
        generateMasterRecord(cmDoc, masterCurrencyDetail, masterCoinDetail);
        // finalize the CMDoc, but let the postprocessor save it
        cmDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.APPROVED);
    }
    
    /**
     * This method verifies that all cash receipts for the document are deposited.
     * 
     * @param cmDoc The cash management document to verify.
     * @return True if all CashReceipts are deposited, false otherwise.
     */
    public boolean allVerifiedCashReceiptsAreDeposited(CashManagementDocument cmDoc) {
        boolean result = true;
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        for (Object o: verifiedReceipts) {
            if (!verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument)o)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * This method returns a collection of cash receipts associated with the deposit given.
     * 
     * @param deposit The deposit to retrieve all the cash receipts for.
     * @return A collection of cash receipts associated with the deposit given.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#retrieveCashReceipts(org.kuali.kfs.fp.businessobject.Deposit)
     */
    public List<CashReceiptDocument> retrieveCashReceipts(Deposit deposit) {
        List<CashReceiptDocument> cashReceiptDocuments = new ArrayList<CashReceiptDocument>();

        if (ObjectUtils.isNull(deposit.getDepositCashReceiptControl()) || deposit.getDepositCashReceiptControl().isEmpty() ) {
            deposit.refreshReferenceObject("depositCashReceiptControl");
        }
        
        Map pkMap = new HashMap();
        for (Object dcrcAsObject : deposit.getDepositCashReceiptControl()) {
            final DepositCashReceiptControl dcrc = (DepositCashReceiptControl)dcrcAsObject;
            try {
                CashReceiptDocument crDoc = (CashReceiptDocument)documentService.getByDocumentHeaderId(dcrc.getFinancialDocumentCashReceiptNumber());
                final WorkflowDocument headerWorkflowDoc = crDoc.getDocumentHeader().getWorkflowDocument();
                crDoc.refreshReferenceObject("documentHeader");
                crDoc.getDocumentHeader().setWorkflowDocument(headerWorkflowDoc);
                cashReceiptDocuments.add(crDoc);
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Could not retrieve related Cash Receipts", we);
            }
        }

        return cashReceiptDocuments;
    }

    /**
     * Verifies if a given cash receipt is deposited as part of the given cash management document.
     * 
     * @param cmDoc The cash management document to search through.
     * @param crDoc The cash receipt to check  the deposited status of.
     * @return True if the given cash receipt document is deposited as part of the given cash management document, false otherwise.
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
     * This method turns the last interim deposit into the final deposit and locks the cash drawer.  A deposit is turned into
     * a final deposit by updating the deposit type code.
     * <br>
     * NOTE: This method throws an IllegalStateException if a final deposit already exists for this document or if there
     *       are any undeposited cash receipts.
     * 
     * @param cmDoc The cash management document to take deposits from for finalization.
     */
    public void finalizeLastInterimDeposit(CashManagementDocument cmDoc) {
        // if there's already a final deposit, throw an IllegalStateException
        if (cmDoc.hasFinalDeposit()) {
            throw new IllegalStateException("CashManagementDocument #"+cmDoc.getDocumentNumber()+" already has a final deposit");
        }
        // if there are still verified un-deposited cash receipts, throw an IllegalStateException
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
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
     * This method switches cash receipts to "final" status as opposed to "interim" status.
     * 
     * @param deposit The deposit the cash receipts are associated with.
     */
    protected void finalizeCashReceiptsForDeposit(Deposit deposit) {
        List cashReceipts = this.retrieveCashReceipts(deposit);
        for (Object o: cashReceipts) {
            CashReceiptDocument crDoc = (CashReceiptDocument)o;
            crDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL);
            documentService.updateDocument(crDoc);
        }
    }
    
    /**
     * This method applies the cashiering transaction to the given CashManagementDocument.  This is accomplished by 
     * retrieving a CashieringTransactionRule object and running the appropriate methods to process the cashiering 
     * application rules.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#applyCashieringTransaction(org.kuali.kfs.fp.document.CashManagementDocument, org.kuali.kfs.fp.businessobject.CashieringTransaction)
     * @see org.kuali.kfs.fp.document.validation.impl.CashieringTransactionRule#processCashieringTransactionApplicationRules(CashManagementDocument)
     */
    public void applyCashieringTransaction(CashManagementDocument cmDoc) {
        if (cmDoc.getCashDrawer() == null) {
            cmDoc.setCashDrawer(cashDrawerService.getByCampusCode(cmDoc.getCampusCode()));
        }
        
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
    
    /**
     * This method puts money from the "money in" portion of the transaction into the cash drawer, and takes money from the
     * "money out" portion of the cash drawer out.
     * 
     * @param drawer The cash drawer to operate on.
     * @param trans The transaction that is the operation.
     */
    protected void updateCashDrawer(CashDrawer drawer, CashieringTransaction trans) {
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
    
    /**
     * 
     * This method completes the new item in process by setting the item remaining amount equal to the item amount.
     * 
     * @param trans The transaction being performed.
     */
    protected void completeNewItemInProcess(CashieringTransaction trans) {
        if (trans.getNewItemInProcess().isPopulated()) {
            trans.getNewItemInProcess().setItemRemainingAmount(trans.getNewItemInProcess().getItemAmount());
        }
        else {
            trans.setNewItemInProcess(null); // we don't want to save it or deal with it
        }
    }
    
    /**
     * 
     * This method retrieves all the checks for the given document and persists them.
     * 
     * @param cmDoc The cash management document the checks will be saved against.
     */
    protected void saveChecks(CashManagementDocument cmDoc) {
        if (cmDoc.getChecks() != null) {
            for (Check check: cmDoc.getChecks()) {
                check.setDocumentNumber(cmDoc.getDocumentNumber());
                check.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
                check.setCashieringStatus(KFSConstants.CheckSources.CASH_MANAGEMENT);
                businessObjectService.save(check);
            }
        }
    }
    
    /**
     * 
     * This method retrieves the checks from the transaction and adds them to the cash management document.
     * 
     * @param cmDoc The document the checks will be transferred to.
     * @param trans The transaction the checks are associated with.
     */
    protected void transferChecksToCashManagementDocument(CashManagementDocument cmDoc, CashieringTransaction trans) {
        for (Check check: trans.getMoneyInChecks()) {
            check.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            check.setCashieringStatus(KFSConstants.CheckSources.CASH_MANAGEMENT);
            check.setDocumentNumber(cmDoc.getDocumentNumber());
            cmDoc.addCheck(check);
        }
    }
    
    /**
     * This methods checks if data was actually entered for the new item in process; if so, it saves that item in process.
     * 
     * @param cmDoc The cash management doc that the new item in process will be associated with.
     * @param trans The cashiering transaction that created the new item in process.
     */
    protected void saveNewItemInProcess(CashManagementDocument cmDoc, CashieringTransaction trans) {
        if (trans.getNewItemInProcess().isPopulated()) {
            trans.getNewItemInProcess().setItemRemainingAmount(trans.getNewItemInProcess().getItemAmount());
            trans.getNewItemInProcess().setItemReducedAmount(KualiDecimal.ZERO);
            trans.getNewItemInProcess().setCampusCode(cmDoc.getCampusCode());
            businessObjectService.save(trans.getNewItemInProcess());
            
            // put it in the list of open items in process
            trans.getOpenItemsInProcess().add(trans.getNewItemInProcess());
            
            CashDrawer drawer = cmDoc.getCashDrawer();
            if (drawer.getFinancialDocumentMiscellaneousAdvanceAmount() == null) {
                drawer.setFinancialDocumentMiscellaneousAdvanceAmount(trans.getNewItemInProcess().getItemAmount());
            }
            else {
                drawer.setFinancialDocumentMiscellaneousAdvanceAmount(drawer.getFinancialDocumentMiscellaneousAdvanceAmount().add(trans.getNewItemInProcess().getItemAmount()));
            }
        }
    }
    
    /**
     * This method checks the cashiering transaction to see if any open items in process were at least partially paid back;
     * it then saves the changes.
     * 
     * @param cmDoc The cash management document that the items in process will be associated with
     * @param trans The cashiering transaction the items in process are associated with.
     */
    protected void saveExisingItemsInProcess(CashManagementDocument cmDoc, CashieringTransaction trans) {
        if (trans.getOpenItemsInProcess() != null) {
            CashDrawer drawer = cmDoc.getCashDrawer();
            
            for (CashieringItemInProcess itemInProc: trans.getOpenItemsInProcess()) {
                if (itemInProc.getCurrentPayment() != null && !itemInProc.getCurrentPayment().equals(KualiDecimal.ZERO)) {
                    itemInProc.setItemRemainingAmount(itemInProc.getItemRemainingAmount().subtract(itemInProc.getCurrentPayment()));
                    itemInProc.setItemReducedAmount(itemInProc.getItemReducedAmount().add(itemInProc.getCurrentPayment()));
                    if (drawer.getFinancialDocumentMiscellaneousAdvanceAmount() != null) {
                        drawer.setFinancialDocumentMiscellaneousAdvanceAmount(drawer.getFinancialDocumentMiscellaneousAdvanceAmount().subtract(itemInProc.getCurrentPayment()));
                    }
                    itemInProc.setCurrentPayment(KualiDecimal.ZERO);
                    if (itemInProc.getItemRemainingAmount().equals(KualiDecimal.ZERO)) {
                        itemInProc.setItemClosedDate(new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
                    }
                    businessObjectService.save(itemInProc);
                }
            }
        }
    }
    
    /**
     * 
     * This method retrieves the amount of cash in the "money in" portion of the transaction and saves it to the 
     * cash management document.
     * 
     * @param cmDoc The cash management document that the cash will be saved to.
     * @param trans The cashiering transaction the cash is currently associated with.
     */
    protected void saveMoneyInCash(CashManagementDocument cmDoc, CashieringTransaction trans) {
        // get the cumulative money in coin for this doc
        CoinDetail cumulativeMoneyInCoin = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        // add the new money in coin
        cumulativeMoneyInCoin.add(trans.getMoneyInCoin());
        // save the cumulative
        businessObjectService.save(cumulativeMoneyInCoin);
        
        CurrencyDetail cumulativeMoneyInCurrency = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        cumulativeMoneyInCurrency.add(trans.getMoneyInCurrency());
        businessObjectService.save(cumulativeMoneyInCurrency);
    }
    
    /**
     * 
     * This method retrieves the amount of cash in the "money out" portion of the transaction and saves it to the 
     * cash management document.
     * 
     * @param cmDoc The cash management document that the cash will be saved to.
     * @param trans The cashiering transaction the cash is currently associated with.
     */
    protected void saveMoneyOutCash(CashManagementDocument cmDoc, CashieringTransaction trans) {
        CoinDetail cumulativeMoneyOutCoin = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        cumulativeMoneyOutCoin.add(trans.getMoneyOutCoin());
        businessObjectService.save(cumulativeMoneyOutCoin);

        CurrencyDetail cumulativeMoneyOutCurrency = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        cumulativeMoneyOutCurrency.add(trans.getMoneyOutCurrency());
        businessObjectService.save(cumulativeMoneyOutCurrency);
    }

    /**
     * This method retrieves a collection of open CashieringItemInProcess objects from the cash management document given 
     * and returns that collection.
     * 
     * @param cmDoc The document the open items in process will be retrieved from.
     * @return The collection of open items.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#getOpenItemsInProcess(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public List<CashieringItemInProcess> getOpenItemsInProcess(CashManagementDocument cmDoc) {
        List<CashieringItemInProcess> itemsInProcess = cashManagementDao.findOpenItemsInProcessByCampusCode(cmDoc.getCampusCode());
        return (itemsInProcess == null) ? new ArrayList<CashieringItemInProcess>() : itemsInProcess;
    }

    /**
     * This method retrieves a collection of recently closed CashieringItemInProcess objects from the cash management
     * document given and returns the collection.  
     * 
     * @param cmDoc The cash management document the recently closed items will be retrieved from.
     * @return The collection of recently closed items.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#getRecentlyClosedItemsInProcess(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public List<CashieringItemInProcess> getRecentlyClosedItemsInProcess(CashManagementDocument cmDoc) {
        return cashManagementDao.findRecentlyClosedItemsInProcess(cmDoc.getCampusCode());
    }

    /**
     * This method generates a master coin detail for the cash management document given.  A master coin detail is a CoinDetail
     * that represents the result of all the money in and out of the cash drawer via the given cash management document.  The
     * following formula is used to perform this calculation:
     * <ul>
     * <li>
     * "coin detail for cash receipt - coin detail for deposits + coin detail for money in - coin detail for money out"
     * </li>
     * </ul>
     * 
     * @param cmDoc The document the master coin detail will be generated from.
     * @return The resulting coin detail.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#generateMasterCoinDetail(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public CoinDetail generateMasterCoinDetail(CashManagementDocument cmDoc) {
        CoinDetail masterDetail = new CoinDetail();
        masterDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        masterDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        masterDetail.setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_MASTER);
        
        masterDetail.zeroOutAmounts();

        CoinDetail cashReceiptDetail = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        if (cashReceiptDetail != null) {
            masterDetail.add(cashReceiptDetail);
        }

        CoinDetail depositDetail = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        if (depositDetail != null) {
            masterDetail.subtract(depositDetail);
        }
        
        CoinDetail moneyInDetail = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        if (moneyInDetail != null) {
            masterDetail.add(moneyInDetail);
        }
        
        CoinDetail moneyOutDetail = cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        if (moneyOutDetail != null) {
            masterDetail.subtract(moneyOutDetail);
        }
        
        return masterDetail;
    }


    /**
     * This method generates a master currency detail for the cash management document given.  A master currency detail is a currencyDetail
     * that represents the result of all the money in and out of the cash drawer via the given cash management document.  The
     * following formula is used to perform this calculation:
     * <ul>
     * <li>
     * "currency detail for cash receipt - currency detail for deposits + currency detail for money in - currency detail for money out"
     * </li>
     * </ul>
     * 
     * @param cmDoc The document the master currency detail will be generated from.
     * @return The resulting currency detail.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#generateMasterCurrencyDetail(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public CurrencyDetail generateMasterCurrencyDetail(CashManagementDocument cmDoc) {
        CurrencyDetail masterDetail = new CurrencyDetail();
        masterDetail.setDocumentNumber(cmDoc.getDocumentNumber());
        masterDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
        masterDetail.setCashieringStatus(KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_MASTER);
        
        masterDetail.zeroOutAmounts();
        
        CurrencyDetail cashReceiptDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        if (cashReceiptDetail != null) {
            masterDetail.add(cashReceiptDetail);
        }

        CurrencyDetail depositDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        if (depositDetail != null) {
            masterDetail.subtract(depositDetail);
        }
        
        CurrencyDetail moneyInDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_IN);
        if (moneyInDetail != null) {
            masterDetail.add(moneyInDetail);
        }
        
        CurrencyDetail moneyOutDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_MANAGEMENT_OUT);
        if (moneyOutDetail != null) {
            masterDetail.subtract(moneyOutDetail);
        }
        
        return masterDetail;
    }
    
    /**
     * Populates the currency and coin detail for final deposits by setting the deposited currency or coin amount equal to the 
     * associated cashiering record currency or coin amount.
     * 
     * @param cmDoc The cash management document which has deposits to populate.
     */
    public void populateCashDetailsForDeposit(CashManagementDocument cmDoc) {
        // if this ever gets changed so that each deposit has currency/coin lines, then
        // we can just do this with the ORM, which would be *much* easier
        for (Deposit d: cmDoc.getDeposits()) {
            if (d.getDepositTypeCode().equals(DepositConstants.DEPOSIT_TYPE_FINAL)) {
                if (d.getDepositedCurrency() == null) {
                    d.setDepositedCurrency(cashManagementDao.findCurrencyDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, CurrencyCoinSources.DEPOSITS));
                }
                if (d.getDepositedCoin() == null) {
                    d.setDepositedCoin(cashManagementDao.findCoinDetailByCashieringStatus(cmDoc.getDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, CurrencyCoinSources.DEPOSITS));
                }
            }
        }
    }

    /**
     * This method retrieves the collection of cashiering checks associated with a given deposit.
     * 
     * @param documentNumber The id of the document to search for the deposit within.
     * @param depositLineNumber The line number of the deposit to be found.
     * @return A collection of checks for the deposit and document given.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#selectCashieringChecksForDeposit(java.lang.String, java.lang.Integer)
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber) {
        return cashManagementDao.selectCashieringChecksForDeposit(documentNumber, depositLineNumber);
    }

    /**
     * This method retrieves the collection of undeposited cashiering checks associated with the document given.
     * 
     * @param documentNumber The id of the document to search for the undeposited checks within.
     * @return A collection of any undeposited checks for the document given.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#selectUndepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber) {
        return cashManagementDao.selectUndepositedCashieringChecks(documentNumber);
    }
    
    /**
     * This method retrieves a collection of all deposited checks associated with the given document.
     * 
     * @param documentNumber The document to retrieve the deposited checks from.
     * @return A collection of all deposited checks for the document given.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#selectDepositedCashieringChecks(java.lang.String)
     */
    public List<Check> selectDepositedCashieringChecks(String documentNumber) {
        return cashManagementDao.selectDepositedCashieringChecks(documentNumber);
    }


    /**
     * Total up the amounts of all checks so far deposited as part of the given cash management document.
     * 
     * @param documentNumber The id of a cash management document.
     * @return The total amount of cashiering checks deposited so far as part of that document.
     */
    public KualiDecimal calculateDepositedCheckTotal(String documentNumber) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Check check: cashManagementDao.selectDepositedCashieringChecks(documentNumber)) {
            if (check != null && check.getAmount() != null && check.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                total = total.add(check.getAmount());
            }
        }
        return total;
    }

    /**
     * Calculates the total amount of all the undeposited checks for a cash management document.
     * 
     * @param documentNumber The id of the cash management document to pull the undeposited checks from.
     * @return The total amount of all undeposited checks for the document given.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#calculateUndepositedCheckTotal(java.lang.String)
     */
    public KualiDecimal calculateUndepositedCheckTotal(String documentNumber) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Check check: cashManagementDao.selectUndepositedCashieringChecks(documentNumber)) {
            if (check != null && check.getAmount() != null && check.getAmount().isGreaterThan(KualiDecimal.ZERO)) {
                total = total.add(check.getAmount());
            }
        }
        return total;
    }


    /**
     * This method determines if a document can be cancelled, by reviewing a set of criteria:
     * - do any cash receipts exist in this document?
     * - do any cashiering checks exist in this document?
     * - do any cash details exist in this document?
     * If any of these questions comes back as true, then the document cannot be canceled.
     * 
     * @param cmDoc The document that would be canceled.
     * @return True if the document can be canceled, false otherwise.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#allowDocumentCancellation(org.kuali.kfs.fp.document.CashManagementDocument)
     */
    public boolean allowDocumentCancellation(CashManagementDocument cmDoc) {
        return !existCashReceipts(cmDoc) && !existCashieringChecks(cmDoc) && !existCashDetails(cmDoc);
    }
    
    /**
     * This method determines if any verified, interim, or final cash receipts currently exist.
     * 
     * @param cmDoc The cash management document to find cash receipts associated with the campus of.
     * @return True if there's some cash receipts that verified, interim, or final in this campus; false if otherwise.
     */
    protected boolean existCashReceipts(CashManagementDocument cmDoc) {
        List<CashReceiptDocument> cashReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), new String[] {KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, KFSConstants.DocumentStatusCodes.CashReceipt.FINAL} );
        return cashReceipts != null && cashReceipts.size() > 0;
    }
    
    /**
     * This method determines if any populated currency or coin details exist for the given document.
     * 
     * @param cmDoc A cash management document to find details.
     * @return True if it finds populated currency or coin details, false if otherwise.
     */
    protected boolean existCashDetails(CashManagementDocument cmDoc) {
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
     * This method determines if cashiering checks exist for the cash management document.
     * 
     * @param cmDoc The cash management document to test.
     * @return True if it finds some checks, false if otherwise.
     */
    protected boolean existCashieringChecks(CashManagementDocument cmDoc) {
        List<Check> undepositedChecks = this.selectUndepositedCashieringChecks(cmDoc.getDocumentNumber());
        List<Check> depositedChecks = cashManagementDao.selectDepositedCashieringChecks(cmDoc.getDocumentNumber());
        return (undepositedChecks != null && undepositedChecks.size() > 0) || (depositedChecks != null && depositedChecks.size() > 0);
    }

    /**
     * This method retrieves the next available check line number from the document provided.
     * 
     * @param documentNumber The document to get the next check line number from.
     * @return The next available check line number.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#selectNextAvailableCheckLineNumber(java.lang.String)
     */
    public Integer selectNextAvailableCheckLineNumber(String documentNumber) {
        return cashManagementDao.selectNextAvailableCheckLineNumber(documentNumber);
    }

    /**
     * This method retrieves the cash details for the final deposit object.  The resulting map contains a CurrencyDetail and a
     * CoinDetail object, both keyed by the class of detail they represent (ie. CurrencyDetail.class is the map key for the 
     * CurrencyDetail of the document).
     * 
     * @param documentNumber The document the details will be generated from.
     * @return A map of the resulting cash details.  This map is keyed by the detail class object.
     * 
     * @see org.kuali.kfs.fp.document.service.CashManagementService#getCashDetailsForFinalDeposit(java.lang.String)
     */
    public Map<Class, Object> getCashDetailsForFinalDeposit(String documentNumber) {
        CurrencyDetail finalDepositCurrencyDetail = cashManagementDao.findCurrencyDetailByCashieringStatus(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        CoinDetail finalDepositCoinDetail = cashManagementDao.findCoinDetailByCashieringStatus(documentNumber, CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.DEPOSITS);
        Map<Class, Object> result = new HashMap<Class, Object>();
        if (finalDepositCurrencyDetail != null) {
            result.put(CurrencyDetail.class, finalDepositCurrencyDetail);
        }
        if (finalDepositCoinDetail != null) {
            result.put(CoinDetail.class, finalDepositCoinDetail);
        }
        return result;
    }
    
    /**
     * This method pulls out the master currency and coin details and sets them to the cashManagementDocument
     * @param cmDoc
     * @param masterCurrencyDetail
     * @param masterCoinDetail
     */
    public void generateMasterRecord(CashManagementDocument cmDoc, CurrencyDetail masterCurrencyDetail, CoinDetail masterCoinDetail) {
        cmDoc.setFinancialDocumentHundredDollarAmount(masterCurrencyDetail.getFinancialDocumentHundredDollarAmount());
        cmDoc.setFinancialDocumentFiftyDollarAmount(masterCurrencyDetail.getFinancialDocumentFiftyDollarAmount());
        cmDoc.setFinancialDocumentTwentyDollarAmount(masterCurrencyDetail.getFinancialDocumentTwentyDollarAmount());
        cmDoc.setFinancialDocumentTenDollarAmount(masterCurrencyDetail.getFinancialDocumentTenDollarAmount());
        cmDoc.setFinancialDocumentFiveDollarAmount(masterCurrencyDetail.getFinancialDocumentFiveDollarAmount());
        cmDoc.setFinancialDocumentTwoDollarAmount(masterCurrencyDetail.getFinancialDocumentTwoDollarAmount());
        cmDoc.setFinancialDocumentOneDollarAmount(masterCurrencyDetail.getFinancialDocumentOneDollarAmount());
        cmDoc.setFinancialDocumentOtherDollarAmount(masterCurrencyDetail.getFinancialDocumentOtherDollarAmount());
        
        cmDoc.setFinancialDocumentHundredCentAmount(masterCoinDetail.getFinancialDocumentHundredCentAmount());
        cmDoc.setFinancialDocumentFiftyCentAmount(masterCoinDetail.getFinancialDocumentFiftyCentAmount());
        cmDoc.setFinancialDocumentTwentyFiveCentAmount(masterCoinDetail.getFinancialDocumentTwentyFiveCentAmount());
        cmDoc.setFinancialDocumentTenCentAmount(masterCoinDetail.getFinancialDocumentTenCentAmount());
        cmDoc.setFinancialDocumentFiveCentAmount(masterCoinDetail.getFinancialDocumentFiveCentAmount());
        cmDoc.setFinancialDocumentOneCentAmount(masterCoinDetail.getFinancialDocumentOneCentAmount());
        cmDoc.setFinancialDocumentOtherCentAmount(masterCoinDetail.getFinancialDocumentOtherCentAmount());
    }

    // injected dependencies
    /**
     * Getter for retrieving an instance of the BusinessObjectService attribute.
     * 
     * @return Current value of businessObjectService.
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
     * Getter for retrieving an instance of the CashDrawerService attribute.
     * 
     * @return Current value of cashDrawerService.
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
     * Gets the documentService attribute.
     * 
     * @return Current value of documentService.
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
     * Gets the dateTimeService attribute.
     * 
     * @return Current value of dateTimeService.
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
     * 
     * @return Returns the cashManagementDao.
     */
    public CashManagementDao getCashManagementDao() {
        return cashManagementDao;
    }

    /**
     * Sets the cashManagementDao attribute value.
     * 
     * @param cashManagementDao The cashManagementDao to set.
     */
    public void setCashManagementDao(CashManagementDao cashManagementDao) {
        this.cashManagementDao = cashManagementDao;
    }


    /**
     * @return an implementation of the DataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the data dictionary service implementation
     * @param dataDictionaryService the implementation of the data dictionary service to use
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
}

