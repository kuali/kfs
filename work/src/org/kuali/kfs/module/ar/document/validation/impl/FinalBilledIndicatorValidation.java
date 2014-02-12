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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.FinalBilledIndicatorDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is Validation class for FinalBilledIndicator.
 */
public class FinalBilledIndicatorValidation {

    private static final String INVOICE_NOT_FINALIZED_ERROR_MESSAGE = "The Invoice is not Final.";
    private static final String INVOICE_NOT_FINAL_ERROR_MESSAGE = "The Invoice is not marked as Final Bill.";
    private static final String INVALID_INVOICE_ERROR_MESSAGE = "Not a valid Contracts and Grants Invoice document number.";
    private static final String NO_ENTRY_ERROR_MESSAGE = "Please enter a Contracts and Grants Invoice document number.";
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalBilledIndicatorValidation.class);
    /**
     * This method validates the Document for valid invoices for reversal.
     *
     * @param document
     * @return
     */
    public static boolean validateDocument(Document document) {
        boolean valid = true;
        if (((FinalBilledIndicatorDocument) document).getInvoiceEntries() == null || ((FinalBilledIndicatorDocument) document).getInvoiceEntries().size() == 0) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, NO_ENTRY_ERROR_MESSAGE);
            valid = false;
        }
        else {
            Iterator<FinalBilledIndicatorEntry> iterator = ((FinalBilledIndicatorDocument) document).getInvoiceEntries().iterator();
            while (iterator.hasNext()) {
                valid &= validateEntry(iterator.next());
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
        if (entry == null || entry.getInvoiceDocumentNumber() == null) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, NO_ENTRY_ERROR_MESSAGE);
            return false;
        }

        return validCGINValidation(entry);
    }

    /**
     * Validates if each entry is a valid CGIN document
     *
     * @param entry
     * @return
     */
    public static boolean validCGINValidation(FinalBilledIndicatorEntry entry) {
        String docNumber = entry.getInvoiceDocumentNumber();

        // KFSMI-7919: invalid document number if not numeric
        if (!StringUtils.isNumeric(docNumber)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, INVALID_INVOICE_ERROR_MESSAGE);
            return false;
        }

        Document testDocument;
        try {
            if (SpringContext.getBean(DocumentService.class).documentExists(docNumber)) {
                testDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docNumber);
                if (!(testDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(KFSConstants.ContractsGrantsModuleDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE))) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, INVALID_INVOICE_ERROR_MESSAGE);
                    return false;
                }
                return entryValidations((ContractsGrantsInvoiceDocument) testDocument);
            }
            else {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, INVALID_INVOICE_ERROR_MESSAGE);
                return false;
            }
        }
        catch (WorkflowException ex) {
            LOG.error("problem during FinalBilledIndicatorValidation.validCGINValidation()", ex);
        }
        return true;
    }

    /**
     * validates if each invoice is final Billed and in final Status.
     *
     * @param document
     * @return
     */
    public static boolean entryValidations(ContractsGrantsInvoiceDocument document) {
        if (!DocumentStatus.FINAL.equals(document.getDocumentHeader().getWorkflowDocument().getStatus())) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, INVOICE_NOT_FINALIZED_ERROR_MESSAGE);
            return false;
        }
        if (!document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.FINAL_BILLED_INDICATOR_ENTRIES_PROPERTY_PATH, ArKeyConstants.FINAL_BILLED_INDICATOR_EDOC_ERROR_KEY, INVOICE_NOT_FINAL_ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
