package edu.arizona.kfs.module.purap.document.web.struts;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;


public class TaxFormAction extends KualiAction {

    public ActionForward downloadTaxForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.valueOf(request.getParameter("id"));
        String year = request.getParameter("year");
        
        TaxReporting1099Service taxReportingService = SpringContext.getBean(TaxReporting1099Service.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] pdf = taxReportingService.getPayee1099Form(id, year);
        
        if ((pdf != null) && (pdf.length > 0)) {
            baos.write(pdf);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, "f1099msc" + ReportGeneration.PDF_FILE_EXTENSION);
        }
        
        return null;
    }
    
    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }
}
