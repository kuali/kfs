/*
 * Created on Jul 12, 2004
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
 * @author delyea
 * 
 * @hibernate.class  table="PDP.PDP_DISB_NBR_RANGE_T"
 */
public class DisbursementNumberRange implements UserRequired,Serializable,PersistenceBrokerAware {

  private Integer id;                     // DISB_NBR_RANGE_ID
  private String physCampusProcCode;      // PHYS_CMP_PROC_CD       
  private Integer beginDisbursementNbr;   // BEG_DISB_NBR        
  private Integer lastAssignedDisbNbr;    // LST_ASND_DISB_NBR      
  private Integer endDisbursementNbr;     // END_DISB_NBR     
  private Timestamp disbNbrEffectiveDt;   // DISB_NBR_EFF_DT   
  private Timestamp disbNbrExpirationDt;  // DISB_NBR_EXPR_DT         
  private Timestamp lastUpdate;           // LST_UPDT_TS
  private PdpUser lastUpdateUser;
  private String lastUpdateUserId;        // LST_UPDT_USR_ID
  private Integer version;                // VER_NBR

  private Integer bankId;
  private Bank bank;

  public DisbursementNumberRange() {
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
   * @hibernate.id column="DISB_NBR_RANGE_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_DISB_NBR_RANGE_ID_SEQ"
   * @return
   */
  public Integer getId() {
    return id;
  }
  /**
   * @param documentTypeId The documentTypeId to set.
   */
  public void setId(Integer documentTypeId) {
    this.id = documentTypeId;
  }
  /**
   * @return
   * @hibernate.property column="LST_UPDT_TS" not-null="true"
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
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
   * @hibernate.many-to-one class="edu.iu.uis.pdp.bo.Bank" column="BNK_ID"  not-null="true"
   */
  public Bank getBank() {
    return bank;
  }

  /**
   * @return
   * @hibernate.property column="BEG_DISB_NBR"
   */
  public Integer getBeginDisbursementNbr() {
    return beginDisbursementNbr;
  }

  /**
   * @return
   * @hibernate.property column="DISB_NBR_EFF_DT"
   */
  public Timestamp getDisbNbrEffectiveDt() {
    return disbNbrEffectiveDt;
  }

  /**
   * @return
   * @hibernate.property column="DISB_NBR_EXPR_DT"
   */
  public Timestamp getDisbNbrExpirationDt() {
    return disbNbrExpirationDt;
  }

  /**
   * @return
   * @hibernate.property column="END_DISB_NBR"
   */
  public Integer getEndDisbursementNbr() {
    return endDisbursementNbr;
  }

  /**
   * @return
   * @hibernate.property column="LST_ASND_DISB_NBR"
   */
  public Integer getLastAssignedDisbNbr() {
    return lastAssignedDisbNbr;
  }

  /**
   * @return
   * @hibernate.property column="PHYS_CMP_PROC_CD" length="2"
   */
  public String getPhysCampusProcCode() {
    return physCampusProcCode;
  }

  /**
   * @param integer
   */
  public void setBank(Bank bank) {
    this.bank = bank;
  }

  /**
   * @param integer
   */
  public void setBeginDisbursementNbr(Integer integer) {
    beginDisbursementNbr = integer;
  }

  /**
   * @param timestamp
   */
  public void setDisbNbrEffectiveDt(Timestamp timestamp) {
    disbNbrEffectiveDt = timestamp;
  }

  /**
   * @param timestamp
   */
  public void setDisbNbrExpirationDt(Timestamp timestamp) {
    disbNbrExpirationDt = timestamp;
  }

  /**
   * @param integer
   */
  public void setEndDisbursementNbr(Integer integer) {
    endDisbursementNbr = integer;
  }

  /**
   * @param integer
   */
  public void setLastAssignedDisbNbr(Integer integer) {
    lastAssignedDisbNbr = integer;
  }

  /**
   * @param timestamp
   */
  public void setLastUpdate(Timestamp timestamp) {
    lastUpdate = timestamp;
  }

  /**
   * @param string
   */
  public void setPhysCampusProcCode(String string) {
    physCampusProcCode = string;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof DisbursementNumberRange) ) { return false; }
    DisbursementNumberRange o = (DisbursementNumberRange)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,43)
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
