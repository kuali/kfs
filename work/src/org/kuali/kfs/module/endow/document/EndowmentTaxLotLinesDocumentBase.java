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
