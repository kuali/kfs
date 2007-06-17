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
 * @hibernate.class  table="PDP.PDP_PMT_GRP_HIST_T"
 */

public class PaymentGroupHistory implements UserRequired,Serializable,PersistenceBrokerAware {

  private Integer id;                      // PMT_GRP_HIST_ID
  
  private String changeNoteText;           // PMT_CHG_NTE_TXT VARCHAR2 250
  private PdpUser changeUser;         
  private String changeUserId;             // PMT_CHG_USR_ID VARCHAR2 8         
  private Timestamp changeTime;            // PMT_CHG_TS DATE 7         
  private Timestamp origPaymentDate;       // ORIG_PMT_DT DATE 7         
  private String origAchBankRouteNbr;      // ORIG_ACH_BNK_RTNG_NBR VARCHAR2 17 0       
  private String origAdviceEmail;          // ORIG_ADV_EMAIL_ADDR VARCHAR2 50
  private Integer origDisburseNbr;         // ORIG_DISB_NBR NUMBER 9 0       
  private Timestamp origDisburseDate;      // ORIG_DISB_TS DATE 7         
  private Boolean origProcessImmediate;    // ORIG_PROC_IMD_IND VARCHAR2 1         
  private Boolean origPmtSpecHandling;     // ORIG_PMT_SPCL_HANDLG_IND VARCHAR2 1         
  private Boolean pmtCancelExtractStat;    // PMT_CNCL_EXTRT_STAT_IND VARCHAR2 1         
  private Timestamp pmtCancelExtractDate;  // PMT_CNCL_EXTRT_TS
  private Timestamp lastUpdate;
  private Integer version;                 // VER_NBR

  private String disbursementTypeCode;
  private DisbursementType disbursementType;
  
  private Integer bankId;
  private Bank bank;
  
  private String paymentStatusCode;
  private PaymentStatus origPaymentStatus; // ORIG_PMT_STAT_CD VARCHAR2 4         
  
  private Integer processId;
  private PaymentProcess paymentProcess;
  
  private String paymentChangeCode;
  private PaymentChange paymentChange;     // PMT_CHG_CD VARCHAR2 4         
  
  private Integer paymentGroupId;
  private PaymentGroup paymentGroup; // PMT_GRP_ID
  
  public PaymentGroupHistory() {
    super();
  }

