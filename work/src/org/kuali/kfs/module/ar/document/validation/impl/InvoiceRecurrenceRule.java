package org.kuali.module.ar.rules;

import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.InvoiceRecurrence;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.service.CustomerService;

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
