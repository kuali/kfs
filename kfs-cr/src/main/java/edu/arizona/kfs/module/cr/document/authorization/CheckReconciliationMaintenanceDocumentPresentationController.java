package edu.arizona.kfs.module.cr.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;

import edu.arizona.kfs.module.cr.CrPropertyConstants;

/**
 * Check Reconciliation Document Presentation Controller
 * 
 * NOTE that the readOnlyAfterAdd is broken when mixed with a ValueFinder, as of 2/17/2010. We're also using a
 * PresentationController to make this field readOnly when in Editing mode. Once the readOnlyAfterAdd business
 * is fixed or figured out, we can remove the PresentationController and keep this simpler.
 *
 * Deprecation: Eclipse identifies org.kuali.rice.kns.document.MaintenanceDocument as deprecated.
 */
@SuppressWarnings("deprecation")
public class CheckReconciliationMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    private static final long serialVersionUID = 2437103353099791198L;

    /**
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyProps = super.getConditionallyReadOnlyPropertyNames(document);

        String maintenanceAction = document.getNewMaintainableObject().getMaintenanceAction();
        if (KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equalsIgnoreCase(maintenanceAction)) {
            readOnlyProps.add(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER);
            readOnlyProps.add(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER);
        }
        return readOnlyProps;
    }

}
