package edu.arizona.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.arizona.kfs.sys.KFSConstants;
import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import edu.arizona.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableSummaryAccount;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.module.purap.util.UseTaxContainer;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;

import edu.arizona.kfs.module.purap.service.PurapUseTaxEntryArchiveService;


public class PurapGeneralLedgerServiceImpl extends org.kuali.kfs.module.purap.service.impl.PurapGeneralLedgerServiceImpl {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapGeneralLedgerServiceImpl.class);
	
	protected PurapUseTaxEntryArchiveService purapUseTaxEntryArchiveService;
	
	
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
        }


        // Manually save GL entries for Payment Request and encumbrances
        saveGLEntries(preq.getGeneralLedgerPendingEntries());

        return success;
    }
    
    public void setPurapUseTaxEntryArchiveService(PurapUseTaxEntryArchiveService purapUseTaxEntryArchiveService) {
        this.purapUseTaxEntryArchiveService = purapUseTaxEntryArchiveService;
    }
}