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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.List;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAmountPositiveValidation;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;


public class DisbursementVoucherAccountingLineAmountPositiveValidation extends AccountingLineAmountPositiveValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineAmountPositiveValidation.class);

    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAmountPositiveValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        if (!parameterService.getParameterValueAsBoolean(DisbursementVoucherDocument.class, KFSParameterKeyConstants.FpParameterConstants.NEGATIVE_ACCOUNTING_LINES_IND)) {

            DisbursementVoucherDocument document = (DisbursementVoucherDocument) this.getAccountingDocumentForValidation();
            DisbursementVoucherNonResidentAlienTax nonResidentAlienTax = document.getDvNonResidentAlienTax();

            // tax accounting lines can have negative amounts
            if (nonResidentAlienTax != null) {
                List<String> taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(nonResidentAlienTax.getFinancialDocumentAccountingLineText());

                if (taxLineNumbers.contains(this.getAccountingLineForValidation().getSequenceNumber())) {
                    return true;
                }
            }
            isValid = super.validate(event);
        }

        return isValid;

    }


    /**
    * Sets the parameterService attribute value.
    * @param parameterService The parameterService to set.
    */
   public void setParameterService(ParameterService parameterService) {
       this.parameterService = parameterService;

   }
}
