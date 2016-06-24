package edu.arizona.kfs.fp.document.authorization;

import java.util.Set;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.rice.krad.document.Document;
import edu.arizona.kfs.sys.KFSConstants;

public class DisbursementVoucherDocumentPresentationController extends org.kuali.kfs.fp.document.authorization.DisbursementVoucherDocumentPresentationController {

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        this.addPaymentMethodEditMode(document, editModes);

        return editModes;
    }

    /**
     * add payment method edit mode into the given edit mode set if the route level is paymentMethod
     * 
     * @param document the given document
     * @param editModes the given edit mode set
     */
    protected void addPaymentMethodEditMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
        	Set<String> currentRouteLevels = workflowDocument.getNodeNames();
            /** Adding "travel" route node for ability to edit payment method. **/
            if (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.PAYMENT_METHOD) 
                    || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS)
                    || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TRAVEL)) {
                editModes.add(KFSConstants.Authorization.PAYMENT_METHOD_EDIT_MODE);
            }
        }
    }
}

