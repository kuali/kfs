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
package org.kuali.kfs.module.cg.web.struts;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.cg.businessobject.options.AgencyInvoiceTemplateValuesFinder;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * This class represents the form for the ContractsGrantsInvoiceTemplateDocument.
 */
public class ContractsGrantsInvoiceTemplateUploadForm extends KualiForm {

    private FormFile uploadedFile;
    private boolean active;
    private boolean accessRestricted;
    private String invoiceTemplateCode;
    private String filePath;

    /**
     * Gets the filePath attribute.
     * 
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the filePath attribute value.
     * 
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private AgencyInvoiceTemplateValuesFinder valuesFinder = new AgencyInvoiceTemplateValuesFinder();
    private List invoiceTemplateList = valuesFinder.getKeyValues();

    /**
     * Gets the invoiceTemplateList attribute.
     * 
     * @return Returns the invoiceTemplateList.
     */
    public List getInvoiceTemplateList() {
        return invoiceTemplateList;
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
     * Constructs a ContractsGrantsInvoiceTemplateUploadForm.java.
     * 
     * @param document
     * @param uploadedFile
     */
    public ContractsGrantsInvoiceTemplateUploadForm() {
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
