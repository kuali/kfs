package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;

public class InvoiceRecurrenceRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceRule.class);
    private InvoiceRecurrence oldInvoiceRecurrence;
    private InvoiceRecurrence newInvoiceRecurrence;

    /**
     * This method initializes the old and new InvoiceRecurrence
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newInvoiceRecurrence == null) {
            newInvoiceRecurrence = (InvoiceRecurrence) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldInvoiceRecurrence == null) {
            oldInvoiceRecurrence = (InvoiceRecurrence) document.getOldMaintainableObject().getBusinessObject();
        }
    }

}
