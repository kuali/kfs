/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.rice.krad.document.Document;


public class PurchaseOrderRetransmitDocumentPresentationController extends PurchaseOrderDocumentPresentationController {
    
    @Override
    public boolean canSave(Document document) {
        return false;
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderRetransmitDocument poDocument = (PurchaseOrderRetransmitDocument)document;
        if (poDocument.isShouldDisplayRetransmitTab()) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);
        }
        return editModes;
    }
    
    //MSU Contribution KFSMI-8595 DTT-2396 KFSCNTRB-946
    @Override
    protected boolean canPreviewPrintPo(PurchaseOrderDocument poDocument) {
        boolean canPreviewPrintPo = super.canPreviewPrintPo(poDocument);
        if (canPreviewPrintPo) {
            return !(poDocument.getDocumentHeader().getWorkflowDocument().isEnroute() && PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS.equals(poDocument.getApplicationDocumentStatus()));
        }
        return canPreviewPrintPo;
    }
}
