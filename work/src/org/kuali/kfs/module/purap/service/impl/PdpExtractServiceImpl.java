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
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
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
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PdpExtractService;
import org.kuali.module.vendor.VendorConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PdpExtractServiceImpl implements PdpExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpExtractServiceImpl.class);

    private PaymentRequestService paymentRequestService;
    private PaymentFileService paymentFileService;
    private KualiConfigurationService kualiConfigurationService;
    private CustomerProfileService customerProfileService;
    private DateTimeService dateTimeService;
    private UniversalUserService universalUserService;
    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;

    public void extractPayments() {
        LOG.debug("extractPayments() started");

        Date processRunDate = dateTimeService.getCurrentDate();

        String userId = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.PURAP_PDP_USER_ID);
        UniversalUser uuser;
        try {
            uuser = universalUserService.getUniversalUserByAuthenticationUserId(userId);
        }
        catch (UserNotFoundException e) {
            LOG.error("extractPaymentsForChart() Unable to find user " + userId);
            throw new IllegalArgumentException("Unable to find user " + userId);
        }
        PdpUser puser = new PdpUser(uuser);

        List<String> campusesToProcess = getChartCodes();
        for (Iterator iter = campusesToProcess.iterator(); iter.hasNext();) {
            String campus = (String)iter.next();
            extractPaymentsForChart(campus,puser,processRunDate);
        }
    }

    private void extractPaymentsForChart(String chartCode,PdpUser puser,Date processRunDate) {
        LOG.debug("extractPaymentsForChart() started for chart: " + chartCode);

        Batch batch = createBatch(chartCode,puser,processRunDate);
        Integer count = 0;
        BigDecimal totalAmount = new BigDecimal("0");

        // Do all the special ones
        Iterator<PaymentRequestDocument> prIter = paymentRequestService.getPaymentRequestsToExtractSpecialPayments(chartCode);
        while ( prIter.hasNext() ) {
            LOG.debug("extractPaymentsForChart() here we are");
            PaymentRequestDocument prd = prIter.next();
            PaymentGroup pg = populatePaymentGroup(prd,batch);
            PaymentDetail pd = populatePaymentDetail(prd,batch);
            pg.addPaymentDetails(pd);
            paymentGroupService.save(pg);

            count++;
            totalAmount = totalAmount.add(pg.getNetPaymentAmount());

            updatePaymentRequest(prd,puser,processRunDate);
        }

        // TODO Do all the regular ones (including credit memos)

        batch.setPaymentCount(count);
        batch.setPaymentTotalAmount(totalAmount);
        paymentFileService.saveBatch(batch);
    }

    private void updatePaymentRequest(PaymentRequestDocument prd,PdpUser puser,Date processRunDate) {
        prd.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
        paymentRequestService.save(prd);
    }

    private PaymentDetail populatePaymentDetail(PaymentRequestDocument prd,Batch batch) {
        PaymentDetail pd = new PaymentDetail();

        pd.setInvoiceNbr(prd.getInvoiceNumber().substring(0,25));
        if ( prd.getPurapDocumentIdentifier() != null ) {
            pd.setPurchaseOrderNbr(prd.getPurapDocumentIdentifier().toString());
        }
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
            if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode()) ) {
                discountAmount = discountAmount.add(item.getExtendedPrice().bigDecimalValue());
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode()) ) {
                shippingAmount = shippingAmount.add(item.getExtendedPrice().bigDecimalValue());
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode()) ) {
                debitAmount = debitAmount.add(item.getExtendedPrice().bigDecimalValue());
            } else if ( PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode()) ) {
                if ( item.getExtendedPrice().isNegative() ) {
                    creditAmount = creditAmount.add(item.getExtendedPrice().bigDecimalValue());
                } else {
                    debitAmount = debitAmount.add(item.getExtendedPrice().bigDecimalValue());
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

    private void addAccounts(PaymentRequestDocument prd,PaymentDetail pd) {
        // Calculate the total amount for each account across all items
        Map accounts = new HashMap();
        for (Iterator iter = prd.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem)iter.next();
            for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                PurApAccountingLine account = (PurApAccountingLine)iterator.next();
                AccountingInfo ai = new AccountingInfo(account.getChartOfAccountsCode(),account.getAccountNumber(),account.getSubAccountNumber(),account.getFinancialObjectCode(),account.getFinancialSubObjectCode(),account.getOrganizationReferenceId(),account.getProjectCode());
                if ( accounts.containsKey(ai) ) {
                    BigDecimal total = account.getAmount().bigDecimalValue().add( (BigDecimal)accounts.get(ai) );
                    accounts.put(account, total);
                } else {
                    accounts.put(account, account.getAmount().bigDecimalValue());
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

    private void addNotes(PaymentRequestDocument prd,PaymentDetail pd) {
        int count = 1;
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
        if ( prd.getNoteLine1Text() != null ) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(prd.getNoteLine1Text());
            pd.addNote(pnt);
        }
        if ( prd.getNoteLine2Text() != null ) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(prd.getNoteLine2Text());
            pd.addNote(pnt);
        }
        if ( prd.getNoteLine3Text() != null ) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(count++);
            pnt.setCustomerNoteText(prd.getNoteLine3Text());
            pd.addNote(pnt);
        }
    }

    private PaymentGroup populatePaymentGroup(PaymentRequestDocument prd,Batch batch)  {
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
        pg.setPaymentDate(new Timestamp(prd.getPaymentPaidDate().getTime()));
        pg.setPymtAttachment(prd.getPaymentAttachmentIndicator());
        pg.setProcessImmediate(prd.getImmediatePaymentIndicator());
        pg.setPymtSpecialHandling( (prd.getSpecialHandlingInstructionLine1Text() != null) || (prd.getSpecialHandlingInstructionLine2Text() != null) || (prd.getSpecialHandlingInstructionLine3Text() != null) );
        pg.setTaxablePayment(Boolean.FALSE);
        pg.setNraPayment( VendorConstants.OwnerTypes.NR.equals(prd.getVendorDetail().getVendorHeader().getVendorOwnershipCode()) );
        pg.setCombineGroups(Boolean.TRUE);
        return pg;
    }

    private Batch createBatch(String chartCode,PdpUser puser,Date processRunDate) {
        String orgCode = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnitCode = kualiConfigurationService.getApplicationParameterValue(PurapParameterConstants.PURAP_ADMIN_GROUP, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(chartCode, orgCode, subUnitCode);
        if ( customer == null ) {
            throw new IllegalArgumentException("Unable to find customer profile for " + chartCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName("from_epic");
        batch.setSubmiterUser(puser);

        // Set these for now, we will update them later
        batch.setPaymentCount(1);
        batch.setPaymentTotalAmount(new BigDecimal("0"));
        paymentFileService.saveBatch(batch);

        return batch;
    }

    private List<String> getChartCodes() {
        List<String> output = new ArrayList<String>();

        Iterator<PaymentRequestDocument> iter = paymentRequestService.getPaymentRequestsToExtract();
        while ( iter.hasNext() ) {
            PaymentRequestDocument prd = iter.next();
            if ( ! output.contains(prd.getProcessingCampusCode()) ) {
                output.add(prd.getProcessingCampusCode());
            }
        }
        return output;
    }

    class AccountingInfo {
        public String chart;
        public String account;
        public String subAccount;
        public String objectCode;
        public String subObjectCode;
        public String orgReferenceId;
        public String projectCode;
        public AccountingInfo(String c,String a,String s,String o,String so,String or,String pc) {
            chart = c;
            account = a;
            subAccount = s;
            objectCode = o;
            subObjectCode = so;
            orgReferenceId = or;
            projectCode = pc;
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
}
