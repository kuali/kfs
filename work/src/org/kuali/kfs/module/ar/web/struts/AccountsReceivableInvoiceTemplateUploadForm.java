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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.businessobject.TemplateBase;



/**
 * This class represents the form for the InvoiceTemplate.
 */
public class AccountsReceivableInvoiceTemplateUploadForm extends AccountsReceivableTemplateUploadForm {

    private String invoiceTemplateCode;

    public AccountsReceivableInvoiceTemplateUploadForm() {
        setHtmlFormAction(ArConstants.Actions.ACCOUNTS_RECEIVABLE_INVOICE_TEMPLATE_UPLOAD);
    }

    /**
     * Gets the invoiceTemplateCode attribute.
     *
     * @return Returns the invoiceTemplateCode.
     */
    public String getInvoiceTemplateCode() {
        return invoiceTemplateCode;
    }

    /**
     * Sets the invoiceTemplateCode attribute value.
     *
     * @param invoiceTemplateCode The invoiceTemplateCode to set.
     */
    public void setInvoiceTemplateCode(String invoiceTemplateCode) {
        this.invoiceTemplateCode = invoiceTemplateCode;
    }

    @Override
    public String getTemplateCode() {
        return invoiceTemplateCode;
    }

    @Override
    public Class<? extends TemplateBase> getTemplateClass() {
        return InvoiceTemplate.class;
    }

    @Override
    public String getErrorPropertyName() {
        return ArConstants.INVOICE_TEMPLATE_UPLOAD;
    }

    @Override
    public String getTemplateType() {
        return ArConstants.INVOICE_TEMPLATE_TYPE;
    }

    @Override
    public String getNewFileNamePrefix() {
        return ArConstants.INVOICE_TEMPLATE_NEW_FILE_NAME_PREFIX;
    }

}
