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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Business rule(s) applicable to Payment Request documents.
 */
public class ElectronicInvoiceRejectDocumentRule extends DocumentRuleBase {

    protected static KualiDecimal zero = KualiDecimal.ZERO;
    protected static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        return processBusinessRules(document);
    }

    protected boolean processBusinessRules(Document document){
        boolean isValid = true;
        
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) document;

        // check to see if the document is being researched
        if (eirDocument.isInvoiceResearchIndicator()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapConstants.REJECT_DOCUMENT_RESEARCH_INCOMPETE);
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
