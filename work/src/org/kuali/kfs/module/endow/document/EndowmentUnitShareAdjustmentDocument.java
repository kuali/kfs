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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSourceTypeCode;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSubTypeCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.sys.document.AmountTotaling;

/**
 * Endowment Unit/Share Adjustment (EUSA) transaction is available for those times when a number of units of a security held by the
 * KEMID must be modified without affecting the original cost or carry value of the security tax lot(s).
 */
public class EndowmentUnitShareAdjustmentDocument extends EndowmentTaxLotLinesDocumentBase {

    /**
     * Constructs a EndowmentUnitShareAdjustmentDocument.java.
     */
    public EndowmentUnitShareAdjustmentDocument() {
        super();
        setTransactionSourceTypeCode(TransactionSourceTypeCode.MANUAL);
        setTransactionSubTypeCode(TransactionSubTypeCode.NON_CASH);

        initializeSubType();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (this instanceof AmountTotaling) {
            getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
        }
        captureWorkflowHeaderInformation();

        sourceTransactionSecurities.clear();
        targetTransactionSecurities.clear();

        // functionality specific to the EndowmentUnitShareAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI allows
        // the user to enter a source security detail by default. This is adjusted before save so that the right security is saved
        // in the DB.
        if (this instanceof EndowmentUnitShareAdjustmentDocument) {

            if (!StringUtils.isEmpty(sourceTransactionSecurity.getSecurityID())) {

                if (this.getSourceTransactionLines() != null && this.getSourceTransactionLines().size() > 0) {
                    getSourceTransactionSecurities().add(0, sourceTransactionSecurity);
                }
                else if (this.getTargetTransactionLines() != null && this.getTargetTransactionLines().size() > 0) {
                    targetTransactionSecurity.setSecurityID(sourceTransactionSecurity.getSecurityID());
                    targetTransactionSecurity.setRegistrationCode(sourceTransactionSecurity.getRegistrationCode());
                    getTargetTransactionSecurities().add(0, targetTransactionSecurity);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase#getSourceTransactionSecurity()
     */
    @Override
    public EndowmentTransactionSecurity getSourceTransactionSecurity() {

        if (this.sourceTransactionSecurities.size() > 0) {
            this.sourceTransactionSecurity = this.sourceTransactionSecurities.get(0);
        }
        // functionality specific to the EndowmentUnitShareAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI
        // display a source security detail by default so this code will return the target security saved to be displayed on the
        // source security on the UI.
        else if (this.targetTransactionSecurities.size() > 0) {
            this.sourceTransactionSecurity.setSecurityID(this.targetTransactionSecurities.get(0).getSecurityID());

            this.sourceTransactionSecurity.setRegistrationCode(this.targetTransactionSecurities.get(0).getRegistrationCode());
        }
        return this.sourceTransactionSecurity;

    }

    @Override
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceLines) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetLines) {
        // TODO Auto-generated method stub

    }

}
