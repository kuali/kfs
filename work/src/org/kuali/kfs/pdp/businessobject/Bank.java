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
 * @hibernate.class table="PDP.PDP_BNK_CD_T"
 *
 */
public class Bank implements UserRequired,Serializable,PersistenceBrokerAware {
  private Integer id;
  private Integer version;
  private String description;
  private String name;
  private String routingNumber;
  private String accountNumber;
  private Boolean active;
  private DisbursementType disbursementType;
  private String disbursementTypeCode;
  private Timestamp lastUpdate;
  private PdpUser lastUpdateUser;
  private String lastUpdateUserId;
  
  /**
   * 
   */
  public Bank() {
    super();
  }

  public void updateUser(UniversalUserService userService) throws UserNotFoundException {
      UniversalUser u = userService.getUniversalUser(lastUpdateUserId);
      if ( u == null ) {
          lastUpdateUserId = null;
          lastUpdateUser = null;
      } else {
          lastUpdateUserId = u.getPersonUniversalIdentifier();
          lastUpdateUser = new PdpUser(u);
      }
  }

  /**
   * @hibernate.property column="BNK_ACCT_NBR" length="17" not-null="true"
   * @return Returns the accountNumber.
   */
  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * @param accountNumber The accountNumber to set.
   */
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  /**
   * @hibernate.property column="BNK_ACTV_IND" type="yes_no" not-null="true"
   * @return Returns the active.
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * @param active The active to set.
   */
  public void setActive(Boolean active) {
    this.active = active;
  }
  /**
   * @hibernate.property column="BNK_DESC" length="25" not-null="true"
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }
  /**
   * @param description The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
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
   * @hibernate.id column="BNK_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_BANK_ID_SEQ" 
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
   * @hibernate.property column="LST_UPDT_TS" not-null="true"
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
  
  /**
   * @hibernate.property column="BNK_NM" not-null="true"
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }
  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
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
   * @hibernate.property column="BNK_RTNG_NBR" length="9" not-null="true"
   * @return Returns the routingNumber.
   */
  public String getRoutingNumber() {
    return routingNumber;
  }
  /**
   * @param routingNumber The routingNumber to set.
   */
  public void setRoutingNumber(String routingNumber) {
    this.routingNumber = routingNumber;
  }

  /**
   * @hibernate.property column="LST_UPDT_USR_ID" not-null="true"
   * @return Returns the lastUpdateUserId.
   */
  public String getLastUpdateUserId() {
    return lastUpdateUserId;
  }
  
  /**
   * @return Returns the lastUpdateUser.
   */
  public PdpUser getLastUpdateUser() {
    return lastUpdateUser;
  }
  
  /**
   * @param lastUpdateUser The lastUpdateUser to set.
   */
  public void setLastUpdateUser(PdpUser lastUpdateUser) {
    if ( lastUpdateUser != null ) {
      this.lastUpdateUserId = lastUpdateUser.getUniversalUser().getPersonUniversalIdentifier();
    } else {
      this.lastUpdateUserId = null;
    }
    this.lastUpdateUser = lastUpdateUser;
  }

  /**
   * @param lastUpdateUserId The lastUpdateUserId to set.
   */
  public void setLastUpdateUserId(String lastUpdateUserId) {
    this.lastUpdateUserId = lastUpdateUserId;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof Bank) ) { return false; }
    Bank o = (Bank)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,67)
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
