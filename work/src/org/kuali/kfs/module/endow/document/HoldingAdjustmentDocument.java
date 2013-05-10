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
package org.kuali.kfs.module.endow.document;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSourceTypeCode;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSubTypeCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.sys.document.AmountTotaling;

/**
 * Holding Adjustment (EHA) transaction is available for those times when a number of units of a security held by the
 * KEMID must be modified without affecting the original cost or carry value of the security tax lot(s).
 */
public class HoldingAdjustmentDocument extends EndowmentTaxLotLinesDocumentBase {

    /**
     * Constructs a HoldingAdjustmentDocument.java.
     */
    public HoldingAdjustmentDocument() {
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

        sourceTransactionSecurities.clear();
        targetTransactionSecurities.clear();

        // functionality specific to the HoldingAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI allows
        // the user to enter a source security detail by default. This is adjusted before save so that the right security is saved
        // in the DB.
        if (this instanceof HoldingAdjustmentDocument) {

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
        // functionality specific to the HoldingAdjustmentDocument. The document will have a source or target security
        // detail depending on whether the user has entered source or target transaction lines (Decrease or Increase). The UI
        // display a source security detail by default so this code will return the target security saved to be displayed on the
        // source security on the UI.
        else if (this.targetTransactionSecurities.size() > 0) {
            this.sourceTransactionSecurity.setSecurityID(this.targetTransactionSecurities.get(0).getSecurityID());

            this.sourceTransactionSecurity.setRegistrationCode(this.targetTransactionSecurities.get(0).getRegistrationCode());
        }
        return this.sourceTransactionSecurity;

    }

}
