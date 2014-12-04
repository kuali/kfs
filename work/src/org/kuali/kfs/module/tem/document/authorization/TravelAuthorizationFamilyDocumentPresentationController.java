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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Abstract document presentation controller which will be a base of methods shared among presentation controllers used by the travel authorization,
 * travel authorization amendment, and travel authorization close documents
 */
public abstract class TravelAuthorizationFamilyDocumentPresentationController extends TravelDocumentPresentationController {
    protected TravelDocumentService travelDocumentService; // not volatile because this object should never be accessible to more than one thread

    /**
     * Determines if the vendor can be paid for this authorization
     * @param document the authorization to check
     * @return true if the vendor can be paid, false otherwise
     */
    public boolean canPayVendor(TravelAuthorizationDocument document) {
        if (getTravelDocumentService().isUnsuccessful(document)) {
            return false;
        }
        boolean enablePayments = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);
        if (enablePayments) {
            return !isRetired(document) && !isCancelled(document) && (document.getDocumentHeader() != null && !(document.getDocumentHeader().getWorkflowDocument().isCanceled() || document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isException() || document.getDocumentHeader().getWorkflowDocument().isDisapproved() || document.getDocumentHeader().getWorkflowDocument().isSaved()));
        } else {
            return isOpen(document) && isFinalOrProcessed(document);
        }
    }

    /**
     * Determines if the travel authorization is open for reimbursement or amendment
     * @param document the authorization to check
     * @return true if the authorization is open, false otherwise
     */
    protected boolean isOpen(TravelAuthorizationDocument document) {
        return TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB.equals(document.getAppDocStatus());
    }

    /**
     * Determines if the document is in processed workflow state
     * @param document the document to check
     * @return true if the document is in processed workflow state, false otherwise
     */
    protected boolean isFinalOrProcessed(TravelAuthorizationDocument document) {
        return document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal();
    }

    /**
     * Determines if the document is in retired mode or not
     * @param document the document to check
     * @return true if the document is retired, false otherwise
     */
    protected boolean isRetired(TravelAuthorizationDocument document) {
        return TemConstants.TravelAuthorizationStatusCodeKeys.RETIRED_VERSION.equals(document.getAppDocStatus());
    }

    /**
     * Determines if the document has been cancelled as a TA or not
     * @param document the document to check
     * @return true if the document is TA cancelled, false otherwise
     */
    protected boolean isCancelled(TravelAuthorizationDocument document) {
        return TemConstants.TravelAuthorizationStatusCodeKeys.CANCELLED.equals(document.getAppDocStatus());
    }

    /**
     * @return the default implementation of the TravelDocumentService
     */
    protected TravelDocumentService getTravelDocumentService() {
        if (travelDocumentService == null) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }
        return travelDocumentService;
    }
}
