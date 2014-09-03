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
package org.kuali.kfs.module.ar.fixture;


import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;

/**
 * Fixture class for CG Agency
 */


public enum DunningLetterTemplateFixture {

    CG_DLTS1("LT1", "Letter Template for Test", "Test File name", true);

    private String dunningLetterTemplateCode;
    private String dunningLetterTemplateDescription;
    private String filename;
    private boolean active;

    private DunningLetterTemplateFixture(String dunningLetterTemplateCode, String dunningLetterTemplateDescription, String filename, boolean active) {

        this.dunningLetterTemplateCode = dunningLetterTemplateCode;
        this.dunningLetterTemplateDescription = dunningLetterTemplateDescription;
        this.filename = filename;
        this.active = active;
    }

    public DunningLetterTemplate createDunningLetterTemplate() {
        DunningLetterTemplate dunningLetterTemplate = new DunningLetterTemplate();
        dunningLetterTemplate.setDunningLetterTemplateCode(this.dunningLetterTemplateCode);
        dunningLetterTemplate.setDunningLetterTemplateDescription(this.dunningLetterTemplateDescription);
        dunningLetterTemplate.setFilename(this.filename);
        dunningLetterTemplate.setActive(this.active);
        return dunningLetterTemplate;
    }
}
