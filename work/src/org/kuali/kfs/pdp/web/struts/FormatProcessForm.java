/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.form.format;

import org.apache.struts.action.ActionForm;

/**
 * @author jsissom
 *
 */
public class FormatProcessForm extends ActionForm {
  private Integer procId;
  private String campusCd;

  public FormatProcessForm() {
    super();
  }

  public String getCampusCd() {
    return campusCd;
  }
  public void setCampusCd(String campusCd) {
    this.campusCd = campusCd;
  }
  public Integer getProcId() {
    return procId;
  }
  public void setProcId(Integer procId) {
    this.procId = procId;
  }
}
