/*
 * Created on Sep 22, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
 * @hibernate.class  table="PDP.PDP_PMT_GRP_T"
 */

public class PaymentGroup implements Serializable,PersistenceBrokerAware {
  private static BigDecimal zero = new BigDecimal(0);

  private Integer id;                       // PMT_GRP_ID
  private String payeeName;                 // PMT_PAYEE_NM        
  private String payeeId;                   // PAYEE_ID         
  private String payeeIdTypeCd;             // PAYEE_ID_TYP_CD         
  private String alternatePayeeId;          // ALTRNT_PAYEE_ID     
  private String alternatePayeeIdTypeCd;    // ALTRNT_PAYEE_ID_TYP_CD         
  private String payeeOwnerCd;              // PAYEE_OWNR_CD     
  private String customerIuNbr;             // CUST_IU_NBR        
  private String line1Address;              // PMT_LN1_ADDR         
  private String line2Address;              // PMT_LN2_ADDR        
  private String line3Address;              // PMT_LN3_ADDR        
  private String line4Address;              // PMT_LN4_ADDR
  private String city;                      // PMT_CTY_NM               
  private String state;                     // PMT_ST_NM               
  private String country;                   // PMT_CNTRY_NM
  private String zipCd;                     // PMT_ZIP_CD        
  private Boolean campusAddress;            // CMP_ADDR_IND        
  private Timestamp paymentDate;            // PMT_DT DATE         
  private Boolean pymtAttachment;           // PMT_ATTCHMNT_IND     
  private Boolean pymtSpecialHandling;      // PMT_SPCL_HANDLG_IND          
  private Boolean taxablePayment;           // PMT_TXBL_IND       
  private Boolean nraPayment;               // NRA_PMT_IND
  private Boolean processImmediate;         // PROC_IMD_IND
  private Boolean combineGroups;            // PMT_GRP_CMB_IND
  private String achBankRoutingNbr;         // ACH_BNK_RTNG_NBR        
  private String adviceEmailAddress;        // ADV_EMAIL_ADDR      
  private Boolean iuEmployee;               // EMP_IND
  private String creditMemoNbr;             // PMT_CRDT_MEMO_NBR       
  private BigDecimal creditMemoAmount;      // PMT_CRDT_MEMO_AMT
  private Integer disbursementNbr;          // DISB_NBR      
  private Timestamp disbursementDate;       // DISB_TS      
  private String physCampusProcessCd;       // PHYS_CMP_PROC_CD       
  private String sortValue;                 // PMT_SORT_ORD_VAL
  private String achAccountType;            // CUST_ACCT_TYP_CD
  private Timestamp epicPaymentExtractedDate; // PDP_EPIC_PMT_CNCL_EXTRT_TS
  private Timestamp lastUpdate;             // LST_UPDT_TS
  private Integer version;                  // VER_NBR
  
  private Integer batchId;
  private Batch batch;                      // PMT_BATCH_ID

  private Integer processId;
  private PaymentProcess process;                  // PROC_ID

  private String paymentStatusCode;
  private PaymentStatus paymentStatus;      // PMT_STAT_CD

  private String disbursementTypeCode;
  private DisbursementType disbursementType;  // DISB_TYP_CD

  private Integer bankId;
  private Bank bank;                        // BNK_ID

  private AchAccountNumber achAccountNumber;
  
  private List paymentGroupHistory = new ArrayList();

  private List paymentDetails = new ArrayList();

  public PaymentGroup() {
    super();
  }

  /**
   * WIDTH MUST BE LESS THAN THE # OF SPACES
   * 
   * @param width
   * @param val
   * @return
   */
  private String getWidthString(int width,String val) {
    return (val + "                                        ").substring(0,width-1);
  }

  public String getSortGroupId() {
    if ( getProcessImmediate().booleanValue() ) {
      return "B";
    } else if ( getPymtSpecialHandling().booleanValue() ) {
      return "C";
    } else if ( getPymtAttachment().booleanValue() ) {
      return "D";
    } else {
      return "E";
    }
  }

  private boolean booleanValue(Boolean b) {
    boolean bv = false;
    if ( b != null ) {
      bv = b.booleanValue();
    }
    return bv;
  }

  /**
   * This field determines the sort order for the format.  It packs all the
   * relevant data into one field to make a field that order by can use
   * 
   * @return sort order string
   */
  public String getFormatSortField() {
    StringBuffer sb = new StringBuffer();

    sb.append(getSortGroupId());

    CustomerProfile cp = this.getBatch().getCustomerProfile();
    sb.append(cp.getChartCode());
    sb.append(getWidthString(4,cp.getOrgCode()));
    sb.append(getWidthString(4,cp.getSubUnitCode()));

    if ( "E".equals(getSortGroupId()) ) {
      sb.append(this.getPayeeId());
      sb.append(this.getPayeeIdTypeCd());
    } else {
      sb.append(this.getPayeeName());
    }

    return sb.toString();
  }

