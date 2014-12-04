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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class EndowmentCode extends PersistableBusinessObjectBase {
    
    private String code;
    private String description;
    
    /**
     * Gets the code attribute. 
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code attribute value.
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
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

    /**
     * Gets the code and description 
     * @return Returns code + " - " + description  
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return code + " - " + description;
    }
    
    /**
     * Sets the codeAndDescription attribute value.
     * @param set codeAndDescription 
     */
    public void setCodeAndDescription(String codeAndDescription) {}

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, this.code);
        return m;
    }

}
