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
package org.kuali.kfs.fp.document.authorization;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashReceiptDocumentPresentationController extends LedgerPostingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canApprove(Document document) {
        return this.canApproveOrBlanketApprove(document) ? super.canApprove(document) : false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canBlanketApprove(Document document) {
        return this.canApproveOrBlanketApprove(document) ? super.canBlanketApprove(document) : false;
    }

    private boolean canApproveOrBlanketApprove(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isApprovalRequested() && !workflowDocument.isAdHocRequested()) {
            CashReceiptDocument cashReceiptDocument = (CashReceiptDocument) document;

            String campusCode = cashReceiptDocument.getCampusLocationCode();
            CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(campusCode);
            if (cashDrawer == null) {
                throw new RuntimeException("No cash drawer exists for campus code "+campusCode+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashReceiptDocument for campus "+campusCode);
            }
            if (cashDrawer == null) {
                throw new IllegalStateException("There is no cash drawer associated with cash receipt: " + cashReceiptDocument.getDocumentNumber());
            }

            if (cashDrawer.isClosed()) {
                return false;
            }
        }

        return true;
    }
}
