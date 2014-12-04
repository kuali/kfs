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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.LiabilityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;


public class LiabilityIncreaseDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#updateTransactionLineTaxLots(boolean, boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {

        LiabilityDocumentService taxLotsService = SpringContext.getBean(LiabilityDocumentService.class);
        LiabilityIncreaseDocument liabilityIncreaseDocument = (LiabilityIncreaseDocument) etlDocument;
        taxLotsService.updateLiabilityIncreaseTransactionLineTaxLots(isSource, liabilityIncreaseDocument, transLine);

    }


    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#getRefreshTaxLotsOnSaveOrSubmit()
     */
    @Override
    protected boolean getRefreshTaxLotsOnSaveOrSubmit() {
        return true;
    }

}
