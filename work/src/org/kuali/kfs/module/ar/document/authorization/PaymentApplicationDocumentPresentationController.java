/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

/**
 * Presentation Controller for Payment Application.
 */
public class PaymentApplicationDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) document;

        // KULAR-452
        if (paymentApplicationDocument.hasCashControlDocument()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        boolean copyable = true;
        PaymentApplicationDocument ciDoc = (PaymentApplicationDocument) document;

        // Confirm doc is in a saved and copyable state.
        copyable &= !ciDoc.getDocumentHeader().getWorkflowDocument().isInitiated();
        copyable &= !ciDoc.getDocumentHeader().getWorkflowDocument().isCanceled();

        // Confirm doc is reversible.
        copyable &= !((PaymentApplicationDocument) document).isPaymentApplicationCorrection();
        return copyable;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        return (super.canEdit(document) && !((PaymentApplicationDocument) document).isPaymentApplicationCorrection()); // can't edit
                                                                                                                       // if it's a
                                                                                                                       // correction
                                                                                                                       // document
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {

        Set<String> editModes = super.getEditModes(document);
        if (((PaymentApplicationDocument) document).isPaymentApplicationCorrection()) {
            editModes.add(KFSConstants.ERROR_CORRECTION_EDITING_MODE);
        }
        return editModes;
    }
}
