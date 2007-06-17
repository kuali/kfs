/*
 * Created on Jan 14, 2005
 *
 */
package org.kuali.module.pdp.form.format;

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
public class FormatSummaryForm extends ActionForm {

  private String processId;
  
  public FormatSummaryForm() {
    super();
  }
  
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    String btnPressed = GeneralUtilities.whichButtonWasPressed(request);
    ActionErrors actionErrors = new ActionErrors();
    
    if ("btnSearch".equals(btnPressed)){
      if (GeneralUtilities.isStringEmpty(this.processId)){
        actionErrors.add("errors", new ActionMessage("FormatSummaryForm.criteria.noneEntered"));
      } else if (!(GeneralUtilities.isStringAllNumbers(this.processId))){
        actionErrors.add("errors", new ActionMessage("FormatSummaryForm.processId.nonNumeric"));
      }
    }
    return actionErrors;
  }

  public String getProcessId() {
    return processId;
  }
  public void setProcessId(String processId) {
    this.processId = processId;
  }
}
