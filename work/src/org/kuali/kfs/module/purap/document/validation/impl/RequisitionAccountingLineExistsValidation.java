package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class ensures that there are accounting lines on each item. If any item
 * is missing accounting lines, a warning or error is issued.
 */
public class RequisitionAccountingLineExistsValidation extends GenericValidation {

    private RequisitionService requisitionService;

    /**
     * If the organization has a content reviewer, the document is allowed to route (validation suceeds), but
     * a warning is issued. If there is no content reviewer, an error is issued and validation does not succeed.
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        RequisitionDocument doc = (RequisitionDocument) event.getDocument();

        if (doc.isMissingAccountingLines()) {
            String org = doc.getOrganizationCode();
            String chart = doc.getChartOfAccountsCode();
            if (!requisitionService.hasContentReviewer(org, chart)) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES);
                return false;
            }
            else {
                /*GlobalVariables.getMessageMap().putWarning(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES);
                 * This should be handled in the action because we need to confirm with the user.
                 */
            }
        }
        return true;
    }

    public void setRequisitionService(RequisitionService reqs){
        this.requisitionService = reqs;
    }

    public RequisitionService getRequisitionService(){
        return this.requisitionService;
    }
}
