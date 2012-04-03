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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerInvoiceWriteoffChartCodeHasCorrespondingWriteoffObjectCodeValidation extends GenericValidation {

    protected CustomerInvoiceDetail customerInvoiceDetail;
    protected CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument;
    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        String writeoffObjectCode = parameterService.getSubParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_OBJECT_CODE_BY_CHART, customerInvoiceDetail.getChartOfAccountsCode());
        if (StringUtils.isEmpty(writeoffObjectCode)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceWriteoffDocumentFields.CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST,customerInvoiceDetail.getChartOfAccountsCode());
            return false;
        }
        return true;
    }

    public void setCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        this.customerInvoiceWriteoffDocument = customerInvoiceWriteoffDocument;
    }
    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
