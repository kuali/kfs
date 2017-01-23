package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.authorization.IncomeTypeAuthorizationHelper;

public class VendorCreditMemoDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.VendorCreditMemoDocumentPresentationController {
    private static final long serialVersionUID = 8615870275897942790L;

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        editModes.add(KFSConstants.VIEW_DOCUWARE);

        IncomeTypeAuthorizationHelper.addIncomeTypeEditModes(document, editModes);

        return editModes;
    }
}
