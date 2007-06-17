/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.module.pdp.form.upload;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * @author jsissom
 *
 */
public class UploadForm extends ActionForm {
  private FormFile file;

  public UploadForm() {
    super();
  }

  /**
   * Retrieve a representation of the file the user has uploaded
   */
  public FormFile getFile() {
      return file;
  }

  /**
   * Set a representation of the file the user has uploaded
   */
  public void setFile(FormFile file) {
      this.file = file;
  }
}
