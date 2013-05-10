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
package org.kuali.kfs.module.endow.document.service;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;

public interface UpdateHoldingAdjustmentDocumentTaxLotsService {

    /**
     * Updates the tax lots related to the given transaction line in the Holding Adjustment document when
     * Unit Adjustment Amount is entered.
     * 
     * @param isUpdate boolean indicator if update
     * @param holdingAdjustmentDocument the Holding Adjustment Document for which we compute the transaction line related tax lots
     * @param transLine the transaction line for which we update the tax lots
     * @param isSource boolean indicator if the transaction lines are source
     */
    public void updateTransactionLineTaxLotsByUnitAdjustmentAmount(boolean isUpdate, HoldingAdjustmentDocument holdingAdjustmentDocument, EndowmentTransactionLine transLine, boolean isSource);

    /**
     * Updates the tax lots related to the given transaction line in the Holding Adjustment document when
     * Transaction Amount is entered.
     * 
     * @param isUpdate boolean indicator if update
     * @param holdingAdjustmentDocument the Holding Adjustment Document for which we compute the transaction line related tax lots
     * @param transLine the transaction line for which we update the tax lots
     * @param isSource boolean indicator if the transaction lines are source
     */
    public void updateTransactionLineTaxLotsByTransactionAmount(boolean isUpdate, HoldingAdjustmentDocument holdingAdjustmentDocument, EndowmentTransactionLine transLine, boolean isSource);    
}
