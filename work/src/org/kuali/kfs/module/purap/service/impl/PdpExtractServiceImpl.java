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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountDetail;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.kuali.module.pdp.service.PaymentFileService;
import org.kuali.module.pdp.service.PaymentGroupService;
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
import org.kuali.module.vendor.VendorConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PdpExtractServiceImpl implements PdpExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpExtractServiceImpl.class);

    private PaymentRequestService paymentRequestService;
    private BusinessObjectService businessObjectService;
    private PaymentFileService paymentFileService;
    private KualiConfigurationService kualiConfigurationService;
    private CustomerProfileService customerProfileService;
    private DateTimeService dateTimeService;
    private UniversalUserService universalUserService;
    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private CreditMemoService creditMemoService;

    // This should only be set to true when testing this system.  Setting this to true will run the code but
    // won't set the extracted date on the credit memos or payment requests
    boolean testMode = false;

    /**
     * @see org.kuali.module.purap.service.PdpExtractService#extractImmediatePaymentsOnly()
     */
    public void extractImmediatePaymentsOnly() {
        LOG.debug("extractImmediatePaymentsOnly() started");

        extractPayments(true);
    }

    /**
     * @see org.kuali.module.purap.service.PdpExtractService#extractPayments()
     */
    public void extractPayments() {
        LOG.debug("extractPayments() started");

        extractPayments(false);
    }

    private void extractPayments(boolean immediateOnly) {
        LOG.debug("extractPayments() started");

        Date processRunDate = dateTimeService.getCurrentDate();

        String userId = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.BATCH, PurapParameterConstants.PURAP_PDP_USER_ID);
        UniversalUser uuser;
        try {
            uuser = universalUserService.getUniversalUserByAuthenticationUserId(userId);
        }
        catch (UserNotFoundException e) {
            LOG.error("extractPayments() Unable to find user " + userId);
            throw new IllegalArgumentException("Unable to find user " + userId);
        }
        PdpUser puser = new PdpUser(uuser);

        List<String> campusesToProcess = getChartCodes(immediateOnly);
        for (Iterator iter = campusesToProcess.iterator(); iter.hasNext();) {
            String campus = (String)iter.next();

            extractPaymentsForCampus(campus,puser,processRunDate,immediateOnly);
        }
    }

    /**
     * Handle a single campus
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     */
    private void extractPaymentsForCampus(String campusCode,PdpUser puser,Date processRunDate,boolean immediateOnly) {
        LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode,puser,processRunDate);
        Integer count = 0;
        BigDecimal totalAmount = new BigDecimal("0");

        // Do all the special ones
        Totals t = extractSpecialPaymentsForChart(campusCode, puser, processRunDate, batch,immediateOnly);
        count = count + t.count;
        totalAmount = totalAmount.add(t.totalAmount);

        if ( ! immediateOnly ) {
            // Do all the regular ones (including credit memos)
            t = extractRegularPaymentsForChart(campusCode, puser, processRunDate, batch);
            count = count + t.count;
            totalAmount = totalAmount.add(t.totalAmount);
        }

        batch.setPaymentCount(count);
        batch.setPaymentTotalAmount(totalAmount);
        paymentFileService.saveBatch(batch);
    }

    /**
     * Get all the payments that could be combined wih credit memos
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @param batch
     * @return
     */
    private Totals extractRegularPaymentsForChart(String campusCode,PdpUser puser,Date processRunDate,Batch batch) {
        Totals t = new Totals();

        // Get all the matching credit memos
        Iterator<CreditMemoDocument> cmIter = creditMemoService.getCreditMemosToExtract(campusCode);
        while ( cmIter.hasNext() ) {
            CreditMemoDocument cmd = cmIter.next();

            List<PaymentRequestDocument> prds = new ArrayList<PaymentRequestDocument>();
            List<CreditMemoDocument> cmds = new ArrayList<CreditMemoDocument>();
            cmds.add(cmd);

            KualiDecimal creditMemoAmount = cmd.getCreditMemoAmount();
            KualiDecimal paymentRequestAmount = KualiDecimal.ZERO;

            Iterator<PaymentRequestDocument> pri = paymentRequestService.getPaymentRequestsToExtractByCM(campusCode,cmd);
            while ( pri.hasNext() ) {
                PaymentRequestDocument prd = pri.next();
                paymentRequestAmount = paymentRequestAmount.add(prd.getGrandTotal());
                prds.add(prd);
            }

            if ( paymentRequestAmount.compareTo(creditMemoAmount) > 0 ) {
                PaymentGroup pg = buildPaymentGroup(prds,cmds,batch);
                paymentGroupService.save(pg);
                t.count++;
                t.totalAmount = t.totalAmount.add(pg.getNetPaymentAmount());

                for (Iterator iter = cmds.iterator(); iter.hasNext();) {
                    CreditMemoDocument element = (CreditMemoDocument)iter.next();
                    updateCreditMemo(element,puser,processRunDate);                    
                }
                for (Iterator iter = prds.iterator(); iter.hasNext();) {
                    PaymentRequestDocument element = (PaymentRequestDocument)iter.next();
                    updatePaymentRequest(element,puser,processRunDate);                    
                }
            }
        }

        // Get all the payment requests to process
        Iterator<PaymentRequestDocument> prIter = paymentRequestService.getPaymentRequestToExtractByChart(campusCode);
        while ( prIter.hasNext() ) {
            PaymentRequestDocument prd = prIter.next();

            PaymentGroup pg = processSinglePaymentRequestDocument(prd,batch,puser,processRunDate);

            t.count = t.count + pg.getPaymentDetails().size();
            t.totalAmount = t.totalAmount.add(pg.getNetPaymentAmount());
        }

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
    private PaymentGroup processSinglePaymentRequestDocument(PaymentRequestDocument prd,Batch batch,PdpUser puser,Date processRunDate) {
        List<PaymentRequestDocument> prds = new ArrayList<PaymentRequestDocument>();
        List<CreditMemoDocument> cmds = new ArrayList<CreditMemoDocument>();
        prds.add(prd);

        PaymentGroup pg = buildPaymentGroup(prds,cmds,batch);
        paymentGroupService.save(pg);
        updatePaymentRequest(prd,puser,processRunDate);

        return pg;
    }

    private List<Payment> getByCombineKey(String key,List itemsToProcess,String typeCode) {
        List<Payment> p = new ArrayList<Payment>();
        for (Iterator iter = itemsToProcess.iterator(); iter.hasNext();) {
            Payment element = (Payment) iter.next();
            if ( key.equals(element.combineKey()) ) {
                if ( (typeCode == null) || (typeCode.equals(element.typeCode)) ) {
                    p.add(element);
                }
            }
        }
        return p;        
    }

    private List<Payment> getByCombineKey(String key,List itemsToProcess) {
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
    private Totals extractSpecialPaymentsForChart(String campusCode,PdpUser puser,Date processRunDate,Batch batch,boolean immediatesOnly) {
        Totals t = new Totals();

        Iterator<PaymentRequestDocument> prIter = null;
        if ( immediatesOnly ) {
            prIter = paymentRequestService.getImmediatePaymentRequestsToExtract(campusCode);
        } else {
            prIter = paymentRequestService.getPaymentRequestsToExtractSpecialPayments(campusCode);
        }

        while ( prIter.hasNext() ) {
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
    private void updateCreditMemo(CreditMemoDocument cmd,PdpUser puser,Date processRunDate) {
        if ( ! testMode ) {
            cmd.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
            businessObjectService.save(cmd);
        }
    }

    /**
     * Mark a payment request as extracted
     * 
     * @param prd
     * @param puser
     * @param processRunDate
     */
    private void updatePaymentRequest(PaymentRequestDocument prd,PdpUser puser,Date processRunDate) {
        if ( ! testMode ) {
            prd.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
            businessObjectService.save(prd);
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
    private PaymentGroup buildPaymentGroup(List<PaymentRequestDocument> prds,List<CreditMemoDocument> cmds,Batch batch) {

        // There should always be at least one Payment Request Document in the list.
        PaymentRequestDocument firstPrd = prds.get(0);
        PaymentGroup pg = populatePaymentGroup(firstPrd, batch);

        for (Iterator iter = prds.iterator(); iter.hasNext();) {
            PaymentRequestDocument p = (PaymentRequestDocument)iter.next();
            PaymentDetail pd = populatePaymentDetail(p, batch);
            pg.addPaymentDetails(pd);
        }
        for (Iterator iter = cmds.iterator(); iter.hasNext();) {
            CreditMemoDocument c = (CreditMemoDocument) iter.next();
            PaymentDetail pd = populatePaymentDetail(c, batch);
            pg.addPaymentDetails(pd);
        }

        return pg;
    }

    /**
     * Create a PDP payment detail from a Credit Memo
     * 
     * @param cmd
     * @param batch
     * @return
     */
    private PaymentDetail populatePaymentDetail(CreditMemoDocument cmd,Batch batch) {
        PaymentDetail pd = new PaymentDetail();

        String invNbr = cmd.getCreditMemoNumber();
        if ( invNbr.length() > 25 ) {
            invNbr = invNbr.substring(0,25);
        }
        pd.setInvoiceNbr(invNbr);
        if ( cmd.getPurapDocumentIdentifier() != null ) {
            pd.setPurchaseOrderNbr(cmd.getPurapDocumentIdentifier().toString());
        }
        if ( cmd.getPurchaseOrderDocument() != null ) {
            if ( cmd.getPurchaseOrderDocument().getRequisitionIdentifier() != null ) {
                pd.setRequisitionNbr(cmd.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
            }
            if ( cmd.getPurchaseOrderDocument().getDocumentHeader().getOrganizationDocumentNumber() != null ) {
                pd.setOrganizationDocNbr(cmd.getPurchaseOrderDocument().getDocumentHeader().getOrganizationDocumentNumber());
            }
        }
        pd.setFinancialDocumentTypeCode("CM");
        pd.setInvoiceDate(new Timestamp(cmd.getCreditMemoDate().getTime()));
        pd.setOrigInvoiceAmount(cmd.getCreditMemoAmount().bigDecimalValue().negate());

        pd.setNetPaymentAmount(cmd.getDocumentHeader().getFinancialDocumentTotalAmount().bigDecimalValue());

        BigDecimal shippingAmount = new BigDecimal("0");
        BigDecimal discountAmount = new BigDecimal("0");
        BigDecimal creditAmount = new BigDecimal("0");
        BigDecimal debitAmount = new BigDecimal("0");
        for (Iterator iter = cmd.getItems().iterator(); iter.hasNext();) {
            CreditMemoItem item = (CreditMemoItem)iter.next();
            BigDecimal itemAmount = new BigDecimal("0");
            if ( item.getExtendedPrice() != null ) {
                itemAmount = item.getExtendedPrice().bigDecimalValue();
            }
            if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode()) ) {
                discountAmount = discountAmount.subtract(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode()) ) {
                shippingAmount = shippingAmount.subtract(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode()) ) {
                debitAmount = debitAmount.subtract(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode()) ) {
                if ( itemAmount.compareTo(new BigDecimal("0")) < 0 ) {
                    creditAmount = creditAmount.subtract(itemAmount);
                } else {
                    debitAmount = debitAmount.subtract(itemAmount);
                }
            }
        }

        pd.setInvTotDiscountAmount(discountAmount);
        pd.setInvTotShipAmount(shippingAmount);
        pd.setInvTotOtherCreditAmount(creditAmount);
        pd.setInvTotOtherDebitAmount(debitAmount);

        addAccounts(cmd,pd);
        addNotes(cmd,pd);
        return pd;
    }

    /**
     * Create a PDP Payment Detail from a Payment Request
     * 
     * @param prd
     * @param batch
     * @return
     */
    private PaymentDetail populatePaymentDetail(PaymentRequestDocument prd,Batch batch) {
        PaymentDetail pd = new PaymentDetail();

        String invNbr = prd.getInvoiceNumber();
        if ( invNbr.length() > 25 ) {
            invNbr = invNbr.substring(0,25);
        }
        pd.setInvoiceNbr(invNbr);
        if ( prd.getPurapDocumentIdentifier() != null ) {
            pd.setPurchaseOrderNbr(prd.getPurapDocumentIdentifier().toString());
        }
        LOG.debug("populatePaymentDetail() po id:  " + prd.getPurchaseOrderIdentifier());
        if ( prd.getPurchaseOrderDocument().getRequisitionIdentifier() != null ) {
            pd.setRequisitionNbr(prd.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
        }
        if ( prd.getPurchaseOrderDocument().getDocumentHeader().getOrganizationDocumentNumber() != null ) {
            pd.setOrganizationDocNbr(prd.getPurchaseOrderDocument().getDocumentHeader().getOrganizationDocumentNumber());
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
            PaymentRequestItem item = (PaymentRequestItem)iter.next();
            BigDecimal itemAmount = new BigDecimal("0");
            if ( item.getExtendedPrice() != null ) {
                itemAmount = item.getExtendedPrice().bigDecimalValue();
            }
            if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode()) ) {
                discountAmount = discountAmount.add(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode()) ) {
                shippingAmount = shippingAmount.add(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode()) ) {
                debitAmount = debitAmount.add(itemAmount);
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode()) ) {
                if ( itemAmount.compareTo(new BigDecimal("0")) < 0 ) {
                    creditAmount = creditAmount.add(itemAmount);
                } else {
                    debitAmount = debitAmount.add(itemAmount);
                }
            }
        }

        pd.setInvTotDiscountAmount(discountAmount);
        pd.setInvTotShipAmount(shippingAmount);
        pd.setInvTotOtherCreditAmount(creditAmount);
        pd.setInvTotOtherDebitAmount(debitAmount);

        addAccounts(prd,pd);
        addNotes(prd,pd);
        return pd;
    }

    /**
     * Add accounts to a PDP Payment Detail
     * 
     * @param prd
     * @param pd
     */
    private void addAccounts(AccountsPayableDocumentBase prd,PaymentDetail pd) {
        // Calculate the total amount for each account across all items
        Map accounts = new HashMap();
        for (Iterator iter = prd.getItems().iterator(); iter.hasNext();) {
            AccountsPayableItemBase item = (AccountsPayableItemBase)iter.next();
            for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                PurApAccountingLine account = (PurApAccountingLine)iterator.next();
                AccountingInfo ai = new AccountingInfo(account.getChartOfAccountsCode(),account.getAccountNumber(),account.getSubAccountNumber(),account.getFinancialObjectCode(),account.getFinancialSubObjectCode(),account.getOrganizationReferenceId(),account.getProjectCode());
                if ( accounts.containsKey(ai) ) {
                    BigDecimal total = account.getAmount().bigDecimalValue().add( (BigDecimal)accounts.get(ai) );
                    accounts.put(ai, total);
                } else {
                    accounts.put(ai, account.getAmount().bigDecimalValue());
                }
            }
        }

        for (Iterator iter = accounts.keySet().iterator(); iter.hasNext();) {
            AccountingInfo ai = (AccountingInfo)iter.next();
            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setAccountNbr(ai.account);
            pad.setAccountNetAmount( (BigDecimal)accounts.get(ai) );
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
    private void addNotes(AccountsPayableDocumentBase doc,PaymentDetail pd) {

        int count = 1;

        if ( doc instanceof PaymentRequestDocument ) {
            PaymentRequestDocument prd = (PaymentRequestDocument)doc;

            if ( prd.getSpecialHandlingInstructionLine1Text() != null ) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine1Text());
                pd.addNote(pnt);
            }
            if ( prd.getSpecialHandlingInstructionLine2Text() != null ) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine2Text());
                pd.addNote(pnt);
            }
            if ( prd.getSpecialHandlingInstructionLine3Text() != null ) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(count++);
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine3Text());
                pd.addNote(pnt);
            }
        }
        if ( doc.getNoteLine1Text() != null ) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(doc.getNoteLine1Text());
            pd.addNote(pnt);
        }
        if ( doc.getNoteLine2Text() != null ) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(doc.getNoteLine2Text());
            pd.addNote(pnt);
        }
        if ( doc.getNoteLine3Text() != null ) {
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
    private PaymentGroup populatePaymentGroup(PaymentRequestDocument prd,Batch batch)  {
        LOG.debug("populatePaymentGroup() documentNumber: " + prd.getDocumentNumber());
        PaymentGroup pg = new PaymentGroup();
        pg.setBatch(batch);

        String postalCode = prd.getVendorPostalCode();
        if ( "US".equals(prd.getVendorCountry()) ) {
            // Add a dash in the zip code if necessary
            if ( postalCode.length() > 5 ) {
                postalCode = postalCode.substring(0,5) + "-" + postalCode.substring(5);
            }
        }

        pg.setPayeeName(prd.getVendorName());
        pg.setPayeeId(prd.getVendorHeaderGeneratedIdentifier() + "-" + prd.getVendorDetailAssignedIdentifier());
        pg.setPayeeIdTypeCd(PdpConstants.PayeeTypeCodes.VENDOR);

        if ( prd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode() != null ) {
            pg.setPayeeOwnerCd(prd.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode());
        }

        if ( prd.getVendorCustomerNumber() != null ) {
            pg.setCustomerIuNbr(prd.getVendorCustomerNumber());
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
        if ( prd.getPaymentRequestPayDate() != null ) {
            pg.setPaymentDate(new Timestamp(prd.getPaymentRequestPayDate().getTime()));
        }
        pg.setPymtAttachment(prd.getPaymentAttachmentIndicator());
        pg.setProcessImmediate(prd.getImmediatePaymentIndicator());
        pg.setPymtSpecialHandling( (prd.getSpecialHandlingInstructionLine1Text() != null) || (prd.getSpecialHandlingInstructionLine2Text() != null) || (prd.getSpecialHandlingInstructionLine3Text() != null) );
        pg.setTaxablePayment(Boolean.FALSE);
        pg.setNraPayment( VendorConstants.OwnerTypes.NR.equals(prd.getVendorDetail().getVendorHeader().getVendorOwnershipCode()) );
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
    private Batch createBatch(String campusCode,PdpUser puser,Date processRunDate) {
        String orgCode = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.BATCH, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnitCode = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.BATCH, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(campusCode, orgCode, subUnitCode);
        if ( customer == null ) {
            throw new IllegalArgumentException("Unable to find customer profile for " + campusCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName("from_epic");
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
    private List<String> getChartCodes(boolean immediatesOnly) {
        List<String> output = new ArrayList<String>();

        Iterator<PaymentRequestDocument> iter = null;
        if ( immediatesOnly ) {
            iter = paymentRequestService.getImmediatePaymentRequestsToExtract(null);
        } else {
            iter = paymentRequestService.getPaymentRequestsToExtract();
            
        }
        while ( iter.hasNext() ) {
            PaymentRequestDocument prd = iter.next();
            if ( ! output.contains(prd.getProcessingCampusCode()) ) {
                output.add(prd.getProcessingCampusCode());
            }
        }
        return output;
    }

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

        public Payment(Integer id,String typeCode,String campusCode,String vendorId,String countryCode,String zipCode,String customerNbr,Object data) {
            this.id = id;
            this.typeCode = typeCode;
            this.campusCode = campusCode;
            this.vendorId = vendorId;
            this.countryCode = countryCode;
            if ( (zipCode != null) && (zipCode.length() > 5) ) {
                this.zipCode = zipCode.substring(0,5);
            } else {
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

        public AccountingInfo(String c,String a,String s,String o,String so,String or,String pc) {
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
            if ( projectCode == null ) {
                this.projectCode = KFSConstants.getDashProjectCode();
            } else {
                this.projectCode = projectCode;
            }
        }

        public void setSubAccount(String subAccount) {
            if ( subAccount == null ) {
                this.subAccount = KFSConstants.getDashSubAccountNumber();
            } else {
                this.subAccount = subAccount;
            }
        }

        public void setSubObjectCode(String subObjectCode) {
            if ( subObjectCode == null ) {
                this.subObjectCode = KFSConstants.getDashFinancialSubObjectCode();
            } else {
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

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
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
}
