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

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * HoldingAdjustmentDocumentAction class
 */
public class HoldingAdjustmentDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#updateTransactionLineTaxLots(boolean,
     *      boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {
        UpdateHoldingAdjustmentDocumentTaxLotsService updateHoldingAdjustmentDocumentTaxLotsService = SpringContext.getBean(UpdateHoldingAdjustmentDocumentTaxLotsService.class);
        HoldingAdjustmentDocument holdingAdjustmentDocument = (HoldingAdjustmentDocument) etlDocument;

        // call service to update the tax lot lines if transaction amount is entered
        if (ObjectUtils.isNotNull(transLine.getTransactionAmount()) && !transLine.getTransactionAmount().isZero()) {
            updateHoldingAdjustmentDocumentTaxLotsService.updateTransactionLineTaxLotsByTransactionAmount(isUpdate, holdingAdjustmentDocument, transLine, isSource);
        }

        // call service to update the tax lot lines if unit adjustment amount is entered
        if (ObjectUtils.isNotNull(transLine.getUnitAdjustmentAmount()) && (transLine.getUnitAdjustmentAmount().compareTo(BigDecimal.ZERO) != 0)) {
            updateHoldingAdjustmentDocumentTaxLotsService.updateTransactionLineTaxLotsByUnitAdjustmentAmount(isUpdate, holdingAdjustmentDocument, transLine, isSource);
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#getRefreshTaxLotsOnSaveOrSubmit()
     */
    @Override
    protected boolean getRefreshTaxLotsOnSaveOrSubmit() {
        return false;
    }

}
