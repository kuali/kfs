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
