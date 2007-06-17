/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
 * @hibernate.class  table="PDP.PDP_PMT_ACCT_DTL_T"
 */

public class PaymentAccountDetail implements Serializable,PersistenceBrokerAware {

  private Integer id;              // PMT_ACCT_DTL_ID
  private String finChartCode;     // FIN_COA_CD         
  private String accountNbr;       // ACCOUNT_NBR         
  private String subAccountNbr;    // SUB_ACCT_NBR         
  private String finObjectCode;    // FIN_OBJECT_CD         
  private String finSubObjectCode; // FIN_SUB_OBJ_CD         
  private String orgReferenceId;   // ORG_REFERENCE_ID         
  private String projectCode;      // PROJECT_CD         
  private BigDecimal accountNetAmount; // ACCT_NET_AMT 
  private Timestamp lastUpdate;
  private Integer version;         // VER_NBR

  private Integer paymentDetailId;
  private PaymentDetail paymentDetail; // PMT_DTL_ID

  private List accountHistory = new ArrayList();

  public PaymentAccountDetail() {
    super();
  }

  public List getAccountHistory() {
    return accountHistory;
  }

  public void setAccountHistory(List ah) {
    accountHistory = ah;
  }

  public void addAccountHistory(PaymentAccountHistory pah) {
    pah.setPaymentAccountDetail(this);
    accountHistory.add(pah);
  }

  public void deleteAccountDetail(PaymentAccountHistory pah) {
    accountHistory.remove(pah);
  }

  /**
   * @hibernate.id column="PMT_ACCT_DTL_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_ACCT_DTL_ID_SEQ"
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
   * @hibernate.many-to-one column="PMT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentDetail"
   */
  public PaymentDetail getPaymentDetail() {
    return this.paymentDetail;
  }

  /**
   * @return
   * @hibernate.property column="ACCOUNT_NBR" length="7"
   */
  public String getAccountNbr() {
    return accountNbr;
  }

  /**
   * @return
   * @hibernate.property column="ACCT_NET_AMT" length="14"
   */
  public BigDecimal getAccountNetAmount() {
    return accountNetAmount;
  }

  /**
   * @return
   * @hibernate.property column="FIN_COA_CD" length="2"
   */
  public String getFinChartCode() {
    return finChartCode;
  }

  /**
   * @return
   * @hibernate.property column="FIN_OBJECT_CD" length="4"
   */
  public String getFinObjectCode() {
    return finObjectCode;
  }

  /**
   * @return
   * @hibernate.property column="FIN_SUB_OBJ_CD" length="3"
   */
  public String getFinSubObjectCode() {
    return finSubObjectCode;
  }

  /**
   * @return
   * @hibernate.property column="ORG_REFERENCE_ID" length="8"
   */
  public String getOrgReferenceId() {
    return orgReferenceId;
  }

  /**
   * @return
   * @hibernate.property column="PROJECT_CD" length="10"
   */
  public String getProjectCode() {
    return projectCode;
  }

  /**
   * @return
   * @hibernate.property column="SUB_ACCT_NBR" length="5"
   */
  public String getSubAccountNbr() {
    return subAccountNbr;
  }

  /**
   * @param string
   */
  public void setAccountNbr(String string) {
    accountNbr = string;
  }

  /**
   * @param string
   */
  public void setAccountNetAmount(BigDecimal bigdecimal) {
    accountNetAmount = bigdecimal;
  }

  /**
   * @param integer
   */
  public void setPaymentDetail(PaymentDetail pd) {
    paymentDetail = pd;
  }

  /**
   * @param string
   */
  public void setFinChartCode(String string) {
    finChartCode = string;
  }

  /**
   * @param string
   */
  public void setFinObjectCode(String string) {
    finObjectCode = string;
  }

  /**
   * @param string
   */
  public void setFinSubObjectCode(String string) {
    finSubObjectCode = string;
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
  public void setOrgReferenceId(String string) {
    orgReferenceId = string;
  }

  /**
   * @param string
   */
  public void setProjectCode(String string) {
    projectCode = string;
  }

  /**
   * @param string
   */
  public void setSubAccountNbr(String string) {
    subAccountNbr = string;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentAccountDetail) ) { return false; }
    PaymentAccountDetail o = (PaymentAccountDetail)obj;
    return new EqualsBuilder()
    .append(id, o.getId())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(59,67)
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
