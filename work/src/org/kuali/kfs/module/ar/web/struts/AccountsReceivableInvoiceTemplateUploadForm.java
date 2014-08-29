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

import org.apache.struts.upload.FormFile;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * This class represents the form for the InvoiceTemplate.
 */
public class AccountsReceivableInvoiceTemplateUploadForm extends KualiForm {

    private FormFile uploadedFile;
    private boolean active;
    private boolean accessRestricted;
    private String invoiceTemplateCode;
    private String fileName;

    /**
     * Gets the fileName attribute.
     *
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName attribute value.
     *
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the accessRestricted attribute.
     *
     * @return Returns the accessRestricted.
     */
    public boolean isAccessRestricted() {
        return accessRestricted;
    }

    /**
     * Sets the accessRestricted attribute value.
     *
     * @param accessRestricted The accessRestricted to set.
     */
    public void setAccessRestricted(boolean accessRestricted) {
        this.accessRestricted = accessRestricted;
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

    /**
     * Gets the uploadedFile attribute.
     *
     * @return Returns the uploadedFile.
     */
    public FormFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Constructs a AccountsReceivableInvoiceTemplateUploadForm.java.
     *
     * @param document
     * @param uploadedFile
     */
    public AccountsReceivableInvoiceTemplateUploadForm() {
        super();

    }

    /**
     * Sets the uploadedFile attribute value.
     *
     * @param uploadedFile The uploadedFile to set.
     */
    public void setUploadedFile(FormFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
