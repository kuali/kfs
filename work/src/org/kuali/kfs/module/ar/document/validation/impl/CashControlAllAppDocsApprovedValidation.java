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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlAllAppDocsApprovedValidation extends GenericValidation {

    
    private CashControlDocument cashControlDocument;
    private CashControlDetail cashControlDetail;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean allAppDocsApproved = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
        WorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

        if (!(workflowDocument.isApproved() || workflowDocument.isFinal())) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(ArPropertyConstants.CashControlDocumentFields.APPLICATION_DOC_STATUS, ArKeyConstants.ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED);
            allAppDocsApproved = false;
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return allAppDocsApproved;
    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    public CashControlDetail getCashControlDetail() {
        return cashControlDetail;
    }

    public void setCashControlDetail(CashControlDetail cashControlDetail) {
        this.cashControlDetail = cashControlDetail;
    }
}
