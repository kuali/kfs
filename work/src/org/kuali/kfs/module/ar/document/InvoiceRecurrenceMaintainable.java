/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;

public class InvoiceRecurrenceMaintainable extends FinancialSystemMaintainable {

    private static final String INACTIVATING_NODE_NAME = "InvoiceRecurrenceIsInactivating";
    
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        
        //  choke if its not a node name we're prepared to handle
        if (!INACTIVATING_NODE_NAME.equalsIgnoreCase(nodeName)) {
            throw new UnsupportedOperationException("InvoiceRecurrenceMaintainable does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName);
        }
        
        //  go through some contortions to get the oldMaintainable to compare against
        FinancialSystemMaintenanceDocument maintDoc = getParentMaintDoc();
        boolean oldIsActive = ((InvoiceRecurrence)maintDoc.getOldMaintainableObject().getBusinessObject()).isActive();
        boolean newIsActive = ((InvoiceRecurrence)this.getBusinessObject()).isActive();
        
        //  return true if the invoicerecurrence is being deactivated, otherwise return false
        return oldIsActive && !newIsActive;
    }

    private FinancialSystemMaintenanceDocument getParentMaintDoc() {
        //  how I wish for the ability to directly access the parent object
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc =(FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(this.documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }
    
}
