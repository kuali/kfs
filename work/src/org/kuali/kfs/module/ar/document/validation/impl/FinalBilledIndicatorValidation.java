/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.FinalBilledIndicatorDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is Validation class for FinalBilledIndicator.
 */
public class FinalBilledIndicatorValidation {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalBilledIndicatorValidation.class);

    /**
     * This method validates the Document for valid invoices for reversal.
     *
     * @param document
     * @return
     */
    public static boolean validateDocument(Document document) {
        boolean valid = true;

        List<FinalBilledIndicatorEntry> invoiceEntries = ((FinalBilledIndicatorDocument) document).getInvoiceEntries();
        if (CollectionUtils.isEmpty(invoiceEntries)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_NO_INVOICE);
            valid = false;
        } else {
            for (FinalBilledIndicatorEntry entry: invoiceEntries) {
                valid &= validateEntry(entry);
            }
        }
        return valid;
    }

    /**
     * This method validates whether the entry is null or not.
     *
     * @param entry
     * @return
     */
    public static boolean validateEntry(FinalBilledIndicatorEntry entry) {
        if (ObjectUtils.isNull(entry) || ObjectUtils.isNull(entry.getInvoiceDocumentNumber())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_NO_INVOICE);
            return false;
        }

        return validContractsGrantsInvoiceValidation(entry);
    }

    /**
     * Validates if each entry is a valid Contracts Grants Invoice document
     *
     * @param entry
     * @return
     */
    public static boolean validContractsGrantsInvoiceValidation(FinalBilledIndicatorEntry entry) {
        String docNumber = entry.getInvoiceDocumentNumber();

        Document testDocument;
        try {
            if (SpringContext.getBean(DocumentService.class).documentExists(docNumber)) {
                testDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docNumber);
                if (!(testDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE))) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_INVALID_INVOICE);
                    return false;
                }
                return entryValidations((ContractsGrantsInvoiceDocument) testDocument);
            }
            else {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_INVALID_INVOICE);
                return false;
            }
        }
        catch (WorkflowException ex) {
            LOG.error("problem during FinalBilledIndicatorValidation.validContractsGrantsInvoiceValidation()", ex);
            throw new RuntimeException("problem during FinalBilledIndicatorValidation.validContractsGrantsInvoiceValidation()", ex);
        }
    }

    /**
     * validates if each invoice is final Billed and in final Status.
     *
     * @param document
     * @return
     */
    public static boolean entryValidations(ContractsGrantsInvoiceDocument document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!workflowDocument.isFinal() && !workflowDocument.isProcessed()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_INVOICE_NOT_FINAL);
            return false;
        }
        if (!document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.ERROR_FINAL_BILLED_INDICATOR_INVOICE_NOT_MARKED_FINAL_BILL);
            return false;
        }
        return true;
    }

}
