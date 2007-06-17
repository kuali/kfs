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
 * @hibernate.class  table="PDP.PDP_ACH_ACCT_NBR_T"
 */

public class AchAccountNumber implements Serializable,PersistenceBrokerAware {
  
  private Integer id;   // PMT_GRP_ID    Primary Key       
  private String achBankAccountNbr; // ACH_BNK_ACCT_NBR
  private Integer version;           // VER_NBR
  private Timestamp lastUpdate;

  public AchAccountNumber() {
    super();
  }

  /**
   * returns a partial string of the account number
   *   if the account number is zero chars it returns empty string
   *   if the account number is 1-4 chars it returns everything masked
   *   if the account number is 5-8 chars it returns the last 2 numbers non masked
   *   if the account number is > 8 chars it returns the last 4 numbers non masked
   * @return
   * @hibernate.property column="ACH_BNK_ACCT_NBR" length="17"
   */
  public String getPartialMaskAchBankAccountNbr() {
    String partialAccountNumber = "";
    String numbers = "";
    int accountLength = achBankAccountNbr.length();
    if (accountLength == 0) {
      return "";
    } else if ( (accountLength == 3) || (accountLength == 4) ) {
      numbers = achBankAccountNbr.substring(accountLength - 1);
    } else if ( (accountLength < 8) && (accountLength > 4) ) {
      numbers = achBankAccountNbr.substring(accountLength - 3);
    } else if (accountLength >= 8) {
      numbers = achBankAccountNbr.substring(accountLength - 4);
    }
    for (int i = 0; i < (accountLength - numbers.length()); i++) {
      partialAccountNumber = partialAccountNumber + "*";
    }
    return partialAccountNumber + numbers;
  }

  /**
   * @return
   * @hibernate.property column="ACH_BNK_ACCT_NBR" length="17"
   */
  public String getAchBankAccountNbr() {
    return achBankAccountNbr;
  }

  /**
   * @return
   * @hibernate.id column="PMT_GRP_ID" generator-class="assigned"
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
   * @param integer
   */
  public void setAchBankAccountNbr(String s) {
    achBankAccountNbr = s;
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
  public void setVersion(Integer integer) {
    version = integer;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof AchAccountNumber) ) { return false; }
    AchAccountNumber o = (AchAccountNumber)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(67,79)
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
