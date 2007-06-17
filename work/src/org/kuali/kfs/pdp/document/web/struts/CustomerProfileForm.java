/*
 * Created on Jul 16, 2004
 *
 */
package org.kuali.module.pdp.form.customerprofile;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 * 
 */
public class CustomerProfileForm extends ActionForm {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileForm.class);

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
  private String defaultAccountNumber; // DFLT_ACCT_NBR
  private String defaultChartCode; // DFLT_COA_CD
  private String defaultObjectCode; // DFLT_OBJ_CD
  private String defaultPhysicalCampusProcessingCode; // DFLT_PHYS_CMP_PROC_CD
  private String defaultSubObjectCode; // DFLT_SUB_OBJ_CD
  private Boolean employeeCheck; // CUST_EMP_CHK_IND
  private String fileThresholdAmount; // FL_THRSHLD_AMT
  private String fileThresholdEmailAddress; // CUST_FILE_THRSHLD_EMAIL_ADDR
  private String id; // CUST_ID
  private Timestamp lastUpdate; // LST_UPDT_TS
  private PdpUser lastUpdateUser;
  private String lastUpdateUserId; // LST_UPDT_USR_ID
  private Boolean nraReview; // CUST_NRA_RVW_IND
  private String version; // VER_NBR
  private String orgCode; // ORG_CD
  private Boolean ownershipCodeRequired; // CUST_OWNR_CD_REQ_IND
  private Boolean payeeIdRequired; // CUST_PAYEE_ID_REQ_IND
  private String paymentThresholdAmount; // PMT_THRSHLD_AMT
  private String paymentThresholdEmailAddress; // CUST_PMT_THRSHLD_EMAIL_ADDR
  private String processingEmailAddr; // CUST_PRCS_EMAIL_ADDR
  private String psdTransactionCode; // PSD_TRN_CD
  private String state; // CUST_ST_CD
  private String subUnitCode; // SBUNT_CD
  private String zipCode; // CUST_ZIP_CD
  private Boolean accountingEditRequired; // ACCTG_EDIT_REQ_IND
  private String defaultSubAccountNumber;
  private Boolean relieveLiabilities;
  private List customerBanks;
  private CustomerBankForm[] customerBankForms;

  public CustomerProfileForm() {
    customerBankForms = new CustomerBankForm[4];
    for (int i = 0; i < customerBankForms.length; i++) {
      customerBankForms[i] = new CustomerBankForm();
    }
  }

  /**
   * @return Returns the customerBankForms.
   */
  public CustomerBankForm[] getCustomerBankForms() {
    return customerBankForms;
  }
  /**
   * @param customerBankForms
   *          The customerBankForms to set.
   */
  public void setCustomerBankForms(CustomerBankForm[] forms) {
    this.customerBankForms = forms;
  }

  public CustomerBankForm getCustomerBankForms(int index) {
    return customerBankForms[index];
  }

  public void setCustomerBankForms(int index,CustomerBankForm c) {
    customerBankForms[index] = c;
  }

  public void addCustomerBankRow() {
    CustomerBankForm[] holdBanks = this.getCustomerBankForms();
    this.setCustomerBankForms(new CustomerBankForm[holdBanks.length + 1]);
    for (int i = 0; i < holdBanks.length; i++) {
      customerBankForms[i] = holdBanks[i];
    }
    customerBankForms[customerBankForms.length - 1] = new CustomerBankForm();
  }

  public void setCustomerBankFormArraySize(int i) {
    customerBankForms = new CustomerBankForm[i];
  }

  public CustomerProfileForm(String coa, String org, String sbuntcd) {
    this.setChartCode(coa);
    this.setOrgCode(org);
    this.setSubUnitCode(sbuntcd);
  }

  /**
   * @return Returns the customerBanks.
   */
  public List getCustomerBanks() {

    return customerBanks;
  }
  /**
   * @param customerBanks
   *          The customerBanks to set.
   */
  public void setCustomerBanks(List customerBanks) {

    this.customerBanks = customerBanks;
  }

  /**
   * @return
   */
  public void setForm(CustomerProfile cp) {

    this.setAchPaymentDescription(cp.getAchPaymentDescription());
    this.setAdditionalCheckNoteTextLine1(cp.getAdditionalCheckNoteTextLine1());
    this.setAdditionalCheckNoteTextLine2(cp.getAdditionalCheckNoteTextLine2());
    this.setAdditionalCheckNoteTextLine3(cp.getAdditionalCheckNoteTextLine3());
    this.setAdditionalCheckNoteTextLine4(cp.getAdditionalCheckNoteTextLine4());
    this.setAddress1(cp.getAddress1());
    this.setAddress2(cp.getAddress2());
    this.setAddress3(cp.getAddress3());
    this.setAddress4(cp.getAddress4());
    this.setAdviceCreate(cp.getAdviceCreate());
    this.setAdviceHeaderText(cp.getAdviceHeaderText());
    this.setAdviceReturnEmailAddr(cp.getAdviceReturnEmailAddr());
    this.setAdviceSubjectLine(cp.getAdviceSubjectLine());
    this.setChartCode(cp.getChartCode().toUpperCase());
    this.setCheckHeaderNoteTextLine1(cp.getCheckHeaderNoteTextLine1());
    this.setCheckHeaderNoteTextLine2(cp.getCheckHeaderNoteTextLine2());
    this.setCheckHeaderNoteTextLine3(cp.getCheckHeaderNoteTextLine3());
    this.setCheckHeaderNoteTextLine4(cp.getCheckHeaderNoteTextLine4());
    this.setCity(cp.getCity());
    this.setContactFullName(cp.getContactFullName());
    this.setCountryName(cp.getCountryName());
    this.setCustomerActive(cp.getCustomerActive());
    this.setCustomerDescription(cp.getCustomerDescription());
    this.setDefaultAccountNumber(cp.getDefaultAccountNumber());
    this.setDefaultChartCode(cp.getDefaultChartCode());
    this.setDefaultObjectCode(cp.getDefaultObjectCode());
    this.setDefaultPhysicalCampusProcessingCode(cp.getDefaultPhysicalCampusProcessingCode().toUpperCase());
    this.setDefaultSubObjectCode(cp.getDefaultSubObjectCode());
    this.setEmployeeCheck(cp.getEmployeeCheck());
    this.setFileThresholdAmount(GeneralUtilities.convertBigDecimalToString(cp.getFileThresholdAmount()));
    this.setFileThresholdEmailAddress(cp.getFileThresholdEmailAddress());
    this.setLastUpdate(cp.getLastUpdate());
    this.setLastUpdateUser(cp.getLastUpdateUser());
    this.setLastUpdateUserId(cp.getLastUpdateUserId());
    this.setNraReview(cp.getNraReview());
    this.setOrgCode(cp.getOrgCode().toUpperCase());
    this.setOwnershipCodeRequired(cp.getOwnershipCodeRequired());
    this.setPayeeIdRequired(cp.getPayeeIdRequired());
    this.setPaymentThresholdAmount(GeneralUtilities.convertBigDecimalToString(cp.getPaymentThresholdAmount()));
    this.setPaymentThresholdEmailAddress(cp.getPaymentThresholdEmailAddress());
    this.setProcessingEmailAddr(cp.getProcessingEmailAddr());
    this.setPsdTransactionCode(cp.getPsdTransactionCode());
    this.setState(cp.getState());
    this.setSubUnitCode(cp.getSubUnitCode().toUpperCase());
    this.setVersion(GeneralUtilities.convertIntegerToString(cp.getVersion()));
    this.setZipCode(cp.getZipCode());
    this.setId(GeneralUtilities.convertIntegerToString(cp.getId()));
    this.setAccountingEditRequired(cp.getAccountingEditRequired());
    this.setDefaultSubAccountNumber(cp.getDefaultSubAccountNumber());
    this.setRelieveLiabilities(cp.getRelieveLiabilities());

  }

  /**
   * @return
   */
  public CustomerProfile getCustomerProfile() {
    CustomerProfile cp = new CustomerProfile();
    cp.setAchPaymentDescription(this.getAchPaymentDescription());
    cp.setAdditionalCheckNoteTextLine1(this.getAdditionalCheckNoteTextLine1());
    cp.setAdditionalCheckNoteTextLine2(this.getAdditionalCheckNoteTextLine2());
    cp.setAdditionalCheckNoteTextLine3(this.getAdditionalCheckNoteTextLine3());
    cp.setAdditionalCheckNoteTextLine4(this.getAdditionalCheckNoteTextLine4());
    cp.setAddress1(this.getAddress1());
    cp.setAddress2(this.getAddress2());
    cp.setAddress3(this.getAddress3());
    cp.setAddress4(this.getAddress4());
    cp.setAdviceCreate(this.getAdviceCreate());
    cp.setAdviceHeaderText(this.getAdviceHeaderText());
    cp.setAdviceReturnEmailAddr(this.getAdviceReturnEmailAddr());
    cp.setAdviceSubjectLine(this.getAdviceSubjectLine());
    cp.setChartCode(this.getChartCode().toUpperCase());
    cp.setCheckHeaderNoteTextLine1(this.getCheckHeaderNoteTextLine1());
    cp.setCheckHeaderNoteTextLine2(this.getCheckHeaderNoteTextLine2());
    cp.setCheckHeaderNoteTextLine3(this.getCheckHeaderNoteTextLine3());
    cp.setCheckHeaderNoteTextLine4(this.getCheckHeaderNoteTextLine4());
    cp.setCity(this.getCity());
    cp.setContactFullName(this.getContactFullName());
    cp.setCountryName(this.getCountryName());
    cp.setCustomerActive(this.getCustomerActive());
    cp.setCustomerDescription(this.getCustomerDescription());
    cp.setDefaultAccountNumber(this.getDefaultAccountNumber());
    cp.setDefaultChartCode(this.getDefaultChartCode().toUpperCase());
    cp.setDefaultObjectCode(this.getDefaultObjectCode());
    cp.setDefaultPhysicalCampusProcessingCode(this.getDefaultPhysicalCampusProcessingCode().toUpperCase());
    cp.setEmployeeCheck(this.getEmployeeCheck());
    cp.setFileThresholdAmount(GeneralUtilities.convertStringToBigDecimal(this.getFileThresholdAmount()));
    cp.setFileThresholdEmailAddress(this.getFileThresholdEmailAddress());
    cp.setLastUpdate(this.getLastUpdate());
    cp.setLastUpdateUser(this.getLastUpdateUser());
    cp.setNraReview(this.getNraReview());
    cp.setOrgCode(this.getOrgCode().toUpperCase());
    cp.setOwnershipCodeRequired(this.getOwnershipCodeRequired());
    cp.setPayeeIdRequired(this.getPayeeIdRequired());
    cp.setPaymentThresholdAmount(GeneralUtilities.convertStringToBigDecimal(this.getPaymentThresholdAmount()));
    cp.setPaymentThresholdEmailAddress(this.getPaymentThresholdEmailAddress());
    cp.setProcessingEmailAddr(this.getProcessingEmailAddr());
    cp.setPsdTransactionCode(this.getPsdTransactionCode());
    cp.setState(this.getState());
    cp.setSubUnitCode(this.getSubUnitCode().toUpperCase());
    cp.setVersion(GeneralUtilities.convertStringToInteger(this.getVersion()));
    cp.setZipCode(this.getZipCode());
    cp.setId(GeneralUtilities.convertStringToInteger(this.getId()));
    cp.setAccountingEditRequired(this.getAccountingEditRequired());
    cp.setRelieveLiabilities(this.getRelieveLiabilities());
    
    if (this.getDefaultSubAccountNumber().equals(null)) {
      cp.setDefaultSubAccountNumber("-----");
    } else {
      cp.setDefaultSubAccountNumber(this.getDefaultSubAccountNumber());  
    }
    
    if (this.getDefaultSubObjectCode().equals(null)) {
      cp.setDefaultSubObjectCode("-----");
    } else {
      cp.setDefaultSubObjectCode(this.getDefaultSubObjectCode());  
    }

    return cp;
  }

  public void clearForm() {
    this.setAchPaymentDescription("");
    this.setAdditionalCheckNoteTextLine1("");
    this.setAdditionalCheckNoteTextLine2("");
    this.setAdditionalCheckNoteTextLine3("");
    this.setAdditionalCheckNoteTextLine4("");
    this.setAddress1("");
    this.setAddress2("");
    this.setAddress3("");
    this.setAddress4("");
    this.setAdviceCreate(null);
    this.setAdviceHeaderText("");
    this.setAdviceReturnEmailAddr("");
    this.setAdviceSubjectLine("");
    this.setChartCode("");
    this.setCheckHeaderNoteTextLine1("");
    this.setCheckHeaderNoteTextLine2("");
    this.setCheckHeaderNoteTextLine3("");
    this.setCheckHeaderNoteTextLine4("");
    this.setCity("");
    this.setContactFullName("");
    this.setCountryName("");
    this.setCustomerActive(null);
    this.setCustomerDescription("");
    this.setDefaultAccountNumber("");
    this.setDefaultChartCode("");
    this.setDefaultObjectCode("");
    this.setDefaultPhysicalCampusProcessingCode("");
    this.setDefaultSubObjectCode("");
    this.setEmployeeCheck(null);
    this.setFileThresholdAmount(null);
    this.setFileThresholdEmailAddress("");
    this.setNraReview(null);
    this.setOrgCode("");
    this.setOwnershipCodeRequired(null);
    this.setPayeeIdRequired(null);
    this.setPaymentThresholdAmount(null);
    this.setPaymentThresholdEmailAddress("");
    this.setProcessingEmailAddr("");
    this.setPsdTransactionCode("");
    this.setState("");
    this.setSubUnitCode("");
    this.setZipCode("");
    this.setAccountingEditRequired(null);
    this.setDefaultSubAccountNumber("");
    this.setRelieveLiabilities(null);
  }

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    LOG.debug("Entered validate().");
    //create instance of ActionErrors to send errors to user
    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

    if (buttonPressed.equals("btnSave")) {
      // Validate Chart as being not null
      if (GeneralUtilities.isStringEmpty(this.chartCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.chartCode.null"));
      } else {
        if (!(this.chartCode.length() == 2)) {
          actionErrors.add("errors", new ActionMessage("customerProfileForm.chartCode.length"));
        }
      }

      // Validate Org as being not null and contains 4 characters
      if (GeneralUtilities.isStringEmpty(this.orgCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.orgCode.null"));
      } else {
        if (!(this.orgCode.length() == 4)) {
          actionErrors.add("errors", new ActionMessage("customerProfileForm.orgCode.length"));
        }
      }

      // Validate Sub-Unit as being not null
      if (GeneralUtilities.isStringEmpty(this.subUnitCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.subUnitCode.null"));
      }
      
      // Validate Process Location as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultPhysicalCampusProcessingCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.campusProcessingCode.null"));
      } else {
        if (!(this.defaultPhysicalCampusProcessingCode.length() == 2)) {
          actionErrors.add("errors", new ActionMessage("customerProfileForm.campusProcessingCode.length"));
        }
      }
      
      // Validate Default Chart Code as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultChartCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.defaultChartCode.null"));
      }
      
      // Validate Default Account Number as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultAccountNumber)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.defaultAccountNumber.null"));
      }
      
      // Validate Default Sub-Account Number as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultSubAccountNumber)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.defaultSubAccountNumber.null"));
      }
      
      // Validate Default Object Code as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultObjectCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.defaultObjectCode.null"));
      }
      
      // Validate Default Sub Object Code as being not null
      if (GeneralUtilities.isStringEmpty(this.defaultSubObjectCode)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.defaultSubObjectCode.null"));
      }
      // Validate Description as being not null
      if (GeneralUtilities.isStringEmpty(this.customerDescription)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.customerDesc.null"));
      }

      // Validate Primary Contact Name as being not null
      if (GeneralUtilities.isStringEmpty(this.contactFullName)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.contactFullName.null"));
      }

      // Validate Process E-mail Address as being not null
      if (GeneralUtilities.isStringEmpty(this.processingEmailAddr)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.processingEmailAddr.null"));
      }

      // Validate Address Line 1 as being not null
      if (GeneralUtilities.isStringEmpty(this.address1)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.address1.null"));
      }

      // Validate City as being not null
      if (GeneralUtilities.isStringEmpty(this.city)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.city.null"));
      }

      // Validate State as being not null
      if (GeneralUtilities.isStringEmpty(this.state)) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.state.null"));
      }

      // Validate Amounts as convertable to BigDecimals
      if (!GeneralUtilities.isStringAllNumbersOrASingleCharacter(this.fileThresholdAmount, '.')) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.fileThresholdAmount.invalid"));
      }
      if (!GeneralUtilities.isStringAllNumbersOrASingleCharacter(this.paymentThresholdAmount, '.')) {
        actionErrors.add("errors", new ActionMessage("customerProfileForm.paymentThresholdAmount.invalid"));
      }
    }
    LOG.debug("Exiting validate()  There were " + actionErrors.size() + " ActionMessages found.");
    return actionErrors;
  }

  /**
   * @return Returns the achPaymentDescription.
   */
  public String getAchPaymentDescription() {
    return achPaymentDescription;
  }
  /**
   * @return Returns the additionalCheckNoteTextLine1.
   */
  public String getAdditionalCheckNoteTextLine1() {
    return additionalCheckNoteTextLine1;
  }
  /**
   * @return Returns the additionalCheckNoteTextLine2.
   */
  public String getAdditionalCheckNoteTextLine2() {
    return additionalCheckNoteTextLine2;
  }
  /**
   * @return Returns the additionalCheckNoteTextLine3.
   */
  public String getAdditionalCheckNoteTextLine3() {
    return additionalCheckNoteTextLine3;
  }
  /**
   * @return Returns the additionalCheckNoteTextLine4.
   */
  public String getAdditionalCheckNoteTextLine4() {
    return additionalCheckNoteTextLine4;
  }
  /**
   * @return Returns the address1.
   */
  public String getAddress1() {
    return address1;
  }
  /**
   * @return Returns the address2.
   */
  public String getAddress2() {
    return address2;
  }
  /**
   * @return Returns the address3.
   */
  public String getAddress3() {
    return address3;
  }
  /**
   * @return Returns the address4.
   */
  public String getAddress4() {
    return address4;
  }
  /**
   * @return Returns the adviceCreate.
   */
  public Boolean getAdviceCreate() {
    return adviceCreate;
  }
  /**
   * @return Returns the adviceHeaderText.
   */
  public String getAdviceHeaderText() {
    return adviceHeaderText;
  }
  /**
   * @return Returns the chartCode.
   */
  public String getChartCode() {
    return chartCode;
  }
  /**
   * @return Returns the checkHeaderNoteTextLine1.
   */
  public String getCheckHeaderNoteTextLine1() {
    return checkHeaderNoteTextLine1;
  }
  /**
   * @return Returns the checkHeaderNoteTextLine2.
   */
  public String getCheckHeaderNoteTextLine2() {
    return checkHeaderNoteTextLine2;
  }
  /**
   * @return Returns the checkHeaderNoteTextLine3.
   */
  public String getCheckHeaderNoteTextLine3() {
    return checkHeaderNoteTextLine3;
  }
  /**
   * @return Returns the checkHeaderNoteTextLine4.
   */
  public String getCheckHeaderNoteTextLine4() {
    return checkHeaderNoteTextLine4;
  }
  /**
   * @return Returns the city.
   */
  public String getCity() {
    return city;
  }
  /**
   * @return Returns the contactFullName.
   */
  public String getContactFullName() {
    return contactFullName;
  }
  /**
   * @return Returns the countryName.
   */
  public String getCountryName() {
    return countryName;
  }
  /**
   * @return Returns the customerActive.
   */
  public Boolean getCustomerActive() {
    return customerActive;
  }
  /**
   * @return Returns the customerDescription.
   */
  public String getCustomerDescription() {
    return customerDescription;
  }
  /**
   * @return Returns the defaultAccountNumber.
   */
  public String getDefaultAccountNumber() {
    return defaultAccountNumber;
  }
  /**
   * @return Returns the defaultChartCode.
   */
  public String getDefaultChartCode() {
    return defaultChartCode;
  }
  /**
   * @return Returns the defaultObjectCode.
   */
  public String getDefaultObjectCode() {
    return defaultObjectCode;
  }
  /**
   * @return Returns the defaultPhysicalCampusProcessingCode.
   */
  public String getDefaultPhysicalCampusProcessingCode() {
    return defaultPhysicalCampusProcessingCode;
  }
  /**
   * @return Returns the defaultSubObjectCode.
   */
  public String getDefaultSubObjectCode() {
    return defaultSubObjectCode;
  }
  /**
   * @return Returns the employeeCheck.
   */
  public Boolean getEmployeeCheck() {
    return employeeCheck;
  }
  /**
   * @return Returns the fileThresholdAmount.
   */
  public String getFileThresholdAmount() {
    return fileThresholdAmount;
  }
  /**
   * @return Returns the fileThresholdEmailAddress.
   */
  public String getFileThresholdEmailAddress() {
    return fileThresholdEmailAddress;
  }
  /**
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }
  /**
   * @return Returns the lastUpdate.
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
  }
  /**
   * @return Returns the lastUpdateUser.
   */
  public PdpUser getLastUpdateUser() {
    return lastUpdateUser;
  }
  /**
   * @return Returns the lastUpdateUserId.
   */
  public String getLastUpdateUserId() {
    return lastUpdateUserId;
  }
  /**
   * @return Returns the nraReview.
   */
  public Boolean getNraReview() {
    return nraReview;
  }
  /**
   * @return Returns the orgCode.
   */
  public String getOrgCode() {
    return orgCode;
  }
  /**
   * @return Returns the ownershipCodeRequired.
   */
  public Boolean getOwnershipCodeRequired() {
    return ownershipCodeRequired;
  }
  /**
   * @return Returns the payeeIdRequired.
   */
  public Boolean getPayeeIdRequired() {
    return payeeIdRequired;
  }
  /**
   * @return Returns the paymentThresholdAmount.
   */
  public String getPaymentThresholdAmount() {
    return paymentThresholdAmount;
  }
  /**
   * @return Returns the paymentThresholdEmailAddress.
   */
  public String getPaymentThresholdEmailAddress() {
    return paymentThresholdEmailAddress;
  }
  /**
   * @return Returns the processingEmailAddr.
   */
  public String getProcessingEmailAddr() {
    return processingEmailAddr;
  }
  /**
   * @return Returns the psdTransactionCode.
   */
  public String getPsdTransactionCode() {
    return psdTransactionCode;
  }
  /**
   * @return Returns the state.
   */
  public String getState() {
    return state;
  }
  /**
   * @return Returns the subUnitCode.
   */
  public String getSubUnitCode() {
    return subUnitCode;
  }
  /**
   * @return Returns the version.
   */
  public String getVersion() {
    return version;
  }
  /**
   * @return Returns the zipCode.
   */
  public String getZipCode() {
    return zipCode;
  }
  /**
   * @param achPaymentDescription
   *          The achPaymentDescription to set.
   */
  public void setAchPaymentDescription(String achPaymentDescription) {
    this.achPaymentDescription = achPaymentDescription;
  }
  /**
   * @param additionalCheckNoteTextLine1
   *          The additionalCheckNoteText to set.
   */
  public void setAdditionalCheckNoteTextLine1(String additionalCheckNoteTextLine1) {
    this.additionalCheckNoteTextLine1 = additionalCheckNoteTextLine1;
  }
  /**
   * @param additionalCheckNoteTextLine2
   *          The additionalCheckNoteText to set.
   */
  public void setAdditionalCheckNoteTextLine2(String additionalCheckNoteTextLine2) {
    this.additionalCheckNoteTextLine2 = additionalCheckNoteTextLine2;
  }
  /**
   * @param additionalCheckNoteTextLine3
   *          The additionalCheckNoteText to set.
   */
  public void setAdditionalCheckNoteTextLine3(String additionalCheckNoteTextLine3) {
    this.additionalCheckNoteTextLine3 = additionalCheckNoteTextLine3;
  }
  /**
   * @param additionalCheckNoteTextLine4
   *          The additionalCheckNoteText to set.
   */
  public void setAdditionalCheckNoteTextLine4(String additionalCheckNoteTextLine4) {
    this.additionalCheckNoteTextLine4 = additionalCheckNoteTextLine4;
  }
  /**
   * @param address1
   *          The address1 to set.
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }
  /**
   * @param address2
   *          The address2 to set.
   */
  public void setAddress2(String address2) {
    this.address2 = address2;
  }
  /**
   * @param address3
   *          The address3 to set.
   */
  public void setAddress3(String address3) {
    this.address3 = address3;
  }
  /**
   * @param address4
   *          The address4 to set.
   */
  public void setAddress4(String address4) {
    this.address4 = address4;
  }
  /**
   * @param adviceCreate
   *          The adviceCreate to set.
   */
  public void setAdviceCreate(Boolean adviceCreate) {
    this.adviceCreate = adviceCreate;
  }
  /**
   * @param adviceHeaderText
   *          The adviceHeaderText to set.
   */
  public void setAdviceHeaderText(String adviceHeaderText) {
    this.adviceHeaderText = adviceHeaderText;
  }
  /**
   * @param chartCode
   *          The chartCode to set.
   */
  public void setChartCode(String chartCode) {
    this.chartCode = chartCode;
  }
  /**
   * @param checkHeaderNoteTextLine1
   *          The checkHeaderNoteText to set.
   */
  public void setCheckHeaderNoteTextLine1(String checkHeaderNoteTextLine1) {
    this.checkHeaderNoteTextLine1 = checkHeaderNoteTextLine1;
  }
  /**
   * @param checkHeaderNoteTextLine2
   *          The checkHeaderNoteText to set.
   */
  public void setCheckHeaderNoteTextLine2(String checkHeaderNoteTextLine2) {
    this.checkHeaderNoteTextLine2 = checkHeaderNoteTextLine2;
  }
  /**
   * @param checkHeaderNoteTextLine3
   *          The checkHeaderNoteText to set.
   */
  public void setCheckHeaderNoteTextLine3(String checkHeaderNoteTextLine3) {
    this.checkHeaderNoteTextLine3 = checkHeaderNoteTextLine3;
  }
  /**
   * @param checkHeaderNoteTextLine4
   *          The checkHeaderNoteText to set.
   */
  public void setCheckHeaderNoteTextLine4(String checkHeaderNoteTextLine4) {
    this.checkHeaderNoteTextLine4 = checkHeaderNoteTextLine4;
  }
  /**
   * @param city
   *          The city to set.
   */
  public void setCity(String city) {
    this.city = city;
  }
  /**
   * @param contactFullName
   *          The contactFullName to set.
   */
  public void setContactFullName(String contactFullName) {
    this.contactFullName = contactFullName;
  }
  /**
   * @param countryName
   *          The countryName to set.
   */
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }
  /**
   * @param customerActive
   *          The customerActive to set.
   */
  public void setCustomerActive(Boolean customerActive) {
    this.customerActive = customerActive;
  }
  /**
   * @param customerDescription
   *          The customerDescription to set.
   */
  public void setCustomerDescription(String customerDescription) {
    this.customerDescription = customerDescription;
  }
  /**
   * @param defaultAccountNumber
   *          The defaultAccountNumber to set.
   */
  public void setDefaultAccountNumber(String defaultAccountNumber) {
    this.defaultAccountNumber = defaultAccountNumber;
  }
  /**
   * @param defaultChartCode
   *          The defaultChartCode to set.
   */
  public void setDefaultChartCode(String defaultChartCode) {
    this.defaultChartCode = defaultChartCode;
  }
  /**
   * @param defaultObjectCode
   *          The defaultObjectCode to set.
   */
  public void setDefaultObjectCode(String defaultObjectCode) {
    this.defaultObjectCode = defaultObjectCode;
  }
  /**
   * @param defaultPhysicalCampusProcessingCode
   *          The defaultPhysicalCampusProcessingCode to set.
   */
  public void setDefaultPhysicalCampusProcessingCode(String defaultPhysicalCampusProcessingCode) {
    this.defaultPhysicalCampusProcessingCode = defaultPhysicalCampusProcessingCode;
  }
  /**
   * @param defaultSubObjectCode
   *          The defaultSubObjectCode to set.
   */
  public void setDefaultSubObjectCode(String defaultSubObjectCode) {
    this.defaultSubObjectCode = defaultSubObjectCode;
  }
  /**
   * @param employeeCheck
   *          The employeeCheck to set.
   */
  public void setEmployeeCheck(Boolean employeeCheck) {
    this.employeeCheck = employeeCheck;
  }
  /**
   * @param fileThresholdAmount
   *          The fileThresholdAmount to set.
   */
  public void setFileThresholdAmount(String fileThresholdAmount) {
    this.fileThresholdAmount = fileThresholdAmount;
  }
  /**
   * @param fileThresholdEmailAddress
   *          The fileThresholdEmailAddress to set.
   */
  public void setFileThresholdEmailAddress(String fileThresholdEmailAddress) {
    this.fileThresholdEmailAddress = fileThresholdEmailAddress;
  }
  /**
   * @param id
   *          The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }
  /**
   * @param lastUpdate
   *          The lastUpdate to set.
   */
  public void setLastUpdate(Timestamp lastUpdate) {
    this.lastUpdate = lastUpdate;
  }
  /**
   * @param lastUpdateUser
   *          The lastUpdateUser to set.
   */
  public void setLastUpdateUser(PdpUser lastUpdateUser) {
    this.lastUpdateUser = lastUpdateUser;
  }
  /**
   * @param lastUpdateUserId
   *          The lastUpdateUserId to set.
   */
  public void setLastUpdateUserId(String lastUpdateUserId) {
    this.lastUpdateUserId = lastUpdateUserId;
  }
  /**
   * @param nraReview
   *          The nraReview to set.
   */
  public void setNraReview(Boolean nraReview) {
    this.nraReview = nraReview;
  }
  /**
   * @param orgCode
   *          The orgCode to set.
   */
  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }
  /**
   * @param ownershipCodeRequired
   *          The ownershipCodeRequired to set.
   */
  public void setOwnershipCodeRequired(Boolean ownershipCodeRequired) {
    this.ownershipCodeRequired = ownershipCodeRequired;
  }
  /**
   * @param payeeIdRequired
   *          The payeeIdRequired to set.
   */
  public void setPayeeIdRequired(Boolean payeeIdRequired) {
    this.payeeIdRequired = payeeIdRequired;
  }
  /**
   * @param paymentThresholdAmount
   *          The paymentThresholdAmount to set.
   */
  public void setPaymentThresholdAmount(String paymentThresholdAmount) {
    this.paymentThresholdAmount = paymentThresholdAmount;
  }
  /**
   * @param paymentThresholdEmailAddress
   *          The paymentThresholdEmailAddress to set.
   */
  public void setPaymentThresholdEmailAddress(String paymentThresholdEmailAddress) {
    this.paymentThresholdEmailAddress = paymentThresholdEmailAddress;
  }
  /**
   * @param processingEmailAddr
   *          The processingEmailAddr to set.
   */
  public void setProcessingEmailAddr(String processingEmailAddr) {
    this.processingEmailAddr = processingEmailAddr;
  }
  /**
   * @param psdTransactionCode
   *          The psdTransactionCode to set.
   */
  public void setPsdTransactionCode(String psdTransactionCode) {
    this.psdTransactionCode = psdTransactionCode;
  }
  /**
   * @param state
   *          The state to set.
   */
  public void setState(String state) {
    this.state = state;
  }
  /**
   * @param subUnitCode
   *          The subUnitCode to set.
   */
  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
  }
  /**
   * @param version
   *          The version to set.
   */
  public void setVersion(String version) {
    this.version = version;
  }
  /**
   * @param zipCode
   *          The zipCode to set.
   */
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }
  /**
   * @return Returns the accountingEditRequired.
   */
  public Boolean getAccountingEditRequired() {
    return accountingEditRequired;
  }
  /**
   * @param accountingEditRequired The accountingEditRequired to set.
   */
  public void setAccountingEditRequired(Boolean accountingEditRequired) {
    this.accountingEditRequired = accountingEditRequired;
  }
  /**
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
  /**
   * @return Returns the adviceReturnEmailAddr.
   */
  public String getAdviceReturnEmailAddr() {
    return adviceReturnEmailAddr;
  }
/**
 * @return Returns the adviceSubjectLine.
 */
public String getAdviceSubjectLine() {
  return adviceSubjectLine;
}
  /**
   * @param adviceReturnEmailAddr The adviceReturnEmailAddr to set.
   */
  public void setAdviceReturnEmailAddr(String adviceReturnEmailAddr) {
    this.adviceReturnEmailAddr = adviceReturnEmailAddr;
  }
/**
 * @param adviceSubjectLine The adviceSubjectLine to set.
 */
public void setAdviceSubjectLine(String adviceSubjectLine) {
  this.adviceSubjectLine = adviceSubjectLine;
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
}