/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.module.pdp.service;

/**
 * @author jsissom
 *
 */
public class AchInformation {
  private String idType;
  private String payeeId;
  private String departmentCode;  
  private String achBankRoutingNbr;
  private String achBankAccountNbr;
  private String achAccountType;
  private String adviceEmailAddress;      

  public AchInformation() {
    super();
  }

  public String getAchBankAccountNbr() {
    return achBankAccountNbr;
  }

  public void setAchBankAccountNbr(String achBankAccountNbr) {
    this.achBankAccountNbr = achBankAccountNbr;
  }

  public String getAchBankRoutingNbr() {
    return achBankRoutingNbr;
  }

  public void setAchBankRoutingNbr(String achBankRoutingNbr) {
    this.achBankRoutingNbr = achBankRoutingNbr;
  }

  public String getAdviceEmailAddress() {
    return adviceEmailAddress;
  }

  public void setAdviceEmailAddress(String adviceEmailAddress) {
    this.adviceEmailAddress = adviceEmailAddress;
  }

  public String getDepartmentCode() {
    return departmentCode;
  }

  public void setDepartmentCode(String departmentCode) {
    this.departmentCode = departmentCode;
  }

  public String getIdType() {
    return idType;
  }

  public void setIdType(String idType) {
    this.idType = idType;
  }

  public String getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(String payeeId) {
    this.payeeId = payeeId;
  }
  
  public String getAchAccountType() {
    return achAccountType;
  }
  
  public void setAchAccountType(String achAccountType) {
    this.achAccountType = achAccountType;
  }
}
