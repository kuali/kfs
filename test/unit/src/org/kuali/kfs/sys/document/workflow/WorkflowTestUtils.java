package org.kuali.workflow;

import junit.framework.Assert;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentWorkflowNodeMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class WorkflowTestUtils {

	public static KualiWorkflowDocument refreshDocument(Document document, NetworkIdVO networkId) throws WorkflowException, UserNotFoundException {
    	Long docId = document.getDocumentHeader().getWorkflowDocument().getRouteHeaderId();
    	KualiWorkflowDocument workflowDocument = SpringServiceLocator.getWorkflowDocumentService().createWorkflowDocument(docId, SpringServiceLocator.getKualiUserService().getUser(new AuthenticationUserId(networkId.getNetworkId())));
    	GlobalVariables.getUserSession().setWorkflowDocument(workflowDocument);
        GlobalVariables.putLocalDocReference(docId.toString());
        document.getDocumentHeader().setWorkflowDocument(workflowDocument);
        return workflowDocument;
    }
	
	public static boolean isAtNode(Document document, String nodeName) throws WorkflowException {
		String[] nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
		for (int index = 0; index < nodeNames.length; index++) {
			if (nodeName.equals(nodeNames[index])) {
				return true;
			}
		}
		return false;
	}
	
	public static void waitForNodeChange(KualiWorkflowDocument document, String desiredNodeName) throws Exception {
		DocumentWorkflowNodeMonitor monitor = new DocumentWorkflowNodeMonitor(document, desiredNodeName);
		Assert.assertTrue(ChangeMonitor.waitUntilChange(monitor, 240, 5));
	}
	
	public static void waitForStatusChange(KualiWorkflowDocument document, String desiredStatus) throws Exception {
		DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(SpringServiceLocator.getDocumentService(), ""+document.getRouteHeaderId(), desiredStatus);
		Assert.assertTrue(ChangeMonitor.waitUntilChange(monitor, 240, 5));
	}    
	
}
