/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author jsissom
 *
 */
public class ProcessSummary implements Serializable,PersistenceBrokerAware {
  private Integer id;
  private Integer customerId;
  private String disbursementTypeCode;
  private Integer processId;
  private String sortGroupId;
  private Integer beginDisbursementNbr;
  private Integer endDisbursementNbr;
  private BigDecimal processTotalAmount;
  private Integer processTotalCount;
  private Integer version;
  private DisbursementType disbursementType;
  private PaymentProcess process;
  private CustomerProfile customer;
  private Timestamp lastUpdate;

  public ProcessSummary() {
  }

  public String getSortGroupId() {
    return sortGroupId;
  }
  public void setSortGroupId(String sortGroupId) {
    this.sortGroupId = sortGroupId;
  }
  public String getSortGroupName() {
    if ( "B".equals(sortGroupId) ) {
      return "Immediate";
    } else if ( "C".equals(sortGroupId) ) {
      return "Special Handling";
    } else if ( "D".equals(sortGroupId) ) {
      return "Attachment";
    } else {
      return "Other";
    }
  }
  public Integer getBeginDisbursementNbr() {
    return beginDisbursementNbr;
  }
  public void setBeginDisbursementNbr(Integer beginDisbursementNbr) {
    this.beginDisbursementNbr = beginDisbursementNbr;
  }
  public CustomerProfile getCustomer() {
    return customer;
  }
  public void setCustomer(CustomerProfile customer) {
    this.customer = customer;
  }
  public DisbursementType getDisbursementType() {
    return disbursementType;
  }
  public void setDisbursementType(DisbursementType disbursementType) {
    this.disbursementType = disbursementType;
  }
  public Integer getEndDisbursementNbr() {
    return endDisbursementNbr;
  }
  public void setEndDisbursementNbr(Integer endDisbursementNbr) {
    this.endDisbursementNbr = endDisbursementNbr;
  }
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public PaymentProcess getProcess() {
    return process;
  }
  public void setProcess(PaymentProcess process) {
    this.process = process;
  }
  public BigDecimal getProcessTotalAmount() {
    return processTotalAmount;
  }
  public void setProcessTotalAmount(BigDecimal processTotalAmount) {
    this.processTotalAmount = processTotalAmount;
  }
  public Integer getProcessTotalCount() {
    return processTotalCount;
  }
  public void setProcessTotalCount(Integer processTotalCount) {
    this.processTotalCount = processTotalCount;
  }
  public Integer getVersion() {
    return version;
  }
  public void setVersion(Integer version) {
    this.version = version;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentProcess) ) { return false; }
    PaymentProcess tc = (PaymentProcess)obj;
    return new EqualsBuilder()
    .append(id, tc.getId())
    .isEquals();
  }
  
  public int hashCode() {
    return new HashCodeBuilder(67,5)
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
