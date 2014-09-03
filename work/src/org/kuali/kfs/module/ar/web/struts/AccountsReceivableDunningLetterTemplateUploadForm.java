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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.TemplateBase;


/**
 * This class is form class for Accounts Receivable Dunning Letter Template Upload.
 */
public class AccountsReceivableDunningLetterTemplateUploadForm extends AccountsReceivableTemplateUploadForm {

    private String dunningLetterTemplateCode;

    public AccountsReceivableDunningLetterTemplateUploadForm() {
        setHtmlFormAction(ArConstants.Actions.ACCOUNTS_RECEIVABLE_DUNNING_LETTER_TEMPLATE_UPLOAD);
    }

    /**
     * Gets the dunningLetterTemplateCode attribute.
     *
     * @return Returns the dunningLetterTemplateCode.
     */
    public String getDunningLetterTemplateCode() {
        return dunningLetterTemplateCode;
    }

    /**
     * Sets the dunningLetterTemplateCode attribute value.
     *
     * @param dunningLetterTemplateCode The dunningLetterTemplateCode to set.
     */
    public void setDunningLetterTemplateCode(String dunningLetterTemplateCode) {
        this.dunningLetterTemplateCode = dunningLetterTemplateCode;
    }

    @Override
    public String getTemplateCode() {
        return dunningLetterTemplateCode;
    }

    @Override
    public Class<? extends TemplateBase> getTemplateClass() {
        return DunningLetterTemplate.class;
    }

    @Override
    public String getErrorPropertyName() {
        return ArConstants.DUNNING_LETTER_TEMPLATE_UPLOAD;
    }

    @Override
    public String getTemplateType() {
        return ArConstants.DUNNING_LETTER_TEMPLATE_TYPE;
    }

    @Override
    public String getNewFileNamePrefix() {
        return ArConstants.DUNNING_LETTER_TEMPLATE_NEW_FILE_NAME_PREFIX;
    }

}
