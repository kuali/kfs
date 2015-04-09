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
package org.kuali.kfs.sys.web.struts;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Struts action form for the batch upload screen.
 */
public class KualiBatchInputFileForm extends KualiForm {
    private FormFile uploadFile;
    private BatchUpload batchUpload;
    private List<KeyValue> userFiles;
    private String url;

    //getterURL pull system parameter used method get  parameter.evaluator


    private String titleKey;

    /**
     * Constructs a KualiBatchInputFileForm.java.
     */
    public KualiBatchInputFileForm() {
        super();
        this.batchUpload = new BatchUpload();
    }

    /**
     * Gets the batchUpload attribute.
     */
    public BatchUpload getBatchUpload() {
        return batchUpload;
    }

    /**
     * Sets the batchUpload attribute value.
     */
    public void setBatchUpload(BatchUpload batchUpload) {
        this.batchUpload = batchUpload;
    }

    /**
     * Gets the uploadFile attribute.
     */
    public FormFile getUploadFile() {
        return uploadFile;
    }

    /**
     * Sets the uploadFile attribute value.
     */
    public void setUploadFile(FormFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    /**
     * Gets the userFiles attribute.
     */
    public List<KeyValue> getUserFiles() {
        return userFiles;
    }

    /**
     * Sets the userFiles attribute value.
     */
    public void setUserFiles(List<KeyValue> userFiles) {
        this.userFiles = userFiles;
    }

    /**
     * Gets the titleKey attribute.
     */
    public String getTitleKey() {
        return titleKey;
    }

    /**
     * Sets the titleKey attribute value.
     */
    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    /**
     * Gets the url attribute.
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url attribute value.
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void customInitMaxUploadSizes() {
        addMaxUploadSize (CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, "Batch", "MAX_FILE_SIZE_UPLOAD"));
    }

}
