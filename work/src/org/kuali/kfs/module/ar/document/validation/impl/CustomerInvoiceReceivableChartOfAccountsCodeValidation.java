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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.ArKeyConstants.OrganizationAccountingDefaultErrors;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceReceivableChartOfAccountsCodeValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        if (StringUtils.isEmpty(customerInvoiceDocument.getPaymentChartOfAccountsCode())) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, OrganizationAccountingDefaultErrors.ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;
        }
        else {
            customerInvoiceDocument.refreshReferenceObject(ArPropertyConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS);
            if (ObjectUtils.isNull(customerInvoiceDocument.getPaymentChartOfAccounts())) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE);
                return false;
            }
        }
        return true;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

}
