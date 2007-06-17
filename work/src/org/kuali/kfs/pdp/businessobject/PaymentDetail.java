/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author delyea
 * 
 * @hibernate.class  table="PDP.PDP_PMT_DTL_T"
 */

public class PaymentDetail implements Serializable,PersistenceBrokerAware {
  private static BigDecimal zero = new BigDecimal(0);

  private Integer id;                       // PMT_DTL_ID
  private String invoiceNbr;                // INV_NBR
  private Timestamp invoiceDate;            // INV_DT
  private String purchaseOrderNbr;          // PO_NBR
  private String custPaymentDocNbr;         // CUST_PMT_DOC_NBR    
  private String financialDocumentTypeCode; // FDOC_TYP_CD 
  private String requisitionNbr;            // REQS_NBR         
  private String organizationDocNbr;        // ORG_DOC_NBR         
  private BigDecimal origInvoiceAmount;     // ORIG_INV_AMT      
  private BigDecimal netPaymentAmount;      // NET_PMT_AMT       
  private BigDecimal invTotDiscountAmount;  // INV_TOT_DSCT_AMT       
  private BigDecimal invTotShipAmount;      // INV_TOT_SHP_AMT        
  private BigDecimal invTotOtherDebitAmount;  // INV_TOT_OTHR_DEBIT_AMT      
  private BigDecimal invTotOtherCreditAmount; // INV_TOT_OTHR_CRDT_AMT      
  private Timestamp lastUpdate;             // LST_UPDT_TS
  private Integer version;                  // VER_NBR
  private Boolean primaryCancelledPayment;  // PDP_PRM_PMT_CNCL_IND
  private Timestamp lastDisbursementActionDate;
  
  private List accountDetail = new ArrayList();
  private List notes = new ArrayList();
  
  private Integer paymentGroupId;
  private PaymentGroup paymentGroup;

  public PaymentDetail() {
    super();
  }

  private boolean isDetailAmountProvided() {
    return (origInvoiceAmount != null) || (invTotDiscountAmount != null) || (invTotShipAmount != null) ||
    (invTotOtherDebitAmount != null) || (invTotOtherCreditAmount != null);
  }

  public BigDecimal getCalculatedPaymentAmount() {
    BigDecimal orig_invoice_amt = origInvoiceAmount == null ? zero : origInvoiceAmount;
    BigDecimal invoice_tot_discount_amt = invTotDiscountAmount == null ? zero : invTotDiscountAmount;
    BigDecimal invoice_tot_ship_amt = invTotShipAmount == null ? zero : invTotShipAmount;
    BigDecimal invoice_tot_other_debits = invTotOtherDebitAmount == null ? zero : invTotOtherDebitAmount;
    BigDecimal invoice_tot_other_credits = invTotOtherCreditAmount == null ? zero : invTotOtherCreditAmount;

    BigDecimal t = orig_invoice_amt.subtract(invoice_tot_discount_amt);
    t = t.add(invoice_tot_ship_amt);
    t = t.add(invoice_tot_other_debits);
    t = t.subtract(invoice_tot_other_credits);

    return t;
  }
  
