package edu.arizona.kfs.fp.document.authorization;

import java.util.Set;

import edu.arizona.kfs.sys.KFSConstants;

import org.kuali.rice.krad.document.Document;

public class ProcurementCardDocumentPresentationController extends org.kuali.kfs.fp.document.authorization.ProcurementCardDocumentPresentationController {
	
	@Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(KFSConstants.VIEW_DOCUWARE);
        
        return editModes;
    }
}