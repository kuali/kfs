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