  public boolean isDisbursementActionAllowed() {
    if (paymentGroup.getDisbursementDate() == null) {
      if ("EXTR".equals(paymentGroup.getPaymentStatus().getCode())) {
        return false;
      }
      return true;
    }
    Calendar c = Calendar.getInstance();
    c.setTime(paymentGroup.getDisbursementDate());
    c.set(Calendar.HOUR, 11);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 59);
    c.set(Calendar.AM_PM, Calendar.PM);
    Timestamp disbursementDate = new Timestamp(c.getTimeInMillis());
    // date is equal to or after lastActionDate Allowed
    return ( (disbursementDate.compareTo(this.lastDisbursementActionDate)) >= 0 );
  }

  public Timestamp getLastDisbursementActionDate() {
    return lastDisbursementActionDate;
  }
  public void setLastDisbursementActionDate(Timestamp lastDisbursementActionDate) {
    this.lastDisbursementActionDate = lastDisbursementActionDate;
  }
  public Timestamp getInvoiceDate() {
    return invoiceDate;
  }
  public void setInvoiceDate(Timestamp invoiceDate) {
    this.invoiceDate = invoiceDate;
  }
  /**
   * @hibernate.set name="accountDetail"
   * @hibernate.collection-key column="pmt_dtl_id"
   * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentAccountDetail"
   */
  public List getAccountDetail() {
   return accountDetail;
  }
  
  public void setAccountDetail(List ad) {
   accountDetail = ad;
  }

  public void addAccountDetail(PaymentAccountDetail pad) {
    pad.setPaymentDetail(this);
    accountDetail.add(pad);
  }

  public void deleteAccountDetail(PaymentAccountDetail pad) {
    accountDetail.remove(pad);
  }

  public List getNotes() {
    return notes;
  }

  public void setNotes(List n) {
    notes = n;
  }

  public void addNote(PaymentNoteText pnt) {
    pnt.setPaymentDetail(this);
    notes.add(pnt);
  }

  public void deleteNote(PaymentNoteText pnt) {
    notes.remove(pnt);
  }

  /**
   * @hibernate.id column="PMT_DTL_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_DTL_ID_SEQ"
   * @return
   */
  public Integer getId() {
    return id;
  }

  /** 
   * @return
   * @hibernate.version column="VER_NBR" not-null="true"
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * @return
   * @hibernate.property column="LST_UPDT_TS" length="7"
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
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
  public BigDecimal getInvTotDiscountAmount() {
    return invTotDiscountAmount;
  }

  /**
   * @return
   * @hibernate.property column="INV_TOT_OTHR_CRDT_AMT" length="14"
   */
  public BigDecimal getInvTotOtherCreditAmount() {
    return invTotOtherCreditAmount;
  }

  /**
   * @return
   * @hibernate.property column="INV_TOT_OTHR_DEBIT_AMT" length="14"
   */
  public BigDecimal getInvTotOtherDebitAmount() {
    return invTotOtherDebitAmount;
  }

  /**
   * @return
   * @hibernate.property column="INV_TOT_SHP_AMT" length="14"
   */
  public BigDecimal getInvTotShipAmount() {
    return invTotShipAmount;
  }

  /**
   * @return
   * @hibernate.property column="NET_PMT_AMT" length="14"
   */
  public BigDecimal getNetPaymentAmount() {
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
  public BigDecimal getOrigInvoiceAmount() {
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
  public void setId(Integer integer) {
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
  public void setInvTotDiscountAmount(BigDecimal decimal) {
    invTotDiscountAmount = decimal;
  }

  /**
   * @param decimal
   */
  public void setInvTotOtherCreditAmount(BigDecimal decimal) {
    invTotOtherCreditAmount = decimal;
  }

  /**
   * @param decimal
   */
  public void setInvTotOtherDebitAmount(BigDecimal decimal) {
    invTotOtherDebitAmount = decimal;
  }

  /**
   * @param decimal
   */
  public void setInvTotShipAmount(BigDecimal decimal) {
    invTotShipAmount = decimal;
  }

  /**
   * @param timestamp
   */
  public void setLastUpdate(Timestamp timestamp) {
    lastUpdate = timestamp;
  }

  /**
   * @param decimal
   */
  public void setNetPaymentAmount(BigDecimal decimal) {
    netPaymentAmount = decimal;
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
  public void setOrigInvoiceAmount(BigDecimal decimal) {
    origInvoiceAmount = decimal;
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
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
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
  
  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentDetail) ) { return false; }
    PaymentDetail o = (PaymentDetail)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(61,67)
      .append(id)
      .toHashCode();
  }

  public String toString() {
    return new ToStringBuilder(this)
      .append("id",  this.id)
      .toString();
  }

  public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
    lastUpdate = new Timestamp( (new Date()).getTime() );
  }

  public void afterInsert(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    lastUpdate = new Timestamp( (new Date()).getTime() );    
  }

  public void afterUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    
  }

  public void beforeDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }
  
  public void afterDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException {

  }
}
