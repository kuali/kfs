package edu.arizona.kfs.module.ec.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.web.struts.EffortCertificationForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class handles Actions for EffortCertification document approval.
 */
public class CertificationReportAction extends org.kuali.kfs.module.ec.document.web.struts.CertificationReportAction {
   
	/**
     * This method recalculates total of the current salary values of the detail lines on the document.
     * The values are not persisted to the database at this time.
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return An ActionForward     
     */	
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        EffortCertificationForm effortCertificationForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortDocument = effortCertificationForm.getEffortCertificationDocument();
        effortDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(effortDocument.getTotalPayrollAmount());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
}
