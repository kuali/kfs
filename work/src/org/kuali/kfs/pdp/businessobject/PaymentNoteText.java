/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author delyea
 * 
 * @hibernate.class  table="PDP.PDP_PMT_NTE_TXT_T"
 */

public class PaymentNoteText implements Serializable,PersistenceBrokerAware {

  private Integer id;                   // PMT_NTE_ID

  private Integer paymentDetailId;
  private PaymentDetail paymentDetail;  // PMT_DTL_ID

  private Integer customerNoteLineNbr;  // CUST_NTE_LN_NBR
  private String customerNoteText;      // CUST_NTE_TXT
  private Timestamp lastUpdate;
  private Integer version;              // VER_NBR
  
  public PaymentNoteText() {
    super();
  }

  /**
   * @hibernate.id column="PMT_NTE_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_NTE_ID_SEQ"
   * @return Returns the paymentNoteId.
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param paymentNoteId The paymentNoteId to set.
   */
  public void setId(Integer paymentNoteId) {
    this.id = paymentNoteId;
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
   * @hibernate.property column="CUST_NTE_LN_NBR" not-null="true"
   */
  public Integer getCustomerNoteLineNbr() {
    return customerNoteLineNbr;
  }

  /**
   * @return
   * @hibernate.property column="CUST_NTE_TXT" length="60" not-null="true"
   */
  public String getCustomerNoteText() {
    return customerNoteText;
  }

  /**
   * @return
   * @hibernate.many-to-one column="PMT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentDetail"
   */
  public PaymentDetail getPaymentDetail() {
    return paymentDetail;
  }

  /**
   * @param integer
   */
  public void setCustomerNoteLineNbr(Integer integer) {
    customerNoteLineNbr = integer;
  }

  /**
   * @param string
   */
  public void setCustomerNoteText(String string) {
    customerNoteText = string;
  }

  /**
   * @param integer
   */
  public void setPaymentDetail(PaymentDetail pd) {
    paymentDetail = pd;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentNoteText) ) { return false; }
    PaymentNoteText o = (PaymentNoteText)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,7)
      .append(id)
      .toHashCode();
  }

  public String toString() {
    return new ToStringBuilder(this)
      .append("id",  this.id)
      .toString();
  }

  /**
   * @return Returns the lastUpdate.
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
  }
  /**
   * @param lastUpdate The lastUpdate to set.
   */
  public void setLastUpdate(Timestamp lastUpdate) {
    this.lastUpdate = lastUpdate;
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
