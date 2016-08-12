package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;

import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.rice.krad.document.Document;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;


public class PaymentRequestDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentPresentationController {
	
    @Override
    public boolean canEdit(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        boolean fullDocEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument);
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        // if the hold or cancel indicator is true, don't allow editing
        if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            return false;
        }

        //  fiscal officer review gets the doc editable once its enroute, but no one else does
        if (fullDocEntryCompleted) {
            if (paymentRequestDocument.isDocumentStoppedInRouteNode(PaymentRequestStatuses.NODE_ACCOUNT_REVIEW)) {
                return true;
            }
            
            return false;
        }        
       
        //  in general, the doc should not be editable once its enroute
        if (workflowDocument.isEnroute() || workflowDocument.isException()) {
            return false; 
        }
        return super.canEdit(document);
    }

	@Override 
	public Set<String> getEditModes(Document document) { 
		Set<String> editModes = super.getEditModes(document); 
		
		//Remove ability for AP Specialist to EDIT PREQ vendor address. 
		editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_VENDOR_ADDR_EDIT_MODE);
		
		return editModes; 
	} 
}
