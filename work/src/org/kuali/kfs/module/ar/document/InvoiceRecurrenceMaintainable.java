/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.service.DocumentService;

public class InvoiceRecurrenceMaintainable extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceMaintainable.class);

    private static final String INACTIVATING_NODE_NAME = "InvoiceRecurrenceIsInactivating";
    private static final String INITIATED_BY_SYSTEM_USER = "InitiatedBySystemUser";

    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        //  return true if the doc is flipping form Active to Inactive, false otherwise
        if ( StringUtils.equalsIgnoreCase( INACTIVATING_NODE_NAME, nodeName) ) {
            //  go through some contortions to get the oldMaintainable to compare against
            FinancialSystemMaintenanceDocument maintDoc = getParentMaintDoc();
            // make sure all the needed objects are there
            if ( maintDoc == null
                    || maintDoc.getOldMaintainableObject() == null
                    || maintDoc.getOldMaintainableObject().getBusinessObject() == null ) {
                return false;
            }
            boolean oldIsActive = ((InvoiceRecurrence)maintDoc.getOldMaintainableObject().getBusinessObject()).isActive();
            boolean newIsActive = ((InvoiceRecurrence)getBusinessObject()).isActive();

            //  return true if the invoicerecurrence is being deactivated, otherwise return false
            return oldIsActive && !newIsActive;
        }

        //  return true if the document was initiated by the SYSTEM_USER, false otherwise
        if ( StringUtils.equalsIgnoreCase( INITIATED_BY_SYSTEM_USER, nodeName) ) {
            FinancialSystemMaintenanceDocument maintDoc = getParentMaintDoc();
            if ( maintDoc == null
                    || maintDoc.getDocumentHeader() == null
                    || maintDoc.getDocumentHeader().getWorkflowDocument() == null
                    || StringUtils.isBlank( maintDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId() ) ) {
                return false;
            }

            String initiatorPrincipalId = maintDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            Principal principal = SpringContext.getBean(IdentityManagementService.class).getPrincipal(initiatorPrincipalId);

            return principal != null && StringUtils.equalsIgnoreCase(principal.getPrincipalName(), KFSConstants.SYSTEM_USER);
        }

        throw new UnsupportedOperationException("InvoiceRecurrenceMaintainable does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName);

    }

    protected FinancialSystemMaintenanceDocument getParentMaintDoc() {
        //  how I wish for the ability to directly access the parent object
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        try {
            return (FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
        } catch (WorkflowException e) {
            LOG.error( "Unable to retrieve maintenance document for use in split node routing - returning null",e);
        }
        return null;
    }

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        // Need to make sure that the customer invoice object has been loaded on the new object
        ((InvoiceRecurrence)getBusinessObject()).setCustomerInvoiceDocument( getBusinessObjectService().findBySinglePrimaryKey(CustomerInvoiceDocument.class, ((InvoiceRecurrence)getBusinessObject()).getInvoiceNumber() ));
    }

    /**
     * Overridden to null out the customer invoice document - BusinessObjectService#linkAndSave (the linking part specifically) gets fairly tripped
     * up by the document hanging around
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        InvoiceRecurrence invoiceRecurrence = (InvoiceRecurrence)getBusinessObject();
        invoiceRecurrence.setCustomerInvoiceDocument(null);
        super.saveBusinessObject();
    }

}
