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

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.ar.businessobject.TemplateBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * Base class for the Template upload forms.
 */
public abstract class AccountsReceivableTemplateUploadForm extends KualiForm {

    protected FormFile uploadedFile;
    protected boolean active;
    protected boolean restrictUseByChartOrg;
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
     * Gets the restrictUseByChartOrg attribute.
     *
     * @return Returns the restrictUseByChartOrg.
     */
    public boolean isRestrictUseByChartOrg() {
        return restrictUseByChartOrg;
    }

    /**
     * Sets the restrictUseByChartOrg attribute value.
     *
     * @param restrictUseByChartOrg The restrictUseByChartOrg to set.
     */
    public void setRestrictUseByChartOrg(boolean restrictUseByChartOrg) {
        this.restrictUseByChartOrg = restrictUseByChartOrg;
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
