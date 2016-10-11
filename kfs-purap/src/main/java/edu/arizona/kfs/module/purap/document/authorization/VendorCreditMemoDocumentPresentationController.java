package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.sys.KFSConstants;

public class VendorCreditMemoDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.VendorCreditMemoDocumentPresentationController {

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(KFSConstants.VIEW_DOCUWARE);
        
        return editModes;
    }
}