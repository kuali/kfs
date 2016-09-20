package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;
import org.kuali.rice.krad.document.Document;
import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;

public class PaymentRequestDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentPresentationController {

	@Override 
	public Set<String> getEditModes(Document document) { 
		Set<String> editModes = super.getEditModes(document); 
		
		//Remove ability for AP Specialist to EDIT PREQ vendor address. 
		editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_VENDOR_ADDR_EDIT_MODE);
		
		return editModes; 
	} 
}
