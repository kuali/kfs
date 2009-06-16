/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccount;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountsPayableServiceImpl implements AccountsPayableService {

    private PurapAccountingService purapAccountingService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private DocumentService documentService;
    private PurapService purapService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private PurchaseOrderService purchaseOrderService;
    private AccountService accountService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#getExpiredOrClosedAccountList(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public HashMap<String, ExpiredOrClosedAccountEntry> getExpiredOrClosedAccountList(AccountsPayableDocument document) {

        // Retrieve a list of accounts and replacement accounts, where accounts or closed or expired.
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccounts = expiredOrClosedAccountsList(document);

        return expiredOrClosedAccounts;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#generateExpiredOrClosedAccountNote(org.kuali.kfs.module.purap.document.AccountsPayableDocument,
     *      java.util.HashMap)
     */
    public void generateExpiredOrClosedAccountNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {

        // create a note of all the replacement accounts
        if (!expiredOrClosedAccountList.isEmpty()) {
            addContinuationAccountsNote(document, expiredOrClosedAccountList);
        }

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#generateExpiredOrClosedAccountWarning(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public void generateExpiredOrClosedAccountWarning(AccountsPayableDocument document) {

        // get user
        Person user = GlobalVariables.getUserSession().getPerson();

        // get parameter to see if fiscal officers may see the continuation account warning
        String showContinuationAccountWaringFO = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS);

        // get parameter to see if ap users may see the continuation account warning
        String showContinuationAccountWaringAP = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS);

        // versus doing it in their respective documents (preq, credit memo)
        // document is past full entry and
        // user is a fiscal officer and a system parameter is set to allow viewing
        // and if the continuation account indicator is set
        if (purapService.isFullDocumentEntryCompleted(document) &&
            (isFiscalUser(document, user) && "Y".equals(showContinuationAccountWaringFO)) && (document.isContinuationAccountIndicator())) {

            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
        }

        // document is past full entry and
        // user is an AP User and a system parameter is set to allow viewing
        // and if the continuation account indicator is set
        if (purapService.isFullDocumentEntryCompleted(document) &&
            (isAPUser(document, user) && "Y".equals(showContinuationAccountWaringAP)) && (document.isContinuationAccountIndicator())) {

            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
        }

    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#processExpiredOrClosedAccount(org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase,
     *      java.util.HashMap)
     */
    public void processExpiredOrClosedAccount(PurApAccountingLineBase acctLineBase, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {

        ExpiredOrClosedAccountEntry accountEntry = null;
        String acctKey = acctLineBase.getChartOfAccountsCode() + "-" + acctLineBase.getAccountNumber();

        if (expiredOrClosedAccountList.containsKey(acctKey)) {

            accountEntry = expiredOrClosedAccountList.get(acctKey);

            if (accountEntry.getOriginalAccount().isContinuationAccountMissing() == false) {
                acctLineBase.setChartOfAccountsCode(accountEntry.getReplacementAccount().getChartOfAccountsCode());
                acctLineBase.setAccountNumber(accountEntry.getReplacementAccount().getAccountNumber());
                acctLineBase.refreshReferenceObject("chart");
                acctLineBase.refreshReferenceObject("account");
            }
        }
    }

    /**
     * Creates and adds a note indicating accounts replaced and what they replaced and attaches it to the document.
     * 
     * @param document  The accounts payable document to which we're adding the note.
     * @param accounts  The HashMap where the keys are the string representations of the chart and account of the 
     *                  original account and the values are the ExpiredOrClosedAccountEntry.
     */
    private void addContinuationAccountsNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> accounts) {
        String noteText;
        StringBuffer sb = new StringBuffer("");
        ExpiredOrClosedAccountEntry accountEntry = null;
        ExpiredOrClosedAccount originalAccount = null;
        ExpiredOrClosedAccount replacementAccount = null;

        // List the entries using entrySet()
        Set entries = accounts.entrySet();
        Iterator it = entries.iterator();

        // loop through the accounts found to be expired/closed and add if they have a continuation account
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            accountEntry = (ExpiredOrClosedAccountEntry) entry.getValue();
            originalAccount = accountEntry.getOriginalAccount();
            replacementAccount = accountEntry.getReplacementAccount();

            // only print out accounts that were replaced and not missing a continuation account
            if (originalAccount.isContinuationAccountMissing() == false) {
                sb.append(" Account " + originalAccount.getAccountString() + " was replaced with account " + replacementAccount.getAccountString() + " ; ");
            }

        }

        // if a note was created, add it to the document
        if (sb.toString().length() > 0) {
            try {
                Note resetNote = documentService.createNoteFromDocument(document, sb.toString());
                documentService.addNoteToDocument(document, resetNote);
            }
            catch (Exception e) {
                throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
            }
        }
    }

    /**
     * Generates a list of replacement accounts for expired or closed accounts, as well as expired/closed accounts
     * without a continuation account.
     * 
     * @param document  The accounts payable document from which we're obtaining the purchase order id to be used
     *                  to obtain the purchase order document, whose accounts we'll use to generate the list of
     *                  replacement accounts for expired or closed accounts.
     * @return          The HashMap where the keys are the string representations of the chart
     *                  and account of the original account and the values are the ExpiredOrClosedAccountEntry.
     */
    private HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountsList(AccountsPayableDocument document) {

        HashMap<String, ExpiredOrClosedAccountEntry> list = new HashMap<String, ExpiredOrClosedAccountEntry>();
        ExpiredOrClosedAccountEntry entry = null;
        ExpiredOrClosedAccount originalAcct = null;
        ExpiredOrClosedAccount replaceAcct = null;
        String chartAccount = null;
        Integer POID = document.getPurchaseOrderIdentifier();

        PurchaseOrderDocument po = document.getPurchaseOrderDocument();

        if (po != null) {
            // get list of active accounts
            List<SourceAccountingLine> accountList = purapAccountingService.generateSummary(po.getItemsActiveOnly());            

            // loop through accounts
            for (SourceAccountingLine poAccountingLine : accountList) {

                Account account = accountService.getByPrimaryId(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber());

                entry = new ExpiredOrClosedAccountEntry();

                originalAcct = new ExpiredOrClosedAccount(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                if (!account.isActive()) {

                    // 1. if the account is closed, get the continuation account and add it to the list
                    Account continuationAccount = accountService.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());

                    if (continuationAccount == null) {
                        replaceAcct = new ExpiredOrClosedAccount();
                        originalAcct.setContinuationAccountMissing(true);

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    else {
                        replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    // 2. if the account is expired and the current date is <= 90 days from the expiration date, do nothing
                    // 3. if the account is expired and the current date is > 90 days from the expiration date, get the continuation
                    // account and add it to the list
                }
                else if (account.isExpired()) {
                    Account continuationAccount = accountService.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());
                    String expirationExtensionDays = parameterService.getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                    int expirationExtensionDaysInt = 3 * 30; // default to 90 days (approximately 3 months)

                    if (expirationExtensionDays.trim().length() > 0) {

                        expirationExtensionDaysInt = new Integer(expirationExtensionDays).intValue();
                    }
                    
                    // if account is C&G and expired then add to list.
                    if ((account.isForContractsAndGrants() && dateTimeService.dateDiff(account.getAccountExpirationDate(), dateTimeService.getCurrentDate(), true) > expirationExtensionDaysInt)) {

                        if (continuationAccount == null) {
                            replaceAcct = new ExpiredOrClosedAccount();
                            originalAcct.setContinuationAccountMissing(true);

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                        else {
                            replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                    }

                    // if account is not C&G, use the same account, do not replace
                }
            }
        }
        return list;
    }

    public HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountsList(PurchaseOrderDocument po) {

        HashMap<String, ExpiredOrClosedAccountEntry> list = new HashMap<String, ExpiredOrClosedAccountEntry>();
        ExpiredOrClosedAccountEntry entry = null;
        ExpiredOrClosedAccount originalAcct = null;
        ExpiredOrClosedAccount replaceAcct = null;
        String chartAccount = null;


        if (po != null) {
            // get list of active accounts
            List<SourceAccountingLine> accountList = purapAccountingService.generateSummary(po.getItemsActiveOnly());            

            // loop through accounts
            for (SourceAccountingLine poAccountingLine : accountList) {

                Account account = accountService.getByPrimaryId(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber());

                entry = new ExpiredOrClosedAccountEntry();

                originalAcct = new ExpiredOrClosedAccount(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                if (!account.isActive()) {

                    // 1. if the account is closed, get the continuation account and add it to the list
                    Account continuationAccount = accountService.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());

                    if (continuationAccount == null) {
                        replaceAcct = new ExpiredOrClosedAccount();
                        originalAcct.setContinuationAccountMissing(true);

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    else {
                        replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    // 2. if the account is expired and the current date is <= 90 days from the expiration date, do nothing
                    // 3. if the account is expired and the current date is > 90 days from the expiration date, get the continuation
                    // account and add it to the list
                }
                else if (account.isExpired()) {
                    Account continuationAccount = accountService.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());
                    String expirationExtensionDays = parameterService.getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                    int expirationExtensionDaysInt = 3 * 30; // default to 90 days (approximately 3 months)

                    if (expirationExtensionDays.trim().length() > 0) {

                        expirationExtensionDaysInt = new Integer(expirationExtensionDays).intValue();
                    }

                    // if account is C&G and expired then add to list.
                    if ((account.isForContractsAndGrants() && dateTimeService.dateDiff(account.getAccountExpirationDate(), dateTimeService.getCurrentDate(), true) > expirationExtensionDaysInt)) {

                        if (continuationAccount == null) {
                            replaceAcct = new ExpiredOrClosedAccount();
                            originalAcct.setContinuationAccountMissing(true);

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                        else {
                            replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                    }

                    // if account is not C&G, use the same account, do not replace
                }
            }
        }
        return list;
    }
    
    /**
     * Creates a chart-account string.
     * 
     * @param ecAccount  The account whose chart and account number we're going to use to create the resulting String for this method.
     * @return           The string representing the chart and account number of the given ecAccount.
     */
    private String createChartAccountString(ExpiredOrClosedAccount ecAccount) {
        StringBuffer buff = new StringBuffer("");

        buff.append(ecAccount.getChartOfAccountsCode());
        buff.append("-");
        buff.append(ecAccount.getAccountNumber());

        return buff.toString();
    }

    /**
     * Determines if the user is a fiscal officer.  Currently this only checks the doc and workflow status for approval requested
     * 
     * @param document  The document to be used to check the status code and whether the workflow approval is requested.
     * @param user      The current user.
     * @return          boolean true if the user is a fiscal officer.
     */
    private boolean isFiscalUser(AccountsPayableDocument document, Person user) {
        boolean isFiscalUser = false;

        if (PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(document.getStatusCode()) && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            isFiscalUser = true;
        }

        return isFiscalUser;
    }

    /**
     * Determines if the user is an AP user.  Currently this only checks the doc and workflow status for approval requested
     * 
     * @param document  The document to be used to check the status code and whether the workflow approval is requested.
     * @param user      The current user.
     * @return          boolean true if the user is an AP User.
     */
    private boolean isAPUser(AccountsPayableDocument document, Person user) {
        boolean isFiscalUser = false;

        if (PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(document.getStatusCode()) && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            isFiscalUser = true;
        }

        return isFiscalUser;
    }    
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#cancelAccountsPayableDocument(org.kuali.kfs.module.purap.document.AccountsPayableDocument, java.lang.String)
     */
    public void cancelAccountsPayableDocument(AccountsPayableDocument apDocument, String currentNodeName) {
        if (purapService.isFullDocumentEntryCompleted(apDocument)) {
            purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(apDocument);
        }
        AccountsPayableDocumentSpecificService accountsPayableDocumentSpecificService = apDocument.getDocumentSpecificService();
        accountsPayableDocumentSpecificService.updateStatusByNode(currentNodeName, apDocument);
        apDocument.refreshReferenceObject(PurapPropertyConstants.STATUS);

        // close/reopen purchase order.
        accountsPayableDocumentSpecificService.takePurchaseOrderCancelAction(apDocument);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#performLogicForFullEntryCompleted(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    public void performLogicForFullEntryCompleted(PurchasingAccountsPayableDocument purapDocument) {
        
        AccountsPayableDocument apDocument = (AccountsPayableDocument)purapDocument;
        AccountsPayableDocumentSpecificService accountsPayableDocumentSpecificService = apDocument.getDocumentSpecificService();
        // eliminate unentered items
        purapService.deleteUnenteredItems(apDocument);
        // change accounts from percents to dollars
        purapAccountingService.updateAccountAmounts(apDocument);
        // do GL entries for document creation
        accountsPayableDocumentSpecificService.generateGLEntriesCreateAccountsPayableDocument(apDocument);
        // save the document
        purapService.saveDocumentNoValidation(apDocument);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#updateItemList(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public void updateItemList(AccountsPayableDocument apDocument) {
        // don't run the following if past full entry
        if (purapService.isFullDocumentEntryCompleted(apDocument)) {
            return;
        }
        if (apDocument instanceof VendorCreditMemoDocument) {
            VendorCreditMemoDocument cm = (VendorCreditMemoDocument) apDocument;
            if (cm.isSourceDocumentPaymentRequest()) {
                // just update encumberances, items shouldn't change, get to them through po (or through preq)
                List<PaymentRequestItem> items = cm.getPaymentRequestDocument().getItems();
                for (PaymentRequestItem preqItem : items) {
                    // skip inactive and below the line
                    if (preqItem.getItemType().isAdditionalChargeIndicator()) {
                        continue;
                    }
                    PurchaseOrderItem poItem = preqItem.getPurchaseOrderItem();
                    CreditMemoItem cmItem = (CreditMemoItem) cm.getAPItemFromPOItem(poItem);
                    // take invoiced quantities from the lower of the preq and po if different
                    updateEncumberances(preqItem, poItem, cmItem);
                }

            }
            else if (cm.isSourceDocumentPurchaseOrder()) {
                PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(apDocument.getPurchaseOrderIdentifier());
                List<PurchaseOrderItem> poItems = po.getItems();
                List<CreditMemoItem> cmItems = cm.getItems();
                // iterate through the above the line poItems to find matching
                for (PurchaseOrderItem purchaseOrderItem : poItems) {
                    // skip inactive and below the line
                    if (purchaseOrderItem.getItemType().isAdditionalChargeIndicator()) {
                        continue;
                    }

                    CreditMemoItem cmItem = (CreditMemoItem) cm.getAPItemFromPOItem(purchaseOrderItem);
                    // check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
                    if (apDocument.getDocumentSpecificService().poItemEligibleForAp(apDocument, purchaseOrderItem)) {
                        // if eligible and not there - add
                        if (ObjectUtils.isNull(cmItem)) {
                            CreditMemoItem cmi = new CreditMemoItem(cm, purchaseOrderItem);
                            cmi.setPurapDocument(apDocument);
                            cmItems.add(cmi);
                        }
                        else {
                            // is eligible and on doc, update encumberances
                            // (this is only qty and amount for now NOTE we should also update other key fields, like description
                            // etc in case ammendment modified a line
                            updateEncumberance(purchaseOrderItem, cmItem);
                        }
                    }
                    else { // if not eligible and there - remove
                        if (ObjectUtils.isNotNull(cmItem)) {
                            cmItems.remove(cmItem);
                            // don't update encumberance
                            continue;
                        }
                    }

                }
            } // else do nothing
            return;

            // finally update encumbrances
        }
        else if (apDocument instanceof PaymentRequestDocument) {


            // get a fresh purchase order
            PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(apDocument.getPurchaseOrderIdentifier());
            PaymentRequestDocument preq = (PaymentRequestDocument) apDocument;

            List<PurchaseOrderItem> poItems = po.getItems();
            List<PaymentRequestItem> preqItems = preq.getItems();
            // iterate through the above the line poItems to find matching
            for (PurchaseOrderItem purchaseOrderItem : poItems) {
                // skip below the line
                if (purchaseOrderItem.getItemType().isAdditionalChargeIndicator()) {
                    continue;
                }
                PaymentRequestItem preqItem = (PaymentRequestItem) preq.getAPItemFromPOItem(purchaseOrderItem);
                // check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
                if (apDocument.getDocumentSpecificService().poItemEligibleForAp(apDocument, purchaseOrderItem)) {
                    // if eligible and not there - add
                    if (ObjectUtils.isNull(preqItem)) {
                        PaymentRequestItem pri = new PaymentRequestItem(purchaseOrderItem, preq);
                        pri.setPurapDocument(apDocument);
                        preqItems.add(pri);
                    }
                    else {
                        updatePossibleAmmendedFields(purchaseOrderItem, preqItem);
                    }
                }
                else { // if not eligible and there - remove
                    if (ObjectUtils.isNotNull(preqItem)) {
                        preqItems.remove(preqItem);
                    }
                }

            }
        }
    }

    /**
     * Updates fields that could've been changed on amendment.
     * 
     * @param sourceItem   The purchase order item from which we're getting the unit price, catalog number and description to be set in the destItem.
     * @param destItem     The payment request item to which we're setting the unit price, catalog number and description.
     */
    private void updatePossibleAmmendedFields(PurchaseOrderItem sourceItem, PaymentRequestItem destItem) {
        destItem.setPurchaseOrderItemUnitPrice(sourceItem.getItemUnitPrice());
        destItem.setItemCatalogNumber(sourceItem.getItemCatalogNumber());
        destItem.setItemDescription(sourceItem.getItemDescription());
    }

    /**
     * Updates encumberances.
     * 
     * @param preqItem  The payment request item from which we're obtaining the item quantity, unit price and extended price.
     * @param poItem    The purchase order item from which we're obtaining the invoice total quantity, unit price and invoice total amount.
     * @param cmItem    The credit memo item whose invoice total quantity, unit price and extended price are to be updated.
     */
    private void updateEncumberances(PaymentRequestItem preqItem, PurchaseOrderItem poItem, CreditMemoItem cmItem) {
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            cmItem.setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            cmItem.setPreqUnitPrice(poItem.getItemUnitPrice());
            cmItem.setPreqTotalAmount(poItem.getItemInvoicedTotalAmount());
        }
        else {
            cmItem.setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            cmItem.setPreqUnitPrice(preqItem.getItemUnitPrice());
            cmItem.setPreqTotalAmount(preqItem.getTotalAmount());
        }
    }

    /**
     * Updates the encumberance related fields.
     * 
     * @param purchaseOrderItem  The purchase order item from which we're obtaining the invoice total quantity, unit price and invoice total amount.
     * @param cmItem             The credit memo item whose invoice total quantity, unit price and extended price are to be updated.
     */
    private void updateEncumberance(PurchaseOrderItem purchaseOrderItem, CreditMemoItem cmItem) {
        cmItem.setPoInvoicedTotalQuantity(purchaseOrderItem.getItemInvoicedTotalQuantity());
        cmItem.setPreqUnitPrice(purchaseOrderItem.getItemUnitPrice());
        cmItem.setPoTotalAmount(purchaseOrderItem.getItemInvoicedTotalAmount());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.AccountsPayableService#purchaseOrderItemEligibleForPayment(org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem)
     */
    public boolean purchaseOrderItemEligibleForPayment(PurchaseOrderItem poi) {
        if (ObjectUtils.isNull(poi)) {
            throw new RuntimeException("item null in purchaseOrderItemEligibleForPayment ... this should never happen");
        }

        // if the po item is not active... skip it
        if (!poi.isItemActiveIndicator()) {
            return false;
        }

        ItemType poiType = poi.getItemType();

        if (poiType.isQuantityBasedGeneralLedgerIndicator()) {
            if (poi.getItemQuantity().isGreaterThan(poi.getItemInvoicedTotalQuantity())) {
                return true;
            }
            return false;
        }
        else { // not quantity based
            if (poi.getItemOutstandingEncumberedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
            return false;
        }
    }
    
}

