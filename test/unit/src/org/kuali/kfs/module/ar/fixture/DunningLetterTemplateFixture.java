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
