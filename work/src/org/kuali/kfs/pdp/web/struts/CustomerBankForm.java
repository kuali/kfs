/*
 * Created on Jul 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.form.customerprofile;

import org.apache.struts.action.ActionForm;

/**
 * @author delyea
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CustomerBankForm extends ActionForm {
  private String disbursementDescription;
  private String disbursementTypeCode;
  private Integer bankId;

  public CustomerBankForm() {
  }
  
  /**
   * @return Returns the disbursementTypeCode.
   */
  public String getDisbursementTypeCode() {
    return disbursementTypeCode;
  }
  /**
   * @param disbursementTypeCode The disbursementTypeCode to set.
   */
  public void setDisbursementTypeCode(String disbursementTypeCode) {
    this.disbursementTypeCode = disbursementTypeCode;
  }
  /**
   * @return Returns the bankId.
   */
  public Integer getBankId() {
    return bankId;
  }
  /**
   * @return Returns the disbursementDescription.
   */
  public String getDisbursementDescription() {
    return disbursementDescription;
  }
  /**
   * @param bankId The bankId to set.
   */
  public void setBankId(Integer bankId) {
    this.bankId = bankId;
  }
  /**
   * @param disbursementDescription The disbursementDescription to set.
   */
  public void setDisbursementDescription(String disbursementDescription) {
    this.disbursementDescription = disbursementDescription;
  }

}
