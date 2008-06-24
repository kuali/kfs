/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.web.struts;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.web.struts.BaseAction;
import org.kuali.kfs.pdp.exception.PaymentLoadException;
import org.kuali.kfs.pdp.web.struts.UploadForm;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;


/**
 * @author jsissom
 */
public class ManualUploadFileAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ManualUploadFileAction.class);

    public ManualUploadFileAction() {
    }

    public String getTmpDir() {
        String tempDirectoryName = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("ManualUploadFileAction.getTmpDir() returning " + tempDirectoryName);
        }
        return tempDirectoryName;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isSubmitRole()||sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() starting");

        UploadForm uForm = (UploadForm) form;

        String filename = getTmpDir() + File.separator + PdpConstants.PDP_MANUAL_FILE_UPLOAD_TEMP_FILE_PREFIX + request.getSession().getId() + ".xml";
        LOG.debug("executeLogic() Filename: " + filename);
        File outputFile = new File(filename);

        FileOutputStream out = new FileOutputStream(outputFile);
        out.write(uForm.getFile().getFileData());
        out.close();

        try {
            LoadPaymentStatus status = getPaymentFileService().loadPayments(filename, getUser(request));
            if (status.getWarnings().size() > 0) {
                LOG.debug("executeLogic() There were warnings when loading " + filename);
                request.setAttribute("errors", status.getWarnings());
            }
            // Save the status in the request so we can print info from it
            request.setAttribute("status", status);
        }
        catch (PaymentLoadException e1) {
            LOG.error("executeLogic() Exception when parsing XML", e1);

            request.setAttribute("errors", e1.getErrors());
            return mapping.findForward("hard_errors");
        }
        finally {
            try {
                // Delete the file because we're done with it
                outputFile.delete();
            }
            catch (RuntimeException e) {
                LOG.error("Error trying to delete temporary file in ManualUploadFileAction.", e);
            }
        }

        LOG.debug("executeLogic() File load was successful");
        return mapping.findForward("successful");
    }

    public PaymentFileService getPaymentFileService() {
        return SpringContext.getBean(PaymentFileService.class);
    }
    
}
