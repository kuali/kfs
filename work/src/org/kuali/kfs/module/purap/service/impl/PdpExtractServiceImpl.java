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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.MailService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountDetail;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.kuali.module.pdp.service.EnvironmentService;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.kuali.module.pdp.service.PaymentFileService;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.kuali.module.pdp.service.ReferenceService;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.bo.AccountsPayableItemBase;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PdpExtractService;
import org.kuali.module.purap.service.PurapRunDateService;
import org.kuali.module.purap.util.VendorGroupingHelper;
import org.kuali.module.vendor.VendorConstants;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Implementation of PdpExtractService
 */
@Transactional
public class PdpExtractServiceImpl implements PdpExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpExtractServiceImpl.class);

    private PaymentRequestService paymentRequestService;
    private BusinessObjectService businessObjectService;
    private PaymentFileService paymentFileService;
    private ParameterService parameterService;
    private CustomerProfileService customerProfileService;
    private DateTimeService dateTimeService;
    private UniversalUserService universalUserService;
    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private CreditMemoService creditMemoService;
    private ReferenceService referenceService;
    private DocumentService documentService;
    private MailService mailService;
    private EnvironmentService environmentService;
    private PurapRunDateService purapRunDateService;
    
    private PaymentStatus openPaymentStatus;

    // This should only be set to true when testing this system. Setting this to true will run the code but
    // won't set the extracted date on the credit memos or payment requests
    boolean testMode = false;

    /**
     * @see org.kuali.module.purap.service.PdpExtractService#extractImmediatePaymentsOnly()
     */
    public void extractImmediatePaymentsOnly() {
        LOG.debug("extractImmediatePaymentsOnly() started");
        
        Date processRunDate = dateTimeService.getCurrentDate();
        extractPayments(true, processRunDate);
    }

    /**
     * @see org.kuali.module.purap.service.PdpExtractService#extractPayments(Date)
     */
    public void extractPayments(Date runDate) {
        LOG.debug("extractPayments() started");

        extractPayments(false, runDate);
    }

    /**
     * Extracts payments from the database
     * 
     * @param immediateOnly whether to pick up immediate payments only
     * @param processRunDate time/date to use to put on the {@link Batch} that's created; and when immediateOnly is false, is also used as the maximum
     * allowed PREQ pay date when searching PREQ documents eligible to have payments extracted 
     */
    private void extractPayments(boolean immediateOnly, Date processRunDate) {
        LOG.debug("extractPayments() started");

        if (openPaymentStatus == null) {
            openPaymentStatus = (PaymentStatus) referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.OPEN);
        }

        String userId = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_USER_ID);
        UniversalUser uuser;
        try {
            uuser = universalUserService.getUniversalUserByAuthenticationUserId(userId);
        }
        catch (UserNotFoundException e) {
            LOG.error("extractPayments() Unable to find user " + userId);
            throw new IllegalArgumentException("Unable to find user " + userId);
        }
        PdpUser puser = new PdpUser(uuser);

        List<String> campusesToProcess = getChartCodes(immediateOnly, processRunDate);
        for (Iterator iter = campusesToProcess.iterator(); iter.hasNext();) {
            String campus = (String) iter.next();

            extractPaymentsForCampus(campus, puser, processRunDate, immediateOnly);
        }
    }

    /**
     * Handle a single campus
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     */
    private void extractPaymentsForCampus(String campusCode, PdpUser puser, Date processRunDate, boolean immediateOnly) {
        LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode, puser, processRunDate);
        Integer count = 0;
        BigDecimal totalAmount = new BigDecimal("0");

        // Do all the special ones
        Totals t = extractSpecialPaymentsForChart(campusCode, puser, processRunDate, batch, immediateOnly);
        count = count + t.count;
        totalAmount = totalAmount.add(t.totalAmount);

        if (!immediateOnly) {
            // Do all the regular ones (including credit memos)
            t = extractRegularPaymentsForChart(campusCode, puser, processRunDate, batch);
            count = count + t.count;
            totalAmount = totalAmount.add(t.totalAmount);
        }

        batch.setPaymentCount(count);
        batch.setPaymentTotalAmount(totalAmount);
        paymentFileService.saveBatch(batch);
        paymentFileService.sendLoadEmail(batch);
    }

    /**
     * Get all the payments that could be combined with credit memos
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @param batch
     * @return
     */
    private Totals extractRegularPaymentsForChart(String campusCode, PdpUser puser, Date processRunDate, Batch batch) {
        LOG.debug( "START - extractRegularPaymentsForChart()" );
        Totals t = new Totals();

        List<String> preqsWithOutstandingCreditMemos = new ArrayList<String>();
        
        // NEW PSEUDOCODE:
        // get list of all vendor id/detail IDs on eligible CMs
        // loop over vendor's IDs and get matching lists of CMs and PREQs
        // if net > 0, generate PDP payment groups and update the documents
        Set<VendorGroupingHelper> vendors = creditMemoService.getVendorsOnCreditMemosToExtract(campusCode); 

        java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());
        
        List<PaymentRequestDocument> prds = new ArrayList<PaymentRequestDocument>();
        List<CreditMemoDocument> cmds = new ArrayList<CreditMemoDocument>();
        for ( VendorGroupingHelper vendor : vendors ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Processing Vendor: " + vendor );
            }
            prds.clear();
            cmds.clear();
            KualiDecimal creditMemoAmount = KualiDecimal.ZERO;
            KualiDecimal paymentRequestAmount = KualiDecimal.ZERO;
        // Get all the matching credit memos
            Iterator<CreditMemoDocument> cmIter = creditMemoService.getCreditMemosToExtractByVendor(campusCode,vendor);
        while (cmIter.hasNext()) {
            CreditMemoDocument cmd = cmIter.next();
                creditMemoAmount = creditMemoAmount.add( cmd.getCreditMemoAmount() );
            cmds.add(cmd);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Added CM to bundle: " + cmd.getDocumentNumber() + " - $" + cmd.getCreditMemoAmount() );
                }
            }
            // get all matching payment requests
            Iterator<PaymentRequestDocument> pri = paymentRequestService.getPaymentRequestsToExtractByVendor(campusCode, vendor, onOrBeforePaymentRequestPayDate);
            while (pri.hasNext()) {
                PaymentRequestDocument prd = pri.next();
                paymentRequestAmount = paymentRequestAmount.add(prd.getGrandTotal());
                prds.add(prd);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Added PREQ to bundle: " + prd.getDocumentNumber() + " - $" + prd.getGrandTotal() );
            }
            }
            // check if there is anything left to pay
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Bundle Total: " + paymentRequestAmount.subtract(creditMemoAmount) );
            }
            if (paymentRequestAmount.compareTo(creditMemoAmount) >= 0) {
                // if so, create a payment group and add the CM and PREQ documents
                PaymentGroup pg = buildPaymentGroup(prds, cmds, batch);
                if(validatePaymentGroup(pg)) { 
                paymentGroupService.save(pg);
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( "Created PaymentGroup: " + pg.getId() );
                    }
                    
                t.count++;
                t.totalAmount = t.totalAmount.add(pg.getNetPaymentAmount());

                    // mark the CMs and PREQs as processed
                    for (CreditMemoDocument cm : cmds) {
                        updateCreditMemo(cm, puser, processRunDate);
                }
                    for (PaymentRequestDocument pr : prds) {
                        updatePaymentRequest(pr, puser, processRunDate);
                }
                } else {
                    LOG.debug( "bundle failed validation - NO BUNDLE CREATED" );
                    // stops PREQ documents from being processed by putting them on a "do not process" list
                    // until validation problems can be resolved
                    for ( PaymentRequestDocument doc : prds ) {
                        preqsWithOutstandingCreditMemos.add( doc.getDocumentNumber() );
            }
        }
            } else {
                LOG.debug( "bundle net < 0 - NO BUNDLE CREATED" );
                // put the PREQ documents in this bundle on a "do not process" list for this run
                // to prevent them from being picked up by the code below
                for ( PaymentRequestDocument doc : prds ) {
                    preqsWithOutstandingCreditMemos.add( doc.getDocumentNumber() );
                }
            }
        }

        LOG.debug( "processing PREQs without CMs" );

        // Get all the payment requests to process that do not have credit memos
        Iterator<PaymentRequestDocument> prIter = paymentRequestService.getPaymentRequestToExtractByChart(campusCode, onOrBeforePaymentRequestPayDate);
        while (prIter.hasNext()) {
            PaymentRequestDocument prd = prIter.next();
            // if in the list created above, don't create the payment group
            if ( !preqsWithOutstandingCreditMemos.contains( prd.getDocumentNumber() )  ) {
            PaymentGroup pg = processSinglePaymentRequestDocument(prd, batch, puser, processRunDate);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Added PREQ to solo bundle: " + prd.getDocumentNumber() + " - $" + prd.getGrandTotal() );
                }

            t.count = t.count + pg.getPaymentDetails().size();
            t.totalAmount = t.totalAmount.add(pg.getNetPaymentAmount());
        }
        }

        LOG.debug( "END - extractRegularPaymentsForChart()" );
        return t;
    }

    /**
     * Handle a single payment request with no credit memos
     * 
     * @param prd
     * @param batch
     * @param puser
     * @param processRunDate
     * @return
     */
    private PaymentGroup processSinglePaymentRequestDocument(PaymentRequestDocument prd, Batch batch, PdpUser puser, Date processRunDate) {
        List<PaymentRequestDocument> prds = new ArrayList<PaymentRequestDocument>();
        List<CreditMemoDocument> cmds = new ArrayList<CreditMemoDocument>();
        prds.add(prd);

        PaymentGroup pg = buildPaymentGroup(prds, cmds, batch);
        if(validatePaymentGroup(pg)) {
        paymentGroupService.save(pg);
        updatePaymentRequest(prd, puser, processRunDate);
        }

        return pg;
    }

    /**
     * 
     * This method...
     * @param key
     * @param itemsToProcess
     * @param typeCode
     * @return
     */
    private List<Payment> getByCombineKey(String key, List itemsToProcess, String typeCode) {
        List<Payment> p = new ArrayList<Payment>();
        for (Iterator iter = itemsToProcess.iterator(); iter.hasNext();) {
            Payment element = (Payment) iter.next();
            if (key.equals(element.combineKey())) {
                if ((typeCode == null) || (typeCode.equals(element.typeCode))) {
                    p.add(element);
                }
            }
        }
        return p;
    }

    /**
     * 
     * This method...
     * @param key
     * @param itemsToProcess
     * @return
     */
    private List<Payment> getByCombineKey(String key, List itemsToProcess) {
        return getByCombineKey(key, itemsToProcess, null);
    }

    /**
     * Get all the special payments for a campus and process them
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @param batch
     * @return
     */
    private Totals extractSpecialPaymentsForChart(String campusCode, PdpUser puser, Date processRunDate, Batch batch, boolean immediatesOnly) {
        Totals t = new Totals();

        Iterator<PaymentRequestDocument> prIter = null;
        if (immediatesOnly) {
            prIter = paymentRequestService.getImmediatePaymentRequestsToExtract(campusCode);
        }
        else {
            java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());
            prIter = paymentRequestService.getPaymentRequestsToExtractSpecialPayments(campusCode, onOrBeforePaymentRequestPayDate);
        }

        while (prIter.hasNext()) {
            PaymentRequestDocument prd = prIter.next();

            PaymentGroup pg = processSinglePaymentRequestDocument(prd, batch, puser, processRunDate);

            t.count = t.count + pg.getPaymentDetails().size();
            t.totalAmount = t.totalAmount.add(pg.getNetPaymentAmount());
        }

        return t;
    }

    /**
     * Mark a credit memo as extracted
     * 
     * @param cmd
     * @param puser
     * @param processRunDate
     */
    private void updateCreditMemo(CreditMemoDocument cmd, PdpUser puser, Date processRunDate) {
        if (!testMode) {
            try {
                CreditMemoDocument doc = (CreditMemoDocument) documentService.getByDocumentHeaderId(cmd.getDocumentNumber());
                doc.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
                creditMemoService.saveDocumentWithoutValidation(doc);
            }
            catch (WorkflowException e) {
                throw new IllegalArgumentException("Unable to retrieve credit memo: " + cmd.getDocumentNumber());
            }
        }
    }

    /**
     * Mark a payment request as extracted
     * 
     * @param prd
     * @param puser
     * @param processRunDate
     */
    private void updatePaymentRequest(PaymentRequestDocument prd, PdpUser puser, Date processRunDate) {
        if (!testMode) {
            try {
                PaymentRequestDocument doc = (PaymentRequestDocument) documentService.getByDocumentHeaderId(prd.getDocumentNumber());
                doc.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
                paymentRequestService.saveDocumentWithoutValidation(doc);
            }
            catch (WorkflowException e) {
                throw new IllegalArgumentException("Unable to retrieve payment request: " + prd.getDocumentNumber());
            }
        }
    }

    /**
     * Create the PDP payment group from a list of payment requests & credit memos
     * 
     * @param prds
     * @param cmds
     * @param batch
     * @return
     */
    private PaymentGroup buildPaymentGroup(List<PaymentRequestDocument> prds, List<CreditMemoDocument> cmds, Batch batch) {

        // There should always be at least one Payment Request Document in the list.
        PaymentGroup pg = null;
        if ( cmds.size() > 0 ) {
            CreditMemoDocument firstCmd = cmds.get(0);
            pg = populatePaymentGroup(firstCmd, batch);
        } else {
            PaymentRequestDocument firstPrd = prds.get(0);
            pg = populatePaymentGroup(firstPrd, batch);
        }

        for (PaymentRequestDocument pr : prds) {
            PaymentDetail pd = populatePaymentDetail(pr, batch);
            pg.addPaymentDetails(pd);
        }
        for (CreditMemoDocument cm : cmds) {
            PaymentDetail pd = populatePaymentDetail(cm, batch);
            pg.addPaymentDetails(pd);
        }

        return pg;
    }

    /**
     * 
     * This method...
     * @param pg
     * @return
     */
    private boolean validatePaymentGroup(PaymentGroup pg) {
        // Check to see if the payment group has too many note lines to be printed on a check
        List<PaymentDetail> payDetails = pg.getPaymentDetails();
        
        int noteLines = 0;
        int maxNoteLines = getMaxNoteLines();

        for(PaymentDetail pd : payDetails) {
            noteLines++; // Add one for each payment detail; summary of each detail is included on check and counts as a line
            noteLines += pd.getNotes().size(); // get all the possible notes for a given detail
        }
        
        if(noteLines > maxNoteLines) {
            
            // compile list of all doc numbers that make up payment group
            List<String> preqDocIds = new ArrayList<String>();
            List<String> cmDocIds = new ArrayList<String>();
            
            List<PaymentDetail> pds = pg.getPaymentDetails();
            for(PaymentDetail payDetail : pds) {
                if("CM".equals(payDetail.getFinancialDocumentTypeCode())) {
                    cmDocIds.add(payDetail.getCustPaymentDocNbr());
                } else {
                    preqDocIds.add(payDetail.getCustPaymentDocNbr());
                }
            }
            
            // send warning email and prevent group from being processed by returning null
            sendExceedsMaxNotesWarningEmail(cmDocIds, preqDocIds, noteLines, maxNoteLines);
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    private int getMaxNoteLines() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.MAX_NOTE_LINES);
    }

    /**
     * Create a PDP payment detail from a Credit Memo
     * 
     * @param cmd
     * @param batch
     * @return
     */
    private PaymentDetail populatePaymentDetail(CreditMemoDocument cmd, Batch batch) {
        PaymentDetail pd = new PaymentDetail();

        String invNbr = cmd.getCreditMemoNumber();
        if (invNbr.length() > 25) {
            invNbr = invNbr.substring(0, 25);
        }

        pd.setCustPaymentDocNbr(cmd.getDocumentNumber());
        pd.setInvoiceNbr(invNbr);
        if (cmd.getPurapDocumentIdentifier() != null) {
            pd.setPurchaseOrderNbr(cmd.getPurapDocumentIdentifier().toString());
        }
        if (cmd.getPurchaseOrderDocument() != null) {
            if (cmd.getPurchaseOrderDocument().getRequisitionIdentifier() != null) {
                pd.setRequisitionNbr(cmd.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
            }
            if (cmd.getDocumentHeader().getOrganizationDocumentNumber() != null) {
                pd.setOrganizationDocNbr(cmd.getDocumentHeader().getOrganizationDocumentNumber());
            }
        }
        pd.setFinancialDocumentTypeCode("CM");
        pd.setInvoiceDate(new Timestamp(cmd.getCreditMemoDate().getTime()));
        pd.setOrigInvoiceAmount(cmd.getCreditMemoAmount().bigDecimalValue().negate());

        pd.setNetPaymentAmount(cmd.getDocumentHeader().getFinancialDocumentTotalAmount().bigDecimalValue().negate());

        BigDecimal shippingAmount = new BigDecimal("0");
        BigDecimal discountAmount = new BigDecimal("0");
        BigDecimal creditAmount = new BigDecimal("0");
        BigDecimal debitAmount = new BigDecimal("0");
        for (Iterator iter = cmd.getItems().iterator(); iter.hasNext();) {
            CreditMemoItem item = (CreditMemoItem) iter.next();
            BigDecimal itemAmount = new BigDecimal("0");
            if (item.getExtendedPrice() != null) {
                itemAmount = item.getExtendedPrice().bigDecimalValue();
            }
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
                discountAmount = discountAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode())) {
                debitAmount = debitAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode())) {
                if (itemAmount.compareTo(new BigDecimal("0")) < 0) {
                    creditAmount = creditAmount.subtract(itemAmount);
                }
                else {
                    debitAmount = debitAmount.subtract(itemAmount);
                }
            }
        }

        pd.setInvTotDiscountAmount(discountAmount);
        pd.setInvTotShipAmount(shippingAmount);
        pd.setInvTotOtherCreditAmount(creditAmount);
        pd.setInvTotOtherDebitAmount(debitAmount);

        pd.setPrimaryCancelledPayment(Boolean.FALSE);        
        
        addAccounts(cmd, pd,"CM");
        addNotes(cmd, pd);
        return pd;
    }

    /**
     * Create a PDP Payment Detail from a Payment Request
     * 
     * @param prd
     * @param batch
     * @return
     */
    private PaymentDetail populatePaymentDetail(PaymentRequestDocument prd, Batch batch) {
        PaymentDetail pd = new PaymentDetail();

        pd.setCustPaymentDocNbr(prd.getDocumentNumber());

        String invNbr = prd.getInvoiceNumber();
        if (invNbr.length() > 25) {
            invNbr = invNbr.substring(0, 25);
        }
        pd.setInvoiceNbr(invNbr);
        if (prd.getPurapDocumentIdentifier() != null) {
            pd.setPurchaseOrderNbr(prd.getPurchaseOrderIdentifier().toString());
        }
        LOG.debug("populatePaymentDetail() po id:  " + prd.getPurchaseOrderIdentifier());
        if (prd.getPurchaseOrderDocument().getRequisitionIdentifier() != null) {
            pd.setRequisitionNbr(prd.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
        }
        if (prd.getDocumentHeader().getOrganizationDocumentNumber() != null) {
            pd.setOrganizationDocNbr(prd.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setFinancialDocumentTypeCode("PREQ");
        pd.setInvoiceDate(new Timestamp(prd.getInvoiceDate().getTime()));
        pd.setOrigInvoiceAmount(prd.getVendorInvoiceAmount().bigDecimalValue());

        pd.setNetPaymentAmount(prd.getDocumentHeader().getFinancialDocumentTotalAmount().bigDecimalValue());

        BigDecimal shippingAmount = new BigDecimal("0");
        BigDecimal discountAmount = new BigDecimal("0");
        BigDecimal creditAmount = new BigDecimal("0");
        BigDecimal debitAmount = new BigDecimal("0");
        for (Iterator iter = prd.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) iter.next();
            BigDecimal itemAmount = new BigDecimal("0");
            if (item.getExtendedPrice() != null) {
                itemAmount = item.getExtendedPrice().bigDecimalValue();
            }
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
                discountAmount = discountAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode())) {
                debitAmount = debitAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode())) {
                if (itemAmount.compareTo(new BigDecimal("0")) < 0) {
                    creditAmount = creditAmount.add(itemAmount);
                }
                else {
                    debitAmount = debitAmount.add(itemAmount);
                }
            }
        }

        pd.setInvTotDiscountAmount(discountAmount);
        pd.setInvTotShipAmount(shippingAmount);
        pd.setInvTotOtherCreditAmount(creditAmount);
        pd.setInvTotOtherDebitAmount(debitAmount);

        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        
        addAccounts(prd, pd,"PREQ");
        addNotes(prd, pd);
        return pd;
    }

    /**
     * Add accounts to a PDP Payment Detail
     * 
     * @param prd
     * @param pd
     */
    private void addAccounts(AccountsPayableDocumentBase prd, PaymentDetail pd,String documentType) {
        // Calculate the total amount for each account across all items
        Map accounts = new HashMap();
        for (Iterator iter = prd.getItems().iterator(); iter.hasNext();) {
            AccountsPayableItemBase item = (AccountsPayableItemBase) iter.next();
            for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                PurApAccountingLine account = (PurApAccountingLine) iterator.next();
                AccountingInfo ai = new AccountingInfo(account.getChartOfAccountsCode(), account.getAccountNumber(), account.getSubAccountNumber(), account.getFinancialObjectCode(), account.getFinancialSubObjectCode(), account.getOrganizationReferenceId(), account.getProjectCode());

                BigDecimal amt = account.getAmount().bigDecimalValue();
                if ( "CM".equals(documentType) ) {
                    amt = amt.negate();
                }

                if (accounts.containsKey(ai)) {
                    BigDecimal total = amt.add( (BigDecimal)accounts.get(ai) );
                    accounts.put(ai, total);
                }
                else {
                    accounts.put(ai, amt);
                }
            }
        }

        for (Iterator iter = accounts.keySet().iterator(); iter.hasNext();) {
            AccountingInfo ai = (AccountingInfo) iter.next();
            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setAccountNbr(ai.account);
            pad.setAccountNetAmount((BigDecimal) accounts.get(ai));
            pad.setFinChartCode(ai.chart);
            pad.setFinObjectCode(ai.objectCode);
            pad.setFinSubObjectCode(ai.subObjectCode);
            pad.setOrgReferenceId(ai.orgReferenceId);
            pad.setProjectCode(ai.projectCode);
            pad.setSubAccountNbr(ai.subAccount);
            pd.addAccountDetail(pad);
        }
    }

    /**
     * Add Notes to a PDP Payment Detail
     * 
     * @param doc
     * @param pd
     */
    private void addNotes(AccountsPayableDocumentBase doc, PaymentDetail pd) {

        int count = 1;

        if (doc instanceof PaymentRequestDocument) {
            PaymentRequestDocument prd = (PaymentRequestDocument) doc;

            if (prd.getSpecialHandlingInstructionLine1Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine1Text());
                pd.addNote(pnt);
            }
            if (prd.getSpecialHandlingInstructionLine2Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine2Text());
                pd.addNote(pnt);
            }
            if (prd.getSpecialHandlingInstructionLine3Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine3Text());
                pd.addNote(pnt);
            }
        }
        if (doc.getNoteLine1Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(doc.getNoteLine1Text());
            pd.addNote(pnt);
        }
        if (doc.getNoteLine2Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(doc.getNoteLine2Text());
            pd.addNote(pnt);
        }
        if (doc.getNoteLine3Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(doc.getNoteLine3Text());
            pd.addNote(pnt);
        }
    }

    /**
     * Populate the PDP Payment Group from fields on a payment request
     * 
     * @param prd
     * @param batch
     * @return
     */
    private PaymentGroup populatePaymentGroup(PaymentRequestDocument prd, Batch batch) {
        LOG.debug("populatePaymentGroup() payment request documentNumber: " + prd.getDocumentNumber());
        PaymentGroup pg = new PaymentGroup();
        pg.setBatch(batch);
        pg.setPaymentStatus(openPaymentStatus);

        String postalCode = prd.getVendorPostalCode();
        if ("US".equals(prd.getVendorCountry())) {
            // Add a dash in the zip code if necessary
            if (postalCode.length() > 5) {
                postalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
            }
        }

        pg.setPayeeName(prd.getVendorName());
        pg.setPayeeId(prd.getVendorHeaderGeneratedIdentifier() + "-" + prd.getVendorDetailAssignedIdentifier());
        pg.setPayeeIdTypeCd(PdpConstants.PayeeTypeCodes.VENDOR);

        if (prd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode() != null) {
            pg.setPayeeOwnerCd(prd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode());
        }

        if (prd.getVendorCustomerNumber() != null) {
            pg.setCustomerInstitutionNumber(prd.getVendorCustomerNumber());
        }
        pg.setLine1Address(prd.getVendorLine1Address());
        pg.setLine2Address(prd.getVendorLine2Address());
        pg.setLine3Address("");
        pg.setLine4Address("");
        pg.setCity(prd.getVendorCityName());
        pg.setState(prd.getVendorStateCode());
        pg.setZipCd(postalCode);
        pg.setCountry(prd.getVendorCountryCode());
        pg.setCampusAddress(Boolean.FALSE);
        if (prd.getPaymentRequestPayDate() != null) {
            pg.setPaymentDate(new Timestamp(prd.getPaymentRequestPayDate().getTime()));
        }
        pg.setPymtAttachment(prd.getPaymentAttachmentIndicator());
        pg.setProcessImmediate(prd.getImmediatePaymentIndicator());
        pg.setPymtSpecialHandling((prd.getSpecialHandlingInstructionLine1Text() != null) || (prd.getSpecialHandlingInstructionLine2Text() != null) || (prd.getSpecialHandlingInstructionLine3Text() != null));
        pg.setTaxablePayment(Boolean.FALSE);
        pg.setNraPayment(VendorConstants.OwnerTypes.NR.equals(prd.getVendorDetail().getVendorHeader().getVendorOwnershipCode()));
        pg.setCombineGroups(Boolean.TRUE);
        return pg;
    }

    /**
     * 
     * This method...
     * @param cmd
     * @param batch
     * @return
     */
    private PaymentGroup populatePaymentGroup(CreditMemoDocument cmd, Batch batch) {
        LOG.debug("populatePaymentGroup() credit memo documentNumber: " + cmd.getDocumentNumber());
        PaymentGroup pg = new PaymentGroup();
        pg.setBatch(batch);
        pg.setPaymentStatus(openPaymentStatus);

        String postalCode = cmd.getVendorPostalCode();
        if ("US".equals(cmd.getVendorCountry())) {
            // Add a dash in the zip code if necessary
            if (postalCode.length() > 5) {
                postalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
            }
        }

        pg.setPayeeName(cmd.getVendorName());
        pg.setPayeeId(cmd.getVendorHeaderGeneratedIdentifier() + "-" + cmd.getVendorDetailAssignedIdentifier());
        pg.setPayeeIdTypeCd(PdpConstants.PayeeTypeCodes.VENDOR);

        if (cmd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode() != null) {
            pg.setPayeeOwnerCd(cmd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode());
        }

        if (cmd.getVendorCustomerNumber() != null) {
            pg.setCustomerInstitutionNumber(cmd.getVendorCustomerNumber());
        }
        pg.setLine1Address(cmd.getVendorLine1Address());
        pg.setLine2Address(cmd.getVendorLine2Address());
        pg.setLine3Address("");
        pg.setLine4Address("");
        pg.setCity(cmd.getVendorCityName());
        pg.setState(cmd.getVendorStateCode());
        pg.setZipCd(postalCode);
        pg.setCountry(cmd.getVendorCountryCode());
        pg.setCampusAddress(Boolean.FALSE);
        if (cmd.getCreditMemoDate() != null) {
            pg.setPaymentDate(new Timestamp(cmd.getCreditMemoDate().getTime()));
        }
        pg.setPymtAttachment(Boolean.FALSE);
        pg.setProcessImmediate(Boolean.FALSE);
        pg.setPymtSpecialHandling(Boolean.FALSE);
        pg.setTaxablePayment(Boolean.FALSE);
        pg.setNraPayment(VendorConstants.OwnerTypes.NR.equals(cmd.getVendorDetail().getVendorHeader().getVendorOwnershipCode()));
        pg.setCombineGroups(Boolean.TRUE);
        return pg;
    }

    /**
     * Create a new PDP batch
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @return
     */
    private Batch createBatch(String campusCode, PdpUser puser, Date processRunDate) {
        String orgCode = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnitCode = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(campusCode, orgCode, subUnitCode);
        if (customer == null) {
            throw new IllegalArgumentException("Unable to find customer profile for " + campusCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName("extr_fr_purap");
        batch.setSubmiterUser(puser);

        // Set these for now, we will update them later
        batch.setPaymentCount(0);
        batch.setPaymentTotalAmount(new BigDecimal("0"));
        paymentFileService.saveBatch(batch);

        return batch;
    }

    /**
     * Find all the campuses that have payments to process
     * 
     * @return
     */
    private List<String> getChartCodes(boolean immediatesOnly, Date processRunDate) {
        List<String> output = new ArrayList<String>();

        Iterator<PaymentRequestDocument> iter = null;
        if (immediatesOnly) {
            iter = paymentRequestService.getImmediatePaymentRequestsToExtract(null);
        }
        else {
            java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());
            iter = paymentRequestService.getPaymentRequestsToExtract(onOrBeforePaymentRequestPayDate);
        }
        while (iter.hasNext()) {
            PaymentRequestDocument prd = iter.next();
            if (!output.contains(prd.getProcessingCampusCode())) {
                output.add(prd.getProcessingCampusCode());
            }
        }
        return output;
    }

    
    /**
     * 
     * This method...
     * @param idList
     * @param lineTotal
     * @param maxNoteLines
     */
    private void sendExceedsMaxNotesWarningEmail(List<String> cmIdList, List<String> preqIdList, int lineTotal, int maxNoteLines) {
        LOG.debug("sendExceedsMaxNotesWarningEmail() starting");

        // To send email or not send email
        boolean noEmail = parameterService.getIndicatorParameter(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        if (noEmail) {
            LOG.debug("sendExceedsMaxNotesWarningEmail() sending payment file email is disabled");
            return;
        }

        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("Error: Maximum rows exceeded on payment bundle");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-Error: Maximum rows exceeded on payment bundle");
        }

        StringBuffer body = new StringBuffer();

        // Get recipient email address
        String toAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL);
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.SOFT_EDIT_CC);
        String ccAddressList[] = ccAddresses.split(",");

        for (int i = 0; i < ccAddressList.length; i++) {
            if (ccAddressList[i] != null) {
                message.addCcAddress(ccAddressList[i].trim());
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        body.append("The positive pay bundle associated with the following credit memo(s) and payment request(s) ");
        body.append("exceeds the row limit. The credit memo(s) may need to be processed as several separate credit ");
        body.append("memos to reduce the number of rows associated with the positive pay bundle. \n\n");
        body.append("The Credit Memo Document Id(s) are ");
        
        int cmCount = cmIdList.size();
        for(String id : cmIdList) {
            body.append(id);
            if(--cmCount > 0) {
                body.append(", ");
            } 
        }
        
        body.append(".  The PREQ Document Id(s) are ");
        
        int prCount = preqIdList.size();
        for(String id : preqIdList) {
            body.append(id);
            if(--prCount > 0) {
                body.append(", ");
            } 
        }
        body.append(".  There were ").append(lineTotal).append(" lines all together, which is more than the ");
        body.append(maxNoteLines).append(" allowed.");
        
        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendExceedsMaxNotesWarningEmail() Invalid email address. Message not sent", e);
        }
    }

    
    /**
     * 
     * This class...
     */
    class Payment {
        public static final String CM = "cm";
        public static final String PREQ = "preq";

        public Integer id;
        public String typeCode;
        public String campusCode;
        public String vendorId;
        public String countryCode;
        public String zipCode;
        public String customerNbr;
        public Object data;

        public Payment(Integer id, String typeCode, String campusCode, String vendorId, String countryCode, String zipCode, String customerNbr, Object data) {
            this.id = id;
            this.typeCode = typeCode;
            this.campusCode = campusCode;
            this.vendorId = vendorId;
            this.countryCode = countryCode;
            if ((zipCode != null) && (zipCode.length() > 5)) {
                this.zipCode = zipCode.substring(0, 5);
            }
            else {
                this.zipCode = zipCode;
            }
            this.customerNbr = customerNbr;
            this.data = data;
        }

        public boolean combine(Payment p) {
            return combineKey().equals(p.combineKey());
        }

        public String combineKey() {
            return campusCode + "~" + vendorId + "~" + customerNbr + "~" + countryCode + "~" + zipCode;
        }

        private String key() {
            return campusCode + "~" + vendorId + "~" + customerNbr + "~" + countryCode + "~" + zipCode + "~" + typeCode + "~" + id;
        }

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(key()).toHashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AccountingInfo)) {
                return false;
            }
            AccountingInfo thisobj = (AccountingInfo) obj;
            return new EqualsBuilder().append(key(), thisobj.key()).isEquals();
        }
    }

    class Totals {
        public Integer count = 0;
        public BigDecimal totalAmount = new BigDecimal("0");
    }

    class AccountingInfo {
        private String chart;
        private String account;
        private String subAccount;
        private String objectCode;
        private String subObjectCode;
        private String orgReferenceId;
        private String projectCode;

        public AccountingInfo(String c, String a, String s, String o, String so, String or, String pc) {
            setChart(c);
            setAccount(a);
            setSubAccount(s);
            setObjectCode(o);
            setSubObjectCode(so);
            setOrgReferenceId(or);
            setProjectCode(pc);
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public void setChart(String chart) {
            this.chart = chart;
        }

        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }

        public void setOrgReferenceId(String orgReferenceId) {
            this.orgReferenceId = orgReferenceId;
        }

        public void setProjectCode(String projectCode) {
            if (projectCode == null) {
                this.projectCode = KFSConstants.getDashProjectCode();
            }
            else {
                this.projectCode = projectCode;
            }
        }

        public void setSubAccount(String subAccount) {
            if (subAccount == null) {
                this.subAccount = KFSConstants.getDashSubAccountNumber();
            }
            else {
                this.subAccount = subAccount;
            }
        }

        public void setSubObjectCode(String subObjectCode) {
            if (subObjectCode == null) {
                this.subObjectCode = KFSConstants.getDashFinancialSubObjectCode();
            }
            else {
                this.subObjectCode = subObjectCode;
            }
        }

        private String key() {
            return chart + "~" + account + "~" + subAccount + "~" + objectCode + "~" + subObjectCode + "~" + orgReferenceId + "~" + projectCode;
        }

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(key()).toHashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AccountingInfo)) {
                return false;
            }
            AccountingInfo thisobj = (AccountingInfo) obj;
            return new EqualsBuilder().append(key(), thisobj.key()).isEquals();
        }
    }

    // Dependencies

    public void setPaymentFileService(PaymentFileService pfs) {
        paymentFileService = pfs;
    }

    public void setPaymentRequestService(PaymentRequestService prs) {
        paymentRequestService = prs;
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setCreditMemoService(CreditMemoService cms) {
        this.creditMemoService = cms;
    }

    public void setBusinessObjectService(BusinessObjectService bos) {
        this.businessObjectService = bos;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setReferenceService(ReferenceService rs) {
        referenceService = rs;
    }

    public void setDocumentService(DocumentService ds) {
        documentService = ds;
    }
    
    public void setMailService(MailService ms) {
        mailService = ms;
}
    public void setEnvironmentService(EnvironmentService es) {
        environmentService = es;
    }

    public void setPurapRunDateService(PurapRunDateService purapRunDateService) {
        this.purapRunDateService = purapRunDateService;
    }

}
