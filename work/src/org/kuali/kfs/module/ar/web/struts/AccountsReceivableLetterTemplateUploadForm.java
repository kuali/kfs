/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.web.struts;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.ar.businessobject.options.AgencyLetterTemplateValuesFinder;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class is form class for AccountsReceivableLetter Template Upload.
 */
public class AccountsReceivableLetterTemplateUploadForm extends KualiForm {
    private FormFile uploadedFile;
    private boolean active;
    private boolean accessRestrictedInd;
    private String letterTemplateCode;
    private String filePath;

    private AgencyLetterTemplateValuesFinder valuesFinder = new AgencyLetterTemplateValuesFinder();
    private List letterTemplateList = valuesFinder.getKeyValues();

    /**
     * Constructs a AccountsReceivableLetterTemplateUploadForm.java.
     *
     * @param document
     * @param uploadedFile
     */
    public AccountsReceivableLetterTemplateUploadForm() {
        super();

    }

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
     * Gets the accessRestrictedInd attribute.
     *
     * @return Returns the accessRestrictedInd.
     */
    public boolean isAccessRestrictedInd() {
        return accessRestrictedInd;
    }

    /**
     * Sets the accessRestrictedInd attribute value.
     *
     * @param accessRestrictedInd The accessRestrictedInd to set.
     */
    public void setAccessRestrictedInd(boolean accessRestrictedInd) {
        this.accessRestrictedInd = accessRestrictedInd;
    }

    /**
     * Gets the letterTemplateCode attribute.
     *
     * @return Returns the letterTemplateCode.
     */
    public String getLetterTemplateCode() {
        return letterTemplateCode;
    }

    /**
     * Sets the letterTemplateCode attribute value.
     *
     * @param letterTemplateCode The letterTemplateCode to set.
     */
    public void setLetterTemplateCode(String letterTemplateCode) {
        this.letterTemplateCode = letterTemplateCode;
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
     * Sets the uploadedFile attribute value.
     *
     * @param uploadedFile The uploadedFile to set.
     */
    public void setUploadedFile(FormFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     * Gets the letterTemplateList attribute.
     *
     * @return Returns the letterTemplateList.
     */
    public List getLetterTemplateList() {
        return letterTemplateList;
    }
}
