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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.SecurityTransferDocument;
import org.kuali.kfs.module.endow.document.service.UpdateSecurityTransferTargetTaxLotsService;
import org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService;
import org.kuali.kfs.sys.context.SpringContext;

public class SecurityTransferDocumentAction extends EndowmentTaxLotLinesDocumentActionBase {

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentActionBase#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected void updateTransactionLineTaxLots(boolean isSource, EndowmentTransactionLinesDocument etlDocument, EndowmentTransactionLine transLine) {
        SecurityTransferDocument securityTransferDocument = (SecurityTransferDocument) etlDocument;

        if (transLine instanceof EndowmentSourceTransactionLine) {
            UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService taxLotsService = SpringContext.getBean(UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService.class);
            taxLotsService.updateTransactionLineTaxLots(false, securityTransferDocument, transLine);
        }

        if (transLine instanceof EndowmentTargetTransactionLine) {
            UpdateSecurityTransferTargetTaxLotsService taxLotsService = SpringContext.getBean(UpdateSecurityTransferTargetTaxLotsService.class);
            taxLotsService.updateTransactionLineTaxLots(securityTransferDocument, transLine);
        }

    }

}
