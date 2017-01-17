package edu.arizona.kfs.module.tax.document.validation.impl;

import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

import edu.arizona.kfs.module.tax.businessobject.Payer;

/**
 * This class is the business rule validation for the Payer Maintenance Document (TXPR). It makes use of
 * the deprecated Maintenance Document and MaintenanceDocumentRuleBase because the current version of Rice
 * requires it.
 * 
 * @author Refactored by kosta@email.arizona.edu.
 */
@SuppressWarnings({ "deprecation", "unused" })
public class PayerRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayerRule.class);

    private Payer oldPayer;
    private Payer newPayer;

    @Override
    public void setupConvenienceObjects() {
        oldPayer = (Payer) this.getOldBo();
        newPayer = (Payer) this.getNewBo();
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        setupBaseConvenienceObjects(document);
        boolean retval = super.processCustomSaveDocumentBusinessRules(document);
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        return retval;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupBaseConvenienceObjects(document);
        boolean retval = super.processCustomRouteDocumentBusinessRules(document);

        return retval;
    }

}
