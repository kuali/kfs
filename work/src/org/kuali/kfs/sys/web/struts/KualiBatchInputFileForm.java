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

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.bo.BatchUpload;

/**
 * Struts action form for the batch upload screen.
 */
public class KualiBatchInputFileForm extends KualiForm {
    private FormFile uploadFile;
    private BatchUpload batchUpload;
    private List<KeyLabelPair> userFiles;

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
    public List<KeyLabelPair> getUserFiles() {
        return userFiles;
    }

    /**
     * Sets the userFiles attribute value.
     */
    public void setUserFiles(List<KeyLabelPair> userFiles) {
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


}
