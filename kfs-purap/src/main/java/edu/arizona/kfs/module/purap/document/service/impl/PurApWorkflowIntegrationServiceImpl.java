package edu.arizona.kfs.module.purap.document.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurApWorkflowIntegrationServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PurApWorkflowIntegrationServiceImpl implements edu.arizona.kfs.module.purap.document.service.PurApWorkflowIntegrationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApWorkflowIntegrationServiceImpl.class);

    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    @Override
    public boolean clearFYIRequestAsSuperUser(ActionRequest actionRequest, String annotation) {
        ActionRequestType actionRequested = actionRequest.getActionRequested();
        boolean isFYI = ActionRequestType.FYI == actionRequested;
        if (!isFYI) {
            throw new IllegalArgumentException("Action request for doc " + actionRequest.getDocumentId() + " is not an FYI request");
        }
        try {
            Person superUser = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            String documentTypeName = getDocumentTypeName(actionRequest);
            WorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(documentTypeName, superUser);
            workflowDocument.superUserTakeRequestedAction(actionRequest.getId(), annotation);
            return true;
        } catch (WorkflowException ex) {
            LOG.error("Exception occurred trying to clear print FYI on PO as super-user", ex);
            throw new RuntimeException(ex);
        }
    }

    private String getDocumentTypeName(ActionRequest actionRequest) throws WorkflowException {
        String documentHeaderId = actionRequest.getDocumentId();
        Document document = documentService.getByDocumentHeaderId(documentHeaderId);
        DocumentHeader header = document.getDocumentHeader();
        WorkflowDocument workflowDoc = header.getWorkflowDocument();
        String documentTypeName = workflowDoc.getDocumentTypeName();
        return documentTypeName;
    }
}
