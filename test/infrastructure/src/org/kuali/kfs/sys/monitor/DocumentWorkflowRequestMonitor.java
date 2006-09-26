package org.kuali.test.monitor;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

public class DocumentWorkflowRequestMonitor extends ChangeMonitor {

    private final Long docHeaderId;
    private final String networkId;
    private final String actionRequestedCode;

    public DocumentWorkflowRequestMonitor(KualiWorkflowDocument document, String networkId, String actionRequestedCode) throws WorkflowException {
        this.docHeaderId = document.getRouteHeaderId();
        this.networkId = networkId;
        this.actionRequestedCode = actionRequestedCode;
    }

    public boolean valueChanged() throws Exception {
        KualiWorkflowDocument document = SpringServiceLocator.getWorkflowDocumentService().createWorkflowDocument(docHeaderId, SpringServiceLocator.getKualiUserService().getKualiUser(new AuthenticationUserId(networkId)));
        if (EdenConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestedCode)) {
            return document.isCompletionRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestedCode)) {
            return document.isApprovalRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestedCode)) {
            return document.isAcknowledgeRequested();
        }
        else if (EdenConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestedCode)) {
            return document.isFYIRequested();
        }
        return false;
    }

}
