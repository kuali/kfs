/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument;

/**
 * This class...
 */
public class EndowmentUnitShareAdjustmentDocumentForm extends EndowmentTransactionLinesDocumentFormBase {

    /**
     * Constructs a EndowmentUnitShareAdjustmentDocumentForm.java.
     */
    public EndowmentUnitShareAdjustmentDocumentForm() {
        super();

        // don't show these values on the edoc.
        setShowPrincipalTotalAmount(false);
        setShowIncomeTotalAmount(false);
        setShowTransactionAmount(false);

        // do not show the etran code on the UI screen
        setShowETranCode(false);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "EUSA";
    }

    /**
     * This method...
     * 
     * @return
     */
    public EndowmentUnitShareAdjustmentDocument getEndowmentUnitShareAdjustmentDocument() {
        return (EndowmentUnitShareAdjustmentDocument) getDocument();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getSourceGroupLabelName()
     */
    @Override
    public String getSourceGroupLabelName() {
        return EndowConstants.DECREASE_TRANSACTION_LINE_GROUP_LABEL_NAME;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getTargetGroupLabelName()
     */
    @Override
    public String getTargetGroupLabelName() {
        return EndowConstants.INCREASE_TRANSACTION_LINE_GROUP_LABEL_NAME;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getSourceTaxLotsLabelName()
     */
    @Override
    public String getSourceTaxLotsLabelName() {
        return EndowConstants.DECREASE_TAX_LOTS_LABEL_NAME;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getTargetTaxLotsLabelName()
     */
    @Override
    public String getTargetTaxLotsLabelName() {
        return EndowConstants.INCREASE_TAX_LOTS_LABEL_NAME;
    }
}
