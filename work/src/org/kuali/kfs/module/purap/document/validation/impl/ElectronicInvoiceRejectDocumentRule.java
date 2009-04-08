/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Business rule(s) applicable to Payment Request documents.
 */
public class ElectronicInvoiceRejectDocumentRule extends DocumentRuleBase {

    private static KualiDecimal zero = KualiDecimal.ZERO;
    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) document;

        // check to see if the document is being researched
        if (eirDocument.isInvoiceResearchIndicator()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapConstants.REJECT_DOCUMENT_RESEARCH_INCOMPETE);
            isValid = false;
        }
        
        if (!eirDocument.isDocumentCreationInProgress()){
            isValid = isValid && SpringContext.getBean(ElectronicInvoiceHelperService.class).doMatchingProcess(eirDocument);
            if (isValid){
                SpringContext.getBean(ElectronicInvoiceHelperService.class).createPaymentRequest(eirDocument);
            }
        }

        return isValid;
    }


}
