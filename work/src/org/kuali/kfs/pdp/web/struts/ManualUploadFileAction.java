/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.module.pdp.action.upload;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.exception.PaymentLoadException;
import org.kuali.module.pdp.form.upload.UploadForm;
import org.kuali.module.pdp.service.LoadPaymentStatus;
import org.kuali.module.pdp.service.PaymentFileService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 *
 */
public class ManualUploadFileAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ManualUploadFileAction.class);

  private PaymentFileService paymentFileService;
  private String tmpDir;

  public ManualUploadFileAction() {
      setPaymentFileService( (PaymentFileService)SpringServiceLocator.getService("pdpPaymentFileSerivce") );
  }

  // TODO Fix this
  public void setTmpDir(String t) {
    LOG.debug("setTmpDir() setting tmp dir to " + t);
    tmpDir = t;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isSubmitRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    LOG.debug("executeLogic() starting");

    UploadForm uForm = (UploadForm)form;
    
    String filename = tmpDir + "/" + request.getSession().getId() + ".xml";
    LOG.debug("executeLogic() Filename: " + filename);
    File outputFile = new File(filename);

    FileOutputStream out = new FileOutputStream(outputFile);
    out.write(uForm.getFile().getFileData());
    out.close();
    
    try {
      LoadPaymentStatus status = paymentFileService.loadPayments(filename,getUser(request));
      if ( status.getWarnings().size() > 0 ) {
        LOG.debug("executeLogic() There were warnings when loading " + filename);
        request.setAttribute("errors",status.getWarnings());
      }
      // Save the status in the request so we can print info from it
      request.setAttribute("status",status);
    } catch (PaymentLoadException e1) {
      LOG.error("executeLogic() Exception when parsing XML", e1);

      request.setAttribute("errors",e1.getErrors());
      return mapping.findForward("hard_errors");
    }

    // Delete the file because we're done with it
    outputFile.delete();

    LOG.debug("executeLogic() File load was successful");
    return mapping.findForward("successful");
  }

  public void setPaymentFileService(PaymentFileService p) {
    this.paymentFileService = p;
  }
}
