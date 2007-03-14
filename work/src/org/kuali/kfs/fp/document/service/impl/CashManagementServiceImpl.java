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
import org.kuali.PropertyConstants;
import org.kuali.Constants.CashDrawerConstants;
import org.kuali.Constants.DepositConstants;
import org.kuali.Constants.DocumentStatusCodes;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.web.struts.form.CashDrawerStatusCodeFormatter;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class CashManagementServiceImpl implements CashManagementService {
    private BusinessObjectService businessObjectService;
    private CashDrawerService cashDrawerService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;


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
        primaryKeys.put(PropertyConstants.DOCUMENT_NUMBER, documentId);
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
        String documentTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(CashManagementDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(documentTypeName);
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
                cashDrawerService.closeCashDrawer(unitName);
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
     * @see org.kuali.module.financial.service.CashManagementService#addInterimDeposit(org.kuali.module.financial.document.CashManagementDocument,
     *      java.lang.String, org.kuali.module.financial.bo.BankAccount, java.util.List)
     */
    @SuppressWarnings("deprecation")
    public void addDeposit(CashManagementDocument cashManagementDoc, String depositTicketNumber, BankAccount bankAccount, List selectedCashReceipts, boolean isFinalDeposit) {
        validateDepositParams(cashManagementDoc, bankAccount, selectedCashReceipts);

        String depositTypeCode = DepositConstants.DEPOSIT_TYPE_INTERIM;
        if (isFinalDeposit) {
            depositTypeCode = DepositConstants.DEPOSIT_TYPE_FINAL;
        }

        //
        // lock the cashDrawer
        cashDrawerService.lockCashDrawer(cashManagementDoc.getWorkgroupName(), cashManagementDoc.getDocumentNumber());


        //
        // create the Deposit
        Deposit deposit = buildDeposit(cashManagementDoc, depositTypeCode, depositTicketNumber, bankAccount, selectedCashReceipts);

        // attach it to the document
        List deposits = cashManagementDoc.getDeposits();
        deposits.add(deposit);
        documentService.updateDocument(cashManagementDoc);

        // associate the CashReceipts with it
        List dccList = new ArrayList();
        for (Iterator i = selectedCashReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument crDoc = (CashReceiptDocument) i.next();
            DocumentHeader dh = crDoc.getDocumentHeader();

            String statusCode = null;
            if (isFinalDeposit) {
                statusCode = DocumentStatusCodes.CashReceipt.FINAL;
            }
            else {
                statusCode = DocumentStatusCodes.CashReceipt.INTERIM;
            }
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


        //
        // unlock the cashDrawer, if needed
        if (!isFinalDeposit) {
            cashDrawerService.unlockCashDrawer(cashManagementDoc.getWorkgroupName(), cashManagementDoc.getDocumentNumber());
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
        else if (selectedCashReceipts.isEmpty()) {
            throw new IllegalArgumentException("invalid (empty) cashReceipts list");
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

    private Deposit buildDeposit(CashManagementDocument cashManagementDoc, String depositTypeCode, String depositTicketNumber, BankAccount bankAccount, List<CashReceiptDocument> selectedCashReceipts) {
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
            total = total.add(crDoc.getTotalDollarAmount());
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
        cashDrawerService.closeCashDrawer(cmDoc.getWorkgroupName());

        // cleanup the CMDoc, but let the postprocessor itself save it
        cmDoc.setDeposits(new ArrayList());
        cmDoc.getDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.CANCELLED);
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

        // unlock the cashDrawer, if needed
        if (deposit.getDepositTypeCode() == DepositConstants.DEPOSIT_TYPE_FINAL) {
            cashDrawerService.unlockCashDrawer(depositWorkgroup, deposit.getDocumentNumber());
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
        CashDrawer cd = cashDrawerService.getByWorkgroupName(workgroupName, false);
        if (!cd.isClosed()) {
            throw new IllegalStateException("cashDrawer for workgroup '" + workgroupName + "' should already have been closed");
        }


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

        // finalize the CMDoc, but let the postprocessor save it
        cmDoc.getDocumentHeader().setFinancialDocumentStatusCode(DocumentStatusCodes.APPROVED);
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
}
