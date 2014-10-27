/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants Invoice Document Presentation Controller class.
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

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!(((ContractsGrantsInvoiceDocument) document).isInvoiceReversal()) &&
                !(((ContractsGrantsInvoiceDocument) document).hasInvoiceBeenCorrected()) &&
                ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            editModes.add(ArAuthorizationConstants.ContractsGrantsInvoiceDocumentEditMode.MODIFY_TRANSMISSION_DATE);
        }

        return editModes;
    }

}
