/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2015 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that all invoice detail account object codes have object codes set
 */
public class ContractsGrantsInvoiceDocumentInvoiceDetailAccountObjectCodeFinancialObjectCodesValidation extends GenericValidation {

    /**
     * Goes through each invoice detail account object code on the document, verifying that the object code is set
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        Set<String> reportedOnChartCategories = new HashSet<>();

        ContractsGrantsInvoiceDocument contractsGrantsInvoice = (ContractsGrantsInvoiceDocument)event.getDocument();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : contractsGrantsInvoice.getInvoiceDetailAccountObjectCodes()) {
            if (StringUtils.isBlank(invoiceDetailAccountObjectCode.getFinancialObjectCode())) {
                final String key = buildChartCategoryKey(invoiceDetailAccountObjectCode);
                if (!reportedOnChartCategories.contains(key)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+ArPropertyConstants.INVOICE_DETAIL+"s", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_INVOICE_DETAIL_ACCOUNT_OBJECT_CODE_FINANCIAL_OBJECT_CODE_REQUIRED, new String[] { invoiceDetailAccountObjectCode.getChartOfAccountsCode(), invoiceDetailAccountObjectCode.getCategoryCode()});
                    success = false;
                    reportedOnChartCategories.add(key);
                }
            }
        }

        return success;
    }

    /**
     * Builds a key out of the chart and category code of the given invoice detail account object code
     * @param invoiceDetailAccountObjectCode the invoice detail account object code to build a key for
     * @return the built key
     */
    protected String buildChartCategoryKey(InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode) {
        StringBuilder s = new StringBuilder();
        s.append(invoiceDetailAccountObjectCode.getChartOfAccountsCode());
        s.append('-');
        s.append(invoiceDetailAccountObjectCode.getCategoryCode());
        return s.toString();
    }

}
