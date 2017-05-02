package edu.arizona.kfs.module.purap.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

import edu.arizona.kfs.module.purap.businessobject.CreditMemoIncomeType;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;

@SuppressWarnings("deprecation")
public class VendorCreditMemoAction extends org.kuali.kfs.module.purap.document.web.struts.VendorCreditMemoAction {

    /**
     * Adds a CreditMemoIncomeType instance created from the current "newIncomeType" to the document
     */
    public ActionForward newIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().addNewIncomeType();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected CreditMemoIncomeType from the document
     */
    public ActionForward deleteIncomeType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getIncomeTypeContainer(form).getIncomeTypeHandler().removeIncomeType(getLineToDelete(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @SuppressWarnings("unchecked")
    private IncomeTypeContainer<CreditMemoIncomeType, Integer> getIncomeTypeContainer(ActionForm form) {
        VendorCreditMemoForm cmForm = (VendorCreditMemoForm) form;
        return (IncomeTypeContainer<CreditMemoIncomeType, Integer>) cmForm.getDocument();
    }

    /**
     * Refreshes 1099 income types based on summary accounting lines
     */
    public ActionForward refreshIncomeTypesFromAccountLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadIncomeTypesFromAccountLines(form);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Forces update to account line amounts then populates income types
     */
    @SuppressWarnings("unchecked")
    private void loadIncomeTypesFromAccountLines(ActionForm form) {
        VendorCreditMemoForm cmForm = (VendorCreditMemoForm) form;
        VendorCreditMemoDocument doc = (VendorCreditMemoDocument) cmForm.getDocument();

        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(doc);
        cmForm.refreshAccountSummmary();

        List<PurApItem> items = doc.getItems();

        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        List<PurApItemUseTax> useTaxItems = null;

        for (PurApItem item : items) {
            accountingLines.addAll(item.getSourceAccountingLines());

            if (doc.isUseTaxIndicator() && (item.getUseTaxItems() != null) && !item.getUseTaxItems().isEmpty()) {
                if (useTaxItems == null) {
                    useTaxItems = new ArrayList<PurApItemUseTax>();
                }

                useTaxItems.addAll(item.getUseTaxItems());
            }
        }

        doc.getIncomeTypeHandler().populateIncomeTypes(accountingLines, useTaxItems);
    }

    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        IncomeTypeContainer<CreditMemoIncomeType, Integer> ict = getIncomeTypeContainer(kualiDocumentFormBase);

        // if we have no DocumentIncomeTypes on the document then lets pre-populate
        // Improve performance of documents with many accounting lines (leading to many route nodes)
        // add isEditableRouteStatus() as a condition
        if (ict.getIncomeTypes().isEmpty() && ict.getIncomeTypeHandler().isEditableRouteStatus()) {
            loadIncomeTypesFromAccountLines(kualiDocumentFormBase);
        }
    }
}
