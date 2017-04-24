package edu.arizona.kfs.module.ec.document.authorization;

import java.util.Set;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Document Authorizer for the Effort Certification document.
 */
public class EffortCertificationDocumentAuthorizer extends org.kuali.kfs.module.ec.document.authorization.EffortCertificationDocumentAuthorizer { 
   
	@Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        documentActionsToReturn.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        
        return documentActionsToReturn;
    }
      
}
