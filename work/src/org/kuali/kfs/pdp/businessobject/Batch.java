/*
 * Created on Jul 8, 2004
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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;

/**
 * @author delyea
 * 
 * @hibernate.class  table="PDP.PDP_BATCH_T"
 */

public class Batch implements UserRequired,Serializable,PersistenceBrokerAware {

  private Integer id;                         // PMT_BATCH_ID

  private Integer customerId;
  private CustomerProfile customerProfile;    // CUST_ID

  private String paymentFileName;             // PMT_FL_NM
  private Timestamp customerFileCreateTimestamp; // CUST_FL_CRTN_TS
  private Integer paymentCount;               // PMT_CNT
  private BigDecimal paymentTotalAmount;      // PMT_TOT_AMT
  private PdpUser submiterUser;  
  private String submiterUserId;              // SBMTR_USR_ID
  private Timestamp fileProcessTimestamp;     // FL_PROC_TS
  private Timestamp lastUpdate;
  private Integer version;                    // VER_NBR
  
  public Batch() {
    super();
  }

  /**
   * @hibernate.property column="FL_PROC_TS"
   */
  public Timestamp getFileProcessTimestamp() {
    return fileProcessTimestamp;
  }

  /** 
   * @hibernate.version column="VER_NBR" not-null="true"
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * @return
   * @hibernate.id column="PMT_BATCH_ID"  generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_BATCH_ID_SEQ"
   */
  public Integer getId() {
    return id;
  }

  /**
   * @return
   * @hibernate.property column="CUST_FL_CRTN_TS"
   */
  public Timestamp getCustomerFileCreateTimestamp() {
    return customerFileCreateTimestamp;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CNT"
   */
  public Integer getPaymentCount() {
    return paymentCount;
  }

  /**
   * @return
   * @hibernate.property column="PMT_FL_NM" lenght="30"
   */
  public String getPaymentFileName() {
    return paymentFileName;
  }

  /**
   * @return
   * @hibernate.property column="PMT_TOT_AMT"         
   */
  public BigDecimal getPaymentTotalAmount() {
    return paymentTotalAmount;
  }

  /**
   * @return
   * @hibernate.many-to-one column="CUST_ID" class="edu.iu.uis.pdp.bo.CustomerProfile"
   */
  public CustomerProfile getCustomerProfile() {
    return customerProfile;
  }

  /**
   * @param string
   */
  public void setCustomerFileCreateTimestamp(Timestamp t) {
    customerFileCreateTimestamp = t;
  }

  /**
   * @param timestamp
   */
  public void setFileProcessTimestamp(Timestamp timestamp) {
    fileProcessTimestamp = timestamp;
  }

  /**
   * @param integer
   */
  public void setId(Integer integer) {
    id = integer;
  }

  /**
   * @param integer
   */
  public void setPaymentCount(Integer integer) {
    paymentCount = integer;
  }

  /**
   * @param string
   */
  public void setPaymentFileName(String string) {
    paymentFileName = string;
  }

  /**
   * @param decimal
   */
  public void setPaymentTotalAmount(BigDecimal decimal) {
    paymentTotalAmount = decimal;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  /**
   * @param integer
   */
  public void setCustomerProfile(CustomerProfile cp) {
    customerProfile = cp;
  }

  public PdpUser getSubmiterUser() {
    return submiterUser;
  }
  
  public void setSubmiterUser(PdpUser s) {
    if ( s != null ) {
      this.submiterUserId = s.getUniversalUser().getPersonUniversalIdentifier();
    } else {
      this.submiterUserId = null;
    }
    this.submiterUser = s;
  }

  /**
   * @hibernate.property column="SBMTR_USR_ID" length="11" not-null="true"
   * @return Returns the submiterUserId.
   */
  public String getSubmiterUserId() {
    return submiterUserId;
  }

  /**
   * @param submiterUserId The submiterUserId to set.
   */
  public void setSubmiterUserId(String submiterUserId) {
    this.submiterUserId = submiterUserId;
  }

  
  public void updateUser(UniversalUserService userService) throws UserNotFoundException {
      UniversalUser u = userService.getUniversalUser(submiterUserId);
      if ( u == null ) {
          setSubmiterUser(null);
      } else {
          setSubmiterUser(new PdpUser(u));
      }
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof Batch) ) { return false; }
    Batch o = (Batch)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,89)
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
