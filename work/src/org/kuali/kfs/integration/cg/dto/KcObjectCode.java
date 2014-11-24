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
package org.kuali.kfs.integration.cg.dto;

public class KcObjectCode {
    String  objectCodeName;
    String  description;
   
     /**
     * Gets the objectCodeName attribute. 
     * @return Returns the objectCodeName.
     */
    public String getObjectCodeName() {
        return objectCodeName;
    }
    /**
     * Sets the objectCodeName attribute value.
     * @param objectCodeName The objectCodeName to set.
     */
    public void setObjectCodeName(String objectCodeName) {
        this.objectCodeName = objectCodeName;
    }
    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
