package edu.arizona.kfs.tax.document.validation.impl;

import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

import edu.arizona.kfs.tax.businessobject.Payer;


public class PayerRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayerRule.class);
    
    private Payer oldPayer;
    
    private Payer newPayer;
    

    public void setupConvenienceObjects() {
        oldPayer = (Payer) super.getOldBo();
        newPayer = (Payer) super.getNewBo();
    }
    

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }
    

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        boolean valid = true;
        
        return valid;
    }
}