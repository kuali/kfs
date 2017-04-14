package edu.arizona.kfs.module.purap.service.impl;

import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.service.PurapUseTaxEntryArchiveService;
import edu.arizona.kfs.sys.KFSConstants;


public class PurapGeneralLedgerServiceImpl extends org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapGeneralLedgerServiceImpl.class);
	public static final String BANK_CODE = "bankCode";
	
	protected PurapUseTaxEntryArchiveService purapUseTaxEntryArchiveService;
	protected PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;
	
	
    @Override
    public void generateEntriesModifyPaymentRequest(PaymentRequestDocument preq) {
        LOG.debug("generateEntriesModifyPaymentRequest() started");

        Map<SourceAccountingLine, KualiDecimal> actualsPositive = new HashMap<SourceAccountingLine, KualiDecimal>();
        List<SourceAccountingLine> newAccountingLines = purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(preq.getItems());
        for (SourceAccountingLine newAccount : newAccountingLines) {
            actualsPositive.put(newAccount, newAccount.getAmount());
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesModifyPaymentRequest() actualsPositive: " + newAccount.getAccountNumber() + " = " + newAccount.getAmount());
            }
        }

        Map<SourceAccountingLine, KualiDecimal> actualsNegative = new HashMap<SourceAccountingLine, KualiDecimal>();
        List<AccountsPayableSummaryAccount> oldAccountingLines = purapAccountingService.getAccountsPayableSummaryAccounts(preq.getPurapDocumentIdentifier(), PurapConstants.CREDIT_MEMO_TYPE_LABELS.TYPE_PREQ);

        for (AccountsPayableSummaryAccount oldAccount : oldAccountingLines) {
            actualsNegative.put(oldAccount.generateSourceAccountingLine(), oldAccount.getAmount());
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesModifyPaymentRequest() actualsNegative: " + oldAccount.getAccountNumber() + " = " + oldAccount.getAmount());
            }
        }

        // Add the positive entries and subtract the negative entries
        Map<SourceAccountingLine, KualiDecimal> glEntries = new HashMap<SourceAccountingLine, KualiDecimal>();

        // Combine the two maps (copy all the positive entries)
        LOG.debug("generateEntriesModifyPaymentRequest() Combine positive/negative entries");
        glEntries.putAll(actualsPositive);

        for (Iterator<SourceAccountingLine> iter = actualsNegative.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine key = iter.next();

            KualiDecimal amt;
            if (glEntries.containsKey(key)) {
                amt = glEntries.get(key);
                amt = amt.subtract(actualsNegative.get(key));
            }
            else {
                amt = ZERO;
                amt = amt.subtract(actualsNegative.get(key));
            }
            glEntries.put(key, amt);
        }

		// need to include all accounts in the SummaryAccount, even if they're unchanged 	
		// the reason for this is to allow the use-tax to be regenerated for all accounting lines when the use tax entries are reversed and regenerated
        boolean hasNonZeroChange = false;
        List<SummaryAccount> summaryAccounts = new ArrayList<SummaryAccount>();
        for (Iterator<SourceAccountingLine> iter = glEntries.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = iter.next();
            KualiDecimal amount = glEntries.get(account);
            if (ZERO.compareTo(amount) != 0) {
            	hasNonZeroChange = true;
            }
            account.setAmount(amount);
            SummaryAccount sa = new SummaryAccount(account);
            summaryAccounts.add(sa);
        }

        LOG.debug("generateEntriesModifyPaymentRequest() Generate GL entries");
        if (hasNonZeroChange) {
        	generateEntriesPaymentRequest(preq, null, summaryAccounts, MODIFY_PAYMENT_REQUEST);
        }
    }
    
    
    /**
     * Creates the general ledger entries for Payment Request actions.
     *
     * @param preq Payment Request document to create entries
     * @param encumbrances List of encumbrance accounts if applies
     * @param accountingLines List of preq accounts to create entries
     * @param processType Type of process (create, modify, cancel)
     * @return Boolean returned indicating whether entry creation succeeded
     */
    @Override
    protected boolean generateEntriesPaymentRequest(PaymentRequestDocument preq, List encumbrances, List summaryAccounts, String processType) {
        LOG.debug("generateEntriesPaymentRequest() started");
        boolean success = true;
        preq.setGeneralLedgerPendingEntries(new ArrayList());

        /*
         * Can't let generalLedgerPendingEntryService just create all the entries because we need the sequenceHelper to carry over
         * from the encumbrances to the actuals and also because we need to tell the PaymentRequestDocumentRule customize entry
         * method how to customize differently based on if creating an encumbrance or actual.
         */
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(getNextAvailableSequence(preq.getDocumentNumber()));

        // when cancelling a PREQ, do not book encumbrances if PO is CLOSED
        if (encumbrances != null && !(CANCEL_PAYMENT_REQUEST.equals(processType) && PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(preq.getPurchaseOrderDocument().getApplicationDocumentStatus()))) {
            LOG.debug("generateEntriesPaymentRequest() generate encumbrance entries");
            if (CREATE_PAYMENT_REQUEST.equals(processType)) {
                // on create, use CREDIT code for encumbrances
                preq.setDebitCreditCodeForGLEntries(KFSConstants.GL_CREDIT_CODE);
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                // on cancel, use DEBIT code
                preq.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
            }
            else if (MODIFY_PAYMENT_REQUEST.equals(processType)) {
                // no encumbrances for modify
            }

            preq.setGenerateEncumbranceEntries(true);
            for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) iter.next();
                preq.generateGeneralLedgerPendingEntries(accountingLine, sequenceHelper);
                sequenceHelper.increment(); // increment for the next line
            }
        }

        if (ObjectUtils.isNotNull(summaryAccounts) && !summaryAccounts.isEmpty()) {
            LOG.debug("generateEntriesPaymentRequest() now book the actuals");
            preq.setGenerateEncumbranceEntries(false);

            if (CREATE_PAYMENT_REQUEST.equals(processType) || MODIFY_PAYMENT_REQUEST.equals(processType)) {
                // on create and modify, use DEBIT code
                preq.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                // on cancel, use CREDIT code
                preq.setDebitCreditCodeForGLEntries(KFSConstants.GL_CREDIT_CODE);
            }

            for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
                SummaryAccount summaryAccount = (SummaryAccount) iter.next();
                // only create the GLPE's if they're non-zero (the modify method could produce 0-amount differences
                if (!MODIFY_PAYMENT_REQUEST.equals(processType) || !ZERO.equals(summaryAccount.getAccount().getAmount())) {
	                preq.generateGeneralLedgerPendingEntries(summaryAccount.getAccount(), sequenceHelper);
	                sequenceHelper.increment(); // increment for the next line
                }
            }
            
            // reverse previous PREQ use tax entries, and store the number of GLPE's before the new Use Tax GLPE's are created
            if (MODIFY_PAYMENT_REQUEST.equals(processType)) {
                purapUseTaxEntryArchiveService.reversePaymentRequestUseTaxEntries(preq, sequenceHelper);
            }
            int glpeSizeBeforeTax = preq.getGeneralLedgerPendingEntries().size();

            // generate offset accounts for use tax if it exists (useTaxContainers will be empty if not a use tax document)
            List<UseTaxContainer> useTaxContainers = purapAccountingService.generateUseTaxAccount(preq);
            for (UseTaxContainer useTaxContainer : useTaxContainers) {
                PurApItemUseTax offset = useTaxContainer.getUseTax();
                List<SourceAccountingLine> accounts = useTaxContainer.getAccounts();
                for (SourceAccountingLine sourceAccountingLine : accounts) {
                    // Don't create use tax GLPEs for summary accounts that do not have use tax, for example federal withholding
                    for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
                        SummaryAccount summaryAccount = (SummaryAccount) iter.next();
                        if (summaryAccount.getAccount().getAccountNumber().equals(sourceAccountingLine.getAccountNumber())) {
                            preq.generateGeneralLedgerPendingEntries(sourceAccountingLine, sequenceHelper, useTaxContainer.getUseTax());
                            sequenceHelper.increment(); // increment for the next line
                            break;
                        }
                    }
                }
            }
            
            // store the newly created Use Tax GLPEs so that they may be reversed upon modification of a PREQ accounting lines during routing
            int glpeSize = preq.getGeneralLedgerPendingEntries().size();
            if (!CANCEL_PAYMENT_REQUEST.equals(processType)) {
                List<GeneralLedgerPendingEntry> taxGlpes = preq.getGeneralLedgerPendingEntries().subList(glpeSizeBeforeTax, glpeSize);
                purapUseTaxEntryArchiveService.archivePaymentRequestUseTaxPendingEntries(preq, taxGlpes);
            }

            // Manually save preq summary accounts
            if (MODIFY_PAYMENT_REQUEST.equals(processType)) {
                //for modify, regenerate the summary from the doc
                List<SummaryAccount> summaryAccountsForModify = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(preq);
                saveAccountsPayableSummaryAccounts(summaryAccountsForModify, preq.getPurapDocumentIdentifier(), PurapConstants.CREDIT_MEMO_TYPE_LABELS.TYPE_PREQ);
            }
            else {
                //for create and cancel, use the summary accounts
                saveAccountsPayableSummaryAccounts(summaryAccounts, preq.getPurapDocumentIdentifier(), PurapConstants.CREDIT_MEMO_TYPE_LABELS.TYPE_PREQ);
            }

            // manually save cm account change tables (CAMS needs this)
            if (CREATE_PAYMENT_REQUEST.equals(processType) || MODIFY_PAYMENT_REQUEST.equals(processType)) {
                SpringContext.getBean(PurapAccountRevisionService.class).savePaymentRequestAccountRevisions(preq.getItems(), preq.getPostingYearFromPendingGLEntries(), preq.getPostingPeriodCodeFromPendingGLEntries());
            }
            else if (CANCEL_PAYMENT_REQUEST.equals(processType)) {
                SpringContext.getBean(PurapAccountRevisionService.class).cancelPaymentRequestAccountRevisions(preq.getItems(), preq.getPostingYearFromPendingGLEntries(), preq.getPostingPeriodCodeFromPendingGLEntries());
            }
            
            // generate any document level GL entries (offsets or fee charges)
            // we would only want to do this when booking the actuals (not the encumbrances)
            if (preq.getGeneralLedgerPendingEntries() == null || preq.getGeneralLedgerPendingEntries().size() < 2) {
                LOG.warn("No gl entries for accounting lines.");
            } else {
                // Upon a modify, we need to skip re-assessing any fees
                // in fact, we need to skip making any of these entries since there could be a combination
                // of debits and credit entries in the entry list - this will cause problems if the first is a
                // credit since it uses that to determine the sign of all the other transactions

                // upon create, build the entries normally
                if ( CREATE_PAYMENT_REQUEST.equals(processType) ) {
                	paymentMethodGeneralLedgerPendingEntryService.generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(
                            preq,((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preq).getPaymentMethodCode(),preq.getBankCode(), KRADConstants.DOCUMENT_PROPERTY_NAME + "." + BANK_CODE, preq.getGeneralLedgerPendingEntry(0), false, false, sequenceHelper);
                } else if ( MODIFY_PAYMENT_REQUEST.equals(processType) ) {
                    // upon modify, we need to calculate the deltas here and pass them in so the appropriate adjustments are created
                    KualiDecimal bankOffsetAmount = KualiDecimal.ZERO;
                    Map<String,KualiDecimal> changesByChart = new HashMap<String, KualiDecimal>();
                    if (ObjectUtils.isNotNull(summaryAccounts) && !summaryAccounts.isEmpty()) {
                        for ( SummaryAccount a : (List<SummaryAccount>)summaryAccounts ) {
                            bankOffsetAmount = bankOffsetAmount.add(a.getAccount().getAmount());
                            if ( changesByChart.get( a.getAccount().getChartOfAccountsCode() ) == null ) {
                                changesByChart.put( a.getAccount().getChartOfAccountsCode(), a.getAccount().getAmount() );
                            } else {
                                changesByChart.put( a.getAccount().getChartOfAccountsCode(), changesByChart.get( a.getAccount().getChartOfAccountsCode() ).add( a.getAccount().getAmount() ) );
                            }
                        }
                    }

                    paymentMethodGeneralLedgerPendingEntryService.generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(
                            preq,((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preq).getPaymentMethodCode(),preq.getBankCode(), KRADConstants.DOCUMENT_PROPERTY_NAME + "." + BANK_CODE, preq.getGeneralLedgerPendingEntry(0), true, false, sequenceHelper, bankOffsetAmount, changesByChart );
                } else if ( CANCEL_PAYMENT_REQUEST.equals(processType) ) {
                    // if cancelling, need to back out all charges
                    paymentMethodGeneralLedgerPendingEntryService.generatePaymentMethodSpecificDocumentGeneralLedgerPendingEntries(
                            preq,((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preq).getPaymentMethodCode(),preq.getBankCode(), KRADConstants.DOCUMENT_PROPERTY_NAME + "." + BANK_CODE, preq.getGeneralLedgerPendingEntry(0), false, true, sequenceHelper );
                }
            }
            preq.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
            // ensure that they all have approved status like other PREQ entries (the payment method service can not assume this)
            for ( GeneralLedgerPendingEntry glpe : preq.getGeneralLedgerPendingEntries() ) {
                glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
            }
        }

        // Manually save GL entries for Payment Request and encumbrances
        saveGLEntries(preq.getGeneralLedgerPendingEntries());

        return success;
    }
    
    @Override
    protected boolean generateEntriesCreditMemo(VendorCreditMemoDocument cm, boolean isCancel) {
        LOG.debug("generateEntriesCreditMemo() started");

        cm.setGeneralLedgerPendingEntries(new ArrayList());

        boolean success = true;
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(getNextAvailableSequence(cm.getDocumentNumber()));

        if (!cm.isSourceVendor()) {
            LOG.debug("generateEntriesCreditMemo() create encumbrance entries for CM against a PO or PREQ (not vendor)");
            PurchaseOrderDocument po = null;
            if (cm.isSourceDocumentPurchaseOrder()) {
                LOG.debug("generateEntriesCreditMemo() PO type");
                po = purchaseOrderService.getCurrentPurchaseOrder(cm.getPurchaseOrderIdentifier());
            }
            else if (cm.isSourceDocumentPaymentRequest()) {
                LOG.debug("generateEntriesCreditMemo() PREQ type");
                po = purchaseOrderService.getCurrentPurchaseOrder(cm.getPaymentRequestDocument().getPurchaseOrderIdentifier());
            }

            // for CM cancel or create, do not book encumbrances if PO is CLOSED, but do update the amounts on the PO
            List encumbrances = getCreditMemoEncumbrance(cm, po, isCancel);
            if (!(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(po.getApplicationDocumentStatus()))) {
                if (encumbrances != null) {
                    cm.setGenerateEncumbranceEntries(true);

                    // even if generating encumbrance entries on cancel, call is the same because the method gets negative amounts
                    // from
                    // the map so Debits on negatives = a credit
                    cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);

                    for (Iterator iter = encumbrances.iterator(); iter.hasNext();) {
                        AccountingLine accountingLine = (AccountingLine) iter.next();
                        if (accountingLine.getAmount().compareTo(ZERO) != 0) {
                            cm.generateGeneralLedgerPendingEntries(accountingLine, sequenceHelper);
                            sequenceHelper.increment(); // increment for the next line
                        }
                    }
                }
            }
        }

        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccountsWithNoZeroTotalsNoUseTax(cm);
        if (summaryAccounts != null) {
            LOG.debug("generateEntriesCreditMemo() now book the actuals");
            cm.setGenerateEncumbranceEntries(false);

            if (!isCancel) {
                // on create, use CREDIT code
                cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                // on cancel, use DEBIT code
                cm.setDebitCreditCodeForGLEntries(KFSConstants.GL_DEBIT_CODE);
            }

            for (Iterator iter = summaryAccounts.iterator(); iter.hasNext();) {
                SummaryAccount summaryAccount = (SummaryAccount) iter.next();
                cm.generateGeneralLedgerPendingEntries(summaryAccount.getAccount(), sequenceHelper);
                sequenceHelper.increment(); // increment for the next line
            }
            // generate offset accounts for use tax if it exists (useTaxContainers will be empty if not a use tax document)
            List<UseTaxContainer> useTaxContainers = purapAccountingService.generateUseTaxAccount(cm);
            for (UseTaxContainer useTaxContainer : useTaxContainers) {
                PurApItemUseTax offset = useTaxContainer.getUseTax();
                List<SourceAccountingLine> accounts = useTaxContainer.getAccounts();
                for (SourceAccountingLine sourceAccountingLine : accounts) {
                    cm.generateGeneralLedgerPendingEntries(sourceAccountingLine, sequenceHelper, useTaxContainer.getUseTax());
                    sequenceHelper.increment(); // increment for the next line
                }

            }
            
            // generate any document level GL entries (offsets or fee charges)
            // we would only want to do this when booking the actuals (not the encumbrances)
            cm.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);

            // manually save cm account change tables (CAMS needs this)
            if (!isCancel) {
                SpringContext.getBean(PurapAccountRevisionService.class).saveCreditMemoAccountRevisions(cm.getItems(), cm.getPostingYearFromPendingGLEntries(), cm.getPostingPeriodCodeFromPendingGLEntries());
            }
            else {
                SpringContext.getBean(PurapAccountRevisionService.class).cancelCreditMemoAccountRevisions(cm.getItems(), cm.getPostingYearFromPendingGLEntries(), cm.getPostingPeriodCodeFromPendingGLEntries());
            }
        }

        saveGLEntries(cm.getGeneralLedgerPendingEntries());

        LOG.debug("generateEntriesCreditMemo() ended");
        return success;
    }
    
    public void setPurapUseTaxEntryArchiveService(PurapUseTaxEntryArchiveService purapUseTaxEntryArchiveService) {
        this.purapUseTaxEntryArchiveService = purapUseTaxEntryArchiveService;
    }
    
    public void setPaymentMethodGeneralLedgerPendingEntryService(PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService) {
        this.paymentMethodGeneralLedgerPendingEntryService  = paymentMethodGeneralLedgerPendingEntryService;
    }


	@Override
	public void generateEntriesApproveAmendPurchaseOrder(PurchaseOrderDocument po) {
		LOG.debug("generateEntriesApproveAmendPurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator<?> items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            // if invoice fields are null (as would be for new items), set fields to zero
            item.setItemInvoicedTotalAmount(item.getItemInvoicedTotalAmount() == null ? ZERO : item.getItemInvoicedTotalAmount());
            item.setItemInvoicedTotalQuantity(item.getItemInvoicedTotalQuantity() == null ? ZERO : item.getItemInvoicedTotalQuantity());

            if (!item.isItemActiveIndicator()) {
                // set outstanding encumbrance amounts to zero for inactive items
                item.setItemOutstandingEncumberedQuantity(ZERO);
                item.setItemOutstandingEncumberedAmount(ZERO);

                for (Iterator<PurApAccountingLine> iter = item.getSourceAccountingLines().iterator(); iter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
                    account.setItemAccountOutstandingEncumbranceAmount(ZERO);
                    account.setAlternateAmountForGLEntryCreation(ZERO);
                }
            }
            else {
                // Set quantities
                if (item.getItemQuantity() != null) {
                    item.setItemOutstandingEncumberedQuantity(item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity()));
                }
                else {
                    // if order qty is null, outstanding encumbered qty should be null
                    item.setItemOutstandingEncumberedQuantity(null);
                }

                // Set amount
                if (item.getItemOutstandingEncumberedQuantity() != null) {
                    //do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                    KualiDecimal itemEncumber = new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(item.getItemUnitPrice()));

                    //add tax for encumbrance
                    KualiDecimal itemTaxAmount = item.getItemTaxAmount() == null ? ZERO : item.getItemTaxAmount();
                    itemEncumber = itemEncumber.add(itemTaxAmount);

                    item.setItemOutstandingEncumberedAmount(itemEncumber);
                }
                else {
                    if (item.getItemUnitPrice() != null) {
                        item.setItemOutstandingEncumberedAmount(new KualiDecimal(item.getItemUnitPrice().subtract(item.getItemInvoicedTotalAmount().bigDecimalValue())));
                    }
                }
                //This section was modified for UAF-1156/UAF-1164
                for (Iterator<PurApAccountingLine> iter = item.getSourceAccountingLines().iterator(); iter.hasNext();) {
                    PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
                    account.setItemAccountOutstandingEncumbranceAmount(account.getAmount());
                    account.setAlternateAmountForGLEntryCreation(account.getItemAccountOutstandingEncumbranceAmount());
                }
                //end of modified section
            }
        }

        PurchaseOrderDocument oldPO = purchaseOrderService.getCurrentPurchaseOrder(po.getPurapDocumentIdentifier());

        if (oldPO == null) {
            throw new IllegalArgumentException("Current Purchase Order not found - poId = " + oldPO.getPurapDocumentIdentifier());
        }

        List<SourceAccountingLine> newAccounts = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly());
        List<SourceAccountingLine> oldAccounts = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(oldPO.getItemsActiveOnlySetupAlternateAmount());

        Map<SourceAccountingLine, KualiDecimal> combination = new HashMap<SourceAccountingLine, KualiDecimal>();

        // Add amounts from the new PO
        for (Iterator<SourceAccountingLine> iter = newAccounts.iterator(); iter.hasNext();) {
            SourceAccountingLine newAccount = iter.next();
            combination.put(newAccount, newAccount.getAmount());
        }

        LOG.info("generateEntriesApproveAmendPurchaseOrder() combination after the add");
        for (Iterator<SourceAccountingLine> iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = iter.next();
            LOG.info("generateEntriesApproveAmendPurchaseOrder() " + element + " = " + (combination.get(element)).floatValue());
        }

        // Subtract the amounts from the old PO
        for (Iterator<SourceAccountingLine> iter = oldAccounts.iterator(); iter.hasNext();) {
            SourceAccountingLine oldAccount = iter.next();
            if (combination.containsKey(oldAccount)) {
                KualiDecimal amount = combination.get(oldAccount);
                amount = amount.subtract(oldAccount.getAmount());
                combination.put(oldAccount, amount);
            }
            else {
                combination.put(oldAccount, ZERO.subtract(oldAccount.getAmount()));
            }
        }

        LOG.debug("generateEntriesApproveAmendPurchaseOrder() combination after the subtract");
        for (Iterator<SourceAccountingLine> iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine element = iter.next();
            LOG.info("generateEntriesApproveAmendPurchaseOrder() " + element + " = " + (combination.get(element)).floatValue());
        }

        List<SourceAccountingLine> encumbranceAccounts = new ArrayList<SourceAccountingLine>();
        for (Iterator<SourceAccountingLine> iter = combination.keySet().iterator(); iter.hasNext();) {
            SourceAccountingLine account = iter.next();
            KualiDecimal amount = combination.get(account);
            if (ZERO.compareTo(amount) != 0) {
                account.setAmount(amount);
                encumbranceAccounts.add(account);
            }
        }

        po.setGlOnlySourceAccountingLines(encumbranceAccounts);
        generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
        saveGLEntries(po.getGeneralLedgerPendingEntries());
        LOG.debug("generateEntriesApproveAmendPo() gl entries created; exit method");
	}


	@Override
	public void generateEntriesClosePurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesClosePurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator<?> items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            KualiDecimal itemAmount = null;
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateEntriesClosePurchaseOrder() " + logItmNbr + " Calculate based on amounts");
            }
            itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO : item.getItemOutstandingEncumberedAmount();

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if (itemAmount.compareTo(ZERO) != 0) {
                Collections.sort((List) item.getSourceAccountingLines());
                for (Iterator<PurApAccountingLine> iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                    	//section modified to comply with UAF-3391 criteria
                        accountTotal = accountTotal.add(acct.getItemAccountOutstandingEncumbranceAmount());
                        acct.setAlternateAmountForGLEntryCreation(acct.getItemAccountOutstandingEncumbranceAmount());
                        //end of section modified
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generateEntriesClosePurchaseOrder() difference: " + logItmNbr + " " + difference);
                    }

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    }
                    else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }

        po.setGlOnlySourceAccountingLines(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly()));
        if (shouldGenerateGLPEForPurchaseOrder(po)) {
            generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
            saveGLEntries(po.getGeneralLedgerPendingEntries());
            LOG.debug("generateEntriesClosePurchaseOrder() gl entries created; exit method");
        }

        // Set outstanding encumbered quantity/amount on items
        for (Iterator<?> items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.setItemOutstandingEncumberedQuantity(KualiDecimal.ZERO);
            }
            item.setItemOutstandingEncumberedAmount(KualiDecimal.ZERO);
            List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
            for (PurApAccountingLine purApAccountingLine : sourceAccountingLines) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) purApAccountingLine;
                account.setItemAccountOutstandingEncumbranceAmount(KualiDecimal.ZERO);
            }
        }

        LOG.debug("generateEntriesClosePurchaseOrder() exit method");
	}

    @Override
    public void generateEntriesReopenPurchaseOrder(PurchaseOrderDocument po) {
        LOG.debug("generateEntriesReopenPurchaseOrder() started");

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity());
                item.setItemOutstandingEncumberedQuantity(item.getItemQuantity().subtract(item.getItemInvoicedTotalQuantity()));
                item.setItemOutstandingEncumberedAmount(new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(item.getItemUnitPrice())));
            } else {
                item.setItemOutstandingEncumberedAmount(item.getTotalAmount().subtract(item.getItemInvoicedTotalAmount()));
            }
            List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
            for (PurApAccountingLine purApAccountingLine : sourceAccountingLines) {
                PurchaseOrderAccount account = (PurchaseOrderAccount) purApAccountingLine;
                account.setItemAccountOutstandingEncumbranceAmount(new KualiDecimal(item.getItemOutstandingEncumberedAmount().bigDecimalValue().multiply(account.getAccountLinePercent()).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue())));
            }
        }// endfor

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = po.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            KualiDecimal itemAmount = null;
            if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateEntriesReopenPurchaseOrder() " + logItmNbr + " Calculate based on amounts");
                }
                itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO : item.getItemOutstandingEncumberedAmount();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateEntriesReopenPurchaseOrder() " + logItmNbr + " Calculate based on quantities");
                }
                // do math as big decimal as doing it as a KualiDecimal will cause the item price to round to 2 digits
                itemAmount = getItemAmount(item);
            }

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if (itemAmount.compareTo(ZERO) != 0) {
                // Sort accounts
                Collections.sort((List) item.getSourceAccountingLines());

                for (Iterator iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                        KualiDecimal acctAmount = itemAmount.multiply(new KualiDecimal(acct.getAccountLinePercent().toString())).divide(PurapConstants.HUNDRED);
                        accountTotal = accountTotal.add(acctAmount);
                        acct.setAlternateAmountForGLEntryCreation(acctAmount);
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("generateEntriesReopenPurchaseOrder() difference: " + logItmNbr + " " + difference);
                    }

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    } else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }// endfor

        po.setGlOnlySourceAccountingLines(purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(po.getItemsActiveOnly()));
        if (shouldGenerateGLPEForPurchaseOrder(po)) {
            generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(po);
            saveGLEntries(po.getGeneralLedgerPendingEntries());
            LOG.debug("generateEntriesReopenPurchaseOrder() gl entries created; exit method");
        }
        LOG.debug("generateEntriesReopenPurchaseOrder() no gl entries created because the amount is 0; exit method");
    }

    private KualiDecimal getItemAmount(PurchaseOrderItem item) {
        BigDecimal scaledItemOutstandingEncumberedQuantity = getScaledBigDecimal(item.getItemOutstandingEncumberedQuantity());
        BigDecimal scaledItemUnitPrice = getScaledBigDecimal(item.getItemUnitPrice());
        BigDecimal scaledItemAmount = scaledItemOutstandingEncumberedQuantity.multiply(scaledItemUnitPrice);
        KualiDecimal retval = new KualiDecimal(scaledItemAmount); // Default out for safety, this most likely will be updated again

        BigDecimal originalTaxTotal = getScaledBigDecimal(item.getItemTaxAmount()); // Obtain the tax total -- this was calculated against (itemCount * unitPrice)
        BigDecimal originalItemCount = getScaledBigDecimal(item.getItemQuantity()); // The original number of items
        BigDecimal perItemPrice = getScaledBigDecimal(item.getItemUnitPrice()); // The price for one item
        BigDecimal totalPrice = originalItemCount.multiply(perItemPrice); // Calculate total for n items

        // Calculate tax rate, apply tax rate to this item's price, add the new tax to the item amount
        if (totalPrice.compareTo(BigDecimal.ZERO) != 0) { // Avoid divide by zero
            BigDecimal taxRatePercent = originalTaxTotal.divide(totalPrice, 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal reencumberedTax = scaledItemAmount.multiply(taxRatePercent);
            scaledItemAmount = scaledItemAmount.add(reencumberedTax);
            retval = new KualiDecimal(scaledItemAmount);
        }

        return retval;
    }

    private BigDecimal getScaledBigDecimal(KualiDecimal kualiDecimal) {
        if (kualiDecimal == null) {
            return getScaledBigDecimal(BigDecimal.ZERO);
        }
        return getScaledBigDecimal(kualiDecimal.bigDecimalValue());
    }

    private BigDecimal getScaledBigDecimal(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return BigDecimal.ZERO.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

}