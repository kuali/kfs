/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationDocumentRule extends GeneralLedgerPostingDocumentRuleBase {
    
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        PaymentApplicationDocument aDocument = (PaymentApplicationDocument)document;
            
        return isValid;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        
        //  if the super failed, dont even bother running these rules
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        if (!isValid) return false;
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) document;

        //  dont let PayApp docs started from CashControl docs through if not all funds are applied
        if (isSelectedInvoiceUnderApplied(paymentApplicationDocument)) {
            isValid &= false;
            errorMap.putError(
                    KNSConstants.GLOBAL_ERRORS,
                    ArKeyConstants.PaymentApplicationDocumentErrors.FULL_AMOUNT_NOT_APPLIED);
        }
        
        return isValid;
    }
    
    protected boolean isSelectedInvoiceUnderApplied(PaymentApplicationDocument doc) {
        
        // Check for full amount not being applied only when there's a related cash control document
        if (!containsCashControlDocument(doc)) {
            return true;
        }
        
        KualiDecimal totalToBeApplied;
        try {
            totalToBeApplied = doc.getTotalToBeApplied();
        }
        catch (WorkflowException e) {
            return false;
        }
        
        // KULAR-451
        return totalToBeApplied.isGreaterThan(KualiDecimal.ZERO);
    }
    
    protected boolean containsCashControlDocument(PaymentApplicationDocument doc) {
        return (StringUtils.isNotBlank(doc.getDocumentHeader().getOrganizationDocumentNumber()));
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        PaymentApplicationDocument aDocument = (PaymentApplicationDocument)approveEvent.getDocument();
        
        
        return isValid;
    }

}
