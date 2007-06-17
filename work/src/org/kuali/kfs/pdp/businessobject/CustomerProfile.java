/*
 * Created on Jul 5, 2004
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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;

/**
 * @author jsissom
 * @hibernate.class table="PDP.PDP_CUST_PRFL_T"
 */
public class CustomerProfile implements UserRequired,Serializable,PersistenceBrokerAware {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfile.class);
  
  // TODO Fix hard code
  public static String EPIC_ORG_CODE = "FMOP";
  public static String EPIC_SUB_UNIT_CODE = "EPIC";

  private String achPaymentDescription; // ACH_PMT_DESC
  private String additionalCheckNoteTextLine1;
  private String additionalCheckNoteTextLine2;
  private String additionalCheckNoteTextLine3;
  private String additionalCheckNoteTextLine4;
  private String address1; // CUST_LN1_ADDR
  private String address2; // CUST_LN2_ADDR
  private String address3; // CUST_LN3_ADDR
  private String address4; // CUST_LN4_ADDR
  private Boolean adviceCreate; // ADV_CRTE_IND
  private String adviceHeaderText; // ADV_HDR_TXT
  private String adviceSubjectLine;
  private String adviceReturnEmailAddr;
  private String chartCode; // FIN_COA_CD
  private String checkHeaderNoteTextLine1;
  private String checkHeaderNoteTextLine2;
  private String checkHeaderNoteTextLine3;
  private String checkHeaderNoteTextLine4;
  private String city; // CUST_CTY_NM
  private String contactFullName; // CUST_CNTC_FULL_NM
  private String countryName; // CUST_CNTRY_NM
  private Boolean customerActive; // CUST_ACTV_IND
  private String customerDescription; // CUST_DESC
  private String defaultChartCode; // DFLT_COA_CD
  private String defaultAccountNumber; // DFLT_ACCT_NBR
  private String defaultSubAccountNumber; // DFLT_SUB_ACCT_NBR
  private String defaultObjectCode; // DFLT_OBJ_CD
  private String defaultPhysicalCampusProcessingCode; // DFLT_PHYS_CMP_PROC_CD
  private String defaultSubObjectCode; // DFLT_SUB_OBJ_CD
  private Boolean employeeCheck; // CUST_EMP_CHK_IND
  private BigDecimal fileThresholdAmount; // FL_THRSHLD_AMT
  private String fileThresholdEmailAddress; // CUST_FILE_THRSHLD_EMAIL_ADDR
  private Integer id; // CUST_ID
  private Timestamp lastUpdate; // LST_UPDT_TS
  private PdpUser lastUpdateUser;
  private String lastUpdateUserId; // LST_UPDT_USR_ID
  private Boolean nraReview; // CUST_NRA_RVW_IND
  private Integer version; // VER_NBR
  private String orgCode; // ORG_CD
  private Boolean ownershipCodeRequired; // CUST_OWNR_CD_REQ_IND
  private Boolean payeeIdRequired; // CUST_PAYEE_ID_REQ_IND
  private BigDecimal paymentThresholdAmount; // PMT_THRSHLD_AMT
  private String paymentThresholdEmailAddress; // CUST_PMT_THRSHLD_EMAIL_ADDR
  private String processingEmailAddr; // CUST_PRCS_EMAIL_ADDR
  private String psdTransactionCode; // PSD_TRN_CD
  private String state; // CUST_ST_CD
  private String subUnitCode; // SBUNT_CD
  private String zipCode; // CUST_ZIP_CD
  private Boolean accountingEditRequired; // ACCTG_EDIT_REQ_IND
  private Boolean relieveLiabilities;
  private List customerBanks;

  public CustomerProfile() {
    super();
    customerBanks = new ArrayList();
  }

  public String getSortName() {
    return (this.chartCode + this.orgCode + this.subUnitCode);
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
   * @hibernate.property column="ACCTG_EDIT_REQ_IND" type="yes_no" not-null="true"
   * @return Returns the accountingEditRequird.
   */
  public Boolean getAccountingEditRequired() {
    return accountingEditRequired;
  }
 
  /**
   * @param accountingEditRequird The accountingEditRequird to set.
   */
  public void setAccountingEditRequired(Boolean accountingEditRequird) {
    this.accountingEditRequired = accountingEditRequird;
  }
  
  /**
   * @hibernate.property column="DFLT_SUB_ACCT_NBR" length="5" not-null="true"
   * @return Returns the defaultSubAccountNumber.
   */
  public String getDefaultSubAccountNumber() {
    return defaultSubAccountNumber;
  }

  /**
   * @param defaultSubAccountNumber The defaultSubAccountNumber to set.
   */
  public void setDefaultSubAccountNumber(String defaultSubAccountNumber) {
    this.defaultSubAccountNumber = defaultSubAccountNumber;
  }

  public List getCustomerBanks() {
    return customerBanks;
  }

  public CustomerBank getCustomerBankByDisbursementType(String dt) {
    for (Iterator iter = customerBanks.iterator(); iter.hasNext();) {
      CustomerBank element = (CustomerBank)iter.next();
      if ( element.getDisbursementType().getCode().equals(dt)) {
        return element;
      }
    }
    return null;
  }

  public void setCustomerBanks(List cbs) {
    customerBanks = cbs;
  }

  public void addCustomerBank(CustomerBank cb) {
    customerBanks.add(cb);
  }

  public void deleteCustomerBank(CustomerBank cb) {
    customerBanks.remove(cb);
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof CustomerProfile) ) { return false; }
    CustomerProfile tc = (CustomerProfile)obj;
    return new EqualsBuilder()
    .append(chartCode, tc.getChartCode())
    .append(orgCode, tc.getOrgCode())
    .append(subUnitCode, tc.getSubUnitCode())    
    .isEquals();
  }

  /**
   * @hibernate.property column="ACH_PMT_DESC" length="100" not-null="false"
   * @return Returns the achPaymentDescription.
   */
  public String getAchPaymentDescription() {
    return achPaymentDescription;
  }

  /**
   * @hibernate.property column="ADDL_CHK_NTE_TXT_LN1" length="90" not-null="false"
   * @return Returns the additionalCheckNoteTextLine1.
   */
  public String getAdditionalCheckNoteTextLine1() {
    return additionalCheckNoteTextLine1;
  }

  /**
   * @hibernate.property column="ADDL_CHK_NTE_TXT_LN2" length="90" not-null="false"
   * @return Returns the additionalCheckNoteTextLine2.
   */
  public String getAdditionalCheckNoteTextLine2() {
    return additionalCheckNoteTextLine2;
  }

  /**
   * @hibernate.property column="ADDL_CHK_NTE_LN3_TXT" length="90" not-null="false"
   * @return Returns the additionalCheckNoteTextLine3.
   */
  public String getAdditionalCheckNoteTextLine3() {
    return additionalCheckNoteTextLine3;
  }

  /**
   * @hibernate.property column="ADDL_CHK_NTE_TXT_LN4" length="90" not-null="false"
   * @return Returns the additionalCheckNoteTextLine4.
   */
  public String getAdditionalCheckNoteTextLine4() {
    return additionalCheckNoteTextLine4;
  }

  /**
   * @hibernate.property column="CUST_LN1_ADDR" length="55" not-null="false"
   * @return Returns the address1.
   */
  public String getAddress1() {
    return address1;
  }
  
  /**
   * @hibernate.property column="CUST_LN2_ADDR" length="55" not-null="false"
   * @return Returns the address2.
   */
  public String getAddress2() {
    return address2;
  }

  /**
   * @hibernate.property column="CUST_LN3_ADDR" length="55" not-null="false"
   * @return Returns the address3.
   */
  public String getAddress3() {
    return address3;
  }

  /**
   * @hibernate.property column="CUST_LN4_ADDR" length="55" not-null="false"
   * @return Returns the address4.
   */
  public String getAddress4() {
    return address4;
  }

  /**
   * @hibernate.property column="ADV_HDR_TXT" length="200" not-null="false"
   * @return Returns the adviceHeaderText.
   */
  public String getAdviceHeaderText() {
    return adviceHeaderText;
  }

  /**
   * @hibernate.property column="FIN_COA_CD" length="2" not-null="true"
   * @return Returns the chartCode.
   */
  public String getChartCode() {
    return chartCode;
  }

  /**
   * @hibernate.property column="CHK_HDR_NTE_TXT_LN1" length="90" not-null="false"
   * @return Returns the checkHeaderNoteTextLine1.
   */
  public String getCheckHeaderNoteTextLine1() {
    return checkHeaderNoteTextLine1;
  }
  /**
   * @hibernate.property column="CHK_HDR_NTE_TXT_LN2" length="90" not-null="false"
   * @return Returns the checkHeaderNoteTextLine2.
   */
  public String getCheckHeaderNoteTextLine2() {
    return checkHeaderNoteTextLine2;
  }
  /**
   * @hibernate.property column="CHK_HDR_NTE_LN3_TXT" length="90" not-null="false"
   * @return Returns the checkHeaderNoteTextLine3.
   */
  public String getCheckHeaderNoteTextLine3() {
    return checkHeaderNoteTextLine3;
  }
  /**
   * @hibernate.property column="CHK_HDR_NTE_TXT_LN4" length="90" not-null="false"
   * @return Returns the checkHeaderNoteTextLine4.
   */
  public String getCheckHeaderNoteTextLine4() {
    return checkHeaderNoteTextLine4;
  }
  /**
   * @hibernate.property column="CUST_CTY_NM" length="30" not-null="false"
   * @return Returns the city.
   */
  public String getCity() {
    return city;
  }
  /**
   * @hibernate.property column="CUST_CNTC_FULL_NM" length="50" not-null="false"
   * @return Returns the contactFullName.
   */
  public String getContactFullName() {
    return contactFullName;
  }
  /**
   * @hibernate.property column="CUST_CNTRY_NM" length="25" not-null="false"
   * @return Returns the countryName.
   */
  public String getCountryName() {
    return countryName;
  }
  /**
   * @hibernate.property column="CUST_DESC" length="50" not-null="false"
   * @return Returns the customerDescription.
   */
  public String getCustomerDescription() {
    return customerDescription;
  }
  /**
   * @hibernate.property column="DFLT_ACCT_NBR" length="7" not-null="true"
   * @return Returns the defaultAccountNumber.
   */
  public String getDefaultAccountNumber() {
    return defaultAccountNumber;
  }
  /**
   * @hibernate.property column="DFLT_COA_CD" length="2" not-null="true"
   * @return Returns the defaultChartCode.
   */
  public String getDefaultChartCode() {
    return defaultChartCode;
  }
  /**
   * @hibernate.property column="DFLT_OBJ_CD" length="4" not-null="true"
   * @return Returns the defaultObjectCode.
   */
  public String getDefaultObjectCode() {
    return defaultObjectCode;
  }
  /**
   * @hibernate.property column="DFLT_PHYS_CMP_PROC_CD" length="2" not-null="false"
   * @return Returns the defaultPhysicalCampusProcessingCode.
   */
  public String getDefaultPhysicalCampusProcessingCode() {
    return defaultPhysicalCampusProcessingCode;
  }
  /**
   * @hibernate.property column="DFLT_SUB_OBJ_CD" length="3" not-null="true"
   * @return Returns the defaultSubObjectCode.
   */
  public String getDefaultSubObjectCode() {
    return defaultSubObjectCode;
  }
  /**
   * @hibernate.property column="FL_THRSHLD_AMT" not-null="false"
   * @return Returns the fileThresholdAmount.
   */
  public BigDecimal getFileThresholdAmount() {
    return fileThresholdAmount;
  }
  /**
   * @hibernate.property column="CUST_FILE_THRSHLD_EMAIL_ADDR" length="50" not-null="false"
   * @return Returns the fileThresholdEmailAddress.
   */
  public String getFileThresholdEmailAddress() {
    return fileThresholdEmailAddress;
  }
  /**
   * @return Returns the id.
   * @hibernate.id column="CUST_ID" generator-class="sequence"
   * @hibernate.generator-param name="sequence" value="PDP.PDP_CUST_ID_SEQ" 
   */
  public Integer getId() {
    return id;
  }
  /**
   * @hibernate.property column="LST_UPDT_TS" not-null="true"
   * @return Returns the lastUpdate.
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
  }
  /**
   * @hibernate.version column="VER_NBR"
   * @return Returns the ojbVerNbr.
   */
  public Integer getVersion() {
    return version;
  }
  /**
   * @hibernate.property column="ORG_CD" length="4" not-null="true"
   * @return Returns the orgCode.
   */
  public String getOrgCode() {
    return orgCode;
  }
  /**
   * @hibernate.property column="PMT_THRSHLD_AMT" not-null="false"
   * @return Returns the paymentThresholdAmount.
   */
  public BigDecimal getPaymentThresholdAmount() {
    return paymentThresholdAmount;
  }
  /**
   * @hibernate.property column="CUST_PMT_THRSHLD_EMAIL_ADDR" length="50" not-null="false"
   * @return Returns the paymentThresholdEmailAddress.
   */
  public String getPaymentThresholdEmailAddress() {
    return paymentThresholdEmailAddress;
  }
  /**
   * @hibernate.property column="CUST_PRCS_EMAIL_ADDR" length="50" not-null="false"
   * @return Returns the processingEmailAddr.
   */
  public String getProcessingEmailAddr() {
    return processingEmailAddr;
  }
  public String getFirstFiftyProcessingEmailAddr() {
    if ( (processingEmailAddr != null) && (processingEmailAddr.length() > 50) ) {
      return processingEmailAddr.substring(0, 50);
    }
    return processingEmailAddr;
  }
  /**
   * @hibernate.property column="PSD_TRN_CD" length="4" not-null="false"
   * @return Returns the psdTransactionCode.
   */
  public String getPsdTransactionCode() {
    return psdTransactionCode;
  }
  /**
   * @hibernate.property column="CUST_ST_CD" length="30" not-null="false"
   * @return Returns the state.
   */
  public String getState() {
    return state;
  }
  /**
   * @hibernate.property column="SBUNT_CD" length="4" not-null="true"
   * @return Returns the subUnitCode.
   */
  public String getSubUnitCode() {
    return subUnitCode;
  }
  /**
   * @hibernate.property column="CUST_ZIP_CD" length="20" not-null="false"
   * @return Returns the zipCode.
   */
  public String getZipCode() {
    return zipCode;
  }
  
  public int hashCode() {
    return new HashCodeBuilder(59,67)
      .append(chartCode)
      .append(orgCode)
      .append(subUnitCode)
      .toHashCode();
  }

  /**
   * @hibernate.property column="ADV_CRTE_IND" type="yes_no" not-null="false"
   * @return Returns the adviceCreate.
   */
  public Boolean getAdviceCreate() {
    return adviceCreate;
  }

  /**
   * @hibernate.property column="ADV_SUBJ_LN_TXT" length="40" not-null="false"
   * @return Returns the adviceSubjectLine.
   */
  public String getAdviceSubjectLine() {
    return adviceSubjectLine;
  }

  /**
   * @hibernate.property column="ADV_RTRN_EMAIL_ADDR" length="50" not-null="false"
   * @return Returns the adviceReturnEmailAddr.
   */
  public String getAdviceReturnEmailAddr() {
    return adviceReturnEmailAddr;
  }

  /**
   * @hibernate.property column="CUST_ACTV_IND" type="yes_no" not-null="false"
   * @return Returns the customerActive.
   */
  public Boolean getCustomerActive() {
    return customerActive;
  }

  /**
   * @hibernate.property column="CUST_EMP_CHK_IND" type="yes_no" not-null="false"
   * @return Returns the employeeCheck.
   */
  public Boolean getEmployeeCheck() {
    return employeeCheck;
  }
  /**
   * @hibernate.property column="CUST_NRA_RVW_IND" type="yes_no" not-null="false"
   * @return Returns the nraReview.
   */
  public Boolean getNraReview() {
    return nraReview;
  }
  /**
   * @hibernate.property column="CUST_OWNR_CD_REQ_IND" type="yes_no" not-null="false"
   * @return Returns the ownershipCodeRequired.
   */
  public Boolean getOwnershipCodeRequired() {
    return ownershipCodeRequired;
  }
  /**
   * @hibernate.property column="CUST_PAYEE_ID_REQ_IND" type="yes_no" not-null="false"
   * @return Returns the payeeIdRequired.
   */
  public Boolean getPayeeIdRequired() {
    return payeeIdRequired;
  }

  /**
   * @param achPaymentDescription The achPaymentDescription to set.
   */
  public void setAchPaymentDescription(String achPaymentDescription) {
    this.achPaymentDescription = achPaymentDescription;
  }

  /**
   * @param additionalCheckNoteTextLine1 The additionalCheckNoteTextLine1 to set.
   */
  public void setAdditionalCheckNoteTextLine1(String additionalCheckNoteTextLine1) {
    this.additionalCheckNoteTextLine1 = additionalCheckNoteTextLine1;
  }

  /**
   * @param additionalCheckNoteTextLine2 The additionalCheckNoteTextLine2 to set.
   */
  public void setAdditionalCheckNoteTextLine2(String additionalCheckNoteTextLine2) {
    this.additionalCheckNoteTextLine2 = additionalCheckNoteTextLine2;
  }

  /**
   * @param additionalCheckNoteTextLine3 The additionalCheckNoteTexLine3 to set.
   */
  public void setAdditionalCheckNoteTextLine3(String additionalCheckNoteTextLine3) {
    this.additionalCheckNoteTextLine3 = additionalCheckNoteTextLine3;
  }

  /**
   * @param additionalCheckNoteTextLine4 The additionalCheckNoteTextLine4 to set.
   */
  public void setAdditionalCheckNoteTextLine4(String additionalCheckNoteTextLine4) {
    this.additionalCheckNoteTextLine4 = additionalCheckNoteTextLine4;
  }

  /**
   * @param address1 The address1 to set.
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  /**
   * @param address2 The address2 to set.
   */
  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  /**
   * @param address3 The address3 to set.
   */
  public void setAddress3(String address3) {
    this.address3 = address3;
  }

  /**
   * @param address4 The address4 to set.
   */
  public void setAddress4(String address4) {
    this.address4 = address4;
  }

  /**
   * @param adviceCreate The adviceCreate to set.
   */
  public void setAdviceCreate(Boolean adviceCreate) {
    this.adviceCreate = adviceCreate;
  }

  /**
   * @param adviceHeaderText The adviceHeaderText to set.
   */
  public void setAdviceHeaderText(String adviceHeaderText) {
    this.adviceHeaderText = adviceHeaderText;
  }

  /**
   * @param adviceSubjectLine The adviceSubjectLine to set.
   */
  public void setAdviceSubjectLine(String adviceSubjectLine) {
    this.adviceSubjectLine = adviceSubjectLine;
  }

  /**
   * @param adviceReturnEmailAddr The adviceReturnEmailAddr to set.
   */
  public void setAdviceReturnEmailAddr(String adviceReturnEmailAddr) {
    this.adviceReturnEmailAddr = adviceReturnEmailAddr;
  }

  /**
   * @param chartCode The chartCode to set.
   */
  public void setChartCode(String chartCode) {
    this.chartCode = chartCode;
  }
  
  /**
   * @param checkHeaderNoteTextLine1 The checkHeaderNoteTextLine1 to set.
   */
  public void setCheckHeaderNoteTextLine1(String checkHeaderNoteTextLine1) {
    this.checkHeaderNoteTextLine1 = checkHeaderNoteTextLine1;
  }
  /**
   * @param checkHeaderNoteTextLine2 The checkHeaderNoteTextLine2 to set.
   */
  public void setCheckHeaderNoteTextLine2(String checkHeaderNoteTextLine2) {
    this.checkHeaderNoteTextLine2 = checkHeaderNoteTextLine2;
  }
  /**
   * @param checkHeaderNoteTextLine3 The checkHeaderNoteTextLine3 to set.
   */
  public void setCheckHeaderNoteTextLine3(String checkHeaderNoteTextLine3) {
    this.checkHeaderNoteTextLine3 = checkHeaderNoteTextLine3;
  }
  /**
   * @param checkHeaderNoteTextLine4 The checkHeaderNoteTextLine4 to set.
   */
  public void setCheckHeaderNoteTextLine4(String checkHeaderNoteTextLine4) {
    this.checkHeaderNoteTextLine4 = checkHeaderNoteTextLine4;
  }
  /**
   * @param city The city to set.
   */
  public void setCity(String city) {
    this.city = city;
  }
  /**
   * @param contactFullName The contactFullName to set.
   */
  public void setContactFullName(String contactFullName) {
    this.contactFullName = contactFullName;
  }
  /**
   * @param countryName The countryName to set.
   */
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }
  /**
   * @param customerActive The customerActive to set.
   */
  public void setCustomerActive(Boolean customerActive) {
    this.customerActive = customerActive;
  }
  /**
   * @param customerDescription The customerDescription to set.
   */
  public void setCustomerDescription(String customerDescription) {
    this.customerDescription = customerDescription;
  }
  /**
   * @param defaultAccountNumber The defaultAccountNumber to set.
   */
  public void setDefaultAccountNumber(String defaultAccountNumber) {
    this.defaultAccountNumber = defaultAccountNumber;
  }
  /**
   * @param defaultChartCode The defaultChartCode to set.
   */
  public void setDefaultChartCode(String defaultChartCode) {
    this.defaultChartCode = defaultChartCode;
  }
  /**
   * @param defaultObjectCode The defaultObjectCode to set.
   */
  public void setDefaultObjectCode(String defaultObjectCode) {
    this.defaultObjectCode = defaultObjectCode;
  }
  /**
   * @param defaultPhysicalCampusProcessingCode The defaultPhysicalCampusProcessingCode to set.
   */
  public void setDefaultPhysicalCampusProcessingCode(String defaultPhysicalCampusProcessingCode) {
    this.defaultPhysicalCampusProcessingCode = defaultPhysicalCampusProcessingCode;
  }
  /**
   * @param defaultSubObjectCode The defaultSubObjectCode to set.
   */
  public void setDefaultSubObjectCode(String defaultSubObjectCode) {
    this.defaultSubObjectCode = defaultSubObjectCode;
  }
  /**
   * @param employeeCheck The employeeCheck to set.
   */
  public void setEmployeeCheck(Boolean employeeCheck) {
    this.employeeCheck = employeeCheck;
  }
  /**
   * @param fileThresholdAmount The fileThresholdAmount to set.
   */
  public void setFileThresholdAmount(BigDecimal fileThresholdAmount) {
    this.fileThresholdAmount = fileThresholdAmount;
  }
  /**
   * @param fileThresholdEmailAddress The fileThresholdEmailAddress to set.
   */
  public void setFileThresholdEmailAddress(String fileThresholdEmailAddress) {
    this.fileThresholdEmailAddress = fileThresholdEmailAddress;
  }
  /**
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  /**
   * @param lastUpdate The lastUpdate to set.
   */
  public void setLastUpdate(Timestamp lastUpdate) {
    this.lastUpdate = lastUpdate;
  }
  /**
   * @param nraReview The nraReview to set.
   */
  public void setNraReview(Boolean nraReview) {
    this.nraReview = nraReview;
  }
  /**
   * @param ojbVerNbr The ojbVerNbr to set.
   */
  public void setVersion(Integer ver) {
    this.version = ver;
  }
  /**
   * @param orgCode The orgCode to set.
   */
  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }
  /**
   * @param ownershipCodeRequired The ownershipCodeRequired to set.
   */
  public void setOwnershipCodeRequired(Boolean ownershipCodeRequired) {
    this.ownershipCodeRequired = ownershipCodeRequired;
  }
  /**
   * @param payeeIdRequired The payeeIdRequired to set.
   */
  public void setPayeeIdRequired(Boolean payeeIdRequired) {
    this.payeeIdRequired = payeeIdRequired;
  }
  /**
   * @param paymentThresholdAmount The paymentThresholdAmount to set.
   */
  public void setPaymentThresholdAmount(BigDecimal paymentThresholdAmount) {
    this.paymentThresholdAmount = paymentThresholdAmount;
  }
  /**
   * @param paymentThresholdEmailAddress The paymentThresholdEmailAddress to set.
   */
  public void setPaymentThresholdEmailAddress(String paymentThresholdEmailAddress) {
    this.paymentThresholdEmailAddress = paymentThresholdEmailAddress;
  }
  /**
   * @param processingEmailAddr The processingEmailAddr to set.
   */
  public void setProcessingEmailAddr(String processingEmailAddr) {
    this.processingEmailAddr = processingEmailAddr;
  }
  /**
   * @param psdTransactionCode The psdTransactionCode to set.
   */
  public void setPsdTransactionCode(String psdTransactionCode) {
    this.psdTransactionCode = psdTransactionCode;
  }
  /**
   * @param state The state to set.
   */
  public void setState(String state) {
    this.state = state;
  }
  /**
   * @param subUnitCode The subUnitCode to set.
   */
  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
  }
  /**
   * @param zipCode The zipCode to set.
   */
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  /**
   * @return Returns the relieveLiabilities.
   */
  public Boolean getRelieveLiabilities() {
    return relieveLiabilities;
  }
  /**
   * @param relieveLiabilities The relieveLiabilities to set.
   */
  public void setRelieveLiabilities(Boolean relieveLiabilities) {
    this.relieveLiabilities = relieveLiabilities;
  }

  public String toString() {
    return new ToStringBuilder(this)
      .append("chartCode",  this.chartCode)
      .append("orgCode", this.orgCode)
      .append("subUnitCode", this.subUnitCode)
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
