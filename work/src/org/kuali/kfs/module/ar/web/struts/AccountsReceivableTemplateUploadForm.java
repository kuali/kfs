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
import org.kuali.kfs.module.ar.businessobject.TemplateBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * Base class for the Template upload forms.
 */
public abstract class AccountsReceivableTemplateUploadForm extends KualiForm {

    protected FormFile uploadedFile;
    protected boolean active;
    protected boolean accessRestricted;
    protected String fileName;
    private String htmlFormAction;

    /**
     * Gets the uploadedFile attribute.
     *
     * @return Returns the uploadedFile.
     */
    public FormFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * Sets the uploadedFile attribute value.
     *
     * @param uploadedFile The uploadedFile to set.
     */
    public void setUploadedFile(FormFile uploadedFile) {
        this.uploadedFile = uploadedFile;
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

    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }

    /**
     * Overridden by child classes to return their specific template code.
     *
     * @return
     */
    public abstract String getTemplateCode();

    /**
     * Overridden by child classes to return their specific template.
     *
     * @return
     */
    public abstract Class<? extends TemplateBase> getTemplateClass();

    /**
     * Overridden by child classes to return their specific error property name.
     *
     * @return
     */
    public abstract String getErrorPropertyName();

    /**
     * Overridden by child classes to return their specific template type.
     *
     * @return
     */
    public abstract String getTemplateType();

    /**
     * Overridden by child classes to return their specific prefix for the new file name
     * used to store the template.
     *
     * @return
     */
    public abstract String getNewFileNamePrefix();

}