  public int getNoteLines() {
    int count = 0;
    for (Iterator iter = this.getPaymentDetails().iterator(); iter.hasNext();) {
      PaymentDetail element = (PaymentDetail)iter.next();
      count++;  // Add a line for the invoice #
      count = count + element.getNotes().size();
    }
    return count;
  }

  /**
   * Get the total of all the detail items
   * @return
   */
  public BigDecimal getNetPaymentAmount() {
    BigDecimal amt = new BigDecimal(0);
    for (Iterator iter = this.getPaymentDetails().iterator(); iter.hasNext();) {
      PaymentDetail element = (PaymentDetail)iter.next();
      amt = amt.add(element.getNetPaymentAmount());
    }
    return amt;
  }

  /**
   * @hibernate.set name="paymentDetail"
   * @hibernate.collection-key column="pmt_grp_id"
   * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentDetail"
   */
  public List getPaymentDetails() {
    return paymentDetails;
  }
  
  public void setPaymentDetails(List paymentDetail) {
    this.paymentDetails = paymentDetail;
  }
  
  public void addPaymentDetails(PaymentDetail pgh) {
    pgh.setPaymentGroup(this);
    paymentDetails.add(pgh);
  }

  public void deletePaymentDetails(PaymentDetail pgh) {
    paymentDetails.remove(pgh);
  }

  /**
   * @hibernate.set name="paymentGroupHistory"
   * @hibernate.collection-key column="pmt_grp_id"
   * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentGroupHistory"
   */
  public List getPaymentGroupHistory() {
    return paymentGroupHistory;
  }
  
  public void setPaymentGroupHistory(List paymentGroupHistory) {
    this.paymentGroupHistory = paymentGroupHistory;
  }
  
  public void addPaymentGroupHistory(PaymentGroupHistory pd) {
    pd.setPaymentGroup(this);
    paymentGroupHistory.add(pd);
  }

  public void deletePaymentGroupHistory(PaymentGroupHistory pd) {
    paymentGroupHistory.remove(pd);
  }

  /**
   * @hibernate.id column="PMT_GRP_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_GRP_ID_SEQ"
   * @return
   */
  public Integer getId() {
    return id;
  }

  /**
   * @hibernate.one-to-one class="edu.iu.uis.pdp.bo.AchAccountNumber"
   * @return
   */
  public AchAccountNumber getAchAccountNumber() {
    return achAccountNumber;
  }

  public void setAchAccountNumber(AchAccountNumber aan) {
    this.achAccountNumber = aan;
  }

  public String getSortValue() {
    return sortValue;
  }

  public void setSortValue(String sortValue) {
    this.sortValue = sortValue;
  }
  
  /**
   * @hibernate.property column="PMT_CTY_NM" length="30"
   * @return Returns the city.
   */
  public String getCity() {
    return city;
  }

  /**
   * @param city The city to set.
   */
  public void setCity(String city) {
    this.city = city;
  }

  public Boolean getCombineGroups() {
    return combineGroups;
  }

  public void setCombineGroups(Boolean combineGroups) {
    this.combineGroups = combineGroups;
  }

  /**
   * @hibernate.property column="PMT_CNTRY_NM" length="30"
   * @return Returns the country.
   */
  public String getCountry() {
    return country;
  }

  /**
   * @param country The country to set.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * @hibernate.property column="PMT_ST_NM" length="30"
   * @return Returns the state.
   */
  public String getState() {
    return state;
  }

