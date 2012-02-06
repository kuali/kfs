/*
 * Copyright 2007 The Kuali Foundation
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
