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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.module.ld.document.LaborJournalVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation which checks that the offset type for the document is a valid active, current accounting document type
 */
public class LaborJournalVoucherOffsetTypeCodeValidation extends GenericValidation {
    private LaborJournalVoucherDocument laborJournalVoucherDocumentForValidation;
    private FinancialSystemDocumentTypeService financialSystemDocumentTypeService;

    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        final String offsetTypeCode = getLaborJournalVoucherDocumentForValidation().getOffsetTypeCode();
        if (!getFinancialSystemDocumentTypeService().isCurrentActiveAccountingDocumentType(offsetTypeCode)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.OFFSET_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_LABOR_JOURNAL_VOUCHER_OFFSET_TYPE_CODE_NON_ACTIVE_CURRENT_ACCOUNTING_DOCUMENT_TYPE, offsetTypeCode);
            return false;
        }
        return true;
    }

    /**
     * Gets the laborJournalVoucherDocumentForValidation attribute. 
     * @return Returns the laborJournalVoucherDocumentForValidation.
     */
    public LaborJournalVoucherDocument getLaborJournalVoucherDocumentForValidation() {
        return laborJournalVoucherDocumentForValidation;
    }

    /**
     * Sets the laborJournalVoucherDocumentForValidation attribute value.
     * @param laborJournalVoucherDocumentForValidation The laborJournalVoucherDocumentForValidation to set.
     */
    public void setLaborJournalVoucherDocumentForValidation(LaborJournalVoucherDocument laborJournalVoucherDocumentForValidation) {
        this.laborJournalVoucherDocumentForValidation = laborJournalVoucherDocumentForValidation;
    }

    /**
     * Gets the financialSystemDocumentTypeService attribute. 
     * @return Returns the financialSystemDocumentTypeService.
     */
    public FinancialSystemDocumentTypeService getFinancialSystemDocumentTypeService() {
        return financialSystemDocumentTypeService;
    }

    /**
     * Sets the financialSystemDocumentTypeService attribute value.
     * @param financialSystemDocumentTypeService The financialSystemDocumentTypeService to set.
     */
    public void setFinancialSystemDocumentTypeService(FinancialSystemDocumentTypeService financialSystemDocumentTypeService) {
        this.financialSystemDocumentTypeService = financialSystemDocumentTypeService;
    }
}
