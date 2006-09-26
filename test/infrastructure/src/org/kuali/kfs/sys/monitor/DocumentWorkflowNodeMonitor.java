package org.kuali.test.monitor;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Watches the workflow document and indicates valueChanged when either the status or the current node changes.
 * 
 * @author ewestfal
 * 
 */
public class DocumentWorkflowNodeMonitor extends ChangeMonitor {

    private Long docHeaderId;
    private String networkId;
    private String desiredNodeName;

    public DocumentWorkflowNodeMonitor(KualiWorkflowDocument document, String desiredNodeName) throws WorkflowException {
        this.docHeaderId = document.getRouteHeaderId();
        this.networkId = document.getInitiatorNetworkId();
        this.desiredNodeName = desiredNodeName;
    }

    public boolean valueChanged() throws Exception {
        KualiWorkflowDocument document = SpringServiceLocator.getWorkflowDocumentService().createWorkflowDocument(docHeaderId, SpringServiceLocator.getKualiUserService().getKualiUser(new AuthenticationUserId(networkId)));
        String currentNodeName = document.getNodeNames()[0];
        // currently in Kuali there is no parallel branching so we can only ever be at one node
        return desiredNodeName.equals(currentNodeName);
    }

}
