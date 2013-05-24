/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Document rule class for Payment Application.
 */
public class PaymentApplicationDocumentRule extends GeneralLedgerPostingDocumentRuleBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentRule.class);

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument)document;
            // Validate the applied payments
            int appliedAmountIndex = 0;
            for(InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
                String fieldName = ArPropertyConstants.PaymentApplicationDocumentFields.AMOUNT_TO_BE_APPLIED_LINE_N;
                fieldName = StringUtils.replace(fieldName, "{0}", new Integer(appliedAmountIndex).toString());
                if(!PaymentApplicationDocumentRuleUtil.validateInvoicePaidApplied(invoicePaidApplied, fieldName, paymentApplicationDocument)) {
                    isValid = false;
                    LOG.info("One of the invoice paid applieds for the payment application document is not valid.");
                }
                appliedAmountIndex++;
            }

        // Validate the nonInvoiced payments
        for (NonInvoiced nonInvoiced : paymentApplicationDocument.getNonInvoiceds()) {
            try {
                if (!PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonInvoiced, paymentApplicationDocument, paymentApplicationDocument.getTotalFromControl())) {
                    isValid = false;
                    LOG.info("One of the non-invoiced lines on the payment application document is not valid.");
                }
            }
            catch (WorkflowException workflowException) {
                isValid = false;
                LOG.error("Workflow exception encountered when trying to validate non invoiced line of payment application document.", workflowException);
            }
        }

        // Validate non applied holdings
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateNonAppliedHolding(paymentApplicationDocument, paymentApplicationDocument.getTotalFromControl())) {
                isValid = false;
                LOG.info("The unapplied line on the payment application document is not valid.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to validate nonAppliedHolding attribute of payment application document.", workflowException);
        }

        // Validate that the cumulative amount applied doesn't exceed the amount owed.
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateCumulativeSumOfInvoicePaidAppliedDoesntExceedCashControlTotal(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total amount applied exceeds the total amount owed per the cash control document total amount.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        // Validate that the cumulative amount applied doesn't exceed the amount owed.
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateCumulativeSumOfInvoicePaidAppliedsIsGreaterThanOrEqualToZero(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total amount applied is less than zero.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        // Validate that the unapplied total doesn't exceed the cash control total
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateUnappliedAmountDoesntExceedCashControlTotal(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total unapplied amount exceeds the total amount owed per the cash control document total amount.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        // Validate that the unapplied total isn't less than zero
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateUnappliedAmountIsGreaterThanOrEqualToZero(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total unapplied amount is less than zero.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        // Validate that the non-invoiced total doesn't exceed the cash control total
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateNonInvoicedAmountDoesntExceedCashControlTotal(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total non-invoiced amount exceeds the total amount owed per the cash control document total amount.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        // Validate that the non-invoiced total isn't less than zero
        try {
            if (!PaymentApplicationDocumentRuleUtil.validateNonInvoicedAmountIsGreaterThanOrEqualToZero(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The total unapplied amount is less than zero.");
            }
        }
        catch (WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {

        // if the super failed, don't even bother running these rules
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        if (!isValid) {
            return false;
        }

        MessageMap errorMap = GlobalVariables.getMessageMap();
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) document;

        // this rules is only applicable to CashControl generated Application document
        // dont let PayApp docs started from CashControl docs through if not all funds are applied
        if (paymentApplicationDocument.hasCashControlDetail()) {
            if (!KualiDecimal.ZERO.equals(paymentApplicationDocument.getUnallocatedBalance())) {
                isValid &= false;
                errorMap.putError(
                    KRADConstants.GLOBAL_ERRORS,
                    ArKeyConstants.PaymentApplicationDocumentErrors.FULL_AMOUNT_NOT_APPLIED);
                LOG.info("The payment application document was not fully applied.");
            }
        }


        return isValid;
    }

    /**
     * @param doc
     * @return true if the organization document number on the document header is not blank.
     */
    protected boolean containsCashControlDocument(PaymentApplicationDocument doc) {
        return (StringUtils.isNotBlank(doc.getDocumentHeader().getOrganizationDocumentNumber()));
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.krad.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        return isValid;
    }

}
