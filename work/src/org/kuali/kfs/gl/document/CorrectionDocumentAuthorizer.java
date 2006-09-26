package org.kuali.module.gl.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentAuthorizerBase;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

public class CorrectionDocumentAuthorizer extends DocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocumentAuthorizer.class);

    public CorrectionDocumentAuthorizer() {
        super();
    }

    @Override
    public Map getEditMode(Document document, KualiUser user) {
        LOG.debug("getEditMode() started");

        String editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsCanceled() || (document.getDocumentHeader().getFinancialDocumentInErrorNumber() != null)) {
            editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
        }
        else if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (workflowDocument.userIsInitiator(user)) {
                editMode = AuthorizationConstants.TransactionalEditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute()) {
            editMode = AuthorizationConstants.TransactionalEditMode.VIEW_ONLY;
        }

        Map editModeMap = new HashMap();
        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }
}
