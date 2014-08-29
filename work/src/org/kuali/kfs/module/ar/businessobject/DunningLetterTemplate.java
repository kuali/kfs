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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Defines the letter templates that will be assigned to the appropriate dunning letter campaigns.
 *
 */
public class DunningLetterTemplate extends TemplateBase implements MutableInactivatable {

    private String dunningLetterTemplateCode;
    private String dunningLetterTemplateDescription;

    /**
     * Gets the dunningLetterTemplateCode attribute.
     *
     * @return Returns dunningLetterTemplateCode.
     */
    public String getDunningLetterTemplateCode() {
        return dunningLetterTemplateCode;
    }

    /**
     * Sets the dunningLetterTemplateCode attribute.
     *
     * @param dunningLetterTemplateCode The dunningLetterTemplateCode attribute to set.
     */
    public void setDunningLetterTemplateCode(String dunningLetterTemplateCode) {
        this.dunningLetterTemplateCode = dunningLetterTemplateCode;
    }

    /**
     * Gets the dunningLetterTemplateDescription attribute.
     *
     * @return Returns the dunningLetterTemplateDescription attribute.
     */
    public String getDunningLetterTemplateDescription() {
        return dunningLetterTemplateDescription;
    }

    /**
     * Sets the dunningLetterTemplateDescription attribute.
     *
     * @param dunningLetterTemplateDescription The dunningLetterTemplateDescription attribute to set.
     */
    public void setDunningLetterTemplateDescription(String dunningLetterTemplateDescription) {
        this.dunningLetterTemplateDescription = dunningLetterTemplateDescription;
    }

}
