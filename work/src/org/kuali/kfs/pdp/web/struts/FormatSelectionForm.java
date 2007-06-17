/*
 * Created on Aug 12, 2004
 *
 */
package org.kuali.module.pdp.form.format;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.kuali.module.pdp.utilities.DateHandler;


/**
 * @author jsissom
 *
 */
public class FormatSelectionForm extends ActionForm {
  private String[] customerProfileId;
  private String paymentDate;
  private String immediate;
  private String paymentTypes;

  public FormatSelectionForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    if ( customerProfileId != null ) {
      for (int i = 0; i < customerProfileId.length; i++) {
        customerProfileId[i] = "";
      }
      immediate = "N";
    }
  }

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    ActionErrors errors = new ActionErrors();
    DateHandler.validDate(errors,"errors",paymentDate);
    return errors;
  }

  public String getPaymentTypes() {
    return paymentTypes;
  }

  public void setPaymentTypes(String paymentTypes) {
    this.paymentTypes = paymentTypes;
  }

  public String[] getCustomerProfileId() {
    return customerProfileId;
  }

  public void setCustomerProfileId(String[] customerProfileId) {
    this.customerProfileId = customerProfileId;
  }

  public String getCustomerProfileId(int index) { 
    return customerProfileId[index];
  }

  public void setStringIndexed(int index, String value) { 
    customerProfileId[index] = value;
  }

  public String getImmediate() {
    return immediate;
  }

  public void setImmediate(String immediate) {
    this.immediate = immediate;
  }

  public String getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(String paymentDate) {
    this.paymentDate = paymentDate;
  }
}
