/*
 * Created on Aug 24, 2004
 */
package org.kuali.module.pdp.form.paymentmaintenance;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 *
 */

public class PaymentMaintenanceForm extends ActionForm {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceForm.class);
  
  private String changeText;
  private Integer changeId;
  private Integer paymentDetailId; // used for cancelling disbursements from EPIC
  private String action;

  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    LOG.debug("Entered validate().");
    //create instance of ActionErrors to send errors to user
    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
    
    if (buttonPressed.startsWith("btnUpdateSave")) {
      if (GeneralUtilities.isStringEmpty(this.changeText)) {
        actionErrors.add("errors", new ActionMessage("paymentMaintenanceForm.changeText.empty"));
      }
      if (GeneralUtilities.isStringFieldAtMostNLength(this.changeText,250)) {
        actionErrors.add("errors", new ActionMessage("paymentMaintenanceForm.changeText.over250"));
      }
    }
    LOG.debug("Exiting validate()  There were " + actionErrors.size() + " ActionMessages found.");
    return actionErrors;
  }
  /**
   * @return Returns the action.
   */
  public String getAction() {
    return action;
  }
  /**
   * @return Returns the changeText.
   */
  public String getChangeText() {
    return changeText;
  }
  /**
   * @return Returns the changeId.
   */
  public Integer getChangeId() {
    return changeId;
  }
  /**
   * @param action The action to set.
   */
  public void setAction(String action) {
    this.action = action;
  }
  /**
   * @param changeText The changeText to set.
   */
  public void setChangeText(String changeText) {
    this.changeText = changeText;
  }
  /**
   * @param detailId The detailId to set.
   */
  public void setChangeId(Integer changeId) {
    this.changeId = changeId;
  }
  /**
   * @return Returns the paymentDetailId.
   */
  public Integer getPaymentDetailId() {
    return paymentDetailId;
  }
  /**
   * @param paymentDetailId The paymentDetailId to set.
   */
  public void setPaymentDetailId(Integer paymentDetailId) {
    this.paymentDetailId = paymentDetailId;
  }
}
