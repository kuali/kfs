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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {

        Set<String> editModes = super.getEditModes(document);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;

        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB);
        }

        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(ArAuthorizationConstants.SALES_TAX_ENABLED);
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON);
        }

        return editModes;
    }

    @Override
    public boolean canCancel(Document document) {
        return !isDocStatusCodeInitiated(document);
    }

    @Override
    public boolean canSave(Document document) {
        return !isDocStatusCodeInitiated(document);
    }

    /**
     * Returns true if the document passed in is in initiated status.
     */
    protected boolean isDocStatusCodeInitiated(Document document) {
        CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) document;
        return (StringUtils.equals(creditMemoDoc.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE));
    }

}
