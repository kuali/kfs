/*
 * Copyright 2008 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArConstants.OrganizationAccountingDefaultErrors;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

public class CustomerInvoiceReceivableChartOfAccountsCodeValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        if (StringUtils.isEmpty(customerInvoiceDocument.getPaymentChartOfAccountsCode())) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, OrganizationAccountingDefaultErrors.ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED);
            return false;
        }
        else {
            customerInvoiceDocument.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS);
            if (ObjectUtils.isNull(customerInvoiceDocument.getPaymentChartOfAccounts())) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.PAYMENT_CHART_OF_ACCOUNTS_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE);
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
