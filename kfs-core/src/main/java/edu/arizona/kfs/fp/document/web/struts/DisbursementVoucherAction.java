package edu.arizona.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;
import edu.arizona.kfs.sys.document.IncomeTypeHandler;

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
    
    @Override
    public ActionForward generateNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm)form;

        DisbursementVoucherDocument doc = (DisbursementVoucherDocument)dvForm.getDocument();
        
        int numAcctLines1 = 0;
        // save the accounting line size so we can determine how many
        // were added after the generateNonResidentAlienTaxLines() call
        if (doc.getSourceAccountingLines() != null) {
            numAcctLines1= doc.getSourceAccountingLines().size();
        }
        
        ActionForward retval = super.generateNonResidentAlienTaxLines(mapping, form, request, response);

        int numAcctLines2 = 0;
        if (doc.getSourceAccountingLines() != null) {
            numAcctLines2 = doc.getSourceAccountingLines().size();
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("#acctlines before: " + numAcctLines1 + ", #acctlines after: " + numAcctLines2);
        }

        // if we added accounting lines then populate the income types from the new lines
        if (numAcctLines2 > numAcctLines1) {
            if (LOG.isDebugEnabled()){
                LOG.debug("found " + (numAcctLines2 - numAcctLines1) + " accounting lines to add");
            }

            IncomeTypeHandler handler = getIncomeTypeContainer(form).getIncomeTypeHandler();
            for (int i = numAcctLines1; i < numAcctLines2; ++i) {
                handler.onAccountingLineAdded(doc.getSourceAccountingLine(i));
            }
        }

        return retval;
    }
}