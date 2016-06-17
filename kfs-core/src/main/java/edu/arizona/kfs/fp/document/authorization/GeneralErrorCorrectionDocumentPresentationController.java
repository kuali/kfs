package edu.arizona.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.sys.KFSConstants;

/**
 * This is the UA modifications for the GeneralErrorCorrectionDocumentPresentationController.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

public class GeneralErrorCorrectionDocumentPresentationController extends org.kuali.kfs.fp.document.authorization.GeneralErrorCorrectionDocumentPresentationController {

    private static final long serialVersionUID = 686769123687866738L;

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            editModes.add(KFSConstants.GL_ENTRY_IMPORTING);
        }

        return editModes;
    }

}
