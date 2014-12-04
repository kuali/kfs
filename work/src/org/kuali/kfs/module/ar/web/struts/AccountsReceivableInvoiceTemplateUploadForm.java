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
