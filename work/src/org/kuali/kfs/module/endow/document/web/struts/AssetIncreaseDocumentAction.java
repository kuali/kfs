/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.web.struts;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.kns.util.KualiInteger;


public class AssetIncreaseDocumentAction extends EndowmentTransactionLinesDocumentActionBase {

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#updateTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, int)
     */
    @Override
    protected void updateTaxLots(boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {

        // create and set a new tax lot line
        EndowmentTransactionTaxLotLine taxLotLine = new EndowmentTransactionTaxLotLine();
        taxLotLine.setDocumentNumber(etlDocument.getDocumentNumber());
        taxLotLine.setDocumentLineNumber(new KualiInteger(transLine.getTransactionLineNumber()));
        taxLotLine.setTransactionHoldingLotNumber(new KualiInteger(1));
        taxLotLine.setLotUnits(transLine.getTransactionUnits());
        taxLotLine.setLotHoldingCost(transLine.getTransactionAmount());

        transLine.getTaxLotLines().clear();
        transLine.getTaxLotLines().add(taxLotLine);

    }

}
