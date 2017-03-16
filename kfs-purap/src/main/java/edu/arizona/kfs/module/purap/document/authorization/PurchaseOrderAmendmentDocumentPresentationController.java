package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.RequisitionEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;

public class PurchaseOrderAmendmentDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PurchaseOrderAmendmentDocumentPresentationController {

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;

        if (PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus())) {
            WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            // amendment doc needs to lock its field for initiator while enroute
            if (workflowDoc.isInitiated() || workflowDoc.isSaved()) {
                editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
            }
        }
        if (PurchaseOrderStatuses.APPDOC_AWAIT_NEW_UNORDERED_ITEM_REVIEW.equals(poDocument.getApplicationDocumentStatus())) {
            editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
        }

        if (SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument) document, "New Unordered Items")) {
            editModes.add(PurchaseOrderEditMode.UNORDERED_ITEM_ACCOUNT_ENTRY);
        }

        //make clear tax button always available like REQS
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(PurchaseOrderEditMode.CLEAR_ALL_TAXES);
        }

        if (isAccountNode(document)) {
            editModes.add(RequisitionEditMode.RESTRICT_FISCAL_ENTRY);
        }

        return editModes;
    }


    /**
     * determine whether the current route node is account
     * 
     * @param document the given POA document
     * @return true if the current route node is account
     */
    protected boolean isAccountNode(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        Set<String> currentRouteLevels = workflowDocument.getNodeNames();

        return workflowDocument.isEnroute() && currentRouteLevels.contains(KFSConstants.RouteLevelNames.ACCOUNT);
    }
}
