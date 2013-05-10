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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.rice.kew.api.exception.WorkflowException;


public abstract class EndowmentTaxLotLinesDocumentBase extends EndowmentSecurityDetailsDocumentBase implements EndowmentTaxLotLinesDocument {

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument#getTaxLotLinesNumber()
     */
    public int getTaxLotLinesNumber() {
        int taxLotLinesNbr = 0;

        for (int i = 0; i < getTargetTransactionLines().size(); i++) {
            EndowmentTransactionLine transactionLine = (EndowmentTransactionLine) getTargetTransactionLines().get(i);
            taxLotLinesNbr += transactionLine.getTaxLotLines().size();
        }

        for (int i = 0; i < getSourceTransactionLines().size(); i++) {
            EndowmentTransactionLine transactionLine = (EndowmentTransactionLine) getSourceTransactionLines().get(i);
            taxLotLinesNbr += transactionLine.getTaxLotLines().size();
        }

        return taxLotLinesNbr;
    }


    /**
     * @see org.kuali.kfs.sys.document.Correctable#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException, IllegalStateException {
        super.toErrorCorrection();

        // Negate the Taxlot lines Amount, Units, Short term gain & long term gain values.
        List<EndowmentTransactionLine> lines = new ArrayList<EndowmentTransactionLine>();
        lines.addAll(sourceTransactionLines);
        lines.addAll(targetTransactionLines);

        for (EndowmentTransactionLine line : lines) {
            for (EndowmentTransactionTaxLotLine taxLotLine : line.getTaxLotLines()) {
                taxLotLine.setLotHoldingCost(taxLotLine.getLotHoldingCost().negate());
                taxLotLine.setLotUnits(taxLotLine.getLotUnits().negate());
                if (null != taxLotLine.getLotLongTermGainLoss() && 0 != taxLotLine.getLotLongTermGainLoss().intValue())
                    taxLotLine.setLotLongTermGainLoss(taxLotLine.getLotLongTermGainLoss().negate());
                if (null != taxLotLine.getLotShortTermGainLoss() && 0 != taxLotLine.getLotShortTermGainLoss().intValue())
                    taxLotLine.setLotShortTermGainLoss(taxLotLine.getLotShortTermGainLoss().negate());
            }
        }
    }
}
