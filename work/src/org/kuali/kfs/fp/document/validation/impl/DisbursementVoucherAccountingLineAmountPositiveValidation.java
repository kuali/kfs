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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.List;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAmountPositiveValidation;

public class DisbursementVoucherAccountingLineAmountPositiveValidation extends AccountingLineAmountPositiveValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineAmountPositiveValidation.class);

    /**
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAmountPositiveValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;
        
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

        return isValid;
    }
}
