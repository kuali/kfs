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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.batch.BatchInputFileSetType;
import org.kuali.kfs.sys.businessobject.BatchUpload;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class is the form used for the batch upload for file sets
 */
public class KualiBatchInputFileSetForm extends KualiForm {
    private Map<String, FormFile> uploadedFiles;

    private BatchUpload batchUpload;
    private List<KeyValue> fileUserIdentifiers;

    private String titleKey;
    private BatchInputFileSetType batchInputFileSetType;

    private String downloadFileType;
    private List<KeyValue> fileTypes;

    /**
     * Constructs a KualiBatchInputFileForm.java.
     */
    public KualiBatchInputFileSetForm() {
        super();
        this.batchUpload = new BatchUpload();
        this.uploadedFiles = new HashMap<String, FormFile>();
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
     * Gets the userFiles attribute.
     */
    public List<KeyValue> getFileUserIdentifiers() {
        return fileUserIdentifiers;
    }

    /**
     * Sets the userFiles attribute value.
     */
    public void setFileUserIdentifiers(List<KeyValue> fileUserIdentifiers) {
        this.fileUserIdentifiers = fileUserIdentifiers;
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
     * Gets the batchInputFileSetType attribute.
     * 
     * @return Returns the batchInputFileSetType.
     */
    public BatchInputFileSetType getBatchInputFileSetType() {
        return batchInputFileSetType;
    }

    /**
     * Sets the batchInputFileSetType attribute value.
     * 
     * @param batchInputFileSetType The batchInputFileSetType to set.
     */
    public void setBatchInputFileSetType(BatchInputFileSetType batchInputFileSetType) {
        this.batchInputFileSetType = batchInputFileSetType;
    }

    /**
     * Gets the uploadedFiles attribute.
     * 
     * @return Returns the uploadedFiles.
     */
    public Map<String, FormFile> getUploadedFiles() {
        return uploadedFiles;
    }

    /**
     * Sets the uploadedFiles attribute value.
     * 
     * @param uploadedFiles The uploadedFiles to set.
     */
    public void setUploadedFiles(Map<String, FormFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    /**
     * Gets the fileAliases attribute.
     * 
     * @return Returns the fileAliases.
     */
    public List<KeyValue> getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the fileAliases attribute value.
     * 
     * @param fileAliases The fileAliases to set.
     */
    public void setFileTypes(List<KeyValue> fileAliases) {
        this.fileTypes = fileAliases;
    }

    /**
     * Gets the downloadFileAlias attribute.
     * 
     * @return Returns the downloadFileAlias.
     */
    public String getDownloadFileType() {
        return downloadFileType;
    }

    /**
     * Sets the downloadFileAlias attribute value.
     * 
     * @param downloadFileAlias The downloadFileAlias to set.
     */
    public void setDownloadFileType(String downloadFileAlias) {
        this.downloadFileType = downloadFileAlias;
    }
}
