package org.kuali.test.monitor;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

public class DocumentWorkflowRequestMonitor extends ChangeMonitor {

    private final Long docHeaderId;
    private final KualiUser user;
    private final String actionRequestedCode;

    public DocumentWorkflowRequestMonitor(Long docHeaderId, KualiUser user, String actionRequestedCode) {
        this.docHeaderId = docHeaderId;
        this.user = user;
        this.actionRequestedCode = actionRequestedCode;
    }

    public boolean valueChanged()
        throws WorkflowException
    {
        KualiWorkflowDocument document = SpringServiceLocator.getWorkflowDocumentService().createWorkflowDocument(docHeaderId, user);
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
