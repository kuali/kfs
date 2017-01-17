package edu.arizona.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.fp.document.DisbursementVoucherConstants;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.authorization.IncomeTypeAuthorizationHelper;

public class DisbursementVoucherDocumentPresentationController
		extends org.kuali.kfs.fp.document.authorization.DisbursementVoucherDocumentPresentationController {
	private static final long serialVersionUID = -3962403281004607474L;

	@Override
	public Set<String> getEditModes(Document document) {
		Set<String> editModes = super.getEditModes(document);
		editModes.add(KFSConstants.VIEW_DOCUWARE);
		this.addPaymentMethodEditMode(document, editModes);
		IncomeTypeAuthorizationHelper.addIncomeTypeEditModes(document, editModes);
		return editModes;
	}

	protected void addPaymentMethodEditMode(Document document, Set<String> editModes) {
		WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

		if (workflowDocument.isEnroute()) {
			Set<String> currentRouteLevels = workflowDocument.getNodeNames();
			if (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.PAYMENT_METHOD)
					|| currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS)
					|| currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TRAVEL)) {
				editModes.add(KFSConstants.Authorization.PAYMENT_METHOD_EDIT_MODE);
			}
		}
	}
}
