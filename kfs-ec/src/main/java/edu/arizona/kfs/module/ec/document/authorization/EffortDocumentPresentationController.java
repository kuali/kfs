package edu.arizona.kfs.module.ec.document.authorization;

import org.kuali.rice.krad.document.Document;

public class EffortDocumentPresentationController extends org.kuali.kfs.module.ec.document.authorization.EffortDocumentPresentationController {
  
    @Override
    public boolean canSave(Document document) {
    	return false;
    }
 
    
}
