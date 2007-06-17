/*
 * Created on Jul 7, 2004
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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;

/**
 * @author jsissom
 * @hibernate.class table="PDP.PDP_CUST_BNK_T"
 *
 */
public class CustomerBank implements UserRequired,Serializable,PersistenceBrokerAware {
  private Integer id;         //CUST_BNK_ID
  private Integer version;

  private Integer customerId;
  private CustomerProfile customerProfile;      // CUST_ID

  private Integer bankId;
  private Bank bank;                            // BNK_ID
  
  private String disbursementTypeCode;
  private DisbursementType disbursementType;    // DISB_TYP_CD

  private Timestamp lastUpdate; // LST_UPDT_TS
  private PdpUser lastUpdateUser; 
  private String lastUpdateUserId; // LST_UPDT_USR_ID
  
  /**
   * 
   */
  public CustomerBank() {
    super();
  }

  public PdpUser getLastUpdateUser() {
      return lastUpdateUser;
  }

  public void setLastUpdateUser(PdpUser s) {
      if ( s != null ) {
          this.lastUpdateUserId = s.getUniversalUser().getPersonUniversalIdentifier();
      } else {
          this.lastUpdateUserId = null;
      }
      this.lastUpdateUser = s;
  }

  public String getLastUpdateUserId() {
      return lastUpdateUserId;
  }

  public void setLastUpdateUserId(String lastUpdateUserId) {
    this.lastUpdateUserId = lastUpdateUserId;
  }

  public void updateUser(UniversalUserService userService) throws UserNotFoundException {
      UniversalUser u = userService.getUniversalUser(lastUpdateUserId);
      if ( u == null ) {
          setLastUpdateUser(null);
      } else {
          setLastUpdateUser(new PdpUser(u));
      }
  }

  /**
   * @hibernate.many-to-one column="CUST_ID" class="edu.iu.uis.pdp.bo.CustomerProfile" not-null="true"
   * @return Returns the customerId.
   */
  public CustomerProfile getCustomerProfile() {
    return customerProfile;
  }

  /**
   * @param customerId The customerId to set.
   */
  public void setCustomerProfile(CustomerProfile customer) {
    this.customerProfile = customer;
  }

  /**
   * @hibernate.many-to-one column="BNK_ID" class="edu.iu.uis.pdp.bo.Bank" not-null="true"
   * @return Returns the bankId.
   */
  public Bank getBank() {
    return bank;
  }

  /**
   * @param bankId The bankId to set.
   */
  public void setBank(Bank bank) {
    this.bank = bank;
  }
  /**
   * @hibernate.many-to-one column="DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType" not-null="true"
   * @return Returns the disbursementType.
   */
  public DisbursementType getDisbursementType() {
    return disbursementType;
  }
  /**
   * @param disbursementType The disbursementType to set.
   */
  public void setDisbursementType(DisbursementType disbursementType) {
    this.disbursementType = disbursementType;
  }
  /**
   * @hibernate.id column="CUST_BNK_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_CUST_BNK_ID_SEQ" 
   * @return Returns the id.
   */
  public Integer getId() {
    return id;
  }
  /**
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  /**
   * @hibernate.version column="VER_NBR"
   * @return Returns the version.
   */
  public Integer getVersion() {
    return version;
  }
  /**
   * @param ojbVerNbr The ojbVerNbr to set.
   */
  public void setVersion(Integer ver) {
    this.version = ver;
  }

  /**
   * @hibernate.property column="LST_UPDT_TS" not-null="true"
   * @return Returns the lastUpdate.
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
  }

  /**
   * @param timestamp
   */
  public void setLastUpdate(Timestamp timestamp) {
    lastUpdate = timestamp;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof CustomerBank) ) { return false; }
    CustomerBank o = (CustomerBank)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,37)
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
