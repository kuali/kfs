/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.web.struts.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.upload.FormFile;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.bo.BatchUpload;

/**
 * This class is the form used for the batch upload for file sets
 */
public class KualiBatchInputFileSetForm extends KualiForm {
    private Map<String, FormFile> uploadedFiles;

    private BatchUpload batchUpload;
    private List<KeyLabelPair> fileUserIdentifiers;

    private String titleKey;
    private BatchInputFileSetType batchInputFileSetType;
    private boolean supressDoneFileCreation;

    private String downloadFileType;
    private List<KeyLabelPair> fileTypes;

    /**
     * Constructs a KualiBatchInputFileForm.java.
     */
    public KualiBatchInputFileSetForm() {
        super();
        this.batchUpload = new BatchUpload();
        this.uploadedFiles = new HashMap<String, FormFile>();
        this.supressDoneFileCreation = false;
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
    public List<KeyLabelPair> getFileUserIdentifiers() {
        return fileUserIdentifiers;
    }

    /**
     * Sets the userFiles attribute value.
     */
    public void setFileUserIdentifiers(List<KeyLabelPair> fileUserIdentifiers) {
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
     * Gets the supressDoneFileCreation attribute.
     * 
     * @return Returns the supressDoneFileCreation.
     */
    public boolean isSupressDoneFileCreation() {
        return supressDoneFileCreation;
    }

    /**
     * Sets the supressDoneFileCreation attribute value.
     * 
     * @param supressDoneFileCreation The supressDoneFileCreation to set.
     */
    public void setSupressDoneFileCreation(boolean supressDoneFileCreation) {
        this.supressDoneFileCreation = supressDoneFileCreation;
    }

    /**
     * Gets the fileAliases attribute.
     * 
     * @return Returns the fileAliases.
     */
    public List<KeyLabelPair> getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the fileAliases attribute value.
     * 
     * @param fileAliases The fileAliases to set.
     */
    public void setFileTypes(List<KeyLabelPair> fileAliases) {
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