  /**
   * @hibernate.id column="PMT_GRP_HIST_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_GRP_HIST_ID_SEQ"
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
   * @hibernate.many-to-one column="PMT_GRP_ID" class="edu.iu.uis.pdp.bo.PaymentGroup"
   * @return Returns the paymentGroup.
   */
  public PaymentGroup getPaymentGroup() {
    return paymentGroup;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CNCL_EXTRT_TS"
   */
  public Timestamp getPmtCancelExtractDate() {
    return pmtCancelExtractDate;
  }

  /**
   * @return
   * @hibernate.many-to-one column="PMT_CHG_CD" class="edu.iu.uis.pdp.bo.PaymentChange"
   */
  public PaymentChange getPaymentChange() {
    return paymentChange;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CHG_NTE_TXT" length="250"
   */
  public String getChangeNoteText() {
    return changeNoteText;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CHG_TS"
   */
  public Timestamp getChangeTime() {
    return changeTime;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_ACH_BNK_RTNG_NBR" length="17"
   */
  public String getOrigAchBankRouteNbr() {
    return origAchBankRouteNbr;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_ADV_EMAIL_ADDR" length="50"
   */
  public String getOrigAdviceEmail() {
    return origAdviceEmail;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_DISB_TS"
   */
  public Timestamp getOrigDisburseDate() {
    return origDisburseDate;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_DISB_NBR"
   */
  public Integer getOrigDisburseNbr() {
    return origDisburseNbr;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_PMT_DT"
   */
  public Timestamp getOrigPaymentDate() {
    return origPaymentDate;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_PMT_SPCL_HANDLG_IND" type="yes_no"
   */
  public Boolean getOrigPmtSpecHandling() {
    return origPmtSpecHandling;
  }

  /**
   * @return
   * @hibernate.many-to-one column="ORIG_PMT_STAT_CD" class="edu.iu.uis.pdp.bo.PaymentStatus"
   */
  public PaymentStatus getOrigPaymentStatus() {
    return origPaymentStatus;
  }

  /**
   * @return
   * @hibernate.property column="ORIG_PROC_IMD_IND" type="yes_no"
   */
  public Boolean getOrigProcessImmediate() {
    return origProcessImmediate;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CNCL_EXTRT_STAT_IND" type="yes_no"
   */
  public Boolean getPmtCancelExtractStat() {
    return pmtCancelExtractStat;
  }

  /**
   * @param string
   */
  public void setPaymentChange(PaymentChange pc) {
    paymentChange = pc;
  }

  /**
   * @param string
   */
  public void setChangeNoteText(String string) {
    changeNoteText = string;
  }

  /**
   * @param timestamp
   */
  public void setChangeTime(Timestamp timestamp) {
    changeTime = timestamp;
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
  public void setOrigAchBankRouteNbr(String s) {
    origAchBankRouteNbr = s;
  }

  /**
   * @param string
   */
  public void setOrigAdviceEmail(String string) {
    origAdviceEmail = string;
  }

  /**
   * @param timestamp
   */
  public void setOrigDisburseDate(Timestamp timestamp) {
    origDisburseDate = timestamp;
  }

  /**
   * @param integer
   */
  public void setOrigDisburseNbr(Integer integer) {
    origDisburseNbr = integer;
  }

  /**
   * @param timestamp
   */
  public void setOrigPaymentDate(Timestamp timestamp) {
    origPaymentDate = timestamp;
  }

  /**
   * @param boolean1
   */
  public void setOrigPmtSpecHandling(Boolean boolean1) {
    origPmtSpecHandling = boolean1;
  }

  /**
   * @param string
   */
  public void setOrigPaymentStatus(PaymentStatus ps) {
    origPaymentStatus = ps;
  }

  /**
   * @param boolean1
   */
  public void setOrigProcessImmediate(Boolean boolean1) {
    origProcessImmediate = boolean1;
  }

  /**
   * @param timestamp
   */
  public void setPmtCancelExtractDate(Timestamp timestamp) {
    pmtCancelExtractDate = timestamp;
  }

  /**
   * @param boolean1
   */
  public void setPmtCancelExtractStat(Boolean boolean1) {
    pmtCancelExtractStat = boolean1;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  /**
   * @param paymentGroupId The paymentGroupId to set.
   */
  public void setPaymentGroup(PaymentGroup pd) {
    this.paymentGroup = pd;
  }
  
  /**
   * @param DisbursementType
   */
  public void setDisbursementType(DisbursementType dt) {
    disbursementType = dt;
  }

  /**
   * @return
   * @hibernate.many-to-one column="ORIG_DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType"
   */
  public DisbursementType getDisbursementType() {
    return disbursementType;
  }

  /**
   * @param Bank
   */
  public void setBank(Bank bank) {
    this.bank = bank;
  }

  /**
   * @return
   * @hibernate.many-to-one column="ORIG_BNK_ID" class="edu.iu.uis.pdp.bo.Bank"
   */
  public Bank getBank() {
    return bank;
  }

  /**
   * @param PaymentProcess
   */
  public void setProcess(PaymentProcess ppl) {
    paymentProcess = ppl;
  }

  /**
   * @return
   * @hibernate.many-to-one column="ORIG_PROC_ID" class="edu.iu.uis.pdp.bo.PaymentProcess"
   */
  public PaymentProcess getProcess() {
    return paymentProcess;
  }

  /**
   * @hibernate.property column="PMT_CHG_USR_ID" length="11" not-null="true"
   * @return Returns the changeUserId.
   */
  public String getChangeUserId() {
    return changeUserId;
  }
  
  /**
   * @return Returns the changeUser.
   */
  public PdpUser getChangeUser() {
    return changeUser;
  }
  
  /**
   * @param changeUser The changeUser to set.
   */
  public void setChangeUser(PdpUser changeUser) {
    if ( changeUser != null ) {
      this.changeUserId = changeUser.getPersonUniversalIdentifier();
    } else {
      this.changeUserId = null;
    }
    this.changeUser = changeUser;
  }

  /**
   * @param changeUserId The changeUserId to set.
   */
  public void setChangeUserId(String changeUserId) {
    this.changeUserId = changeUserId;
  }
  
  public void updateUser(UniversalUserService userService) throws UserNotFoundException {
      UniversalUser u = userService.getUniversalUser(changeUserId);
      if ( u == null ) {
          setChangeUser(null);
      } else {
          setChangeUser(new PdpUser(u));
      }
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentGroupHistory) ) { return false; }
    PaymentGroupHistory o = (PaymentGroupHistory)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,3)
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
