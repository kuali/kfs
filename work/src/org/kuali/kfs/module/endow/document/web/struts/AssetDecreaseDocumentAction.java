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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService;
import org.kuali.kfs.sys.context.SpringContext;

public class AssetDecreaseDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#updateTransactionLineTaxLots(boolean,
     *      boolean, org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isUpdate, boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {
        UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) etlDocument;
        taxLotsService.updateTransactionLineTaxLots(isUpdate, assetDecreaseDocument, transLine);

    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTaxLotLinesDocumentActionBase#getRefreshTaxLotsOnSaveOrSubmit()
     */
    @Override
    protected boolean getRefreshTaxLotsOnSaveOrSubmit() {
        return false;
    }

}
