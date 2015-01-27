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

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;

/**
 * Contracts & Grants Invoice Document Presentation Controller class.
 */
public class ContractsGrantsInvoiceDocumentPresentationController extends CustomerInvoiceDocumentPresentationController {

    /**
     * @see org.kuali.kfs.module.ar.document.authorization.ContractsGrantsInvoiceDocumentPresentationController#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        if (StringUtils.isNotBlank(document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId())) {
            return false;
        }
        if (((ContractsGrantsInvoiceDocument) document).isInvoiceReversal()) {
            return false;
        }
        else {
            // a normal invoice can only be error corrected if document is in a final state
            // and no amounts have been applied (excluding discounts)
            return isDocFinalWithNoAppliedAmountsExceptDiscounts((ContractsGrantsInvoiceDocument) document);
        }
    }

    public boolean canProrate(ContractsGrantsInvoiceDocument document) {
        if (canEdit(document) &&
                KRADConstants.YES_INDICATOR_VALUE.equals(SpringContext.getBean(ParameterService.class).getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.CG_PRORATE_BILL_IND)) &&
                !StringUtils.equals(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE, document.getInvoiceGeneralDetail().getBillingFrequencyCode()) &&
                !StringUtils.equals(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE, document.getInvoiceGeneralDetail().getBillingFrequencyCode())) {
            return true;
        }
        return false;
    }

    public boolean canModifyTransmissionDate(ContractsGrantsInvoiceDocument document) {
        if (document.hasInvoiceBeenCorrected()) {
            return false;
        }

        if (document.isInvoiceReversal()) {
            return false;
        }

        if (StringUtils.equals(ArConstants.LOC_BILLING_SCHEDULE_CODE, document.getInvoiceGeneralDetail().getBillingFrequencyCode())) {
            return false;
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isProcessed() || workflowDocument.isFinal()));
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (canModifyTransmissionDate((ContractsGrantsInvoiceDocument) document)) {
            editModes.add(ArAuthorizationConstants.ContractsGrantsInvoiceDocumentEditMode.MODIFY_TRANSMISSION_DATE);
        }

        return editModes;
    }

    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);
        documentActions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        return documentActions;
    }

}
