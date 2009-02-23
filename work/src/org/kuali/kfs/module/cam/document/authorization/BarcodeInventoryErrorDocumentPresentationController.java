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
package org.kuali.kfs.module.cam.document.authorization;

import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;

/**
 * Presentation Controller for Barcode Error Documents
 */
public class BarcodeInventoryErrorDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    @Override
    protected boolean canReload(Document document) {
        return false;
    }

    @Override
    protected boolean canSave(Document document) {
        return false;
    }

    @Override
    protected boolean canBlanketApprove(Document document) {
        return false;
    }

    /**
     * hide submit button if no error exist.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canRoute(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canRoute(Document document) {
        BarcodeInventoryErrorDocument barcodeInventoryErrorDocument = (BarcodeInventoryErrorDocument) document;
        return (document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() || document.getDocumentHeader().getWorkflowDocument().stateIsSaved()) && !barcodeInventoryErrorDocument.getBarcodeInventoryErrorDetail().isEmpty();
    }
}
