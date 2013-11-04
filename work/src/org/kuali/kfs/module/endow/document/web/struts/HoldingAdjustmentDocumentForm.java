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
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;

/**
 * HoldingAdjustmentDocumentForm class
 */
public class HoldingAdjustmentDocumentForm extends EndowmentTransactionLinesDocumentFormBase {

    /**
     * Constructs a EndowmentUnitShareAdjustmentDocumentForm.java.
     */
    public HoldingAdjustmentDocumentForm() {
        super();

        // don't show these values on the edoc.
        setShowPrincipalTotalUnits(false);
        setShowIncomeTotalUnits(false);
        setShowUnitAdjustmentAmount(true);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "EHA";
    }

    /**
     * Gets the HoldingAdjustmentDocument document
     * 
     * @return document
     */
    public HoldingAdjustmentDocument getHoldingAdjustmentDocument() {
        return (HoldingAdjustmentDocument) getDocument();
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
