package edu.arizona.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.tax.document.IncomeTypeContainer;
import edu.arizona.kfs.tax.document.IncomeTypeHandler;

public class DisbursementVoucherAction extends org.kuali.kfs.fp.document.web.struts.DisbursementVoucherAction {

    public ActionForward newIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().addNewIncomeType();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().removeIncomeType(getLineToDelete(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    
    private IncomeTypeContainer <DisbursementVoucherIncomeType, String>getIncomeTypeContainer(ActionForm form) {
       DisbursementVoucherForm dvForm = (DisbursementVoucherForm)form;
        return (IncomeTypeContainer<DisbursementVoucherIncomeType, String>)dvForm.getDocument();
    }

    public ActionForward refreshIncomeTypesFromAccountLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm)form;
        DisbursementVoucherDocument doc = (DisbursementVoucherDocument)dvForm.getDocument();
        doc.getIncomeTypeHandler().populateIncomeTypes(doc.getSourceAccountingLines());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}