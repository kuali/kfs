/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.bo;

import java.util.LinkedHashMap;

import org.kuali.PropertyConstants;

/**
 * This class represents an ad-hoc org.
 * 
 * 
 */
public class AdhocOrg extends AbstractAdhoc {
    
    private String fiscalCampusCode;
    private String primaryDepartmentCode;
    
    /**
     * Gets the fiscalCampusCode attribute. 
     * @return Returns the fiscalCampusCode.
     */
    public String getFiscalCampusCode() {
        return fiscalCampusCode;
    }
    /**
     * Sets the fiscalCampusCode attribute value.
     * @param fiscalCampusCode The fiscalCampusCode to set.
     */
    public void setFiscalCampusCode(String fiscalCampusCode) {
        this.fiscalCampusCode = fiscalCampusCode;
    }
    /**
     * Gets the primaryDepartmentCode attribute. 
     * @return Returns the primaryDepartmentCode.
     */
    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }
    /**
     * Sets the primaryDepartmentCode attribute value.
     * @param primaryDepartmentCode The primaryDepartmentCode to set.
     */
    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("fiscalCampusCode", this.fiscalCampusCode);
        m.put("primaryDepartmentCode", this.primaryDepartmentCode);
        
        return m;
    }
}
