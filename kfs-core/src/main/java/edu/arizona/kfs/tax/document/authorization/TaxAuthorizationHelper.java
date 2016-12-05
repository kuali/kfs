package edu.arizona.kfs.tax.document.authorization;

import java.util.Set;

import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.document.IncomeTypeContainer;

/**
 * This class is provides a single place to setup the IncomeTypes edit modes
 */
public class TaxAuthorizationHelper {
	/**
	 * add income type edit modes
	 */
	public static void addIncomeTypeEditModes(Document document, Set<String> editModes) {
		editModes.add(TaxConstants.Authorization.VIEW_INCOME_TYPES_EDIT_MODE);

		// if this helper is getting called then it is expected that the document implements IncomeTypeContainer
		IncomeTypeContainer ic = (IncomeTypeContainer) document;

		// only want to allow editing income types if the document is in an editable route status set to - initiated, saved or enroute
		if (ic.getIncomeTypeHandler().isEditableRouteStatus()) {
			editModes.add(TaxConstants.Authorization.EDIT_INCOME_TYPES_EDIT_MODE);
		}
	}
}
