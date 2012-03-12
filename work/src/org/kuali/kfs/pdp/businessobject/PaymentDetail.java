/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentDetail extends TimestampedBusinessObjectBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetail.class);
    private static KualiDecimal zero = KualiDecimal.ZERO;

    private KualiInteger id;
    private String invoiceNbr;
    private Date invoiceDate;
    private String purchaseOrderNbr;
    private String custPaymentDocNbr;
    private String financialSystemOriginCode;
    private String financialDocumentTypeCode;
    private String requisitionNbr;
    private String organizationDocNbr;
    private String customerInstitutionNumber;
    private KualiDecimal origInvoiceAmount;
    private KualiDecimal netPaymentAmount;
    private KualiDecimal invTotDiscountAmount;
    private KualiDecimal invTotShipAmount;
    private KualiDecimal invTotOtherDebitAmount;
    private KualiDecimal invTotOtherCreditAmount;
    private Boolean primaryCancelledPayment;

    private List<PaymentAccountDetail> accountDetail = new ArrayList<PaymentAccountDetail>();
    private List<PaymentNoteText> notes = new ArrayList<PaymentNoteText>();

    private KualiInteger paymentGroupId;
    private PaymentGroup paymentGroup;

    public PaymentDetail() {
        super();
    }

    public boolean isDetailAmountProvided() {
        return (origInvoiceAmount != null) || (invTotDiscountAmount != null) || (invTotShipAmount != null) || (invTotOtherDebitAmount != null) || (invTotOtherCreditAmount != null);
    }

    public KualiDecimal getCalculatedPaymentAmount() {
        KualiDecimal orig_invoice_amt = origInvoiceAmount == null ? zero : origInvoiceAmount;
        KualiDecimal invoice_tot_discount_amt = invTotDiscountAmount == null ? zero : invTotDiscountAmount;
        KualiDecimal invoice_tot_ship_amt = invTotShipAmount == null ? zero : invTotShipAmount;
        KualiDecimal invoice_tot_other_debits = invTotOtherDebitAmount == null ? zero : invTotOtherDebitAmount;
        KualiDecimal invoice_tot_other_credits = invTotOtherCreditAmount == null ? zero : invTotOtherCreditAmount;

        KualiDecimal t = orig_invoice_amt.subtract(invoice_tot_discount_amt);
        t = t.add(invoice_tot_ship_amt);
        t = t.add(invoice_tot_other_debits);
        t = t.subtract(invoice_tot_other_credits);

        return t;
    }

    /**
     * Determines if the disbursement date is past the number of days old (configured in system parameter) in which actions can take
     * place
     * 
     * @return true if actions are allowed on disbursement, false otherwise
     */
    public boolean isDisbursementActionAllowed() {
        if (paymentGroup.getDisbursementDate() == null) {
            if (PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentGroup.getPaymentStatus().getCode())) {
                return false;
            }
        return true;
    }

        String daysStr = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentDetail.class, PdpParameterConstants.DISBURSEMENT_CANCELLATION_DAYS);
        int days = Integer.valueOf(daysStr);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, (days * -1));
        c.set(Calendar.HOUR, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);
        Timestamp lastDisbursementActionDate = new Timestamp(c.getTimeInMillis());

        Calendar c2 = Calendar.getInstance();
        c2.setTime(paymentGroup.getDisbursementDate());
        c2.set(Calendar.HOUR, 11);
        c2.set(Calendar.MINUTE, 59);
        c2.set(Calendar.SECOND, 59);
        c2.set(Calendar.MILLISECOND, 59);
        c2.set(Calendar.AM_PM, Calendar.PM);
        Timestamp disbursementDate = new Timestamp(c2.getTimeInMillis());

        // date is equal to or after lastActionDate Allowed
        return ((disbursementDate.compareTo(lastDisbursementActionDate)) >= 0);
    }

    /**
     * @return total of all account detail amounts
     */
    public KualiDecimal getAccountTotal() {
        KualiDecimal acctTotal = new KualiDecimal(0.00);

        for (PaymentAccountDetail paymentAccountDetail : accountDetail) {
            if (paymentAccountDetail.getAccountNetAmount() != null) {
                acctTotal = acctTotal.add(paymentAccountDetail.getAccountNetAmount());
            }
        }

        return acctTotal;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Takes a <code>String</code> and attempt to format as <code>Timestamp</code for setting the
     * invoiceDate field
     * 
     * @param invoiceDate Timestamp as string
     */
    public void setInvoiceDate(String invoiceDate) throws ParseException {
        this.invoiceDate = SpringContext.getBean(DateTimeService.class).convertToSqlDate(invoiceDate);
    }

    /**
     * @hibernate.set name="accountDetail"
     * @hibernate.collection-key column="pmt_dtl_id"
     * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentAccountDetail"
     */
    public List<PaymentAccountDetail> getAccountDetail() {
        return accountDetail;
    }

    public void setAccountDetail(List<PaymentAccountDetail> ad) {
        accountDetail = ad;
    }

    public void addAccountDetail(PaymentAccountDetail pad) {
        pad.setPaymentDetail(this);
        accountDetail.add(pad);
    }

    public void deleteAccountDetail(PaymentAccountDetail pad) {
        accountDetail.remove(pad);
    }

    public List<PaymentNoteText> getNotes() {
        return notes;
    }

    public void setNotes(List<PaymentNoteText> n) {
        notes = n;
    }

    public void addNote(PaymentNoteText pnt) {
        if (!StringUtils.isBlank(pnt.getCustomerNoteText())) {
            pnt.setPaymentDetail(this);
            notes.add(pnt);
        } else {
            LOG.warn("Did not add note to payment detail build from Document #: "+(!StringUtils.isBlank(custPaymentDocNbr) ? custPaymentDocNbr : "")+" because note was empty");
        }
    }

    /**
     * Constructs a new <code>PaymentNoteText</code> for the given payment text and adds to the detail <code>List</code>
     * 
     * @param paymentText note text
     */
    public void addPaymentText(String paymentText) {
        PaymentNoteText paymentNoteText = new PaymentNoteText();

        paymentNoteText.setCustomerNoteText(paymentText);
        paymentNoteText.setCustomerNoteLineNbr(new KualiInteger(this.notes.size() + 1));

        addNote(paymentNoteText);
    }

    public void deleteNote(PaymentNoteText pnt) {
        notes.remove(pnt);
    }

    /**
     * @hibernate.id column="PMT_DTL_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_DTL_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     * @hibernate.property column="CUST_PMT_DOC_NBR" length="9"
     */
    public String getCustPaymentDocNbr() {
        return custPaymentDocNbr;
    }

    /**
     * @return
     * @hibernate.property column="INV_NBR" length="14"
     */
    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_DSCT_AMT" length="14"
     */
    public KualiDecimal getInvTotDiscountAmount() {
        return invTotDiscountAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_OTHR_CRDT_AMT" length="14"
     */
    public KualiDecimal getInvTotOtherCreditAmount() {
        return invTotOtherCreditAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_OTHR_DEBIT_AMT" length="14"
     */
    public KualiDecimal getInvTotOtherDebitAmount() {
        return invTotOtherDebitAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_SHP_AMT" length="14"
     */
    public KualiDecimal getInvTotShipAmount() {
        return invTotShipAmount;
    }

    /**
     * @return
     * @hibernate.property column="NET_PMT_AMT" length="14"
     */
    public KualiDecimal getNetPaymentAmount() {
        return netPaymentAmount;
    }

    /**
     * @return
     * @hibernate.property column="ORG_DOC_NBR" length="10"
     */
    public String getOrganizationDocNbr() {
        return organizationDocNbr;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_INV_AMT" length="14"
     */
    public KualiDecimal getOrigInvoiceAmount() {
        return origInvoiceAmount;
    }

    /**
     * @return
     * @hibernate.property column="PO_NBR" length="9"
     */
    public String getPurchaseOrderNbr() {
        return purchaseOrderNbr;
    }

    /**
     * @return
     * @hibernate.property column="REQS_NBR" length=8"
     */
    public String getRequisitionNbr() {
        return requisitionNbr;
    }

    /**
     * @return Returns the paymentGroup.
     */
    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    /**
     * @param string
     */
    public void setCustPaymentDocNbr(String string) {
        custPaymentDocNbr = string;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param string
     */
    public void setInvoiceNbr(String string) {
        invoiceNbr = string;
    }

    /**
     * @param decimal
     */
    public void setInvTotDiscountAmount(KualiDecimal decimal) {
        invTotDiscountAmount = decimal;
    }

    public void setInvTotDiscountAmount(String decimal) {
        invTotDiscountAmount = new KualiDecimal(decimal);
    }

    /**
     * @param decimal
     */
    public void setInvTotOtherCreditAmount(KualiDecimal decimal) {
        invTotOtherCreditAmount = decimal;
    }

    public void setInvTotOtherCreditAmount(String decimal) {
        invTotOtherCreditAmount = new KualiDecimal(decimal);
    }

    /**
     * @param decimal
     */
    public void setInvTotOtherDebitAmount(KualiDecimal decimal) {
        invTotOtherDebitAmount = decimal;
    }

    public void setInvTotOtherDebitAmount(String decimal) {
        invTotOtherDebitAmount = new KualiDecimal(decimal);
    }

    /**
     * @param decimal
     */
    public void setInvTotShipAmount(KualiDecimal decimal) {
        invTotShipAmount = decimal;
    }

    public void setInvTotShipAmount(String decimal) {
        invTotShipAmount = new KualiDecimal(decimal);
    }

    /**
     * @param decimal
     */
    public void setNetPaymentAmount(KualiDecimal decimal) {
        netPaymentAmount = decimal;
    }

    public void setNetPaymentAmount(String decimal) {
        netPaymentAmount = new KualiDecimal(decimal);
    }

    /**
     * @param string
     */
    public void setOrganizationDocNbr(String string) {
        organizationDocNbr = string;
    }

    /**
     * @param decimal
     */
    public void setOrigInvoiceAmount(KualiDecimal decimal) {
        origInvoiceAmount = decimal;
    }

    public void setOrigInvoiceAmount(String decimal) {
        origInvoiceAmount = new KualiDecimal(decimal);
    }

    /**
     * @param string
     */
    public void setPurchaseOrderNbr(String string) {
        purchaseOrderNbr = string;
    }

    /**
     * @param string
     */
    public void setRequisitionNbr(String string) {
        requisitionNbr = string;
    }

    /**
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * @return Returns the primaryCancelledPayment.
     */
    public Boolean getPrimaryCancelledPayment() {
        return primaryCancelledPayment;
    }

    /**
     * @param primaryCancelledPayment The primaryCancelledPayment to set.
     */
    public void setPrimaryCancelledPayment(Boolean primaryCancelledPayment) {
        this.primaryCancelledPayment = primaryCancelledPayment;
    }

    /**
     * @param paymentGroup The paymentGroup to set.
     */
    public void setPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroup = paymentGroup;
    }

    /**
     * Gets the paymentGroupId attribute.
     * 
     * @return Returns the paymentGroupId.
     */
    public KualiInteger getPaymentGroupId() {
        return paymentGroupId;
    }

    /**
     * Sets the paymentGroupId attribute value.
     * 
     * @param paymentGroupId The paymentGroupId to set.
     */
    public void setPaymentGroupId(KualiInteger paymentGroupId) {
        this.paymentGroupId = paymentGroupId;
    }

    /**
     * Gets the financialSystemOriginCode attribute.
     * 
     * @return Returns the financialSystemOriginCode.
     */
    public String getFinancialSystemOriginCode() {
        return financialSystemOriginCode;
    }

    /**
     * Sets the financialSystemOriginCode attribute value.
     * 
     * @param financialSystemOriginCode The financialSystemOriginCode to set.
     */
    public void setFinancialSystemOriginCode(String financialSystemOriginCode) {
        this.financialSystemOriginCode = financialSystemOriginCode;
    }
    
    /**
     * @return the customerInstitutionNumber
     */
    public String getCustomerInstitutionNumber() {
        return customerInstitutionNumber;
    }

    /**
     * @param customerInstitutionNumber the customerInstitutionNumber to set
     */
    public void setCustomerInstitutionNumber(String customerInstitutionNumber) {
        this.customerInstitutionNumber = customerInstitutionNumber;
    }

    /**
     * This method returns a String representation of the payment detail notes
     * 
     * @return the String representation of the payment detail notes
     */
    public String getNotesText() {
        StringBuffer notes = new StringBuffer();
        List<PaymentNoteText> notesList = getNotes();
        for (PaymentNoteText note : notesList) {
            notes.append(note.getCustomerNoteText());
            notes.append(KFSConstants.NEWLINE);
        }
        return notes.toString();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

    /**
     * This method returns the number of payments in the payment group associated with this payment detail.
     * 
     * @return the number of payments in the payment group
     */
    public int getNbrOfPaymentsInPaymentGroup() {
        return paymentGroup.getPaymentDetails().size();
    }

    /**
     * This method returns the number of payments in the disbursement associated with this payment detail.
     * 
     * @return the number of payments in the disbursement
     */
    public int getNbrOfPaymentsInDisbursement() {
        
        int nbrOfPaymentsInDisbursement = 0;
        if (ObjectUtils.isNotNull((paymentGroup.getDisbursementNbr()))) {
            List<PaymentGroup> paymentGroupList = SpringContext.getBean(PaymentGroupService.class).getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());
            for (PaymentGroup paymentGroup : paymentGroupList) {
                nbrOfPaymentsInDisbursement += paymentGroup.getPaymentDetails().size();
            }
        }
        return nbrOfPaymentsInDisbursement;
    }

}
