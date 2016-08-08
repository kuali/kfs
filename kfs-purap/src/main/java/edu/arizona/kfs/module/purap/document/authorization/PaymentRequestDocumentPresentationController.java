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
package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;

import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;

import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.rice.krad.document.Document;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;


public class PaymentRequestDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentPresentationController {
	
    @Override
    public boolean canEdit(Document document) {
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) document;
        boolean fullDocEntryCompleted = SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(paymentRequestDocument);
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        // if the hold or cancel indicator is true, don't allow editing
        if (paymentRequestDocument.isHoldIndicator() || paymentRequestDocument.isPaymentRequestedCancelIndicator()) {
            return false;
        }

        //  fiscal officer review gets the doc editable once its enroute, but no one else does
        if (fullDocEntryCompleted) {
            if (paymentRequestDocument.isDocumentStoppedInRouteNode(PaymentRequestStatuses.NODE_ACCOUNT_REVIEW)) {
                return true;
            }
//            // if routed to fiscal officer for overage review, the fiscal officer can edit.
//            if (paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.UNIT_PRICE_OVERAGE_FO_REVIEW)) {
//                return true;
//            }
//            
//            // if routed back to initiator for overage, the initiator can edit.
//            if (paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.UNIT_PRICE_OVERAGE_BACK_TO_INITIATOR) || paymentRequestDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.UNIT_PRICE_OVERAGE_BACK_TO_APSPEC)) {
//                return true;
//            }
            
            return false;
        }        
       
        //  in general, the doc should not be editable once its enroute
        if (workflowDocument.isEnroute() || workflowDocument.isException()) {
            return false; 
        }
        return super.canEdit(document);
    }

	@Override 
	public Set<String> getEditModes(Document document) { 
		Set<String> editModes = super.getEditModes(document); 
		
		//Remove ability for AP Specialist to EDIT PREQ vendor address. 
		editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_VENDOR_ADDR_EDIT_MODE);
		
		return editModes; 
	} 
}