  /**
   * @param state The state to set.
   */
  public void setState(String state) {
    this.state = state;
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
   * @hibernate.property column="ACH_BNK_RTNG_NBR" length="9"
   */
  public String getAchBankRoutingNbr() {
    return achBankRoutingNbr;
  }

  /**
   * @return
   * @hibernate.property column="ADV_EMAIL_ADDR" length="50"
   */
  public String getAdviceEmailAddress() {
    return adviceEmailAddress;
  }

  /**
   * @return
   * @hibernate.property column="ALTRNT_PAYEE_ID" length="25" 
   */
  public String getAlternatePayeeId() {
    return alternatePayeeId;
  }

  /**
   * @return
   * @hibernate.property column="ALTRNT_PAYEE_ID_TYP_CD" length="2"
   */
  public String getAlternatePayeeIdTypeCd() {
    return alternatePayeeIdTypeCd;
  }

  /**
   * @return
   * @hibernate.many-to-one column="BNK_ID" class="edu.iu.uis.pdp.bo.Bank" not-null="false"
   */
  public Bank getBank() {
    return bank;
  }

  /**
   * @return
   * @hibernate.many-to-one column="PMT_BATCH_ID" class="edu.iu.uis.pdp.bo.Batch" not-null="true"
   */
  public Batch getBatch() {
    return batch;
  }

  /**
   * @return
   * @hibernate.property column="CMP_ADDR_IND" type="yes_no"
   */
  public Boolean getCampusAddress() {
    return campusAddress;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CRDT_MEMO_AMT" length="14"
   */
  public BigDecimal getCreditMemoAmount() {
    return creditMemoAmount;
  }

  /**
   * @return
   * @hibernate.property column="PMT_CRDT_MEMO_NBR" length="14"
   */
  public String getCreditMemoNbr() {
    return creditMemoNbr;
  }

  /**
   * @return
   * @hibernate.property column="CUST_IU_NBR" length="30"
   */
  public String getCustomerIuNbr() {
    return customerIuNbr;
  }

  /**
   * @return
   * @hibernate.property column="DISB_TS" length="7"
   */
  public Timestamp getDisbursementDate() {
    return disbursementDate;
  }

  /**
   * @return
   * @hibernate.property column="DISB_NBR" length="9"
   */
  public Integer getDisbursementNbr() {
    return disbursementNbr;
  }

  /**
   * @return
   * @hibernate.many-to-one column="DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType" not-null="false"
   */
  public DisbursementType getDisbursementType() {
    return disbursementType;
  }

  /**
   * @return
   * @hibernate.property column="EMP_IND" type="yes_no"
   */
  public Boolean getIuEmployee() {
    return iuEmployee;
  }

  /**
   * @return
   * @hibernate.property column="PMT_LN1_ADDR" length="45"
   */
  public String getLine1Address() {
    return line1Address;
  }

  /**
   * @return
   * @hibernate.property column="PMT_LN2_ADDR" length="45"
   */
  public String getLine2Address() {
    return line2Address;
  }

  /**
   * @return
   * @hibernate.property column="PMT_LN3_ADDR" length="45"
   */
  public String getLine3Address() {
    return line3Address;
  }

  /**
   * @return
   * @hibernate.property column="PMT_LN4_ADDR" length="45"
   */
  public String getLine4Address() {
    return line4Address;
  }

  /**
   * @return
   * @hibernate.property column="NRA_PMT_IND" type="yes_no"
   */
  public Boolean getNraPayment() {
    return nraPayment;
  }

  /**
   * @return
   * @hibernate.property column="PAYEE_ID" length="25"
   */
  public String getPayeeId() {
    return payeeId;
  }

  /**
   * @return
   * @hibernate.property column="PAYEE_ID_TYP_CD" length="1"
   */
  public String getPayeeIdTypeCd() {
    return payeeIdTypeCd;
  }

  /**
   * @return
   * @hibernate.property column="PMT_PAYEE_NM" length="40"
   */
  public String getPayeeName() {
    return payeeName;
  }

  /**
   * @return
   * @hibernate.property column="PAYEE_OWNR_CD" length="2"
   */
  public String getPayeeOwnerCd() {
    return payeeOwnerCd;
  }

  /**
   * @return
   * @hibernate.property column="PMT_DT"
   */
  public Timestamp getPaymentDate() {
    return paymentDate;
  }

  /**
   * @return
   * @hibernate.many-to-one column="PMT_STAT_CD" class="edu.iu.uis.pdp.bo.PaymentStatus" not-null="true"
   */
  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  /**
   * @return
   * @hibernate.property column="PHYS_CMP_PROC_CD" length="2"
   */
  public String getPhysCampusProcessCd() {
    return physCampusProcessCd;
  }

  /**
   * @return
   * @hibernate.many-to-one column="PROC_ID" class="edu.iu.uis.pdp.bo.PaymentProcess" not-null="false"
   */
  public PaymentProcess getProcess() {
    return process;
  }

  /**
   * @return
   * @hibernate.property column="PROC_IMD_IND" type="yes_no" length="1"
   */
  public Boolean getProcessImmediate() {
    return processImmediate;
  }

  /**
   * @return
   * @hibernate.property column="PMT_ATTCHMNT_IND" type="yes_no" length="1"
   */
  public Boolean getPymtAttachment() {
    return pymtAttachment;
  }

  /**
   * @return
   * @hibernate.property column="PMT_SPCL_HANDLG_IND" type="yes_no" length="1"
   */
  public Boolean getPymtSpecialHandling() {
    return pymtSpecialHandling;
  }

  /**
   * @return
   * @hibernate.property column="PMT_TXBL_IND" type="yes_no" length="1"
   */
  public Boolean getTaxablePayment() {
    return taxablePayment;
  }

  /**
   * @return
   * @hibernate.property column="PMT_ZIP_CD" length="2"
   */
  public String getZipCd() {
    return zipCd;
  }

  /**
   * @param integer
   */
  public void setAchBankRoutingNbr(String s) {
    achBankRoutingNbr = s;
  }

  /**
   * @param string
   */
  public void setAdviceEmailAddress(String string) {
    adviceEmailAddress = string;
  }

  /**
   * @param string
   */
  public void setAlternatePayeeId(String string) {
    alternatePayeeId = string;
  }

  /**
   * @param string
   */
  public void setAlternatePayeeIdTypeCd(String string) {
    alternatePayeeIdTypeCd = string;
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
  public void setBatch(Batch b) {
    batch = b;
  }

  /**
   * @param boolean1
   */
  public void setCampusAddress(Boolean boolean1) {
    campusAddress = boolean1;
  }

  /**
   * @param decimal
   */
  public void setCreditMemoAmount(BigDecimal decimal) {
    creditMemoAmount = decimal;
  }

  /**
   * @param string
   */
  public void setCreditMemoNbr(String string) {
    creditMemoNbr = string;
  }

  /**
   * @param string
   */
  public void setCustomerIuNbr(String string) {
    customerIuNbr = string;
  }

  /**
   * @param timestamp
   */
  public void setDisbursementDate(Timestamp timestamp) {
    disbursementDate = timestamp;
  }

  /**
   * @param integer
   */
  public void setDisbursementNbr(Integer integer) {
    disbursementNbr = integer;
  }

  /**
   * @param string
   */
  public void setDisbursementType(DisbursementType dt) {
    disbursementType = dt;
  }

  /**
   * @param integer
   */
  public void setId(Integer integer) {
    id = integer;
  }

  /**
   * @param boolean1
   */
  public void setIuEmployee(Boolean boolean1) {
    iuEmployee = boolean1;
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
  public void setLine1Address(String string) {
    line1Address = string;
  }

  /**
   * @param string
   */
  public void setLine2Address(String string) {
    line2Address = string;
  }

  /**
   * @param string
   */
  public void setLine3Address(String string) {
    line3Address = string;
  }

  /**
   * @param string
   */
  public void setLine4Address(String string) {
    line4Address = string;
  }

  /**
   * @param boolean1
   */
  public void setNraPayment(Boolean boolean1) {
    nraPayment = boolean1;
  }

  /**
   * @param string
   */
  public void setPayeeId(String string) {
    payeeId = string;
  }

  /**
   * @param string
   */
  public void setPayeeIdTypeCd(String string) {
    payeeIdTypeCd = string;
  }

  /**
   * @param string
   */
  public void setPayeeName(String string) {
    payeeName = string;
  }

  /**
   * @param string
   */
  public void setPayeeOwnerCd(String string) {
    payeeOwnerCd = string;
  }

  /**
   * @param timestamp
   */
  public void setPaymentDate(Timestamp timestamp) {
    paymentDate = timestamp;
  }

  /**
   * @param string
   */
  public void setPaymentStatus(PaymentStatus stat) {
    paymentStatus = stat;
  }

  /**
   * @param string
   */
  public void setPhysCampusProcessCd(String string) {
    physCampusProcessCd = string;
  }

  /**
   * @param integer
   */
  public void setProcess(PaymentProcess p) {
    if ( p != null ) {
      processId = p.getId();
    } else {
      processId = null;
    }
    this.process = p;
  }

  /**
   * @param boolean1
   */
  public void setProcessImmediate(Boolean boolean1) {
    processImmediate = boolean1;
  }

  /**
   * @param boolean1
   */
  public void setPymtAttachment(Boolean boolean1) {
    pymtAttachment = boolean1;
  }

  /**
   * @param boolean1
   */
  public void setTaxablePayment(Boolean boolean1) {
    taxablePayment = boolean1;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  /**
   * @param string
   */
  public void setZipCd(String string) {
    zipCd = string;
  }
  
  /**
   * @param string
   */
  public void setPymtSpecialHandling(Boolean pymtSpecialHandling) {
    this.pymtSpecialHandling = pymtSpecialHandling;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentGroup) ) { return false; }
    PaymentGroup o = (PaymentGroup)obj;
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
  /**
   * @return Returns the achAccountType.
   */
  public String getAchAccountType() {
    return achAccountType;
  }
  /**
   * @param achAccountType The achAccountType to set.
   */
  public void setAchAccountType(String achAccountType) {
    this.achAccountType = achAccountType;
  }
  /**
   * @return Returns the epicPaymentExtractedDate.
   */
  public Timestamp getEpicPaymentExtractedDate() {
    return epicPaymentExtractedDate;
  }
  /**
   * @param epicPaymentExtractedDate The epicPaymentExtractedDate to set.
   */
  public void setEpicPaymentExtractedDate(Timestamp epicPaymentExtractedDate) {
    this.epicPaymentExtractedDate = epicPaymentExtractedDate;
  }
}
