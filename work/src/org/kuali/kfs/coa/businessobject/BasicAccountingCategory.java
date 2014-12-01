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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class BasicAccountingCategory extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String code;
    private String description;
    private String shortName;
    private String sortCode;
    private boolean active;

    /**
     * Default constructor.
     */
    public BasicAccountingCategory() {

    }

    /**
     * Gets the accountCategoryCode attribute.
     * 
     * @return Returns the accountCategoryCode
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the accountCategoryCode attribute.
     * 
     * @param accountCategoryCode The accountCategoryCode to set.
     */
    public void setCode(String basicAccountingCategoryCode) {
        this.code = basicAccountingCategoryCode;
    }


    /**
     * Gets the description attribute.
     * 
     * @return Returns the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute.
     * 
     * @param description The description to set.
     */
    public void setDescription(String accountCategoryDescription) {
        this.description = accountCategoryDescription;
    }


    /**
     * Gets the accountCategoryShortName attribute.
     * 
     * @return Returns the accountCategoryShortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the accountCategoryShortName attribute.
     * 
     * @param accountCategoryShortName The accountCategoryShortName to set.
     */
    public void setShortName(String basicAccountingCategoryShortName) {
        this.shortName = basicAccountingCategoryShortName;
    }


    /**
     * Gets the sortCode attribute.
     * 
     * @return Returns the sortCode
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Sets the sortCode attribute.
     * 
     * @param sortCode The sortCode to set.
     */
    public void setSortCode(String financialReportingSortCode) {
        this.sortCode = financialReportingSortCode;
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
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("accountCategoryCode", this.code);
        return m;
    }

    /**
     * This method generates a standard String of the code and description together
     * 
     * @return string representation of the code and description for this Account Category.
     */
    public String getCodeAndDescription() {
        StringBuilder codeAndDescription = new StringBuilder();
        codeAndDescription.append(this.getCode());
        codeAndDescription.append(" - ");
        codeAndDescription.append(this.getDescription());
        return codeAndDescription.toString();
    }
}
