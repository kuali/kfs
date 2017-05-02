package edu.arizona.kfs.module.tax.document.validation.impl;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.DocumentService;

import edu.arizona.kfs.module.tax.TaxKeyConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payment;

@SuppressWarnings({ "unused", "deprecation" })
public class PaymentRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRule.class);

    private Payment oldPayment;

    private Payment newPayment;

    @Override
    public void setupConvenienceObjects() {
        oldPayment = (Payment) super.getOldBo();
        newPayment = (Payment) super.getNewBo();
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");

        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        boolean valid = true;

        if (newPayment.getDocNbr() != null) {
            boolean exists = SpringContext.getBean(DocumentService.class).documentExists(newPayment.getDocNbr());

            if (!exists) {
                putFieldError(TaxPropertyConstants.PaymentFields.DOCUMENT_NUMBER, TaxKeyConstants.ERROR_TAX_DOCNBR);
                valid = false;
            }
        }

        return valid;
    }
}
