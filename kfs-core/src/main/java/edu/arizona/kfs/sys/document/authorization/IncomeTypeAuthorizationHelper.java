package edu.arizona.kfs.sys.document.authorization;

import java.util.Set;

import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.document.IncomeTypeContainer;

/**
 * This class is provides a single place to setup the IncomeTypes edit modes
 */
public class IncomeTypeAuthorizationHelper {
    /**
     * add income type edit modes
     */
    public static void addIncomeTypeEditModes(Document document, Set<String> editModes) {
        editModes.add(KFSConstants.IncomeTypeConstants.IncomeTypesAuthorization.VIEW_INCOME_TYPES_EDIT_MODE);

        // if this helper is getting called then it is expected that the document implements IncomeTypeContainer
        @SuppressWarnings("rawtypes")
        IncomeTypeContainer ic = (IncomeTypeContainer) document;

        // only want to allow editing income types if the document is in an editable route status set to - initiated, saved or enroute
        if (ic.getIncomeTypeHandler().isEditableRouteStatus()) {
            editModes.add(KFSConstants.IncomeTypeConstants.IncomeTypesAuthorization.EDIT_INCOME_TYPES_EDIT_MODE);
        }
    }
}
