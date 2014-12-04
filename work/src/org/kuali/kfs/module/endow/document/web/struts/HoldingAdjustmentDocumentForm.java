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
